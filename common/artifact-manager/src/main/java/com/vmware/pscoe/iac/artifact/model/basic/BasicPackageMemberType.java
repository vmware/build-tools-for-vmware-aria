package com.vmware.pscoe.iac.artifact.model.basic;

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

public enum BasicPackageMemberType {
    CONTENT("content");

    private final String name;

    BasicPackageMemberType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static BasicPackageMemberType fromString(String name) {
        for (BasicPackageMemberType type : BasicPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
