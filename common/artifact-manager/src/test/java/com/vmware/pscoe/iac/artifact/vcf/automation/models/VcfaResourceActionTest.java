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
 * Unit tests for VcfaResourceAction serialization.
 */
class VcfaResourceActionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"ra-1\"," +
            "\"name\":\"Reboot\"," +
            "\"displayName\":\"Reboot VM\"," +
            "\"description\":\"Reboots a VM\"," +
            "\"providerName\":\"VM Provider\"," +
            "\"resourceType\":\"Custom.VM\"," +
            "\"status\":\"RELEASED\"," +
            "\"orgId\":\"org-1\"," +
            "\"runnableItem\":{\"id\":\"wf-1\",\"name\":\"Reboot Workflow\"}," +
            "\"formDefinition\":{\"id\":\"form-1\"}," +
            "\"criteria\":{\"type\":\"string\"}" +
            "}";

        VcfaResourceAction action = MAPPER.readValue(json, VcfaResourceAction.class);

        assertEquals("ra-1", action.getId());
        assertEquals("Reboot", action.getName());
        assertEquals("Reboot VM", action.getDisplayName());
        assertEquals("Reboots a VM", action.getDescription());
        assertEquals("VM Provider", action.getProviderName());
        assertEquals("Custom.VM", action.getResourceType());
        assertEquals("RELEASED", action.getStatus());
        assertEquals("org-1", action.getOrgId());
        assertEquals("wf-1", action.getRunnableItem().get("id"));
        assertEquals("form-1", action.getFormDefinition().get("id"));
        assertEquals("string", action.getCriteria().get("type"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaResourceAction action = new VcfaResourceAction();
        action.setId("ra-2");
        action.setName("Shutdown");
        action.setDisplayName("Shutdown VM");
        action.setResourceType("Custom.VM");
        action.setRunnableItem(Collections.singletonMap("id", "wf-2"));
        action.setFormDefinition(Collections.singletonMap("layout", "simple"));

        String json = MAPPER.writeValueAsString(action);
        VcfaResourceAction roundTripped = MAPPER.readValue(json, VcfaResourceAction.class);

        assertEquals(action.getId(), roundTripped.getId());
        assertEquals(action.getDisplayName(), roundTripped.getDisplayName());
        assertEquals(action.getRunnableItem().get("id"), roundTripped.getRunnableItem().get("id"));
    }
}
