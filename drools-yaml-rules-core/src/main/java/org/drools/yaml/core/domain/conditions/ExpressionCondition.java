package org.drools.yaml.core.domain.conditions;

import org.drools.model.Index;
import org.drools.model.PrototypeExpression;
import org.drools.yaml.core.rulesmodel.ParsedCondition;

public class ExpressionCondition extends Condition {
    protected final PrototypeExpression left;
    protected final Index.ConstraintType operator;
    protected final PrototypeExpression right;

    public ExpressionCondition(PrototypeExpression left, Index.ConstraintType operator, PrototypeExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionCondition(String patternBinding, PrototypeExpression left, Index.ConstraintType operator, PrototypeExpression right) {
        this(left, operator, right);
        setPatternBinding(patternBinding);
    }

    @Override
    public ParsedCondition parse() {
        return new ParsedCondition(left, operator, right);
    }

    @Override
    public String toString() {
        return left + " " + operator + " " + right;
    }
}
