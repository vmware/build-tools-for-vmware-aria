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
 * Unit tests for VcfaSubscription serialization.
 */
class VcfaSubscriptionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testDeserialize_RealisticJson() throws Exception {
        String json = "{" +
            "\"id\":\"sub-1\"," +
            "\"name\":\"Provision VM Subscription\"," +
            "\"type\":\"RUNNABLE\"," +
            "\"eventTopicId\":\"compute.allocation.pre\"," +
            "\"orgId\":\"org-1\"," +
            "\"ownerId\":\"configurationadmin\"," +
            "\"subscriberId\":\"subscriber-1\"," +
            "\"blocking\":true," +
            "\"description\":\"My subscription\"," +
            "\"criteria\":\"event.data.tags === \\\"my-project\\\"\"," +
            "\"constraints\":{\"projectId\":\"proj-1\"}," +
            "\"timeout\":300," +
            "\"broadcast\":false," +
            "\"priority\":1," +
            "\"disabled\":false," +
            "\"system\":false," +
            "\"contextual\":false," +
            "\"runnableType\":\"extensibility.vro\"," +
            "\"runnableId\":\"wf-1\"," +
            "\"config\":{\"key\":\"value\"}" +
            "}";

        VcfaSubscription sub = MAPPER.readValue(json, VcfaSubscription.class);

        assertEquals("sub-1", sub.getId());
        assertEquals("Provision VM Subscription", sub.getName());
        assertEquals("RUNNABLE", sub.getType());
        assertEquals("compute.allocation.pre", sub.getEventTopicId());
        assertEquals("org-1", sub.getOrgId());
        assertEquals("configurationadmin", sub.getOwnerId());
        assertEquals("subscriber-1", sub.getSubscriberId());
        assertEquals(Boolean.TRUE, sub.getBlocking());
        assertEquals("My subscription", sub.getDescription());
        assertTrue(sub.getCriteria().contains("my-project"));
        assertEquals("proj-1", sub.getConstraints().get("projectId"));
        assertEquals(Integer.valueOf(300), sub.getTimeout());
        assertEquals(Boolean.FALSE, sub.getBroadcast());
        assertEquals(Integer.valueOf(1), sub.getPriority());
        assertEquals(Boolean.FALSE, sub.getDisabled());
        assertEquals(Boolean.FALSE, sub.getSystem());
        assertEquals(Boolean.FALSE, sub.getContextual());
        assertEquals("extensibility.vro", sub.getRunnableType());
        assertEquals("wf-1", sub.getRunnableId());
        assertEquals("value", sub.getConfig().get("key"));
    }

    @Test
    void testSerialize_RoundTrip() throws Exception {
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-2");
        sub.setName("Simple Subscription");
        sub.setEventTopicId("com.vmw.vro.workflow");
        sub.setRunnableType("extensibility.abx");
        sub.setRunnableId("abx-1");
        sub.setConstraints(Collections.singletonMap("projectId", "proj-2"));

        String json = MAPPER.writeValueAsString(sub);
        VcfaSubscription roundTripped = MAPPER.readValue(json, VcfaSubscription.class);

        assertEquals(sub.getId(), roundTripped.getId());
        assertEquals(sub.getName(), roundTripped.getName());
        assertEquals(sub.getRunnableId(), roundTripped.getRunnableId());
        assertEquals("proj-2", roundTripped.getConstraints().get("projectId"));
    }
}
