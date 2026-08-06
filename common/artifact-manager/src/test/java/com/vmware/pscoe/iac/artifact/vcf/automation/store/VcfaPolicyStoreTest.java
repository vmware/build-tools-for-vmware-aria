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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import org.junit.jupiter.api.Test;

class VcfaPolicyStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // ==================== Export Sanitization Tests ====================

    @Test
    void testExportSanitization_RemovesOrgId() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");
        policyJson.put("orgId", "old-org-id");
        policyJson.put("typeId", "com.vmware.policy.approval");

        VcfaPayloadSanitizer.sanitize(policyJson);

        assertFalse(policyJson.has("orgId"), "orgId should be removed during export");
        assertTrue(policyJson.has("name"), "name should be preserved");
        assertTrue(policyJson.has("typeId"), "typeId should be preserved");
    }

    @Test
    void testExportSanitization_RemovesProjectId() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");
        policyJson.putArray("projectId").add("old-project-id");

        VcfaPayloadSanitizer.sanitize(policyJson);

        assertFalse(policyJson.has("projectId"), "projectId should be removed during export");
    }

    @Test
    void testExportSanitization_ScrubsLegacyId() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("id", "null-policy-123");

        VcfaPayloadSanitizer.sanitize(policyJson);

        assertEquals("policy-123", policyJson.get("id").asText(), "Legacy null- prefix should be scrubbed");
    }

    @Test
    void testExportSanitization_NullNodeHandling() {
        ObjectNode result = VcfaPayloadSanitizer.sanitize((ObjectNode) null);
        assertNull(result, "Null node should return null");
    }

    // ==================== Import Sanitization Tests ====================

    @Test
    void testImportSanitization_SetsOrgId() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");

        String currentOrgId = "new-org-id";
        VcfaPayloadSanitizer.sanitize(policyJson, currentOrgId, null);

        assertTrue(policyJson.has("orgId"), "orgId should be set");
        assertEquals(currentOrgId, policyJson.get("orgId").asText(), "orgId should match target");
    }

    @Test
    void testImportSanitization_SetsProjectId() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");

        String currentProjectId = "new-project-id";
        VcfaPayloadSanitizer.sanitize(policyJson, "org-id", currentProjectId);

        assertTrue(policyJson.has("projectId"), "projectId should be set");
        assertTrue(policyJson.get("projectId").isArray(), "projectId should be an array");
        assertEquals(currentProjectId, policyJson.get("projectId").get(0).asText(), "projectId should match target");
    }

    @Test
    void testImportSanitization_NullProjectId_DoesNotSet() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");

        VcfaPayloadSanitizer.sanitize(policyJson, "org-id", null);

        assertTrue(policyJson.has("orgId"), "orgId should be set");
        assertFalse(policyJson.has("projectId"), "projectId should not be set when null");
    }

    @Test
    void testImportSanitization_RemovesOldOrgIdBeforeSetting() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");
        policyJson.put("orgId", "old-org-id");

        String currentOrgId = "new-org-id";
        VcfaPayloadSanitizer.sanitize(policyJson, currentOrgId, null);

        assertEquals(currentOrgId, policyJson.get("orgId").asText(), "orgId should be updated to new value");
    }

    @Test
    void testImportSanitization_WithConstraints() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("name", "TestPolicy");
        ObjectNode constraints = policyJson.putObject("constraints");
        constraints.putArray("projectId").add("old-constraint-project");

        String currentProjectId = "new-project-id";
        VcfaPayloadSanitizer.sanitize(policyJson, "org-id", currentProjectId);

        assertTrue(policyJson.has("projectId"), "projectId should be set on root");
        assertFalse(constraints.has("projectId"), "projectId should be removed from constraints");
    }

    @Test
    void testImportSanitization_ScrubsLegacyIdWithNewValues() {
        ObjectNode policyJson = MAPPER.createObjectNode();
        policyJson.put("id", "policy-null-123");
        policyJson.put("orgId", "old-org");

        VcfaPayloadSanitizer.sanitize(policyJson, "new-org", null);

        assertEquals("policy-123", policyJson.get("id").asText(), "Legacy ID should be scrubbed");
        assertEquals("new-org", policyJson.get("orgId").asText(), "orgId should be updated");
    }
}
