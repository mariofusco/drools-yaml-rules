package org.drools.yaml.runtime.utils;

import org.drools.yaml.runtime.model.EfestoInputMap;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class InputMaps {
    public static EfestoInputMap executeFacts(long id, Map<String, Object> factMap) {
        return common(id, factMap, "execute-facts");
    }

    public static EfestoInputMap processFacts(long id, Map<String, Object> factMap) {
        return common(id, factMap, "process-facts");
    }

    private static EfestoInputMap common(long id, Map<String, Object> factMap, String operation) {
        FRI fri = makeFRI(id, operation);
        return new EfestoInputMap(fri, id, factMap, operation);
    }

    private static FRI makeFRI(Object... suffix) {
        String suffixString = Arrays.stream(suffix).map(Object::toString).collect(Collectors.joining("/"));
        String basePath = "/drl/ruleset/" + suffixString;
        return new FRI(basePath, "drl");
    }


}
