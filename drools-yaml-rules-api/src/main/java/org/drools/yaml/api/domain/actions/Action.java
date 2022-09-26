package org.drools.yaml.api.domain.actions;

import org.drools.model.Drools;
import org.drools.yaml.api.KieSessionHolder;
import org.drools.yaml.api.KieSessionHolderContainer;

public interface Action {
    void execute(long kieBaseHolderId, Drools drools);

    default KieSessionHolder kieSessionHolder(long kieBaseHolderId) {
        return KieSessionHolderContainer.INSTANCE.get(kieBaseHolderId);
    }
}
