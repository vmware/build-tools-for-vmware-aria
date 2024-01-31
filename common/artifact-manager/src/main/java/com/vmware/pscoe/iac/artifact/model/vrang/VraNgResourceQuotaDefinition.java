package com.vmware.pscoe.iac.artifact.model.vrang;

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
 * #L%q
 */

public class VraNgResourceQuotaDefinition {

	private VraNgCompositeLimits projectLevel;

	private VraNgCompositeLimits orgLevel;

	public VraNgCompositeLimits getProjectLevel() {
		return projectLevel;
	}

	public void setProjectLevel(VraNgCompositeLimits projectLevel) {

		this.projectLevel = projectLevel;
	}

	public VraNgCompositeLimits getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(VraNgCompositeLimits orgLevel) {
		this.orgLevel = orgLevel;
	}

	public VraNgResourceQuotaDefinition(VraNgCompositeLimits organizationLevelIn, VraNgCompositeLimits projectLevelIn){
		this.orgLevel = organizationLevelIn;
		this.projectLevel = projectLevelIn;
	}
	public VraNgResourceQuotaDefinition(){
		this.orgLevel = new VraNgCompositeLimits();
		this.projectLevel = new VraNgCompositeLimits();
	}
}
