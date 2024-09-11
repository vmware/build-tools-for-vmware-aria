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
package com.vmware.pscoe.iac.artifact.model.vrli;

public enum VrliPackageMemberType {
	ALERTS("alerts"), CONTENT_PACKS("content_packs");
	
	private final String name;

	VrliPackageMemberType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static VrliPackageMemberType fromString(String name) {
		for (VrliPackageMemberType type : VrliPackageMemberType.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}
}
