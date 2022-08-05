/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.yaml.runtime.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.api.domain.durable.DurableRuleMatch;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputBoolean;
import org.drools.yaml.runtime.model.EfestoOutputFactMaps;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.drools.yaml.runtime.model.EfestoOutputMatches;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RuntimeUtils {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    private RuntimeUtils() {
    }

    public static int execute(long id, Map<String, Object> factMap) {
        EfestoOutputInteger output = (EfestoOutputInteger) common(id, factMap, "execute");
        return output.getOutputData();
    }

    public static List<RuleMatch> process(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process");
        return output.getOutputData().stream().map(RuleMatch::from).collect(Collectors.toList());
    }

    public static boolean retract(long id, Map<String, Object> factMap) {
        EfestoOutputBoolean output = (EfestoOutputBoolean) common(id, factMap, "retract");
        return output.getOutputData();
    }

    public static List<Map<String, Object>> getAllFacts(long id) {
        String basePath = "/drl/ruleset/" + id;
        FRI fri = new FRI(basePath, "drl");
        EfestoInputId efestoInputId = new EfestoInputId(fri, id);
        EfestoOutputFactMaps output = (EfestoOutputFactMaps) common(efestoInputId);
        return output.getOutputData();
    }

    public static List<Map<String, Map>> processDurableRules(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process");
        return output.getOutputData().stream()
                .map(DurableRuleMatch::from).collect(Collectors.toList());
    }

    private static EfestoOutput common(long id, Map<String, Object> factMap, String operation) {
        String basePath = "/drl/ruleset/" + id + '/' + operation;
        FRI fri = new FRI(basePath, "drl");
        EfestoInputMap efestoInputMap = new EfestoInputMap(fri, id, factMap, operation);
        return common(efestoInputMap);
    }

    private static EfestoOutput common(EfestoInput efestoInput) {
        RulesRuntimeContext rulesRuntimeContext =
                new RulesRuntimeContext(
                        new KieMemoryCompiler.MemoryCompilerClassLoader(
                                Thread.currentThread().getContextClassLoader()));

        Collection<EfestoOutput> outputs = runtimeManager.evaluateInput(rulesRuntimeContext, efestoInput);
        return outputs.iterator().next();
    }
}
