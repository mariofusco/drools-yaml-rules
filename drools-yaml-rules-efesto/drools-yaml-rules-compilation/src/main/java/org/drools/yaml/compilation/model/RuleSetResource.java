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
package org.drools.yaml.compilation.model;

import org.drools.yaml.api.domain.RulesSet;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

/**
 * File set for "drl" files
 */
public final class RuleSetResource implements EfestoResource<RulesSet> {

    private final RulesSet ruleSet;

    private final String basePath;

    public RuleSetResource(RulesSet ruleSet) {
        this.ruleSet = ruleSet;
        this.basePath = "/drl/ruleset/" + ruleSet.getName();
    }

    @Override
    public RulesSet getContent() {
        return ruleSet;
    }

    public String getBasePath() {
        return basePath;
    }
}
