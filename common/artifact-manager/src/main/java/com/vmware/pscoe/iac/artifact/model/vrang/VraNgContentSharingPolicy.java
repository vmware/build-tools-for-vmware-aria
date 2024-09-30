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
package com.vmware.pscoe.iac.artifact.model.vrang;

/**

*/
public class VraNgContentSharingPolicy implements Identifiable {
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
	 * Scope of the content sharing policy. The scope is a project name.
	 */
	private String scope;
	/**
	 * Organization for the content sharing policy.
	 */
	private String organization;
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
	 * @param id              Id
	 * @param name            Name
	 * @param typeId          Typeid
	 * @param projectId       ProjectId
	 * @param orgId           OrgId
	 * @param enforcementType enforcementType
	 * @param description     description
	 * @param definition      definition
	 * @param scope           scope
	 * @param organization    organization name
	 */
	public VraNgContentSharingPolicy(final String id, final String name, final String typeId, final String projectId,
			final String orgId,
			final String enforcementType, final String description, final VraNgDefinition definition,
			final String scope, final String organization) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.projectId = projectId;
		this.scope = scope;
		this.orgId = orgId;
		this.enforcementType = enforcementType;
		this.description = description;
		this.definition = definition;
		this.organization = organization;
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
	 * Get the scope of the content policy which is a vRA project name.
	 * 
	 * @return content sharing scope project name.
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Set the scope of the content sharing policy (a vRA project name).
	 * 
	 * @param scope - scope of the content sharing policy (should be a vRA project
	 *              name).
	 */
	public void setScope(String scope) {
		this.scope = scope;
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
	 * Get the organization name for the content sharing policy.
	 *
	 * @return organization name for the content sharing policy.
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Set the organization name for the content sharing policy.
	 * 
	 * @param organization - Organization name for the content sharing policy
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
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
