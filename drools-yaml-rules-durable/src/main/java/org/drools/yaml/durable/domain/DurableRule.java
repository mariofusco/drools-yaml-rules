package org.drools.yaml.durable.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.model.Index;
import org.drools.model.PrototypeExpression;
import org.drools.yaml.core.domain.Rule;
import org.drools.yaml.core.domain.conditions.Condition;
import org.drools.yaml.core.domain.conditions.ExpressionCondition;

import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;

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

        return new Condition(entry.getKey() + " == " + toOperand(entry.getValue()), binding);
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
            return createOperatorCondition(binding, e.getKey(), key, e.getKey(), e.getValue());
        }

        String leftValue = key != null ? (key + (isOperator(e.getKey()) ? "" : ("." + e.getKey()))) : e.getKey();
        return createCondition(binding, leftValue, "==", e.getKey(), e.getValue());
    }

    private Condition createOperatorCondition(String binding, String leftValue, String operator, String rightKey, Object rightValue) {
        String decodedOp;
        switch (operator) {
            case "$neq":
                return new Condition(
                        new Condition(leftValue + " != null", binding),
                        new Condition(leftValue + " != " + toOperand(rightValue), binding));
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
                throw new UnsupportedOperationException("Unrecognized operator " + operator);
        }

        return createCondition(binding, leftValue, decodedOp, rightKey, rightValue);
    }

    private Condition createCondition(String binding, String leftValue, String decodedOp, String rightKey, Object rightValue) {
        if (isOperator(rightKey)) {
            Object l = ((Map) rightValue).get("$l");
            Object r = ((Map) rightValue).get("$r");

            PrototypeExpression rightExpression = toPrototypeExpression(l).composeWith(decodeBinaryOperator(rightKey), toPrototypeExpression(r));
            return new ExpressionCondition(prototypeField(leftValue), decodeConstraintType(decodedOp), rightExpression, binding);
        }

        return new Condition(leftValue + " " + decodedOp + " " + toOperand(rightValue), binding);
    }

    private PrototypeExpression.ExpressionBuilder toPrototypeExpression(Object value) {
        if (value instanceof Map) {
            return prototypeField(((Map) value).get("$m").toString());
        }
        return fixedValue(value);
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
