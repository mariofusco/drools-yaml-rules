package org.drools.yaml.api;

import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;
import static org.junit.Assert.assertEquals;

public class JsonTest {

    public static final String JSON1 =
            "{\n" +
            "   \"sources\":{\"EventSource\":\"test\"},\n" +
            "   \"rules\":[\n" +
            "      {\"Rule\": {\n" +
            "         \"condition\":{\n" +
            "           \"AssignmentExpression\": {\n" +
            "             \"lhs\": {\n" +
            "               \"Events\": \"first\"\n" +
            "             },\n" +
            "             \"rhs\": {\n" +
            "               \"EqualsExpression\":{\n" +
            "                 \"lhs\":{\n" +
            "                    \"Event\":\"sensu.data.i\"\n" +
            "                 },\n" +
            "                 \"rhs\":{\n" +
            "                    \"Integer\":1\n" +
            "                 }\n" +
            "               }\n" +
            "             }\n" +
            "           }\n" +
            "         },\n" +
            "         \"action\":{\n" +
            "            \"assert_fact\":{\n" +
            "               \"ruleset\":\"Test rules4\",\n" +
            "               \"fact\":{\n" +
            "                  \"j\":1\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"condition\":{\n" +
            "            \"EqualsExpression\":{\n" +
            "               \"lhs\":{\n" +
            "                  \"sensu\":\"data.i\"\n" +
            "               },\n" +
            "               \"rhs\":{\n" +
            "                  \"Integer\":2\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"action\":{\n" +
            "            \"run_playbook\":[\n" +
            "               {\n" +
            "                  \"name\":\"hello_playbook.yml\"\n" +
            "               }\n" +
            "            ]\n" +
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"condition\":{\n" +
            "            \"EqualsExpression\":{\n" +
            "               \"lhs\":{\n" +
            "                  \"sensu\":\"data.i\"\n" +
            "               },\n" +
            "               \"rhs\":{\n" +
            "                  \"Integer\":3\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"action\":{\n" +
            "            \"retract_fact\":{\n" +
            "               \"ruleset\":\"Test rules4\",\n" +
            "               \"fact\":{\n" +
            "                  \"j\":3\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"condition\":{\n" +
            "            \"AllCondition\":[{\n" +
            "              \"EqualsExpression\":{\n" +
            "                 \"lhs\":\"j\",\n" +
            "                 \"rhs\":{\n" +
            "                    \"Integer\":1\n" +
            "                 }\n" +
            "              }\n" +
            "            }]" +
            "          },\n" +
            "         \"action\":{\n" +
            "            \"post_event\":{\n" +
            "               \"ruleset\":\"Test rules4\",\n" +
            "               \"fact\":{\n" +
            "                  \"j\":4\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }}\n" +
            "   ]\n" +
            "}";

    @Test
    public void testReadJson() throws JsonProcessingException {
        System.out.println(JSON1);

        ObjectMapper mapper = createMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(JSON1, RulesSet.class);
        System.out.println(rulesSet);
    }

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);
        int executedRules = rulesExecutor.executeFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 2, executedRules );
        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );
        assertEquals( 1, matchedRules.get(0).getDeclarationIds().size() );
        assertEquals( "first", matchedRules.get(0).getDeclarationIds().get(0) );

        matchedRules = rulesExecutor.processFacts( "{ \"j\":1 }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_3", matchedRules.get(0).getRule().getName() );
        assertEquals( 1, matchedRules.get(0).getDeclarationIds().size() );
        assertEquals( "m", matchedRules.get(0).getDeclarationIds().get(0) );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithBoolean() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\":{ \"EqualsExpression\":{ \"lhs\":{ \"sensu\":\"data.i\" }, \"rhs\":{ \"Boolean\":true } } } }} ] }");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":true } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithoutAction() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\":{ \"EqualsExpression\":{ \"lhs\":{ \"sensu\":\"data.i\" }, \"rhs\":{ \"Integer\":1 } } } }} ] }");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithUnknownAction() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\":{ \"EqualsExpression\":{ \"lhs\":{ \"sensu\":\"data.i\" }, \"rhs\":{ \"Integer\":1 } } }, \"action\": { \"unknown\": { \"ruleset\": \"Test rules4\", \"fact\": { \"j\": 1 } } } }} ] }\n");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testIsDefinedExpression() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\":{ \"IsDefinedExpression\":{ \"sensu\":\"data.i\" } } }} ] }");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessNoteEqualsWithNull() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", \"condition\":{ \"NotEqualsExpression\":{ \"lhs\":{ \"sensu\":\"data.i\" }, \"rhs\":{ \"Integer\":1 } } } }} ] }");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"j\":1 } } }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":2 } } }" );
        assertEquals( 1, matchedRules.size() );

        rulesExecutor.dispose();
    }
}
