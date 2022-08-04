package org.drools.yaml.core.endpoint.durable;

import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.drools.yaml.api.domain.durable.DurableRuleMatch;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputRuleMatches;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rules-durable-executors/{id}/process")
public class ProcessFactsDurableEndpoint {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Map>> process(@PathParam("id") long id, Map<String, Object> factMap) {
//        return Collections.emptyList();
//        return RulesExecutorContainer.INSTANCE.get(id).process(factMap).stream()
//                .map(DurableRuleMatch::from).collect(Collectors.toList());

        String basePath = "/drl/ruleset/" + id + "/process";
        FRI fri = new FRI(basePath, "drl");
        EfestoInputMap efestoInputMap = new EfestoInputMap(fri, factMap, "process");
        RulesRuntimeContext rulesRuntimeContext =
                new RulesRuntimeContext(
                        new KieMemoryCompiler.MemoryCompilerClassLoader(
                                Thread.currentThread().getContextClassLoader()));

        RulesExecutor rulesExecutor = RulesExecutorContainer.INSTANCE.get(id);
        rulesRuntimeContext.setRulesExecutor(rulesExecutor);

        Collection<EfestoOutput> outputs = runtimeManager.evaluateInput(rulesRuntimeContext, efestoInputMap);
        EfestoOutputRuleMatches output = (EfestoOutputRuleMatches) outputs.iterator().next();
        return output.getOutputData().stream()
                .map(DurableRuleMatch::from).collect(Collectors.toList());
    }
}
