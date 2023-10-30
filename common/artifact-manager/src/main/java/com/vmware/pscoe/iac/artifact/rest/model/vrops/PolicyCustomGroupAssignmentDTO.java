package com.vmware.pscoe.iac.artifact.rest.model.vrops;

/*-
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

import java.io.Serializable;
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
@JsonPropertyOrder({ "id", "groups" })
public class PolicyCustomGroupAssignmentDTO implements Serializable {
	private static final long serialVersionUID = -8042756118741033913L;

	/**
	 * id.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * groups.
	 */
	@JsonProperty("groups")
	private List<String> groups;

	/**
	 * additionalProperties.
	 */
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	/**
	 * getId().
	 * 
	 * @return policy id.
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * setId().
	 * 
	 * @param id policy id to be set.
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * getGroups().
	 * 
	 * @return list of assigned groups.
	 */
	@JsonProperty("groups")
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * setGroups().
	 * 
	 * @param groups to be assigned policy to.
	 */
	@JsonProperty("groups")
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * getAdditionalProperties().
	 * 
	 * @return map of additional properties.
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * setAdditionalProperties().
	 * 
	 * @param name the name of the property.
	 * @param value the value of the property.
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
