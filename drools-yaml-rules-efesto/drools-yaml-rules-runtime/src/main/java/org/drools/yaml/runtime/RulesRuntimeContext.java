package org.drools.yaml.runtime;

import java.util.Set;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesRuntimeContext implements EfestoRuntimeContext,
                                            HasRulesExecutorContainer {
    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private static final RulesExecutorContainer rulesExecutorContainer = RulesExecutorContainer.INSTANCE;


    public RulesRuntimeContext(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;

        prepareClassLoader();
    }

    private void prepareClassLoader() {
        Set<FRI> friKeySet = friKeySet();
        friKeySet.stream()
                .map(this::getGeneratedClasses)
                .forEach(generatedClasses -> generatedClasses.forEach(memoryCompilerClassLoader::addCodeIfAbsent));
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return memoryCompilerClassLoader.loadClass(className);
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
