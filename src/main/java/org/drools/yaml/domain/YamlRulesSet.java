package org.drools.yaml.domain;

import java.util.List;

public class YamlRulesSet {
    private String name;
    private String hosts;
    private List<YamlSource> sources;
    private List<YamlRule> host_rules;

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

    public List<YamlRule> getHost_rules() {
        return host_rules;
    }

    public void setHost_rules(List<YamlRule> host_rules) {
        this.host_rules = host_rules;
    }

    public List<YamlSource> getSources() {
        return sources;
    }

    public void setSources(List<YamlSource> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "YamlRulesSet{" +
                "name='" + name + '\'' +
                ", hosts='" + hosts + '\'' +
                ", sources=" + sources +
                ", host_rules=" + host_rules +
                '}';
    }
}
