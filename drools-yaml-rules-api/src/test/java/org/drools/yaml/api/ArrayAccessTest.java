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
    public void testArrayAccess() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts( "{'host': 'A', 'os': {'array': ['abc']}}" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{'host': 'B', 'os': {'array': ['abc', 'windows']}}" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    public static final String JSON2 =
            "{\n" +
            "    \"rules\": [\n" +
            "            {\n" +
            "                \"Rule\": {\n" +
            "                    \"condition\": {\n" +
            "                        \"AllCondition\": [\n" +
            "                            {\n" +
            "                                \"EqualsExpression\": {\n" +
            "                                    \"lhs\": {\n" +
            "                                        \"Fact\": \"os.array[1].versions[2]\"\n" +
            "                                    },\n" +
            "                                    \"rhs\": {\n" +
            "                                        \"String\": \"Vista\"\n" +
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
    public void testNestedArrayAccess() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON2);

        List<Match> matchedRules = rulesExecutor.processFacts( "{\n" +
                "   \"host\":\"B\",\n" +
                "   \"os\":{\n" +
                "      \"array\":[\n" +
                "         {\n" +
                "            \"name\":\"abc\",\n" +
                "            \"versions\":\"Unknown\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"name\":\"windows\",\n" +
                "            \"versions\":[\"XP\", \"Millenium\"]\n" +
                "         }\n" +
                "      ]\n" +
                "   }\n" +
                "}" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{\n" +
                "   \"host\":\"B\",\n" +
                "   \"os\":{\n" +
                "      \"array\":[\n" +
                "         {\n" +
                "            \"name\":\"abc\",\n" +
                "            \"versions\":\"Unknown\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"name\":\"windows\",\n" +
                "            \"versions\":[\"XP\", \"Millenium\", \"Vista\"]\n" +
                "         }\n" +
                "      ]\n" +
                "   }\n" +
                "}" );

        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
