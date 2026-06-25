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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaScenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaScenarioStore import, export, and delete behavior.
 */
class VcfaScenarioStoreTest {

    @TempDir
    File tempDir;

    private VcfaScenarioStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaScenarioStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesScenarioFolderWithDetailsAndTemplate() throws IOException {
        VcfaScenario scenario = createScenario("sc-1", "WelcomeScenario", true, "greeting", "<h1>Hello</h1>");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(scenario));

        store.exportContent();

        File scenarioFolder = new File(new File(pkg.getFilesystemPath(), "scenarios"), "WelcomeScenario");
        File detailsFile = new File(scenarioFolder, "details.json");
        File templateFile = new File(scenarioFolder, "template.html");
        assertTrue(detailsFile.exists());
        assertTrue(templateFile.exists());
        String detailsContent = new String(Files.readAllBytes(detailsFile.toPath()));
        String templateContent = new String(Files.readAllBytes(templateFile.toPath()));
        assertTrue(detailsContent.contains("WelcomeScenario"));
        assertTrue(detailsContent.contains("scenarioName"));
        assertTrue(detailsContent.contains("scenarioId"));
        assertFalse(detailsContent.contains("<h1>Hello</h1>"));
        assertEquals("<h1>Hello</h1>", templateContent);
    }

    @Test
    void testExportContent_AddsScenarioNameAndIdWhenMissing() throws IOException {
        VcfaScenario scenario = new VcfaScenario();
        scenario.setId("sc-2");
        scenario.setName("LegacyScenario");
        scenario.setEnabled(false);
        scenario.setScenarioCategory("alert");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(scenario));

        store.exportContent();

        File detailsFile = new File(new File(new File(pkg.getFilesystemPath(), "scenarios"), "LegacyScenario"), "details.json");
        String content = new String(Files.readAllBytes(detailsFile.toPath()));
        assertTrue(content.contains("scenarioName"));
        assertTrue(content.contains("LegacyScenario"));
        assertTrue(content.contains("scenarioId"));
        assertTrue(content.contains("sc-2"));
    }

    @Test
    void testExportContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("scenario: []\n");

        store.exportContent();

        verify(restClient, never()).getScenarios();
        File scenariosDir = new File(pkg.getFilesystemPath(), "scenarios");
        assertFalse(scenariosDir.exists());
    }

    @Test
    void testExportContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.exportContent();

        File scenariosDir = new File(pkg.getFilesystemPath(), "scenarios");
        assertFalse(scenariosDir.exists());
    }

    @Test
    void testExportContent_RestClientNullSkips() {
        VcfaScenarioStore bareStore = new VcfaScenarioStore();
        bareStore.init(null, pkg, config, descriptor);
        assertDoesNotThrow(bareStore::exportContent);
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("scenario:\n  - OtherScenario\n");
        VcfaScenario scenario = createScenario("sc-1", "ExcludedScenario", true, "greeting", "body");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(scenario));

        setUserDir(tempDir, () -> store.exportContent());

        File excludedFolder = new File(new File(pkg.getFilesystemPath(), "scenarios"), "ExcludedScenario");
        assertFalse(excludedFolder.exists());
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewScenario() throws IOException {
        File srcDir = createScenarioSource("NewScenario",
                "{\"scenarioId\":\"sc-1\",\"scenarioName\":\"NewScenario\",\"enabled\":true,\"scenarioCategory\":\"greeting\"}",
                "<h1>New</h1>");

        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createScenario(argThat(json -> json.contains("NewScenario") && json.contains("<h1>New</h1>")));
    }

    @Test
    void testImportContent_RecreatesScenarioWhenChanged() throws IOException {
        File srcDir = createScenarioSource("ChangedScenario",
                "{\"scenarioId\":\"sc-1\",\"scenarioName\":\"ChangedScenario\",\"enabled\":true,\"scenarioCategory\":\"greeting\"}",
                "<h1>local</h1>");

        VcfaScenario remote = createScenario("sc-remote", "ChangedScenario", true, "greeting", "<h1>remote</h1>");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient).deleteScenario("sc-remote");
        verify(restClient).createScenario(argThat(json -> json.contains("<h1>local</h1>")));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        File srcDir = createScenarioSource("SameScenario",
                "{\"scenarioId\":\"sc-1\",\"scenarioName\":\"SameScenario\",\"enabled\":true,\"scenarioCategory\":\"greeting\"}",
                "<h1>same</h1>");

        VcfaScenario remote = createScenario("sc-remote", "SameScenario", true, "greeting", "<h1>same</h1>");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient, never()).deleteScenario(anyString());
        verify(restClient, never()).createScenario(anyString());
    }

    @Test
    void testImportContent_MatchesById() throws IOException {
        File srcDir = createScenarioSource("IdMatchScenario",
                "{\"scenarioId\":\"sc-local\",\"scenarioName\":\"IdMatchScenario\",\"enabled\":true,\"scenarioCategory\":\"greeting\"}",
                "");

        VcfaScenario remote = createScenario("sc-local", "IdMatchScenario", false, "greeting", "");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient).deleteScenario("sc-local");
        verify(restClient).createScenario(anyString());
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("scenario:\n  - OtherScenario\n");
        File srcDir = createScenarioSource("ExcludedScenario",
                "{\"scenarioId\":\"sc-1\",\"scenarioName\":\"ExcludedScenario\",\"enabled\":true}",
                "");

        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient).getScenarios();
        verify(restClient, never()).createScenario(anyString());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("scenario: []\n");
        File srcDir = createScenarioSource("SkippedScenario",
                "{\"scenarioId\":\"sc-1\",\"scenarioName\":\"SkippedScenario\",\"enabled\":true}",
                "");

        store.importContent(srcDir);

        verify(restClient, never()).getScenarios();
    }

    @Test
    void testImportContent_MissingScenariosFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getScenarios();
    }

    @Test
    void testImportContent_EmptyScenariosFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        new File(srcDir, "scenarios").mkdirs();

        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getScenarios();
        verify(restClient, never()).createScenario(anyString());
    }

    @Test
    void testImportContent_MissingDetailsJsonSkipsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        File scenariosDir = new File(srcDir, "scenarios");
        File scenarioDir = new File(scenariosDir, "NoDetails");
        scenarioDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(scenarioDir, "template.html"))) {
            writer.write("<h1>No details</h1>");
        }

        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient, never()).createScenario(anyString());
    }

    @Test
    void testImportContent_MissingScenarioNameSkipsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        File scenariosDir = new File(srcDir, "scenarios");
        File scenarioDir = new File(scenariosDir, "NoName");
        scenarioDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(scenarioDir, "details.json"))) {
            writer.write("{\"scenarioId\":\"sc-1\",\"enabled\":true}");
        }

        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient, never()).createScenario(anyString());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaScenario one = createScenario("sc-1", "One", true, "greeting", "<h1>One</h1>");
        VcfaScenario two = createScenario("sc-2", "Two", true, "greeting", "<h1>Two</h1>");

        when(restClient.getScenarios()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deleteScenario("sc-1");
        verify(restClient).deleteScenario("sc-2");
    }

    @Test
    void testDeleteContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("scenario: []\n");
        VcfaScenario scenario = createScenario("sc-1", "Only", true, "greeting", "<h1>Only</h1>");

        when(restClient.getScenarios()).thenReturn(Collections.singletonList(scenario));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteScenario(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("scenario:\n  - Target\n");
        VcfaScenario target = createScenario("sc-target", "Target", true, "greeting", "");
        VcfaScenario other = createScenario("sc-other", "Other", true, "greeting", "");

        when(restClient.getScenarios()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteScenario("sc-target");
        verify(restClient, never()).deleteScenario("sc-other");
    }

    @Test
    void testDeleteContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(restClient.getScenarios()).thenReturn(Collections.emptyList());

        store.deleteContent();

        verify(restClient, never()).deleteScenario(anyString());
    }

    // ==================== Helpers ====================

    private VcfaScenario createScenario(String id, String name, boolean enabled, String category, String body) {
        VcfaScenario scenario = new VcfaScenario();
        scenario.setId(id);
        scenario.setName(name);
        scenario.setEnabled(enabled);
        scenario.setScenarioCategory(category);
        scenario.setBody(body);
        return scenario;
    }

    private File createScenarioSource(String name, String detailsJson, String htmlBody) throws IOException {
        File srcDir = new File(tempDir, "src");
        File scenariosDir = new File(srcDir, "scenarios");
        File scenarioDir = new File(scenariosDir, name);
        scenarioDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(scenarioDir, "details.json"))) {
            writer.write(detailsJson);
        }
        try (FileWriter writer = new FileWriter(new File(scenarioDir, "template.html"))) {
            writer.write(htmlBody);
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
