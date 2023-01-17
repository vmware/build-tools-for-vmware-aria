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

public enum VraNgCatalogEntitlementType {

    @SerializedName("CatalogItemIdentifier")
    CATALOG_ITEM_IDENTIFIER("CatalogItemIdentifier"),

    @SerializedName("CatalogSourceIdentifier")
    CATALOG_SOURCE_IDENTIFIER("CatalogSourceIdentifier"),

	@SerializedName("DEFAULT")
	DEFAULT("DEFAULT");

    private final String name;

    VraNgCatalogEntitlementType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static VraNgCatalogEntitlementType fromString(String name) {
        for (VraNgCatalogEntitlementType type : VraNgCatalogEntitlementType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
