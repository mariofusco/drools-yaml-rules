package org.drools.yaml.core.endpoint.durable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.api.domain.durable.DurableRules;

import static org.drools.yaml.compilation.utils.CompilationUtils.compileDurableRules;

@Path("/create-durable-rules-executor")
public class CreateRulesExecutorDurableEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRulesExecutor(DurableRules durableRules) {
        return compileDurableRules(durableRules);
    }
}
