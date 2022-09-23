package org.drools.yaml.compilation.jpy;


import org.drools.yaml.api.RulesExecutor;


public class AstRulesCompilation {


    public long createRuleset(String rulesetString) {
        RulesExecutor executor = RulesExecutor.createFromJson(rulesetString);
        return executor.getId();
    }

}