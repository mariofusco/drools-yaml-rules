package org.drools.yaml.api;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

public class AutoEvictTest {

    public static final String JSON1 =
            "{\n" +
                    "   \"rules\":[\n" +
                    "      {\n" +
                    "         \"Rule\":{\n" +
                    "            \"condition\":{\n" +
                    "               \"AllCondition\":[\n" +
                    "                  {\n" +
                    "                     \"EqualsExpression\":{\n" +
                    "                        \"lhs\":{\n" +
                    "                           \"Fact\":\"i\"\n" +
                    "                        },\n" +
                    "                        \"rhs\":{\n" +
                    "                           \"Integer\":2\n" +
                    "                        }\n" +
                    "                     }\n" +
                    "                  }\n" +
                    "               ]\n" +
                    "            },\n" +
                    "            \"enabled\":true,\n" +
                    "            \"name\":null\n" +
                    "         }\n" +
                    "      }\n" +
                    "   ]\n" +
                    "}";

    @Test
    public void test() {
        RulesExecutor rulesExecutor = RulesExecutorFactory.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"i\": 3 }");
        assertEquals(0, matchedRules.size());
        assertEquals(0, rulesExecutor.getAllFacts().size());

        matchedRules = rulesExecutor.processFacts("{ \"i\": 2 }");
        assertEquals(1, matchedRules.size());
        assertEquals("r_0", matchedRules.get(0).getRule().getName());
        assertEquals(1, rulesExecutor.getAllFacts().size());

        rulesExecutor.dispose();
    }
}
