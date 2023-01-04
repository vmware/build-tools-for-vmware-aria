package com.vmware.pscoe.iac.artifact.rest.model.sso;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SsoEndpointDto {

    @JsonProperty("lastUpdated")
    private String lastUpdated;

    @JsonProperty("endPointType")
    private EndPointType endPointType;

    @JsonProperty("serviceInfoId")
    private String serviceInfoId;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("endPointAttributes")
    private List<EndPointAttributesItem> endPointAttributes;

    @JsonProperty("id")
    private String id;

    @JsonProperty("sslTrusts")
    private List<String> sslTrusts;

    @JsonProperty("url")
    private String url;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public EndPointType getEndPointType() {
        return endPointType;
    }

    public String getServiceInfoId() {
        return serviceInfoId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public List<EndPointAttributesItem> getEndPointAttributes() {
        return endPointAttributes;
    }

    public String getId() {
        return id;
    }

    public List<String> getSslTrusts() {
        return sslTrusts;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "SsoEndpointDto{" + "lastUpdated = '" + lastUpdated + '\'' + ",endPointType = '" + endPointType + '\'' + ",serviceInfoId = '" + serviceInfoId
                + '\'' + ",createdDate = '" + createdDate + '\'' + ",endPointAttributes = '" + endPointAttributes + '\'' + ",id = '" + id + '\''
                + ",sslTrusts = '" + sslTrusts + '\'' + ",url = '" + url + '\'' + "}";
    }
}