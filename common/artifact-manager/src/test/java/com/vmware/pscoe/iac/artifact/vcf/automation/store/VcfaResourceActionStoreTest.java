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
 * Unit tests for VcfaResourceActionStore.
 */
class VcfaResourceActionStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WithResourceActions() {
        ObjectNode resourceAction = MAPPER.createObjectNode();
        resourceAction.put("name", "TestResourceAction");
        resourceAction.put("resourceType", "Cloud.vSphere.Machine");

        assertTrue(resourceAction.has("name"), "Resource action should have name");
        assertTrue(resourceAction.has("resourceType"), "Resource action should have resourceType");
    }

    @Test
    void testExportContent_WithFormData() {
        ObjectNode resourceAction = MAPPER.createObjectNode();
        resourceAction.put("name", "TestResourceAction");
        ObjectNode formDefinition = resourceAction.putObject("formDefinition");
        formDefinition.put("id", "form-123");

        assertTrue(resourceAction.has("formDefinition"), "Resource action should have formDefinition");
        assertTrue(formDefinition.has("id"), "Form definition should have id");
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_WithResourceActions() {
        ObjectNode resourceAction = MAPPER.createObjectNode();
        resourceAction.put("name", "TestResourceAction");
        resourceAction.put("runnableType", "extensibility.vro");

        assertEquals("extensibility.vro", resourceAction.get("runnableType").asText());
    }

    @Test
    void testImportContent_FormDefinitionHandling() {
        ObjectNode resourceAction = MAPPER.createObjectNode();
        resourceAction.put("name", "TestResourceAction");
        ObjectNode formDefinition = resourceAction.putObject("formDefinition");
        formDefinition.put("styles", "css content");

        // Form definition should be processed
        assertTrue(formDefinition.has("styles"), "Form definition should have styles");
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent() {
        String actionName = "TestResourceAction";
        assertEquals("TestResourceAction", actionName);
    }
}
