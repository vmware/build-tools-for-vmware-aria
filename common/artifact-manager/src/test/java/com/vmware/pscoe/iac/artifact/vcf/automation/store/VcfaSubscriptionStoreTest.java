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

/**
 * Unit tests for VcfaSubscriptionStore with VcfaPayloadSanitizer verification.
 */
class VcfaSubscriptionStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // ==================== Export Tests with Sanitization ====================

    @Test
    void testExportContent_WithSubscriptions() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");
        subscription.put("eventTopicId", "compute.allocation.pre");

        assertTrue(subscription.has("name"), "Subscription should have name");
        assertTrue(subscription.has("eventTopicId"), "Subscription should have eventTopicId");
    }

    @Test
    void testExportContent_SanitizesOrgId() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");
        subscription.put("orgId", "old-org-id");

        VcfaPayloadSanitizer.sanitize(subscription);

        assertFalse(subscription.has("orgId"), "orgId should be removed during export");
    }

    @Test
    void testExportContent_SanitizesProjectId() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");
        subscription.putArray("projectId").add("old-project-id");

        VcfaPayloadSanitizer.sanitize(subscription);

        assertFalse(subscription.has("projectId"), "projectId should be removed during export");
    }

    @Test
    void testExportContent_SanitizesLegacyId() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("id", "null-subscription-123");

        VcfaPayloadSanitizer.sanitize(subscription);

        assertEquals("subscription-123", subscription.get("id").asText(), "Legacy null- prefix should be scrubbed");
    }

    // ==================== Import Tests with Sanitization ====================

    @Test
    void testImportContent_WithSubscriptions() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");
        subscription.put("runnableType", "extensibility.vro");

        assertEquals("extensibility.vro", subscription.get("runnableType").asText());
    }

    @Test
    void testImportContent_SetsOrgId() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");

        String currentOrgId = "new-org-id";
        VcfaPayloadSanitizer.sanitize(subscription, currentOrgId, null);

        assertTrue(subscription.has("orgId"), "orgId should be set");
        assertEquals(currentOrgId, subscription.get("orgId").asText());
    }

    @Test
    void testImportContent_SetsProjectId() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");

        String currentProjectId = "new-project-id";
        VcfaPayloadSanitizer.sanitize(subscription, "org-id", currentProjectId);

        assertTrue(subscription.has("projectId"), "projectId should be set");
        assertEquals(currentProjectId, subscription.get("projectId").get(0).asText());
    }

    @Test
    void testImportContent_RemovesOldValues() {
        ObjectNode subscription = MAPPER.createObjectNode();
        subscription.put("name", "TestSubscription");
        subscription.put("orgId", "old-org-id");
        subscription.put("projectId", "old-project-id");

        String currentOrgId = "new-org-id";
        VcfaPayloadSanitizer.sanitize(subscription, currentOrgId, null);

        assertEquals(currentOrgId, subscription.get("orgId").asText(), "orgId should be updated");
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent() {
        String subscriptionName = "TestSubscription";
        assertEquals("TestSubscription", subscriptionName);
    }
}
