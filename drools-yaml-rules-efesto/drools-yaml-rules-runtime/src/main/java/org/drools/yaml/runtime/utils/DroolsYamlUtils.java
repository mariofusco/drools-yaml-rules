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

import java.util.List;
import java.util.Map;

import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputBoolean;
import org.drools.yaml.runtime.model.EfestoOutputFactMaps;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.drools.yaml.runtime.model.EfestoOutputMatches;
import org.kie.api.runtime.rule.Match;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

public class DroolsYamlUtils {

    private DroolsYamlUtils() {
    }

    public static EfestoOutputInteger executeFacts(EfestoInputJson toEvaluate, RulesExecutor rulesExecutor) {
        int retrieved = rulesExecutor.executeFacts(toEvaluate.getInputData());
        return new EfestoOutputInteger(toEvaluate.getFRI(), retrieved);
    }

    public static EfestoOutputMatches processFacts(EfestoInputJson toEvaluate, RulesExecutor rulesExecutor) {
        List<Match> matches = rulesExecutor.processFacts(toEvaluate.getInputData());
        return new EfestoOutputMatches(toEvaluate.getFRI(), matches);
    }

    public static EfestoOutputFactMaps getAllFacts(EfestoInputId toEvaluate, RulesExecutor rulesExecutor) {
        List<Map<String, Object>> allFacts = rulesExecutor.getAllFactsAsMap();
        return new EfestoOutputFactMaps(toEvaluate.getFRI(), allFacts);
    }

    public static EfestoOutput evaluate(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        switch(toEvaluate.getOperation()) {
            case "execute-facts" :
                return executeFacts(toEvaluate, rulesExecutor);
            case "process-facts":
                return processFacts(toEvaluate, rulesExecutor);
            case "process-events":
                return processEvents(toEvaluate, rulesExecutor);
            case "retract":
                return retract(toEvaluate, rulesExecutor);
            default:
                throw new RuntimeException("Failed to evaluate " + toEvaluate.getOperation());
        }
    }

    private static EfestoOutputInteger executeFacts(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        int retrieved = rulesExecutor.executeFacts(toEvaluate.getInputData());
        return new EfestoOutputInteger(toEvaluate.getFRI(), retrieved);
    }

    private static EfestoOutputMatches processFacts(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        List<Match> matches = rulesExecutor.processFacts(toEvaluate.getInputData());
        return new EfestoOutputMatches(toEvaluate.getFRI(), matches);
    }

    private static EfestoOutputMatches processEvents(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        List<Match> matches = rulesExecutor.processEvents(toEvaluate.getInputData());
        return new EfestoOutputMatches(toEvaluate.getFRI(), matches);
    }

    private static EfestoOutputBoolean retract(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        boolean retracted = rulesExecutor.retractFact(toEvaluate.getInputData());
        return new EfestoOutputBoolean(toEvaluate.getFRI(), retracted);
    }
}
