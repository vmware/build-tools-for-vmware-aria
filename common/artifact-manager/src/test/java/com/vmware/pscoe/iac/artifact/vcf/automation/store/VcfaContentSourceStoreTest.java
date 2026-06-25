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
import java.util.HashMap;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaContentSourceStore import, export, and delete behavior.
 */
class VcfaContentSourceStoreTest {

    @TempDir
    File tempDir;

    private VcfaContentSourceStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaContentSourceStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewContentSourceFromFlatFile() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = createContentSourceSource("NewSource", "NewSource.json",
                "{\"id\":\"cs-new\",\"name\":\"NewSource\",\"typeId\":\"com.vmw.vro.workflow\",\"description\":\"A source\"}");

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createContentSource(argThat(source -> "NewSource".equals(source.getName())
                && "com.vmw.vro.workflow".equals(source.getTypeId())));
    }

    @Test
    void testImportContent_CreatesNewContentSourceFromNestedFolder() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = new File(tempDir, "src");
        File contentSourcesDir = new File(srcDir, "content-sources");
        File nestedDir = new File(contentSourcesDir, "NestedSource");
        nestedDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(nestedDir, "details.json"))) {
            writer.write("{\"id\":\"cs-nested\",\"name\":\"NestedSource\",\"typeId\":\"com.vmw.vro.workflow\"}");
        }

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createContentSource(argThat(source -> "NestedSource".equals(source.getName())));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = createContentSourceSource("SameSource", "SameSource.json",
                "{\"id\":\"cs-same\",\"name\":\"SameSource\",\"typeId\":\"com.vmw.vro.workflow\",\"description\":\"A source\",\"global\":true}");

        VcfaContentSource existing = new VcfaContentSource();
        existing.setId("cs-existing");
        existing.setName("SameSource");
        existing.setTypeId("com.vmw.vro.workflow");
        existing.setDescription("A source");
        existing.setGlobal(true);

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(existing));

        store.importContent(srcDir);

        verify(restClient, never()).createContentSource(any());
        verify(restClient, never()).updateContentSource(anyString(), any());
    }

    @Test
    void testImportContent_UpdatesWhenChanged() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = createContentSourceSource("ChangedSource", "ChangedSource.json",
                "{\"id\":\"cs-changed\",\"name\":\"ChangedSource\",\"typeId\":\"com.vmw.vro.workflow\",\"description\":\"local\"}");

        VcfaContentSource existing = new VcfaContentSource();
        existing.setId("cs-existing");
        existing.setName("ChangedSource");
        existing.setTypeId("com.vmw.vro.workflow");
        existing.setDescription("remote");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(existing));

        store.importContent(srcDir);

        verify(restClient).updateContentSource(eq("cs-existing"), argThat(source -> "local".equals(source.getDescription())));
    }

    @Test
    void testImportContent_ResolvesProjectName() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = createContentSourceSource("ProjectSource", "ProjectSource.json",
                "{\"id\":\"cs-proj\",\"name\":\"ProjectSource\",\"typeId\":\"com.vmw.vro.workflow\",\"projectName\":\"My Project\"}");

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());
        when(restClient.getProjectId("My Project")).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).getProjectId("My Project");
        verify(restClient).createContentSource(argThat(source -> "proj-1".equals(source.getProjectId())));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getContentSource()).thenReturn(Collections.singletonList("OtherSource"));
        File srcDir = createContentSourceSource("ExcludedSource", "ExcludedSource.json",
                "{\"id\":\"cs-excluded\",\"name\":\"ExcludedSource\",\"typeId\":\"com.vmw.vro.workflow\"}");

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getContentSources();
        verify(restClient, never()).createContentSource(any());
        verify(restClient, never()).updateContentSource(anyString(), any());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        when(descriptor.getContentSource()).thenReturn(Collections.emptyList());
        File srcDir = createContentSourceSource("SkippedSource", "SkippedSource.json",
                "{\"id\":\"cs-skip\",\"name\":\"SkippedSource\",\"typeId\":\"com.vmw.vro.workflow\"}");

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getContentSources();
        verify(restClient, never()).createContentSource(any());
        verify(restClient, never()).updateContentSource(anyString(), any());
    }

    @Test
    void testImportContent_MissingContentSourcesFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getContentSources();
    }

    @Test
    void testImportContent_EmptyContentSourcesFolder() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = new File(tempDir, "src");
        new File(srcDir, "content-sources").mkdirs();

        store.importContent(srcDir);

        verify(restClient).getContentSources();
        verify(restClient, never()).createContentSource(any());
    }

    @Test
    void testImportContent_SkipsNonJsonFiles() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File srcDir = new File(tempDir, "src");
        File contentSourcesDir = new File(srcDir, "content-sources");
        contentSourcesDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(contentSourcesDir, "Readme.txt"))) {
            writer.write("not json");
        }

        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient, never()).createContentSource(any());
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesFlatFileToDisk() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-1");
        source.setName("FlatSource");
        source.setTypeId("com.vmw.vro.workflow");
        source.setDescription("A source");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "content-sources"), "FlatSource.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("FlatSource"));
    }

    @Test
    void testExportContent_WritesToExistingNestedFolder() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File nestedDir = new File(new File(pkg.getFilesystemPath(), "content-sources"), "NestedSource");
        nestedDir.mkdirs();

        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-2");
        source.setName("NestedSource");
        source.setTypeId("com.vmw.vro.workflow");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        store.exportContent();

        File exported = new File(nestedDir, "details.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("NestedSource"));
    }

    @Test
    void testExportContent_WritesToExistingFlatFile() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        File flatFile = new File(new File(pkg.getFilesystemPath(), "content-sources"), "ExistingFlat.json");
        flatFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(flatFile)) {
            writer.write("{\"old\":true}");
        }

        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-3");
        source.setName("ExistingFlat");
        source.setTypeId("com.vmw.vro.workflow");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        store.exportContent();

        String content = new String(Files.readAllBytes(flatFile.toPath()));
        assertTrue(content.contains("cs-3"));
        assertFalse(content.contains("old"));
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getContentSource()).thenReturn(Collections.singletonList("OtherSource"));
        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-1");
        source.setName("ExcludedSource");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "content-sources"), "ExcludedSource.json");
        assertFalse(exported.exists());
    }

    @Test
    void testExportContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(descriptor.getContentSource()).thenReturn(null);
        when(restClient.getContentSources()).thenReturn(Collections.emptyList());

        store.exportContent();

        File contentSourcesDir = new File(pkg.getFilesystemPath(), "content-sources");
        assertFalse(contentSourcesDir.exists());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        when(descriptor.getContentSource()).thenReturn(Collections.singletonList("TargetSource"));
        VcfaContentSource target = new VcfaContentSource();
        target.setId("cs-target");
        target.setName("TargetSource");
        VcfaContentSource other = new VcfaContentSource();
        other.setId("cs-other");
        other.setName("OtherSource");

        when(restClient.getContentSources()).thenReturn(Arrays.asList(target, other));

        store.deleteContent();

        verify(restClient).deleteContentSource("cs-target");
        verify(restClient, never()).deleteContentSource("cs-other");
    }

    @Test
    void testDeleteContent_EmptyListSkips() throws IOException {
        writeContentYaml("content-source: []\n");
        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-1");
        source.setName("OnlySource");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteContentSource(anyString());
    }

    @Test
    void testDeleteContent_MissingDescriptorSkips() throws IOException {
        VcfaContentSource source = new VcfaContentSource();
        source.setId("cs-1");
        source.setName("OnlySource");

        when(restClient.getContentSources()).thenReturn(Collections.singletonList(source));

        store.deleteContent();

        verify(restClient, never()).deleteContentSource(anyString());
    }

    // ==================== Helpers ====================

    private File createContentSourceSource(String name, String fileName, String json) throws IOException {
        File srcDir = new File(tempDir, "src");
        File contentSourcesDir = new File(srcDir, "content-sources");
        contentSourcesDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(contentSourcesDir, fileName))) {
            writer.write(json);
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
