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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pageInfo", "links", "superMetrics" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class SupermetricDTO implements Serializable {
	private static final long serialVersionUID = 5670765783544505917L;

	@JsonProperty("superMetrics")
	private List<SuperMetric> superMetrics = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("superMetrics")
	public List<SuperMetric> getSuperMetrics() {
		return superMetrics;
	}

	@JsonProperty("superMetrics")
	public void setSuperMetrics(List<SuperMetric> superMetrics) {
		this.superMetrics = superMetrics;
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
	@JsonPropertyOrder({ "id", "name", "formula", "description", "modificationTime" })
	public static class SuperMetric implements Serializable {
		private static final long serialVersionUID = -4863216664973423038L;

		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("formula")
		private String formula;

		@JsonProperty("description")
		private String description;

		@JsonProperty("modificationTime")
		private long modificationTime;

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

		@JsonProperty("formula")
		public String getFormula() {
			return formula;
		}

		@JsonProperty("formula")
		public void setFormula(String formula) {
			this.formula = formula;
		}

		@JsonProperty("description")
		public String getDescription() {
			return description;
		}

		@JsonProperty("description")
		public void setDescription(String description) {
			this.description = description;
		}

		@JsonProperty("modificationTime")
		public long getModificationTime() {
			return modificationTime;
		}

		@JsonProperty("modificationTime")
		public void setModificationTime(long modificationTime) {
			this.modificationTime = modificationTime;
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
