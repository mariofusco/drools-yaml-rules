package org.drools.yaml.core.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.yaml.api.domain.RulesSet;

import static org.drools.yaml.api.ObjectMapperFactory.createMapper;
import static org.drools.yaml.api.RulesExecutor.createRulesExecutor;

@Path("/create-rules-executor")
public class CreateRulesExecutorEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long executeQuery(String s) throws JsonProcessingException {
        RulesSet rulesSet = createMapper(new JsonFactory()).readValue(s, RulesSet.class);
        return createRulesExecutor(rulesSet).getId();
    }
}
