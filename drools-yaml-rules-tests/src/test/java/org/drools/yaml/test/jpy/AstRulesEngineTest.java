package org.drools.yaml.test.jpy;

import org.drools.yaml.api.JsonTest;
import org.drools.yaml.core.jpy.AstRulesEngine;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class AstRulesEngineTest {
    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        AstRulesEngine engine = new AstRulesEngine();
        long id = engine.createRuleset(rules);

        String result = engine.assertFact(id, "{ \"sensu\": { \"data\": { \"i\":1 } } }");

        assertNotNull(result);
    }
}