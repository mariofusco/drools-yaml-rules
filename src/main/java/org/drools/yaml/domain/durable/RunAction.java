package org.drools.yaml.domain.durable;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;
import org.drools.yaml.domain.rulesset.actions.Action;

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
