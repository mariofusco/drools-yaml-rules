package org.drools.yaml.domain.rulesset.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public class RunPlaybook implements Action {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RunPlaybook{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Run playbook " + name);
    }
}
