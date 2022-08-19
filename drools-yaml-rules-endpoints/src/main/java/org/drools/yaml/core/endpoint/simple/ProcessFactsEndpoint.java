package org.drools.yaml.core.endpoint.simple;

import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputMatches;
import org.drools.yaml.runtime.utils.InputMaps;
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
import java.util.stream.Collectors;

@Path("/rules-executors/{id}/process-facts")
public class ProcessFactsEndpoint {

    @Inject
    RuntimeManager runtimeManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<RuleMatch> process(@PathParam("id") long id, Map<String, Object> factMap) {
        RulesRuntimeContext rulesRuntimeContext = RulesRuntimeContext.create();
        EfestoInputMap efestoInputMap = InputMaps.processFacts(id, factMap);

        var output = (EfestoOutputMatches) runtimeManager
                .evaluateInput(rulesRuntimeContext, efestoInputMap)
                .stream().findFirst().get();

        return output.getOutputData().stream()
                .map(RuleMatch::from)
                .collect(Collectors.toList());
    }
}