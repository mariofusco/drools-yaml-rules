package org.drools.yaml.core.jpy;

import org.drools.yaml.core.JsonTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class AstRulesEngineTest {
    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        AstRulesEngine engine = new AstRulesEngine();
        long id = engine.createRuleset(rules);

        int result = engine.assertFact(id, "{ \"sensu\": { \"data\": { \"i\":1 } } }");

        String nextResult = engine.advanceState();

        assertNotNull(nextResult);
    }
}