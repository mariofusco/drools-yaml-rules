package org.drools.yaml.runtime;

import org.drools.yaml.core.RulesExecutor;
import org.junit.Test;

import static org.drools.yaml.api.YamlTest.YAML1;
import static org.drools.yaml.runtime.JsonTest.compileRulesFromYaml;
import static org.junit.Assert.assertEquals;

public class YamlTest {

    @Test
    public void testExecuteRules() {
        long rulesId = compileRulesFromYaml(YAML1);
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);
        int executedRules = rulesExecutor.executeFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(2, executedRules);
    }
}