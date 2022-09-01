package org.drools.yaml.core.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.core.RulesExecutor;

public class PostEvent extends FactAction {

    static final String ACTION_NAME = "post_event";

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