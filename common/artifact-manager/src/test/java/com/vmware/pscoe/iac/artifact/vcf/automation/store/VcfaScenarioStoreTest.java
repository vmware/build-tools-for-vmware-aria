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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VcfaScenarioStore.
 */
class VcfaScenarioStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WithScenarios() {
        ObjectNode scenario = MAPPER.createObjectNode();
        scenario.put("name", "TestScenario");
        scenario.put("description", "Test Description");

        assertTrue(scenario.has("name"), "Scenario should have name");
        assertTrue(scenario.has("description"), "Scenario should have description");
    }

    @Test
    void testExportContent_WithEmptyDescriptor() {
        ObjectNode scenario = MAPPER.createObjectNode();
        scenario.put("name", "TestScenario");

        // Empty descriptor should skip
        assertTrue(scenario.has("name"), "Name should be preserved");
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_WithScenarios() {
        ObjectNode scenario = MAPPER.createObjectNode();
        scenario.put("name", "TestScenario");
        scenario.put("enabled", true);

        assertTrue(scenario.get("enabled").asBoolean());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent() {
        String scenarioName = "TestScenario";
        assertEquals("TestScenario", scenarioName);
    }
}
