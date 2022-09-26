package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

public class AssertFact extends FactAction {

    static final String ACTION_NAME = "assert_fact";

    @Override
    public String toString() {
        return "AssertFact{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(long kieBaseHolderId, Drools drools) {
        System.out.println("Asserting " + getFact());
        kieSessionHolder(kieBaseHolderId).insertFact(getFact());
    }
}
