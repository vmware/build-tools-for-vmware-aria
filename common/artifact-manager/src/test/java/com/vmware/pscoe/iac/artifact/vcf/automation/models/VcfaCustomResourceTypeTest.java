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
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaCustomResourceType serialization.
 */
class VcfaCustomResourceTypeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"crt-1\"," +
            "\"displayName\":\"Custom Load Balancer\"," +
            "\"description\":\"A custom resource\"," +
            "\"resourceType\":\"Custom.LoadBalancer\"," +
            "\"externalType\":\"vRO.LoadBalancer\"," +
            "\"status\":\"RELEASED\"," +
            "\"orgId\":\"org-1\"," +
            "\"schemaType\":\"ABSTRACT\"," +
            "\"mainActions\":{\"create\":\"action-1\"}," +
            "\"additionalActions\":[{\"id\":\"action-2\"}]," +
            "\"properties\":{\"name\":{\"type\":\"string\"}}" +
            "}";

        VcfaCustomResourceType crt = MAPPER.readValue(json, VcfaCustomResourceType.class);

        assertEquals("crt-1", crt.getId());
        assertEquals("Custom Load Balancer", crt.getDisplayName());
        assertEquals("A custom resource", crt.getDescription());
        assertEquals("Custom.LoadBalancer", crt.getResourceType());
        assertEquals("Custom.LoadBalancer", crt.getName());
        assertEquals("vRO.LoadBalancer", crt.getExternalType());
        assertEquals("RELEASED", crt.getStatus());
        assertEquals("org-1", crt.getOrgId());
        assertEquals("ABSTRACT", crt.getSchemaType());
        assertEquals("action-1", crt.getMainActions().get("create"));
        assertEquals("action-2", crt.getAdditionalActions().get(0).get("id"));
        @SuppressWarnings("unchecked")
        Map<String, Object> nameProperty = (Map<String, Object>) crt.getProperties().get("name");
        assertEquals("string", nameProperty.get("type"));
    }

    @Test
    void testSetName_UpdatesResourceType() {
        VcfaCustomResourceType crt = new VcfaCustomResourceType();
        crt.setName("Custom.DB");

        assertEquals("Custom.DB", crt.getResourceType());
        assertEquals("Custom.DB", crt.getName());
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaCustomResourceType crt = new VcfaCustomResourceType();
        crt.setId("crt-2");
        crt.setDisplayName("Custom DB");
        crt.setResourceType("Custom.DB");
        crt.setExternalType("vRO.DB");
        crt.setStatus("DRAFT");
        crt.setMainActions(Collections.singletonMap("delete", "action-3"));
        crt.setAdditionalActions(Arrays.asList(Collections.singletonMap("id", "action-4")));

        String json = MAPPER.writeValueAsString(crt);
        VcfaCustomResourceType roundTripped = MAPPER.readValue(json, VcfaCustomResourceType.class);

        assertEquals(crt.getId(), roundTripped.getId());
        assertEquals(crt.getName(), roundTripped.getName());
        assertEquals(crt.getStatus(), roundTripped.getStatus());
    }
}
