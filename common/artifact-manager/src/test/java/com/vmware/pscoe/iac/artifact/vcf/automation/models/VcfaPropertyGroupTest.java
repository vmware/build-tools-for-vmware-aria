/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaPropertyGroup serialization.
 */
class VcfaPropertyGroupTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"pg-1\"," +
            "\"name\":\"group1\"," +
            "\"displayName\":\"Group One\"," +
            "\"description\":\"First property group\"," +
            "\"type\":\"INPUT\"," +
            "\"orgId\":\"org-1\"," +
            "\"organization\":\"orgA\"," +
            "\"projectId\":\"proj-1\"," +
            "\"createdAt\":\"2026-01-01T00:00:00Z\"," +
            "\"createdBy\":\"admin\"," +
            "\"updatedAt\":\"2026-01-02T00:00:00Z\"," +
            "\"updatedBy\":\"admin\"," +
            "\"properties\":{\"cpu\":{\"type\":\"integer\"}}" +
            "}";

        VcfaPropertyGroup group = MAPPER.readValue(json, VcfaPropertyGroup.class);

        assertEquals("pg-1", group.getId());
        assertEquals("group1", group.getName());
        assertEquals("Group One", group.getDisplayName());
        assertEquals("First property group", group.getDescription());
        assertEquals("INPUT", group.getType());
        assertEquals("org-1", group.getOrgId());
        assertEquals("orgA", group.getOrganization());
        assertEquals("proj-1", group.getProjectId());
        assertEquals("2026-01-01T00:00:00Z", group.getCreatedAt());
        assertEquals("admin", group.getCreatedBy());
        assertEquals("2026-01-02T00:00:00Z", group.getUpdatedAt());
        assertEquals("admin", group.getUpdatedBy());
        @SuppressWarnings("unchecked")
        Map<String, Object> cpuProperty = (Map<String, Object>) group.getProperties().get("cpu");
        assertEquals("integer", cpuProperty.get("type"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaPropertyGroup group = new VcfaPropertyGroup();
        group.setId("pg-2");
        group.setName("group2");
        group.setDisplayName("Group Two");
        group.setType("INPUT");
        group.setProjectId("proj-2");
        group.setProperties(Collections.singletonMap("memory", Collections.singletonMap("type", "integer")));

        String json = MAPPER.writeValueAsString(group);
        VcfaPropertyGroup roundTripped = MAPPER.readValue(json, VcfaPropertyGroup.class);

        assertEquals(group.getId(), roundTripped.getId());
        assertEquals(group.getDisplayName(), roundTripped.getDisplayName());
        assertEquals(group.getProjectId(), roundTripped.getProjectId());
    }
}
