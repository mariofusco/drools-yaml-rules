package org.drools.yaml.core.endpoint.durable;

import org.drools.yaml.api.domain.durable.DurableRules;
import org.drools.yaml.compilation.RulesCompilationContext;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/create-durable-rules-executor")
public class CreateRulesExecutorDurableEndpoint {

    @Inject
    CompilationManager compilationManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRulesExecutor(DurableRules durableRules) {
        var resource = new RuleSetResource(durableRules.toRulesSet());
        var ctx = RulesCompilationContext.create();
        compilationManager.processResource(ctx, resource);
        return ctx.ruleExecutorId();
    }
}
