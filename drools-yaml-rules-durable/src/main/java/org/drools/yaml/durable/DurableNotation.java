package org.drools.yaml.durable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.RuleNotation;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.durable.domain.DurableRules;

public enum DurableNotation implements RuleNotation {

    INSTANCE;

    @Override
    public RulesSet jsonToRuleSet(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, DurableRules.class ).toRulesSet();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
