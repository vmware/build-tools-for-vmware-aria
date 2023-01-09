
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
@JsonPropertyOrder({ "pageInfo", "links", "resourceList" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class ResourcesDTO implements Serializable {
    private static final long serialVersionUID = -4565701751149713687L;

    @JsonProperty("resourceList")
    private List<ResourceList> resourceList = new ArrayList<>();

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("resourceList")
    public List<ResourceList> getResourceList() {
        return resourceList;
    }

    @JsonProperty("resourceList")
    public void setResourceList(List<ResourceList> resourceList) {
        this.resourceList = resourceList;
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
    @JsonPropertyOrder({ "creationTime", "resourceKey", "resourceStatusStates", "resourceHealth", "resourceHealthValue", "dtEnabled", "monitoringInterval",
            "badges", "relatedResources", "links", "identifier" })
    @JsonIgnoreProperties({ "links" })
    public static class ResourceList implements Serializable {
        private static final long serialVersionUID = -7272621081244854723L;

        @JsonProperty("creationTime")
        private long creationTime;

        @JsonProperty("credentialInstanceId")
        private String credentialInstanceId;

        @JsonProperty("description")
        private String description;

        @JsonProperty("resourceKey")
        private ResourceKey resourceKey;

        @JsonProperty("resourceStatusStates")
        private List<ResourceStatusState> resourceStatusStates = new ArrayList<>();

        @JsonProperty("resourceHealth")
        private String resourceHealth;

        @JsonProperty("resourceHealthValue")
        private Double resourceHealthValue;

        @JsonProperty("dtEnabled")
        private Boolean dtEnabled;

        @JsonProperty("monitoringInterval")
        private Integer monitoringInterval;

        @JsonProperty("badges")
        private List<Object> badges = new ArrayList<>();

        @JsonProperty("relatedResources")
        private List<Object> relatedResources = new ArrayList<>();

        @JsonProperty("identifier")
        private String identifier;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("creationTime")
        public long getCreationTime() {
            return creationTime;
        }

        @JsonProperty("creationTime")
        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public String getCredentialInstanceId() {
            return credentialInstanceId;
        }

        public void setCredentialInstanceId(String credentialInstanceId) {
            this.credentialInstanceId = credentialInstanceId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("resourceKey")
        public ResourceKey getResourceKey() {
            return resourceKey;
        }

        @JsonProperty("resourceKey")
        public void setResourceKey(ResourceKey resourceKey) {
            this.resourceKey = resourceKey;
        }

        @JsonProperty("resourceStatusStates")
        public List<ResourceStatusState> getResourceStatusStates() {
            return resourceStatusStates;
        }

        @JsonProperty("resourceStatusStates")
        public void setResourceStatusStates(List<ResourceStatusState> resourceStatusStates) {
            this.resourceStatusStates = resourceStatusStates;
        }

        @JsonProperty("resourceHealth")
        public String getResourceHealth() {
            return resourceHealth;
        }

        @JsonProperty("resourceHealth")
        public void setResourceHealth(String resourceHealth) {
            this.resourceHealth = resourceHealth;
        }

        @JsonProperty("resourceHealthValue")
        public Double getResourceHealthValue() {
            return resourceHealthValue;
        }

        @JsonProperty("resourceHealthValue")
        public void setResourceHealthValue(Double resourceHealthValue) {
            this.resourceHealthValue = resourceHealthValue;
        }

        @JsonProperty("dtEnabled")
        public Boolean isDtEnabled() {
            return dtEnabled;
        }

        @JsonProperty("dtEnabled")
        public void setDtEnabled(Boolean dtEnabled) {
            this.dtEnabled = dtEnabled;
        }

        @JsonProperty("monitoringInterval")
        public Integer getMonitoringInterval() {
            return monitoringInterval;
        }

        @JsonProperty("monitoringInterval")
        public void setMonitoringInterval(Integer monitoringInterval) {
            this.monitoringInterval = monitoringInterval;
        }

        @JsonProperty("badges")
        public List<Object> getBadges() {
            return badges;
        }

        @JsonProperty("badges")
        public void setBadges(List<Object> badges) {
            this.badges = badges;
        }

        @JsonProperty("relatedResources")
        public List<Object> getRelatedResources() {
            return relatedResources;
        }

        @JsonProperty("relatedResources")
        public void setRelatedResources(List<Object> relatedResources) {
            this.relatedResources = relatedResources;
        }

        @JsonProperty("identifier")
        public String getIdentifier() {
            return identifier;
        }

        @JsonProperty("identifier")
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "adapterInstanceId", "resourceStatus", "resourceState", "statusMessage" })
    public static class ResourceStatusState implements Serializable {
        private static final long serialVersionUID = -787582647822155817L;

        @JsonProperty("adapterInstanceId")
        private String adapterInstanceId;

        @JsonProperty("resourceStatus")
        private String resourceStatus;

        @JsonProperty("resourceState")
        private String resourceState;

        @JsonProperty("statusMessage")
        private String statusMessage;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("adapterInstanceId")
        public String getAdapterInstanceId() {
            return adapterInstanceId;
        }

        @JsonProperty("adapterInstanceId")
        public void setAdapterInstanceId(String adapterInstanceId) {
            this.adapterInstanceId = adapterInstanceId;
        }

        @JsonProperty("resourceStatus")
        public String getResourceStatus() {
            return resourceStatus;
        }

        @JsonProperty("resourceStatus")
        public void setResourceStatus(String resourceStatus) {
            this.resourceStatus = resourceStatus;
        }

        @JsonProperty("resourceState")
        public String getResourceState() {
            return resourceState;
        }

        @JsonProperty("resourceState")
        public void setResourceState(String resourceState) {
            this.resourceState = resourceState;
        }

        @JsonProperty("statusMessage")
        public String getStatusMessage() {
            return statusMessage;
        }

        @JsonProperty("statusMessage")
        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "adapterKindKey", "resourceKindKey", "resourceIdentifiers" })
    public static class ResourceKey implements Serializable {
        private static final long serialVersionUID = -7173935404365000209L;

        @JsonProperty("name")
        private String name;

        @JsonProperty("adapterKindKey")
        private String adapterKindKey;

        @JsonProperty("resourceKindKey")
        private String resourceKindKey;

        @JsonProperty("resourceIdentifiers")
        private List<ResourceIdentifier> resourceIdentifiers = new ArrayList<>();

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

        @JsonProperty("adapterKindKey")
        public String getAdapterKindKey() {
            return adapterKindKey;
        }

        @JsonProperty("adapterKindKey")
        public void setAdapterKindKey(String adapterKindKey) {
            this.adapterKindKey = adapterKindKey;
        }

        @JsonProperty("resourceKindKey")
        public String getResourceKindKey() {
            return resourceKindKey;
        }

        @JsonProperty("resourceKindKey")
        public void setResourceKindKey(String resourceKindKey) {
            this.resourceKindKey = resourceKindKey;
        }

        @JsonProperty("resourceIdentifiers")
        public List<ResourceIdentifier> getResourceIdentifiers() {
            return resourceIdentifiers;
        }

        @JsonProperty("resourceIdentifiers")
        public void setResourceIdentifiers(List<ResourceIdentifier> resourceIdentifiers) {
            this.resourceIdentifiers = resourceIdentifiers;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "identifierType", "value" })
    public static class ResourceIdentifier implements Serializable {
        private static final long serialVersionUID = -4631445128052519908L;

        @JsonProperty("identifierType")
        private IdentifierType identifierType;

        @JsonProperty("value")
        private String value;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("identifierType")
        public IdentifierType getIdentifierType() {
            return identifierType;
        }

        @JsonProperty("identifierType")
        public void setIdentifierType(IdentifierType identifierType) {
            this.identifierType = identifierType;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }

        @JsonProperty("value")
        public void setValue(String value) {
            this.value = value;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "dataType", "isPartOfUniqueness" })
    public static class IdentifierType implements Serializable {
        private static final long serialVersionUID = -1370467208132974903L;

        @JsonProperty("name")
        private String name;

        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("isPartOfUniqueness")
        private Boolean isPartOfUniqueness;

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

        @JsonProperty("dataType")
        public String getDataType() {
            return dataType;
        }

        @JsonProperty("dataType")
        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        @JsonProperty("isPartOfUniqueness")
        public Boolean isIsPartOfUniqueness() {
            return isPartOfUniqueness;
        }

        @JsonProperty("isPartOfUniqueness")
        public void setIsPartOfUniqueness(Boolean isPartOfUniqueness) {
            this.isPartOfUniqueness = isPartOfUniqueness;
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
