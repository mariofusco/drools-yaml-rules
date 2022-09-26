package org.drools.yaml.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.drools.yaml.api.domain.Rule;

public class RuleContainer {

    @JsonProperty("Rule")
    private Rule rule;

    public RuleContainer() { }

    public RuleContainer(Rule rule) {
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
