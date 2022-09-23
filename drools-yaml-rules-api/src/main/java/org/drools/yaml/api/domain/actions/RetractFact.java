package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.api.RulesExecutor;

public class RetractFact extends FactAction {

    static final String ACTION_NAME = "retract_fact";

    @Override
    public String toString() {
        return "RetractFact{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Retracting " + getFact());
    }
}
