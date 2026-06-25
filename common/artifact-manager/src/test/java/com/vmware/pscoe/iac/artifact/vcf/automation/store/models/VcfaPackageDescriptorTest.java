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
package com.vmware.pscoe.iac.artifact.vcf.automation.store.models;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaPackageDescriptor YAML loading and getters.
 */
class VcfaPackageDescriptorTest {

    @TempDir
    File tempDir;

    @Test
    void testGetInstance_LoadsKebabCaseYaml() throws IOException {
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint:\n  - Small VM\n");
            writer.write("subscription:\n  - Provision VM\n");
            writer.write("flavor-mapping:\n  - small\n");
            writer.write("image-mapping:\n  - ubuntu\n");
            writer.write("storage-profile:\n  - standard\n");
            writer.write("catalog-item:\n  - Source__Item\n");
            writer.write("custom-resource:\n  - CustomVM\n");
            writer.write("resource-action:\n  - Reboot\n");
            writer.write("catalog-entitlement:\n  - Ent1\n");
            writer.write("content-source:\n  - vRO Source\n");
            writer.write("property-group:\n  - Group1\n");
            writer.write("scenario:\n  - Scenario1\n");
            writer.write("policy:\n  approval:\n    - Policy1\n");
        }

        VcfaPackageDescriptor descriptor = VcfaPackageDescriptor.getInstance(contentYaml);

        assertEquals(Arrays.asList("Small VM"), descriptor.getBlueprint());
        assertEquals(Arrays.asList("Provision VM"), descriptor.getSubscription());
        assertEquals(Arrays.asList("small"), descriptor.getFlavorMapping());
        assertEquals(Arrays.asList("ubuntu"), descriptor.getImageMapping());
        assertEquals(Arrays.asList("standard"), descriptor.getStorageProfile());
        assertEquals(Arrays.asList("Source__Item"), descriptor.getCatalogItem());
        assertEquals(Arrays.asList("CustomVM"), descriptor.getCustomResource());
        assertEquals(Arrays.asList("Reboot"), descriptor.getResourceAction());
        assertEquals(Arrays.asList("Ent1"), descriptor.getCatalogEntitlement());
        assertEquals(Arrays.asList("vRO Source"), descriptor.getContentSource());
        assertEquals(Arrays.asList("Group1"), descriptor.getPropertyGroup());
        assertEquals(Arrays.asList("Scenario1"), descriptor.getScenarios());
        assertEquals(Collections.singletonMap("approval", Arrays.asList("Policy1")), descriptor.getPolicy());
    }

    @Test
    void testGetInstance_EmptyLists() throws IOException {
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint: []\n");
            writer.write("subscription: []\n");
            writer.write("property-group: []\n");
        }

        VcfaPackageDescriptor descriptor = VcfaPackageDescriptor.getInstance(contentYaml);

        assertNotNull(descriptor.getBlueprint());
        assertTrue(descriptor.getBlueprint().isEmpty());
        assertNotNull(descriptor.getSubscription());
        assertTrue(descriptor.getSubscription().isEmpty());
        assertNotNull(descriptor.getPropertyGroup());
        assertTrue(descriptor.getPropertyGroup().isEmpty());
        assertNull(descriptor.getCatalogItem());
    }

    @Test
    void testGetInstance_MissingFileThrowsRuntimeException() {
        File missing = new File(tempDir, "missing.yaml");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> VcfaPackageDescriptor.getInstance(missing));
        assertTrue(ex.getMessage().contains("Unable to load VCFA package descriptor"));
    }

    @Test
    void testSettersAndGetters() {
        VcfaPackageDescriptor descriptor = new VcfaPackageDescriptor();

        descriptor.setBlueprint(Arrays.asList("BP1"));
        descriptor.setSubscription(Arrays.asList("Sub1"));
        descriptor.setFlavorMapping(Arrays.asList("small"));
        descriptor.setImageMapping(Arrays.asList("ubuntu"));
        descriptor.setStorageProfile(Arrays.asList("fast"));
        descriptor.setCatalogItem(Arrays.asList("Item1"));
        descriptor.setCustomResource(Arrays.asList("CR1"));
        descriptor.setResourceAction(Arrays.asList("RA1"));
        descriptor.setCatalogEntitlement(Arrays.asList("Ent1"));
        descriptor.setContentSource(Arrays.asList("CS1"));
        descriptor.setPropertyGroup(Arrays.asList("PG1"));
        descriptor.setPolicy(Collections.singletonMap("approval", Arrays.asList("Pol1")));
        descriptor.setScenarios(Arrays.asList("Sc1"));

        assertEquals(Arrays.asList("BP1"), descriptor.getBlueprint());
        assertEquals(Arrays.asList("Sub1"), descriptor.getSubscription());
        assertEquals(Arrays.asList("small"), descriptor.getFlavorMapping());
        assertEquals(Arrays.asList("ubuntu"), descriptor.getImageMapping());
        assertEquals(Arrays.asList("fast"), descriptor.getStorageProfile());
        assertEquals(Arrays.asList("Item1"), descriptor.getCatalogItem());
        assertEquals(Arrays.asList("CR1"), descriptor.getCustomResource());
        assertEquals(Arrays.asList("RA1"), descriptor.getResourceAction());
        assertEquals(Arrays.asList("Ent1"), descriptor.getCatalogEntitlement());
        assertEquals(Arrays.asList("CS1"), descriptor.getContentSource());
        assertEquals(Arrays.asList("PG1"), descriptor.getPropertyGroup());
        assertEquals(Collections.singletonMap("approval", Arrays.asList("Pol1")), descriptor.getPolicy());
        assertEquals(Arrays.asList("Sc1"), descriptor.getScenarios());
    }

    @Test
    void testGetInstance_IgnoresUnknownProperties() throws IOException {
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("unknown-section:\n  - value\n");
            writer.write("blueprint:\n  - Small VM\n");
        }

        VcfaPackageDescriptor descriptor = VcfaPackageDescriptor.getInstance(contentYaml);

        assertEquals(Arrays.asList("Small VM"), descriptor.getBlueprint());
    }
}
