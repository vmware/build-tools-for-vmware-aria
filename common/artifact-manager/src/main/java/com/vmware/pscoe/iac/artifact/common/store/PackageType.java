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
package com.vmware.pscoe.iac.artifact.common.store;

// Make sure not to introduce new types with dash - in the extension. This will introduce maven and artifactory issues!
public enum PackageType {
	VRO("package", "vro", "package"),
	VRANG("vrang", "vra-ng", "vra-ng"),
	// TODO: check if VRANG and VRANGv3 has the proper new packageType
	// TODO: check all usage of .fromExtension
	VRANGv3("vra-ng", "vra-ng", "vrang"), // kept for backward compatibility with Build Tools for VMware Aria <= 3.x.x
	VCF_AUTO_MODERN("vcfam", "vcf-auto-modern", "vcf-auto-modern"),
	VCDNG("vcd-ng", "vcd-ng", "vcd-ng"),
	VROPS("vrops", "vrops", "vrops"),
	VRLI("vrli", "vrli", "vrli"),
	ABX("abx", "abx", "abx"),
	BASIC("bsc", "bsc", "bsc"),
	CS("cs", "cs", "cs");

	private final String packageContainer;
	private final String packageExtension;
	private final String packageType;

	PackageType(String packageExtension, String packageContainer, String packageType) {
		this.packageContainer = packageContainer;
		this.packageExtension = packageExtension;
		this.packageType = packageType;
	}

	public String getPackageContainer() {
		return packageContainer;
	}

	public String getPackageExtension() {
		return packageExtension;
	}

	public String getPackageType() {
		return packageType;
	}

	public static PackageType fromExtension(String packageFileExtension) {
		for (PackageType type : PackageType.values()) {
			if (type.packageExtension.equalsIgnoreCase(packageFileExtension)) {
				return type;
			}
		}
		return null;
	}

	public static PackageType fromType(String packageType) {
		for (PackageType type : PackageType.values()) {
			if (type.packageType.equalsIgnoreCase(packageType)) {
				return type;
			}
		}
		return null;
	}

}
