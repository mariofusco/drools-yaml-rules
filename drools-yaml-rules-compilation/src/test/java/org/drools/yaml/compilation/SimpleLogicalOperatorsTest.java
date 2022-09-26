package org.drools.yaml.compilation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.SimpleLogicalOperatorsTest.SIMPLE_LOGICAL_OPERATOR_JSON;

public class SimpleLogicalOperatorsTest {

    @Test
    public void testCompileRules() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromJson(SIMPLE_LOGICAL_OPERATOR_JSON);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(3);
    }
}
