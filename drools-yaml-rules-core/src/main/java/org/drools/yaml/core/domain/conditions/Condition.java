package org.drools.yaml.core.domain.conditions;

import org.drools.model.view.ViewItem;
import org.drools.yaml.core.SessionGenerator;

public interface Condition {
    ViewItem toPattern(SessionGenerator.RuleContext ruleContext);
}
