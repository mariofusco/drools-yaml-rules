package org.drools.yaml.api;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

public class ProcessEventTest {

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
            "                                        \"Integer\": 1\n" +
            "                                    }\n" +
            "                                }\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    \"enabled\": true,\n" +
            "                    \"name\": null\n" +
            "                }\n" +
            "            },\n" +
            "            {\n" +
            "                \"Rule\": {\n" +
            "                    \"condition\": {\n" +
            "                        \"AllCondition\": [\n" +
            "                            {\n" +
            "                                \"IsNotDefinedExpression\": {\n" +
            "                                    \"Event\": \"msg\"\n" +
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
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processEvents( "{ \"i\": 1 }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
