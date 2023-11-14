package com.vmware.pscoe.iac.artifact.model.vrops;

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

/**
 * VropsPackageMemberType defines the asset types that vROPs package supports.
 */
public enum VropsPackageMemberType {

	/**
	 * VIEW.
	 */
	VIEW("view"),
	/**
	 * DASHBOARD.
	 */
	DASHBOARD("dashboard"),
	/**
	 * REPORT.
	 */
	REPORT("report"),
	/**
	 * ALERT_DEFINITION.
	 */
	ALERT_DEFINITION("alert_definition"),

	/**
	 * SYMPTOM_DEFINITION.
	 */
	SYMPTOM_DEFINITION("symptom_definition"),
	/**
	 * POLICY.
	 */
	POLICY("policy"),
	/**
	 * DEFAULT_POLICY.
	 */
	DEFAULT_POLICY("default_policy"),
	/**
	 * SUPERMETRIC.
	 */
	SUPERMETRIC("supermetric"),
	/**
	 * RECOMMENDATION.
	 */
	RECOMMENDATION("recommendation"),
	/**
	 * METRICCONFIG.
	 */
	METRICCONFIG("metric_config"),
	/**
	 * CUSTOM_GROUP.
	 */
	CUSTOM_GROUP("custom_group");

	/**
	 * name.
	 */
	private final String name;

	/**
	 * Constructor.
	 * 
	 * @param name of the member type.
	 */
	VropsPackageMemberType(String name) {
		this.name = name;
	}

	/**
	 * toString().
	 * 
	 * @return string representation.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * fromString().
	 * 
	 * @param name name of the type.
	 * @return VropsPackageMemberType.
	 */
	public static VropsPackageMemberType fromString(String name) {
		for (VropsPackageMemberType type : VropsPackageMemberType.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}
}
