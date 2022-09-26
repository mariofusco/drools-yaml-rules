package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

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
}
