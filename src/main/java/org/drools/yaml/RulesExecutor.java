package org.drools.yaml;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.facttemplates.Fact;
import org.drools.model.Prototype;
import org.drools.yaml.domain.YamlRulesSet;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;
import static org.drools.yaml.SessionGenerator.GLOBAL_MAP_FIELD;

public class RulesExecutor {

    private final SessionGenerator sessionGenerator;
    private final KieSession ksession;

    private FactHandle globalFactHandle;

    private RulesExecutor(SessionGenerator sessionGenerator) {
        this.sessionGenerator = sessionGenerator;
        this.ksession = sessionGenerator.build(this);
    }

    public static RulesExecutor create(String yaml) {
        try {
            ObjectMapper mapper = new ObjectMapper( new YAMLFactory() );
            YamlRulesSet rulesSet = mapper.readValue( yaml, YamlRulesSet.class );
            return new RulesExecutor( new SessionGenerator(rulesSet) );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void process(String json) {
        processFacts( new JSONObject(json).toMap() );
        ksession.fireAllRules();
    }

    public void processFacts(Map<String, Object> factMap) {
        for (Map.Entry<String, Object> entry : factMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Prototype prototype = sessionGenerator.getPrototype(entry.getKey());
                Fact fact = createMapBasedFact( prototype );
                populateFact(fact, (Map) entry.getValue(), "");
                ksession.insert(fact);
            } else {
                if (globalFactHandle == null) {
                    Prototype prototype = sessionGenerator.getPrototype(GLOBAL_MAP_FIELD);
                    Fact fact = createMapBasedFact( prototype );
                    fact.set(entry.getKey(), entry.getValue());
                    this.globalFactHandle = ksession.insert(fact);
                } else {
                    Fact fact = (Fact) ((InternalFactHandle) globalFactHandle).getObject();
                    fact.set(entry.getKey(), entry.getValue());
                    ksession.update(globalFactHandle, fact);
                }
            }
        }
    }

    private void populateFact(Fact fact, Map<?, ?> value, String fieldName) {
        for (Map.Entry entry : value.entrySet()) {
            fieldName += entry.getKey();
            if (entry.getValue() instanceof Map) {
                populateFact(fact, (Map) entry.getValue(), fieldName + ".");
            } else {
                fact.set(fieldName, entry.getValue());
            }
        }
    }
}
