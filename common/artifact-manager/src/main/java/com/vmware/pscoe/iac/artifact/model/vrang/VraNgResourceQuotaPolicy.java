package com.vmware.pscoe.iac.artifact.model.vrang;

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

public class VraNgResourceQuotaPolicy {
	

	/**
	 * Id of the Resource Quota Policy.
	 */
	private String id;
	/**
	 * Name of the Resource Quota Policy.
	 */
	private String name;
	/**
	 * TypeId of the Resource Quota Policy.
	 */
	private String typeId;
	/**
	 * ProjectId of the Resource Quota Policy.
	 */
	private String projectId;
	/**
	 * OrgId of the Resource Quota Policy.
	 */
	private String orgId;
	/**
	 * EnforcementType of the Resource Quota Policy.
	 */
	private String enforcementType;
	/**
	 * Description of the Resource Quota Policy.
	 */
	private String description;
	/**
	 * Definition of the Resource Quota Policy.
	 */
	private VraNgResourceQuota quotas;
	/**
	 * Definition of the Resource Quota Policy.
	 */
	private VraNgResourceQuotaDefinition definition;


	/**
	 * Constructor VraNgResourceQuotaPolicy.
	 */
	public VraNgResourceQuotaPolicy() {
	}

	/**
	 * Constructor.
	 * 
	 * @param idIn              Id
	 * @param nameIn            Name
	 * @param typeIdIn          Typeid
	 * @param projectIdIn       ProjectId
	 * @param orgIdIn           OrgId
	 * @param enforcementTypeIn enforcementType
	 * @param descriptionIn     description
	 * @param definitionIn      definition
	 */
	public VraNgResourceQuotaPolicy(final String idIn, final String nameIn, final String typeIdIn,
			final String projectIdIn, final String orgIdIn,
			final String enforcementTypeIn, final String descriptionIn,
			final VraNgResourceQuotaDefinition definitionIn) {
		this.id = idIn;
		this.name = nameIn;
		this.typeId = typeIdIn;
		this.projectId = projectIdIn;
		this.orgId = orgIdIn;
		this.enforcementType = enforcementTypeIn;
		this.description = descriptionIn;
		this.definition = definitionIn;
	}

	/**
	 * Get the name of the resource quota policy.
	 * 
	 * @return content sharing policy id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of the resource quota policy.
	 * 
	 * @param idIn - id of the resource quota policy
	 */
	public void setId(final String idIn) {
		this.id = idIn;
	}

	/**
	 * Get the name of the resource quota policy.
	 * 
	 * @return content sharing policy name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the resource quota policy.
	 * 
	 * @param nameIn - name of the resource quota policy
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Get the name of the resource quota policy.
	 * 
	 * @return content sharing policy name
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Set the projectId of the resource quota policy.
	 * 
	 * @param projectIdIn - project id of the resource quota policy
	 */
	public void setProjectId(final String projectIdIn) {
		this.projectId = projectIdIn;
	}

	/**
	 * Get the id of the resource quota policy org.
	 * 
	 * @return content sharing policy org id
	 */
	public String getOrgId() {
		return this.orgId;
	}

	/**
	 * Set the orgId of the resource quota policy.
	 * 
	 * @param orgIdIn - Org id of the resource quota policy
	 */
	public void setOrgId(final String orgIdIn) {
		this.orgId = orgIdIn;
	}

	/**
	 * Get the typeId of the resource quota policy.
	 * 
	 * @return content sharing policy typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Set the typeId of the resource quota policy.
	 * 
	 * @param typeIdIn - typeId of the resource quota policy
	 */
	public void setTypeId(final String typeIdIn) {
		this.typeId = typeIdIn;
	}

	/**
	 * Get the enforcementType of the resource quota policy.
	 * 
	 * @return content sharing policy enforcementType
	 */
	public String getEnforcementType() {
		return enforcementType;
	}

	/**
	 * Set the enforcementType of the resource quota policy.
	 * 
	 * @param enforcementTypeIn - enforcementType of the resource quota policy
	 */
	public void setEnforcementType(final String enforcementTypeIn) {
		this.enforcementType = enforcementTypeIn;
	}

	/**
	 * Get the description of the resource quota policy.
	 * 
	 * @return content sharing policy description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the resource quota policy.
	 * 
	 * @param descriptionIn - description of the resource quota policy
	 */
	public void setDescription(final String descriptionIn) {
		this.description = descriptionIn;
	}

	/**
	 * Get the definition of the resource quota policy.
	 * 
	 * @return content sharing policy definition
	 */
	public VraNgResourceQuotaDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the resource quota policy.
	 * 
	 * @param definitionIn - definition of the resource quota policy
	 */
	public void setDefinition(final VraNgResourceQuotaDefinition definitionIn) {
		this.definition = definitionIn;
	}
}
