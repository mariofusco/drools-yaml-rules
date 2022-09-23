package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;

public interface RuleNotation {

    RulesSet jsonToRuleSet(ObjectMapper mapper, String json);

    default RulesSet toRulesSet(RuleFormat format, String text) {
        return jsonToRuleSet(ObjectMapperFactory.createMapper(format.getJsonFactory() ), text );
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
