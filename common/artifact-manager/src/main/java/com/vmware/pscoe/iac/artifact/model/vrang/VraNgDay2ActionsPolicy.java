package com.vmware.pscoe.iac.artifact.model.vrang;

import com.google.gson.JsonObject;

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
public class VraNgDay2ActionsPolicy {

	/**
	 * Id of the Day 2 Actions Policy.
	 */
	private String id;
	/**
	 * Name of the Day 2 Actions Policy.
	 */
	private String name;
	/**
	 * TypeId of the Day 2 Actions Policy.
	 */
	private String typeId;
	/**
	 * ProjectId of the Day 2 Actions Policy.
	 */
	private String projectId;
	/**
	 * OrgId of the Day 2 Actions Policy.
	 */
	private String orgId;
	/**
	 * EnforcementType of the Day 2 Actions Policy.
	 */
	private String enforcementType;
	/**
	 * Description of the Day 2 Actions Policy.
	 */
	private String description;


	/**
	 * Scope of the Day 2 Actions Policy.
	 */
	private JsonObject scopeCriteria;
	/**
	 * Definition of the Day 2 Actions Policy.
	 */
	private JsonObject definition;

	public  VraNgDay2ActionsPolicy() {};
	public VraNgDay2ActionsPolicy(final String idIn, final String nameIn, final String typeIdIn,
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
		this.scopeCriteria = criteriaIn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getEnforcementType() {
		return enforcementType;
	}

	public void setEnforcementType(String enforcementType) {
		this.enforcementType = enforcementType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JsonObject getScopeCriteria() {
		return scopeCriteria;
	}

	public void setScopeCriteria(JsonObject scopeCriteria) {
		this.scopeCriteria = scopeCriteria;
	}

	public JsonObject getDefinition() {
		return definition;
	}

	public void setDefinition(JsonObject definition) {
		this.definition = definition;
	}
}