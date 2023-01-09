package com.vmware.pscoe.iac.artifact.rest.model.vrli;

import java.io.Serializable;
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
@JsonPropertyOrder({ "contentPackMetadataList" })
public class ContentPackMetadataListDTO implements Serializable {
    private static final long serialVersionUID = 7183649262190355333L;

    @JsonProperty("contentPackMetadataList")
    private List<ContentPackDTO> contentPackMetadataList;

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("contentPackMetadataList")
    public List<ContentPackDTO> getContentPackMetadataList() {
        return contentPackMetadataList;
    }

    @JsonProperty("contentPackMetadataList")
    public void setContentPackMetadataList(List<ContentPackDTO> contentPackMetadataList) {
        this.contentPackMetadataList = contentPackMetadataList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
