package org.drools.yaml;

import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.yaml.domain.YamlRule;
import org.drools.yaml.domain.YamlRulesSet;
import org.drools.yaml.rulesmodel.ParsedCondition;
import org.drools.yaml.rulesmodel.PrototypeFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.drools.model.DSL.execute;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.variable;
import static org.drools.yaml.rulesmodel.ParsedCondition.parse;

public class SessionGenerator {

    public static final String GLOBAL_MAP_FIELD = "global_map";

    private static int counter = 0;

    private final PrototypeFactory prototypeFactory = new PrototypeFactory();

    private final YamlRulesSet rulesSet;

    public SessionGenerator(YamlRulesSet rulesSet) {
        this.rulesSet = rulesSet;
    }

    public KieSession build(RulesExecutor rulesExecutor) {
        ModelImpl model = new ModelImpl();
        rulesSet.getHost_rules().stream().map(yamlRule -> toExecModelRule(yamlRule, rulesExecutor)).forEach(model::addRule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        return kieBase.newKieSession();
    }

    private Rule toExecModelRule(YamlRule yamlRule, RulesExecutor rulesExecutor) {
        String ruleName = yamlRule.getName();
        if (ruleName == null) {
            ruleName = "R" + counter++;
        }

        var pattern = condition2Pattern(yamlRule.getCondition());
        var consequence = execute(drools -> yamlRule.getAction().execute(rulesExecutor, drools));

        return rule( ruleName ).build(pattern, consequence);
    }

    private PrototypeDSL.PrototypePatternDef condition2Pattern(String condition) {
        ParsedCondition parsedCondition = parse(condition);
        var protoVar = variable(prototypeFactory.getPrototype(parsedCondition.getLeftVar()), parsedCondition.getLeftVar());
        var pattern = protoPattern( protoVar );
        pattern.expr(parsedCondition.getLeftField(), parsedCondition.getOperator(), parsedCondition.getRight());
        return pattern;
    }

    public Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }
}
