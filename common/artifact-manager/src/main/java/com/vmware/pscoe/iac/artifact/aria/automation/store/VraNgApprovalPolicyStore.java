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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

/**
 * Store for Approval Policies.
 */
public final class VraNgApprovalPolicyStore extends AbstractVraNgPolicyStore<VraNgApprovalPolicy> {

	/**
	 * Constructor for policy store of type VraNgPolicyTypes.APPROVAL_POLICY_TYPE
	 */
	public VraNgApprovalPolicyStore() {
		super(VraNgPolicyTypes.APPROVAL_POLICY_TYPE, "approval", "Approval", VraNgApprovalPolicy.class);
	}

	/**
	 * getItemListFromDescriptor.
	 * 
	 * @return list of policy names to import or export.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getApproval();
		}
	}

	/**
	 * Retrieves all approval policies from the server.
	 *
	 * Used for deletion
	 *
	 * @return A List of approval policies
	 */
	protected List<VraNgApprovalPolicy> getAllServerContents() {
		return this.restClient.getApprovalPolicies();
	}

	/**
	 * Makes an API call to create or update a Policy
	 * 
	 * @param policy - policy to create (when it has no ID) or update (when it has
	 *               an ID)
	 */
	@Override
	protected void createOrUpdatePolicy(final VraNgApprovalPolicy policy) {
		this.restClient.createOrUpdateApprovalPolicy(policy);
	}
}
