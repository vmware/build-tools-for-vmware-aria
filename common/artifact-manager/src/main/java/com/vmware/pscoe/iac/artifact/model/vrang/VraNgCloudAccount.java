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

import java.util.Collections;
import java.util.List;

public class VraNgCloudAccount {

    private final String id;
    private final String name;
    private final String type;
    private final List<String> regionIds;
    private final List<String> tags;

    public VraNgCloudAccount(String id, String name, String type, List<String> regionIds, List<String> tags) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.regionIds = regionIds;
        this.tags = tags;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getRegionIds() {
        return Collections.unmodifiableList(this.regionIds);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(this.tags);
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
