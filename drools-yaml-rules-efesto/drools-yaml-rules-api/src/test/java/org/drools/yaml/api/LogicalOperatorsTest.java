package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.Rule;
import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.domain.conditions.Condition;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testWriteJson() throws JsonProcessingException {
        Rule rule = new Rule();
        Condition c1 = new Condition();
        c1.setAll(Arrays.asList(new Condition("sensu.data.i == 3"), new Condition("j == 2")));
        Condition c2 = new Condition();
        c2.setAll(Arrays.asList(new Condition("sensu.data.i == 4"), new Condition("j == 3")));
        Condition c3 = new Condition();
        c3.setAny(Arrays.asList(c1, c2));
        rule.setCondition(c3);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String json = mapper.writerFor(Rule.class).writeValueAsString(rule);
        System.out.println(json);
    }

    @Test
    public void testReadJson() throws JsonProcessingException {
        System.out.println(JSON1);
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        RulesSet rulesSet = mapper.readValue(JSON1, RulesSet.class);
        System.out.println(rulesSet);
        String json = mapper.writerFor(RulesSet.class).writeValueAsString(rulesSet);
        System.out.println(json);
    }

    @Test
    public void testProcessRules() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":1 } } }" );
        assertEquals( 1, matchedRules.size() );
        assertEquals( "R1", matchedRules.get(0).getRule().getName() );

        matchedRules = rulesExecutor.processFacts( "{ facts: [ { \"sensu\": { \"data\": { \"i\":3 } } }, { \"j\":3 } ] }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"sensu\": { \"data\": { \"i\":4 } } }" );
        assertEquals( 1, matchedRules.size() );

        RuleMatch ruleMatch = RuleMatch.from( matchedRules.get(0) );
        assertEquals( "R3", ruleMatch.getRuleName() );
        assertEquals( 3, ruleMatch.getFacts().get("j") );

        assertEquals( 4, ((Map) ((Map) ruleMatch.getFacts().get("sensu")).get("data")).get("i") );

        rulesExecutor.dispose();
    }
}
