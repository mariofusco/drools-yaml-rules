package org.drools.yaml.compilation.jpy;

import org.drools.yaml.compilation.RulesCompiler;

public class AstRulesCompilation {


    public long createRuleset(String rulesetString) {
        RulesCompiler compiler = RulesCompiler.createFromJson(rulesetString);
        return compiler.getId();
    }

}