package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drools.yaml.api.domain.RulesSet;

public interface RuleNotation {

    enum RuleConfigurationOption {
        ALLOW_IMPLICIT_JOINS
    }

    RulesSet jsonToRuleSet(ObjectMapper mapper, String json);

    default RulesSet toRulesSet(RuleFormat format, String text) {
        return jsonToRuleSet(ObjectMapperFactory.createMapper(format.getJsonFactory() ), text );
    }

    default RuleNotation withOptions(RuleConfigurationOption... options) {
        throw new UnsupportedOperationException();
    }

    enum CoreNotation implements RuleNotation {

        INSTANCE;

        @Override
        public RulesSet jsonToRuleSet(ObjectMapper mapper, String json) {
            try {
                return mapper.readValue(json, RulesSet.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public RuleNotation withOptions(RuleConfigurationOption... options) {
            return new CoreNotationWithOptions(options);
        }
    }

    class CoreNotationWithOptions implements RuleNotation {

        private final RuleConfigurationOption[] options;

        public CoreNotationWithOptions(RuleConfigurationOption[] options) {
            this.options = options;
        }

        @Override
        public RulesSet jsonToRuleSet(ObjectMapper mapper, String json) {
            return CoreNotation.INSTANCE.jsonToRuleSet(mapper, json).withOptions(options);
        }

    }

}
