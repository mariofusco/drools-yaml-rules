package org.drools.yaml.core.domain.conditions;

import java.util.Arrays;
import java.util.List;

import org.drools.model.Index;
import org.drools.yaml.core.rulesmodel.ParsedCondition;

import static org.drools.yaml.core.domain.Binding.generateBinding;

public class Condition {

    public enum Type { ALL, ANY, SINGLE }

    private List<Condition> all;
    private List<Condition> any;
    private String single;
    private String patternBinding;

    public Condition() { }

    public Condition(String single) {
        this.single = single;
    }

    public Condition(String single, String patternBinding) {
        this(single);
        this.patternBinding = patternBinding;
    }

    public Condition(Condition... all) {
        this(Arrays.asList(all));
    }

    public Condition(List<Condition> all) {
        this.all = all;
    }

    public List<Condition> getAll() {
        return all;
    }

    public void setAll(List<Condition> all) {
        this.all = all;
    }

    public List<Condition> getAny() {
        return any;
    }

    public void setAny(List<Condition> any) {
        this.any = any;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getPatternBinding() {
        if (patternBinding == null) {
            patternBinding = generateBinding();
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
        return "Condition{'" + (patternBinding != null ? patternBinding + ": " : "") + single + "'}";
    }

    public static Condition combineConditions(Type type, List<Condition> conditions) {
        if (type == Type.SINGLE) {
            if (conditions.size() == 1) {
                return conditions.get(0);
            }
            throw new IllegalArgumentException();
        }
        Condition condition = new Condition();
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
}
