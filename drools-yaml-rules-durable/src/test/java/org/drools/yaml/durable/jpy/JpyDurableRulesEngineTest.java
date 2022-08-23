package org.drools.yaml.durable.jpy;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class JpyDurableRulesEngineTest {
    @Test
    public void testJpyApi() {

        String name = "Test rules multiple hosts";
        String rules = "{\"r_0\": {\"all\": [{\"m\": {\"i\": 1}}]}, \"r_1\": {\"all\": [{\"m\": {\"i\": 2}}]}, " +
                "\"r_2\": {\"all\": [{\"m\": {\"i\": 3}}]}, " +
                "\"r_3\": {\"all\": [{\"m\": {\"i\": 4}}]}, \"r_4\": {\"all\": [{\"m\": {\"$ex\": {\"j\": 1}}}]}}";

        JpyDurableRulesEngine engine = new JpyDurableRulesEngine();
        long id = engine.createRuleset(name, rules);

        int result = engine.assertFact(id, "{\"i\": 1}");

        String nextResult = engine.advanceState();

        assertNotNull(nextResult);
    }
}