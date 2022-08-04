package org.drools.yaml.core.endpoint.durable;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.runtime.utils.RuntimeUtils;

@Path("/rules-durable-executors/{id}/execute")
public class ExecuteRulesDurableEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int execute(@PathParam("id") long id, Map<String, Object> factMap) {
        return RuntimeUtils.executeQuery(id, factMap);
    }
}
