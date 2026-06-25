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
 * Unit tests for VcfaPolicy serialization.
 */
class VcfaPolicyTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"policy-1\"," +
            "\"name\":\"Approval Policy\"," +
            "\"typeId\":\"com.vmware.policy.approval\"," +
            "\"enforcementType\":\"HARD\"," +
            "\"orgId\":\"org-1\"," +
            "\"projectId\":\"proj-1\"," +
            "\"definition\":{\"level\":\"project\"}" +
            "}";

        VcfaPolicy policy = MAPPER.readValue(json, VcfaPolicy.class);

        assertEquals("policy-1", policy.getId());
        assertEquals("Approval Policy", policy.getName());
        assertEquals("com.vmware.policy.approval", policy.getTypeId());
        assertEquals("HARD", policy.getEnforcementType());
        assertEquals("org-1", policy.getOrgId());
        assertEquals("proj-1", policy.getProjectId());
        assertEquals("project", policy.getDefinition().get("level"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaPolicy policy = new VcfaPolicy();
        policy.setId("policy-2");
        policy.setName("Lease Policy");
        policy.setTypeId("com.vmware.policy.lease");
        policy.setEnforcementType("SOFT");
        policy.setProjectId("proj-2");
        policy.setDefinition(Collections.singletonMap("maxDays", 30));

        String json = MAPPER.writeValueAsString(policy);
        VcfaPolicy roundTripped = MAPPER.readValue(json, VcfaPolicy.class);

        assertEquals(policy.getId(), roundTripped.getId());
        assertEquals(policy.getName(), roundTripped.getName());
        assertEquals(policy.getEnforcementType(), roundTripped.getEnforcementType());
        assertEquals(Integer.valueOf(30), roundTripped.getDefinition().get("maxDays"));
    }
}
