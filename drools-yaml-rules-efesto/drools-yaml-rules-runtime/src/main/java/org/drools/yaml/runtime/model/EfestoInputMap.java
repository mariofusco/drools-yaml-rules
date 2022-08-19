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
package org.drools.yaml.runtime.model;

import java.util.Map;

import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;

public class EfestoInputMap extends AbstractEfestoInput<Map<String, Object>> implements HasId {

    private final String operation;
    private final long id;

    public EfestoInputMap(FRI fri, long id, Map<String, Object> inputData, String operation) {
        super(fri, inputData);
        this.operation = operation;
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public long getId() {
        return id;
    }

}