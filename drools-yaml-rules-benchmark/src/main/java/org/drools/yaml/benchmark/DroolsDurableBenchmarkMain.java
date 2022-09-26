package org.drools.yaml.benchmark;

import org.drools.yaml.api.KieSessionHolder;
import org.drools.yaml.compilation.RulesCompiler;
import org.drools.yaml.durable.DurableNotation;

import static org.drools.yaml.runtime.KieSessionHolderUtils.kieSessionHolder;

public class DroolsDurableBenchmarkMain {
    public static void main(String[] args) {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"event.i\": 1}}}]}}}";
        RulesCompiler rulesCompiler = RulesCompiler.createFromJson(DurableNotation.INSTANCE, jsonRule);
        KieSessionHolder rulesExecutor = kieSessionHolder(rulesCompiler.getId());
        rulesExecutor.processEvents("{ \"event\": { \"i\": \"Done\" } }");
    }
}
