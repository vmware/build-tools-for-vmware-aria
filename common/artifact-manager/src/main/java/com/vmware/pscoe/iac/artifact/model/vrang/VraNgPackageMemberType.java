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

public enum VraNgPackageMemberType {
    BLUEPRINT("blueprint"),
    SUBSCRIPTION("subscription"),
    FLAVOR_PROFILE("flavor-profile"),
    IMAGE_PROFILE("image-profile"),
    STORAGE_PROFILE("storage-profile"),
    REGION_MAPPING("region-mapping"),
    CATALOG_ENTITLEMENT("catalog-entitlement"),
    CUSTOM_RESOURCE("custom-resource"),
    RESOURCE_ACTION("resource-action"),
	PROPERTY_GROUP("property-group"),
    CONTENT_SOURCE("content-source"),
    CATALOG_ITEM("catalog-item"),
	POLICY("policy");

    private final String name;
    private final boolean isNativeContent;

    VraNgPackageMemberType(String name) {
        this(name, true);
    }

    VraNgPackageMemberType(String name, boolean isNativeContent) {
        this.name = name;
        this.isNativeContent = isNativeContent;
    }

    public boolean isNativeContent() {
        return this.isNativeContent;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static VraNgPackageMemberType fromString(String name) {
        for (VraNgPackageMemberType type : VraNgPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
