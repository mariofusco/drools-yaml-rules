package org.drools.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.yaml.domain.YamlRulesSet;
import org.junit.Test;

public class YamlTest {

    private static final String YAML1 =
            "  name: Test rules4\n" +
            "  hosts: all\n" +
            "  sources:\n" +
            "    - name: sensu\n" +
            "      topic: sensuprod\n" +
            "      url: prod\n" +
            "      schema: sensu/v1\n" +
            "  host_rules:\n" +
            "    - name:\n" +
            "      condition: sensu.data.i == 1\n" +
            "      action:\n" +
            "        assert_fact:\n" +
            "          ruleset: Test rules4\n" +
            "          fact:\n" +
            "            j: 1\n" +
            "    - name:\n" +
            "      condition: sensu.data.i == 2\n" +
            "      action:\n" +
            "        run_playbook:\n" +
            "          - name: hello_playbook.yml\n" +
            "    - name:\n" +
            "      condition: sensu.data.i == 3\n" +
            "      action:\n" +
            "        retract_fact:\n" +
            "          ruleset: Test rules4\n" +
            "          fact:\n" +
            "            j: 3\n" +
            "    - name:\n" +
            "      condition: j == 1\n" +
            "      action:\n" +
            "        post_event:\n" +
            "          ruleset: Test rules4\n" +
            "          fact:\n" +
            "            j: 4\n\n";


    @Test
    public void testReadYaml() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        YamlRulesSet rulesSet = mapper.readValue(YAML1, YamlRulesSet.class);
        System.out.println(rulesSet);
    }

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.create(YAML1);
        rulesExecutor.process( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
    }
}