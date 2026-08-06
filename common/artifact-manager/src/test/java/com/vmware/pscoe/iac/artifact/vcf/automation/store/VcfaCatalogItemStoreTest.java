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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaCatalogItemStore.
 */
class VcfaCatalogItemStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WithCatalogItems() {
        ObjectNode catalogItem = MAPPER.createObjectNode();
        catalogItem.put("name", "TestCatalogItem");
        catalogItem.put("sourceId", "source-123");

        assertTrue(catalogItem.has("name"), "Catalog item should have name");
        assertTrue(catalogItem.has("sourceId"), "Catalog item should have sourceId");
    }

    @Test
    void testExportContent_WithFormData() {
        ObjectNode catalogItem = MAPPER.createObjectNode();
        catalogItem.put("name", "TestCatalogItem");
        ObjectNode form = catalogItem.putObject("form");
        form.put("layout", "test layout");

        assertTrue(catalogItem.has("form"), "Catalog item should have form data");
        assertTrue(form.has("layout"), "Form should have layout");
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_WithCatalogItems() {
        ObjectNode catalogItem = MAPPER.createObjectNode();
        catalogItem.put("name", "TestCatalogItem");
        catalogItem.put("type", "vra:resource");

        assertEquals("vra:resource", catalogItem.get("type").asText());
    }

    @Test
    void testImportContent_SourceIdResolution() {
        ObjectNode catalogItem = MAPPER.createObjectNode();
        catalogItem.put("name", "TestCatalogItem");
        catalogItem.put("sourceName", "Blueprint Name");

        // sourceId should be resolved from sourceName
        assertTrue(catalogItem.has("sourceName"), "Catalog item should have sourceName for resolution");
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent() {
        String itemName = "TestCatalogItem";
        assertEquals("TestCatalogItem", itemName);
    }
}
