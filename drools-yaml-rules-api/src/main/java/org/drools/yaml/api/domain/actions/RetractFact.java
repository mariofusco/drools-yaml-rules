package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

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
    public void execute(long kieBaseHolderId, Drools drools) {
        System.out.println("Retracting " + getFact());
    }
}
