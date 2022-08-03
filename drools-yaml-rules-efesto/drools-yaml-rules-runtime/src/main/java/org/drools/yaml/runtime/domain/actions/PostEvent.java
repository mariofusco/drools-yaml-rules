package org.drools.yaml.runtime.domain.actions;

import org.drools.model.Drools;

public class PostEvent extends FactAction {

    @Override
    public String toString() {
        return "PostEvent{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(Drools drools) {
        System.out.println("Post Event " + getFact());
    }
}