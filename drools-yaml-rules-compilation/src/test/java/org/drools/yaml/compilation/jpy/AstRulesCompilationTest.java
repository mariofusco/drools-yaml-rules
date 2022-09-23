package org.drools.yaml.compilation.jpy;

import org.drools.yaml.api.JsonTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AstRulesCompilationTest {

    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        AstRulesCompilation engine = new AstRulesCompilation();
        long id = engine.createRuleset(rules);
        assertThat(id).isGreaterThan(0);
    }
}