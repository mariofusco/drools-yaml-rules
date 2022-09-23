package org.drools.yaml.benchmark;

import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.durable.DurableNotation;

public class DroolsDurableBenchmarkMain {
    public static void main(String[] args) {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"event.i\": 1}}}]}}}";
        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
        rulesExecutor.processEvents("{ \"event\": { \"i\": \"Done\" } }");
    }
}
