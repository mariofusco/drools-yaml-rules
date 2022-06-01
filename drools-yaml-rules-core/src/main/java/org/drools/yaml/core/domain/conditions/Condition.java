package org.drools.yaml.core.domain.conditions;

import java.util.Arrays;
import java.util.List;

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
        this.single = single;
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

    public Type getType() {
        if (all != null) {
            return Type.ALL;
        }
        if (any != null) {
            return Type.ANY;
        }
        return Type.SINGLE;
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
}
