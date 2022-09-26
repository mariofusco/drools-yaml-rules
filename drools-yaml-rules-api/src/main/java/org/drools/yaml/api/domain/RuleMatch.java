package org.drools.yaml.api.domain;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.facttemplates.Fact;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.api.RuleGenerationContext.isGeneratedBinding;

public class RuleMatch {

    private final String ruleName;
    private final Map<String, Object> facts;

    public RuleMatch(String ruleName, Map<String, Object> facts) {
        this.ruleName = ruleName;
        this.facts = facts;
    }

    public static RuleMatch from(Match match) {
        String ruleName = match.getRule().getName();
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
        return new RuleMatch(ruleName, facts);
    }

    public String getRuleName() {
        return ruleName;
    }

    public Map<String, Object> getFacts() {
        return facts;
    }

    public static Map<String, Object> toNestedMap(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        map.entrySet().forEach(e -> addToNestedMap(result, e.getKey(), e.getValue()));
        return result;
    }

    private static void addToNestedMap(Map<String, Object> result, String key, Object value) {
        int dotPos = key.indexOf('.');
        if (dotPos < 0) {
            result.put(key, value);
        } else {
            addToNestedMap( (Map<String, Object>) result.computeIfAbsent(key.substring(0, dotPos), s -> new HashMap<>()), key.substring(dotPos+1), value);
        }
    }
}
