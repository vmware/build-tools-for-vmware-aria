/*-
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
package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.util.ArrayList;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgDefinition;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgEntitledUser;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgItem;
import com.vmware.pscoe.iac.artifact.aria.models.VraNgPrincipal;

public final class ContentSharingPolicyMockBuilder {

	private ContentSharingPolicyMockBuilder() {
	};

	/**
	 * Create a mock policy for testing.
	 * 
	 * @return an instance of VraNgContentSharingPolicy for testing.
	 */
	public static VraNgContentSharingPolicy buildContentSharingPolicy() {
		VraNgContentSharingPolicy result = new VraNgContentSharingPolicy();
		ArrayList<VraNgItem> items = new ArrayList<>();
		VraNgItem item = new VraNgItem();
		item.setId("d0624893-4932-46a7-8e25-fab1e4109c2e");
		item.setType("CATALOG_SOURCE_IDENTIFIER");
		items.add(item);
		ArrayList<VraNgPrincipal> principals = new ArrayList<>();
		principals.add(new VraNgPrincipal("PROJECT", ""));
		ArrayList<VraNgEntitledUser> entitledUsers = new ArrayList<>();
		entitledUsers.add(new VraNgEntitledUser(items, "USER", principals));
		VraNgDefinition definition = new VraNgDefinition(entitledUsers);

		result.setId("679daee9-d63d-4ce2-9ee1-d4336861fe87");
		result.setName("CsPolicy");
		result.setDescription("Testing");
		result.setEnforcementType("HARD");
		result.setProjectId("c3f029f9-a97c-4df6-bdc4-c0e4b91aa18e");
		result.setOrgId("27aaf31d-d9af-4c48-9736-eb9c9faa4ae8");
		result.setTypeId("com.vmware.policy.catalog.entitlement");
		result.setDefinition(definition);

		return result;
	}
}
