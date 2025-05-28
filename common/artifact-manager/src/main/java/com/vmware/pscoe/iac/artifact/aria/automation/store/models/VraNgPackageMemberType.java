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
package com.vmware.pscoe.iac.artifact.aria.automation.store.models;

public enum VraNgPackageMemberType {
	/**
	 * Blueprint package member type
	 */
	BLUEPRINT("blueprint"),
	/**
	 * Subscription package member type
	 */
	SUBSCRIPTION("subscription"),
	/**
	 * Scenario package member type
	 */
	SCENARIO("scenario"),
	/**
	 * Catalog entitlement package member type
	 */
	CATALOG_ENTITLEMENT("catalog-entitlement"),
	/**
	 * Custom resource package member type
	 */
	CUSTOM_RESOURCE("custom-resource"),
	/**
	 * Resource action package member type
	 */
	RESOURCE_ACTION("resource-action"),
	/**
	 * Property group package member type
	 */
	PROPERTY_GROUP("property-group"),
	/**
	 * Content source package member type
	 */
	CONTENT_SOURCE("content-source"),
	/**
	 * Catalog item package member type
	 */
	CATALOG_ITEM("catalog-item"),
	/**
	 * Policy package member type
	 */
	POLICY("policy");

	/**
	 * Package member type name.
	 */
	private final String name;
	/**
	 * Is package member type a native content.
	 */
	private final boolean isNativeContent;

	VraNgPackageMemberType(String name) {
		this(name, true);
	}

	VraNgPackageMemberType(String name, boolean isNativeContent) {
		this.name = name;
		this.isNativeContent = isNativeContent;
	}

	/**
	 * @return isNativeContent
	 */
	public boolean isNativeContent() {
		return this.isNativeContent;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * @param name Package member type name
	 * @return Package member type object
	 */
	public static VraNgPackageMemberType fromString(String name) {
		for (VraNgPackageMemberType type : VraNgPackageMemberType.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
