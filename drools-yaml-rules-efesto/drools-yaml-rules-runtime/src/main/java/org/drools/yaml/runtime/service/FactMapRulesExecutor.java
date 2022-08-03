package org.drools.yaml.runtime.service;

import java.util.Map;
import java.util.Optional;

import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.execute;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;

public class FactMapRulesExecutor implements KieRuntimeService<Map<String, Object>, Integer, EfestoInputMap,
        EfestoOutputInteger,
        EfestoRuntimeContext> {

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (toEvaluate instanceof EfestoInputMap) && getGeneratedExecutableResource(toEvaluate.getFRI(), "drl").isPresent();
    }

    @Override
    public Optional<EfestoOutputInteger> evaluateInput(EfestoInputMap toEvaluate, EfestoRuntimeContext context) {
        return Optional.ofNullable(execute(toEvaluate));
    }
}
