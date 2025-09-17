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
@JsonPropertyOrder({ "symptomDefinitions" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class SymptomDefinitionDTO implements Serializable {
	private static final long serialVersionUID = -8907264880213103337L;

	@JsonProperty("symptomDefinitions")
	private List<SymptomDefinition> symptomDefinitions = new ArrayList<>();

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("symptomDefinitions")
	public List<SymptomDefinition> getSymptomDefinitions() {
		return symptomDefinitions;
	}

	@JsonProperty("symptomDefinitions")
	public void setSymptomDefinitions(List<SymptomDefinition> symptomDefinitions) {
		this.symptomDefinitions = symptomDefinitions;
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
	@JsonPropertyOrder({ "id", "name", "adapterKindKey", "resourceKindKey", "waitCycles", "cancelCycles", "state" })
	public static class SymptomDefinition implements Serializable {
		private static final long serialVersionUID = -5587983216831252144L;

		@JsonProperty("id")
		private String id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("adapterKindKey")
		private String adapterKindKey;

		@JsonProperty("resourceKindKey")
		private String resourceKindKey;

		@JsonProperty("waitCycles")
		private Integer waitCycles;

		@JsonProperty("cancelCycles")
		private Integer cancelCycles;

		@JsonProperty("state")
		private State state;

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

		@JsonProperty("state")
		public State getState() {
			return state;
		}

		@JsonProperty("state")
		public void setState(State state) {
			this.state = state;
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
		@JsonPropertyOrder({ "severity", "condition" })
		public static class State implements Serializable {
			private static final long serialVersionUID = -1599769134473820197L;

			@JsonProperty("severity")
			private String severity;

			@JsonProperty("condition")
			private Condition condition;

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

			@JsonProperty("condition")
			public Condition getCondition() {
				return condition;
			}

			@JsonProperty("condition")
			public void setCondition(Condition condition) {
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
		@JsonPropertyOrder({ "type", "key", "operator", "value", "valueType", "instanced", "thresholdType", "stringValue", "eventType", "message", "faultKey" })
		@JsonIgnoreProperties({ "faultEvents", "instanced", "hardThresholdEventType", "statKey" })
		public static class Condition implements Serializable {
			private static final long serialVersionUID = 6996617212704194983L;

			@JsonProperty("type")
			private String type;

			@JsonProperty("key")
			private String key;

			@JsonProperty("operator")
			private String operator;

			@JsonInclude(value = JsonInclude.Include.CUSTOM,
				valueFilter = ConditionValueFilter.class)
			@JsonProperty("value")
			private String value;

			@JsonProperty("valueType")
			private String valueType;

			@JsonProperty("instanced")
			private Boolean instanced;

			@JsonInclude(value = JsonInclude.Include.NON_ABSENT)
			@JsonProperty("targetKey")
			private String targetKey;

			@JsonProperty("thresholdType")
			private String thresholdType;

			@JsonProperty("stringValue")
			private String stringValue;

			@JsonProperty("eventType")
			private String eventType;

			@JsonProperty("message")
			private String message;

			@JsonProperty("faultKey")
			private String faultKey;

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

			@JsonProperty("key")
			public String getKey() {
				return key;
			}

			@JsonProperty("key")
			public void setKey(String key) {
				this.key = key;
			}

			@JsonProperty("operator")
			public String getOperator() {
				return operator;
			}

			@JsonProperty("operator")
			public void setOperator(String operator) {
				this.operator = operator;
			}

			@JsonInclude(value = JsonInclude.Include.CUSTOM,
				valueFilter = ConditionValueFilter.class)
			@JsonProperty("value")
			public String getValue() {
				return value;
			}

			@JsonProperty("value")
			public void setValue(String value) {
				this.value = value;
			}

			@JsonProperty("valueType")
			public String getValueType() {
				return valueType;
			}

			@JsonProperty("valueType")
			public void setValueType(String valueType) {
				this.valueType = valueType;
			}

			@JsonProperty("instanced")
			public Boolean isInstanced() {
				return instanced;
			}

			@JsonProperty("instanced")
			public void setInstanced(Boolean instanced) {
				this.instanced = instanced;
			}

			@JsonInclude(value = JsonInclude.Include.NON_ABSENT)
			@JsonProperty("targetKey")
			public String getTargetKey() {
				return targetKey;
			}

			@JsonInclude(value = JsonInclude.Include.NON_ABSENT)
			@JsonProperty("targetKey")
			public void setTargetKey(String targetKey) {
				this.targetKey = targetKey;
			}

			@JsonProperty("thresholdType")
			public String getThresholdType() {
				return thresholdType;
			}

			@JsonProperty("thresholdType")
			public void setThresholdType(String thresholdType) {
				this.thresholdType = thresholdType;
			}

			@JsonProperty("stringValue")
			public String getStringValue() {
				return stringValue;
			}

			@JsonProperty("stringValue")
			public void setStringValue(String stringValue) {
				this.stringValue = stringValue;
			}

			@JsonProperty("eventType")
			public String getEventType() {
				return eventType;
			}

			@JsonProperty("eventType")
			public void setEventType(String eventType) {
				this.eventType = eventType;
			}

			@JsonProperty("message")
			public String getMessage() {
				return message;
			}

			@JsonProperty("message")
			public void setMessage(String message) {
				this.message = message;
			}

			@JsonProperty("faultKey")
			public String getFaultKey() {
				return faultKey;
			}

			@JsonProperty("faultKey")
			public void setFaultKey(String faultKey) {
				this.faultKey = faultKey;
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

class ConditionValueFilter {

	@Override
	public boolean equals(Object other) {
		return "null".equals(other);
	}
}
