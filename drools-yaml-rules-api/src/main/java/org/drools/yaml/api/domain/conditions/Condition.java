package org.drools.yaml.api.domain.conditions;

import org.drools.model.view.ViewItem;
import org.drools.yaml.api.RuleGenerationContext;

public interface Condition {
    ViewItem toPattern(RuleGenerationContext ruleContext);
}
