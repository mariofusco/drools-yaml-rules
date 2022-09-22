package org.drools.yaml.core.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
