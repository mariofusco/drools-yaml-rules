package org.drools.yaml.domain.rulesset.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public class PostEvent extends FactAction {

    @Override
    public String toString() {
        return "PostEvent{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Post Event " + getFact());
    }
}