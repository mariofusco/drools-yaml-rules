package org.drools.yaml.api;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;
import org.drools.yaml.api.RuleNotation.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BetaEqualsTest {
    public static final String JSON1 =
            "{\n" +
            "    \"rules\": [\n" +
            "            {\n" +
            "                \"Rule\": {\n" +
            "                    \"condition\": {\n" +
            "                        \"AllCondition\": [\n" +
            "                            {\n" +
            "                                \"EqualsExpression\": {\n" +
            "                                    \"lhs\": {\n" +
            "                                        \"Event\": \"i\"\n" +
            "                                    },\n" +
            "                                    \"rhs\": {\n" +
            "                                        \"Fact\": \"custom.expected_index\"\n" +
            "                                    }\n" +
            "                                }\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    \"enabled\": true,\n" +
            "                    \"name\": null\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "}";

    @Test
    public void testExecuteRulesWithImplicitJoin() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(CoreNotation.INSTANCE.withOptions(RuleConfigurationOption.ALLOW_IMPLICIT_JOINS), JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"custom\": { \"expected_index\": 2 } }");
        assertEquals(0, matchedRules.size());

        matchedRules = rulesExecutor.processEvents("{ \"i\": 3 }");
        assertEquals(0, matchedRules.size());

        matchedRules = rulesExecutor.processEvents("{ \"i\": 2 }");
        assertEquals(1, matchedRules.size());
        assertEquals("r_0", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    @Test
    public void testInvalidImplicitJoin() {
        try {
            RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);
            fail("It shouldn't compile");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public static final String JSON2 =
            "{\n" +
            "   \"rules\":[\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"condition\":{\n" +
            "               \"AllCondition\":[\n" +
            "                  {\n" +
            "                     \"AssignmentExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"Facts\":\"first\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"IsDefinedExpression\":{\n" +
            "                              \"Fact\":\"custom.expected_index\"\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"EqualsExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"Event\":\"i\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"Facts\":\"first.custom.expected_index\"\n" +
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
    public void testExecuteRulesWithExplicitJoin() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON2);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"custom\": { \"expected_index\": 2 } }");
        assertEquals(0, matchedRules.size());

        matchedRules = rulesExecutor.processEvents("{ \"i\": 3 }");
        assertEquals(0, matchedRules.size());

        matchedRules = rulesExecutor.processEvents("{ \"i\": 2 }");
        assertEquals(1, matchedRules.size());
        assertEquals("r_0", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }
}