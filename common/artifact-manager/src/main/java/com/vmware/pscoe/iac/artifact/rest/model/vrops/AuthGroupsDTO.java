
package com.vmware.pscoe.iac.artifact.rest.model.vrops;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userGroups" })
public class AuthGroupsDTO implements Serializable {
    private static final long serialVersionUID = -1418134583655271586L;

    @JsonProperty("userGroups")
    private List<AuthGroupDTO> userGroups = new ArrayList<>();

    @JsonIgnore    
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("userGroups")
    public List<AuthGroupDTO> getUserGroups() {
        return userGroups;
    }

    @JsonProperty("userGroups")
    public void setUserGroups(List<AuthGroupDTO> userGroups) {
        this.userGroups = userGroups;
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
