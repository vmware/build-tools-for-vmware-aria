package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.ArrayList;

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
 
 public class VraNgEntitledUser{
    public ArrayList<VraNgItem> items;
    public String userType;
    public ArrayList<VraNgPrincipal> principals;

	public VraNgEntitledUser() {
		super();
	}
	public VraNgEntitledUser(ArrayList<VraNgItem> items,String userType,ArrayList<VraNgPrincipal> principals) {
		this.items= items;
		this.userType= userType;
		this.principals= principals;
	}
}
