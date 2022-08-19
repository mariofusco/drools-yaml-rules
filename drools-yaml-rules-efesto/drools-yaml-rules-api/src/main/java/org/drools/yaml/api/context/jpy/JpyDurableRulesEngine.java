package org.drools.yaml.api.context.jpy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.drools.yaml.api.domain.durable.DurableRuleMatch;
import org.drools.yaml.api.notations.DurableNotation;
import org.kie.api.runtime.rule.Match;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JpyDurableRulesEngine {

    private Iterator<Map<String, Map>> lastResponse = Collections.emptyIterator();

    public long createRuleset(String ruleSetName, String rulesetString) {
        RulesExecutor executor = RulesExecutor.createFromJson(
                DurableNotation.INSTANCE,
                String.format("{\"%s\":%s}", ruleSetName, rulesetString));
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
        List<Map<String, Map>> lastResponse = DurableRuleMatch.asList(command.apply(serializedFact));
        this.lastResponse = lastResponse.iterator();
        return 0;
    }
}
