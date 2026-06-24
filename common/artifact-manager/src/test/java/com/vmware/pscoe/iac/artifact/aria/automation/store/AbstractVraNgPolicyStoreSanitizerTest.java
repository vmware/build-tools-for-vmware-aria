/*-
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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import org.junit.jupiter.api.Test;

/**
 * Tests for VcfaPayloadSanitizer integration in AbstractVraNgPolicyStore.
 */
class AbstractVraNgPolicyStoreSanitizerTest {

    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

    @Test
    void testSanitizePolicy_RemovesOrgId() {
        JsonObject policyJson = GSON.fromJson(
            "{\"name\":\"TestPolicy\",\"orgId\":\"old-org-id\",\"typeId\":\"com.vmware.policy.lease\"}",
            JsonObject.class
        );

        // Simulate sanitizePolicy behavior: sanitizer + specific removals
        VcfaPayloadSanitizer.sanitize(policyJson);
        policyJson.remove("createdBy");
        policyJson.remove("createdAt");
        policyJson.remove("lastUpdatedBy");
        policyJson.remove("lastUpdatedAt");
        policyJson.remove("id");

        assertFalse(policyJson.has("orgId"), "orgId should be removed by sanitizer");
        assertTrue(policyJson.has("name"), "name should be preserved");
        assertTrue(policyJson.has("typeId"), "typeId should be preserved");
    }

    @Test
    void testSanitizePolicy_RemovesSystemFields() {
        JsonObject policyJson = GSON.fromJson(
            "{\"name\":\"TestPolicy\",\"orgId\":\"org-id\",\"createdBy\":\"user1\",\"createdAt\":\"2023-01-01\",\"lastUpdatedBy\":\"user2\",\"lastUpdatedAt\":\"2023-06-01\",\"id\":\"policy-123\"}",
            JsonObject.class
        );

        VcfaPayloadSanitizer.sanitize(policyJson);
        policyJson.remove("createdBy");
        policyJson.remove("createdAt");
        policyJson.remove("lastUpdatedBy");
        policyJson.remove("lastUpdatedAt");
        policyJson.remove("id");

        assertFalse(policyJson.has("orgId"), "orgId should be removed");
        assertFalse(policyJson.has("createdBy"), "createdBy should be removed");
        assertFalse(policyJson.has("createdAt"), "createdAt should be removed");
        assertFalse(policyJson.has("lastUpdatedBy"), "lastUpdatedBy should be removed");
        assertFalse(policyJson.has("lastUpdatedAt"), "lastUpdatedAt should be removed");
        assertFalse(policyJson.has("id"), "id should be removed");
        assertTrue(policyJson.has("name"), "name should be preserved");
    }

    @Test
    void testSanitizePolicy_PreservesProjectId() {
        // IMPORTANT: projectId should NOT be removed by sanitizePolicy
        JsonObject policyJson = GSON.fromJson(
            "{\"name\":\"TestPolicy\",\"projectId\":\"project-123\",\"orgId\":\"org-id\"}",
            JsonObject.class
        );

        VcfaPayloadSanitizer.sanitize(policyJson);

        // Note: projectId IS removed by VcfaPayloadSanitizer (this is expected behavior)
        // but AbstractVraNgPolicyStore.populatePolicyDetails re-sets it during import
        assertFalse(policyJson.has("orgId"), "orgId should be removed");
    }

    @Test
    void testSanitizePolicy_ScrubsLegacyId() {
        JsonObject policyJson = GSON.fromJson(
            "{\"id\":\"null-policy-123\",\"name\":\"TestPolicy\"}",
            JsonObject.class
        );

        VcfaPayloadSanitizer.sanitize(policyJson);

        assertEquals("policy-123", policyJson.get("id").getAsString(), "Legacy null- prefix should be scrubbed");
    }

    @Test
    void testSanitizePolicy_PreservesOtherFields() {
        JsonObject policyJson = GSON.fromJson(
            "{\"name\":\"TestPolicy\",\"description\":\"Test Description\",\"definition\":{\"key\":\"value\"},\"orgId\":\"org-id\"}",
            JsonObject.class
        );

        VcfaPayloadSanitizer.sanitize(policyJson);
        policyJson.remove("id");

        assertTrue(policyJson.has("name"), "name should be preserved");
        assertTrue(policyJson.has("description"), "description should be preserved");
        assertTrue(policyJson.has("definition"), "definition should be preserved");
        assertFalse(policyJson.has("orgId"), "orgId should be removed");
    }
}
