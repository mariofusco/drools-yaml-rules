package org.drools.yaml.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.RulesExecutor;

public interface Action {
    void execute(RulesExecutor rulesExecutor, Drools drools);
}
