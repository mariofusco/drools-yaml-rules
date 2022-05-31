package org.drools.yaml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.yaml.domain.rulesset.Rule;
import org.drools.yaml.domain.rulesset.RulesSet;
import org.drools.yaml.domain.rulesset.conditions.Condition;
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

    private final RulesSet rulesSet;

    public SessionGenerator(RulesSet rulesSet) {
        this.rulesSet = rulesSet;
    }

    public KieSession build(RulesExecutor rulesExecutor) {
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

        RuleContext ruleContext = new RuleContext(prototypeFactory);
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
                return singleCondition2Pattern(ruleContext, condition.getSingle());
        }
        throw new UnsupportedOperationException();
    }

    private ViewItem scopingCondition2Pattern(RuleContext ruleContext, Condition condition) {
        ruleContext.pushContext();
        ViewItem pattern = condition2Pattern(ruleContext, condition);
        ruleContext.popContext();
        return pattern;
    }

    private ViewItem singleCondition2Pattern(RuleContext ruleContext, String condition) {
        ParsedCondition parsedCondition = parse(condition);
        var pattern = ruleContext.getOrCreatePattern(parsedCondition.getLeftVar());
        pattern.expr(parsedCondition.getLeftField(), parsedCondition.getOperator(), parsedCondition.getRight());
        return pattern;
    }

    public Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }

    private static class RuleContext {
        private final PrototypeFactory prototypeFactory;

        private final StackedContext<String, PrototypeDSL.PrototypePatternDef> patterns = new StackedContext<>();

        private RuleContext(PrototypeFactory prototypeFactory) {
            this.prototypeFactory = prototypeFactory;
        }

        public PrototypeDSL.PrototypePatternDef getOrCreatePattern(String name) {
            return patterns.computeIfAbsent(name, n -> protoPattern( variable(prototypeFactory.getPrototype(n), n)));
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
