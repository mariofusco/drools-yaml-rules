package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.api.RulesExecutor;

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
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Asserting " + getFact());
        rulesExecutor.insertFact(getFact());
    }
}
