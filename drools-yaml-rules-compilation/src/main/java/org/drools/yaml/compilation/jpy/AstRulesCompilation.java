package org.drools.yaml.compilation.jpy;


import org.drools.yaml.api.RulesExecutor;
import org.drools.yaml.api.RulesExecutorFactory;


public class AstRulesCompilation {


    public long createRuleset(String rulesetString) {
        RulesExecutor executor = RulesExecutorFactory.createFromJson(rulesetString);
        return executor.getId();
    }

}