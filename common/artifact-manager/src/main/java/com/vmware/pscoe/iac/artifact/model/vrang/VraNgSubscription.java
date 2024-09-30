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

public class VraNgSubscription implements Identifiable {

	private final String id;
	private final String name;
	private final String json;

	public VraNgSubscription(String id, String name, String json) {
		this.id = id;
		this.name = name;
		this.json = json;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getJson() {
		return this.json;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgBlueprint other = (VraNgBlueprint) obj;
		return this.id.equals(other.getId());
	}

}
