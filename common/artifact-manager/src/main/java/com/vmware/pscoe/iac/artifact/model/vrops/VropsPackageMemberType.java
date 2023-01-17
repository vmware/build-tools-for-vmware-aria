package com.vmware.pscoe.iac.artifact.model.vrops;

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

public enum VropsPackageMemberType {
    VIEW("view"), DASHBOARD("dashboard"), REPORT("report"), ALERT_DEFINITION("alert_definition"), SYMPTOM_DEFINITION("symptom_definition"), POLICY("policy"),
    SUPERMETRIC("supermetric"), RECOMMENDATION("recommendation"), METRICCONFIG("metric_config"), CUSTOM_GROUP("custom_group");

    private final String name;

    VropsPackageMemberType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static VropsPackageMemberType fromString(String name) {
        for (VropsPackageMemberType type : VropsPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
