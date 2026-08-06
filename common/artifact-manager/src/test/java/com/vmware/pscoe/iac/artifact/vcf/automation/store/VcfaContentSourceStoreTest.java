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
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import org.junit.jupiter.api.Test;

class VcfaContentSourceStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== Export Sanitization Tests ====================

    @Test
    void testExportContentSanitization_RemovesOrgId() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");
        contentSourceJson.put("orgId", "old-org-id");

        VcfaPayloadSanitizer.sanitize(contentSourceJson);

        assertFalse(contentSourceJson.has("orgId"), "orgId should be removed during export");
        assertTrue(contentSourceJson.has("name"), "name should be preserved");
    }

    @Test
    void testExportContentSanitization_RemovesProjectId() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");
        contentSourceJson.put("projectId", "old-project-id");

        VcfaPayloadSanitizer.sanitize(contentSourceJson);

        assertFalse(contentSourceJson.has("projectId"), "projectId should be removed during export");
    }

    @Test
    void testExportContentSanitization_ScrubsLegacyId() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("id", "null-source-123");

        VcfaPayloadSanitizer.sanitize(contentSourceJson);

        assertEquals("source-123", contentSourceJson.get("id").asText(), "Legacy null- prefix should be scrubbed");
    }

    @Test
    void testExportContentSanitization_PreservesOtherFields() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");
        contentSourceJson.put("type", "vro");
        contentSourceJson.put("description", "Test Description");
        contentSourceJson.put("orgId", "old-org-id");

        VcfaPayloadSanitizer.sanitize(contentSourceJson);

        assertFalse(contentSourceJson.has("orgId"), "orgId should be removed");
        assertTrue(contentSourceJson.has("name"), "name should be preserved");
        assertTrue(contentSourceJson.has("type"), "type should be preserved");
        assertTrue(contentSourceJson.has("description"), "description should be preserved");
    }

    @Test
    void testExportContentSanitization_NullNodeHandling() {
        ObjectNode result = VcfaPayloadSanitizer.sanitize((ObjectNode) null);
        assertNull(result, "Null node should return null");
    }

    // ==================== Import Sanitization Tests ====================

    @Test
    void testImportContentSanitization_SetsOrgId() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");

        String currentOrgId = "new-org-id";
        VcfaPayloadSanitizer.sanitize(contentSourceJson, currentOrgId, null);

        assertTrue(contentSourceJson.has("orgId"), "orgId should be set");
        assertEquals(currentOrgId, contentSourceJson.get("orgId").asText(), "orgId should match target");
    }

    @Test
    void testImportContentSanitization_SetsProjectId() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");

        String currentProjectId = "new-project-id";
        VcfaPayloadSanitizer.sanitize(contentSourceJson, "org-id", currentProjectId);

        assertTrue(contentSourceJson.has("projectId"), "projectId should be set");
        assertEquals(currentProjectId, contentSourceJson.get("projectId").get(0).asText(), "projectId should match target");
    }

    @Test
    void testImportContentSanitization_RemovesOldValues() {
        ObjectNode contentSourceJson = MAPPER.createObjectNode();
        contentSourceJson.put("name", "TestContentSource");
        contentSourceJson.put("orgId", "old-org-id");
        contentSourceJson.put("projectId", "old-project-id");

        String currentOrgId = "new-org-id";
        String currentProjectId = "new-project-id";
        VcfaPayloadSanitizer.sanitize(contentSourceJson, currentOrgId, currentProjectId);

        assertEquals(currentOrgId, contentSourceJson.get("orgId").asText(), "orgId should be updated");
    }
}
