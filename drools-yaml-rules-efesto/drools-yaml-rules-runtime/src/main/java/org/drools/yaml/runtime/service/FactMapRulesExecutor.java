package org.drools.yaml.runtime.service;

import java.util.Map;
import java.util.Optional;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.execute;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class FactMapRulesExecutor implements KieRuntimeService<Map<String, Object>, Integer, EfestoInputMap,
        EfestoOutputInteger> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputMap) && ((HasRuleExecutor)context).getRulesExecutor()!=null;
    }

    @Override
    public Optional<EfestoOutputInteger> evaluateInput(EfestoInputMap toEvaluate, EfestoRuntimeContext context) {
        HasRuleExecutor hasRuleExecutor = (HasRuleExecutor) context;
        return Optional.ofNullable(execute(toEvaluate, hasRuleExecutor.getRulesExecutor()));
    }
}
