package org.drools.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.facttemplates.Fact;
import org.drools.model.Prototype;
import org.drools.yaml.domain.durable.DurableRules;
import org.drools.yaml.domain.rulesset.RulesSet;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;
import static org.drools.yaml.SessionGenerator.GLOBAL_MAP_FIELD;

public class RulesExecutor {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private enum RuleFormat {
        YAML, JSON;

        JsonFactory getJsonFactory() {
            return this == YAML ? new YAMLFactory() : new JsonFactory();
        }
    }

    public enum RuleNotation {
        RULES_SET(RuleNotation::readRulesSet), DURABLE_RULES(RuleNotation::readDurableRules);

        private final BiFunction<ObjectMapper, String, RulesSet> reader;

        RuleNotation(BiFunction<ObjectMapper, String, RulesSet> reader) {
            this.reader = reader;
        }

        RulesSet toRulesSet(RuleFormat format, String text) {
            return reader.apply( new ObjectMapper( format.getJsonFactory() ), text );
        }

        private static RulesSet readRulesSet(ObjectMapper mapper, String text) {
            try {
                return mapper.readValue(text, RulesSet.class );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        private static RulesSet readDurableRules(ObjectMapper mapper, String text) {
            try {
                return mapper.readValue(text, DurableRules.class ).toRulesSet();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final SessionGenerator sessionGenerator;
    private final KieSession ksession;
    private final long id;

    private FactHandle globalFactHandle;

    private RulesExecutor(SessionGenerator sessionGenerator, long id) {
        this.sessionGenerator = sessionGenerator;
        this.ksession = sessionGenerator.build(this);
        this.id = id;
    }

    public static RulesExecutor createFromYaml(String yaml) {
        return createFromYaml(RuleNotation.RULES_SET, yaml);
    }

    public static RulesExecutor createFromYaml(RuleNotation notation, String yaml) {
        return create(RuleFormat.YAML, notation, yaml);
    }

    public static RulesExecutor createFromJson(String json) {
        return createFromJson(RuleNotation.RULES_SET, json);
    }

    public static RulesExecutor createFromJson(RuleNotation notation, String json) {
        return create(RuleFormat.JSON, notation, json);
    }

    private static RulesExecutor create(RuleFormat format, RuleNotation notation, String text) {
        return createRulesExecutor( notation.toRulesSet( format, text ) );
    }

    public static RulesExecutor createRulesExecutor(RulesSet rulesSet) {
        RulesExecutor rulesExecutor = new RulesExecutor( new SessionGenerator(rulesSet), ID_GENERATOR.getAndIncrement());
        RulesExecutorContainer.INSTANCE.register(rulesExecutor);
        return rulesExecutor;
    }

    public long getId() {
        return id;
    }

    public void dispose() {
        RulesExecutorContainer.INSTANCE.dispose(this);
        ksession.dispose();
    }

    public long rulesCount() {
        return ksession.getKieBase().getKiePackages().stream().flatMap(p -> p.getRules().stream()).count();
    }

    public int execute(String json) {
        return execute( new JSONObject(json).toMap() );
    }

    public int execute(Map<String, Object> factMap) {
        processFacts( factMap );
        return ksession.fireAllRules();
    }

    public List<Match> process(String json) {
        return process( new JSONObject(json).toMap() );
    }

    public List<Match> process(Map<String, Object> factMap) {
        processFacts( factMap );
        RegisterOnlyAgendaFilter filter = new RegisterOnlyAgendaFilter();
        ksession.fireAllRules(filter);
        return filter.getMatchedRules();
    }

    public void processFacts(Map<String, Object> factMap) {
        if (factMap.size() != 1) {
            throw new IllegalArgumentException("Expecting a map with only one entry, but found: " + factMap );
        }
        Map.Entry<String, Object> entry = factMap.entrySet().iterator().next();
        if (entry.getValue() instanceof Iterable) {
            for (Object item : (Iterable) entry.getValue()) {
                if (item instanceof Map) {
                    processFacts( (Map<String, Object>) item );
                } else {
                    throw new IllegalArgumentException("Expecting a map, but found: " + item );
                }
            }
        } else if (entry.getValue() instanceof Map) {
            Prototype prototype = sessionGenerator.getPrototype(entry.getKey());
            Fact fact = createMapBasedFact( prototype );
            populateFact(fact, (Map) entry.getValue(), "");
            ksession.insert(fact);
        } else {
            // single value fact like 'j = 1' are represented as field of a special map based object also inserted in the working memory
            if (globalFactHandle == null) {
                Prototype prototype = sessionGenerator.getPrototype(GLOBAL_MAP_FIELD);
                Fact fact = createMapBasedFact( prototype );
                fact.set(entry.getKey(), entry.getValue());
                this.globalFactHandle = ksession.insert(fact);
            } else {
                Fact fact = (Fact) ((InternalFactHandle) globalFactHandle).getObject();
                fact.set(entry.getKey(), entry.getValue());
                ksession.update(globalFactHandle, fact);
            }
        }
    }

    private void populateFact(Fact fact, Map<?, ?> value, String fieldName) {
        for (Map.Entry entry : value.entrySet()) {
            fieldName += entry.getKey();
            if (entry.getValue() instanceof Map) {
                populateFact(fact, (Map) entry.getValue(), fieldName + ".");
            } else {
                fact.set(fieldName, entry.getValue());
            }
        }
    }

    private static class RegisterOnlyAgendaFilter implements AgendaFilter {

        private final List<Match> matchedRules = new ArrayList<>();

        @Override
        public boolean accept(Match match) {
            matchedRules.add(match);
            return false;
        }

        public List<Match> getMatchedRules() {
            return matchedRules;
        }
    }
}
