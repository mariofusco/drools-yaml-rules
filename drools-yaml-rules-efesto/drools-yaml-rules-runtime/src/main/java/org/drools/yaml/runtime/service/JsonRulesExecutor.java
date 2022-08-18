package org.drools.yaml.runtime.service;

import java.util.Optional;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.executeFacts;

public class JsonRulesExecutor implements KieRuntimeService<String, Integer, EfestoInputJson, EfestoOutputInteger, EfestoRuntimeContext> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputJson) &&
                (context instanceof HasRulesExecutorContainer) &&
                ((HasRulesExecutorContainer) context).hasRulesExecutor(((EfestoInputJson) toEvaluate).getId());
        // getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    @Override
    public Optional<EfestoOutputInteger> evaluateInput(EfestoInputJson toEvaluate, EfestoRuntimeContext context) {
        HasRulesExecutorContainer hasRuleExecutor = (HasRulesExecutorContainer) context;
        return Optional.ofNullable(executeFacts(toEvaluate, hasRuleExecutor.getRulesExecutor((toEvaluate).getId())));
    }
}
