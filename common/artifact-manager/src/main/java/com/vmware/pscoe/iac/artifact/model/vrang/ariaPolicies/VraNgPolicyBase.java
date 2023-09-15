package com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies;

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

public abstract class VraNgPolicyBase {
	/**
	 * Id of the Content Sharing Policy.
	 */
	private String id;
	/**
	 * TypeId of the Content Sharing Policy.
	 */
	private String typeId;
	/**
	 * Name of the Content Sharing Policy.
	 */
	private String name;
	/**
	 * EnforcementType of the Content Sharing Policy.
	 */
	private String enforcementType;
	/**
	 * OrgId of the Content Sharing Policy.
	 */
	private String orgId;
	/**
	 * ProjectId of the Content Sharing Policy.
	 */
	private String projectId;
	/**
	 * Description of the Content Sharing Policy.
	 */
	private String description;

	public VraNgPolicyBase() {
	}

	public VraNgPolicyBase(String idIn,
						   String typeId,
						   String name,
						   final String enforcementTypeIn,
						   String orgIdIn,
						   String projectIdIn,
						   String description) {
		this.id = idIn;
		this.typeId = typeId;
		this.name = name;
		this.enforcementType = enforcementTypeIn;
		this.orgId = orgIdIn;
		this.projectId = projectIdIn;
		this.description = description;
	}

	/**
	 * Get the name of the content sharing policy.
	 *
	 * @return content sharing policy id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of the content sharing policy.
	 *
	 * @param idIn - id of the content sharing policy
	 */
	public void setId(final String idIn) {
		this.id = idIn;
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
	 * @param typeIdIn - typeId of the content sharing policy
	 */
	public void setTypeId(final String typeIdIn) {
		this.typeId = typeIdIn;
	}

	/**
	 * Get the name of the policy.
	 *
	 * @return policy name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the policy.
	 *
	 * @param nameIn - name of the policy
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
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
	 * @param enforcementTypeIn - enforcementType of the content sharing policy
	 */
	public void setEnforcementType(final String enforcementTypeIn) {
		this.enforcementType = enforcementTypeIn;
	}

	/**
	 * Get the id of the policy org.
	 *
	 * @return policy org id
	 */
	public String getOrgId() {
		return this.orgId;
	}

	/**
	 * Set the orgId of the content sharing policy.
	 *
	 * @param orgIdIn - Org id of the content sharing policy
	 */
	public void setOrgId(final String orgIdIn) {
		this.orgId = orgIdIn;
	}

	/**
	 * Get the name of the content sharing policy.
	 *
	 * @return content sharing policy name
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Set the projectId of the content sharing policy.
	 *
	 * @param projectIdIn - project id of the content sharing policy
	 */
	public void setProjectId(final String projectIdIn) {
		this.projectId = projectIdIn;
	}

	/**
	 * Get the description of the content sharing policy.
	 *
	 * @return content sharing policy description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the content sharing policy.
	 *
	 * @param descriptionIn - description of the content sharing policy
	 */
	public void setDescription(final String descriptionIn) {
		this.description = descriptionIn;
	}
}
