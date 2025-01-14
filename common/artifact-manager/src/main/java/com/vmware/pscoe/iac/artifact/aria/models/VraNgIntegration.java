
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
 * Aria Automation > Infrastructure > Integration implementation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "endpointUri", "endpointConfigurationLink" })
public class VraNgIntegration implements Serializable {
	private static final long serialVersionUID = 6323872416192310200L;

	@JsonProperty("name")
	private String name;

	@JsonProperty("endpointUri")
	private String endpointUri;

	@JsonProperty("endpointConfigurationLink")
	private String endpointConfigurationLink;

	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * @param the name of the integration
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * @param name - the integration name to set
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the endpoint of the integration
	 */
	@JsonProperty("endpointUri")
	public String getEndpointUri() {
		return endpointUri;
	}

	/**
	 * @param endpointUri - the endpoint uri to set
	 */
	@JsonProperty("endpointUri")
	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	/**
	 * @return the endpoint configuration link
	 */
	@JsonProperty("endpointConfigurationLink")
	public String getEndpointConfigurationLink() {
		return endpointConfigurationLink;
	}

	/**
	 * @param endpointConfigurationLink - the endpoint config link
	 */
	@JsonProperty("endpointConfigurationLink")
	public void setEndpointConfigurationLink(String endpointConfigurationLink) {
		this.endpointConfigurationLink = endpointConfigurationLink;
	}

	/**
	 * @return a map of the additional properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * @param name  - name of an additional property to set
	 * @param value - the value of the additional prop to add
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
