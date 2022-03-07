package org.drools.yaml.domain.actions;

import java.util.Map;

public abstract class YamlFactAction implements Action {
    private String ruleset;
    private Map<String, Object> fact;

    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    public Map<String, Object> getFact() {
        return fact;
    }

    public void setFact(Map<String, Object> fact) {
        this.fact = fact;
    }
}
