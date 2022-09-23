package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.api.RulesExecutor;

public interface Action {
    void execute(RulesExecutor rulesExecutor, Drools drools);
}
