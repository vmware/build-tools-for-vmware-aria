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
package com.vmware.pscoe.iac.artifact.aria.model;

import java.util.List;

public class VraNgEntitledUser {
	/**
	 * items that are part of the user entitlement.
	 */
	private List<VraNgItem> items;
	/**
	 * userType of the user.
	 */
	private String userType;
	/**
	 * principals for the user.
	 */
	private List<VraNgPrincipal> principals;

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

	/**
	 * Get the list vRA items.
	 * 
	 * @return list vRA items.
	 */
	public List<VraNgItem> getItems() {
		return items;
	}

	/**
	 * Set the list of the vRA items.
	 * 
	 * @param items list of vRA items.
	 */
	public void setItems(List<VraNgItem> items) {
		this.items = items;
	}

	/**
	 * Get the user type.
	 * 
	 * @return user type.
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * Set the user type.
	 * 
	 * @param userType user type.
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * Get the list of principals for the user.
	 * 
	 * @return list of VraNgPrincipal for the user.
	 */
	public List<VraNgPrincipal> getPrincipals() {
		return principals;
	}

	/**
	 * Set the list of the vRA principals.
	 * 
	 * @param principals list of vRA principals.
	 */
	public void setPrincipals(List<VraNgPrincipal> principals) {
		this.principals = principals;
	}

}
