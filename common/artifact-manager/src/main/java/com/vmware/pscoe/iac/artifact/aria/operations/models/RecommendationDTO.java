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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "recommendations" })
public class RecommendationDTO implements Serializable {
	private static final long serialVersionUID = -8907264880213103337L;

	@JsonProperty("recommendations")
	private List<Recommendation> recommendations = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("recommendations")
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	@JsonProperty("symptomDefinitions")
	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
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
	@JsonPropertyOrder({ "id", "description", "action" })
	public static class Recommendation implements Serializable {
		private static final long serialVersionUID = 7776106897722096326L;

		@JsonProperty("id")
		private String id;

		@JsonProperty("description")
		private String description;

		@JsonProperty("action")
		private Action action;

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

		@JsonProperty("description")
		public String getDescription() {
			return description;
		}

		@JsonProperty("description")
		public void setDescription(String description) {
			this.description = description;
		}

		@JsonProperty("action")
		public Action getAction() {
			return action;
		}

		@JsonProperty("action")
		public void setAction(Action action) {
			this.action = action;
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
		@JsonPropertyOrder({ "actionAdapterKindId", "targetAdapterKindId", "targetResourceKindId", "targetMethod" })
		public static class Action implements Serializable {
			private static final long serialVersionUID = -3341905633826439983L;

			@JsonProperty("actionAdapterKindId")
			private String actionAdapterKindId;

			@JsonProperty("targetAdapterKindId")
			private String targetAdapterKindId;

			@JsonProperty("targetResourceKindId")
			private String targetResourceKindId;

			@JsonProperty("targetMethod")
			private String targetMethod;

			@JsonIgnore
			private transient Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("actionAdapterKindId")
			public String getActionAdapterKindId() {
				return actionAdapterKindId;
			}

			@JsonProperty("actionAdapterKindId")
			public void setActionAdapterKindId(String actionAdapterKindId) {
				this.actionAdapterKindId = actionAdapterKindId;
			}

			@JsonProperty("targetAdapterKindId")
			public String getTargetAdapterKindId() {
				return targetAdapterKindId;
			}

			@JsonProperty("targetAdapterKindId")
			public void setTargetAdapterKindId(String targetAdapterKindId) {
				this.targetAdapterKindId = targetAdapterKindId;
			}

			@JsonProperty("targetResourceKindId")
			public String getTargetResourceKindId() {
				return targetResourceKindId;
			}

			@JsonProperty("targetResourceKindId")
			public void setTargetResourceKindId(String targetResourceKindId) {
				this.targetResourceKindId = targetResourceKindId;
			}

			@JsonProperty("targetMethod")
			public String getTargetMethod() {
				return targetMethod;
			}

			@JsonProperty("targetMethod")
			public void setTargetMethod(String targetMethod) {
				this.targetMethod = targetMethod;
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
}
