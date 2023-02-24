package com.vmware.pscoe.iac.artifact.helpers.stubs;

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
		items.add(new VraNgItem("d0624893-4932-46a7-8e25-fab1e4109c2e", "CATALOG_SOURCE_IDENTIFIER"));
		ArrayList<VraNgPrincipal> principals= new ArrayList<>();
		principals.add(new VraNgPrincipal("PROJECT", ""));
		ArrayList<VraNgEntitledUser> entitledUsers= new ArrayList<>();
		entitledUsers.add(new VraNgEntitledUser(items,"USER", principals));
		VraNgDefinition definition = new VraNgDefinition(entitledUsers);

		result.setId("679daee9-d63d-4ce2-9ee1-d4336861fe87");
		result.setName("CsPolicy");
		result.setDescription("Testing");
		result.setProjectId("c3f029f9-a97c-4df6-bdc4-c0e4b91aa18e");
		result.setOrgId("27aaf31d-d9af-4c48-9736-eb9c9faa4ae8");
		result.setEnforcementType("HARD");
		result.setTypeId("com.vmware.policy.catalog.entitlement");
		result.setDefinition(definition);
		return result;
	}
}
