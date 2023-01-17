package com.vmware.pscoe.iac.artifact.rest.model.sso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndPointType {

    @JsonProperty("protocol")
    private String protocol;

    @JsonProperty("typeId")
    private String typeId;

    public String getProtocol() {
        return protocol;
    }

    public String getTypeId() {
        return typeId;
    }

    @Override
    public String toString() {
        return "EndPointType{" + "protocol = '" + protocol + '\'' + ",typeId = '" + typeId + '\'' + "}";
    }
}