package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.api.context.RulesExecutor;

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