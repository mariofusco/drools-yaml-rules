package org.drools.yaml.core;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.drools.yaml.core.domain.conditions.Condition;
import org.drools.yaml.core.domain.conditions.MapCondition;
import org.drools.yaml.core.domain.conditions.SimpleCondition;

public class ObjectMapperFactory {

    public static ObjectMapper createMapper(JsonFactory jsonFactory) {
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Condition.class, new ConditionDeserializer(mapper));
        mapper.registerModule(module);
        return mapper;
    }

    static class ConditionDeserializer extends JsonDeserializer<Condition> {

        private final ObjectMapper mapper;

        public ConditionDeserializer(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public Condition deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String node = jp.getCodec().readTree(jp).toString();

            if (node.contains("lhs")) {
                return new MapCondition(mapper.readValue(node, Map.class));
            }

            try {
                return mapper.readValue(node, SimpleCondition.class);
            } catch (Exception e) {
                return new MapCondition(mapper.readValue(node, Map.class));
            }
        }
    }
}
