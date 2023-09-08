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

public class VraNgPolicyBase {
	/**
	 * TypeId of the Content Sharing Policy.
	 */
	private String typeId;
	/**
	 * Name of the Content Sharing Policy.
	 */
	private String name;
	/**
	 * Description of the Content Sharing Policy.
	 */
	private String description;

	public VraNgPolicyBase() {
	}

	public VraNgPolicyBase(String typeId, String name, String description) {
		this.typeId = typeId;
		this.name = name;
		this.description = description;
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
