package org.drools.yaml.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public enum RuleFormat {
    YAML, JSON;

    public JsonFactory getJsonFactory() {
        return this == YAML ? new YAMLFactory() : new JsonFactory();
    }
}