package org.drools.yaml.core.jpy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.RulesExecutorContainer;
import org.json.JSONObject;
import org.kie.api.runtime.rule.Match;

public class AstRulesEngine {

    @Deprecated(forRemoval = true)
    /**
     * This method is part of the compilation phase and should be avoided in favour of
     * AstRulesCompilation.createRuleset
     */
    public long createRuleset(String rulesetString) {
        RulesExecutor executor = RulesExecutor.createFromJson(rulesetString);
        return executor.getId();
    }

    public void dispose(long sessionId) {
        RulesExecutorContainer.INSTANCE.get(sessionId).dispose();
    }

    /**
     * @return error code (currently always 0)
     */
    public String retractFact(long sessionId, String serializedFact) {
        Map<String, Object> fact = new JSONObject(serializedFact).toMap();
        Map<String, Object> boundFact = Map.of("m", fact);
        List<Map<String, Map>> objs = processMessage(
                serializedFact,
                RulesExecutorContainer.INSTANCE.get(sessionId)::processRetract);
        List<Map<String, ?>> results = objs.stream()
                .map(m -> m.entrySet().stream().findFirst()
                        .map(e -> Map.of(e.getKey(), boundFact)).get())
                .collect(Collectors.toList());
        return toJson(results);
    }

    public String assertFact(long sessionId, String serializedFact) {
        return toJson(processMessage(
                serializedFact,
                RulesExecutorContainer.INSTANCE.get(sessionId)::processFacts));
    }

    public String assertEvent(long sessionId, String serializedFact) {
        return toJson(processMessage(
                serializedFact,
                RulesExecutorContainer.INSTANCE.get(sessionId)::processEvents));
    }

    public String getFacts(long session_id) {
        return RulesExecutorContainer.INSTANCE.get(session_id).getAllFactsAsJson();
    }

    private List<Map<String, Map>> processMessage(String serializedFact, Function<String, Collection<Match>> command) {
        return AstRuleMatch.asList(command.apply(serializedFact));
    }

    private String toJson(Object elem) {
        try {
            return RulesExecutor.OBJECT_MAPPER.writeValueAsString(elem);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
