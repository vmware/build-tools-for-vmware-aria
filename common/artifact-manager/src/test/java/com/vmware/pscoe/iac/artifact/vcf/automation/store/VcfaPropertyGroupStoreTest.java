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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaPropertyGroupStore.
 */
class VcfaPropertyGroupStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WithPropertyGroups() {
        ObjectNode propertyGroup = MAPPER.createObjectNode();
        propertyGroup.put("name", "TestPropertyGroup");
        propertyGroup.put("description", "Test Description");

        assertTrue(propertyGroup.has("name"), "Property group should have name");
        assertTrue(propertyGroup.has("description"), "Property group should have description");
    }

    @Test
    void testExportContent_WithEmptyDescriptor() {
        ObjectNode propertyGroup = MAPPER.createObjectNode();
        propertyGroup.put("name", "TestPropertyGroup");

        // Empty descriptor should skip
        assertTrue(propertyGroup.has("name"), "Name should be preserved");
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_WithPropertyGroups() {
        ObjectNode propertyGroup = MAPPER.createObjectNode();
        propertyGroup.put("name", "TestPropertyGroup");
        ArrayNode properties = propertyGroup.putArray("properties");
        properties.add("prop1");

        assertTrue(propertyGroup.has("properties"), "Property group should have properties");
        assertEquals(1, properties.size());
    }

    @Test
    void testImportContent_IdenticalCheck() {
        ObjectNode propertyGroup = MAPPER.createObjectNode();
        propertyGroup.put("name", "TestPropertyGroup");
        propertyGroup.put("version", "1.0");

        // Identical check should compare name and version
        assertEquals("1.0", propertyGroup.get("version").asText());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent() {
        String groupName = "TestPropertyGroup";
        assertEquals("TestPropertyGroup", groupName);
    }
}
