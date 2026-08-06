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
package com.vmware.pscoe.iac.artifact.vcf.automation.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

class VcfaPayloadSanitizerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    // ==================== Jackson: scrub (no-arg) ====================

    @Test
    void scrubJackson_removesOrgIdAndProjectId() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", "sub_abc123");
        node.put("orgId", "old-org-id");
        node.putArray("projectId").add("old-project-id");
        ObjectNode constraints = node.putObject("constraints");
        constraints.putArray("projectId").add("constraint-project-id");

        VcfaPayloadSanitizer.sanitize(node);

        assertFalse(node.has("orgId"));
        assertFalse(node.has("projectId"));
        assertTrue(constraints.has("projectId"));
    }

    @Test
    void scrubJackson_nullNode_returnsNull() {
        assertNull(VcfaPayloadSanitizer.sanitize((ObjectNode) null));
    }

    @Test
    void scrubJackson_scrubsLegacyIdWithNullPrefix() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", "sub-null-myName");
        VcfaPayloadSanitizer.sanitize(node);
        assertEquals("sub-myName", node.get("id").asText());
    }

    @Test
    void scrubJackson_scrubsLegacyIdWithNullDash() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", "org-null-sub_myName");
        VcfaPayloadSanitizer.sanitize(node);
        assertEquals("org-sub_myName", node.get("id").asText());
    }

    @Test
    void scrubJackson_nullIdNode_doesNotCrash() {
        ObjectNode node = MAPPER.createObjectNode();
        VcfaPayloadSanitizer.sanitize(node);
        assertFalse(node.has("id"));
    }

    // ==================== Jackson: replace (two-arg) ====================

    @Test
    void replaceJackson_injectsOrgIdAndProjectId() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", "sub_abc123");
        node.put("orgId", "old-org");
        ObjectNode constraints = node.putObject("constraints");
        constraints.putArray("projectId").add("old-pid");

        VcfaPayloadSanitizer.sanitize(node, "new-org", "new-pid");

        assertEquals("new-org", node.get("orgId").asText());
        assertTrue(node.get("projectId").isArray());
        assertEquals("new-pid", node.get("projectId").get(0).asText());
        assertFalse(constraints.has("projectId"));
    }

    @Test
    void replaceJackson_noConstraints_setsProjectIdOnNode() {
        ObjectNode node = MAPPER.createObjectNode();
        VcfaPayloadSanitizer.sanitize(node, "org1", "pid1");

        assertEquals("org1", node.get("orgId").asText());
        assertTrue(node.get("projectId").isArray());
        assertEquals("pid1", node.get("projectId").get(0).asText());
    }

    @Test
    void replaceJackson_nullOrgId_removesOrgId() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("orgId", "old-org");
        node.putArray("projectId").add("old-pid");
        VcfaPayloadSanitizer.sanitize(node, null, "pid1");

        assertFalse(node.has("orgId"));
        assertEquals("pid1", node.get("projectId").get(0).asText());
    }

    @Test
    void replaceJackson_nullProjectId_removesProjectId() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("orgId", "old-org");
        ObjectNode constraints = node.putObject("constraints");
        constraints.putArray("projectId").add("old-pid");

        VcfaPayloadSanitizer.sanitize(node, "new-org", null);

        assertEquals("new-org", node.get("orgId").asText());
        assertFalse(node.has("projectId"));
        assertFalse(constraints.has("projectId"));
    }

    @Test
    void replaceJackson_constraintsNotObject_setsProjectIdOnNode() {
        ObjectNode node = MAPPER.createObjectNode();
        node.putArray("constraints").add("not-an-object");

        VcfaPayloadSanitizer.sanitize(node, "org1", "pid1");

        assertTrue(node.get("projectId").isArray());
        assertTrue(node.get("constraints").isArray());
        assertFalse(node.get("constraints").isEmpty());
    }

    @Test
    void replaceJackson_bothNull_cleansEverything() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("orgId", "old-org");
        node.putArray("projectId").add("old-pid");

        VcfaPayloadSanitizer.sanitize(node, null, null);

        assertFalse(node.has("orgId"));
        assertFalse(node.has("projectId"));
    }

    @Test
    void replaceJackson_nullNode_returnsNull() {
        assertNull(VcfaPayloadSanitizer.sanitize((ObjectNode) null, "org", "pid"));
    }

    // ==================== Gson: scrub (no-arg) ====================

    @Test
    void scrubGson_removesOrgIdAndProjectId() {
        JsonObject obj = GSON.fromJson(
                "{\"id\":\"sub_abc\",\"orgId\":\"old-org\",\"projectId\":[\"old-pid\"]}", JsonObject.class);
        obj.add("constraints", new JsonObject());

        JsonElement result = VcfaPayloadSanitizer.sanitize((JsonElement) obj);

        assertNotNull(result);
        assertSame(result, obj);
        assertFalse(obj.has("orgId"));
        assertFalse(obj.has("projectId"));
    }

    @Test
    void scrubGson_nullElement_returnsAsIs() {
        JsonObject testObj = new JsonObject();
        assertSame(testObj, VcfaPayloadSanitizer.sanitize(testObj));
        assertNull(VcfaPayloadSanitizer.sanitize((JsonElement) null));
    }

    @Test
    void scrubGson_scrubsLegacyIdWithNullPrefix() {
        JsonObject obj = GSON.fromJson("{\"id\":\"null-sub_myName\"}", JsonObject.class);
        VcfaPayloadSanitizer.sanitize(obj);
        assertEquals("sub_myName", obj.get("id").getAsString());
    }

    @Test
    void scrubGson_scrubsLegacyIdWithNullDash() {
        JsonObject obj = GSON.fromJson("{\"id\":\"org-null-sub_myName\"}", JsonObject.class);
        VcfaPayloadSanitizer.sanitize(obj);
        assertEquals("org-sub_myName", obj.get("id").getAsString());
    }

    @Test
    void scrubGson_nullIdValue_doesNotCrash() {
        JsonObject obj = new JsonObject();
        obj.add("id", com.google.gson.JsonNull.INSTANCE);
        VcfaPayloadSanitizer.sanitize(obj);
        // Should still have the null id (unchanged)
        assertTrue(obj.get("id").isJsonNull());
    }

    @Test
    void scrubGson_nonJsonObject_returnsAsIs() {
        JsonElement arr = GSON.fromJson("[1,2,3]", JsonElement.class);
        assertSame(arr, VcfaPayloadSanitizer.sanitize(arr));
    }

    // ==================== Gson: replace (two-arg) ====================

    @Test
    void replaceGson_injectsOrgIdAndProjectId() {
        JsonObject obj = GSON.fromJson(
                "{\"id\":\"sub_abc\",\"orgId\":\"old\",\"constraints\":{}}", JsonObject.class);

        VcfaPayloadSanitizer.sanitize(obj, "new-org", "new-pid");

        assertEquals("new-org", obj.get("orgId").getAsString());
        assertTrue(obj.get("constraints").isJsonObject());
        JsonArray pidArr = obj.get("constraints").getAsJsonObject().get("projectId").getAsJsonArray();
        assertEquals("new-pid", pidArr.get(0).getAsString());
    }

    @Test
    void replaceGson_noConstraints_setsProjectIdOnNode() {
        JsonObject obj = GSON.fromJson("{\"id\":\"sub_abc\"}", JsonObject.class);
        VcfaPayloadSanitizer.sanitize(obj, "org1", "pid1");

        assertTrue(obj.get("projectId").isJsonArray());
        assertEquals("pid1", obj.get("projectId").getAsJsonArray().get(0).getAsString());
    }

    @Test
    void replaceGson_nullOrgId_removesOrgId() {
        JsonObject obj = GSON.fromJson("{\"orgId\":\"old\"}", JsonObject.class);
        VcfaPayloadSanitizer.sanitize(obj, null, "pid1");

        assertFalse(obj.has("orgId"));
    }

    @Test
    void replaceGson_nullProjectId_removesProjectId() {
        JsonObject obj = GSON.fromJson("{\"orgId\":\"old\",\"constraints\":{}}", JsonObject.class);
        VcfaPayloadSanitizer.sanitize(obj, "new-org", null);

        assertEquals("new-org", obj.get("orgId").getAsString());
        assertFalse(obj.has("projectId"));
        assertFalse(obj.get("constraints").getAsJsonObject().has("projectId"));
    }

    @Test
    void replaceGson_constraintsNotArray_setsProjectIdOnNode() {
        JsonObject obj = new JsonObject();
        obj.add("constraints", new com.google.gson.JsonArray());

        VcfaPayloadSanitizer.sanitize(obj, "org1", "pid1");

        assertTrue(obj.get("projectId").isJsonArray());
    }

    @Test
    void replaceGson_nullNode_returnsNull() {
        assertNull(VcfaPayloadSanitizer.sanitize((JsonElement) null, "org", "pid"));
    }
}
