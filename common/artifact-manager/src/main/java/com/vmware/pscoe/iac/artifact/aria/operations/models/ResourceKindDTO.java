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
package com.vmware.pscoe.iac.artifact.aria.operations.models;

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
@JsonPropertyOrder({ "pageInfo", "links", "resource-kind" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class ResourceKindDTO implements Serializable {
	private static final long serialVersionUID = 6493081292177966190L;

	@JsonProperty("resource-kind")
	private List<ResourceKind> resourceKind = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("resource-kind")
	public List<ResourceKind> getResourceKind() {
		return resourceKind;
	}

	@JsonProperty("resource-kind")
	public void setResourceKind(List<ResourceKind> resourceKind) {
		this.resourceKind = resourceKind;
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
	@JsonPropertyOrder({ "key", "name", "adapterKind", "adapterKindName", "resourceKindType", "resourceKindSubType", "resourceIdentifierTypes", "links" })
	@JsonIgnoreProperties({ "links" })
	public static class ResourceKind implements Serializable {
		private static final long serialVersionUID = -4211567276552123736L;

		@JsonProperty("key")
		private String key;

		@JsonProperty("name")
		private String name;

		@JsonProperty("adapterKind")
		private String adapterKind;

		@JsonProperty("adapterKindName")
		private String adapterKindName;

		@JsonProperty("resourceKindType")
		private String resourceKindType;

		@JsonProperty("resourceKindSubType")
		private String resourceKindSubType;

		@JsonProperty("resourceIdentifierTypes")
		private List<ResourceIdentifierType> resourceIdentifierTypes = new ArrayList<>();

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

		@JsonProperty("adapterKind")
		public String getAdapterKind() {
			return adapterKind;
		}

		@JsonProperty("adapterKind")
		public void setAdapterKind(String adapterKind) {
			this.adapterKind = adapterKind;
		}

		@JsonProperty("adapterKindName")
		public String getAdapterKindName() {
			return adapterKindName;
		}

		@JsonProperty("adapterKindName")
		public void setAdapterKindName(String adapterKindName) {
			this.adapterKindName = adapterKindName;
		}

		@JsonProperty("resourceKindType")
		public String getResourceKindType() {
			return resourceKindType;
		}

		@JsonProperty("resourceKindType")
		public void setResourceKindType(String resourceKindType) {
			this.resourceKindType = resourceKindType;
		}

		@JsonProperty("resourceKindSubType")
		public String getResourceKindSubType() {
			return resourceKindSubType;
		}

		@JsonProperty("resourceKindSubType")
		public void setResourceKindSubType(String resourceKindSubType) {
			this.resourceKindSubType = resourceKindSubType;
		}

		@JsonProperty("resourceIdentifierTypes")
		public List<ResourceIdentifierType> getResourceIdentifierTypes() {
			return resourceIdentifierTypes;
		}

		@JsonProperty("resourceIdentifierTypes")
		public void setResourceIdentifierTypes(List<ResourceIdentifierType> resourceIdentifierTypes) {
			this.resourceIdentifierTypes = resourceIdentifierTypes;
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
	public static class ResourceIdentifierType implements Serializable {
		private static final long serialVersionUID = -7009149651340736012L;

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
