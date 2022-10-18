package org.drools.yaml.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;
import static org.junit.Assert.assertEquals;

public class LogicalOperatorsTest {

    private static final String JSON1 =
            "{\n" +
            "   \"rules\":[\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"name\":\"R1\",\n" +
            "            \"condition\":{\n" +
            "               \"EqualsExpression\":{\n" +
            "                  \"lhs\":{\n" +
            "                     \"sensu\":\"data.i\"\n" +
            "                  },\n" +
            "                  \"rhs\":{\n" +
            "                     \"Integer\":1\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"name\":\"R2\",\n" +
            "            \"condition\":{\n" +
            "               \"AllCondition\":[\n" +
            "                  {\n" +
            "                     \"EqualsExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"sensu\":\"data.i\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"Integer\":3\n" +
            "                        }\n" +
            "                     }\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"EqualsExpression\":{\n" +
            "                        \"lhs\":\"j\",\n" +
            "                        \"rhs\":{\n" +
            "                           \"Integer\":2\n" +
            "                        }\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      },\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"name\":\"R3\",\n" +
            "            \"condition\":{\n" +
            "               \"AnyCondition\":[\n" +
            "                  {\n" +
            "                     \"AllCondition\":[\n" +
            "                        {\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"sensu\":\"data.i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":3\n" +
            "                              }\n" +
            "                           }\n" +
            "                        },\n" +
            "                        {\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":\"j\",\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":2\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     ]\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"AllCondition\":[\n" +
            "                        {\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"sensu\":\"data.i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":4\n" +
            "                              }\n" +
            "                           }\n" +
            "                        },\n" +
            "                        {\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":\"j\",\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":3\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     ]\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    @Test
    public void testReadJson() throws JsonProcessingException {
        System.out.println(JSON1);
        ObjectMapper mapper = createMapper(new JsonFactory());
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

    private static final String JSON2 =
            "{\n" +
            "   \"rules\":[\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"condition\":{\n" +
            "               \"AllCondition\":[\n" +
            "                  {\n" +
            "                     \"AndExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"AndExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"GreaterThanExpression\":{\n" +
            "                                    \"lhs\":{\n" +
            "                                       \"Event\":\"i\"\n" +
            "                                    },\n" +
            "                                    \"rhs\":{\n" +
            "                                       \"Integer\":0\n" +
            "                                    }\n" +
            "                                 }\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"GreaterThanExpression\":{\n" +
            "                                    \"lhs\":{\n" +
            "                                       \"Event\":\"i\"\n" +
            "                                    },\n" +
            "                                    \"rhs\":{\n" +
            "                                       \"Integer\":1\n" +
            "                                    }\n" +
            "                                 }\n" +
            "                              }\n" +
            "                           }\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"GreaterThanExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":3\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    @Test
    public void testMultipleConditionOnSameFact() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON2);

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"i\":1 }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"i\":2 }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"i\":3 }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"i\":4 }" );
        assertEquals( 1, matchedRules.size() );
    }

    private static final String JSON3 =
            "{\n" +
            "   \"rules\":[\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"condition\":{\n" +
            "               \"AllCondition\":[\n" +
            "                  {\n" +
            "                     \"OrExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"LessThanExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":1\n" +
            "                              }\n" +
            "                           }\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"GreaterThanExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":3\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    @Test
    public void testOr() {
        System.out.println(JSON3);
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON3);

        List<Match> matchedRules = rulesExecutor.processFacts( "{ \"i\":0 }" );
        assertEquals( 1, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"i\":2 }" );
        assertEquals( 0, matchedRules.size() );

        matchedRules = rulesExecutor.processFacts( "{ \"i\":4 }" );
        assertEquals( 1, matchedRules.size() );
    }
}
