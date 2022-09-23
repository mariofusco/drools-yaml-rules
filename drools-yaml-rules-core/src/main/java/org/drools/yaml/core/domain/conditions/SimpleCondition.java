package org.drools.yaml.core.domain.conditions;

import java.util.Arrays;
import java.util.List;

import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.PrototypeDSL;
import org.drools.model.PrototypeExpression;
import org.drools.model.PrototypeVariable;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.yaml.core.RuleGenerationContext;
import org.drools.yaml.core.rulesmodel.ParsedCondition;

import static org.drools.yaml.core.SessionGenerator.PROTOTYPE_NAME;

public class SimpleCondition implements Condition {

    public enum Type { ALL, ANY, SINGLE }

    private List<SimpleCondition> all;
    private List<SimpleCondition> any;
    private String single;
    private String patternBinding;

    public SimpleCondition() { }

    public SimpleCondition(String single) {
        this.single = single;
    }

    public SimpleCondition(String single, String patternBinding) {
        this(single);
        this.patternBinding = patternBinding;
    }

    public SimpleCondition(SimpleCondition... all) {
        this(Arrays.asList(all));
    }

    public SimpleCondition(List<SimpleCondition> all) {
        this.all = all;
    }

    public List<SimpleCondition> getAll() {
        return all;
    }

    public void setAll(List<SimpleCondition> all) {
        this.all = all;
    }

    public List<SimpleCondition> getAny() {
        return any;
    }

    public void setAny(List<SimpleCondition> any) {
        this.any = any;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    private String getPatternBinding(RuleGenerationContext ruleContext) {
        if (patternBinding == null) {
            patternBinding = ruleContext.generateBinding();
        }
        return patternBinding;
    }

    public void setPatternBinding(String patternBinding) {
        this.patternBinding = patternBinding;
    }

    public Type getType() {
        if (all != null) {
            return Type.ALL;
        }
        if (any != null) {
            return Type.ANY;
        }
        return Type.SINGLE;
    }

    public String otherBinding() {
        throw new UnsupportedOperationException();
    }

    public boolean beta() {
        return false;
    }

    @Override
    public String toString() {
        if (all != null) {
            return "AND_Condition{" + all + '}';
        }
        if (any != null) {
            return "OR_Condition{" + any + '}';
        }
        return "SimpleCondition{'" + (patternBinding != null ? patternBinding + ": " : "") + single + "'}";
    }

    public static SimpleCondition combineConditions(Type type, List<SimpleCondition> conditions) {
        if (type == Type.SINGLE) {
            if (conditions.size() == 1) {
                return conditions.get(0);
            }
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition();
        if (type == Type.ALL) {
            condition.setAll(conditions);
        } else if (type == Type.ANY) {
            condition.setAny(conditions);
        }
        return condition;
    }

    public ParsedCondition parse() {
        String condition = getSingle();

        Index.ConstraintType operator;
        int pos;
        if (condition.indexOf("==") >= 0) {
            pos = condition.indexOf("==");
            operator = Index.ConstraintType.EQUAL;
        } else if (condition.indexOf("!=") >= 0) {
            pos = condition.indexOf("!=");
            operator = Index.ConstraintType.NOT_EQUAL;
        } else if (condition.indexOf(">=") >= 0) {
            pos = condition.indexOf(">=");
            operator = Index.ConstraintType.GREATER_OR_EQUAL;
        } else if (condition.indexOf("<=") >= 0) {
            pos = condition.indexOf("<=");
            operator = Index.ConstraintType.LESS_OR_EQUAL;
        } else if (condition.indexOf(">") >= 0) {
            pos = condition.indexOf(">");
            operator = Index.ConstraintType.GREATER_THAN;
        } else if (condition.indexOf("<") >= 0) {
            pos = condition.indexOf("<");
            operator = Index.ConstraintType.LESS_THAN;
        } else {
            throw new UnsupportedOperationException("Unknown operator for condition: " + condition);
        }

        String left = condition.substring(0, pos).trim();
        int rightStart = pos + (operator == Index.ConstraintType.GREATER_THAN || operator == Index.ConstraintType.LESS_THAN ? 1 : 2);
        Object right = parseRightOperand( condition.substring(rightStart).trim() );

        return new ParsedCondition(left, operator, right);
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

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public ViewItem toPattern(RuleGenerationContext ruleContext) {
        return condition2Pattern(ruleContext, this);
    }

    private static ViewItem condition2Pattern(RuleGenerationContext ruleContext, SimpleCondition condition) {
        switch (condition.getType()) {
            case ANY:
                return new CombinedExprViewItem(org.drools.model.Condition.Type.OR, condition.getAny().stream().map(subC -> scopingCondition2Pattern(ruleContext, subC)).toArray(ViewItem[]::new));
            case ALL:
                return new CombinedExprViewItem(org.drools.model.Condition.Type.AND, condition.getAll().stream().map(subC -> condition2Pattern(ruleContext, subC)).toArray(ViewItem[]::new));
            case SINGLE:
                return singleCondition2Pattern(ruleContext, condition);
        }
        throw new UnsupportedOperationException();
    }

    private static ViewItem scopingCondition2Pattern(RuleGenerationContext ruleContext, SimpleCondition condition) {
        ruleContext.pushContext();
        ViewItem pattern = condition2Pattern(ruleContext, condition);
        ruleContext.popContext();
        return pattern;
    }

    private static ViewItem singleCondition2Pattern(RuleGenerationContext ruleContext, SimpleCondition condition) {
        ParsedCondition parsedCondition = condition.parse();
        var pattern = ruleContext.getOrCreatePattern(condition.getPatternBinding(ruleContext), PROTOTYPE_NAME);
        if (condition.beta()) {
            pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), ruleContext.getPatternVariable(condition.otherBinding()), parsedCondition.getRight());
        } else {
            if (!condition.coercedCondition(pattern, parsedCondition)) {
                pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), parsedCondition.getRight());
            }
        }
        return pattern;
    }

    private boolean coercedCondition(PrototypeDSL.PrototypePatternDef pattern, ParsedCondition parsedCondition) {
        // if the condition right value is a string literal representing a number creates an OR condition matching both the string and the number
        if (parsedCondition.getOperator() == Index.ConstraintType.EQUAL && parsedCondition.getRight() instanceof PrototypeExpression.FixedValue) {
            Object rightValue = ((PrototypeExpression.FixedValue) parsedCondition.getRight()).getValue();
            if (rightValue instanceof String) {
                try {
                    int intValue = Integer.parseInt(((String) rightValue));
                    PrototypeDSL.PrototypePatternDefImpl orPattern = new PrototypeDSL.PrototypePatternDefImpl(((PrototypeVariable) pattern.getFirstVariable()));
                    orPattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), parsedCondition.getRight());
                    orPattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), PrototypeExpression.fixedValue(intValue));
                    ((PrototypeDSL.PrototypePatternDefImpl) pattern).getItems().add( new PatternDSL.CombinedPatternExprItem<>( PatternDSL.LogicalCombiner.OR, orPattern.getItems() ) );
                    return true;
                } catch (NumberFormatException nfe) {
                    // not a number, ignore
                }
            }
        }
        return false;
    }

}
