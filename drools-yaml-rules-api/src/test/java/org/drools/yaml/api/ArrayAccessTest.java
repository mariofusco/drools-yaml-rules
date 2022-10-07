package org.drools.yaml.api;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

public class ArrayAccessTest {
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
            "                                        \"Fact\": \"os.array[1]\"\n" +
            "                                    },\n" +
            "                                    \"rhs\": {\n" +
            "                                        \"String\": \"windows\"\n" +
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
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts( "{'host': 'A', 'os': {'array': ['abc']}}" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{'host': 'B', 'os': {'array': ['abc', 'windows']}}" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
