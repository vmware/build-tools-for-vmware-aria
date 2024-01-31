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
 * #L%
 */

import com.google.gson.JsonObject;

public class VraNgCompositeLimits {

	private  VraNgUserLevelLimits userLevel;
	private  VraNgLimits limits;


	public VraNgCompositeLimits(VraNgLimits limitsIn, VraNgUserLevelLimits userLevelIn){
		this.limits = limitsIn;
		this.userLevel  = userLevelIn;
	}
	public VraNgCompositeLimits(){
		this.limits   = new VraNgLimits( "","", "", "");
		this.userLevel   = new VraNgUserLevelLimits(new VraNgLimits("", "", "", ""));
	}

	public VraNgLimits getLimits() {
		return limits;
	}

	public void setLimits(VraNgLimits limits) {
		this.limits = limits;
	}

	public VraNgUserLevelLimits getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(VraNgUserLevelLimits userLevel) {
		this.userLevel = userLevel;
	}
}
