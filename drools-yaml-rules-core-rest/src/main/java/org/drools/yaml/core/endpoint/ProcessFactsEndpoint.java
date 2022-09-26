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

import org.drools.yaml.api.domain.RuleMatch;

import static org.drools.yaml.runtime.KieSessionHolderUtils.kieSessionHolder;

@Path("/rules-executors/{id}/process")
public class ProcessFactsEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<RuleMatch> executeQuery(@PathParam("id") long id, Map<String, Object> factMap) {
        return kieSessionHolder(id).processFacts(factMap).stream()
                .map(RuleMatch::from).collect(Collectors.toList());
    }
}
