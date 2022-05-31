package org.drools.yaml.core;

import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.core.RulesExecutor;
import org.drools.yaml.core.domain.RulesSet;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

public class JsonTest {

    private static final String JSON1 =
            "{\n" +
            "  \"host_rules\": [\n" +
            "    {\n" +
            "      \"name\": \"R1\",\n" +
            "      \"condition\": \"sensu.data.i == 1\",\n" +
            "      \"action\": {\n" +
            "        \"assert_fact\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 1\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R2\",\n" +
            "      \"condition\": \"sensu.data.i == 2\",\n" +
            "      \"action\": {\n" +
            "        \"run_playbook\": [\n" +
            "          {\n" +
            "            \"name\": \"hello_playbook.yml\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R3\",\n" +
            "      \"condition\": \"sensu.data.i == 3\",\n" +
            "      \"action\": {\n" +
            "        \"retract_fact\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 3\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"R4\",\n" +
            "      \"condition\": \"j == 1\",\n" +
            "      \"action\": {\n" +
            "        \"post_event\": {\n" +
            "          \"ruleset\": \"Test rules4\",\n" +
            "          \"fact\": {\n" +
            "            \"j\": 4\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void testReadJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(JSON1, RulesSet.class);
        System.out.println(rulesSet);
    }

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);
        int executedRules = rulesExecutor.execute( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 2, executedRules );
        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.process( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.process( "{ \"j\":1 }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R4", matchedRules.get(0).getRule().getName() );

        rulesExecutor.dispose();
    }
}
