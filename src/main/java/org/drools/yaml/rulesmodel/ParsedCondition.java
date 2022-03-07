package org.drools.yaml.rulesmodel;

import org.drools.model.Index.ConstraintType;

import static org.drools.yaml.SessionGenerator.GLOBAL_MAP_FIELD;

public class ParsedCondition {

    private static final String DEFAULT_VAR = GLOBAL_MAP_FIELD;

    private String leftVar;
    private String leftField;
    private ConstraintType operator;
    private Object right;

    private ParsedCondition(String condition) {
        int pos = findOperatorPos(condition);
        String left = condition.substring(0, pos).trim();
        int leftDotPos = left.indexOf('.');
        if (leftDotPos < 0) {
            this.leftVar = DEFAULT_VAR;
            this.leftField = left;
        } else {
            this.leftVar = left.substring(0, leftDotPos).trim();
            this.leftField = left.substring(leftDotPos+1).trim();
        }
        this.right = parseRightOperand( condition.substring(pos+2).trim() );
    }

    public static ParsedCondition parse(String condition) {
        return new ParsedCondition(condition);
    }

    public String getLeftVar() {
        return leftVar;
    }

    public String getLeftField() {
        return leftField;
    }

    public ConstraintType getOperator() {
        return operator;
    }

    public Object getRight() {
        return right;
    }

    private int findOperatorPos(String condition) {
        int pos = condition.indexOf("==");
        if (pos >= 0) {
            operator = ConstraintType.EQUAL;
            return pos;
        }

        pos = condition.indexOf("!=");
        if (pos >= 0) {
            operator = ConstraintType.NOT_EQUAL;
            return pos;
        }

        pos = condition.indexOf(">=");
        if (pos >= 0) {
            operator = ConstraintType.GREATER_OR_EQUAL;
            return pos;
        }

        pos = condition.indexOf("<=");
        if (pos >= 0) {
            operator = ConstraintType.LESS_OR_EQUAL;
            return pos;
        }

        pos = condition.indexOf(">");
        if (pos >= 0) {
            operator = ConstraintType.GREATER_THAN;
            return pos;
        }

        pos = condition.indexOf("<");
        if (pos >= 0) {
            operator = ConstraintType.LESS_THAN;
            return pos;
        }

        throw new UnsupportedOperationException("Unknown operator for condition: " + condition);
    }

    private Object parseRightOperand(String right) {
        if (right.startsWith("\"")) {
            return right.substring(1, right.length()-1);
        }
        try {
            return Integer.parseInt(right);
        } catch (NumberFormatException nfe) {
            return Double.parseDouble(right);
        }
    }
}