package org.drools.yaml.core.endpoint.simple;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long executeQuery(RulesSet rulesSet) {
        long toReturn = ID_GENERATOR.getAndAdd(1);
        String basePath = "/drl/ruleset/" + toReturn;
        RuleSetResource ruleSetResource = new RuleSetResource(rulesSet, basePath);
        EfestoCompilationContext efestoCompilationContext =
                EfestoCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        compilationManager.processResource(efestoCompilationContext, ruleSetResource);
        return toReturn;
    }
}
