package org.drools.yaml.core.endpoint.durable;

import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputBoolean;
import org.drools.yaml.runtime.utils.InputMaps;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/rules-durable-executors/{id}/retract-fact")
public class RetractFactsDurableEndpoint {

    @Inject
    RuntimeManager runtimeManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean retract(@PathParam("id") long id, Map<String, Object> factMap) {
        RulesRuntimeContext rulesRuntimeContext = RulesRuntimeContext.create();
        EfestoInputMap efestoInputMap = InputMaps.retract(id, factMap);

        var output = (EfestoOutputBoolean) runtimeManager
                .evaluateInput(rulesRuntimeContext, efestoInputMap)
                .iterator().next();

        return output.getOutputData();
    }
}
