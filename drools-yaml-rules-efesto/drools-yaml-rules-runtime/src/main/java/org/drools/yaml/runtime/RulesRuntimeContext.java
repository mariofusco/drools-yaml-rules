package org.drools.yaml.runtime;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.api.context.RulesExecutor;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesRuntimeContext extends EfestoRuntimeContextImpl implements HasRuleExecutor {
    private RulesExecutor rulesExecutor;

    public RulesRuntimeContext(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
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
