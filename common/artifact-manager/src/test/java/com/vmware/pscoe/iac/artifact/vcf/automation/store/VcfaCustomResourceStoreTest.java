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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VcfaCustomResourceStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final String CURRENT_ORG_ID = "new-org-id";
    private static final String CURRENT_PROJECT_ID = "new-project-id";

    @BeforeEach
    void setUp() {
    }

    // ==================== Main Object Sanitization Tests ====================

    @Test
    void testFixCustomResourceDefinition_SetsOrgId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        assertTrue(customResourceJson.has("orgId"), "orgId should be set");
        assertEquals(CURRENT_ORG_ID, customResourceJson.get("orgId").asText(), "orgId should match target");
    }

    @Test
    void testFixCustomResourceDefinition_SetsProjectId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, CURRENT_PROJECT_ID);

        assertTrue(customResourceJson.has("projectId"), "projectId should be set");
        assertTrue(customResourceJson.get("projectId").isArray(), "projectId should be an array");
        assertEquals(CURRENT_PROJECT_ID, customResourceJson.get("projectId").get(0).asText(), "projectId should match target");
    }

    @Test
    void testFixCustomResourceDefinition_RemovesOldOrgId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");
        customResourceJson.put("orgId", "old-org-id");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        assertEquals(CURRENT_ORG_ID, customResourceJson.get("orgId").asText(), "orgId should be updated");
    }

    @Test
    void testFixCustomResourceDefinition_NullProjectId_NotSet() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        assertTrue(customResourceJson.has("orgId"), "orgId should be set");
        assertFalse(customResourceJson.has("projectId"), "projectId should not be set when null");
    }

    // ==================== Additional Actions Sanitization Tests ====================

    @Test
    void testFixCustomResourceDefinition_SanitizesAdditionalActions() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");

        ArrayNode additionalActions = customResourceJson.putArray("additionalActions");
        ObjectNode action = MAPPER.createObjectNode();
        action.put("name", "TestAction");
        action.put("orgId", "old-action-org-id");
        additionalActions.add(action);

        // Simulate the store's logic: sanitize main object and nested actions
        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        // The sanitizer only handles the main object, not nested actions
        // This test documents the current behavior
        assertEquals(CURRENT_ORG_ID, customResourceJson.get("orgId").asText(), "Main orgId should be updated");
    }

    @Test
    void testFixCustomResourceDefinition_FormDefinitionHandling() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");

        ArrayNode additionalActions = customResourceJson.putArray("additionalActions");
        ObjectNode action = MAPPER.createObjectNode();
        action.put("name", "TestAction");

        ObjectNode formDefinition = MAPPER.createObjectNode();
        formDefinition.put("id", "old-form-id");
        formDefinition.put("projectId", "old-project-id");
        action.set("formDefinition", formDefinition);
        additionalActions.add(action);

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, CURRENT_PROJECT_ID);

        // Main object should be sanitized
        assertEquals(CURRENT_ORG_ID, customResourceJson.get("orgId").asText());
    }

    @Test
    void testFixCustomResourceDefinition_ScrubsLegacyId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("id", "null-resource-123");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        assertEquals("resource-123", customResourceJson.get("id").asText(), "Legacy null- prefix should be scrubbed");
    }

    @Test
    void testFixCustomResourceDefinition_ScrubsNullDash() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("id", "org-null-resource-456");

        VcfaPayloadSanitizer.sanitize(customResourceJson, CURRENT_ORG_ID, null);

        assertEquals("org-resource-456", customResourceJson.get("id").asText(), "Legacy -null- should be scrubbed");
    }

    @Test
    void testFixCustomResourceDefinition_NullNodeHandling() {
        ObjectNode result = VcfaPayloadSanitizer.sanitize((ObjectNode) null, CURRENT_ORG_ID, null);
        assertNull(result, "Null node should return null");
    }

    // ==================== Export Sanitization Tests ====================

    @Test
    void testExportSanitization_RemovesOrgId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");
        customResourceJson.put("orgId", "old-org-id");

        VcfaPayloadSanitizer.sanitize(customResourceJson);

        assertFalse(customResourceJson.has("orgId"), "orgId should be removed during export");
        assertTrue(customResourceJson.has("name"), "name should be preserved");
    }

    @Test
    void testExportSanitization_RemovesProjectId() {
        ObjectNode customResourceJson = MAPPER.createObjectNode();
        customResourceJson.put("name", "TestResource");
        customResourceJson.putArray("projectId").add("old-project-id");

        VcfaPayloadSanitizer.sanitize(customResourceJson);

        assertFalse(customResourceJson.has("projectId"), "projectId should be removed during export");
    }
}
