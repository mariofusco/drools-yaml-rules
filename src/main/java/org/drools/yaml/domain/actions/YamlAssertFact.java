package org.drools.yaml.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public class YamlAssertFact extends YamlFactAction {

    @Override
    public String toString() {
        return "YamlAssertFact{" +
                "ruleset='" + getRuleset() + '\'' +
                ", fact=" + getFact() +
                '}';
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        System.out.println("Asserting " + getFact());
        rulesExecutor.processFacts(getFact());
    }
}
