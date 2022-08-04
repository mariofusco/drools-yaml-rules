package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

public class AssertFact extends FactAction {

    @Override
    public String toString() {
        return "AssertFact{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(Drools drools) {
        System.out.println("Asserting " + getFact());
        // TODO
//        rulesExecutor.processFact(getFact());
    }
}
