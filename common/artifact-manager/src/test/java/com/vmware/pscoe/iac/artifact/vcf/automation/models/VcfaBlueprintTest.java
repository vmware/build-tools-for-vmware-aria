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

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaBlueprint serialization and derived maps.
 */
class VcfaBlueprintTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"bp-123\"," +
            "\"name\":\"Small VM\"," +
            "\"description\":\"A small VM blueprint\"," +
            "\"content\":\"{\\\"formatVersion\\\":\\\"1.0\\\"}\"," +
            "\"styles\":\".body { color: red; }\"," +
            "\"projectId\":\"proj-1\"," +
            "\"requestScopeOrg\":true" +
            "}";

        VcfaBlueprint bp = MAPPER.readValue(json, VcfaBlueprint.class);

        assertEquals("bp-123", bp.getId());
        assertEquals("Small VM", bp.getName());
        assertEquals("A small VM blueprint", bp.getDescription());
        assertEquals("{\"formatVersion\":\"1.0\"}", bp.getContent());
        assertEquals(".body { color: red; }", bp.getStyles());
        assertEquals("proj-1", bp.getProjectId());
        assertEquals(Boolean.TRUE, bp.getRequestScopeOrg());
    }

    @Test
    void testSerialize_NonNullFields() throws Exception {
        VcfaBlueprint bp = new VcfaBlueprint();
        bp.setId("bp-456");
        bp.setName("Medium VM");
        bp.setDescription("Medium blueprint");

        String json = MAPPER.writeValueAsString(bp);

        assertTrue(json.contains("\"id\":\"bp-456\""));
        assertTrue(json.contains("\"name\":\"Medium VM\""));
        assertFalse(json.contains("content"));
    }

    @Test
    void testToMap_ContainsAllFields() {
        VcfaBlueprint bp = new VcfaBlueprint();
        bp.setId("bp-789");
        bp.setName("Large VM");
        bp.setDescription("Large blueprint");
        bp.setContent("{}");
        bp.setStyles(".foo {}");
        bp.setProjectId("proj-2");
        bp.setRequestScopeOrg(false);

        Map<String, Object> map = bp.toMap();

        assertEquals("bp-789", map.get("id"));
        assertEquals("Large VM", map.get("name"));
        assertEquals("Large blueprint", map.get("description"));
        assertEquals("{}", map.get("content"));
        assertEquals(".foo {}", map.get("styles"));
        assertEquals("proj-2", map.get("projectId"));
        assertEquals(Boolean.FALSE, map.get("requestScopeOrg"));
    }

    @Test
    void testAsExportMap_EqualsToMap() {
        VcfaBlueprint bp = new VcfaBlueprint();
        bp.setId("bp-000");
        bp.setName("Empty");

        assertEquals(bp.toMap(), bp.asExportMap());
    }
}
