package org.drools.yaml.api.notations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.domain.durable.DurableRules;

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
