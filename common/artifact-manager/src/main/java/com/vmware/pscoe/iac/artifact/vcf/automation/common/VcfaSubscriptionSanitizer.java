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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Utility class that sanitises subscription JSON for cross-org / cross-project
 * portability.  Mirrors {@link VcfaPathValidator} in structure: final class,
 * private ctor, pure static methods that mutate in-place and return the node
 * for chaining.
 * <p>
 * During <b>import (push)</b> it removes stale org-scoped values so the target
 * infrastructure derives them from the active profile configuration.
 * <p>
 * During <b>export (pull)</b> it scrubs org-scoped prefixes from local JSON
 * files to avoid carrying remote identifiers back into the workspace.
 */
public final class VcfaSubscriptionSanitizer {

    private static final String ID_PREFIX_REGEX = "^[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}-";
    private static final String NULL_PREFIX = "null-";
    private static final String NULL_DASH = "-null-";

    private VcfaSubscriptionSanitizer() {
        // Utility class — private constructor
    }

    // =========================================================================
    // VcfaSubscriptionStore (Jackson / JsonNode)
    // =========================================================================

    /**
     * Sanitises a subscription node before <b>import (push)</b> to target.
     * Removes org-scoped values that would prevent correct cross-org migration.
     * <ul>
     *   <li>Strips {@code orgId} — the target platform derives it from profile config</li>
     *   <li>Strips legacy {@code null-} prefix from {@code id}</li>
     * </ul>
     *
     * @param node the subscription node (mutated in-place)
     */
    public static void sanitizeImport(ObjectNode node) {
        node.remove("orgId");
        scrubLegacyId(node);
    }

    /**
     * Sanitises a subscription node before <b>export (pull)</b> from source.
     * Removes org-scoped values that would prevent safe re-push to a different org.
     * <ul>
     *   <li>Removes {@code orgId} — prevents pushing to wrong org</li>
     *   <li>Strips legacy {@code -null-} and {@code null-} from {@code id}</li>
     * </ul>
     *
     * @param node the subscription node (mutated in-place)
     */
    public static void sanitizeExport(ObjectNode node) {
        node.remove("orgId");
        scrubLegacyId(node);
    }

    // =========================================================================
    // VraNgSubscriptionStore (Gson / JsonElement)
    // =========================================================================

    /**
     * Sanitises a subscription element before <b>import (push)</b> to target.
     * Removes org-scoped values so the target platform derives them from profile config.
     *
     * @param element the subscription element (mutated in-place)
     */
    public static void sanitizeImport(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        obj.remove("orgId");
        scrubLegacyIdElement(obj);
    }

    /**
     * Sanitises a subscription element before <b>export (pull)</b> from source.
     * Removes org-scoped values to prevent cross-org contamination.
     *
     * @param element the subscription element (mutated in-place)
     */
    public static void sanitizeExport(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        obj.remove("orgId");
        scrubLegacyIdElement(obj);
    }

    // =========================================================================
    // Shared helpers
    // =========================================================================

    private static void scrubLegacyId(ObjectNode node) {
        if (!node.has("id") || node.get("id").isNull()) {
            return;
        }
        String rawId = node.get("id").asText();
        String cleaned = rawId.replace(NULL_DASH, "-");
        cleaned = cleaned.replace(NULL_PREFIX, "");
        node.put("id", cleaned);
    }

    private static void scrubLegacyIdElement(JsonObject obj) {
        if (!obj.has("id") || obj.get("id").isJsonNull()) {
            return;
        }
        String rawId = obj.get("id").getAsString();
        String cleaned = rawId.replace(NULL_DASH, "-");
        cleaned = cleaned.replace(NULL_PREFIX, "");
        obj.addProperty("id", cleaned);
    }
}
