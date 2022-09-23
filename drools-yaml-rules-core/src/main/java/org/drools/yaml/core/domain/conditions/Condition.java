package org.drools.yaml.core.domain.conditions;

import org.drools.model.view.ViewItem;
import org.drools.yaml.core.RuleGenerationContext;

public interface Condition {
    ViewItem toPattern(RuleGenerationContext ruleContext);
}
