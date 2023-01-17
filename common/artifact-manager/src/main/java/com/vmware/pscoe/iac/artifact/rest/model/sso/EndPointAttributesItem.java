package com.vmware.pscoe.iac.artifact.rest.model.sso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndPointAttributesItem {

    @JsonProperty("@type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("key")
    private String key;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "EndPointAttributesItem{" + "@type = '" + type + '\'' + ",id = '" + id + '\'' + ",value = '" + value + '\'' + ",key = '" + key + '\'' + "}";
    }
}