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

public class VraNgBlueprint {

    private String id;
    private String name;
    private String content;
    private String description;
    private Boolean requestScopeOrg;

    public VraNgBlueprint(String id, String name, String content, String description, Boolean requestScopeOrg) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.description = description;
        this.requestScopeOrg = requestScopeOrg;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
        return this.description;
    }

    public Boolean getRequestScopeOrg() {
        return this.requestScopeOrg;
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
