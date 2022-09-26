package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

public class SimpleJsonTest {

    public static final String SIMPLE_JSON =
            "{\n" +
                    "  \"rules\": [\n" +
                    "    {\"Rule\": {\n" +
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
            "    }},\n" +
            "    {\"Rule\": {\n" +
            "      \"name\": \"R2\",\n" +
            "      \"condition\": \"sensu.data.i == 2\",\n" +
            "      \"action\": {\n" +
            "        \"run_playbook\": [\n" +
            "          {\n" +
            "            \"name\": \"hello_playbook.yml\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }},\n" +
            "    {\"Rule\": {\n" +
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
            "    }},\n" +
            "    {\"Rule\": {\n" +
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
            "    }}\n" +
            "  ]\n" +
            "}";

    @Test
    public void testReadJson() throws JsonProcessingException {
        ObjectMapper mapper = createMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(SIMPLE_JSON, RulesSet.class);
        System.out.println(rulesSet);
    }
}
