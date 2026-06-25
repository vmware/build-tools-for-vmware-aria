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
package com.vmware.pscoe.iac.artifact.vcf.automation.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CatalogEntitlement serialization.
 */
class CatalogEntitlementTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{\"id\":\"ent-1\",\"name\":\"Entitlement One\",\"description\":\"Description\",\"details\":{\"projectId\":\"proj-1\"}}";

        CatalogEntitlement entitlement = MAPPER.readValue(json, CatalogEntitlement.class);

        assertEquals("ent-1", entitlement.getId());
        assertEquals("Entitlement One", entitlement.getName());
        assertEquals("Description", entitlement.getDescription());
        assertEquals("proj-1", entitlement.getDetails().get("projectId"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        CatalogEntitlement entitlement = new CatalogEntitlement();
        entitlement.setId("ent-2");
        entitlement.setName("Entitlement Two");
        entitlement.setDescription("Another description");
        entitlement.setDetails(Collections.singletonMap("catalogItemId", "item-1"));

        String json = MAPPER.writeValueAsString(entitlement);
        CatalogEntitlement roundTripped = MAPPER.readValue(json, CatalogEntitlement.class);

        assertEquals(entitlement.getId(), roundTripped.getId());
        assertEquals(entitlement.getName(), roundTripped.getName());
        assertEquals(entitlement.getDetails().get("catalogItemId"), roundTripped.getDetails().get("catalogItemId"));
    }
}
