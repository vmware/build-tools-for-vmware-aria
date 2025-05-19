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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

/**
 * Base policy interface
 */
public interface VraNgPolicyDTO extends Identifiable {

	/**
	 * Setter for policy ID
	 * 
	 * @param id - policy ID
	 */
	void setId(String id);

	/**
	 * Setter for policy name
	 * 
	 * @param name - policy name
	 */
	void setName(String name);
	/**
	 * @return the type ID
	 */
	String getTypeId();

	/**
	 * @return the organization's ID
	 */
	String getOrgId();
	
	/**
	 * Setter for organization ID
	 * 
	 * @param orgId - organization ID
	 */
	void setOrgId(String orgId);

	/**
	 * @return the project's ID
	 */
	String getProjectId();

	/**
	 * Setter for project ID
	 * 
	 * @param projectId - project ID
	 */
	void setProjectId(String projectId);
}
