package org.drools.yaml.compilation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.JsonTest.JSON1;

public class JsonTest {



    @Test
    public void testCompileRules() {
        RulesCompiler rulesExecutor = RulesCompiler.createFromJson(JSON1);
        assertThat(rulesExecutor.rulesCount()).isEqualTo(4);
    }

}
