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
package org.drools.yaml.compilation.service;

import java.util.Collections;
import java.util.List;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;


public class KieCompilerServiceRuleSet implements KieCompilerService {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof RuleSetResource;
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }

        RuleSetResource ruleSetResource = (RuleSetResource) toProcess;
        RulesSet rulesSet = ruleSetResource.getContent();
        RulesExecutor rulesExecutor = RulesExecutor.createRulesExecutor(rulesSet);

        ((HasRulesExecutorContainer) context).register(rulesExecutor);
        return Collections.emptyList();
    }
}
