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

import com.google.gson.JsonObject;

/**
 * The purpose of this class is to be able to export and import Approval Policy from Service Broker.
 */
public final class VraNgApprovalPolicy {
	/**
	 * Id of the Policy.
	 */
	private String id;
	/**
	 * Name of the  Policy.
	 */
	private String name;
	/**
	 * TypeId of the  Policy.
	 */
	private String typeId;
	/**
	 * ProjectId of the  Policy.
	 */
	private String projectId;
	/**
	 * OrgId of the Policy.
	 */
	private String orgId;
	/**
	 * EnforcementType of the Policy.
	 */
	private String enforcementType;
	/**
	 * Description of the  Policy.
	 */
	private String description;


	/**
	 * Scope Criteria of the Policy.
	 */
	private JsonObject criteria;
	/**
	 * Definition  Policy.
	 */
	private JsonObject definition;

	/**
	 * Constructor with all member values.
	 *
	 * @param idIn              id of the policy.
	 * @param nameIn            name of the policy.
	 * @param typeIdIn          type of the policy.
	 * @param projectIdIn       projectId of the policy.
	 * @param orgIdIn           organizationId of the policy.
	 * @param enforcementTypeIn enforcement type of the policy.
	 * @param descriptionIn     description of the policy.
	 * @param definitionIn      actual policy definition - different structure for every policy type.
	 * @param criteriaIn        scope criteria of the policy.
	 */
	public VraNgApprovalPolicy(final String idIn, final String nameIn, final String typeIdIn,
							   final String projectIdIn, final String orgIdIn,
							   final String enforcementTypeIn, final String descriptionIn,
							   final JsonObject definitionIn, final JsonObject criteriaIn) {
		this.id = idIn;
		this.name = nameIn;
		this.typeId = typeIdIn;
		this.projectId = projectIdIn;
		this.orgId = orgIdIn;
		this.enforcementType = enforcementTypeIn;
		this.description = descriptionIn;
		this.definition = definitionIn;
		this.criteria = criteriaIn;
	}

	/**
	 * Get policy id.
	 *
	 * @return policy id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set policy id.
	 *
	 * @param id - the policy id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the policy name.
	 *
	 * @return policy name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the policy name.
	 *
	 * @param name policy name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the typeId of the policy.
	 *
	 * @return typeId - typeId of the policy.
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Set the typeId of the policy.
	 *
	 * @param typeId - typeId of the policy.
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * Get the projectId of the policy. May be missing and return null.
	 *
	 * @return project id.
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Set the projectId of the policy. May be null.
	 *
	 * @param projectId the project id of the policy.
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * Get the organization id.
	 *
	 * @return the organization id of the policy.
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Set the organization id of the policy.
	 *
	 * @param orgId the organization id of the policy.
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * Get the enforcement type of the policy.
	 *
	 * @return the enforcement type of the policy.
	 */
	public String getEnforcementType() {
		return enforcementType;
	}

	/**
	 * Set the enforcement type of the policy.
	 *
	 * @param enforcementType the enforcement type of the policy.
	 */
	public void setEnforcementType(String enforcementType) {
		this.enforcementType = enforcementType;
	}

	/**
	 * Get the description of the policy.
	 *
	 * @return the description of the policy.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the policy.
	 *
	 * @param description the description of the policy.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the criteria of the policy.
	 *
	 * @return policy criteria as JsonObject.
	 */
	public JsonObject getCriteria() {
		return criteria;
	}

	/**
	 * Set the criteria of the policy.
	 *
	 * @param criteria the criteria of the policy.
	 */
	public void setCriteria(JsonObject criteria) {
		this.criteria = criteria;
	}

	/**
	 * Get the definition of the policy.
	 *
	 * @return the definition of the policy as JsonObject.
	 */
	public JsonObject getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the policy.
	 *
	 * @param definition the definition of the policy as JsonObject.
	 */
	public void setDefinition(JsonObject definition) {
		this.definition = definition;
	}
}
