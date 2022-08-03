package org.drools.yaml.api.notations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.RuleFormat;
import org.drools.yaml.api.domain.RulesSet;

public interface RuleNotation {

    RulesSet jsonToRuleSet(ObjectMapper mapper, String json);

    default RulesSet toRulesSet(RuleFormat format, String text) {
        return jsonToRuleSet( new ObjectMapper( format.getJsonFactory() ), text );
    }

    enum CoreNotation implements RuleNotation {

        INSTANCE;

        @Override
        public RulesSet jsonToRuleSet(ObjectMapper mapper, String json) {
            try {
                return mapper.readValue(json, RulesSet.class );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
