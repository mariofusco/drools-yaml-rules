package org.drools.yaml.core.endpoint;

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
import org.drools.yaml.core.domain.RuleMatch;

@Path("/rules-executors/{id}/process")
public class ProcessFactsEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<RuleMatch> executeQuery(@PathParam("id") long id, Map<String, Object> factMap) {
        return RulesExecutorContainer.INSTANCE.get(id).process(factMap).stream()
                .map(RuleMatch::from).collect(Collectors.toList());
    }
}
