
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
package com.vmware.pscoe.iac.artifact.aria.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("endpointUri")
	public String getEndpointUri() {
		return endpointUri;
	}

	@JsonProperty("endpointUri")
	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@JsonProperty("endpointConfigurationLink")
	public String getEndpointConfigurationLink() {
		return endpointConfigurationLink;
	}

	@JsonProperty("endpointConfigurationLink")
	public void setEndpointConfigurationLink(String endpointConfigurationLink) {
		this.endpointConfigurationLink = endpointConfigurationLink;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
