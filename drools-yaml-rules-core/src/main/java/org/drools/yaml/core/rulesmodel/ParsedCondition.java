package org.drools.yaml.core.rulesmodel;

import org.drools.model.Index.ConstraintType;

public class ParsedCondition {

    private String left;
    private ConstraintType operator;
    private Object right;

    private ParsedCondition(String condition) {
        int pos = findOperatorPos(condition);
        this.left = condition.substring(0, pos).trim();
        this.right = parseRightOperand( condition.substring(pos+2).trim() );
    }

    public static ParsedCondition parse(String condition) {
        return new ParsedCondition(condition);
    }

    public String getLeft() {
        return left;
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
        if (right.equals("null")) {
            return null;
        }
        if (right.equals("true")) {
            return true;
        }
        if (right.equals("false")) {
            return false;
        }
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