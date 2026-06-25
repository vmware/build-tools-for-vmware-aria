/*-
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaBlueprintStore import, export, and delete behavior.
 */
class VcfaBlueprintStoreTest {

    @TempDir
    File tempDir;

    private VcfaBlueprintStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaBlueprintStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewBlueprint() throws IOException {
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "formatVersion: 1");

        when(restClient.getBlueprints()).thenReturn(Collections.emptyList());
        when(restClient.getProjectId()).thenReturn("proj-1");
        VcfaBlueprint created = new VcfaBlueprint();
        created.setId("bp-1");
        created.setName("TestBlueprint");
        when(restClient.createBlueprint(any(VcfaBlueprint.class))).thenReturn(created);
        when(restClient.versionBlueprint(anyString(), anyMap())).thenReturn(Collections.emptyMap());

        store.importContent(srcDir);

        verify(restClient).createBlueprint(any(VcfaBlueprint.class));
        verify(restClient).versionBlueprint(eq("bp-1"), anyMap());
    }

    @Test
    void testImportContent_UpdatesBlueprintWhenContentChanged() throws IOException {
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "local content");

        VcfaBlueprint serverSummary = new VcfaBlueprint();
        serverSummary.setId("bp-existing");
        serverSummary.setName("TestBlueprint");

        VcfaBlueprint serverFull = new VcfaBlueprint();
        serverFull.setId("bp-existing");
        serverFull.setName("TestBlueprint");
        serverFull.setContent("server content");

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(serverSummary));
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(restClient.getBlueprintById("bp-existing")).thenReturn(serverFull);
        when(restClient.updateBlueprint(eq("bp-existing"), any(VcfaBlueprint.class))).thenReturn(serverFull);
        when(restClient.versionBlueprint(anyString(), anyMap())).thenReturn(Collections.emptyMap());

        store.importContent(srcDir);

        verify(restClient).updateBlueprint(eq("bp-existing"), any(VcfaBlueprint.class));
        verify(restClient).versionBlueprint(eq("bp-existing"), anyMap());
    }

    @Test
    void testImportContent_SkipsWhenContentMatches() throws IOException {
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "same content");

        VcfaBlueprint serverSummary = new VcfaBlueprint();
        serverSummary.setId("bp-existing");
        serverSummary.setName("TestBlueprint");

        VcfaBlueprint serverFull = new VcfaBlueprint();
        serverFull.setId("bp-existing");
        serverFull.setName("TestBlueprint");
        serverFull.setContent("same content");

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(serverSummary));
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(restClient.getBlueprintById("bp-existing")).thenReturn(serverFull);

        store.importContent(srcDir);

        verify(restClient, never()).createBlueprint(any());
        verify(restClient, never()).updateBlueprint(anyString(), any());
        verify(restClient, never()).versionBlueprint(anyString(), anyMap());
    }

    @Test
    void testImportContent_ProcessesCustomForm() throws IOException {
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "local content");

        File bpDir = new File(new File(srcDir, "blueprints"), "TestBlueprint");
        File formFile = new File(bpDir, "TestBlueprint__FormData.json");
        try (FileWriter writer = new FileWriter(formFile)) {
            writer.write("{\"form\":{\"layout\":\"vertical\"},\"styles\":\".body{}\"}");
        }

        VcfaBlueprint serverSummary = new VcfaBlueprint();
        serverSummary.setId("bp-existing");
        serverSummary.setName("TestBlueprint");

        VcfaBlueprint serverFull = new VcfaBlueprint();
        serverFull.setId("bp-existing");
        serverFull.setName("TestBlueprint");
        serverFull.setContent("local content");

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(serverSummary));
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(restClient.getBlueprintById("bp-existing")).thenReturn(serverFull);
        when(restClient.getCatalogItemForm(anyString(), anyString())).thenReturn(null);
        when(restClient.updateBlueprint(eq("bp-existing"), any(VcfaBlueprint.class))).thenReturn(serverFull);
        when(restClient.versionBlueprint(anyString(), anyMap())).thenReturn(Collections.emptyMap());

        store.importContent(srcDir);

        verify(restClient).createBlueprintForm(anyString(), anyMap(), anyString(), anyString(), anyString(), any(), anyString());
        verify(restClient).versionBlueprint(eq("bp-existing"), anyMap());
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("blueprint:\n  - OtherBlueprint\n");
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "local content");

        when(restClient.getBlueprints()).thenReturn(Collections.emptyList());
        when(restClient.getProjectId()).thenReturn("proj-1");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient, never()).createBlueprint(any());
        verify(restClient, never()).updateBlueprint(anyString(), any());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("blueprint: []\n");
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "local content");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient).getBlueprints();
        verify(restClient, never()).createBlueprint(any());
        verify(restClient, never()).updateBlueprint(anyString(), any());
    }

    @Test
    void testImportContent_MissingBlueprintsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getBlueprints();
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesBlueprintToDisk() throws IOException {
        VcfaBlueprint serverBp = new VcfaBlueprint();
        serverBp.setId("bp-1");
        serverBp.setName("TestBlueprint");
        serverBp.setDescription("A blueprint");
        serverBp.setContent("formatVersion: 1");
        serverBp.setRequestScopeOrg(true);

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(serverBp));
        when(restClient.getBlueprintById("bp-1")).thenReturn(serverBp);
        when(restClient.getCatalogItemForm(anyString(), anyString())).thenReturn(null);

        store.exportContent();

        File bpFolder = new File(new File(pkg.getFilesystemPath(), "blueprints"), "TestBlueprint");
        assertTrue(bpFolder.exists());
        File detailsFile = new File(bpFolder, "details.json");
        assertTrue(detailsFile.exists());
        String detailsJson = new String(Files.readAllBytes(detailsFile.toPath()));
        assertTrue(detailsJson.contains("bp-1"));
        assertTrue(detailsJson.contains("TestBlueprint"));
        assertTrue(detailsJson.contains("requestScopeOrg"));

        File contentFile = new File(bpFolder, "content.yaml");
        assertTrue(contentFile.exists());
        assertEquals("formatVersion: 1", new String(Files.readAllBytes(contentFile.toPath())).trim());
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("blueprint:\n  - OtherBlueprint\n");
        VcfaBlueprint serverBp = new VcfaBlueprint();
        serverBp.setId("bp-1");
        serverBp.setName("TestBlueprint");

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(serverBp));

        setUserDir(tempDir, () -> store.exportContent());

        verify(restClient, never()).getBlueprintById(anyString());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaBlueprint bpOne = new VcfaBlueprint();
        bpOne.setId("bp-1");
        bpOne.setName("One");
        VcfaBlueprint bpTwo = new VcfaBlueprint();
        bpTwo.setId("bp-2");
        bpTwo.setName("Two");

        when(restClient.getBlueprints()).thenReturn(Arrays.asList(bpOne, bpTwo));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteBlueprint("bp-1");
        verify(restClient).deleteBlueprint("bp-2");
    }

    @Test
    void testDeleteContent_EmptyListSkips() throws IOException {
        writeContentYaml("blueprint: []\n");
        VcfaBlueprint bp = new VcfaBlueprint();
        bp.setId("bp-1");
        bp.setName("One");

        when(restClient.getBlueprints()).thenReturn(Collections.singletonList(bp));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteBlueprint(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("blueprint:\n  - Target\n");
        VcfaBlueprint target = new VcfaBlueprint();
        target.setId("bp-target");
        target.setName("Target");
        VcfaBlueprint other = new VcfaBlueprint();
        other.setId("bp-other");
        other.setName("Other");

        when(restClient.getBlueprints()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteBlueprint("bp-target");
        verify(restClient, never()).deleteBlueprint("bp-other");
    }

    // ==================== Version Unrelease Tests ====================

    @Test
    void testImportContent_UnreleasesOldVersionsWhenEnabled() throws IOException {
        File srcDir = createBlueprintSource("TestBlueprint",
                "{\"id\":\"bp-1\",\"name\":\"TestBlueprint\",\"description\":\"A blueprint\"}",
                "formatVersion: 1");

        when(restClient.getBlueprints()).thenReturn(Collections.emptyList());
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(config.getUnreleaseBlueprintVersions()).thenReturn(true);

        VcfaBlueprint created = new VcfaBlueprint();
        created.setId("bp-1");
        created.setName("TestBlueprint");
        when(restClient.createBlueprint(any(VcfaBlueprint.class))).thenReturn(created);
        when(restClient.versionBlueprint(anyString(), anyMap())).thenReturn(Collections.emptyMap());

        String versionsJson = "{\"content\":[{\"id\":\"v1\",\"version\":\"1\",\"createdAt\":\"2026-01-01T00:00:00Z\",\"released\":true},{\"id\":\"v2\",\"version\":\"2\",\"createdAt\":\"2026-01-02T00:00:00Z\",\"released\":true}]}";
        when(restClient.getBlueprintVersions("bp-1")).thenReturn(versionsJson);

        store.importContent(srcDir);

        verify(restClient).unreleaseBlueprintVersion("bp-1", "v1");
        verify(restClient, never()).unreleaseBlueprintVersion("bp-1", "v2");
    }

    // ==================== Helpers ====================

    private File createBlueprintSource(String name, String detailsJson, String yamlContent) throws IOException {
        File srcDir = new File(tempDir, "src");
        File blueprintsDir = new File(srcDir, "blueprints");
        File bpDir = new File(blueprintsDir, name);
        bpDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(bpDir, "details.json"))) {
            writer.write(detailsJson);
        }
        if (yamlContent != null) {
            try (FileWriter writer = new FileWriter(new File(bpDir, "content.yaml"))) {
                writer.write(yamlContent);
            }
        }
        return srcDir;
    }

    private void writeContentYaml(String content) throws IOException {
        File contentYaml = new File(tempDir, "content.yaml");
        try (FileWriter writer = new FileWriter(contentYaml)) {
            writer.write(content);
        }
    }

    private void setUserDir(File dir, ThrowingRunnable action) throws IOException {
        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", dir.getAbsolutePath());
        try {
            action.run();
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws IOException;
    }
}
