package org.drools.yaml.api;

import org.drools.model.Prototype;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.yaml.api.RuleGenerationContext;
import org.drools.yaml.api.domain.Rule;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.rulesmodel.PrototypeFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.drools.model.DSL.execute;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.variable;

public class SessionGenerator {

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";

    private int counter = 0;

    private final PrototypeFactory prototypeFactory = new PrototypeFactory();

    private final RulesSet rulesSet;

    public SessionGenerator(RulesSet rulesSet) {
        this.rulesSet = rulesSet;
    }

    public KieSession build(RulesExecutor rulesExecutor) {
        ModelImpl model = new ModelImpl();
        rulesSet.getRules().stream().map(rule -> toExecModelRule(rule, rulesExecutor)).forEach(model::addRule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        return kieBase.newKieSession();
    }

    private org.drools.model.Rule toExecModelRule(Rule rule, RulesExecutor rulesExecutor) {
        String ruleName = rule.getName();
        if (ruleName == null) {
            ruleName = "r_" + counter++;
        }

        RuleGenerationContext ruleContext = new RuleGenerationContext(prototypeFactory, rulesSet.getOptions());
        ViewItem pattern = rule.getCondition().toPattern(ruleContext);
        var consequence = execute(drools -> rule.getAction().execute(rulesExecutor, drools));

        return rule( ruleName ).build(pattern, consequence);
    }

    public Prototype getPrototype() {
        return getPrototype(PROTOTYPE_NAME);
    }

    private Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }
}
