package org.drools.yaml.runtime;

import org.drools.model.Prototype;
import org.drools.yaml.api.KieBaseHolderContainer;
import org.drools.yaml.api.rulesmodel.PrototypeFactory;
import org.kie.api.runtime.KieSession;

import static org.drools.yaml.api.Constants.PROTOTYPE_NAME;

public class SessionGenerator {

    public KieSession build(long kieSessionHolderId) {
        return KieBaseHolderContainer.INSTANCE.get(kieSessionHolderId).getKieBase().newKieSession();
    }

    private final PrototypeFactory prototypeFactory = new PrototypeFactory();

    public Prototype getPrototype() {
        return getPrototype(PROTOTYPE_NAME);
    }

    private Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }


}
