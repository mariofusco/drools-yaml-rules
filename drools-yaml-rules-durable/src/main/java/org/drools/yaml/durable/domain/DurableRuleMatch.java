package org.drools.yaml.durable.domain;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.facttemplates.Fact;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.core.domain.Binding.isGeneratedBinding;
import static org.drools.yaml.core.domain.RuleMatch.toNestedMap;

public class DurableRuleMatch {

    public static Map<String, Map> from(Match match) {
        Map<String, Object> facts = new HashMap<>();
        for (String decl : match.getDeclarationIds()) {
            Object value = match.getDeclarationValue(decl);
            if (value instanceof Fact) {
                Fact fact = (Fact) value;
                Map<String, Object> map = toNestedMap( fact.asMap()) ;
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
