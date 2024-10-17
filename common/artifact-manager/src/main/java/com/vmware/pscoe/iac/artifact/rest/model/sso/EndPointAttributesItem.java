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
package com.vmware.pscoe.iac.artifact.rest.model.sso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndPointAttributesItem {

	@JsonProperty("@type")
	private String type;

	@JsonProperty("id")
	private String id;

	@JsonProperty("value")
	private String value;

	@JsonProperty("key")
	private String key;

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "EndPointAttributesItem{" + "@type = '" + type + '\'' + ",id = '" + id + '\'' + ",value = '" + value + '\'' + ",key = '" + key + '\'' + "}";
	}
}
