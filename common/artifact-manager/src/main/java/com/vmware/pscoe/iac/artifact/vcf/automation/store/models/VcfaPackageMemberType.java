/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the 
 * terms and conditions of the subcomponent's license, as noted in the 
 * LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.vcf.automation.store.models;

import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent;

public enum VcfaPackageMemberType implements PackageContent.ContentType {
    BLUEPRINT("blueprint"),
    CATALOG_ITEM("catalog-item"),
    CONTENT_SOURCE("content-source"),
    CUSTOM_RESOURCE("custom-resource"),
    RESOURCE_ACTION("resource-action"),
    SUBSCRIPTION("subscription"),
    POLICY("policy"),
    PROPERTY_GROUP("property-group"),
    CATALOG_ENTITLEMENT("catalog-entitlement"),
    SCENARIO("scenario");

    private final String type;

    VcfaPackageMemberType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }

    public static VcfaPackageMemberType fromString(String type) {
        for (VcfaPackageMemberType memberType : VcfaPackageMemberType.values()) {
            if (memberType.type.equals(type)) {
                return memberType;
            }
        }
        return null;
    }

    public static VcfaPackageMemberType getInstance(String type) {
        for (VcfaPackageMemberType ct : VcfaPackageMemberType.values()) {
            if (ct.getTypeValue().equalsIgnoreCase(type)) {
                return ct;
            }
        }
        return null;
    }
}
