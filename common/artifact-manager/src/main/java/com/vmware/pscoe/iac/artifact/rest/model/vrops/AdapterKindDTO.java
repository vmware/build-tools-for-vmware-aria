
package com.vmware.pscoe.iac.artifact.rest.model.vrops;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "adapter-kind" })
public class AdapterKindDTO implements Serializable {
    private static final long serialVersionUID = 1068047698436844558L;

    @JsonProperty("adapter-kind")
    private List<AdapterKind> adapterKind = new ArrayList<>();

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("adapter-kind")
    public List<AdapterKind> getAdapterKind() {
        return adapterKind;
    }

    @JsonProperty("adapter-kind")
    public void setAdapterKind(List<AdapterKind> adapterKind) {
        this.adapterKind = adapterKind;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "key", "name", "description", "adapterKindType", "describeVersion", "identifiers", "resourceKinds", "links" })
    @JsonIgnoreProperties({ "links" })
    public static class AdapterKind implements Serializable {
        private static final long serialVersionUID = -6414593808503585818L;

        @JsonProperty("key")
        private String key;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("adapterKindType")
        private String adapterKindType;

        @JsonProperty("describeVersion")
        private Integer describeVersion;

        @JsonProperty("identifiers")
        private List<Object> identifiers = new ArrayList<>();

        @JsonProperty("resourceKinds")
        private List<String> resourceKinds = new ArrayList<>();

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("key")
        public String getKey() {
            return key;
        }

        @JsonProperty("key")
        public void setKey(String key) {
            this.key = key;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("adapterKindType")
        public String getAdapterKindType() {
            return adapterKindType;
        }

        @JsonProperty("adapterKindType")
        public void setAdapterKindType(String adapterKindType) {
            this.adapterKindType = adapterKindType;
        }

        @JsonProperty("describeVersion")
        public Integer getDescribeVersion() {
            return describeVersion;
        }

        @JsonProperty("describeVersion")
        public void setDescribeVersion(Integer describeVersion) {
            this.describeVersion = describeVersion;
        }

        @JsonProperty("identifiers")
        public List<Object> getIdentifiers() {
            return identifiers;
        }

        @JsonProperty("identifiers")
        public void setIdentifiers(List<Object> identifiers) {
            this.identifiers = identifiers;
        }

        @JsonProperty("resourceKinds")
        public List<String> getResourceKinds() {
            return resourceKinds;
        }

        @JsonProperty("resourceKinds")
        public void setResourceKinds(List<String> resourceKinds) {
            this.resourceKinds = resourceKinds;
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
}
