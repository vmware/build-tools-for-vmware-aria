package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent.*;

import java.util.HashMap;
import java.util.Map;

public class VraNgDirs {

    public static final String DIR_ENTITLEMENTS = "entitlements";
    public static final String DIR_PROPERTY_GROUPS = "property-groups";
    public static final String DIR_CATALOG_ITEMS = "catalog-items";
	public static final String DIR_POLICIES = "policies";
    public static final String DIR_CONTENT_SOURCES = "content-sources";
    public static final String DIR_BLUEPRINTS = "blueprints";
    public static final String DIR_SUBSCRIPTIONS = "subscriptions";
    public static final String DIR_REGIONS = "regions";
    public static final String DIR_FLAVOR_MAPPINGS = "flavor-mappings";
    public static final String DIR_IMAGE_MAPPINGS = "image-mappings";
    public static final String DIR_STORAGE_PROFILES = "storage-profiles";
    public static final String DIR_RESOURCE_ACTIONS = "resource-actions";
    public static final String DIR_CUSTOM_RESOURCES = "custom-resources";

	/**
	 * Sub folder path for content sharing policy.
	 */
	private static final String DIR_CONTENT_SHARING_POLICY = "content-sharing";
	/**
	 * Sub folder path for resource quota policy.
	 */
	private static final String DIR_RESOURCE_QUOTA_POLICY = "resource-quota";
	/**
	 * Sub folder path for lease policy.
	 */
	private static final String DIR_LEASE_POLICY = "lease";
	/**
	 * Sub folder path for day 2 actions policy.
	 */
	private static final String DIR_DAY_2_ACTIONS_POLICY = "day-2-actions";
	/**
	 * Sub folder path for approval policy.
	 */
	private static final String DIR_APPROVAL_POLICY = "approval";
	/**
	 * Sub folder path for approval policy.
	 */
	private static final String DIR_DEPLOYMENT_LIMIT_POLICY = "deployment-limit";

	public static Map<PolicyType, String> getPolicySubDirs() {
		return policySubDirs;
	}

	private static final Map<PolicyType, String> policySubDirs;

	static {
		policySubDirs = new HashMap<>();

		policySubDirs.put(PolicyType.CONTENT_SHARING_POLICY_TYPE, DIR_CONTENT_SHARING_POLICY);
		policySubDirs.put(PolicyType.RESOURCE_QUOTA_POLICY_TYPE, DIR_RESOURCE_QUOTA_POLICY);
		policySubDirs.put(PolicyType.LEASE_POLICY_TYPE, DIR_LEASE_POLICY);
		policySubDirs.put(PolicyType.DAY_2_ACTION_POLICY_TYPE, DIR_DAY_2_ACTIONS_POLICY);
		policySubDirs.put(PolicyType.APPROVAL_POLICY_TYPE, DIR_APPROVAL_POLICY);
		policySubDirs.put(PolicyType.DEPLOYMENT_LIMIT_POLICY_TYPE, DIR_DEPLOYMENT_LIMIT_POLICY);
	}
}
