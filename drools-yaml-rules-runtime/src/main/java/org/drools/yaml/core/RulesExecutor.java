package org.drools.yaml.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.facttemplates.Fact;
import org.drools.yaml.api.KieSessionHolder;
import org.drools.yaml.api.KieSessionHolderContainer;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;

public class RulesExecutor implements KieSessionHolder {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SessionGenerator sessionGenerator;
    private final KieSession ksession;
    private final long id;

    private final Set<Long> ephemeralFactHandleIds = ConcurrentHashMap.newKeySet();

    // This class has the following issues:
    // it is responsible to instantiate a KieSession (using the SessionGenerator)
    // it holds the generated kiesession
    // it is hold inside the RulesExecutorContainer just to provide anm (indirect) mapping between generated kiesession and evaluation requests
    // it is also responsible for rule execution

    // All the code contained inside SessionGenerator, written that way, would have to be copied over and over, if
    // not already a copy,
    // because it fullfill the basic behavior "return a kiebase containing an executable model  out of a set of rules"
    // it mixes/bind the two phases, i.e. the creation of an executable model (that is a sort-of "compilation") and
    // the execution of it (that is the runtime)
    // invoking a method of a parameter, passing itself as parameter, smells a lot of anti-pattern
    // sessionGenerator.build(this);
    private RulesExecutor(SessionGenerator sessionGenerator, long id) {
        this.sessionGenerator = sessionGenerator;
        this.ksession = sessionGenerator.build(id);
        this.id = id;
    }

    public static RulesExecutor createRulesExecutor(long id) {
        RulesExecutor rulesExecutor = new RulesExecutor(new SessionGenerator(), id);
        KieSessionHolderContainer.INSTANCE.register(rulesExecutor);
        return rulesExecutor;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void dispose() {
        KieSessionHolderContainer.INSTANCE.dispose(this);
        ksession.dispose();
    }

    @Override
    public long rulesCount() {
        return ksession.getKieBase().getKiePackages().stream().mapToLong(p -> p.getRules().size()).sum();
    }

    @Override
    public int executeFacts(String json) {
        return executeFacts( new JSONObject(json).toMap() );
    }

    @Override
    public int executeFacts(Map<String, Object> factMap) {
        insertFact( factMap );
        return ksession.fireAllRules();
    }

    @Override
    public List<Match> processFacts(String json) {
        return processFacts( new JSONObject(json).toMap() );
    }

    @Override
    public List<Match> processFacts(Map<String, Object> factMap) {
        return process(factMap, false);
    }

    @Override
    public List<Match> processEvents(String json) {
        return processEvents( new JSONObject(json).toMap() );
    }

    @Override
    public List<Match> processEvents(Map<String, Object> factMap) {
        return process(factMap, true);
    }

    private List<Match> process(Map<String, Object> factMap, boolean ephemeral) {
        Collection<FactHandle> fhs = insertFacts(factMap);
        if (ephemeral) {
            fhs.stream()
                    .map(InternalFactHandle.class::cast)
                    .map(InternalFactHandle::getId)
                    .forEach(ephemeralFactHandleIds::add);
        }
        RegisterOnlyAgendaFilter filter = new RegisterOnlyAgendaFilter(ksession, ephemeralFactHandleIds);
        ksession.fireAllRules(filter);
        return filter.getMatchedRules();
    }

    private Collection<FactHandle> insertFacts(Map<String, Object> factMap) {
        if (factMap.size() == 1 && factMap.containsKey("facts")) {
            return ((List<Map<String, Object>>)factMap.get("facts")).stream()
                    .flatMap(map -> this.insertFacts(map).stream())
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList( insertFact(factMap) );
        }
    }

    @Override
    public FactHandle insertFact(Map<String, Object> factMap) {
        return ksession.insert( mapToFact(factMap) );
    }

    @Override
    public boolean retract(String json) {
        return retractFact( new JSONObject(json).toMap() );
    }

    @Override
    public boolean retractFact(Map<String, Object> factMap) {
        Fact toBeRetracted = mapToFact(factMap);

        return ksession.getFactHandles(o -> o instanceof Fact && Objects.equals(((Fact) o).asMap(),
                                                                                toBeRetracted.asMap()))
                .stream().findFirst()
                .map(fh -> {
                    ksession.delete(fh);
                    return true;
                }).orElse(false);
    }

    private Fact mapToFact(Map<String, Object> factMap) {
        Fact fact = createMapBasedFact( sessionGenerator.getPrototype() );
        populateFact(fact, factMap, "");
        return fact;
    }

    private void populateFact(Fact fact, Map<?, ?> value, String fieldName) {
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            String key = fieldName + entry.getKey();
            if (entry.getValue() instanceof Map) {
                populateFact(fact, (Map<?, ?>) entry.getValue(), key + ".");
            } else {
                fact.set(key, entry.getValue());
            }
        }
    }

    @Override
    public Collection<?> getAllFacts() {
        return ksession.getObjects();
    }

    @Override
    public List<Map<String, Object>> getAllFactsAsMap() {
        return getAllFacts().stream().map(Fact.class::cast).map(Fact::asMap).collect(Collectors.toList());
    }

    @Override
    public String getAllFactsAsJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(getAllFactsAsMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private static class RegisterOnlyAgendaFilter implements AgendaFilter {

        private final KieSession ksession;
        private final Set<Long> ephemeralFactHandleIds;

        private final Set<Match> matchedRules = new LinkedHashSet<>();

        private RegisterOnlyAgendaFilter(KieSession ksession, Set<Long> ephemeralFactHandleIds) {
            this.ksession = ksession;
            this.ephemeralFactHandleIds = ephemeralFactHandleIds;
        }

        @Override
        public boolean accept(Match match) {
            matchedRules.add(match);
            if (!ephemeralFactHandleIds.isEmpty()) {
                for (FactHandle fh : match.getFactHandles()) {
                    if (ephemeralFactHandleIds.remove(((InternalFactHandle) fh).getId())) {
                        ksession.delete(fh);
                    }
                }
            }
            return false;
        }

        public List<Match> getMatchedRules() {
            return new ArrayList<>( matchedRules );
        }
    }
}
