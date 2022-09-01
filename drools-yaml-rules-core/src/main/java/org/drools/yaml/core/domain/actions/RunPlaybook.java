package org.drools.yaml.core.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.core.RulesExecutor;

public class RunPlaybook implements Action {

    static final String ACTION_NAME = "run_playbook";

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
