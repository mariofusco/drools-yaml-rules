package org.drools.yaml.core.domain.conditions;

import java.util.Map;

import org.drools.model.view.ViewItem;
import org.drools.yaml.core.SessionGenerator;

public class MapCondition implements Condition {

    private Map map;

    public MapCondition() { }

    public MapCondition(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public ViewItem toPattern(SessionGenerator.RuleContext ruleContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "MapCondition{" +
                "map=" + map +
                '}';
    }
}
