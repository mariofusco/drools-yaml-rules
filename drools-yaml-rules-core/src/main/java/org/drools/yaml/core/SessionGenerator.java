package org.drools.yaml.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.drools.model.Prototype;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeVariable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.yaml.core.domain.Rule;
import org.drools.yaml.core.domain.RulesSet;
import org.drools.yaml.core.rulesmodel.PrototypeFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.drools.model.DSL.execute;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.variable;

public class SessionGenerator {

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";

    private static int counter = 0;

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
            ruleName = "R" + counter++;
        }

        RuleContext ruleContext = new RuleContext(prototypeFactory);
        var pattern = rule.getCondition().toPattern(ruleContext);
        var consequence = execute(drools -> rule.getAction().execute(rulesExecutor, drools));

        return rule( ruleName ).build(pattern, consequence);
    }

    public Prototype getPrototype() {
        return getPrototype(PROTOTYPE_NAME);
    }

    private Prototype getPrototype(String name) {
        return prototypeFactory.getPrototype(name);
    }

    public static class RuleContext {
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
