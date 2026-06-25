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
 * Unit tests for VcfaGenericItem serialization.
 */
class VcfaGenericItemTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{\"id\":\"item-1\",\"name\":\"Generic Item\",\"description\":\"Generic description\",\"properties\":{\"key\":\"value\"}}";

        VcfaGenericItem item = MAPPER.readValue(json, VcfaGenericItem.class);

        assertEquals("item-1", item.getId());
        assertEquals("Generic Item", item.getName());
        assertEquals("Generic description", item.getDescription());
        assertEquals("value", item.getProperties().get("key"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaGenericItem item = new VcfaGenericItem();
        item.setId("item-2");
        item.setName("Another Generic Item");
        item.setDescription("Another description");
        item.setProperties(Collections.singletonMap("foo", "bar"));

        String json = MAPPER.writeValueAsString(item);
        VcfaGenericItem roundTripped = MAPPER.readValue(json, VcfaGenericItem.class);

        assertEquals(item.getId(), roundTripped.getId());
        assertEquals(item.getName(), roundTripped.getName());
        assertEquals(item.getProperties().get("foo"), roundTripped.getProperties().get("foo"));
    }
}
