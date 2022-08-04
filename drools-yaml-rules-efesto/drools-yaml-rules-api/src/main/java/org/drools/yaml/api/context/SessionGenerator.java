package org.drools.yaml.api.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeVariable;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.Rule;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.domain.conditions.Condition;
import org.drools.yaml.compilation.rulesmodel.ParsedCondition;
import org.drools.yaml.compilation.rulesmodel.PrototypeFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.drools.model.DSL.execute;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.variable;

public enum SessionGenerator {

    INSTANCE;

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";

    private static int counter = 0;

    SessionGenerator() {
    }

    public KieSession build(RulesSet rulesSet, RulesExecutor rulesExecutor) {
        ModelImpl model = new ModelImpl();
        rulesSet.getHost_rules().stream().map(rule -> toExecModelRule(rule, rulesExecutor)).forEach(model::addRule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        return kieBase.newKieSession();
    }

    private org.drools.model.Rule toExecModelRule(Rule rule, RulesExecutor rulesExecutor) {
        String ruleName = rule.getName();
        if (ruleName == null) {
            ruleName = "R" + counter++;
        }

        RuleContext ruleContext = new RuleContext(rulesExecutor.getPrototypeFactory());
        var pattern = condition2Pattern(ruleContext, rule.getCondition());
        var consequence = execute(drools -> rule.getAction().execute(rulesExecutor, drools));

        return rule( ruleName ).build(pattern, consequence);
    }

    private ViewItem condition2Pattern(RuleContext ruleContext, Condition condition) {
        switch (condition.getType()) {
            case ANY:
                return new CombinedExprViewItem(org.drools.model.Condition.Type.OR, condition.getAny().stream().map(subC -> scopingCondition2Pattern(ruleContext, subC)).toArray(ViewItem[]::new));
            case ALL:
                return new CombinedExprViewItem(org.drools.model.Condition.Type.AND, condition.getAll().stream().map(subC -> condition2Pattern(ruleContext, subC)).toArray(ViewItem[]::new));
            case SINGLE:
                return singleCondition2Pattern(ruleContext, condition);
        }
        throw new UnsupportedOperationException();
    }

    private ViewItem scopingCondition2Pattern(RuleContext ruleContext, Condition condition) {
        ruleContext.pushContext();
        ViewItem pattern = condition2Pattern(ruleContext, condition);
        ruleContext.popContext();
        return pattern;
    }

    private ViewItem singleCondition2Pattern(RuleContext ruleContext, Condition condition) {
        ParsedCondition parsedCondition = condition.parse();
        var pattern = ruleContext.getOrCreatePattern(condition.getPatternBinding(), PROTOTYPE_NAME);
        if (condition.beta()) {
            pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), ruleContext.getPatternVariable(condition.otherBinding()), parsedCondition.getRight());
        } else {
            pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), parsedCondition.getRight());
        }
        return pattern;
    }

//    public Prototype getPrototype() {
//        return getPrototype(PROTOTYPE_NAME);
//    }
//
//    private Prototype getPrototype(String name) {
//        return prototypeFactory.getPrototype(name);
//    }

    private static class RuleContext {
        private final PrototypeFactory prototypeFactory;

        private final StackedContext<String, PrototypeDSL.PrototypePatternDef> patterns = new StackedContext<>();

        private RuleContext(PrototypeFactory prototypeFactory) {
            this.prototypeFactory = prototypeFactory;
        }

        public PrototypeDSL.PrototypePatternDef getOrCreatePattern(String binding, String name) {
            return patterns.computeIfAbsent(binding, b -> protoPattern( variable(prototypeFactory.getPrototype(name), b)));
        }

        public PrototypeVariable getPatternVariable(String binding) {
            return (PrototypeVariable) patterns.get(binding).getFirstVariable();
        }

        public void pushContext() {
            patterns.pushContext();
        }

        public void popContext() {
            patterns.popContext();
        }
    }

    private static class StackedContext<K, V> {
        private final Deque<Map<K, V>> stack = new ArrayDeque<>();

        public StackedContext() {
            pushContext();
        }

        public void pushContext() {
            stack.addFirst(new HashMap<>());
        }

        public void popContext() {
            stack.removeFirst();
        }

        public V get(K key) {
            for (Map<K,V> map : stack) {
                V value = map.get(key);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }

        public void put(K key, V value) {
            stack.getFirst().put(key, value);
        }

        public V computeIfAbsent(K key, Function<K, V> f) {
            V value = get(key);
            if (value != null) {
                return value;
            }
            value = f.apply(key);
            put(key, value);
            return value;
        }
    }
}
