package org.drools.yaml.core.rulesmodel;

import org.drools.model.Index.ConstraintType;
import org.drools.model.PrototypeExpression;

import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;

public class ParsedCondition {

    private final PrototypeExpression left;
    private final ConstraintType operator;
    private final PrototypeExpression right;

    public ParsedCondition(String left, ConstraintType operator, Object right) {
        this(prototypeField(left), operator, fixedValue(right));
    }

    public ParsedCondition(PrototypeExpression left, ConstraintType operator, PrototypeExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public PrototypeExpression getLeft() {
        return left;
    }

    public ConstraintType getOperator() {
        return operator;
    }

    public PrototypeExpression getRight() {
        return right;
    }
}