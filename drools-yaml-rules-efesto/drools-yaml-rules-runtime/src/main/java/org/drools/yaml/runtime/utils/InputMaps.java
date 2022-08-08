package org.drools.yaml.runtime.utils;

import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class InputMaps {

    public static EfestoInput<?> getAllFacts(long id) {
        FRI fri = makeFRI(id);
        return new EfestoInputId(fri, id);
    }

    public static EfestoInputMap executeFacts(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "execute-facts");
    }

    public static EfestoInputMap processFacts(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "process-facts");
    }

    public static EfestoInputMap processEvents(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "process-events");
    }

    public static EfestoInputMap retract(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "retract");
    }

    private static EfestoInputMap commonInputMap(long id, Map<String, Object> factMap, String operation) {
        FRI fri = makeFRI(id, operation);
        return new EfestoInputMap(fri, id, factMap, operation);
    }

    private static FRI makeFRI(Object... suffix) {
        String suffixString = Arrays.stream(suffix).map(Object::toString).collect(Collectors.joining("/"));
        String basePath = "/drl/ruleset/" + suffixString;
        return new FRI(basePath, "drl");
    }


}
