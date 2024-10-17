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
@JsonPropertyOrder({ "created-policies", "updated-policies", "skipped-policies" })
public class PolicyImportResultDTO implements Serializable {
	private static final long serialVersionUID = -8042756118741022913L;

	@JsonProperty("created-policies")
	private List<Policy> createdPolicies = new ArrayList<>();

	@JsonProperty("updated-policies")
	private List<Policy> updatedPolicies = new ArrayList<>();

	@JsonProperty("skipped-policies")
	private List<Policy> skippedPolicies = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("created-policies")
	public List<Policy> getCreatedPolicies() {
		return createdPolicies;
	}

	@JsonProperty("created-policies")
	public void setCreatedPolicies(List<Policy> createdPolicies) {
		this.createdPolicies = createdPolicies;
	}

	@JsonProperty("updated-policies")
	public List<Policy> getUpdatedPolicies() {
		return updatedPolicies;
	}

	@JsonProperty("updated-policies")
	public void setUpdatedPolicies(List<Policy> updatedPolicies) {
		this.updatedPolicies = updatedPolicies;
	}

	@JsonProperty("skipped-policies")
	public List<Policy> getSkippedPolicies() {
		return skippedPolicies;
	}

	@JsonProperty("skipped-policies")
	public void setSkippedPolicies(List<Policy> skippedPolicies) {
		this.skippedPolicies = skippedPolicies;
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
	@JsonPropertyOrder({ "id", "name" })
	public static class Policy implements Serializable {
		private static final long serialVersionUID = 6102066058193690851L;

		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

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

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
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
