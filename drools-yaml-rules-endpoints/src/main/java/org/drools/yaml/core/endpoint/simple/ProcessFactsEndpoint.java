package org.drools.yaml.core.endpoint.simple;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.runtime.utils.RuntimeUtils;

@Path("/rules-executors/{id}/process")
public class ProcessFactsEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<RuleMatch> processQuery(@PathParam("id") long id, Map<String, Object> factMap) {
        return RuntimeUtils.processQuery(id, factMap);
    }
}
