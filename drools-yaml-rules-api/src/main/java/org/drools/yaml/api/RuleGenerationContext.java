package org.drools.yaml.api;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeVariable;
import org.drools.yaml.api.rulesmodel.PrototypeFactory;

import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.variable;

public class RuleGenerationContext {
    private final PrototypeFactory prototypeFactory;

    private final RuleNotation.RuleConfigurationOption[] options;

    private final StackedContext<String, PrototypeDSL.PrototypePatternDef> patterns = new StackedContext<>();

    private int bindingsCounter = 0;

    RuleGenerationContext(PrototypeFactory prototypeFactory, RuleNotation.RuleConfigurationOption[] options) {
        this.prototypeFactory = prototypeFactory;
        this.options = options;
    }

    public PrototypeDSL.PrototypePatternDef getOrCreatePattern(String binding, String name) {
        return patterns.computeIfAbsent(binding, b -> protoPattern(variable(prototypeFactory.getPrototype(name), b)));
    }

    public PrototypeVariable getPatternVariable(String binding) {
        PrototypeDSL.PrototypePatternDef patternDef = patterns.get(binding);
        return patternDef != null ? (PrototypeVariable) patternDef.getFirstVariable() : null;
    }

    public boolean isExistingBoundVariable(String binding) {
        return patterns.get(binding) != null;
    }

    public PrototypeDSL.PrototypePatternDef getBoundPattern(String binding) {
        return patterns.get(binding);
    }

    public void pushContext() {
        patterns.pushContext();
    }

    public void popContext() {
        patterns.popContext();
    }

    public String generateBinding() {
        String binding = bindingsCounter == 0 ? "m" : "m_" + bindingsCounter;
        bindingsCounter++;
        return binding;
    }

    public boolean hasOption(RuleNotation.RuleConfigurationOption option) {
        if (options == null) {
            return false;
        }
        for (RuleNotation.RuleConfigurationOption op : options) {
            if (op == option) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGeneratedBinding(String binding) {
        return binding.equals("m") || binding.startsWith("m_");
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
