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

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegion;

public class VraNgRegionMockBuilder {
	private String id;
	private String cloudAccountId;
	private String regionName;

	public VraNgRegionMockBuilder() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCloudAccountId(String cloudAccountId) {
		this.cloudAccountId = cloudAccountId;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public VraNgRegion build() {
		return new VraNgRegion(this.id, this.cloudAccountId, this.regionName);
	}
}
