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
@JsonPropertyOrder({ "pageInfo", "links", "viewDefinitions" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class ViewDefinitionDTO implements Serializable {
	private static final long serialVersionUID = -5287778608502410853L;

	@JsonProperty("viewDefinitions")
	private List<ViewDefinition> viewDefinitions = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("viewDefinitions")
	public List<ViewDefinition> getViewDefinitions() {
		return viewDefinitions;
	}

	@JsonProperty("viewDefinitions")
	public void setViewDefinitions(List<ViewDefinition> viewDefinitions) {
		this.viewDefinitions = viewDefinitions;
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
	@JsonPropertyOrder({ "name", "description", "type", "presentationType", "subjects", "owner", "active", "links", "id" })
	@JsonIgnoreProperties({ "links" })
	public static class ViewDefinition implements Serializable {
		private static final long serialVersionUID = 5175470177444359491L;

		@JsonProperty("name")
		private String name;

		@JsonProperty("description")
		private String description;

		@JsonProperty("type")
		private String type;

		@JsonProperty("presentationType")
		private String presentationType;

		@JsonProperty("subjects")
		private List<String> subjects = new ArrayList<>();

		@JsonProperty("owner")
		private String owner;

		@JsonProperty("active")
		private Boolean active;

		@JsonProperty("id")
		private String id;

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

		@JsonProperty("description")
		public String getDescription() {
			return description;
		}

		@JsonProperty("description")
		public void setDescription(String description) {
			this.description = description;
		}

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("presentationType")
		public String getPresentationType() {
			return presentationType;
		}

		@JsonProperty("presentationType")
		public void setPresentationType(String presentationType) {
			this.presentationType = presentationType;
		}

		@JsonProperty("subjects")
		public List<String> getSubjects() {
			return subjects;
		}

		@JsonProperty("subjects")
		public void setSubjects(List<String> subjects) {
			this.subjects = subjects;
		}

		@JsonProperty("owner")
		public String getOwner() {
			return owner;
		}

		@JsonProperty("owner")
		public void setOwner(String owner) {
			this.owner = owner;
		}

		@JsonProperty("active")
		public Boolean isActive() {
			return active;
		}

		@JsonProperty("active")
		public void setActive(Boolean active) {
			this.active = active;
		}

		@JsonProperty("id")
		public String getId() {
			return id;
		}

		@JsonProperty("id")
		public void setId(String id) {
			this.id = id;
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
