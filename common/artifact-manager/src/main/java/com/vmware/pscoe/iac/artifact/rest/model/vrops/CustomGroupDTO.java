
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
@JsonPropertyOrder({ "groups" })
public class CustomGroupDTO implements Serializable {
    private static final long serialVersionUID = -8907264880213106297L;

    @JsonProperty("groups")
    private List<Group> groups = new ArrayList<>();

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("groups")
    public List<Group> getGroups() {
        return groups;
    }

    @JsonProperty("groups")
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "id", "identifier", "resourceKey", "policy", "autoResolveMembership", "membershipDefinition", "links" })
    @JsonIgnoreProperties({ "links" })
    public static class Group implements Serializable {
        private static final long serialVersionUID = -3051771900705327803L;

        @JsonProperty("id")
        private String id;

        @JsonProperty("identifier")
        private String identifier;

        @JsonProperty("resourceKey")
        private ResourceKey resourceKey;

        @JsonProperty("policy")
        private String policy;

        @JsonProperty("autoResolveMembership")
        private Boolean autoResolveMembership;

        @JsonProperty("membershipDefinition")
        private MembershipDefinition membershipDefinition;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("identifier")
        public String getIdentifier() {
            return identifier;
        }

        @JsonProperty("identifier")
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @JsonProperty("resourceKey")
        public ResourceKey getResourceKey() {
            return resourceKey;
        }

        @JsonProperty("resourceKey")
        public void setResourceKey(ResourceKey resourceKey) {
            this.resourceKey = resourceKey;
        }

        @JsonProperty("policy")
        public String getPolicy() {
            return policy;
        }

        @JsonProperty("policy")
        public void setPolicy(String policy) {
            this.policy = policy;
        }

        @JsonProperty("autoResolveMembership")
        public Boolean isAutoResolveMembership() {
            return autoResolveMembership;
        }

        @JsonProperty("autoResolveMembership")
        public void setAutoResolveMembership(Boolean autoResolveMembership) {
            this.autoResolveMembership = autoResolveMembership;
        }

        @JsonProperty("membershipDefinition")
        public MembershipDefinition getMembershipDefinition() {
            return membershipDefinition;
        }

        @JsonProperty("membershipDefinition")
        public void setMembershipDefinition(MembershipDefinition membershipDefinition) {
            this.membershipDefinition = membershipDefinition;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "includedResources", "excludedResources", "custom-group-properties", "rules" })
    public static class MembershipDefinition implements Serializable {
        private static final long serialVersionUID = 7552803338575289017L;

        @JsonProperty("includedResources")
        private List<Object> includedResources = new ArrayList<>();

        @JsonProperty("excludedResources")
        private List<Object> excludedResources = new ArrayList<>();

        @JsonProperty("custom-group-properties")
        private List<Object> customGroupProperties = new ArrayList<>();

        @JsonProperty("rules")
        private List<Rule> rules = new ArrayList<>();

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("includedResources")
        public List<Object> getIncludedResources() {
            return includedResources;
        }

        @JsonProperty("includedResources")
        public void setIncludedResources(List<Object> includedResources) {
            this.includedResources = includedResources;
        }

        @JsonProperty("excludedResources")
        public List<Object> getExcludedResources() {
            return excludedResources;
        }

        @JsonProperty("excludedResources")
        public void setExcludedResources(List<Object> excludedResources) {
            this.excludedResources = excludedResources;
        }

        @JsonProperty("custom-group-properties")
        public List<Object> getCustomGroupProperties() {
            return customGroupProperties;
        }

        @JsonProperty("custom-group-properties")
        public void setCustomGroupProperties(List<Object> customGroupProperties) {
            this.customGroupProperties = customGroupProperties;
        }

        @JsonProperty("rules")
        public List<Rule> getRules() {
            return rules;
        }

        @JsonProperty("rules")
        public void setRules(List<Rule> rules) {
            this.rules = rules;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "resourceKindKey", "statConditionRules", "propertyConditionRules", "resourceNameConditionRules", "relationshipConditionRules" })
    public static class Rule implements Serializable {
        private static final long serialVersionUID = 2858223788988040394L;

        @JsonProperty("resourceKindKey")
        private ResourceKindKey resourceKindKey;

        @JsonProperty("statConditionRules")
        private List<StatConditionRule> statConditionRules = new ArrayList<>();

        @JsonProperty("propertyConditionRules")
        private List<Object> propertyConditionRules = new ArrayList<>();

        @JsonProperty("resourceNameConditionRules")
        private List<ResourceNameConditionRule> resourceNameConditionRules = new ArrayList<>();

        @JsonProperty("relationshipConditionRules")
        private List<RelationshipConditionRule> relationshipConditionRules = new ArrayList<>();

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("resourceKindKey")
        public ResourceKindKey getResourceKindKey() {
            return resourceKindKey;
        }

        @JsonProperty("resourceKindKey")
        public void setResourceKindKey(ResourceKindKey resourceKindKey) {
            this.resourceKindKey = resourceKindKey;
        }

        @JsonProperty("statConditionRules")
        public List<StatConditionRule> getStatConditionRules() {
            return statConditionRules;
        }

        @JsonProperty("statConditionRules")
        public void setStatConditionRules(List<StatConditionRule> statConditionRules) {
            this.statConditionRules = statConditionRules;
        }

        @JsonProperty("propertyConditionRules")
        public List<Object> getPropertyConditionRules() {
            return propertyConditionRules;
        }

        @JsonProperty("propertyConditionRules")
        public void setPropertyConditionRules(List<Object> propertyConditionRules) {
            this.propertyConditionRules = propertyConditionRules;
        }

        @JsonProperty("resourceNameConditionRules")
        public List<ResourceNameConditionRule> getResourceNameConditionRules() {
            return resourceNameConditionRules;
        }

        @JsonProperty("resourceNameConditionRules")
        public void setResourceNameConditionRules(List<ResourceNameConditionRule> resourceNameConditionRules) {
            this.resourceNameConditionRules = resourceNameConditionRules;
        }

        @JsonProperty("relationshipConditionRules")
        public List<RelationshipConditionRule> getRelationshipConditionRules() {
            return relationshipConditionRules;
        }

        @JsonProperty("relationshipConditionRules")
        public void setRelationshipConditionRules(List<RelationshipConditionRule> relationshipConditionRules) {
            this.relationshipConditionRules = relationshipConditionRules;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "key", "stringValue", "doubleValue", "compareOperator" })
    public static class StatConditionRule implements Serializable {
        private static final long serialVersionUID = -5386165550764511301L;

        @JsonProperty("key")
        private String key;

        @JsonProperty("stringValue")
        private String stringValue;

        @JsonProperty("doubleValue")
        private Double doubleValue;

        @JsonProperty("compareOperator")
        private String compareOperator;

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

        @JsonProperty("stringValue")
        public String getStringValue() {
            return stringValue;
        }

        @JsonProperty("stringValue")
        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        @JsonProperty("doubleValue")
        public Double getDoubleValue() {
            return doubleValue;
        }

        @JsonProperty("doubleValue")
        public void setDoubleValue(Double doubleValue) {
            this.doubleValue = doubleValue;
        }

        @JsonProperty("compareOperator")
        public String getCompareOperator() {
            return compareOperator;
        }

        @JsonProperty("compareOperator")
        public void setCompareOperator(String compareOperator) {
            this.compareOperator = compareOperator;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "compareOperator" })
    public static class ResourceNameConditionRule implements Serializable {
        private static final long serialVersionUID = -7089630077528662679L;

        @JsonProperty("name")
        private String name;

        @JsonProperty("compareOperator")
        private String compareOperator;

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

        @JsonProperty("compareOperator")
        public String getCompareOperator() {
            return compareOperator;
        }

        @JsonProperty("compareOperator")
        public void setCompareOperator(String compareOperator) {
            this.compareOperator = compareOperator;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "relation", "name", "compareOperator", "travesalSpecId" })
    public static class RelationshipConditionRule implements Serializable {
        private static final long serialVersionUID = -750100357961113288L;

        @JsonProperty("relation")
        private String relation;

        @JsonProperty("name")
        private String name;

        @JsonProperty("compareOperator")
        private String compareOperator;

        @JsonProperty("travesalSpecId")
        private String travesalSpecId;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("relation")
        public String getRelation() {
            return relation;
        }

        @JsonProperty("relation")
        public void setRelation(String relation) {
            this.relation = relation;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("compareOperator")
        public String getCompareOperator() {
            return compareOperator;
        }

        @JsonProperty("compareOperator")
        public void setCompareOperator(String compareOperator) {
            this.compareOperator = compareOperator;
        }

        @JsonProperty("travesalSpecId")
        public String getTravesalSpecId() {
            return travesalSpecId;
        }

        @JsonProperty("travesalSpecId")
        public void setTravesalSpecId(String travesalSpecId) {
            this.travesalSpecId = travesalSpecId;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "resourceKind", "adapterKind" })
    public static class ResourceKindKey implements Serializable {
        private static final long serialVersionUID = -1575312205325925888L;

        @JsonProperty("resourceKind")
        private String resourceKind;

        @JsonProperty("adapterKind")
        private String adapterKind;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("resourceKind")
        public String getResourceKind() {
            return resourceKind;
        }

        @JsonProperty("resourceKind")
        public void setResourceKind(String resourceKind) {
            this.resourceKind = resourceKind;
        }

        @JsonProperty("adapterKind")
        public String getAdapterKind() {
            return adapterKind;
        }

        @JsonProperty("adapterKind")
        public void setAdapterKind(String adapterKind) {
            this.adapterKind = adapterKind;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "adapterKindKey", "resourceKindKey", "resourceIdentifiers" })
    public static class ResourceKey implements Serializable {
        private static final long serialVersionUID = 1936847230252766216L;

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
        public void setAdditionalProperty(String name, Object value) {
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
        public void setAdditionalProperty(String name, Object value) {
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
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }
    }
}
