package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.ArrayList;
import java.util.List;

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

public class VraNgEntitledUser {
	/**
	 * items that are part of the user entitlement.
	 */
	public List<VraNgItem> items;
	/**
	 * userType of the user.
	 */
	public String userType;
	/**
	 * principals for the user.
	 */
	public List<VraNgPrincipal> principals;

	/**
	 * Default constructor.
	 */
	public VraNgEntitledUser() {
	}

	/**
	 * Constructor.
	 * 
	 * @param items      list of vRA items that will be part of the user.
	 * @param userType   type of the user.
	 * @param principals list of principals.
	 */
	public VraNgEntitledUser(List<VraNgItem> items, String userType, List<VraNgPrincipal> principals) {
		this.items = items;
		this.userType = userType;
		this.principals = principals;
	}
}
