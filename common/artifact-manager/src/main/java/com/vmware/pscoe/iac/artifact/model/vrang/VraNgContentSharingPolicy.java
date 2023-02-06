package com.vmware.pscoe.iac.artifact.model.vrang;

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

/**

*/
public class VraNgContentSharingPolicy {
	private String id;
	private String name;
	private String typeId;
	private String enforcementType;
	private String orgId;
	private String projectId;

	public VraNgContentSharingPolicy(String id, String name, String typeId, String sourceName, String enforcementType,
			String orgId, String projectId) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.enforcementType = enforcementType;
		this.orgId = orgId;
		this.projectId = projectId;
	}

	public VraNgContentSharingPolicy() {
	}

	/**
	 * Get the id of the content sharing policy.
	 * 
	 * @return content sharing policy entitlement id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of the content sharing policy.
	 * 
	 * @param id - id of the content sharing policy
	 * @return void
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the name of the content sharing policy.
	 * 
	 * @return content sharing policy name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the content sharing policy.
	 * 
	 * @param name - name of the content sharing policy
	 * @return void
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the typeId of the content sharing policy.
	 * 
	 * @return content sharing policy typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Set the typeId of the content sharing policy.
	 * 
	 * @param typeId - typeId of the content sharing policy
	 * @return void
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * Get the enforcementType of the content sharing policy.
	 * 
	 * @return content sharing policy enforcementType
	 */
	public String getEnforcementType() {
		return enforcementType;
	}

	/**
	 * Set the enforcementType of the content sharing policy.
	 * 
	 * @param enforcementType - enforcementType of the content sharing policy
	 * @return void
	 */
	public void setEnforcementType(String enforcementType) {
		this.enforcementType = enforcementType;
	}

	/**
	 * Get the orgId of the content sharing policy.
	 * 
	 * @return content sharing policy orgId
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Set the orgId of the content sharing policy.
	 * 
	 * @param orgId - orgId of the content sharing policy
	 * @return void
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * Get the projectId of the content sharing policy.
	 * 
	 * @return content sharing policy projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Set the projectId of the content sharing policy.
	 * 
	 * @param projectId - projectId of the content sharing policy
	 * @return void
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
