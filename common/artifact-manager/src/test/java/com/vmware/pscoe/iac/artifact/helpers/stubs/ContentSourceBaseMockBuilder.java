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

import com.vmware.pscoe.iac.artifact.aria.models.VraNgContentSourceBase;

public class ContentSourceBaseMockBuilder {
	private String id = "mockedSourceId";
	private String name = "mockedSourceName";
	private String typeId = "com.vmw.vro.workflow";

	public ContentSourceBaseMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public ContentSourceBaseMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public ContentSourceBaseMockBuilder setTypeId(String typeId) {
		this.typeId = typeId;
		return this;
	}

	public VraNgContentSourceBase build() {
		VraNgContentSourceBase contentSource = new VraNgContentSourceBase();
		contentSource.setId(this.id);
		contentSource.setName(this.name);
		contentSource.setTypeId(this.typeId);
		return contentSource;
	}
}
