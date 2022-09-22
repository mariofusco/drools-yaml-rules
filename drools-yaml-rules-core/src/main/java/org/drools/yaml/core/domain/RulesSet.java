package org.drools.yaml.core.domain;

import java.util.List;
import java.util.stream.Collectors;

public class RulesSet {
    private String name;
    private List<String> hosts;
    private List<Source> sources;
    private List<RuleContainer> rules;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<Rule> getRules() {
        return rules.stream().map(RuleContainer::getRule).collect(Collectors.toList());
    }

    public void setRules(List<RuleContainer> rules) {
        this.rules = rules;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "RulesSet{" +
                "name='" + name + '\'' +
                ", hosts='" + hosts + '\'' +
                ", sources=" + sources +
                ", rules=" + rules +
                '}';
    }
}
