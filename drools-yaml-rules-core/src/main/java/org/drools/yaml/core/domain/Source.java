package org.drools.yaml.core.domain;

public class Source {
    private String name;
    private String topic;
    private String url;
    private String schema;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "Source{" +
                "name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", url='" + url + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
