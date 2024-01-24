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

	CONTENT_SHARING("content-sharing"),
	RESOURCE_QUOTA("resource-quota"),
	DAY2_ACTION("day-2-actions"),
	APPROVAL("approval"),
	LEASE("lease"),
	DEPLOYMENT_LIMIT("deployment-limit");
	private final String name;
	private VraNgPolicyFolderName (String value){
			this.name = value;
	}

	@Override
	public String toString() {
		return this.name;
	}


	public static VraNgPolicyFolderName fromString(String name) {
		for (VraNgPolicyFolderName type : VraNgPolicyFolderName.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
	public String getName(){
		return this.name;
	}
}

