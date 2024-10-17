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
package com.vmware.pscoe.iac.artifact.model;

public enum PackageType {
	VRO("package", "vro"),
	VRANG("vra-ng", "vra-ng"),
	VCDNG("vcd-ng", "vcd-ng"),
	VROPS("vrops", "vrops"),
	VRLI("vrli", "vrli"),
	ABX("abx", "abx"),
	BASIC("bsc", "bsc"),
	CS("cs", "cs");

	private final String packageContainer;
	private final String packageExtension;

	PackageType(String packageExtension, String packageContainer) {
		this.packageContainer = packageContainer;
		this.packageExtension = packageExtension;
	}

	public String getPackageContainer() {
		return packageContainer;
	}

	public String getPackageExtention() {
		return packageExtension;
	}

	public static PackageType fromExtension(String packageFileExtension) {
		for (PackageType type : PackageType.values()) {
			if (type.packageExtension.equalsIgnoreCase(packageFileExtension)) {
				return type;
			}
		}
		return null;
	}

}
