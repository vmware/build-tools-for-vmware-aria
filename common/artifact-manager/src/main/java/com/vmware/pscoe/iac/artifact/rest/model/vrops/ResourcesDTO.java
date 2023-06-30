
package com.vmware.pscoe.iac.artifact.rest.model.vrops;

/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public final class ResourcesDTO implements Serializable {
	private static final long serialVersionUID = -4565701751149713687L;

	/**
	 * pageInfo.
	 */
	@JsonProperty("pageInfo")
	private PageInfo pageInfo;

	/**
	 * links.
	 */
	@JsonProperty("links")
	private List<Link> links;

	/**
	 * resourceList.
	 */
	@JsonProperty("resourceList")
	private List<ResourceList> resourceList = new ArrayList<>();

	/**
	 * additionalProperties.
	 */
	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * getPageInfo.
	 * 
	 * @return pageInfo
	 */
	@JsonProperty("pageInfo")
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * setPageInfo.
	 * 
	 * @param pageInfo
	 */
	@JsonProperty("pageInfo")
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	/**
	 * getLinks.
	 * 
	 * @return links
	 */
	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * setLinks.
	 * 
	 * @param links
	 */
	@JsonProperty("links")
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	/**
	 * getResourceList.
	 * 
	 * @return resourceList
	 */
	@JsonProperty("resourceList")
	public List<ResourceList> getResourceList() {
		return resourceList;
	}

	/**
	 * setResourceList.
	 * 
	 * @param resourceList
	 */
	@JsonProperty("resourceList")
	public void setResourceList(List<ResourceList> resourceList) {
		this.resourceList = resourceList;
	}

	/**
	 * getAdditionalProperties.
	 * 
	 * @return additionalProperties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * setAdditionalProperties.
	 * 
	 * @param key
	 * @param val
	 */
	@JsonAnySetter
	public void setAdditionalProperties(String key, Object val) {
		this.additionalProperties.put(key, val);
	}

	@JsonPropertyOrder({ "totalCount", "page", "pageSize" })
	public static final class PageInfo {
		/**
		 * totalCount.
		 */
		@JsonProperty("totalCount")
		private Long totalCount;

		/**
		 * page.
		 */
		@JsonProperty("page")
		private Long page;

		/**
		 * pageSize.
		 */
		@JsonProperty("pageSize")
		private Long pageSize;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

		/**
		 * getTotalCount.
		 * 
		 * @return totalCount
		 */
		@JsonProperty("totalCount")
		public Long getTotalCount() {
			return totalCount;
		}

		/**
		 * setTotalCount.
		 * 
		 * @param totalCount
		 */
		@JsonProperty("totalCount")
		public void setTotalCount(Long totalCount) {
			this.totalCount = totalCount;
		}

		/**
		 * getPage.
		 * 
		 * @return page
		 */
		@JsonProperty("page")
		public Long getPage() {
			return page;
		}

		/**
		 * setPage.
		 * 
		 * @param page
		 */
		@JsonProperty("page")
		public void setPage(Long page) {
			this.page = page;
		}

		/**
		 * getPageSize.
		 * 
		 * @return pageSize
		 */
		@JsonProperty("pageSize")
		public Long getPageSize() {
			return pageSize;
		}

		/**
		 * setPageSize.
		 * 
		 * @param pageSize
		 */
		@JsonProperty("pageSize")
		public void setPageSize(Long pageSize) {
			this.pageSize = pageSize;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperty.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperty(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "href", "rel", "name" })
	public static final class Link {
		/**
		 * href.
		 */
		@JsonProperty("href")
		private String href;

		/**
		 * rel.
		 */
		@JsonProperty("rel")
		private String rel;

		/**
		 * name.
		 */
		@JsonProperty("name")
		private String name;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

		/**
		 * getHref.
		 * 
		 * @return href
		 */
		@JsonProperty("href")
		public String getHref() {
			return href;
		}

		/**
		 * setHref.
		 * 
		 * @param href
		 */
		@JsonProperty("href")
		public void setHref(String href) {
			this.href = href;
		}

		/**
		 * getRel.
		 * 
		 * @return getRel
		 */
		@JsonProperty("rel")
		public String getRel() {
			return rel;
		}

		/**
		 * setRel.
		 * 
		 * @param rel
		 */
		@JsonProperty("rel")
		public void setRel(String rel) {
			this.rel = rel;
		}

		/**
		 * getName.
		 * 
		 * @return name
		 */
		@JsonProperty("name")
		public String getName() {
			return name;
		}

		/**
		 * setName.
		 * 
		 * @param name
		 */
		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperty.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperty(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "creationTime", "resourceKey", "resourceStatusStates", "resourceHealth", "resourceHealthValue", "dtEnabled", "monitoringInterval",
			"badges", "relatedResources", "links", "identifier" })
	@JsonIgnoreProperties({ "links" })
	public static final class ResourceList implements Serializable {
		private static final long serialVersionUID = -7272621081244854723L;

		/**
		 * creationTime.
		 */
		@JsonProperty("creationTime")
		private long creationTime;

		/**
		 * credentialInstanceId.
		 */
		@JsonProperty("credentialInstanceId")
		private String credentialInstanceId;

		/**
		 * description.
		 */
		@JsonProperty("description")
		private String description;

		/**
		 * resourceKey.
		 */
		@JsonProperty("resourceKey")
		private ResourceKey resourceKey;

		/**
		 * resourceStatusStates.
		 */
		@JsonProperty("resourceStatusStates")
		private List<ResourceStatusState> resourceStatusStates = new ArrayList<>();

		/**
		 * resourceHealth.
		 */
		@JsonProperty("resourceHealth")
		private String resourceHealth;

		/**
		 * resourceHealthValue.
		 */
		@JsonProperty("resourceHealthValue")
		private Double resourceHealthValue;

		/**
		 * dtEnabled.
		 */
		@JsonProperty("dtEnabled")
		private Boolean dtEnabled;

		/**
		 * monitoringInterval.
		 */
		@JsonProperty("monitoringInterval")
		private Integer monitoringInterval;

		/**
		 * badges.
		 */
		@JsonProperty("badges")
		private List<Object> badges = new ArrayList<>();

		/**
		 * relatedResources.
		 */
		@JsonProperty("relatedResources")
		private List<Object> relatedResources = new ArrayList<>();

		/**
		 * identifier.
		 */
		@JsonProperty("identifier")
		private String identifier;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getCreationTime.
		 * 
		 * @return creationTime
		 */
		@JsonProperty("creationTime")
		public long getCreationTime() {
			return creationTime;
		}

		/**
		 * setCreationTime.
		 * 
		 * @param creationTime
		 */
		@JsonProperty("creationTime")
		public void setCreationTime(long creationTime) {
			this.creationTime = creationTime;
		}

		/**
		 * getCredentialInstanceId.
		 * 
		 * @return credentialInstanceId
		 */
		public String getCredentialInstanceId() {
			return credentialInstanceId;
		}

		/**
		 * setCredentialInstanceId.
		 * 
		 * @param credentialInstanceId
		 */
		public void setCredentialInstanceId(String credentialInstanceId) {
			this.credentialInstanceId = credentialInstanceId;
		}

		/**
		 * getDescription.
		 * 
		 * @return description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * setDescription.
		 * 
		 * @param description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * getResourceKey.
		 * 
		 * @return resourceKey
		 */
		@JsonProperty("resourceKey")
		public ResourceKey getResourceKey() {
			return resourceKey;
		}

		/**
		 * setResourceKey.
		 * 
		 * @param resourceKey
		 */
		@JsonProperty("resourceKey")
		public void setResourceKey(ResourceKey resourceKey) {
			this.resourceKey = resourceKey;
		}

		/**
		 * getResourceStatusStates.
		 * 
		 * @return resourceStatusStates
		 */
		@JsonProperty("resourceStatusStates")
		public List<ResourceStatusState> getResourceStatusStates() {
			return resourceStatusStates;
		}

		/**
		 * setResourceStatusStates.
		 * 
		 * @param resourceStatusStates
		 */
		@JsonProperty("resourceStatusStates")
		public void setResourceStatusStates(List<ResourceStatusState> resourceStatusStates) {
			this.resourceStatusStates = resourceStatusStates;
		}

		/**
		 * getResourceHealth.
		 * 
		 * @return resourceHealth
		 */
		@JsonProperty("resourceHealth")
		public String getResourceHealth() {
			return resourceHealth;
		}

		/**
		 * setResourceHealth.
		 * 
		 * @param resourceHealth
		 */
		@JsonProperty("resourceHealth")
		public void setResourceHealth(String resourceHealth) {
			this.resourceHealth = resourceHealth;
		}

		/**
		 * getResourceHealthValue.
		 * 
		 * @return resourceHealthValue
		 */
		@JsonProperty("resourceHealthValue")
		public Double getResourceHealthValue() {
			return resourceHealthValue;
		}

		/**
		 * setResourceHealthValue.
		 * 
		 * @param resourceHealthValue
		 */
		@JsonProperty("resourceHealthValue")
		public void setResourceHealthValue(Double resourceHealthValue) {
			this.resourceHealthValue = resourceHealthValue;
		}

		/**
		 * isDtEnabled.
		 * 
		 * @return dtEnabled
		 */
		@JsonProperty("dtEnabled")
		public Boolean isDtEnabled() {
			return dtEnabled;
		}

		/**
		 * setDtEnabled.
		 * 
		 * @param dtEnabled
		 */
		@JsonProperty("dtEnabled")
		public void setDtEnabled(Boolean dtEnabled) {
			this.dtEnabled = dtEnabled;
		}

		/**
		 * getMonitoringInterval.
		 * 
		 * @return monitoringInterval
		 */
		@JsonProperty("monitoringInterval")
		public Integer getMonitoringInterval() {
			return monitoringInterval;
		}

		/**
		 * setMonitoringInterval.
		 * 
		 * @param monitoringInterval
		 */
		@JsonProperty("monitoringInterval")
		public void setMonitoringInterval(Integer monitoringInterval) {
			this.monitoringInterval = monitoringInterval;
		}

		/**
		 * getBadges.
		 * 
		 * @return badges
		 */
		@JsonProperty("badges")
		public List<Object> getBadges() {
			return badges;
		}

		/**
		 * setBadges.
		 * 
		 * @param badges
		 */
		@JsonProperty("badges")
		public void setBadges(List<Object> badges) {
			this.badges = badges;
		}

		/**
		 * getRelatedResources.
		 * 
		 * @return relatedResources
		 */
		@JsonProperty("relatedResources")
		public List<Object> getRelatedResources() {
			return relatedResources;
		}

		/**
		 * setRelatedResources.
		 * 
		 * @param relatedResources
		 */
		@JsonProperty("relatedResources")
		public void setRelatedResources(List<Object> relatedResources) {
			this.relatedResources = relatedResources;
		}

		/**
		 * getIdentifier.
		 * 
		 * @return identifier
		 */
		@JsonProperty("identifier")
		public String getIdentifier() {
			return identifier;
		}

		/**
		 * setIdentifier.
		 * 
		 * @param identifier
		 */
		@JsonProperty("identifier")
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "adapterInstanceId", "resourceStatus", "resourceState", "statusMessage" })
	public static final class ResourceStatusState implements Serializable {
		private static final long serialVersionUID = -787582647822155817L;

		/**
		 * adapterInstanceId.
		 */
		@JsonProperty("adapterInstanceId")
		private String adapterInstanceId;

		/**
		 * resourceStatus.
		 */
		@JsonProperty("resourceStatus")
		private String resourceStatus;

		/**
		 * resourceState.
		 */
		@JsonProperty("resourceState")
		private String resourceState;

		/**
		 * statusMessage.
		 */
		@JsonProperty("statusMessage")
		private String statusMessage;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getAdapterInstanceId.
		 * 
		 * @return adapterInstanceId
		 */
		@JsonProperty("adapterInstanceId")
		public String getAdapterInstanceId() {
			return adapterInstanceId;
		}

		/**
		 * setAdapterInstanceId.
		 * 
		 * @param adapterInstanceId
		 */
		@JsonProperty("adapterInstanceId")
		public void setAdapterInstanceId(String adapterInstanceId) {
			this.adapterInstanceId = adapterInstanceId;
		}

		/**
		 * getResourceStatus.
		 * 
		 * @return adapterInstanceId
		 */
		@JsonProperty("resourceStatus")
		public String getResourceStatus() {
			return resourceStatus;
		}

		/**
		 * setResourceStatus.
		 * 
		 * @param resourceStatus
		 */
		@JsonProperty("resourceStatus")
		public void setResourceStatus(String resourceStatus) {
			this.resourceStatus = resourceStatus;
		}

		/**
		 * getResourceState.
		 * 
		 * @return resourceState
		 */
		@JsonProperty("resourceState")
		public String getResourceState() {
			return resourceState;
		}

		/**
		 * setResourceState.
		 * 
		 * @param resourceState
		 */
		@JsonProperty("resourceState")
		public void setResourceState(String resourceState) {
			this.resourceState = resourceState;
		}

		/**
		 * getStatusMessage.
		 * 
		 * @return statusMessage
		 */
		@JsonProperty("statusMessage")
		public String getStatusMessage() {
			return statusMessage;
		}

		/**
		 * statusMessage.
		 * 
		 * @param statusMessage
		 */
		@JsonProperty("statusMessage")
		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "name", "adapterKindKey", "resourceKindKey", "resourceIdentifiers" })
	public static final class ResourceKey implements Serializable {
		private static final long serialVersionUID = -7173935404365000209L;

		/**
		 * name.
		 */
		@JsonProperty("name")
		private String name;

		/**
		 * adapterKindKey.
		 */
		@JsonProperty("adapterKindKey")
		private String adapterKindKey;

		/**
		 * resourceKindKey.
		 */
		@JsonProperty("resourceKindKey")
		private String resourceKindKey;

		/**
		 * resourceIdentifiers.
		 */
		@JsonProperty("resourceIdentifiers")
		private List<ResourceIdentifier> resourceIdentifiers = new ArrayList<>();

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getName.
		 * 
		 * @return name
		 */
		@JsonProperty("name")
		public String getName() {
			return name;
		}

		/**
		 * setName.
		 * 
		 * @param name
		 */
		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * getAdapterKindKey.
		 * 
		 * @return adapterKindKey
		 */
		@JsonProperty("adapterKindKey")
		public String getAdapterKindKey() {
			return adapterKindKey;
		}

		/**
		 * setAdapterKindKey.
		 * 
		 * @param adapterKindKey
		 */
		@JsonProperty("adapterKindKey")
		public void setAdapterKindKey(String adapterKindKey) {
			this.adapterKindKey = adapterKindKey;
		}

		/**
		 * getResourceKindKey.
		 * 
		 * @return resourceKindKey
		 */
		@JsonProperty("resourceKindKey")
		public String getResourceKindKey() {
			return resourceKindKey;
		}

		/**
		 * resourceKindKey.
		 * 
		 * @param resourceKindKey
		 */
		@JsonProperty("resourceKindKey")
		public void setResourceKindKey(String resourceKindKey) {
			this.resourceKindKey = resourceKindKey;
		}

		/**
		 * getResourceIdentifiers.
		 * 
		 * @return resourceIdentifiers
		 */
		@JsonProperty("resourceIdentifiers")
		public List<ResourceIdentifier> getResourceIdentifiers() {
			return resourceIdentifiers;
		}

		/**
		 * setResourceIdentifiers.
		 * 
		 * @param resourceIdentifiers
		 */
		@JsonProperty("resourceIdentifiers")
		public void setResourceIdentifiers(List<ResourceIdentifier> resourceIdentifiers) {
			this.resourceIdentifiers = resourceIdentifiers;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "identifierType", "value" })
	public static final class ResourceIdentifier implements Serializable {
		private static final long serialVersionUID = -4631445128052519908L;

		/**
		 * identifierType.
		 */
		@JsonProperty("identifierType")
		private IdentifierType identifierType;

		/**
		 * value.
		 */
		@JsonProperty("value")
		private String value;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getIdentifierType.
		 * 
		 * @return identifierType
		 */
		@JsonProperty("identifierType")
		public IdentifierType getIdentifierType() {
			return identifierType;
		}

		/**
		 * setIdentifierType.
		 * 
		 * @param identifierType
		 */
		@JsonProperty("identifierType")
		public void setIdentifierType(IdentifierType identifierType) {
			this.identifierType = identifierType;
		}

		/**
		 * getValue.
		 * 
		 * @return value
		 */
		@JsonProperty("value")
		public String getValue() {
			return value;
		}

		/**
		 * setValue.
		 * 
		 * @param value
		 */
		@JsonProperty("value")
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "name", "dataType", "isPartOfUniqueness" })
	public static final class IdentifierType implements Serializable {
		private static final long serialVersionUID = -1370467208132974903L;

		/**
		 * name.
		 */
		@JsonProperty("name")
		private String name;

		/**
		 * dataType.
		 */
		@JsonProperty("dataType")
		private String dataType;

		/**
		 * isPartOfUniqueness.
		 */
		@JsonProperty("isPartOfUniqueness")
		private Boolean isPartOfUniqueness;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private transient Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getName.
		 * 
		 * @return name
		 */
		@JsonProperty("name")
		public String getName() {
			return name;
		}

		/**
		 * setName.
		 * 
		 * @param name
		 */
		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * getDataType.
		 * 
		 * @return dataType
		 */
		@JsonProperty("dataType")
		public String getDataType() {
			return dataType;
		}

		/**
		 * setDataType.
		 * 
		 * @param dataType
		 */
		@JsonProperty("dataType")
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		/**
		 * isIsPartOfUniqueness.
		 * 
		 * @return isPartOfUniqueness
		 */
		@JsonProperty("isPartOfUniqueness")
		public Boolean isIsPartOfUniqueness() {
			return isPartOfUniqueness;
		}

		/**
		 * setIsPartOfUniqueness.
		 * 
		 * @param isPartOfUniqueness
		 */
		@JsonProperty("isPartOfUniqueness")
		public void setIsPartOfUniqueness(Boolean isPartOfUniqueness) {
			this.isPartOfUniqueness = isPartOfUniqueness;
		}

		/**
		 * getAdditionalProperties.
		 * 
		 * @return additionalProperties
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties.
		 * 
		 * @param key
		 * @param val
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String key, Object val) {
			this.additionalProperties.put(key, val);
		}
	}
}
