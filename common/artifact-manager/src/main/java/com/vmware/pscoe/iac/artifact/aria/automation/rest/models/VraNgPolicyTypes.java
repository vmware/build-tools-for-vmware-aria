package com.vmware.pscoe.iac.artifact.aria.automation.rest.models;

import com.vmware.pscoe.iac.artifact.aria.automation.models.IVraNgPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDay2ActionsPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDeploymentLimitPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceQuotaPolicy;

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
public enum VraNgPolicyTypes {
	/**
	 * CONTENT_SHARING_POLICY_TYPE.
	 */
	CONTENT_SHARING_POLICY_TYPE("com.vmware.policy.catalog.entitlement", "content-sharing", "Content Sharing", 
				VraNgContentSharingPolicy.class),
	/**
	 * APPROVAL_POLICY_TYPE.
	 */
	APPROVAL_POLICY_TYPE("com.vmware.policy.approval", "approval", "Approval", VraNgApprovalPolicy.class),
	/**
	 * DAY2_ACTION_POLICY_TYPE.
	 */
	DAY2_ACTION_POLICY_TYPE("com.vmware.policy.deployment.action", "day2-actions", "Day Two Actions",
				VraNgDay2ActionsPolicy.class),
	/**
	 * LEASE_POLICY_TYPE.
	 */
	LEASE_POLICY_TYPE("com.vmware.policy.deployment.lease", "lease", "Lease", VraNgLeasePolicy.class),
	/**
	 * DEPLOYMENT_LIMIT_POLICY_TYPE.
	 */
	DEPLOYMENT_LIMIT_POLICY_TYPE("com.vmware.policy.deployment.limit", "deployment-limit", "Deployment Limit",
				VraNgDeploymentLimitPolicy.class),
	/**
	 * RESOURCE_QUOTA_POLICY_TYPE.
	 */
	RESOURCE_QUOTA_POLICY_TYPE("com.vmware.policy.resource.quota", "resource-quota", "Resource Quota",
				VraNgResourceQuotaPolicy.class);

	/** Policy type ID */
	public final String id;
	/** Policy folder when exported to the file system (relative to VraNgDirs.DIR_POLICIES) */
	public final String folder;
	/** Policy type description - for logging and error handling */
	public final String description;
	/** Policy class */
	public final Class vraNgPolicyClass;
	
	/**
	 * Abstract parent to all Policy Store classes
	 * 
	 * @param id  - policy type ID
	 * @param folder   - policy folder when exported to the file system (relative to VraNgDirs.DIR_POLICIES)
	 * @param description  - description - for logging/error handling
	 * @param vraNgPolicyClass - policy data class of the type VraNgWhateverPolicy
	 */
	<T extends IVraNgPolicy> VraNgPolicyTypes(String id, String folder, String description, Class<T> vraNgPolicyClass) {
		this.id = id;
		this.folder = folder;
		this.description = description;
		this.vraNgPolicyClass = vraNgPolicyClass;
	}

	/**
	 * Finds VraNgPolicyTypes based on policy class
	 * 
	 * @param vraNgPolicyClass - policy class
	 * @return VraNgPolicyTypes
	 */
	public static VraNgPolicyTypes forPolicyClass(Class vraNgPolicyClass) {
		for (VraNgPolicyTypes val: values()) {
			if (vraNgPolicyClass.getName() == val.vraNgPolicyClass.getName()) {
				return val;
			}
		}
		throw new RuntimeException("Unsupported policy class: " + vraNgPolicyClass);
	}
}
