package org.drools.yaml.domain;

import org.drools.yaml.domain.actions.RuleAction;
import org.drools.yaml.domain.conditions.Condition;

public class Rule {
    private String name;
    private Condition condition;
    private RuleAction action;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
