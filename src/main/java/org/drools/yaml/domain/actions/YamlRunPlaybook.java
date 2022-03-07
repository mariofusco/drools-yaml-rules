package org.drools.yaml.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public class YamlRunPlaybook implements Action {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "YamlRunPlaybook{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Run playbook " + name);
    }
}
