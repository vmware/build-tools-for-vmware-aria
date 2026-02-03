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
@JsonPropertyOrder({ "alertDefinitions", "pageInfo", "links" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class AlertDefinitionDTO implements Serializable {
	private static final long serialVersionUID = -8907264880213106337L;

	@JsonProperty("alertDefinitions")
	private List<AlertDefinition> alertDefinitions = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("alertDefinitions")
	public List<AlertDefinition> getAlertDefinitions() {
		return alertDefinitions;
	}

	@JsonProperty("alertDefinitions")
	public void setAlertDefinitions(List<AlertDefinition> alertDefinitions) {
		this.alertDefinitions = alertDefinitions;
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
	@JsonPropertyOrder({ "id", "name", "description", "adapterKindKey", "resourceKindKey", "waitCycles", "cancelCycles", "type", "subType", "states" })
	public static class AlertDefinition implements Serializable {
		private static final long serialVersionUID = -5556208274143439935L;

		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("description")
		private String description;

		@JsonProperty("adapterKindKey")
		private String adapterKindKey;

		@JsonProperty("resourceKindKey")
		private String resourceKindKey;

		@JsonProperty("waitCycles")
		private Integer waitCycles;

		@JsonProperty("cancelCycles")
		private Integer cancelCycles;

		@JsonProperty("type")
		private Integer type;

		@JsonProperty("subType")
		private Integer subType;

		@JsonProperty("states")
		private List<State> states = new ArrayList<>();

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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
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

		@JsonProperty("waitCycles")
		public Integer getWaitCycles() {
			return waitCycles;
		}

		@JsonProperty("waitCycles")
		public void setWaitCycles(Integer waitCycles) {
			this.waitCycles = waitCycles;
		}

		@JsonProperty("cancelCycles")
		public Integer getCancelCycles() {
			return cancelCycles;
		}

		@JsonProperty("cancelCycles")
		public void setCancelCycles(Integer cancelCycles) {
			this.cancelCycles = cancelCycles;
		}

		@JsonProperty("type")
		public Integer getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(Integer type) {
			this.type = type;
		}

		@JsonProperty("subType")
		public Integer getSubType() {
			return subType;
		}

		@JsonProperty("subType")
		public void setSubType(Integer subType) {
			this.subType = subType;
		}

		@JsonProperty("states")
		public List<State> getStates() {
			return states;
		}

		@JsonProperty("states")
		public void setStates(List<State> states) {
			this.states = states;
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
		@JsonPropertyOrder({ "waitCycles", "cancelCycles", "severity", "condition" })
		public static class AlertCondition implements Serializable {
			private static final long serialVersionUID = 2188722915313653545L;
			
			@JsonProperty("id")
			private String id;

			@JsonProperty("waitCycles")
			private Integer waitCycles;

			@JsonProperty("cancelCycles")
			private Integer cancelCycles;

			@JsonProperty("severity")
			private String severity;

			@JsonProperty("condition")
			private SymptomDefinitionDTO.SymptomDefinition.Condition condition;

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
			
			@JsonProperty("waitCycles")
			public Integer getWaitCycles() {
				return waitCycles;
			}

			@JsonProperty("waitCycles")
			public void setWaitCycles(Integer waitCycles) {
				this.waitCycles = waitCycles;
			}

			@JsonProperty("cancelCycles")
			public Integer getCancelCycles() {
				return cancelCycles;
			}

			@JsonProperty("cancelCycles")
			public void setCancelCycles(Integer cancelCycles) {
				this.cancelCycles = cancelCycles;
			}

			@JsonProperty("severity")
			public String getSeverity() {
				return severity;
			}

			@JsonProperty("severity")
			public void setSeverity(String severity) {
				this.severity = severity;
			}

			@JsonProperty("condition")
			public SymptomDefinitionDTO.SymptomDefinition.Condition getCondition() {
				return condition;
			}

			@JsonProperty("condition")
			public void setCondition(SymptomDefinitionDTO.SymptomDefinition.Condition condition) {
				this.condition = condition;
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
		@JsonPropertyOrder({ "type", "relation", "aggregation", "symptomSetOperator", "symptomDefinitionIds", "symptomSets", "alertConditions" })
		public static class BaseSymptomSet implements Serializable {
			private static final long serialVersionUID = 7061962708255866132L;

			@JsonProperty("type")
			private String type;

			@JsonProperty("relation")
			private String relation;

			@JsonProperty("aggregation")
			private String aggregation;

			@JsonProperty("symptomSetOperator")
			private String symptomSetOperator;

			@JsonProperty("symptomDefinitionIds")
			private List<String> symptomDefinitionIds = new ArrayList<>();

			@JsonProperty("symptomSets")
			private List<SymptomSet> symptomSets = new ArrayList<>();

			@JsonProperty("alertConditions")
			private List<AlertCondition> alertConditions = new ArrayList<>();

			@JsonIgnore
			private transient Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("type")
			public String getType() {
				return type;
			}

			@JsonProperty("type")
			public void setType(String type) {
				this.type = type;
			}

			@JsonProperty("relation")
			public String getRelation() {
				return relation;
			}

			@JsonProperty("relation")
			public void setRelation(String relation) {
				this.relation = relation;
			}

			@JsonProperty("aggregation")
			public String getAggregation() {
				return aggregation;
			}

			@JsonProperty("aggregation")
			public void setAggregation(String aggregation) {
				this.aggregation = aggregation;
			}

			@JsonProperty("symptomSetOperator")
			public String getSymptomSetOperator() {
				return symptomSetOperator;
			}

			@JsonProperty("symptomSetOperator")
			public void setSymptomSetOperator(String symptomSetOperator) {
				this.symptomSetOperator = symptomSetOperator;
			}

			@JsonProperty("symptomDefinitionIds")
			public List<String> getSymptomDefinitionIds() {
				return symptomDefinitionIds;
			}

			@JsonProperty("symptomDefinitionIds")
			public void setSymptomDefinitionIds(List<String> symptomDefinitionIds) {
				this.symptomDefinitionIds = symptomDefinitionIds;
			}

			@JsonProperty("symptomSets")
			public List<SymptomSet> getSymptomSets() {
				return symptomSets;
			}

			@JsonProperty("symptomSets")
			public void setSymptomSets(List<SymptomSet> symptomSets) {
				this.symptomSets = symptomSets;
			}

			@JsonProperty("alertConditions")
			public List<AlertCondition> getAlertConditions() {
				return alertConditions;
			}

			@JsonProperty("alertConditions")
			public void setAlertConditions(List<AlertCondition> alertConditions) {
				this.alertConditions = alertConditions;
			}

			@JsonAnySetter
			public void setAdditionalProperties(Map<String, Object> additionalProperties) {
				this.additionalProperties = additionalProperties;
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
		@JsonPropertyOrder({ "type", "relation", "aggregation", "symptomSetOperator", "symptomDefinitionIds", "alertConditions" })
		public static class SymptomSet implements Serializable {
			private static final long serialVersionUID = 7061962708255866132L;

			@JsonProperty("type")
			private String type;

			@JsonProperty("relation")
			private String relation;

			@JsonProperty("aggregation")
			private String aggregation;

			@JsonProperty("symptomSetOperator")
			private String symptomSetOperator;

			@JsonProperty("symptomDefinitionIds")
			private List<String> symptomDefinitionIds = new ArrayList<>();

			@JsonProperty("alertConditions")
			private List<AlertCondition> alertConditions = new ArrayList<>();

			@JsonIgnore
			private transient Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("type")
			public String getType() {
				return type;
			}

			@JsonProperty("type")
			public void setType(String type) {
				this.type = type;
			}

			@JsonProperty("relation")
			public String getRelation() {
				return relation;
			}

			@JsonProperty("relation")
			public void setRelation(String relation) {
				this.relation = relation;
			}

			@JsonProperty("aggregation")
			public String getAggregation() {
				return aggregation;
			}

			@JsonProperty("aggregation")
			public void setAggregation(String aggregation) {
				this.aggregation = aggregation;
			}

			@JsonProperty("symptomSetOperator")
			public String getSymptomSetOperator() {
				return symptomSetOperator;
			}

			@JsonProperty("symptomSetOperator")
			public void setSymptomSetOperator(String symptomSetOperator) {
				this.symptomSetOperator = symptomSetOperator;
			}

			@JsonProperty("symptomDefinitionIds")
			public List<String> getSymptomDefinitionIds() {
				return symptomDefinitionIds;
			}

			@JsonProperty("symptomDefinitionIds")
			public void setSymptomDefinitionIds(List<String> symptomDefinitionIds) {
				this.symptomDefinitionIds = symptomDefinitionIds;
			}

			@JsonProperty("alertConditions")
			public List<AlertCondition> getAlertConditions() {
				return alertConditions;
			}

			@JsonProperty("alertConditions")
			public void setAlertConditions(List<AlertCondition> alertConditions) {
				this.alertConditions = alertConditions;
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
		@JsonPropertyOrder({ "impactType", "detail" })
		public static class Impact implements Serializable {
			private static final long serialVersionUID = 8675023498733072632L;

			@JsonProperty("impactType")
			private String impactType;

			@JsonProperty("detail")
			private String detail;

			@JsonIgnore
			private transient Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("impactType")
			public String getImpactType() {
				return impactType;
			}

			@JsonProperty("impactType")
			public void setImpactType(String impactType) {
				this.impactType = impactType;
			}

			@JsonProperty("detail")
			public String getDetail() {
				return detail;
			}

			@JsonProperty("detail")
			public void setDetail(String detail) {
				this.detail = detail;
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
		@JsonPropertyOrder({ "severity", "base-symptom-set", "impact", "recommendationPriorityMap" })
		@JsonIgnoreProperties({ "recommendationPriorityMap" })
		public static class State implements Serializable {
			private static final long serialVersionUID = -4167387367232110038L;

			@JsonProperty("severity")
			private String severity;

			@JsonProperty("base-symptom-set")
			private BaseSymptomSet baseSymptomSet;

			@JsonProperty("impact")
			private Impact impact;

			@JsonIgnore
			private transient Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("severity")
			public String getSeverity() {
				return severity;
			}

			@JsonProperty("severity")
			public void setSeverity(String severity) {
				this.severity = severity;
			}

			@JsonProperty("base-symptom-set")
			public BaseSymptomSet getBaseSymptomSet() {
				return baseSymptomSet;
			}

			@JsonProperty("base-symptom-set")
			public void setBaseSymptomSet(BaseSymptomSet baseSymptomSet) {
				this.baseSymptomSet = baseSymptomSet;
			}

			@JsonProperty("impact")
			public Impact getImpact() {
				return impact;
			}

			@JsonProperty("impact")
			public void setImpact(Impact impact) {
				this.impact = impact;
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
