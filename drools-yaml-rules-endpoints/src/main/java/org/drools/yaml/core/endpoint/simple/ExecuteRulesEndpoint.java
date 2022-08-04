package org.drools.yaml.core.endpoint.simple;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.drools.yaml.compilation.RulesCompilationContext;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;

@Path("/rules-executors/{id}/execute")
public class ExecuteRulesEndpoint {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int executeQuery(@PathParam("id") long id, Map<String, Object> factMap) {
        String basePath = "/drl/ruleset/" + id + "/execute";
        FRI fri = new FRI(basePath, "drl");
        EfestoInputMap efestoInputMap = new EfestoInputMap(fri, factMap, "execute");
        RulesRuntimeContext rulesRuntimeContext =
                new RulesRuntimeContext(
                        new KieMemoryCompiler.MemoryCompilerClassLoader(
                                Thread.currentThread().getContextClassLoader()));

        RulesExecutor rulesExecutor = RulesExecutorContainer.INSTANCE.get(id);
        rulesRuntimeContext.setRulesExecutor(rulesExecutor);

        Collection<EfestoOutput> outputs = runtimeManager.evaluateInput(rulesRuntimeContext, efestoInputMap);
        EfestoOutputInteger output = (EfestoOutputInteger) outputs.iterator().next();
        return output.getOutputData();
    }
}
