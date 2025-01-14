
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
package com.vmware.pscoe.iac.artifact.aria.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * VraNgWorkflow is a model class.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "version", "integration" })
public class VraNgWorkflow implements Serializable, Identifiable {
	/**
	 * @param serialVersionUID default serial version id
	 */
	private static final long serialVersionUID = 2298605860128696224L;

	/**
	 * default id.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * default name.
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * default version.
	 */
	@JsonProperty("version")
	private String version;

	/**
	 * default integration.
	 */
	@JsonProperty("integration")
	private VraNgIntegration integration;

	/**
	 * default additionalProperties.
	 */
	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * @return id the id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return version
	 */
	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return integration
	 */
	@JsonProperty("integration")
	public VraNgIntegration getIntegration() {
		return integration;
	}

	/**
	 * @param integration the integration to set
	 */
	@JsonProperty("integration")
	public void setIntegration(VraNgIntegration integration) {
		this.integration = integration;
	}

	/**
	 * @return additionalProperties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * @param name  the name
	 * @param value the value
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
