package org.drools.yaml.api.rulesmodel;

import java.util.HashMap;
import java.util.Map;

import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;

public class PrototypeFactory {

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";

    private final Map<String, Prototype> prototypes = new HashMap<>();

    public Prototype getPrototype(String name) {
        return prototypes.computeIfAbsent(name, PrototypeDSL::prototype);
    }

    public Prototype getPrototype() {
        return getPrototype(PROTOTYPE_NAME);
    }
}
