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

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDay2ActionsPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

public final class VraNgDay2ActionsPolicyStore extends AbstractVraNgPolicyStore<VraNgDay2ActionsPolicy> {
	/**
	 * Constructor for policy store of type VraNgPolicyTypes.DAY2_ACTION_POLICY_TYPE
	 */
	public VraNgDay2ActionsPolicyStore() {
		super(VraNgPolicyTypes.DAY2_ACTION_POLICY_TYPE);
	}

	/**
	 * Get all day 2 actions policies from the server.
	 *
	 * @return list of day 2 actions policies
	 */
	protected List<VraNgDay2ActionsPolicy> getAllServerContents() {
		return this.restClient.getDay2ActionsPolicies();
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
			return this.vraNgPackageDescriptor.getPolicy().getDay2Actions();
		}
	}
}
