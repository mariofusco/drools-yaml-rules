package org.drools.yaml.core.endpoint;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.drools.yaml.core.KieSessionHolderUtils.kieSessionHolder;

@Path("/rules-executors/{id}/execute")
public class ExecuteRulesEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int executeQuery(@PathParam("id") long id, Map<String, Object> factMap) {
        return kieSessionHolder(id).executeFacts(factMap);
    }
}
