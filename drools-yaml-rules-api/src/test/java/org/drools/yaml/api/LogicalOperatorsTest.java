package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

public class LogicalOperatorsTest {

    public static final String LOGICAL_OPERATOR_JSON =
            "{\n" +
            "   \"rules\":[\n" +
            "      {\"Rule\": {\n" +
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
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"name\":\"R2\",\n" +
            "         \"condition\":{\n" +
            "            \"AndExpression\":{\n" +
            "               \"lhs\":{\n" +
            "                  \"EqualsExpression\":{\n" +
            "                     \"lhs\":{\n" +
            "                        \"sensu\":\"data.i\"\n" +
            "                     },\n" +
            "                     \"rhs\":{\n" +
            "                        \"Integer\":3\n" +
            "                     }\n" +
            "                  }\n" +
            "               },\n" +
            "               \"rhs\":{\n" +
            "                  \"EqualsExpression\":{\n" +
            "                     \"lhs\":\"j\",\n" +
            "                     \"rhs\":{\n" +
            "                        \"Integer\":2\n" +
            "                     }\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"name\":\"R3\",\n" +
            "         \"condition\":{\n" +
            "            \"OrExpression\":{\n" +
            "               \"lhs\":{\n" +
            "                  \"AndExpression\":{\n" +
            "                     \"lhs\":{\n" +
            "                        \"EqualsExpression\":{\n" +
            "                           \"lhs\":{\n" +
            "                              \"sensu\":\"data.i\"\n" +
            "                           },\n" +
            "                           \"rhs\":{\n" +
            "                              \"Integer\":3\n" +
            "                           }\n" +
            "                        }\n" +
            "                     },\n" +
            "                     \"rhs\":{\n" +
            "                        \"EqualsExpression\":{\n" +
            "                           \"lhs\":\"j\",\n" +
            "                           \"rhs\":{\n" +
            "                              \"Integer\":2\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  }\n" +
            "               },\n" +
            "               \"rhs\":{\n" +
            "                  \"AllCondition\":[\n" +
            "                      {\"EqualsExpression\":{\n" +
            "                         \"lhs\":{\n" +
            "                            \"sensu\":\"data.i\"\n" +
            "                         },\n" +
            "                         \"rhs\":{\n" +
            "                            \"Integer\":4\n" +
            "                         }\n" +
            "                     }},\n" +
            "                     {\"EqualsExpression\":{\n" +
            "                        \"lhs\":\"j\",\n" +
            "                        \"rhs\":{\n" +
            "                           \"Integer\":3\n" +
            "                        }\n" +
            "                     }}\n" +
            "                  ]\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }}\n" +
            "   ]\n" +
            "}";

    @Test
    public void testReadJson() throws JsonProcessingException {
        System.out.println(LOGICAL_OPERATOR_JSON);
        ObjectMapper mapper = createMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(LOGICAL_OPERATOR_JSON, RulesSet.class);
        System.out.println(rulesSet);
        String json = mapper.writerFor(RulesSet.class).writeValueAsString(rulesSet);
        System.out.println(json);
    }
}
