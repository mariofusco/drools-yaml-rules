package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

public class YamlTest {

    public static final String YAML1 =
            "  rules:\n" +
                    "  - Rule:\n" +
                    "      condition: sensu.data.i == 1\n" +
                    "      action:\n" +
                    "        assert_fact:\n" +
                    "          ruleset: Test rules4\n" +
                    "          fact:\n" +
                    "            j: 1\n" +
                    "  - Rule:\n" +
                    "      condition: sensu.data.i == 2\n" +
            "      action:\n" +
            "        run_playbook:\n" +
            "          - name: hello_playbook.yml\n" +
            "  - Rule:\n" +
            "      condition: sensu.data.i == 3\n" +
            "      action:\n" +
            "        retract_fact:\n" +
            "          ruleset: Test rules4\n" +
            "          fact:\n" +
            "            j: 3\n" +
            "  - Rule:\n" +
            "      condition: j == 1\n" +
            "      action:\n" +
            "        post_event:\n" +
            "          ruleset: Test rules4\n" +
                    "          fact:\n" +
                    "            j: 4\n\n";

    @Test
    public void testReadSimpleYaml() throws JsonProcessingException {
        ObjectMapper mapper = createMapper(new YAMLFactory());
        RulesSet rulesSet = mapper.readValue(YAML1, RulesSet.class);
        System.out.println(rulesSet);
    }

    public static final String YAML2 =
            "    hosts:\n" +
                    "    - localhost\n" +
                    "    name: Demo rules\n" +
                    "    rules:\n" +
                    "    - Rule:\n" +
                    "        condition:\n" +
                    "          AllCondition:\n" +
                    "          - EqualsExpression:\n" +
                    "              lhs:\n" +
                    "                Event: payload.provisioningState\n" +
            "              rhs:\n" +
            "                String: Succeeded\n" +
            "        enabled: true\n" +
            "        name: send to slack3\n" +
            "    - Rule:\n" +
            "        condition:\n" +
            "          AllCondition:\n" +
            "          - EqualsExpression:\n" +
            "              lhs:\n" +
            "                Event: payload.provisioningState\n" +
            "              rhs:\n" +
            "                String: Deleted\n" +
            "        enabled: true\n" +
            "        name: send to slack4\n" +
            "    - Rule:\n" +
            "        condition:\n" +
            "          AllCondition:\n" +
            "          - NotEqualsExpression:\n" +
            "              lhs:\n" +
            "                Event: payload.eventType\n" +
            "              rhs:\n" +
            "                String: GET\n" +
            "        enabled: true\n" +
            "        name: send to slack5\n" +
            "    - Rule:\n" +
            "        condition:\n" +
            "          AllCondition:\n" +
            "          - NotEqualsExpression:\n" +
            "              lhs:\n" +
            "                Event: payload.text\n" +
            "              rhs:\n" +
            "                String: ''\n" +
            "        enabled: true\n" +
            "        name: send to slack6\n" +
            "    - Rule:\n" +
            "        condition:\n" +
            "          AllCondition:\n" +
            "          - NotEqualsExpression:\n" +
            "              lhs:\n" +
            "                Event: payload.text\n" +
            "              rhs:\n" +
            "                String: ''\n" +
            "        enabled: true\n" +
            "        name: assert fact\n" +
            "    - Rule:\n" +
            "        condition:\n" +
            "          AllCondition:\n" +
            "          - NotEqualsExpression:\n" +
            "              lhs:\n" +
            "                Event: payload.text\n" +
            "              rhs:\n" +
            "                String: ''\n";

    @Test
    public void testReadYaml() throws JsonProcessingException {
        ObjectMapper mapper = createMapper(new YAMLFactory());
        RulesSet rulesSet = mapper.readValue(YAML2, RulesSet.class);
        System.out.println(rulesSet);
    }
}