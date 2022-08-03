package org.drools.yaml.runtime.domain.actions;

import org.drools.model.Drools;

public class RetractFact extends FactAction {

    @Override
    public String toString() {
        return "RetractFact{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(Drools drools) {
        System.out.println("Retracting " + getFact());
    }
}
