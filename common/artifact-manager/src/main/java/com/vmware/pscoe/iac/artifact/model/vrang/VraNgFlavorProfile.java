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
package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;

public class VraNgFlavorProfile {

	private final String id;
	private List<VraNgFlavorMapping> flavorMappings;

	public VraNgFlavorProfile(String id, List<VraNgFlavorMapping> flavorMappings) {
		this.id = id;
		this.flavorMappings = flavorMappings;
	}

	public String getId() {
		return this.id;
	}

	public List<VraNgFlavorMapping> getFlavorMappings() {
		return flavorMappings;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgFlavorProfile other = (VraNgFlavorProfile) obj;
		return this.id.equals(other.getId());
	}

}
