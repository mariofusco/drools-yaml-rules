package org.drools.yaml.api.context;

public interface HasRulesExecutorContainer {

    default void register(RulesExecutor rulesExecutor) {
        throw new RuntimeException("Unexpected invocation");
    }

    default void dispose(RulesExecutor rulesExecutor) {
        throw new RuntimeException("Unexpected invocation");
    }

    boolean hasRulesExecutor(long id);


    RulesExecutor getRulesExecutor(long id);




}
