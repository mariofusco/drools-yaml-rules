package org.drools.yaml.test.jpy;

import org.drools.yaml.api.JsonTest;
import org.drools.yaml.core.jpy.AstRulesEngine;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;


public class AstRulesEngineTest {
    @Test
    public void testJpyApi() {

        String rules = JsonTest.JSON1;

        AstRulesEngine engine = new AstRulesEngine();
        long id = engine.createRuleset(rules);

        String result = engine.assertFact(id, "{ \"sensu\": { \"data\": { \"i\":1 } } }");

        assertNotNull(result);
    }

    @Test
    public void testBrokenApi() throws IOException {
        try (InputStream s = getClass().getClassLoader().getResourceAsStream("broken.json")) {
            String rules = new String(s.readAllBytes());

            AstRulesEngine engine = new AstRulesEngine();
            assertThrows(UnsupportedOperationException.class, () -> engine.createRuleset(rules));
        }
    }

}