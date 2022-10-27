package org.drools.yaml.api;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

public class RetractTest {

    public static final String JSON1 =
            "{\n" +
            "           \"rules\": [\n" +
            "            {\n" +
            "                \"Rule\": {\n" +
            "                    \"action\": {\n" +
            "                        \"Action\": {\n" +
            "                            \"action\": \"assert_fact\",\n" +
            "                            \"action_args\": {\n" +
            "                                \"fact\": {\n" +
            "                                    \"msg\": \"hello world\"\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "                    },\n" +
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
            "                    \"action\": {\n" +
            "                        \"Action\": {\n" +
            "                            \"action\": \"retract_fact\",\n" +
            "                            \"action_args\": {\n" +
            "                                \"fact\": {\n" +
            "                                    \"msg\": \"hello world\"\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "                    },\n" +
            "                    \"condition\": {\n" +
            "                        \"AllCondition\": [\n" +
            "                            {\n" +
            "                                \"EqualsExpression\": {\n" +
            "                                    \"lhs\": {\n" +
            "                                        \"Event\": \"msg\"\n" +
            "                                    },\n" +
            "                                    \"rhs\": {\n" +
            "                                        \"String\": \"hello world\"\n" +
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
            "                    \"action\": {\n" +
            "                        \"Action\": {\n" +
            "                            \"action\": \"debug\",\n" +
            "                            \"action_args\": {}\n" +
            "                        }\n" +
            "                    },\n" +
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
        RulesExecutor rulesExecutor = RulesExecutorFactory.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"msg\" : \"hello world\" }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.processRetract( "{ \"msg\" : \"hello world\" }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_2", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
