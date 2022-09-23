package org.drools.yaml.runtime.jpy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.RulesExecutorContainer;
import org.kie.api.runtime.rule.Match;

public class AstRulesEngine {

    private Iterator<Map<String, Map>> lastResponse = Collections.emptyIterator();

    @Deprecated(forRemoval = true)
    /**
     * This method is part of the compilation phase and should be avoided in favour of
     * AstRulesCompilation.createRuleset
     */
    public long createRuleset(String rulesetString) {
        RulesExecutor executor = RulesExecutor.createFromJson(rulesetString);
        return executor.getId();
    }

    /**
     * @return error code (currently always 0)
     */
    public int retractFact(long sessionId, String serializedFact) {
        RulesExecutorContainer.INSTANCE.get(sessionId).retract(serializedFact);
        return 0;
    }

    public String advanceState() {
        if (lastResponse.hasNext()) {
            Map<String, Map> elem = lastResponse.next();
            try {
                return RulesExecutor.OBJECT_MAPPER.writeValueAsString(elem);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public int assertFact(long sessionId, String serializedFact) {
        return processMessage(
                serializedFact,
                RulesExecutorContainer.INSTANCE.get(sessionId)::processFacts);
    }

    public int assertEvent(long sessionId, String serializedFact) {
        return processMessage(
                serializedFact,
                RulesExecutorContainer.INSTANCE.get(sessionId)::processEvents);
    }

    public String getFacts(long session_id) {
        return RulesExecutorContainer.INSTANCE.get(session_id).getAllFactsAsJson();
    }

    private int processMessage(String serializedFact, Function<String, Collection<Match>> command) {
        List<Map<String, Map>> lastResponse = AstRuleMatch.asList(command.apply(serializedFact));
        this.lastResponse = lastResponse.iterator();
        return 0;
    }
}
