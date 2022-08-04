package org.drools.yaml.runtime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputRuleMatches;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.process;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class FactMapRulesProcessor implements KieRuntimeService<Map<String, Object>, List<RuleMatch>, EfestoInputMap,
        EfestoOutputRuleMatches> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputMap) && getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    @Override
    public Optional<EfestoOutputRuleMatches> evaluateInput(EfestoInputMap toEvaluate, EfestoRuntimeContext context) {
        return Optional.ofNullable(process(toEvaluate));
    }
}
