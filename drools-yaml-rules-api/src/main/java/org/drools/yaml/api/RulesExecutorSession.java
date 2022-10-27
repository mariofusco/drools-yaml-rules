package org.drools.yaml.api;

import java.util.function.Supplier;

import org.drools.yaml.api.rulesmodel.PrototypeFactory;
import org.kie.api.runtime.KieSession;

public class RulesExecutorSession {

    private final PrototypeFactory prototypeFactory;

    private final KieSession kieSession;

    private final RulesExecutorHolder rulesExecutorHolder;

    public RulesExecutorSession(PrototypeFactory prototypeFactory, KieSession kieSession, RulesExecutorHolder rulesExecutorHolder) {
        this.prototypeFactory = prototypeFactory;
        this.kieSession = kieSession;
        this.rulesExecutorHolder = rulesExecutorHolder;
    }

    public void setRulesExecutor(RulesExecutor rulesExecutor) {
        rulesExecutorHolder.set(rulesExecutor);
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    public PrototypeFactory getPrototypeFactory() {
        return prototypeFactory;
    }

    static class RulesExecutorHolder implements Supplier<RulesExecutor> {
        private RulesExecutor rulesExecutor;

        @Override
        public RulesExecutor get() {
            return rulesExecutor;
        }

        public void set(RulesExecutor rulesExecutor) {
            this.rulesExecutor = rulesExecutor;
        }
    }
}
