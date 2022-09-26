package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

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
    public void execute(long kieBaseHolderId, Drools drools) {
        System.out.println("Run playbook " + name);
    }
}
