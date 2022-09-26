package org.drools.yaml.compilation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.SimpleJsonTest.SIMPLE_JSON;

public class SimpleJsonTest {

    @Test
    public void testCompileRules() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromJson(SIMPLE_JSON);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(4);
    }
}
