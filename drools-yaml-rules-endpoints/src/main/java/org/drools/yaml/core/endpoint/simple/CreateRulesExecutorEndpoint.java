package org.drools.yaml.core.endpoint.simple;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.api.domain.RulesSet;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;

import static org.drools.yaml.compilation.utils.CompilationUtils.compileRulesSet;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRulesExecutor(RulesSet rulesSet) {
        return compileRulesSet(rulesSet);
    }
}
