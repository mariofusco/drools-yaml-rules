package org.drools.yaml.core.jpy;

import org.drools.yaml.core.JsonTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class JpyAstRulesEngineTest {
    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        JpyAstRulesEngine engine = new JpyAstRulesEngine();
        long id = engine.createRuleset(rules);

        int result = engine.assertFact(id, "{ \"sensu\": { \"data\": { \"i\":1 } } }");

        String nextResult = engine.advanceState();

        assertNotNull(nextResult);
    }
}