package org.drools.yaml.durable.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.model.Index;
import org.drools.model.PrototypeExpression;
import org.drools.yaml.core.domain.Rule;
import org.drools.yaml.core.domain.conditions.BetaExpressionCondition;
import org.drools.yaml.core.domain.conditions.SimpleCondition;
import org.drools.yaml.core.domain.conditions.ExpressionCondition;

import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;

public class DurableRule {
    private Set<String> existingBindings = new HashSet<>();

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
        SimpleCondition condition = toCondition(isAny, isAny ? any : all);

        Rule rule = new Rule();
        rule.setCondition(condition);
        if (run != null) {
            rule.setGenericAction(new RunAction(run));
        }
        return rule;
    }

    private SimpleCondition toCondition(boolean isAny, List<Map<String, ?>> conditionMap) {
        List<SimpleCondition> conditions = new ArrayList<>();

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

        SimpleCondition condition = new SimpleCondition();
        if (isAny) {
            condition.setAny(conditions);
        } else {
            condition.setAll(conditions);
        }
        return condition;
    }

    private SimpleCondition listEntryToCondition(Map.Entry<String, ?> entry) {
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

    private List<SimpleCondition> mapEntryToConditions(Map.Entry<String, ?> conditionEntry) {
        String binding = conditionEntry.getKey();
        Map<String,?> value = (Map) conditionEntry.getValue();
        List<SimpleCondition> conditions = value.entrySet().stream().map(e -> mapEntryToCondition(binding, e)).collect(Collectors.toList());
        existingBindings.add(binding);
        return conditions;
    }

    private SimpleCondition mapEntryToCondition(String binding, Map.Entry<String, ?> entry) {
        Object value = entry.getValue();

        if (value instanceof Map) {
            return mapValueToCondition(binding, entry.getKey(), (Map<String, ?>) value);
        }

        if (value instanceof List) {
            return SimpleCondition.combineConditions( decodeConditionType(entry.getKey()),
                    ((List<?>) value).stream().map(Map.class::cast)
                            .map( m -> mapValueToCondition(binding, m)).collect(Collectors.toList()) );
        }

        return new SimpleCondition(entry.getKey() + " == " + toOperand(entry.getValue()), binding);
    }

    private SimpleCondition.Type decodeConditionType(String type) {
        if (type.equals("$and")) {
            return SimpleCondition.Type.ALL;
        }
        if (type.equals("$or")) {
            return SimpleCondition.Type.ANY;
        }
        throw new UnsupportedOperationException();
    }

    private SimpleCondition mapValueToCondition(String binding, Map<String, ?> value) {
        return mapValueToCondition(binding, null, value);
    }

    private SimpleCondition mapValueToCondition(String binding, String key, Map<String, ?> value) {
        if (value.size() != 1) {
            throw new UnsupportedOperationException();
        }
        Map.Entry<String, ?> e = value.entrySet().iterator().next();

        if ( key != null && isOperator(key) ) {
            return createOperatorCondition(binding, e.getKey(), key, e.getKey(), e.getValue());
        }

        return createCondition(binding, toLeftValue(key, e.getKey()), "==", e.getKey(), e.getValue());
    }

    private String toLeftValue(String leftKey, String entryKey) {
        if (leftKey == null) {
            return entryKey;
        }
        if (isOperator(entryKey) || existingBindings.contains(entryKey)) {
            return leftKey;
        }
        return leftKey + "." + entryKey;
    }

    private SimpleCondition createOperatorCondition(String binding, String leftValue, String operator, String rightKey, Object rightValue) {
        String decodedOp;
        switch (operator) {
            case "$neq":
                return new SimpleCondition(
                        new SimpleCondition(leftValue + " != null", binding),
                        new SimpleCondition(leftValue + " != " + toOperand(rightValue), binding));
            case "$ex":
                return new ExpressionCondition(binding, prototypeField(leftValue), Index.ConstraintType.EXISTS_PROTOTYPE_FIELD, fixedValue(true));
            case "$nex":
                return new ExpressionCondition(binding, prototypeField(leftValue), Index.ConstraintType.EXISTS_PROTOTYPE_FIELD, fixedValue(false));
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
                throw new UnsupportedOperationException("Unrecognized operator " + operator);
        }

        return createCondition(binding, leftValue, decodedOp, rightKey, rightValue);
    }

    private SimpleCondition createCondition(String binding, String leftValue, String decodedOp, String rightKey, Object rightValue) {
        if (isOperator(rightKey)) {
            Object l = ((Map) rightValue).get("$l");
            Object r = ((Map) rightValue).get("$r");

            String leftBinding = getBindingFromMap(l);
            if (leftBinding != null) {
                PrototypeExpression rightExpression = prototypeField(((Map) l).get(leftBinding).toString()).composeWith(decodeBinaryOperator(rightKey), toPrototypeExpression(r));
                return new BetaExpressionCondition(binding, prototypeField(leftValue), decodeConstraintType(decodedOp), leftBinding, rightExpression);
            }
            String rightBinding = getBindingFromMap(r);
            if (rightBinding != null) {
                PrototypeExpression rightExpression = toPrototypeExpression(l).composeWith(decodeBinaryOperator(rightKey), prototypeField(((Map) r).get(rightBinding).toString()));
                return new BetaExpressionCondition(binding, prototypeField(leftValue), decodeConstraintType(decodedOp), rightBinding, rightExpression);
            }

            PrototypeExpression rightExpression = toPrototypeExpression(l).composeWith(decodeBinaryOperator(rightKey), toPrototypeExpression(r));
            return new ExpressionCondition(binding, prototypeField(leftValue), decodeConstraintType(decodedOp), rightExpression);
        }

        if (existingBindings.contains(rightKey)) {
            if (rightValue instanceof String) {
                return new BetaExpressionCondition(binding, prototypeField(leftValue), decodeConstraintType(decodedOp), rightKey, prototypeField((String) rightValue));
            }
            throw new UnsupportedOperationException();
        }

        return new SimpleCondition(leftValue + " " + decodedOp + " " + toOperand(rightValue), binding);
    }

    private PrototypeExpression toPrototypeExpression(Object value) {
        if (value instanceof Map) {
            Map map = (Map) value;
            assert(map.size() == 1);
            String fieldName = (String) map.get("$m");
            assert(fieldName != null);
            return prototypeField(fieldName);
        }
        return fixedValue(value);
    }

    private String getBindingFromMap(Object value) {
        if (value instanceof Map) {
            Map map = (Map) value;
            assert(map.size() == 1);
            String key = (String) map.keySet().iterator().next();
            if (existingBindings.contains(key)) {
                return key;
            }
        }
        return null;
    }

    private String toOperand(Object value) {
        if (value instanceof Map) {
            if (((Map) value).size() != 1) {
                throw new UnsupportedOperationException();
            }
            return "" + ((Map) value).values().iterator().next();
        }
        return value instanceof String ? "\"" + value + "\"" : "" + value;
    }

    private Index.ConstraintType decodeConstraintType(String operator) {
        switch (operator) {
            case "==":
                return Index.ConstraintType.EQUAL;
            case "!=":
                return Index.ConstraintType.NOT_EQUAL;
            case ">=":
                return Index.ConstraintType.GREATER_OR_EQUAL;
            case "<=":
                return Index.ConstraintType.LESS_OR_EQUAL;
            case ">":
                return Index.ConstraintType.GREATER_THAN;
            case "<":
                return Index.ConstraintType.LESS_THAN;
        }
        throw new UnsupportedOperationException("Unrecognized constraint type " + operator);
    }

    private PrototypeExpression.BinaryOperation.Operator decodeBinaryOperator(String operator) {
        switch (operator) {
            case "$add":
                return PrototypeExpression.BinaryOperation.Operator.ADD;
            case "$sub":
                return PrototypeExpression.BinaryOperation.Operator.SUB;
            case "$mul":
                return PrototypeExpression.BinaryOperation.Operator.MUL;
            case "$div":
                return PrototypeExpression.BinaryOperation.Operator.DIV;
        }
        throw new UnsupportedOperationException("Unrecognized binary operator " + operator);
    }

    private boolean isOperator(String operator) {
        return operator != null && operator.startsWith("$");
    }
}
