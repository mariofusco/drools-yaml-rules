package org.drools.yaml.runtime.utils;

import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.yaml.api.domain.RuleMatch;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Match;

import static org.junit.Assert.assertEquals;

@QuarkusTest
public class LogicalOperatorsTest {

    private static final String JSON1 =
            "{\n" +
                    "   \"host_rules\":[\n" +
                    "      {\n" +
                    "         \"name\":\"R1\",\n" +
                    "         \"condition\":\"sensu.data.i == 1\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"name\":\"R2\",\n" +
                    "         \"condition\":{\n" +
                    "            \"all\":[\n" +
                    "               \"sensu.data.i == 3\",\n" +
                    "               \"j == 2\"\n" +
                    "            ]\n" +
                    "         }\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"name\":\"R3\",\n" +
                    "         \"condition\":{\n" +
                    "            \"any\":[\n" +
                    "               {\n" +
                    "                  \"all\":[\n" +
                    "                     \"sensu.data.i == 3\",\n" +
                    "                     \"j == 2\"\n" +
                    "                  ]\n" +
                    "               },\n" +
                    "               {\n" +
                    "                  \"all\":[\n" +
                    "                     \"sensu.data.i == 4\",\n" +
                    "                     \"j == 3\"\n" +
                    "                  ]\n" +
                    "               }\n" +
                    "            ]\n" +
                    "         }\n" +
                    "      }\n" +
                    "   ]\n" +
                    "}";

    @Test
    public void testProcessRules() {

        KieSession kieSession = null; // TODO

        List<Match> matchedRules = DroolsYamlUtils.process("{ \"sensu\": { \"data\": { \"i\":1 } } }", kieSession);
        assertEquals(1, matchedRules.size());
        assertEquals("R1", matchedRules.get(0).getRule().getName());

        matchedRules = DroolsYamlUtils.process("{ facts: [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }"
                , kieSession);
        assertEquals(0, matchedRules.size());

        matchedRules = DroolsYamlUtils.process("{ \"sensu\": { \"data\": { \"i\":4 } } }", kieSession);
        assertEquals(1, matchedRules.size());

        RuleMatch ruleMatch = RuleMatch.from(matchedRules.get(0));
        assertEquals("R3", ruleMatch.getRuleName());
        assertEquals(3, ruleMatch.getFacts().get("j"));

        assertEquals(4, ((Map) ((Map) ruleMatch.getFacts().get("sensu")).get("data")).get("i"));

//        rulesExecutor.dispose();
    }
}
