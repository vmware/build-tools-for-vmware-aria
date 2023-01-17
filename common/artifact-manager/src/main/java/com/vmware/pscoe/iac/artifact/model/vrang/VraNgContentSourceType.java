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

import com.google.gson.annotations.SerializedName;

public enum VraNgContentSourceType {
    @SerializedName("com.vmw.vro.workflow")
    VRO_WORKFLOW("com.vmw.vro.workflow", VraNgWorkflowContentSource.class),

    @SerializedName("com.aws.cft")
    CFT("com.aws.cft", VraNgContentSourceBase.class),

    @SerializedName("com.vmw.blueprint")
    BLUEPRINT("com.vmw.blueprint", VraNgContentSource.class),

    @SerializedName("com.vmw.abx.actions")
    ABX_ACTIONS("com.vmw.abx.actions", VraNgContentSource.class),

    @SerializedName("com.vmw.codestream")
    CODE_STREAM("com.vmw.codestream", VraNgContentSource.class),

    UNKNOWN("unknown", VraNgContentSourceBase.class);

    private final String name;

    private final Class<? extends VraNgContentSourceBase> clazz;

    VraNgContentSourceType(String name, Class<? extends VraNgContentSourceBase> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public Class<? extends VraNgContentSourceBase> getTypeClass() {
        return this.clazz;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static VraNgContentSourceType fromString(String name) {
        for (VraNgContentSourceType type : VraNgContentSourceType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
