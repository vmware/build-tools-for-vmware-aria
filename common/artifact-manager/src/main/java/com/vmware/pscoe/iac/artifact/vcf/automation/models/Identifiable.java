/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2025 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.vcf.automation.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.common.annotation.SkipExport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Identifiable model for VCF automation models.
 */
public interface Identifiable {
    String getId();
    String getName();

    default Map<String,Object> toMap() {
        ObjectMapper m = new ObjectMapper();
        return m.convertValue(this, Map.class);
    }

    default Map<String,Object> asExportMap() {
        ObjectMapper m = new ObjectMapper();
        Map<String,Object> map = m.convertValue(this, Map.class);
        Class<?> cls = this.getClass();
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.isAnnotationPresent(SkipExport.class)) {
                    String key = f.getName();
                    JsonProperty jp = f.getAnnotation(JsonProperty.class);
                    if (jp != null && jp.value() != null && !jp.value().isEmpty()) {
                        key = jp.value();
                    }
                    map.remove(key);
                }
            }
            cls = cls.getSuperclass();
        }
        return map;
    }
}
