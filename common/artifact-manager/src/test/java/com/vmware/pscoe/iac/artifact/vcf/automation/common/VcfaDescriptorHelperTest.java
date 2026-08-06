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
package com.vmware.pscoe.iac.artifact.vcf.automation.common;

import static org.junit.jupiter.api.Assertions.*;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Unit tests for VcfaDescriptorHelper.
 */
class VcfaDescriptorHelperTest {

    @TempDir
    File tempDir;

    // ==================== getTargetedItems Tests ====================

    @Test
    void testGetTargetedItems_WithNullContentYaml() {
        // No content.yaml exists
        List<String> items = VcfaDescriptorHelper.getTargetedItems(null, "blueprint", "blueprints");

        assertNull(items, "Should return null when content.yaml doesn't exist");
    }

    @Test
    void testGetTargetedItems_WithEmptyList() throws IOException {
        // Create content.yaml with empty list
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint: []\n");
        }

        // Change to temp directory for the test
        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            List<String> items = VcfaDescriptorHelper.getTargetedItems(null, "blueprint");
            assertNotNull(items, "Should return empty list, not null");
            assertTrue(items.isEmpty(), "Should return empty list");
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    void testGetTargetedItems_WithItems() throws IOException {
        // Create content.yaml with items
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint:\n  - Small VM\n  - Medium VM\n");
        }

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            List<String> items = VcfaDescriptorHelper.getTargetedItems(null, "blueprint");
            assertNotNull(items, "Should return list");
            assertEquals(2, items.size(), "Should have 2 items");
            assertTrue(items.contains("Small VM"), "Should contain Small VM");
            assertTrue(items.contains("Medium VM"), "Should contain Medium VM");
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    void testGetTargetedItems_WithMultipleKeys() throws IOException {
        // Create content.yaml with alternate key
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprints:\n  - Small VM\n");
        }

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            List<String> items = VcfaDescriptorHelper.getTargetedItems(null, "blueprint", "blueprints");
            assertNotNull(items, "Should find items under alternate key");
            assertEquals(1, items.size());
            assertEquals("Small VM", items.get(0));
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    // ==================== validateAssetsPresent Tests ====================

    @Test
    void testValidateAssetsPresent_WithNullItems() {
        // Should not throw when items is null
        File assetDir = new File(tempDir, "blueprints");
        assertDoesNotThrow(() ->
            VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Blueprint", true, "blueprint")
        );
    }

    @Test
    void testValidateAssetsPresent_WithEmptyItems() {
        // Should not throw when items is empty
        File assetDir = new File(tempDir, "blueprints");
        assertDoesNotThrow(() ->
            VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Blueprint", true, "blueprint")
        );
    }

    @Test
    void testValidateAssetsPresent_FolderOnly_Missing() throws IOException {
        // Create content.yaml with item but no folder
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint:\n  - MissingBlueprint\n");
        }

        File assetDir = new File(tempDir, "blueprints");
        assetDir.mkdirs();

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            // Should log error but not throw
            assertDoesNotThrow(() ->
                VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Blueprint", true, "blueprint")
            );
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    void testValidateAssetsPresent_FolderOnly_Exists() throws IOException {
        // Create content.yaml with item and matching folder
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("blueprint:\n  - ExistingBlueprint\n");
        }

        File assetDir = new File(tempDir, "blueprints");
        assetDir.mkdirs();
        File blueprintFolder = new File(assetDir, "ExistingBlueprint");
        blueprintFolder.mkdirs();

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            // Should not log error when folder exists
            assertDoesNotThrow(() ->
                VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Blueprint", true, "blueprint")
            );
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    void testValidateAssetsPresent_Flexible_Missing() throws IOException {
        // Test flexible validation (file or folder)
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("subscription:\n  - MissingSub\n");
        }

        File assetDir = new File(tempDir, "subscriptions");
        assetDir.mkdirs();

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            assertDoesNotThrow(() ->
                VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Subscription", false, "subscription")
            );
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    void testValidateAssetsPresent_Flexible_FileExists() throws IOException {
        // Test flexible validation with existing file
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write("subscription:\n  - ExistingSub\n");
        }

        File assetDir = new File(tempDir, "subscriptions");
        assetDir.mkdirs();
        File subFile = new File(assetDir, "ExistingSub.json");
        subFile.createNewFile();

        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.getAbsolutePath());
        try {
            assertDoesNotThrow(() ->
                VcfaDescriptorHelper.validateAssetsPresent(null, assetDir, "Subscription", false, "subscription")
            );
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }
}
