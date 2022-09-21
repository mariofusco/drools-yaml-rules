package org.drools.yaml.core;

import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.core.domain.RulesSet;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.core.ObjectMapperFactory.createMapper;
import static org.junit.Assert.assertEquals;

public class JsonTest {

    private static final String JSON1 =
            "{\n" +
            "   \"host_rules\":[\n" +
            "      {\n" +
            "         \"name\":\"R1\",\n" +
            "         \"condition\":{\n" +
            "            \"EqualsExpression\":{\n" +
            "               \"lhs\":{\n" +
            "                  \"sensu\":\"data.i\"\n" +
            "               },\n" +
            "               \"rhs\":{\n" +
            "                  \"Integer\":1\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"action\":{\n" +
            "            \"assert_fact\":{\n" +
            "               \"ruleset\":\"Test rules4\",\n" +
            "               \"fact\":{\n" +
            "                  \"j\":1\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"name\":\"R2\",\n" +
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
            "      },\n" +
            "      {\n" +
            "         \"name\":\"R3\",\n" +
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
            "      },\n" +
            "      {\n" +
            "         \"name\":\"R4\",\n" +
            "         \"condition\":{\n" +
            "            \"EqualsExpression\":{\n" +
            "               \"lhs\":\"j\",\n" +
            "               \"rhs\":{\n" +
            "                  \"Integer\":1\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"action\":{\n" +
            "            \"post_event\":{\n" +
            "               \"ruleset\":\"Test rules4\",\n" +
            "               \"fact\":{\n" +
            "                  \"j\":4\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
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
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.processFacts( "{ \"j\":1 }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R4", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithoutAction() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"host_rules\": [ { \"name\": \"R1\", \"condition\": \"sensu.data.i == 1\" } ] }");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithUnknownAction() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson("{ \"host_rules\": [ { \"name\": \"R1\", \"condition\": \"sensu.data.i == 1\", \"action\": { \"unknown\": { \"ruleset\": \"Test rules4\", \"fact\": { \"j\": 1 } } } } ] }\n");

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
