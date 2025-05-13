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

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceQuotaPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

public final class VraNgResourceQuotaPolicyStore extends AbstractVraNgPolicyStore<VraNgResourceQuotaPolicy> {

	/**
	 * Constructor for policy store of type VraNgPolicyTypes.RESOURCE_QUOTA_POLICY_TYPE
	 */
	public VraNgResourceQuotaPolicyStore() {
		super(VraNgPolicyTypes.RESOURCE_QUOTA_POLICY_TYPE, "resource-quota", "Resource Quota",
				VraNgResourceQuotaPolicy.class);
	}

	/**
	 * Gets all the resource quotas from the server.
	 *
	 * @return List of the quotas
	 */
	protected List<VraNgResourceQuotaPolicy> getAllServerContents() {
		return this.restClient.getResourceQuotaPolicies();
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
			return this.vraNgPackageDescriptor.getPolicy().getResourceQuota();
		}
	}

	/**
	 * Makes an API call to create a Policy
	 * 
	 * @param policy
	 */
	@Override
	protected void createPolicy(final VraNgResourceQuotaPolicy policy) {
		this.restClient.createResourceQuotaPolicy(policy);
	}
}
