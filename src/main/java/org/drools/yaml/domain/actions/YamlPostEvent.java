package org.drools.yaml.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public class YamlPostEvent extends YamlFactAction {

    @Override
    public String toString() {
        return "YamlPostEvent{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Post Event " + getFact());
    }
}