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

import com.google.gson.JsonObject;

/**

*/
public class VraNgLeasePolicy implements Identifiable {

	/**
	 * Unique identifier for the policy.
	 */
	private String id;

	/**
	 * Name of the policy.
	 */
	private String name;

	/**
	 * Unique identifier for the policy type.
	 */
	private String typeId;

	/**
	 * Unique identifier for the project.
	 */
	private String projectId;

	/**
	 * Unique identifier for the organization.
	 */
	private String orgId;

	/**
	 * Enforcement type for the policy.
	 */
	private String enforcementType;

	/**
	 * Description of the policy.
	 */
	private String description;

	/**
	 * Definition of the policy.
	 */
	private JsonObject definition;
	/**
	 * Criteria of the policy.
	 */
	private JsonObject criteria;
	/**
	 * Scope criteria of the policy.
	 */
	private JsonObject scopeCriteria;

	/**
	 * Constructs a new instance of the VraNgLeasePolicy with the given parameters.
	 *
	 * @param idIn              The unique identifier for the lease policy.
	 * @param nameIn            The name of the lease policy
	 * @param typeIdIn          The unique for the lease policy.
	 * @param projectIdIn       The unique for the project associated with the lease
	 *                          policy
	 * @param orgIdIn           The unique identifier for the organization
	 *                          associated with the lease policy.
	 * @param enforcementTypeIn The type of enforcement for the lease policy.
	 * @param descriptionIn     A description of the lease policy.
	 * @param definitionIn      The definition of the lease policy.
	 * @param criteriaIn        the criteria of the lease policy.
	 * @param scopeCriteriaIn   the scope criteria of the lease policy
	 */
	public VraNgLeasePolicy(final String idIn, final String nameIn, final String typeIdIn,
			final String projectIdIn, final String orgIdIn,
			final String enforcementTypeIn, final String descriptionIn,
			final JsonObject definitionIn, final JsonObject criteriaIn, final JsonObject scopeCriteriaIn) {
		this.id = idIn;
		this.name = nameIn;
		this.typeId = typeIdIn;
		this.projectId = projectIdIn;
		this.orgId = orgIdIn;
		this.enforcementType = enforcementTypeIn;
		this.description = descriptionIn;
		this.definition = definitionIn;
		this.criteria = criteriaIn;
		this.scopeCriteria = scopeCriteriaIn;
	}

	/**
	 * Returns the unique identifier for this lease policy.
	 *
	 * @return The unique identifier for this lease policy.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique identifier for this lease policy.
	 *
	 * @param idIn The new unique identifier for this lease policy.
	 */
	public void setId(final String idIn) {
		this.id = idIn;
	}

	/**
	 * Returns the name of this lease policy.
	 *
	 * @return The name of this lease policy.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this lease policy to the given value.
	 *
	 * @param nameIn The new name for this lease policy.
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Returns the project ID associated with this lease policy.
	 *
	 * @return The project ID as a string.
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Set the projectId of the lease policy.
	 * 
	 * @param projectIdIn - project id of the lease policy
	 */
	public void setProjectId(final String projectIdIn) {
		this.projectId = projectIdIn;
	}

	/**
	 * Get the id of the lease policy org.
	 * 
	 * @return lease policy org id
	 */
	public String getOrgId() {
		return this.orgId;
	}

	/**
	 * Set the orgId of the lease policy.
	 * 
	 * @param orgIdIn - Org id of the lease policy
	 */
	public void setOrgId(final String orgIdIn) {
		this.orgId = orgIdIn;
	}

	/**
	 * Get the typeId of the lease policy.
	 * 
	 * @return lease policy typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Set the typeId of the lease policy.
	 * 
	 * @param typeIdIn - typeId of the lease policy
	 */
	public void setTypeId(final String typeIdIn) {
		this.typeId = typeIdIn;
	}

	/**
	 * Get the enforcementType of the lease policy.
	 * 
	 * @return lease policy enforcementType
	 */
	public String getEnforcementType() {
		return enforcementType;
	}

	/**
	 * Set the enforcementType of the lease policy.
	 * 
	 * @param enforcementTypeIn - enforcementType of the lease policy
	 */
	public void setEnforcementType(final String enforcementTypeIn) {
		this.enforcementType = enforcementTypeIn;
	}

	/**
	 * Get the description of the lease policy.
	 * 
	 * @return lease policy description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the lease policy.
	 * 
	 * @param descriptionIn - description of the lease policy
	 */
	public void setDescription(final String descriptionIn) {
		this.description = descriptionIn;
	}

	/**
	 * Get the definition of the lease policy.
	 * 
	 * @return lease policy definition
	 */
	public JsonObject getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the lease policy.
	 * 
	 * @param definitionIn - definition of the lease policy
	 */
	public void setDefinition(final JsonObject definitionIn) {
		this.definition = definitionIn;
	}

	/**
	 * Get the criteria of the lease policy.
	 *
	 * @return lease policy criteria
	 */
	public JsonObject getCriteria() {
		return criteria;
	}

	/**
	 * Set the criteria of the lease policy.
	 *
	 * @param criteriaIn - criteria of the lease policy
	 */
	public void setCriteria(final JsonObject criteriaIn) {
		this.criteria = criteriaIn;
	}

}
