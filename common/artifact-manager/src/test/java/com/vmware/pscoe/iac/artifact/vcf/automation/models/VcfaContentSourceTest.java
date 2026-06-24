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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaContentSource model serialization/deserialization.
 */
class VcfaContentSourceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== typeId Serialization Tests ====================

    @Test
    void testDeserialize_WithTypeIdField() throws Exception {
        // JSON with typeId string (as returned by VCF 9.1 API)
        String json = "{\"id\":\"test-id\",\"name\":\"Test Source\",\"typeId\":\"com.vmw.vro.workflow\"}";

        VcfaContentSource source = MAPPER.readValue(json, VcfaContentSource.class);

        assertEquals("test-id", source.getId());
        assertEquals("Test Source", source.getName());
        assertEquals("com.vmw.vro.workflow", source.getTypeId());
        // getType() should derive from typeId
        assertNotNull(source.getType());
        assertEquals("com.vmw.vro.workflow", source.getType().getId());
    }

    @Test
    void testDeserialize_WithNestedTypeObject() throws Exception {
        // JSON with nested type object (legacy format)
        String json = "{\"id\":\"test-id\",\"name\":\"Test Source\",\"type\":{\"id\":\"com.vmw.vro.workflow\",\"name\":\"vRO Workflow\"}}";

        VcfaContentSource source = MAPPER.readValue(json, VcfaContentSource.class);

        assertEquals("test-id", source.getId());
        assertEquals("Test Source", source.getName());
        assertNotNull(source.getType());
        assertEquals("com.vmw.vro.workflow", source.getType().getId());
        assertEquals("vRO Workflow", source.getType().getName());
    }

    @Test
    void testSerialize_WithTypeId() throws Exception {
        VcfaContentSource source = new VcfaContentSource();
        source.setId("test-id");
        source.setName("Test Source");
        source.setTypeId("com.vmw.vro.workflow");

        String json = MAPPER.writeValueAsString(source);

        assertTrue(json.contains("\"typeId\":\"com.vmw.vro.workflow\""), "Should serialize typeId field");
    }

    @Test
    void testSerialize_WithNestedType() throws Exception {
        VcfaContentSource source = new VcfaContentSource();
        source.setId("test-id");
        source.setName("Test Source");

        VcfaContentSource.VcfaSourceType type = new VcfaContentSource.VcfaSourceType();
        type.setId("com.vmw.vro.workflow");
        type.setName("vRO Workflow");
        source.setType(type);

        String json = MAPPER.writeValueAsString(source);

        assertTrue(json.contains("\"type\":"), "Should serialize nested type object");
        assertTrue(json.contains("\"id\":\"com.vmw.vro.workflow\""), "Should contain type id");
    }

    @Test
    void testGetType_DerivesFromTypeId() {
        VcfaContentSource source = new VcfaContentSource();
        source.setTypeId("com.vmw.vro.workflow");

        VcfaContentSource.VcfaSourceType type = source.getType();

        assertNotNull(type, "getType() should derive from typeId when type is null");
        assertEquals("com.vmw.vro.workflow", type.getId());
    }

    @Test
    void testGetType_ReturnsNullWhenNeitherSet() {
        VcfaContentSource source = new VcfaContentSource();

        VcfaContentSource.VcfaSourceType type = source.getType();

        assertNull(type, "getType() should return null when neither type nor typeId is set");
    }

    @Test
    void testSetType_SyncsTypeId() {
        VcfaContentSource source = new VcfaContentSource();

        VcfaContentSource.VcfaSourceType type = new VcfaContentSource.VcfaSourceType();
        type.setId("com.vmw.vro.workflow");
        source.setType(type);

        assertEquals("com.vmw.vro.workflow", source.getTypeId(), "setType() should sync typeId");
    }

    @Test
    void testGetTypeId_DerivesFromType() {
        VcfaContentSource source = new VcfaContentSource();

        VcfaContentSource.VcfaSourceType type = new VcfaContentSource.VcfaSourceType();
        type.setId("com.vmw.blueprint");
        source.setType(type);

        assertEquals("com.vmw.blueprint", source.getTypeId(), "getTypeId() should derive from type");
    }

    @Test
    void testFullRoundTrip_WithTypeId() throws Exception {
        // Simulate what VCF 9.1 API returns
        String apiResponse = "{" +
            "\"id\":\"19f67ab7-764c-40e9-97b8-76176d418141\"," +
            "\"name\":\"Orchestrator_Development\"," +
            "\"typeId\":\"com.vmw.vro.workflow\"," +
            "\"description\":\"Test Description\"," +
            "\"global\":true" +
            "}";

        VcfaContentSource source = MAPPER.readValue(apiResponse, VcfaContentSource.class);

        assertEquals("19f67ab7-764c-40e9-97b8-76176d418141", source.getId());
        assertEquals("Orchestrator_Development", source.getName());
        assertEquals("com.vmw.vro.workflow", source.getTypeId());
        assertEquals("Test Description", source.getDescription());
        assertEquals(true, source.getGlobal());
    }
}
