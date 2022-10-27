package org.drools.yaml.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.facttemplates.Fact;
import org.json.JSONObject;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;

public class RulesExecutor {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RulesExecutorSession rulesExecutorSession;

    private final long id;

    private final Set<Long> ephemeralFactHandleIds = ConcurrentHashMap.newKeySet();

    RulesExecutor(RulesExecutorSession rulesExecutorSession, long id) {
        this.rulesExecutorSession = rulesExecutorSession;
        this.id = id;
        rulesExecutorSession.setRulesExecutor(this);
    }

    public long getId() {
        return id;
    }

    public void dispose() {
        RulesExecutorContainer.INSTANCE.dispose(this);
        rulesExecutorSession.dispose();
    }

    public long rulesCount() {
        return rulesExecutorSession.rulesCount();
    }

    public int executeFacts(String json) {
        return executeFacts( new JSONObject(json).toMap() );
    }

    public int executeFacts(Map<String, Object> factMap) {
        insertFact( factMap );
        return rulesExecutorSession.fireAllRules();
    }

    public List<Match> processFacts(String json) {
        return processFacts( new JSONObject(json).toMap() );
    }

    public List<Match> processFacts(Map<String, Object> factMap) {
        return process(factMap, false);
    }

    public List<Match> processEvents(String json) {
        return processEvents( new JSONObject(json).toMap() );
    }

    public List<Match> processEvents(Map<String, Object> factMap) {
        return process(factMap, true);
    }

    private List<Match> process(Map<String, Object> factMap, boolean event) {
        Collection<FactHandle> fhs = insertFacts(factMap, event);
        if (event) {
            fhs.stream()
                    .map(InternalFactHandle.class::cast)
                    .map(InternalFactHandle::getId)
                    .forEach(ephemeralFactHandleIds::add);
        }
        List<Match> matches = findMatchedRules();
        return !event || matches.size() < 2 ?
                matches :
                // when processing an event return only the matches for the first matched rule
                matches.stream().takeWhile( match -> match.getRule().getName().equals(matches.get(0).getRule().getName())).collect(Collectors.toList());
    }

    private List<Match> findMatchedRules() {
        RegisterOnlyAgendaFilter filter = new RegisterOnlyAgendaFilter(rulesExecutorSession, ephemeralFactHandleIds);
        rulesExecutorSession.fireAllRules(filter);
        return filter.getMatchedRules();
    }

    private Collection<FactHandle> insertFacts(Map<String, Object> factMap, boolean event) {
        if (factMap.size() == 1 && factMap.containsKey(event ? "events" : "facts")) {
            return ((List<Map<String, Object>>)factMap.get(event ? "events" : "facts")).stream()
                    .flatMap(map -> this.insertFacts(map, event).stream())
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList( insertFact(factMap) );
        }
    }

    public FactHandle insertFact(Map<String, Object> factMap) {
        return rulesExecutorSession.insert( mapToFact(factMap) );
    }

    public int executeRetract(String json) {
        return retractFact( new JSONObject(json).toMap() ) ? rulesExecutorSession.fireAllRules() : 0;
    }

    public List<Match> processRetract(String json) {
        return retractFact( new JSONObject(json).toMap() ) ? findMatchedRules() : Collections.emptyList();
    }

    public boolean retractFact(Map<String, Object> factMap) {
        return rulesExecutorSession.deleteFact( mapToFact(factMap) );
    }

    private Fact mapToFact(Map<String, Object> factMap) {
        Fact fact = createMapBasedFact( rulesExecutorSession.getPrototype() );
        populateFact(fact, factMap, "");
        return fact;
    }

    private void populateFact(Fact fact, Map<?, ?> value, String fieldName) {
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            String key = fieldName + entry.getKey();
            fact.set(key, entry.getValue());
            if (entry.getValue() instanceof Map) {
                populateFact(fact, (Map<?, ?>) entry.getValue(), key + ".");
            }
        }
    }

    public Collection<?> getAllFacts() {
        return rulesExecutorSession.getObjects();
    }

    public List<Map<String, Object>> getAllFactsAsMap() {
        return getAllFacts().stream().map(Fact.class::cast).map(Fact::asMap).collect(Collectors.toList());
    }

    public String getAllFactsAsJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(getAllFactsAsMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class RegisterOnlyAgendaFilter implements AgendaFilter {

        private final RulesExecutorSession rulesExecutorSession;
        private final Set<Long> ephemeralFactHandleIds;

        private final Set<Match> matchedRules = new LinkedHashSet<>();

        private RegisterOnlyAgendaFilter(RulesExecutorSession rulesExecutorSession, Set<Long> ephemeralFactHandleIds) {
            this.rulesExecutorSession = rulesExecutorSession;
            this.ephemeralFactHandleIds = ephemeralFactHandleIds;
        }

        @Override
        public boolean accept(Match match) {
            matchedRules.add(match);
            if (!ephemeralFactHandleIds.isEmpty()) {
                for (FactHandle fh : match.getFactHandles()) {
                    if (ephemeralFactHandleIds.remove(((InternalFactHandle) fh).getId())) {
                        rulesExecutorSession.delete(fh);
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
