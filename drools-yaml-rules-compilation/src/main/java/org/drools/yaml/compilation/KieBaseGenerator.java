package org.drools.yaml.compilation;

import org.drools.model.Prototype;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.yaml.api.domain.Rule;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.rulesmodel.PrototypeFactory;
import org.kie.api.KieBase;

import static org.drools.model.DSL.execute;
import static org.drools.model.PatternDSL.rule;

public class KieBaseGenerator {

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";

    private int counter = 0;

    private final PrototypeFactory prototypeFactory = new PrototypeFactory();

    private final RulesSet rulesSet;

    public KieBaseGenerator(RulesSet rulesSet) {
        this.rulesSet = rulesSet;
    }

    public KieBase build(long kieBaseHolderId) {
        ModelImpl model = new ModelImpl();
        rulesSet.getRules().stream().map(rule -> toExecModelRule(rule, kieBaseHolderId)).forEach(model::addRule);
        return KieBaseBuilder.createKieBaseFromModel(model);
    }

    private org.drools.model.Rule toExecModelRule(Rule rule, long kieBaseHolderId) {
        String ruleName = rule.getName();
        if (ruleName == null) {
            ruleName = "r_" + counter++;
        }

        RuleGenerationContextImpl ruleContext = new RuleGenerationContextImpl(prototypeFactory);
        var pattern = rule.getCondition().toPattern(ruleContext);
        var consequence = execute(drools -> rule.getAction().execute(kieBaseHolderId, drools));

        return rule(ruleName).build(pattern, consequence);
    }

    public Prototype getPrototype() {
        return getPrototype(PROTOTYPE_NAME);
    }

    private Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }
}
