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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogEntitlement;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaEntitlementStore export, import, and delete behavior.
 */
class VcfaEntitlementStoreTest {

    @TempDir
    File tempDir;

    private VcfaEntitlementStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaEntitlementStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    @Test
    void testImportContent_DoesNothing() {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        assertDoesNotThrow(() -> store.importContent(srcDir));

        verifyNoInteractions(restClient);
    }

    @Test
    void testExportContent_WritesNamedEntitlementFolder() throws IOException {
        CatalogEntitlement item = createEntitlement("ent-1", "MyEntitlement", "A catalog entitlement");

        when(restClient.getCatalogEntitlements()).thenReturn(Collections.singletonList(item));

        store.exportContent();

        File detailsFile = new File(new File(new File(pkg.getFilesystemPath(), "catalog-entitlements"), "MyEntitlement"), "details.json");
        assertTrue(detailsFile.exists());
        String content = new String(Files.readAllBytes(detailsFile.toPath()));
        assertTrue(content.contains("MyEntitlement"));
        assertTrue(content.contains("A catalog entitlement"));
    }

    @Test
    void testExportContent_UsesIdWhenNameMissing() throws IOException {
        CatalogEntitlement item = createEntitlement("ent-2", null, "No name");

        when(restClient.getCatalogEntitlements()).thenReturn(Collections.singletonList(item));

        store.exportContent();

        File detailsFile = new File(new File(new File(pkg.getFilesystemPath(), "catalog-entitlements"), "ent-2"), "details.json");
        assertTrue(detailsFile.exists());
    }

    @Test
    void testExportContent_MultipleItems() throws IOException {
        CatalogEntitlement one = createEntitlement("ent-1", "One", "First");
        CatalogEntitlement two = createEntitlement("ent-2", "Two", "Second");

        when(restClient.getCatalogEntitlements()).thenReturn(java.util.Arrays.asList(one, two));

        store.exportContent();

        assertTrue(new File(new File(new File(pkg.getFilesystemPath(), "catalog-entitlements"), "One"), "details.json").exists());
        assertTrue(new File(new File(new File(pkg.getFilesystemPath(), "catalog-entitlements"), "Two"), "details.json").exists());
    }

    @Test
    void testExportContent_ThrowsWhenClientFails() throws IOException {
        when(restClient.getCatalogEntitlements()).thenThrow(new IOException("network error"));

        RuntimeException ex = assertThrows(RuntimeException.class, store::exportContent);
        assertTrue(ex.getMessage().contains("Unable to fetch catalog entitlements"));
    }

    @Test
    void testExportContent_LogsErrorWhenFolderCannotBeCreated() throws IOException {
        File parentDir = new File(pkg.getFilesystemPath(), "catalog-entitlements");
        parentDir.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(parentDir)) {
            writer.write("blocking file");
        }

        CatalogEntitlement item = createEntitlement("ent-1", "Blocked", "Blocked");
        when(restClient.getCatalogEntitlements()).thenReturn(Collections.singletonList(item));

        assertDoesNotThrow(store::exportContent);

        assertFalse(new File(parentDir, "Blocked").exists());
    }

    @Test
    void testDeleteContent_DoesNothing() {
        assertDoesNotThrow(store::deleteContent);
        verifyNoInteractions(restClient);
    }

    private CatalogEntitlement createEntitlement(String id, String name, String description) {
        CatalogEntitlement item = new CatalogEntitlement();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        Map<String, Object> details = new HashMap<>();
        details.put("key", "value");
        item.setDetails(details);
        return item;
    }
}
