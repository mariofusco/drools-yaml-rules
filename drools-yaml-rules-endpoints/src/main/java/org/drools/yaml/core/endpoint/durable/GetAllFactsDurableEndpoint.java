package org.drools.yaml.core.endpoint.durable;

import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoOutputFactMaps;
import org.drools.yaml.runtime.utils.InputMaps;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/rules-durable-executors/{id}/get-all-facts")
public class GetAllFactsDurableEndpoint {

    @Inject
    RuntimeManager runtimeManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Object>> getAllFacts(@PathParam("id") long id) {
        RulesRuntimeContext rulesRuntimeContext = RulesRuntimeContext.create();
        EfestoInput<?> efestoInput = InputMaps.getAllFacts(id);

        var output = (EfestoOutputFactMaps) runtimeManager
                .evaluateInput(rulesRuntimeContext, efestoInput)
                .stream().findFirst().get();

        return output.getOutputData();
    }
}
