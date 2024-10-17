
package com.vmware.pscoe.iac.artifact.model.abx;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "shared", "inputSecrets", "inputConstants", "inputs" })
public class Abx {
	@JsonProperty("shared")
	private Boolean shared;

	@JsonProperty("inputSecrets")
	private List<String> inputSecrets;

	@JsonProperty("inputConstants")
	private List<String> inputConstants;

	@JsonProperty("inputs")
	private Map<String, Object> inputs = new LinkedHashMap<>();

	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<>();

	/**
	 * getShared().
	 * 
	 * @return shared
	 */
	@JsonProperty("shared")
	public Boolean getShared() {
		return shared;
	}

	/**
	 * setShared().
	 * 
	 * @param shared shared flag to be set.
	 */
	@JsonProperty("shared")
	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	/**
	 * getInputSecrets().
	 * 
	 * @return inputSecrets
	 */
	@JsonProperty("inputSecrets")
	public List<String> getInputSecrets() {
		return inputSecrets;
	}

	/**
	 * setInputSecrets().
	 * 
	 * @param inputSecrets inputSecrets to be set.
	 */
	@JsonProperty("inputSecrets")
	public void setInputSecrets(List<String> inputSecrets) {
		this.inputSecrets = inputSecrets;
	}

	/**
	 * getInputConstants().
	 * 
	 * @return inputConstants
	 */
	@JsonProperty("inputConstants")
	public List<String> getInputConstants() {
		return inputConstants;
	}

	/**
	 * setInputConstants().
	 * 
	 * @param inputConstants inputConstants to be set.
	 */
	@JsonProperty("inputConstants")
	public void setInputConstants(List<String> inputConstants) {
		this.inputConstants = inputConstants;
	}

	/**
	 * getInputs().
	 * 
	 * @return inputs
	 */
	@JsonProperty("inputs")
	public Map<String, Object> getInputs() {
		return inputs;
	}

	/**
	 * setInputs().
	 * 
	 * @param inputs inputs to be set.
	 */
	@JsonProperty("inputs")
	public void setInputs(Map<String, Object> inputs) {
		this.inputs = inputs;
	}

	/**
	 * getAdditionalProperties().
	 * 
	 * @return additionalProperties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * setAdditionalProperty().
	 * 
	 * @param name  name to be set.
	 * @param value name to be set.
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
