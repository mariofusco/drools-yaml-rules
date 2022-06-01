package org.drools.yaml.durable;

import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.yaml.core.RulesExecutor;
import org.drools.yaml.core.domain.RuleMatch;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

@QuarkusTest
public class DurableRulesJsonTest {

    private static final String DURABLE_RULES_JSON =
            "{\n" +
            "   \"myrules\":{\n" +
            "      \"R1\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"m\":{\n" +
            "                  \"$lt\":{\n" +
            "                     \"sensu.data.i\": 2\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec first\"\n" +
            "      },\n" +
            "      \"R2\":{\n" +
            "         \"all\":[\n" +
            "            {\n" +
            "               \"first\":{\n" +
            "                  \"sensu.data.i\": 3\n" +
            "               },\n" +
            "               \"second\":{\n" +
            "                  \"j\": 2\n" +
            "               }\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec second\"\n" +
            "      },\n" +
            "      \"R3\":{\n" +
            "         \"any\":[\n" +
            "            {\n" +
            "               \"all\":[\n" +
            "                  {\n" +
            "                     \"first\":{\n" +
            "                        \"sensu.data.i\": 3\n" +
            "                     },\n" +
            "                     \"second\":{\n" +
            "                        \"j\": 2\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            {\n" +
            "               \"all\":[\n" +
            "                  {\n" +
            "                     \"first\":{\n" +
            "                        \"sensu.data.i\": 4\n" +
            "                     },\n" +
            "                     \"second\":{\n" +
            "                        \"j\": 3\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         ],\n" +
            "         \"run\": \"exec third\"\n" +
            "      }\n" +
            "   }\n" +
            "}";

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, DURABLE_RULES_JSON);

        List<Match> matchedRules = rulesExecutor.process( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.process( "{ facts: [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.process( "{ \"sensu\": { \"data\": { \"i\":4 } } }" );
        assertEquals( 1, matchedRules.size() );

        RuleMatch ruleMatch = RuleMatch.from( matchedRules.get(0) );
        Assert.assertEquals( "R3", ruleMatch.getRuleName() );
        Assert.assertEquals( 3, ruleMatch.getFacts().get("j") );

        RuleMatch.MatchedFact sensu = (RuleMatch.MatchedFact) ruleMatch.getFacts().get("first");
        Assert.assertEquals( "sensu", sensu.getType() );
        Assert.assertEquals( 4, sensu.getValues().get("data.i") );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessWithAnd() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m_0\": {\"payload.provisioningState\": \"Succeeded\"}}, {\"m_1\": {\"payload.provisioningState\": \"Deleted\"}}]}}}";

        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);

        List<Match> matchedRules = rulesExecutor.process( "{ \"payload\": { \"provisioningState\": \"Succeeded\" } }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.process( "{ \"payload\": { \"provisioningState\": \"Deleted\" } }" );
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithExists() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"subject.x\": 1}}}]}, \"r_1\": {\"all\": [{\"m\": {\"$nex\": {\"subject.x\": 1}}}]}}}";

        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        List<Match> matchedRules = rulesExecutor.process( "{ \"subject\": { \"y\": \"Succeeded\" } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.process( "{ \"subject\": { \"x\": \"Succeeded\" } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );
    }
}
