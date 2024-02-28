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

public enum VraNgPolicyFolderName {
	/**
	 * Sub-folder name for content sharing policies.
	 */
	CONTENT_SHARING("content-sharing"),
	/**
	 * Sub-folder name for resource quota policies.
	 */
	RESOURCE_QUOTA("resource-quota"),
	/**
	 * Sub-folder name for day 2 actions policies.
	 */
	DAY2_ACTION("day2-actions"),
	/**
	 * Sub-folder name for approval policies.
	 */
	APPROVAL("approval"),
	/**
	 * Sub-folder name for lease policies.
	 */
	LEASE("lease"),
	/**
	 * Sub-folder name for deployment limit policies.
	 */
	DEPLOYMENT_LIMIT("deployment-limit");
	/**
	 * Policy sub-folder name.
	 */
	private final String name;
	VraNgPolicyFolderName(String value) {
			this.name = value;
	}

	/**
	 * From enum to string.
	 * @return sub-folder name.
	 */
	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Return sub-folder name from string.
	 * @param name .
	 * @return sub-folder name.
	 */
	public static VraNgPolicyFolderName fromString(String name) {
		for (VraNgPolicyFolderName type : VraNgPolicyFolderName.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Getter.
	 * @return sub-folder name.
	 */
	public String getName() {
		return this.name;
	}
}

