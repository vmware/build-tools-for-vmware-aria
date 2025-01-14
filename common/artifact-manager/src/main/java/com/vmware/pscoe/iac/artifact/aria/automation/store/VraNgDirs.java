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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

public final class VraNgDirs {
	/**
	 * entitlements folder name.
	 */
	public static final String DIR_ENTITLEMENTS = "entitlements";
	/**
	 * property-groups folder name.
	 */
	public static final String DIR_PROPERTY_GROUPS = "property-groups";
	/**
	 * catalog-items folder name.
	 */
	public static final String DIR_CATALOG_ITEMS = "catalog-items";
	/**
	 * policies folder name.
	 */
	public static final String DIR_POLICIES = "policies";
	/**
	 * content-sources folder name.
	 */
	public static final String DIR_CONTENT_SOURCES = "content-sources";
	/**
	 * blueprints folder name.
	 */
	public static final String DIR_BLUEPRINTS = "blueprints";
	/**
	 * subscriptions folder name.
	 */
	public static final String DIR_SUBSCRIPTIONS = "subscriptions";
	/**
	 * resource-actions folder name.
	 */
	public static final String DIR_RESOURCE_ACTIONS = "resource-actions";
	/**
	 * custom-resources folder name.
	 */
	public static final String DIR_CUSTOM_RESOURCES = "custom-resources";

	/**
	 * Hiding constructor of utility class.
	 * Linter quote:
	 * Utility classes should not have a public or default constructor.
	 * [HideUtilityClassConstructor] .
	 */
	private VraNgDirs() {
	};
}
