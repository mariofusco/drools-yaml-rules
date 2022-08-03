package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;

public interface Action {

    void execute(Drools drools);
}
