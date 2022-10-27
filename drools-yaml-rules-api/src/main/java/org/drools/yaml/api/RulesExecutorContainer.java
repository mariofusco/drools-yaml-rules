package org.drools.yaml.api;

import java.util.HashMap;
import java.util.Map;

public enum RulesExecutorContainer {

    INSTANCE;

    private Map<Long, RulesExecutor> rulesExecutors = new HashMap<>();

    public RulesExecutor register(RulesExecutor rulesExecutor) {
        rulesExecutors.put(rulesExecutor.getId(), rulesExecutor);
        return rulesExecutor;
    }

    public void dispose(RulesExecutor rulesExecutor) {
        rulesExecutors.remove(rulesExecutor.getId());
    }

    public RulesExecutor get(Long id) {
        return rulesExecutors.get(id);
    }
}
