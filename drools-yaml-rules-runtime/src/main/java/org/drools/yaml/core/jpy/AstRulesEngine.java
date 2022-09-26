package org.drools.yaml.core.jpy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.compilation.RulesCompiler;
import org.drools.yaml.core.RulesExecutor;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.core.KieSessionHolderUtils.kieSessionHolder;

public class AstRulesEngine {

    @Deprecated(forRemoval = true)
    /**
     * This method is part of the compilation phase and should be avoided in favour of
     * AstRulesCompilation.createRuleset
     */
    public long createRuleset(String rulesetString) {
        // This creates a direct dependency between runtime and compile module that should be removed
        RulesCompiler rulesCompiler = RulesCompiler.createFromJson(rulesetString);
        return rulesCompiler.getId();
    }

    /**
     * @return error code (currently always 0)
     */
    public int retractFact(long sessionId, String serializedFact) {
        kieSessionHolder(sessionId).retract(serializedFact);
        return 0;
    }

    public String assertFact(long sessionId, String serializedFact) {
        return toJson(processMessage(
                serializedFact,
                kieSessionHolder(sessionId)::processFacts));
    }

    public String assertEvent(long sessionId, String serializedFact) {
        return toJson(processMessage(
                serializedFact,
                kieSessionHolder(sessionId)::processEvents));
    }

    public String getFacts(long session_id) {
        return kieSessionHolder(session_id).getAllFactsAsJson();
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
