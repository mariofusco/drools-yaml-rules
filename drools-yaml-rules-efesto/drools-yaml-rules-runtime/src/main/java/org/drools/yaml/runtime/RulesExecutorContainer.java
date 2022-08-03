package org.drools.yaml.runtime;

import java.util.HashMap;
import java.util.Map;

import org.drools.yaml.runtime.service.JsonRulesExecutor;

public enum RulesExecutorContainer {

    INSTANCE;

    private Map<Long, JsonRulesExecutor> rulesExecutors = new HashMap<>();

    public void register(JsonRulesExecutor rulesExecutor) {
        rulesExecutors.put(rulesExecutor.getId(), rulesExecutor);
    }

    public void dispose(JsonRulesExecutor rulesExecutor) {
        rulesExecutors.remove(rulesExecutor.getId());
    }

    public JsonRulesExecutor get(Long id) {
        return rulesExecutors.get(id);
    }
}
