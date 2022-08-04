package org.drools.yaml.runtime.service;

import org.drools.yaml.api.context.HasRuleExecutor;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputRuleMatches;
import org.kie.api.runtime.rule.Match;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.process;

public class FactMapRulesProcessor implements KieRuntimeService<Map<String, Object>, List<Match>, EfestoInputMap,
        EfestoOutputRuleMatches> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputMap) &&
                ((EfestoInputMap)toEvaluate).getOperation().equals("process") &&
                (context instanceof HasRuleExecutor) &&
                ((HasRuleExecutor)context).getRulesExecutor() != null;
                // getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    @Override
    public Optional<EfestoOutputRuleMatches> evaluateInput(EfestoInputMap toEvaluate, EfestoRuntimeContext context) {
        HasRuleExecutor hasRuleExecutor = (HasRuleExecutor) context;
        return Optional.ofNullable(process(toEvaluate, hasRuleExecutor.getRulesExecutor()));
    }
}
