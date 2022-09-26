package org.drools.yaml.test.jpy;

import org.drools.yaml.api.JsonTest;
import org.drools.yaml.compilation.jpy.AstRulesCompilation;
import org.drools.yaml.core.jpy.AstRulesEngine;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AstRulesTest {
    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        AstRulesCompilation rulesCompilation = new AstRulesCompilation();
        long id = rulesCompilation.createRuleset(rules);

        AstRulesEngine rulesEngine = new AstRulesEngine();

        String result = rulesEngine.assertFact(id, "{ \"sensu\": { \"data\": { \"i\":1 } } }");
//
//        String nextResult = rulesEngine.advanceState();

        assertNotNull(result);
    }
}