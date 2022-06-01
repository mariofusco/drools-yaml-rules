package org.drools.yaml.core.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Binding {

    private static final String GENERATED_BINDING_PREFIX = "Fact#";

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static String generateBinding() {
        return GENERATED_BINDING_PREFIX + COUNTER.getAndIncrement();
    }

    public static boolean isGeneratedBinding(String binding) {
        return binding.startsWith(GENERATED_BINDING_PREFIX);
    }
}
