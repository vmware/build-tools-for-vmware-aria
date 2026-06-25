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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaCatalogItemForm custom form serialization.
 */
class VcfaCatalogItemFormTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_FormAsString() throws Exception {
        String json = "{" +
            "\"id\":\"form-1\"," +
            "\"name\":\"Request Form\"," +
            "\"form\":\"{\\\"layout\\\":\\\"vertical\\\"}\"," +
            "\"styles\":\".body {}\"," +
            "\"sourceType\":\"com.vmw.vro.workflow\"," +
            "\"sourceId\":\"source-1\"," +
            "\"type\":\"REQUEST\"," +
            "\"status\":\"RELEASED\"," +
            "\"formFormat\":\"JSON\"" +
            "}";

        VcfaCatalogItemForm form = MAPPER.readValue(json, VcfaCatalogItemForm.class);

        assertEquals("form-1", form.getId());
        assertEquals("Request Form", form.getName());
        assertNotNull(form.getFormAsJson());
        assertEquals("vertical", form.getFormAsJson().get("layout").asText());
        assertEquals(".body {}", form.getStyles());
        assertEquals("com.vmw.vro.workflow", form.getSourceType());
        assertEquals("source-1", form.getSourceId());
        assertEquals("REQUEST", form.getType());
        assertEquals("RELEASED", form.getStatus());
        assertEquals("JSON", form.getFormFormat());
    }

    @Test
    void testDeserialize_FormAsObject() throws Exception {
        String json = "{" +
            "\"id\":\"form-2\"," +
            "\"name\":\"Second Form\"," +
            "\"form\":{\"schema\":{\"type\":\"object\"}}," +
            "\"styles\":\"\"" +
            "}";

        VcfaCatalogItemForm form = MAPPER.readValue(json, VcfaCatalogItemForm.class);

        JsonNode formAsJson = form.getFormAsJson();
        assertNotNull(formAsJson);
        assertTrue(formAsJson.has("schema"));
        assertEquals("object", formAsJson.get("schema").get("type").asText());
    }

    @Test
    void testSerialize_FormBecomesObjectTree() throws Exception {
        VcfaCatalogItemForm form = new VcfaCatalogItemForm();
        form.setId("form-3");
        form.setName("Third Form");
        form.setFormFromJson(MAPPER.readTree("{\"layout\":\"horizontal\"}"));
        form.setStyles(".foo {}");

        String json = MAPPER.writeValueAsString(form);

        assertTrue(json.contains("\"form\":"));
        assertTrue(json.contains("\"layout\":\"horizontal\""));
        assertFalse(json.contains("\"form\":\""));
    }

    @Test
    void testRoundTrip() throws Exception {
        VcfaCatalogItemForm form = new VcfaCatalogItemForm();
        form.setId("form-4");
        form.setName("Fourth Form");
        form.setFormFromJson(MAPPER.readTree("{\"title\":\"Demo\"}"));

        String json = MAPPER.writeValueAsString(form);
        VcfaCatalogItemForm roundTripped = MAPPER.readValue(json, VcfaCatalogItemForm.class);

        assertEquals(form.getId(), roundTripped.getId());
        assertNotNull(roundTripped.getFormAsJson());
        assertEquals("Demo", roundTripped.getFormAsJson().get("title").asText());
    }
}
