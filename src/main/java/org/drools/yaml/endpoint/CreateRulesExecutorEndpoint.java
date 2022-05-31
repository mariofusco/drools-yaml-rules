package org.drools.yaml.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.domain.rulesset.RulesSet;

import static org.drools.yaml.RulesExecutor.createRulesExecutor;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long executeQuery(RulesSet rulesSet) {
        return createRulesExecutor(rulesSet).getId();
    }
}
