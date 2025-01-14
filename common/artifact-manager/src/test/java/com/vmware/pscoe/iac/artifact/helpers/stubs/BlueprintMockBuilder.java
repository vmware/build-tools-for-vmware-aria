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
package com.vmware.pscoe.iac.artifact.helpers.stubs;

import com.vmware.pscoe.iac.artifact.aria.models.VraNgBlueprint;

public class BlueprintMockBuilder {
	private String id;
	private final String name;
	private final String contentSourceId = "mockedContentSourceId";
	private final String description = "mockedDescription";
	private boolean	requestScopeOrg;

	public BlueprintMockBuilder(String name) {
		this.name = name;
		this.id = name + "_id";
	}

	public BlueprintMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public BlueprintMockBuilder setRequestScopeOrg(boolean requestScopeOrg) {
		this.requestScopeOrg = requestScopeOrg;
		return this;
	}

	/**
	 * Return a Blueprint with mocked id, source id and description
	 *
	 * @return VraNgBlueprint
	 */
	public VraNgBlueprint build() {
		return new VraNgBlueprint(this.id, this.name, this.contentSourceId, this.description, this.requestScopeOrg);
	}
}
