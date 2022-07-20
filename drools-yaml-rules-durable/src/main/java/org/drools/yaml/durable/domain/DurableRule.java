package org.drools.yaml.durable.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.yaml.core.domain.Rule;
import org.drools.yaml.core.domain.conditions.Condition;

public class DurableRule {
    private List<Map<String,?>> all;
    private List<Map<String,?>> any;
    private String run;

    public List<Map<String,?>> getAll() {
        return all;
    }

    public void setAll(List<Map<String,?>> all) {
        this.all = all;
    }

    public List<Map<String,?>> getAny() {
        return any;
    }

    public void setAny(List<Map<String,?>> any) {
        this.any = any;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public Rule toRule() {
        boolean isAny = any != null;
        Condition condition = toCondition(isAny, isAny ? any : all);

        Rule rule = new Rule();
        rule.setCondition(condition);
        if (run != null) {
            rule.setGenericAction(new RunAction(run));
        }
        return rule;
    }

    private Condition toCondition(boolean isAny, List<Map<String, ?>> conditionMap) {
        List<Condition> conditions = new ArrayList<>();

        for (Map<String,?> map : conditionMap) {
            for (Map.Entry<String,?> entry : map.entrySet()) {
                if ( entry.getValue() instanceof Map ) {
                    conditions.addAll( mapEntryToConditions(entry) );
                } else if ( entry.getValue() instanceof List ) {
                    conditions.add( listEntryToCondition(entry) );
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }

        if (conditions.size() == 1) {
            return conditions.get(0);
        }

        Condition condition = new Condition();
        if (isAny) {
            condition.setAny(conditions);
        } else {
            condition.setAll(conditions);
        }
        return condition;
    }

    private Condition listEntryToCondition(Map.Entry<String, ?> entry) {
        boolean nestedConditionIsAny;
        if (entry.getKey().equals("all") || entry.getKey().endsWith("$all")) {
            nestedConditionIsAny = false;
        } else if (entry.getKey().equals("any") || entry.getKey().endsWith("$any")) {
            nestedConditionIsAny = true;
        } else {
            throw new UnsupportedOperationException();
        }
        return toCondition(nestedConditionIsAny, (List<Map<String, ?>>) entry.getValue());
    }

    private List<Condition> mapEntryToConditions(Map.Entry<String, ?> conditionEntry) {
        String binding = conditionEntry.getKey();
        Map<String,?> value = (Map) conditionEntry.getValue();
        return value.entrySet().stream().map(e -> mapEntryToCondition(binding, e)).collect(Collectors.toList());
    }

    private Condition mapEntryToCondition(String binding, Map.Entry<String, ?> entry) {
        Object value = entry.getValue();

        if (value instanceof Map) {
            return mapValueToCondition(binding, entry.getKey(), (Map<String, ?>) value);
        }

        if (value instanceof List) {
            return Condition.combineConditions( decodeConditionType(entry.getKey()),
                    ((List<?>) value).stream().map(Map.class::cast)
                            .map( m -> mapValueToCondition(binding, m)).collect(Collectors.toList()) );
        }

        return new Condition(entry.getKey() + " == " + toRightValue(entry.getValue()), binding);
    }

    private Condition.Type decodeConditionType(String type) {
        if (type.equals("$and")) {
            return Condition.Type.ALL;
        }
        if (type.equals("$or")) {
            return Condition.Type.ANY;
        }
        throw new UnsupportedOperationException();
    }

    private Condition mapValueToCondition(String binding, Map<String, ?> value) {
        return mapValueToCondition(binding, null, value);
    }

    private Condition mapValueToCondition(String binding, String key, Map<String, ?> value) {
        if (value.size() != 1) {
            throw new UnsupportedOperationException();
        }
        Map.Entry<String, ?> e = value.entrySet().iterator().next();

        if ( key != null && isOperator(key) ) {
            return createOperatorCondition(binding, e.getKey(), key, toRightValue(e.getValue()));
        }

        String leftValue = key != null ? (key + "." + e.getKey()) : e.getKey();
        return new Condition(leftValue + " == " + toRightValue(e.getValue()), binding);
    }

    private Condition createOperatorCondition(String binding, String leftValue, String operator, String rightValue) {
        String decodedOp;
        switch (operator) {
            case "$neq":
                return new Condition(
                        new Condition(leftValue + " != null", binding),
                        new Condition(leftValue + " != " + rightValue, binding));
            case "$ex":
                return new Condition(leftValue + " != null", binding);
            case "$nex":
                return new Condition(leftValue + " == null", binding);
            case "$eq":
                decodedOp = "==";
                break;
            case "$lt":
                decodedOp = "<";
                break;
            case "$gt":
                decodedOp = ">";
                break;
            case "$lte":
                decodedOp = "<=";
                break;
            case "$gte":
                decodedOp = ">=";
                break;
            default:
                throw new UnsupportedOperationException("Unrecongnized operator " + operator);
        }

        return new Condition(leftValue + " " + decodedOp + " " + rightValue, binding);
    }

    private String toRightValue(Object value) {
        return value instanceof String ? "\"" + value + "\"" : "" + value;
    }

    private boolean isOperator(String operator) {
        return operator.startsWith("$");
    }
}
