package org.drools.yaml.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.api.domain.RulesSet;
import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;
import static org.junit.Assert.assertEquals;

public class MultipleConditionTest {

    public static final String JSON1 =
            "{\n" +
            "   \"sources\":{\n" +
            "      \"EventSource\":\"test\"\n" +
            "   },\n" +
            "   \"rules\":[\n" +
            "      {\n" +
            "         \"Rule\":{\n" +
            "            \"condition\":{\n" +
            "               \"AllCondition\":[\n" +
            "                  {\n" +
            "                     \"AssignmentExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"Events\":\"first\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":0\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"AssignmentExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"Events\":\"second\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"Integer\":1\n" +
            "                              }\n" +
            "                           }\n" +
            "                        }\n" +
            "                     }\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"AssignmentExpression\":{\n" +
            "                        \"lhs\":{\n" +
            "                           \"Events\":\"third\"\n" +
            "                        },\n" +
            "                        \"rhs\":{\n" +
            "                           \"EqualsExpression\":{\n" +
            "                              \"lhs\":{\n" +
            "                                 \"Event\":\"i\"\n" +
            "                              },\n" +
            "                              \"rhs\":{\n" +
            "                                 \"AdditionExpression\":{\n" +
            "                                    \"lhs\":{\n" +
            "                                       \"Event\":\"first.i\"\n" +
            "                                    },\n" +
            "                                    \"rhs\":{\n" +
            "                                       \"Integer\":2\n" +
            "                                    }\n" +
            "                                 }\n" +
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
    public void testReadJson() {
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(JSON1);

        List<Match> matchedRules = rulesExecutor.processEvents( "{ events: [ { \"i\":0 }, { \"i\":1 }, { \"i\":2 } ] }" );

        assertEquals( 1, matchedRules.size() );
        assertEquals( "r_0", matchedRules.get(0).getRule().getName() );

        RuleMatch ruleMatch = RuleMatch.from(matchedRules.get(0));
        assertEquals(3, ruleMatch.getFacts().size());
        assertEquals(Map.of("i", 0), ruleMatch.getFacts().get("first"));
        assertEquals(Map.of("i", 1), ruleMatch.getFacts().get("second"));
        assertEquals(Map.of("i", 2), ruleMatch.getFacts().get("third"));

        rulesExecutor.dispose();
    }

}
