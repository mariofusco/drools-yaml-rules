package org.drools.yaml.api.rulesmodel;

import java.util.Map;

import org.drools.model.Index.ConstraintType;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeExpression;
import org.drools.model.PrototypeVariable;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.yaml.api.RuleGenerationContext;
import org.drools.yaml.api.domain.conditions.MapCondition;

import static org.drools.model.DSL.not;
import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;
import static org.drools.yaml.api.SessionGenerator.PROTOTYPE_NAME;

public class ParsedCondition {

    private final PrototypeExpression left;
    private final ConstraintType operator;
    private final PrototypeExpression right;

    private boolean notPattern = false;

    private boolean implicitPattern = false;

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

    public ParsedCondition withImplicitPattern(boolean implicitPattern) {
        this.implicitPattern = implicitPattern;
        return this;
    }

    public ViewItem patternToViewItem(RuleGenerationContext ruleContext, PrototypeDSL.PrototypePatternDef pattern) {
        if (operator == ConstraintType.NOT_EQUAL) {
            pattern.expr(getLeft(), ConstraintType.NOT_EQUAL, fixedValue(null));
        }

        if (implicitPattern) {
            PrototypeDSL.PrototypePatternDef first = ruleContext.getOrCreatePattern(ruleContext.generateBinding(), PROTOTYPE_NAME);
            pattern.expr(getLeft(), getOperator(), (PrototypeVariable) first.getFirstVariable(), getRight());
            return new CombinedExprViewItem(org.drools.model.Condition.Type.AND, new ViewItem[] { first, pattern });
        }

        pattern.expr(getLeft(), getOperator(), getRight());
        if (notPattern) {
            return not(pattern);
        }
        return pattern;
    }

    @Override
    public String toString() {
        return left + " " + operator + " " + right;
    }
}