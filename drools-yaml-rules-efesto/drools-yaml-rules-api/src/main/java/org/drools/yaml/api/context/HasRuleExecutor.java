package org.drools.yaml.api.context;

public interface HasRuleExecutor {
    void setRulesExecutor(RulesExecutor rulesExecutor);
    RulesExecutor getRulesExecutor();
}
