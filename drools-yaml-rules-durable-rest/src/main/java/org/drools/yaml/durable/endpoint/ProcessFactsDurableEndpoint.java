package org.drools.yaml.durable.endpoint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.core.RulesExecutorContainer;
import org.drools.yaml.durable.domain.DurableRuleMatch;

@Path("/rules-durable-executors/{id}/process-facts")
public class ProcessFactsDurableEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Map>> process(@PathParam("id") long id, Map<String, Object> factMap) {
        return RulesExecutorContainer.INSTANCE.get(id).processFacts(factMap).stream()
                .map(DurableRuleMatch::from).collect(Collectors.toList());
    }
}
