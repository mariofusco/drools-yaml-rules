package org.drools.yaml.core.domain.conditions;

import java.util.Map;

import org.drools.model.Index;
import org.drools.model.PrototypeExpression;
import org.drools.model.view.ViewItem;
import org.drools.yaml.core.SessionGenerator;
import org.drools.yaml.core.rulesmodel.ParsedCondition;

import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;
import static org.drools.yaml.core.SessionGenerator.PROTOTYPE_NAME;
import static org.drools.yaml.core.domain.Binding.generateBinding;

public class MapCondition implements Condition {

    private Map<?,?> map;

    private String patternBinding;

    public MapCondition() { }

    public MapCondition(Map<?,?> map) {
        this.map = map;
    }

    public Map<?,?> getMap() {
        return map;
    }

    public void setMap(Map<?,?> map) {
        this.map = map;
    }

    public String getPatternBinding() {
        if (patternBinding == null) {
            patternBinding = generateBinding();
        }
        return patternBinding;
    }

    @Override
    public ViewItem toPattern(SessionGenerator.RuleContext ruleContext) {
        return singleCondition2Pattern(ruleContext, this);
    }

    private ViewItem singleCondition2Pattern(SessionGenerator.RuleContext ruleContext, MapCondition condition) {
        ParsedCondition parsedCondition = condition.parse();
        var pattern = ruleContext.getOrCreatePattern(condition.getPatternBinding(), PROTOTYPE_NAME);
        pattern.expr(parsedCondition.getLeft(), parsedCondition.getOperator(), parsedCondition.getRight());
        return pattern;
    }

    private ParsedCondition parse() {
        assert(map.size() == 1);
        Map.Entry entry = map.entrySet().iterator().next();
        String expressionName = (String) entry.getKey();
        Index.ConstraintType operator = decodeOperation(expressionName);

        Map<?,?> expression = (Map<?,?>) entry.getValue();
        PrototypeExpression left = map2Expr(expression.get("lhs"));
        PrototypeExpression right = map2Expr(expression.get("rhs"));

        return new ParsedCondition(left, operator, right);
    }

    private PrototypeExpression map2Expr(Object expr) {
        if (expr instanceof String) {
            return prototypeField((String)expr);
        }

        Map<?,?> exprMap = (Map) expr;
        assert(exprMap.size() == 1);
        Map.Entry entry = exprMap.entrySet().iterator().next();
        String key = (String) entry.getKey();
        Object value = entry.getValue();

        switch (key) {
            case "Integer":
            case "String":
                return fixedValue(value);
        }

        return prototypeField(key + "." + value);
    }

    private Index.ConstraintType decodeOperation(String expressionName) {
        switch (expressionName) {
            case "EqualsExpression": return Index.ConstraintType.EQUAL;
            case "GreaterThanExpression": return Index.ConstraintType.GREATER_THAN;
            case "GreaterThanOrEqualToExpression": return Index.ConstraintType.GREATER_OR_EQUAL;
            case "LessThanExpression": return Index.ConstraintType.LESS_THAN;
            case "LessThanOrEqualToExpression": return Index.ConstraintType.LESS_OR_EQUAL;
        }
        return null;
    }

    @Override
    public String toString() {
        return "MapCondition{" +
                "map=" + map +
                '}';
    }
}
