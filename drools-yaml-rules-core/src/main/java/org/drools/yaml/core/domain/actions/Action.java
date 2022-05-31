package org.drools.yaml.core.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.core.RulesExecutor;

public interface Action {
    void execute(RulesExecutor rulesExecutor, Drools drools);
}
