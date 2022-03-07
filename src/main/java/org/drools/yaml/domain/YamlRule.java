package org.drools.yaml.domain;

public class YamlRule {
    private String name;
    private String condition;
    private YamlRuleAction action;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public YamlRuleAction getAction() {
        return action;
    }

    public void setAction(YamlRuleAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "YamlRule{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
