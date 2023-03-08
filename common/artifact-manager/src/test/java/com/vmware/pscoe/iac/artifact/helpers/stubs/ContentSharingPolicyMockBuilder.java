package com.vmware.pscoe.iac.artifact.helpers.stubs;

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

import java.util.ArrayList;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgEntitledUser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPrincipal;

public class ContentSharingPolicyMockBuilder {
	public static VraNgContentSharingPolicy buildContentSharingPolicy() {
		VraNgContentSharingPolicy result = new VraNgContentSharingPolicy();
		ArrayList<VraNgItem> items= new ArrayList<>();
		items.add(new VraNgItem("d0624893-4932-46a7-8e25-fab1e4109c2e","Contentsource", "CATALOG_SOURCE_IDENTIFIER"));
		ArrayList<VraNgPrincipal> principals= new ArrayList<>();
		principals.add(new VraNgPrincipal("PROJECT", ""));
		ArrayList<VraNgEntitledUser> entitledUsers= new ArrayList<>();
		entitledUsers.add(new VraNgEntitledUser(items,"USER", principals));
		VraNgDefinition definition = new VraNgDefinition(entitledUsers);

		result.setName("CsPolicy");
		result.setDescription("Testing");
		result.setEnforcementType("HARD");
		result.setTypeId("com.vmware.policy.catalog.entitlement");
		result.setDefinition(definition);
		return result;
	}
}
