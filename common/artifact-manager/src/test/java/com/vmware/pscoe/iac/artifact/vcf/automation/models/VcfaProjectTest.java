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
 * Unit tests for VcfaProject serialization.
 */
class VcfaProjectTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testConstructor_AndGetters() {
        VcfaProject project = new VcfaProject("proj-1", "Project One");
        project.setOrgId("org-1");

        assertEquals("proj-1", project.getId());
        assertEquals("Project One", project.getName());
        assertEquals("org-1", project.getOrgId());
    }

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{\"id\":\"proj-2\",\"orgId\":\"org-2\",\"name\":\"Project Two\"}";

        VcfaProject project = MAPPER.readValue(json, VcfaProject.class);

        assertEquals("proj-2", project.getId());
        assertEquals("org-2", project.getOrgId());
        assertEquals("Project Two", project.getName());
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaProject project = new VcfaProject("proj-3", "Project Three");
        project.setOrgId("org-3");

        String json = MAPPER.writeValueAsString(project);
        VcfaProject roundTripped = MAPPER.readValue(json, VcfaProject.class);

        assertEquals(project.getId(), roundTripped.getId());
        assertEquals(project.getName(), roundTripped.getName());
        assertEquals(project.getOrgId(), roundTripped.getOrgId());
    }
}
