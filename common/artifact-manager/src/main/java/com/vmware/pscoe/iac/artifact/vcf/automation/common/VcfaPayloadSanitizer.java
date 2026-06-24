package com.vmware.pscoe.iac.artifact.vcf.automation.common;

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

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Utility class that sanitizes subscription JSON for cross-org / cross-project
 * portability. Mirrors {@link VcfaPathValidator} in structure: final class,
 * private ctor, pure static methods that mutate in-place and return the node
 * for chaining.
 */
public final class VcfaPayloadSanitizer {
    private static final String NULL_PREFIX = "null-";
    private static final String NULL_DASH = "-null-";
    
    // Define properties to systematically strip during synchronization operations
    private static final List<String> PROPERTIES_TO_REMOVE = Arrays.asList("orgId", "projectId");

    private VcfaPayloadSanitizer() {
        // Utility class — private constructor
    }

    // =========================================================================
    // VcfaSubscriptionStore (Jackson / JsonNode)
    // =========================================================================

    /**
     * Sanitizes a subscription node during lifecycle synchronization operations.
     * Iterates through the predefined environment property list to scrub matches
     * and fixes malformed legacy IDs.
     *
     * @param node the subscription node (mutated in-place)
     * @return the mutated ObjectNode instance for chaining configuration sequences
     */
    public static ObjectNode sanitize(ObjectNode node) {
        if (node == null) {
            return null;
        }

        // Loop through property rules and clear matches out dynamically
        for (String property : PROPERTIES_TO_REMOVE) {
            node.remove(property);
        }

        scrubLegacyId(node);
        return node;
    }

    // =========================================================================
    // VraNgSubscriptionStore (Gson / JsonElement)
    // =========================================================================

    /**
     * Sanitizes a subscription element during lifecycle synchronization operations.
     * Iterates through the predefined environment property list to scrub matches
     * and fixes malformed legacy IDs.
     *
     * @param element the subscription element (mutated in-place)
     * @return the mutated JsonElement instance for chaining configuration sequences
     */
    public static JsonElement sanitize(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return element;
        }

        JsonObject obj = element.getAsJsonObject();
        
        // Loop through property rules and clear matches out dynamically
        for (String property : PROPERTIES_TO_REMOVE) {
            obj.remove(property);
        }

        scrubLegacyIdElement(obj);
        return element;
    }

    // =========================================================================
    // Shared helpers
    // =========================================================================

    private static void scrubLegacyId(ObjectNode node) {
        if (!node.has("id") || node.get("id").isNull()) {
            return;
        }
        String rawId = node.get("id").asText();
        String cleaned = rawId.replace(NULL_DASH, "-").replace(NULL_PREFIX, "");
        node.put("id", cleaned);
    }

    private static void scrubLegacyIdElement(JsonObject obj) {
        if (!obj.has("id") || obj.get("id").isJsonNull()) {
            return;
        }
        String rawId = obj.get("id").getAsString();
        String cleaned = rawId.replace(NULL_DASH, "-").replace(NULL_PREFIX, "");
        obj.addProperty("id", cleaned);
    }
}