package org.drools.yaml.durable.domain;

import org.drools.model.Drools;
import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.domain.actions.Action;

public class RunAction implements Action {
    private final String name;

    public RunAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Run{" + "name='" + name + '\'' + '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Run " + name);
    }
}
