
package com.vmware.pscoe.iac.artifact.model.vrang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "endpointUri", "endpointConfigurationLink" })
public class VraNgIntegration implements Serializable {
    private static final long serialVersionUID = 6323872416192310200L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("endpointUri")
    private String endpointUri;

    @JsonProperty("endpointConfigurationLink")
    private String endpointConfigurationLink;

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("endpointUri")
    public String getEndpointUri() {
        return endpointUri;
    }

    @JsonProperty("endpointUri")
    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    @JsonProperty("endpointConfigurationLink")
    public String getEndpointConfigurationLink() {
        return endpointConfigurationLink;
    }

    @JsonProperty("endpointConfigurationLink")
    public void setEndpointConfigurationLink(String endpointConfigurationLink) {
        this.endpointConfigurationLink = endpointConfigurationLink;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
