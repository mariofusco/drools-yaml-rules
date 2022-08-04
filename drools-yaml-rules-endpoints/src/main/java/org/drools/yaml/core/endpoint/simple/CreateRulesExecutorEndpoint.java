package org.drools.yaml.core.endpoint.simple;

import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.compilation.RulesCompilationContext;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.drools.yaml.api.context.RulesExecutorContainer;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.memorycompiler.KieMemoryCompiler;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();

    public static final RulesExecutorContainer executors = RulesExecutorContainer.INSTANCE;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRulesExecutor(RulesSet rulesSet) {
        String name = rulesSet.getName();
        String basePath = "/drl/ruleset/" + name;
        RuleSetResource ruleSetResource = new RuleSetResource(rulesSet, basePath);

        RulesCompilationContext rulesCompilationContext =
                new RulesCompilationContext(
                        new KieMemoryCompiler.MemoryCompilerClassLoader(
                                Thread.currentThread().getContextClassLoader()));

        compilationManager.processResource(rulesCompilationContext, ruleSetResource);

        RulesExecutor rulesExecutor = rulesCompilationContext.getRulesExecutor();
        executors.register(rulesExecutor);
        return rulesExecutor.getId();
    }
}
