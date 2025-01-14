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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VraNgCatalogEntitlementDto is used to store the entitlement details of the
 * VraNgCatalog.
 */
public class VraNgCatalogEntitlementDto {
	private String id;
	private String projectId;
	private Map<String, String> definition = new LinkedHashMap<String, String>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the project id
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the definition
	 */
	public Map<String, String> getDefinition() {
		return definition;
	}

	/**
	 * @param definition to set
	 */
	public void setDefinition(Map<String, String> definition) {
		this.definition = definition;
	}
}
