package org.drools.yaml.compilation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.LogicalOperatorsTest.LOGICAL_OPERATOR_JSON;

public class LogicalOperatorsTest {

    @Test
    public void testCompileRules() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromJson(LOGICAL_OPERATOR_JSON);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(3);
    }
}
