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

import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaCatalogItem serialization.
 */
class VcfaCatalogItemTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"item-1\"," +
            "\"name\":\"Add REST host\"," +
            "\"description\":\"Catalog item for adding a REST host\"," +
            "\"type\":{\"id\":\"type-1\"}," +
            "\"sourceId\":\"source-1\"," +
            "\"sourceProjectId\":\"proj-1\"," +
            "\"projects\":[{\"id\":\"proj-1\"}]," +
            "\"iconId\":\"icon-1\"," +
            "\"global\":true," +
            "\"bulkRequestLimit\":10," +
            "\"additionalActions\":[{\"id\":\"action-1\"}]," +
            "\"isRequestable\":true" +
            "}";

        VcfaCatalogItem item = MAPPER.readValue(json, VcfaCatalogItem.class);

        assertEquals("item-1", item.getId());
        assertEquals("Add REST host", item.getName());
        assertEquals("Catalog item for adding a REST host", item.getDescription());
        assertEquals("type-1", item.getType().get("id"));
        assertEquals("source-1", item.getSourceId());
        assertEquals("proj-1", item.getSourceProjectId());
        assertEquals("proj-1", item.getProjects().get(0).get("id"));
        assertEquals("icon-1", item.getIconId());
        assertEquals(Boolean.TRUE, item.getGlobal());
        assertEquals(Integer.valueOf(10), item.getBulkRequestLimit());
        assertEquals("action-1", item.getAdditionalActions().get(0).get("id"));
        assertEquals(Boolean.TRUE, item.getIsRequestable());
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("item-2");
        item.setName("Simple Item");
        item.setSourceId("source-2");
        item.setSourceProjectId("proj-2");
        item.setProjects(Arrays.asList(Collections.singletonMap("id", "proj-2")));
        item.setGlobal(false);
        item.setIsRequestable(true);

        String json = MAPPER.writeValueAsString(item);
        VcfaCatalogItem roundTripped = MAPPER.readValue(json, VcfaCatalogItem.class);

        assertEquals(item.getId(), roundTripped.getId());
        assertEquals(item.getSourceId(), roundTripped.getSourceId());
        assertEquals(item.getIsRequestable(), roundTripped.getIsRequestable());
    }
}
