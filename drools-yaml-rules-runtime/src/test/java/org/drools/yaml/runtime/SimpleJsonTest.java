package org.drools.yaml.runtime;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.SimpleJsonTest.SIMPLE_JSON;
import static org.drools.yaml.runtime.JsonTest.compileRulesFromJson;
import static org.junit.Assert.assertEquals;

public class SimpleJsonTest {

    private static long RULES_COMPILER_ID;

    @BeforeClass
    public static void setup() {
        RULES_COMPILER_ID = compileRulesFromJson(SIMPLE_JSON);
    }

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(RULES_COMPILER_ID);
        int executedRules = rulesExecutor.executeFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(2, executedRules);
        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(RULES_COMPILER_ID);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        matchedRules = rulesExecutor.processFacts("{ \"j\":1 }");
        assertEquals(1, matchedRules.size());
        assertEquals("R4", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithoutAction() {
        long rulesId = compileRulesFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\": \"sensu.data.i == " +
                                            "1\" }} ] }");
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithUnknownAction() {
        long rulesId = compileRulesFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\": \"sensu.data.i == " +
                                            "1\", \"action\": { \"unknown\": { \"ruleset\": \"Test rules4\", " +
                                            "\"fact\": { \"j\": 1 } } } }} ] }\n");
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }
}
