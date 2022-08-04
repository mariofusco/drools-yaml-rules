package org.drools.yaml.runtime.service;

import java.util.Optional;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.execute;

public class JsonRulesExecutor implements KieRuntimeService<String, Integer, EfestoInputJson, EfestoOutputInteger> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputJson) &&
                (context instanceof HasRuleExecutor) &&
                ((HasRuleExecutor) context).getRulesExecutor() != null;
        // getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    @Override
    public Optional<EfestoOutputInteger> evaluateInput(EfestoInputJson toEvaluate, EfestoRuntimeContext context) {
        HasRuleExecutor hasRuleExecutor = (HasRuleExecutor) context;
        return Optional.ofNullable(execute(toEvaluate, hasRuleExecutor.getRulesExecutor()));
    }
}
