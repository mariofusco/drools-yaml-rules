package org.drools.yaml.durable.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.yaml.core.domain.Rule;
import org.drools.yaml.core.domain.conditions.Condition;

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
                    conditions.add( mapEntryToCondition(entry) );
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

    private Condition mapEntryToCondition(Map.Entry<String, ?> entry) {
        Map<String,?> value = (Map) entry.getValue();
        if (value.size() != 1) {
            throw new UnsupportedOperationException();
        }

        Map.Entry<String,?> e = value.entrySet().iterator().next();
        String operator = "==";
        if (e.getValue() instanceof Map) {
            operator = decodeOperator(e.getKey());
            value = (Map) e.getValue();
            if (value.size() != 1) {
                throw new UnsupportedOperationException();
            }
            e = value.entrySet().iterator().next();
        }

        String rightValue = e.getValue() instanceof String ? "\"" + e.getValue() + "\"" : "" + e.getValue();

        if (operator.equals("!=")) {
            return new Condition(
                    new Condition(e.getKey() + " != null"),
                    new Condition(e.getKey() + " " + operator + " " + rightValue));
        }

        return new Condition(e.getKey() + " " + operator + " " + rightValue);
    }

    private String decodeOperator(String op) {
        switch (op) {
            case "$eq":
                return "==";
            case "$lt":
                return "<";
            case "$gt":
                return ">";
            case "$lte":
                return "<=";
            case "$gte":
                return ">=";
            case "$neq":
                return "!=";
        }
        throw new UnsupportedOperationException("Unrecongnized operator " + op);
    }
}
