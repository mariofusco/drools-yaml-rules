package org.drools.yaml.runtime;

import java.util.List;

import org.drools.yaml.compilation.RulesCompiler;
import org.drools.yaml.core.RulesExecutor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.yaml.api.JsonTest.JSON1;
import static org.junit.Assert.assertEquals;

public class JsonTest {

    private static long RULES_COMPILER_ID;

    @BeforeClass
    public static void setup() {
        RULES_COMPILER_ID = compileRulesFromJson(JSON1);
    }

    @Test
    public void testExecuteRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(RULES_COMPILER_ID);
        int executedRules = rulesExecutor.executeFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(2, executedRules);
        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(RULES_COMPILER_ID);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("r_0", matchedRules.get(0).getRule().getName());
        assertEquals(1, matchedRules.get(0).getDeclarationIds().size());
        assertEquals("first", matchedRules.get(0).getDeclarationIds().get(0));

        matchedRules = rulesExecutor.processFacts("{ \"j\":1 }");
        assertEquals(1, matchedRules.size());
        assertEquals("r_3", matchedRules.get(0).getRule().getName());
        assertEquals(1, matchedRules.get(0).getDeclarationIds().size());
        assertEquals("m", matchedRules.get(0).getDeclarationIds().get(0));

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithoutAction() {
        long rulesId = compileRulesFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", " +
                                            "\"condition\":{ \"EqualsExpression\":{ " +
                                            "\"lhs\":{ \"sensu\":\"data.i\" }, " +
                                            "\"rhs\":{ \"Integer\":1 } } } }} ] }");
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    @Test
    public void testProcessRuleWithUnknownAction() {
        long rulesId = compileRulesFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", " +
                                            "\"condition\":{ \"EqualsExpression\":{ " +
                                            "\"lhs\":{ \"sensu\":\"data.i\" }, " +
                                            "\"rhs\":{ \"Integer\":1 } } }, " +
                                            "\"action\": { \"unknown\": { \"ruleset\":" +
                                            " \"Test rules4\", \"fact\": { \"j\": 1 } " +
                                            "} } }} ] }\n");
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    @Test
    public void testIsDefinedExpression() {
        long rulesId = compileRulesFromJson("{ \"rules\": [ {\"Rule\": { \"name\": \"R1\", " +
                                            "\"condition\":{ \"IsDefinedExpression\":{" +
                                            " \"sensu\":\"data.i\" } } }} ] }");
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesId);

        List<Match> matchedRules = rulesExecutor.processFacts("{ \"sensu\": { \"data\": { \"i\":1 } } }");
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        rulesExecutor.dispose();
    }

    public static long compileRulesFromJson(String json) {
        RulesCompiler rulesCompiler = RulesCompiler.createFromJson(json);
        assertThat(rulesCompiler.getKieBase()).isNotNull();
        return rulesCompiler.getId();
    }

    public static long compileRulesFromYaml(String yaml) {
        RulesCompiler rulesCompiler = RulesCompiler.createFromYaml(yaml);
        assertThat(rulesCompiler.getKieBase()).isNotNull();
        return rulesCompiler.getId();
    }
}
