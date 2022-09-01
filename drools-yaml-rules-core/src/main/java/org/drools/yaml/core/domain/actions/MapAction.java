package org.drools.yaml.core.domain.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.model.Drools;
import org.drools.yaml.core.RulesExecutor;

public class MapAction extends HashMap implements Action {

    private final List<Action> knownActions = new ArrayList<>();

    @Override
    public Object put(Object key, Object value) {
        switch(key.toString()) {
            case AssertFact.ACTION_NAME:
            case RetractFact.ACTION_NAME:
            case PostEvent.ACTION_NAME:
                String ruleset = (String)((Map) value).get("ruleset");
                Map<String, Object> fact = (Map<String, Object>)((Map) value).get("fact");
                if (ruleset != null && fact != null) {
                    FactAction factAction = null;
                    if (key.toString().equals(RetractFact.ACTION_NAME)) {
                        factAction = new RetractFact();
                    } else if (key.toString().equals(PostEvent.ACTION_NAME)) {
                        factAction = new PostEvent();
                    } else {
                        factAction = new AssertFact();
                    }
                    factAction.setRuleset(ruleset);
                    factAction.setFact(fact);
                    knownActions.add(factAction);
                }
                break;
            case RunPlaybook.ACTION_NAME:
                for (Map playbook : ((Collection<Map>) value)) {
                    String name = (String)playbook.get("name");
                    if (name != null) {
                        RunPlaybook runPlaybook = new RunPlaybook();
                        runPlaybook.setName(name);
                        knownActions.add(runPlaybook);
                    }
                }

        }
        return super.put(key, value);
    }

    @Override
    public void execute(RulesExecutor rulesExecutor, Drools drools) {
        knownActions.forEach( a -> a.execute(rulesExecutor, drools));
    }
}
