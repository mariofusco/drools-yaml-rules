package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

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
    public void execute(Drools drools) {
        System.out.println("Run playbook " + name);
    }
}
