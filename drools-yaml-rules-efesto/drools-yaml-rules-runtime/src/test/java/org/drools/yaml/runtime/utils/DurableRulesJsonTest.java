package org.drools.yaml.runtime.utils;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.yaml.api.domain.RuleMatch;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, DURABLE_RULES_JSON);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"sensu\": { \"data\": { \"i\":1 } } }", kieSession);
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        matchedRules = DroolsYamlUtils.process( "{ facts: [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"sensu\": { \"data\": { \"i\":4 } } }", kieSession);
        assertEquals( 1, matchedRules.size() );

        RuleMatch ruleMatch = RuleMatch.from(matchedRules.get(0) );
        Assert.assertEquals( "R3", ruleMatch.getRuleName() );
        Assert.assertEquals( 3, ((Map) ruleMatch.getFacts().get("second")).get("j") );

        assertEquals( 4, ((Map) ((Map) ((Map) ruleMatch.getFacts().get("first")).get("sensu")).get("data")).get("i") );

//        rulesExecutor.dispose();
    }

    @Test
    public void testProcessWithAnd() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m_0\": {\"payload.provisioningState\": \"Succeeded\"}}, {\"m_1\": {\"payload.provisioningState\": \"Deleted\"}}]}}}";

////        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"payload\": { \"provisioningState\": \"Succeeded\" } }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"payload\": { \"provisioningState\": \"Deleted\" } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithExists() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"subject.x\": 1}}}]}, \"r_1\": {\"all\": [{\"m\": {\"$nex\": {\"subject.x\": 1}}}]}}}";

////        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO
        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"subject\": { \"y\": \"Succeeded\" } }", kieSession);
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_1", matchedRules.get(0).getRule().getName() );

        matchedRules = DroolsYamlUtils.process( "{ \"subject\": { \"x\": null } }", kieSession);
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );
    }

    @Test
    public void testProcessWithNestedValues() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"subject\": {\"x\": \"Kermit\"}, \"predicate\": \"eats\", \"object\": \"flies\"}}]}}}";

////        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO
        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"subject\": { \"x\": \"Kermit\" }, \"predicate\": \"eats\", \"object\": \"flies\" }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithAndConstraint() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$and\": [{\"nested.i\" : 1}, {\"nested.j\" : 2}]}}]}}}";

////        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO
        
        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1 } }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1, \"j\": 2 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithOrConstraint() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$or\": [{\"nested.i\" : 1}, {\"nested.j\" : 2}]}}]}}}";

////        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testRetract() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"nested.i\" : 1}}]}}}";


//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1 } }", kieSession);
        assertEquals( 1, matchedRules.size() );

//        assertTrue( DroolsYamlUtils.retract( "{ \"nested\": { \"i\": 1 } }" ) ); // TODO
    }

    @Test
    public void testProcessWithAddConstraint() {
        String jsonRule =
                "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"nested.i\": {\"$add\": {\"$l\": {\"$m\": \"nested.j\"}, \"$r\": 1}}}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 2, \"j\":1 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithSubConstraint() {
        String jsonRule =
                "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"nested.i\": {\"$sub\": {\"$l\": {\"$m\": \"nested.j\"}, \"$r\": 1}}}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1, \"j\":2 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testGetAllFacts() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"nested.i\" : 1}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
        matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"j\": 1 } }", kieSession);
        assertEquals( 0, matchedRules.size() );

//        assertEquals( 2, DroolsYamlUtils.getAllFactsAsMap().size() ); // TODO
    }

    @Test
    public void testProcessWithBindingJoin() {
        String jsonRule =
                "{ \"rules\": {\"r_0\": {\"all\": [{\"first\": {\"i\": 0}}, {\"second\": {\"i\": {\"first\": \"j\"}}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"i\": 0, \"j\": 3 }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"i\": 3 }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testProcessWithBindingJoinAddConstraint() {
        String jsonRule =
                "{ \"rules\": {\"r_0\": {\"all\": [" +
                        "{\"first\": {\"i\": 0}}, " +
                        "{\"second\": {\"i\": 1}}, " +
                        "{\"third\": {\"i\": {\"$add\": {\"$l\": {\"first\": \"i\"}, \"$r\": 2}}}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"i\": 0, \"j\": 3 }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"i\": 1 }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"i\": 2 }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }

    @Test
    public void testOr() {
        String jsonRule =
                "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$or\": [{\"nested.i\": 1}, {\"nested.j\": 2}]}}]}}}";

//        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 2, \"j\":1 } }", kieSession);
        assertEquals( 0, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"j\":2 } }", kieSession);
        assertEquals( 1, matchedRules.size() );

        matchedRules = DroolsYamlUtils.process( "{ \"nested\": { \"i\": 1, \"j\":2 } }", kieSession);
        assertEquals( 1, matchedRules.size() );
    }
}
