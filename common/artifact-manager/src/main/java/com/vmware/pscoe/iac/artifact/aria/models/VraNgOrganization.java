
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

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;

/**
 * Model for Organizations in Aria.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "refLink" })
public class VraNgOrganization implements Serializable {
	private static final long serialVersionUID = -3313748896114761975L;

	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("refLink")
	private String refLink;

	/**
	 * @return the project id
	 */
	@JsonProperty("id")
	public String getId() {
		if (id == null) {
			this.id = refLink.substring(refLink.lastIndexOf("/") + 1);
		}
		return id;
	}

	/**
	 * @param id - the project id to set
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name of the project
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * @param name - the project name to set
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the reference link of the project
	 */
	public String getRefLink() {
		return refLink;
	}

	/**
	 * @param refLink - the reference link of the project to set
	 */
	public void setRefLink(String refLink) {
		this.refLink = refLink;
	}
}
