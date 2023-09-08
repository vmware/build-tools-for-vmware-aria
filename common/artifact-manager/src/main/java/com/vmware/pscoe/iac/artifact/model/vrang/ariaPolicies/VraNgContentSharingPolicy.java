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

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;

/**

*/
public class VraNgContentSharingPolicy extends VraNgPolicyBase {
	/**
	 * Id of the Content Sharing Policy.
	 */
	private String id;
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
	 * Definition of the Content Sharing Policy.
	 */
	private VraNgDefinition definition;

	/**
	 * Constructor VraNgContentSharingPolicy.
	 */
	public VraNgContentSharingPolicy() {
		super();
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
		super(typeIdIn, nameIn, descriptionIn);

		this.id = idIn;
		this.projectId = projectIdIn;
		this.orgId = orgIdIn;
		this.enforcementType = enforcementTypeIn;
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
