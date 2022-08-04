package org.drools.yaml.compilation;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.api.context.RulesExecutor;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RulesCompilationContext extends EfestoCompilationContextImpl implements HasRuleExecutor {
    private RulesExecutor rulesExecutor;

    public RulesCompilationContext(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
    }

    @Override
    public void setRulesExecutor(RulesExecutor ruleExecutor) {
        this.rulesExecutor = ruleExecutor;
    }

    @Override
    public RulesExecutor getRulesExecutor() {
        return rulesExecutor;
    }
}
