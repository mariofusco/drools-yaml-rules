package org.drools.yaml.core.domain;

import java.util.List;

public class RulesSet {
    private String name;
    private String hosts;
    private List<Source> sources;
    private List<Rule> host_rules;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public List<Rule> getHost_rules() {
        return host_rules;
    }

    public void setHost_rules(List<Rule> host_rules) {
        this.host_rules = host_rules;
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
                ", host_rules=" + host_rules +
                '}';
    }
}
