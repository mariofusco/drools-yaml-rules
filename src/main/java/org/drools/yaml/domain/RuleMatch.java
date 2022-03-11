package org.drools.yaml.domain;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.facttemplates.Fact;
import org.kie.api.runtime.rule.Match;

import static org.drools.yaml.SessionGenerator.GLOBAL_MAP_FIELD;

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
                if (GLOBAL_MAP_FIELD.equals(decl)) {
                    facts.putAll(fact.asMap());
                } else {
                    facts.put(decl, new MatchedFact(fact.getFactTemplate().getName(), fact.asMap()));
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

    public static class MatchedFact {
        private final String type;
        private final Map<String, Object> values;

        public MatchedFact(String type, Map<String, Object> values) {
            this.type = type;
            this.values = values;
        }

        public String getType() {
            return type;
        }

        public Map<String, Object> getValues() {
            return values;
        }
    }
}
