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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaScenario serialization.
 */
class VcfaScenarioTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"scenarioId\":\"scenario-1\"," +
            "\"scenarioName\":\"VM Provisioned\"," +
            "\"enabled\":true," +
            "\"scenarioCategory\":\"Lifecycle\"," +
            "\"subject\":\"VM provisioned\"," +
            "\"body\":\"A VM was provisioned.\"" +
            "}";

        VcfaScenario scenario = MAPPER.readValue(json, VcfaScenario.class);

        assertEquals("scenario-1", scenario.getId());
        assertEquals("VM Provisioned", scenario.getName());
        assertEquals(Boolean.TRUE, scenario.getEnabled());
        assertEquals("Lifecycle", scenario.getScenarioCategory());
        assertEquals("VM provisioned", scenario.getSubject());
        assertEquals("A VM was provisioned.", scenario.getBody());
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaScenario scenario = new VcfaScenario();
        scenario.setId("scenario-2");
        scenario.setName("VM Deleted");
        scenario.setEnabled(false);
        scenario.setScenarioCategory("Lifecycle");
        scenario.setSubject("VM deleted");
        scenario.setBody("A VM was deleted.");

        String json = MAPPER.writeValueAsString(scenario);
        VcfaScenario roundTripped = MAPPER.readValue(json, VcfaScenario.class);

        assertEquals(scenario.getId(), roundTripped.getId());
        assertEquals(scenario.getName(), roundTripped.getName());
        assertEquals(scenario.getEnabled(), roundTripped.getEnabled());
    }
}
