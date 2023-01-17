package com.vmware.pscoe.iac.artifact.helpers.stubs;

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
