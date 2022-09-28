package org.drools.yaml.api.rulesmodel;

import org.drools.model.Index.ConstraintType;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeExpression;
import org.drools.model.view.ViewItem;

import static org.drools.model.DSL.not;
import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;

public class ParsedCondition {

    private final PrototypeExpression left;
    private final ConstraintType operator;
    private final PrototypeExpression right;

    private boolean notPattern = false;

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

    public ParsedCondition withNotPattern(boolean notPattern) {
        this.notPattern = notPattern;
        return this;
    }

    public ViewItem patternToViewItem(PrototypeDSL.PrototypePatternDef pattern) {
        pattern.expr(getLeft(), getOperator(), getRight());
        if (notPattern) {
            return not(pattern);
        }
        return pattern;
    }
}