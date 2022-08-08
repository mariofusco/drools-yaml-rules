package org.drools.yaml.compilation;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesCompilationContext extends EfestoCompilationContextImpl implements HasRulesExecutorContainer {

    private static final RulesExecutorContainer rulesExecutorContainer = RulesExecutorContainer.INSTANCE;

    // TODO to refactor
    private long lastCreatedId;

    public static RulesCompilationContext create() {
        return new RulesCompilationContext(
                new KieMemoryCompiler.MemoryCompilerClassLoader(
                        Thread.currentThread().getContextClassLoader()));
    }

    public RulesCompilationContext(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
    }

    @Override
    public void register(RulesExecutor rulesExecutor) {
        rulesExecutorContainer.register(rulesExecutor);
        lastCreatedId = rulesExecutor.getId();
    }

    @Override
    public boolean hasRulesExecutor(long id) {
        return rulesExecutorContainer.get(id) != null;
    }

    @Override
    public RulesExecutor getRulesExecutor(long id) {
        return rulesExecutorContainer.get(id);
    }

    // TODO to refactor
    public long ruleExecutorId() {
        return lastCreatedId;
    }


}
