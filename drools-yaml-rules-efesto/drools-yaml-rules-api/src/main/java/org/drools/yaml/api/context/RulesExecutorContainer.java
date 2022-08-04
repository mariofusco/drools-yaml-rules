package org.drools.yaml.api.context;

import java.util.HashMap;
import java.util.Map;

public enum RulesExecutorContainer {

    INSTANCE;

    private Map<Long, RulesExecutor> rulesExecutors = new HashMap<>();

    public void register(RulesExecutor rulesExecutor) {
        rulesExecutors.put(rulesExecutor.getId(), rulesExecutor);
    }

    public void dispose(RulesExecutor rulesExecutor) {
        rulesExecutors.remove(rulesExecutor.getId());
    }

    public RulesExecutor get(Long id) {
        return rulesExecutors.get(id);
    }
}
