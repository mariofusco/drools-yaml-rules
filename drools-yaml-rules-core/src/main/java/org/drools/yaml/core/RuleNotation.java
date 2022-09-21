package org.drools.yaml.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.core.domain.RulesSet;

import static org.drools.yaml.core.ObjectMapperFactory.createMapper;

public interface RuleNotation {

    RulesSet jsonToRuleSet(ObjectMapper mapper, String json);

    default RulesSet toRulesSet(RuleFormat format, String text) {
        return jsonToRuleSet( createMapper( format.getJsonFactory() ), text );
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
