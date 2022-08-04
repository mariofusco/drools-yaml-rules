package org.drools.yaml.core.endpoint.durable;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import static org.drools.yaml.runtime.utils.RuntimeUtils.processDurableRules;

@Path("/rules-durable-executors/{id}/process")
public class ProcessFactsDurableEndpoint {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Map>> process(@PathParam("id") long id, Map<String, Object> factMap) {
        return processDurableRules(id, factMap);
    }
}
