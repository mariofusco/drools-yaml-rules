package org.drools.yaml.runtime;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesRuntimeContext extends EfestoRuntimeContextImpl implements EfestoRuntimeContext,
                                            HasRulesExecutorContainer {
    private static final RulesExecutorContainer rulesExecutorContainer = RulesExecutorContainer.INSTANCE;


    public static RulesRuntimeContext create() {
        return new RulesRuntimeContext(new KieMemoryCompiler.MemoryCompilerClassLoader(
                Thread.currentThread().getContextClassLoader()));
    }

    public RulesRuntimeContext(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
    }

    @Override
    public void dispose(RulesExecutor rulesExecutor) {
        rulesExecutorContainer.dispose(rulesExecutor);
    }

    @Override
    public boolean hasRulesExecutor(long id) {
        return rulesExecutorContainer.get(id) != null;
    }

    @Override
    public RulesExecutor getRulesExecutor(long id) {
        return rulesExecutorContainer.get(id);
    }
}
