package org.drools.yaml.compilation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.YamlTest.YAML1;
import static org.drools.yaml.api.YamlTest.YAML2;

public class YamlTest {

    @Test
    public void testCompileRules1() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromYaml(YAML1);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(4);
    }

    @Test
    public void testCompileRules2() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromYaml(YAML2);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(6);
    }
}