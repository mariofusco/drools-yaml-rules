package org.drools.yaml.core.domain;

import org.drools.yaml.core.domain.actions.RuleAction;
import org.drools.yaml.core.domain.conditions.Condition;
import org.drools.yaml.core.domain.actions.Action;

public class Rule {
    private String name;
    private Condition condition;
    private Action action;

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

    public Action getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }

    public void setGenericAction(Action action) {
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
