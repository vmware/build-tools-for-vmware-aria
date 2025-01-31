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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "parameters" })
public class WorkflowParameters implements Serializable {
	private static final long serialVersionUID = 2431190111433401419L;

	@JsonProperty("parameters")
	private List<Parameter> parameters = new ArrayList<>();

	@JsonIgnore
	private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("parameters")
	public List<Parameter> getParameters() {
		return parameters;
	}

	@JsonProperty("parameters")
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@JsonAnyGetter
	public Map<java.lang.String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(java.lang.String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "name", "scope", "type", "value" })
	public static class Parameter implements Serializable {
		private static final long serialVersionUID = 3962662620392140309L;

		@JsonProperty("name")
		private java.lang.String name;

		@JsonProperty("scope")
		private java.lang.String scope;

		@JsonProperty("type")
		private java.lang.String type;

		@JsonProperty("value")
		private WorkflowParameterValue value;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("name")
		public java.lang.String getName() {
			return name;
		}

		@JsonProperty("name")
		public void setName(java.lang.String name) {
			this.name = name;
		}

		@JsonProperty("scope")
		public java.lang.String getScope() {
			return scope;
		}

		@JsonProperty("scope")
		public void setScope(java.lang.String scope) {
			this.scope = scope;
		}

		@JsonProperty("type")
		public java.lang.String getType() {
			return type;
		}

		@JsonProperty("type")
		public void setType(java.lang.String type) {
			this.type = type;
		}

		@JsonProperty("value")
		public WorkflowParameterValue getValue() {
			return value;
		}

		@JsonProperty("value")
		public void setValue(WorkflowParameterValue value) {
			this.value = value;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "value" })
	public static class String implements Serializable {
		private static final long serialVersionUID = 7995918737791424350L;

		@JsonProperty("value")
		private java.lang.String value;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("value")
		public java.lang.String getValue() {
			return value;
		}

		@JsonProperty("value")
		public void setValue(java.lang.String value) {
			this.value = StringEscapeUtils.unescapeJson(value);
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "value" })
	public static class Number implements Serializable {
		private static final long serialVersionUID = 7775118737791424297L;

		@JsonProperty("value")
		private java.lang.Integer value;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("value")
		public java.lang.Integer getValue() {
			return value;
		}

		@JsonProperty("value")
		public void setValue(java.lang.String value) {
			this.value = Integer.parseInt(value);
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "value" })
	public static class Bool implements Serializable {
		private static final long serialVersionUID = 7975771737791423937L;

		@JsonProperty("value")
		private java.lang.Boolean value;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("value")
		public java.lang.Boolean getValue() {
			return value;
		}

		@JsonProperty("value")
		public void setValue(java.lang.String value) {
			this.value = Boolean.parseBoolean(value);
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "boolean" })
	public static class BooleanValue implements WorkflowParameterValue {
		private static final long serialVersionUID = 3239758269209803112L;

		@JsonProperty("boolean")
		@SerializedName(value = "boolean")
		private WorkflowParameters.Bool bool;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("boolean")
		public WorkflowParameters.Bool getBoolean() {
			return bool;
		}

		@JsonProperty("boolean")
		public void setBoolean(WorkflowParameters.Bool bool) {
			this.bool = bool;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "number" })
	public static class NumberValue implements WorkflowParameterValue {
		private static final long serialVersionUID = 3534736269209803202L;

		@JsonProperty("number")
		private WorkflowParameters.Number number;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("number")
		public WorkflowParameters.Number getNumber() {
			return number;
		}

		@JsonProperty("number")
		public void setNumber(WorkflowParameters.Number number) {
			this.number = number;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "string" })
	public static class StringValue implements WorkflowParameterValue {
		private static final long serialVersionUID = 8624736269209802096L;

		@JsonProperty("string")
		private WorkflowParameters.String string;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("string")
		public WorkflowParameters.String getString() {
			return string;
		}

		@JsonProperty("string")
		public void setString(WorkflowParameters.String string) {
			this.string = string;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "array" })
	public static class ArrayStringValue implements WorkflowParameterValue {
		private static final long serialVersionUID = 8945736269209803399L;

		@JsonProperty("array")
		private WorkflowParameters.ArrayElements array;

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("array")
		public WorkflowParameters.ArrayElements getArray() {
			return array;
		}

		@JsonProperty("array")
		public void setArray(WorkflowParameters.ArrayElements array) {
			this.array = array;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "elements" })
	public static class ArrayElements implements Serializable {
		private static final long serialVersionUID = 8934736269209802297L;

		@JsonProperty("elements")
		private List<WorkflowParameters.StringValue> elements = new ArrayList<>();

		@JsonIgnore
		private transient Map<java.lang.String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("elements")
		public List<WorkflowParameters.StringValue> getElements() {
			return elements;
		}

		@JsonProperty("elements")
		public void setElements(List<WorkflowParameters.StringValue> elements) {
			this.elements = elements;
		}

		@JsonAnyGetter
		public Map<java.lang.String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(java.lang.String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}
}
