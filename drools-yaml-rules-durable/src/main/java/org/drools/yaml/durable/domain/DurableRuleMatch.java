package org.drools.yaml.durable.domain;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.facttemplates.Fact;
import org.drools.yaml.core.domain.RuleMatch;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.core.SessionGenerator.GLOBAL_MAP_FIELD;

public class DurableRuleMatch {

    public static Map<String, Map> from(Match match) {
        Map<String, Object> facts = new HashMap<>();
        for (String decl : match.getDeclarationIds()) {
            Object value = match.getDeclarationValue(decl);
            if (value instanceof Fact) {
                Fact fact = (Fact) value;
                if (GLOBAL_MAP_FIELD.equals(decl)) {
                    facts.putAll(fact.asMap());
                } else {
                    facts.put(decl, fact.asMap());
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
