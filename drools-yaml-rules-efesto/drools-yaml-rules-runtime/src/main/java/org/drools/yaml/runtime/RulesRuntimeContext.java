package org.drools.yaml.runtime;

import java.util.Set;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.api.context.RulesExecutor;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesRuntimeContext implements EfestoRuntimeContext, HasRuleExecutor {
    private RulesExecutor rulesExecutor;
    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;


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
    public void setRulesExecutor(RulesExecutor rulesExecutor) {
        this.rulesExecutor = rulesExecutor;
    }

    @Override
    public RulesExecutor getRulesExecutor() {
        return rulesExecutor;
    }
}
