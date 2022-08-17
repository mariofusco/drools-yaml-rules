package org.drools.yaml.durable.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.core.facttemplates.Fact;
import org.drools.yaml.core.RulesExecutor;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.core.domain.Binding.isGeneratedBinding;
import static org.drools.yaml.core.domain.RuleMatch.toNestedMap;

public class DurableRuleMatch {

    public static List<Map<String, Map>> asList(Collection<Match> matches) {
        return matches.stream()
                .map(DurableRuleMatch::from)
                .collect(Collectors.toList());
    }

    public static String asJson(Collection<Match> matches) {
        try {
            return RulesExecutor.OBJECT_MAPPER.writeValueAsString(asList(matches));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Map> from(Match match) {
        Map<String, Object> facts = new HashMap<>();
        for (String decl : match.getDeclarationIds()) {
            Object value = match.getDeclarationValue(decl);
            if (value instanceof Fact) {
                Fact fact = (Fact) value;
                Map<String, Object> map = toNestedMap(fact.asMap());
                if (isGeneratedBinding(decl)) {
                    facts.putAll(map);
                } else {
                    facts.put(decl, map);
                }
            } else {
                facts.put(decl, value);
            }
        }

        Map<String, Map> result = new HashMap<>();
        result.put(match.getRule().getName(), facts);
        return result;
    }
}
