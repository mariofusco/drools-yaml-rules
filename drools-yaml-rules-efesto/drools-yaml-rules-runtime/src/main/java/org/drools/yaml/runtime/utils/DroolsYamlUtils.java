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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.core.facttemplates.Fact;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.drools.yaml.runtime.model.EfestoOutputRuleMatches;
import org.drools.yaml.runtime.rulesmodel.PrototypeFactory;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;
public class DroolsYamlUtils {

    public static final String PROTOTYPE_NAME = "DROOLS_PROTOTYPE";
    private final static PrototypeFactory prototypeFactory = new PrototypeFactory();

    private DroolsYamlUtils() {
    }

    public static EfestoOutputInteger execute(EfestoInputJson toEvaluate, RulesExecutor rulesExecutor) {
        int retrieved = rulesExecutor.execute(toEvaluate.getInputData());
        return new EfestoOutputInteger(toEvaluate.getFRI(), retrieved);
    }

    public static EfestoOutputInteger execute(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        int retrieved = rulesExecutor.execute(toEvaluate.getInputData());
        return new EfestoOutputInteger(toEvaluate.getFRI(), retrieved);
    }

    public static EfestoOutputRuleMatches process(EfestoInputMap toEvaluate, RulesExecutor rulesExecutor) {
        List<Match> matches = rulesExecutor.process(toEvaluate.getInputData());
        List<RuleMatch> toReturn = matches.stream().map(RuleMatch::from).collect(Collectors.toList());
        return new EfestoOutputRuleMatches(toEvaluate.getFRI(), toReturn);
    }

    public static EfestoOutputRuleMatches process(EfestoInputJson toEvaluate) {
        KieSession ksession = null; // TODO retrieve by fri
        List<Match> matches = process(toEvaluate.getInputData(), ksession);
        List<RuleMatch> toReturn = matches.stream().map(RuleMatch::from).collect(Collectors.toList());
        return new EfestoOutputRuleMatches(toEvaluate.getFRI(), toReturn);
    }

    static int execute(String json, KieSession ksession) {
        return execute(new JSONObject(json).toMap(), ksession);
    }

    private static int execute(Map<String, Object> factMap, KieSession ksession) {
        processFact(factMap, ksession);
        return ksession.fireAllRules();
    }

    static List<Match> process(String json, KieSession ksession) {
        return process( new JSONObject(json).toMap(), ksession );
    }

    static List<Match> process(Map<String, Object> factMap, KieSession ksession) {
        processFacts( factMap, ksession );
        RegisterOnlyAgendaFilter filter = new RegisterOnlyAgendaFilter();
        ksession.fireAllRules(filter);
        return filter.getMatchedRules();
    }

    static void processFacts(Map<String, Object> factMap, KieSession ksession) {
        if (factMap.size() == 1 && factMap.containsKey("facts")) {
            ((List<Map<String, Object>>) factMap.get("facts")).forEach( fact ->  processFacts(fact, ksession));
        } else {
            processFact(factMap, ksession);
        }
    }

    private static void processFact(Map<String, Object> factMap, KieSession ksession) {
        ksession.insert(mapToFact(factMap));
    }

    public static Fact mapToFact(Map<String, Object> factMap) {
        Fact fact = createMapBasedFact(prototypeFactory.getPrototype(PROTOTYPE_NAME));
        populateFact(fact, factMap, "");
        return fact;
    }

    private static void populateFact(Fact fact, Map<?, ?> value, String fieldName) {
        for (Map.Entry entry : value.entrySet()) {
            String key = fieldName + entry.getKey();
            if (entry.getValue() instanceof Map) {
                populateFact(fact, (Map) entry.getValue(), key + ".");
            } else {
                fact.set(key, entry.getValue());
            }
        }
    }

    private static class RegisterOnlyAgendaFilter implements AgendaFilter {

        private final Set<Match> matchedRules = new LinkedHashSet<>();

        @Override
        public boolean accept(Match match) {
            matchedRules.add(match);
            return false;
        }

        public List<Match> getMatchedRules() {
            return new ArrayList<>(matchedRules );
        }
    }
}
