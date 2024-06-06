package com.vmware.pscoe.iac.artifact.model.vrang;

/*-
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

import java.util.List;

public class VraNgDefinition {
	/**
	 * entitledUsers for the Content Sharing Policy.
	 */
	public List<VraNgEntitledUser> entitledUsers;

	/**
	 * Default Constructor.
	 */
	public VraNgDefinition() {
	}

	/**
	 * Constructor.
	 * 
	 * @param entitledUsers list of the entitled users able to access the policy.
	 */
	public VraNgDefinition(List<VraNgEntitledUser> entitledUsers) {
		this.entitledUsers = entitledUsers;
	}

	/**
	 * Get the list of entitled users.
	 * 
	 * @return list of the entitled users.
	 */
	public List<VraNgEntitledUser> getEntitledUsers() {
		return entitledUsers;
	}

	/**
	 * Set the list of the entitled users.
	 * 
	 * @param entitledUsers - list of entitled users.
	 */
	public void setEntitledUsers(List<VraNgEntitledUser> entitledUsers) {
		this.entitledUsers = entitledUsers;
	}
}
