package org.drools.yaml.compilation;

import java.util.concurrent.atomic.AtomicLong;

import org.drools.yaml.api.KieBaseHolder;
import org.drools.yaml.api.KieBaseHolderContainer;
import org.drools.yaml.api.RuleFormat;
import org.drools.yaml.api.RuleNotation;
import org.drools.yaml.api.domain.RulesSet;
import org.kie.api.KieBase;

public class RulesCompiler implements KieBaseHolder {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private final KieBase kieBase;
    private final long id;

    // This class has the following issues:
    // it is responsible to instantiate a KieSession (using the SessionGenerator)
    // it holds the generated kiesession
    // it is hold inside the RulesExecutorContainer just to provide anm (indirect) mapping between generated
    // kiesession and evaluation requests
    // it is also responsible for rule execution

    // All the code contained inside SessionGenerator, written that way, would have to be copied over and over, if
    // not already a copy,
    // because it fullfill the basic behavior "return a kiebase containing an executable model  out of a set of rules"
    // it mixes/bind the two phases, i.e. the creation of an executable model (that is a sort-of "compilation") and
    // the execution of it (that is the runtime)
    // invoking a method of a parameter, passing itself as parameter, smells a lot of anti-pattern
    // sessionGenerator.build(this);
    private RulesCompiler(KieBaseGenerator kieBaseGenerator, long id) {
        this.kieBase = kieBaseGenerator.build(id);
        this.id = id;
    }

    public static RulesCompiler createFromYaml(String yaml) {
        return createFromYaml(RuleNotation.CoreNotation.INSTANCE, yaml);
    }

    public static RulesCompiler createFromYaml(RuleNotation notation, String yaml) {
        return create(RuleFormat.YAML, notation, yaml);
    }

    public static RulesCompiler createFromJson(String json) {
        return createFromJson(RuleNotation.CoreNotation.INSTANCE, json);
    }

    public static RulesCompiler createFromJson(RuleNotation notation, String json) {
        return create(RuleFormat.JSON, notation, json);
    }

    private static RulesCompiler create(RuleFormat format, RuleNotation notation, String text) {
        return createRulesCompiler(notation.toRulesSet(format, text));
    }

    public static RulesCompiler createRulesCompiler(RulesSet rulesSet) {
        RulesCompiler rulesExecutor = new RulesCompiler(new KieBaseGenerator(rulesSet), ID_GENERATOR.getAndIncrement());
        KieBaseHolderContainer.INSTANCE.register(rulesExecutor);
        return rulesExecutor;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void dispose() {
        KieBaseHolderContainer.INSTANCE.dispose(this);
    }

    @Override
    public long rulesCount() {
        return kieBase.getKiePackages().stream().mapToLong(p -> p.getRules().size()).sum();
    }

    @Override
    public KieBase getKieBase() {
        return kieBase;
    }
}
