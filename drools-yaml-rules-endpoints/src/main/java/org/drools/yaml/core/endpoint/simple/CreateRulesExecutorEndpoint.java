package org.drools.yaml.core.endpoint.simple;

import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.compilation.RulesCompilationContext;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    @Inject
    CompilationManager compilationManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRulesExecutor(RulesSet rulesSet) {
        var resource = new RuleSetResource(rulesSet);
        var ctx = RulesCompilationContext.create();
        compilationManager.processResource(ctx, resource);
        return ctx.ruleExecutorId();
    }
}
