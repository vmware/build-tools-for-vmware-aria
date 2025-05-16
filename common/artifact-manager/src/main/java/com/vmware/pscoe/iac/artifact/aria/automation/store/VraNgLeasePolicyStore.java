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

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

public final class VraNgLeasePolicyStore extends AbstractVraNgPolicyStore<VraNgLeasePolicy> {

	/**
	 * Constructor for policy store of type VraNgPolicyTypes.LEASE_POLICY_TYPE
	 */
	public VraNgLeasePolicyStore() {
		super(VraNgPolicyTypes.LEASE_POLICY_TYPE, "lease", "Lease", VraNgLeasePolicy.class);
	}

	protected List<VraNgLeasePolicy> getAllServerContents() {
		return this.restClient.getLeasePolicies();
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of Lease policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getLease();
		}
	}

	/**
	 * Makes an API call to create or update a Policy
	 * 
	 * @param policy - policy to create (when it has no ID) or update (when it has
	 *               an ID)
	 */
	@Override
	protected void createOrUpdatePolicy(final VraNgLeasePolicy policy) {
		this.restClient.createOrUpdateLeasePolicy(policy);
	}
}
