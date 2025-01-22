package com.vmware.pscoe.iac.artifact.aria.automation.rest.models;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2025 VMware
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
 * VraNgPolicyTypes is used to hold different policy types
 */
public final class VraNgPolicyTypes {
	/**
	 * CONTENT_SHARING_POLICY_TYPE.
	 */
	public static final String CONTENT_SHARING_POLICY_TYPE = "com.vmware.policy.catalog.entitlement";
	/**
	 * APPROVAL_POLICY_TYPE.
	 */
	public static final String APPROVAL_POLICY_TYPE = "com.vmware.policy.approval";
	/**
	 * DAY2_ACTION_POLICY_TYPE.
	 */
	public static final String DAY2_ACTION_POLICY_TYPE = "com.vmware.policy.deployment.action";
	/**
	 * LEASE_POLICY_TYPE.
	 */
	public static final String LEASE_POLICY_TYPE = "com.vmware.policy.deployment.lease";
	/**
	 * DEPLOYMENT_LIMIT_POLICY_TYPE.
	 */
	public static final String DEPLOYMENT_LIMIT_POLICY_TYPE = "com.vmware.policy.deployment.limit";
	/**
	 * RESOURCE_QUOTA_POLICY_TYPE.
	 */
	public static final String RESOURCE_QUOTA_POLICY_TYPE = "com.vmware.policy.resource.quota";

	private VraNgPolicyTypes() {
	}
}
