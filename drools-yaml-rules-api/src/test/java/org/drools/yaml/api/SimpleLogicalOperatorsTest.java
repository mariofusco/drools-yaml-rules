package org.drools.yaml.api;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.Rule;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.domain.conditions.SimpleCondition;
import org.junit.Test;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

public class SimpleLogicalOperatorsTest {

    public static final String SIMPLE_LOGICAL_OPERATOR_JSON =
            "{\n" +
                    "   \"rules\":[\n" +
                    "      {\"Rule\": {\n" +
                    "         \"name\":\"R1\",\n" +
                    "         \"condition\":\"sensu.data.i == 1\"\n" +
                    "      }},\n" +
                    "      {\"Rule\": {\n" +
                    "         \"name\":\"R2\",\n" +
                    "         \"condition\":{\n" +
                    "            \"all\":[\n" +
            "               \"sensu.data.i == 3\",\n" +
            "               \"j == 2\"\n" +
            "            ]\n" +
            "         }\n" +
            "      }},\n" +
            "      {\"Rule\": {\n" +
            "         \"name\":\"R3\",\n" +
            "         \"condition\":{\n" +
            "            \"any\":[\n" +
            "               {\n" +
            "                  \"all\":[\n" +
            "                     \"sensu.data.i == 3\",\n" +
            "                     \"j == 2\"\n" +
            "                  ]\n" +
            "               },\n" +
            "               {\n" +
            "                  \"all\":[\n" +
            "                     \"sensu.data.i == 4\",\n" +
            "                     \"j == 3\"\n" +
            "                  ]\n" +
            "               }\n" +
            "            ]\n" +
            "         }\n" +
            "      }}\n" +
            "   ]\n" +
            "}";


    @Test
    public void testWriteJson() throws JsonProcessingException {
        Rule rule = new Rule();
        SimpleCondition c1 = new SimpleCondition();
        c1.setAll(Arrays.asList(new SimpleCondition("sensu.data.i == 3"), new SimpleCondition("j == 2")));
        SimpleCondition c2 = new SimpleCondition();
        c2.setAll(Arrays.asList(new SimpleCondition("sensu.data.i == 4"), new SimpleCondition("j == 3")));
        SimpleCondition c3 = new SimpleCondition();
        c3.setAny(Arrays.asList(c1, c2));
        rule.setCondition(c3);

        ObjectMapper mapper = createMapper(new JsonFactory());
        String json = mapper.writerFor(Rule.class).writeValueAsString(rule);
        System.out.println(json);
    }

    @Test
    public void testReadJson() throws JsonProcessingException {
        System.out.println(SIMPLE_LOGICAL_OPERATOR_JSON);
        ObjectMapper mapper = createMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(SIMPLE_LOGICAL_OPERATOR_JSON, RulesSet.class);
        System.out.println(rulesSet);
        String json = mapper.writerFor(RulesSet.class).writeValueAsString(rulesSet);
        System.out.println(json);
    }
}
