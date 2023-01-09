package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlement;

public class CatalogEntitlementMockBuilder {
	private String name = "mockedEntitlementName";
	private String id;
	private List<String> projects;

	public CatalogEntitlementMockBuilder(String name) {
		this.name = name;
		this.projects = new ArrayList<String>();
		this.projects.add(name + "_project");
	}

	public CatalogEntitlementMockBuilder setProjects(List<String> projects) {
		this.projects = projects;
		return this;
	}

	public CatalogEntitlementMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public VraNgCatalogEntitlement build() {
		VraNgCatalogEntitlement entitlement = new VraNgCatalogEntitlement();
		entitlement.setId(this.id);
		entitlement.setProjects(this.projects);
		entitlement.setName(this.name);
		return entitlement;
	}
}
