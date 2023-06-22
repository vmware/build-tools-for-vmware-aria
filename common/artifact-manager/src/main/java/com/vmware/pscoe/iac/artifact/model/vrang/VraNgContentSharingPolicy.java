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
	/**
	 * Id of the Content Sharing Policy.
	 */
	private String id;
	/**
	 * Name of the Content Sharing Policy.
	 */
	private String name;
	/**
	 * TypeId of the Content Sharing Policy.
	 */
	private String typeId;
	/**
	 * ProjectId of the Content Sharing Policy.
	 */
	private String projectId;
	/**
	 * OrgId of the Content Sharing Policy.
	 */
	private String orgId;
	/**
	 * EnforcementType of the Content Sharing Policy.
	 */
	private String enforcementType;
	/**
	 * Description of the Content Sharing Policy.
	 */
	private String description;
	/**
	 * Definition of the Content Sharing Policy.
	 */
	private VraNgDefinition definition;

	/**
	 * Constructor VraNgContentSharingPolicy.
	 */
	public VraNgContentSharingPolicy() {
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
	public VraNgContentSharingPolicy(final String idIn, final String nameIn, final String typeIdIn,
			final String projectIdIn, final String orgIdIn,
			final String enforcementTypeIn, final String descriptionIn,
			final VraNgDefinition definitionIn) {
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
	 * @param nameIn - name of the content sharing policy
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
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
	 * Get the id of the content sharing policy org.
	 * 
	 * @return content sharing policy org id
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

	/**
	 * Get the definition of the content sharing policy.
	 * 
	 * @return content sharing policy definition
	 */
	public VraNgDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the content sharing policy.
	 * 
	 * @param definitionIn - definition of the content sharing policy
	 */
	public void setDefinition(final VraNgDefinition definitionIn) {
		this.definition = definitionIn;
	}
}
