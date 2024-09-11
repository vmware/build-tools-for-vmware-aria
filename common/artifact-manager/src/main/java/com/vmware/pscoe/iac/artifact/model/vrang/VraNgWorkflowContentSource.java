
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
@JsonPropertyOrder({"id", "name", "description", "typeId", "createdAt", "createdBy",
		"lastUpdatedAt", "lastUpdatedBy", "config", "itemsImported", "itemsFound",
		"lastImportStartedAt", "lastImportCompletedAt", "lastImportErrors", "global"})
public class VraNgWorkflowContentSource extends VraNgContentSourceBase implements Serializable {
	private static final long serialVersionUID = -3313748896114761975L;

	@JsonProperty("createdAt")
	protected String createdAt;

	@JsonProperty("createdBy")
	protected String createdBy;

	@JsonProperty("lastUpdatedAt")
	protected String lastUpdatedAt;

	@JsonProperty("lastUpdatedBy")
	protected String lastUpdatedBy;

	@JsonProperty("config")
	protected VraNgConfig config;

	@JsonProperty("lastImportStartedAt")
	protected String lastImportStartedAt;

	@JsonProperty("lastImportCompletedAt")
	protected String lastImportCompletedAt;

	@JsonIgnore
	protected transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("createdAt")
	public String getCreatedAt() {
		return createdAt;
	}

	@JsonProperty("createdAt")
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty("createdBy")
	public String getCreatedBy() {
		return createdBy;
	}

	@JsonProperty("createdBy")
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonProperty("lastUpdatedAt")
	public String getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	@JsonProperty("lastUpdatedAt")
	public void setLastUpdatedAt(String lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	@JsonProperty("lastUpdatedBy")
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	@JsonProperty("lastUpdatedBy")
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@JsonProperty("config")
	public VraNgConfig getConfig() {
		return config;
	}

	@JsonProperty("config")
	public void setConfig(VraNgConfig config) {
		this.config = config;
	}

	@JsonProperty("lastImportStartedAt")
	public String getLastImportStartedAt() {
		return lastImportStartedAt;
	}

	@JsonProperty("lastImportStartedAt")
	public void setLastImportStartedAt(String lastImportStartedAt) {
		this.lastImportStartedAt = lastImportStartedAt;
	}

	@JsonProperty("lastImportCompletedAt")
	public String getLastImportCompletedAt() {
		return lastImportCompletedAt;
	}

	@JsonProperty("lastImportCompletedAt")
	public void setLastImportCompletedAt(String lastImportCompletedAt) {
		this.lastImportCompletedAt = lastImportCompletedAt;
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
