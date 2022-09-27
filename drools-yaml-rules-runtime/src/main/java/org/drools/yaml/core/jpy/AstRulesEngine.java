package org.drools.yaml.core.jpy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.RulesExecutorContainer;
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
    public int retractFact(long sessionId, String serializedFact) {
        RulesExecutorContainer.INSTANCE.get(sessionId).retract(serializedFact);
        return 0;
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
