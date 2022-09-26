package org.drools.yaml.runtime;

import java.util.List;
import java.util.Map;

import org.drools.yaml.api.domain.RuleMatch;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.SimpleLogicalOperatorsTest.SIMPLE_LOGICAL_OPERATOR_JSON;
import static org.drools.yaml.runtime.JsonTest.compileRulesFromJson;
import static org.junit.Assert.assertEquals;

public class SimpleLogicalOperatorsTest {

    @Test
    public void testProcessRules() {
        long rulesId = compileRulesFromJson(SIMPLE_LOGICAL_OPERATOR_JSON);
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        matchedRules = rulesExecutor.processFacts("{ facts: [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ]" +
                                                          " }");
        assertEquals(0, matchedRules.size());

        matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":4 } } }");
        assertEquals(1, matchedRules.size());

        RuleMatch ruleMatch = RuleMatch.from(matchedRules.get(0));
        assertEquals("R3", ruleMatch.getRuleName());
        assertEquals(3, ruleMatch.getFacts().get("j"));

        assertEquals(4, ((Map) ((Map) ruleMatch.getFacts().get("sensu")).get("data")).get("i"));

        rulesExecutor.dispose();
    }
}
