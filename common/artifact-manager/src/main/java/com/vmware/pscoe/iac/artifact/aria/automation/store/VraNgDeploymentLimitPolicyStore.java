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

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDeploymentLimitPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

public final class VraNgDeploymentLimitPolicyStore extends AbstractVraNgPolicyStore<VraNgDeploymentLimitPolicy> {
	/**
	 * Constructor for policy store of type VraNgPolicyTypes.DEPLOYMENT_LIMIT_POLICY_TYPE
	 */
	public VraNgDeploymentLimitPolicyStore() {
		super(VraNgPolicyTypes.DEPLOYMENT_LIMIT_POLICY_TYPE, "deployment-limit", "Deployment Limit",
				VraNgDeploymentLimitPolicy.class);
	}

	/**
	 * Get all the deployment limit policies from the server.
	 *
	 * @return list of all deployment limit policies.
	 */
	protected List<VraNgDeploymentLimitPolicy> getAllServerContents() {
		return this.restClient.getDeploymentLimitPolicies();
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
			return this.vraNgPackageDescriptor.getPolicy().getDeploymentLimit();
		}
	}

	/**
	 * Makes an API call to create a Policy
	 * 
	 * @param policy
	 */
	@Override
	protected void createPolicy(final VraNgDeploymentLimitPolicy policy) {
		this.restClient.createDeploymentLimitPolicy(policy);
	}
}
