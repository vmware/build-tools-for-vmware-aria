package com.vmware.pscoe.iac.artifact.model.vrang;

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

public class VraNgRegion {

    private final String id;
    private final String cloudAccountId;

	private final String name;

	public VraNgRegion(String id, String cloudAccountId, String regionName) {
		this.id=id;
		this.cloudAccountId=cloudAccountId;
		this.name=regionName;
	}

    public String getId(){
        return id;
    }

	public String getName(){
		return name;
	}

    public String getCloudAccountId(){
        return cloudAccountId;
    }

    @Override
    public boolean equals(Object obj){
        if (obj==null || !this.getClass().equals(obj.getClass())){
            return false;
        }

        VraNgRegion other=(VraNgRegion) obj;
        return this.id.equals(other.getId());
    }

}
