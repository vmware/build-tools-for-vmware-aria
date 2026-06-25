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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaCatalogItemStore import, export, and delete behavior.
 */
class VcfaCatalogItemStoreTest {

    @TempDir
    File tempDir;

    private VcfaCatalogItemStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaCatalogItemStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesCatalogItemToDisk() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-1");
        item.setName("TestCatalogItem");
        item.setGlobal(true);
        item.setBulkRequestLimit(10);

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "catalog-items"), "TestCatalogItem.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("TestCatalogItem"));
    }

    @Test
    void testExportContent_WritesBlueprintCustomFormFromLocalDetails() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-1");
        item.setName("BlueprintCatalogItem");
        item.setType(Collections.singletonMap("id", "com.vmw.blueprint"));

        VcfaCatalogItemForm form = new VcfaCatalogItemForm();
        form.setId("form-1");
        form.setName("BlueprintCatalogItem");
        form.setFormFromJson(new ObjectMapper().readTree("{\"layout\":\"vertical\"}"));
        form.setStyles(".body{}");

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));
        when(restClient.getCatalogItemForm("com.vmw.blueprint", "bp-remote-1")).thenReturn(form);

        createLocalBlueprintDetails("BlueprintCatalogItem", "bp-remote-1");

        store.exportContent();

        File formFile = new File(new File(new File(pkg.getFilesystemPath(), "catalog-items"), "forms"),
                "BlueprintCatalogItem__FormData.json");
        assertTrue(formFile.exists());
        String content = new String(Files.readAllBytes(formFile.toPath()));
        assertTrue(content.contains("vertical"));
    }

    @Test
    void testExportContent_WritesNonBlueprintCustomForm() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-2");
        item.setName("WorkflowCatalogItem");
        item.setType(Collections.singletonMap("id", "com.vmw.vro.workflow"));

        VcfaCatalogItemForm form = new VcfaCatalogItemForm();
        form.setId("form-2");
        form.setName("WorkflowCatalogItem");
        form.setFormFromJson(new ObjectMapper().readTree("{\"layout\":\"horizontal\"}"));

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));
        when(restClient.getCatalogItemForm("com.vmw.vro.workflow", "ci-2")).thenReturn(form);

        store.exportContent();

        File formFile = new File(new File(new File(pkg.getFilesystemPath(), "catalog-items"), "forms"),
                "WorkflowCatalogItem__FormData.json");
        assertTrue(formFile.exists());
        String content = new String(Files.readAllBytes(formFile.toPath()));
        assertTrue(content.contains("horizontal"));
    }

    @Test
    void testExportContent_SkipsWhenNoFormFound() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-3");
        item.setName("StandardCatalogItem");
        item.setType(Collections.singletonMap("id", "com.vmw.blueprint"));

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));
        when(restClient.getCatalogItemForm(anyString(), anyString())).thenReturn(null);

        createLocalBlueprintDetails("StandardCatalogItem", "bp-remote-3");

        store.exportContent();

        File formsDir = new File(new File(pkg.getFilesystemPath(), "catalog-items"), "forms");
        assertTrue(!formsDir.exists() || formsDir.list() == null || formsDir.list().length == 0);
    }

    @Test
    void testExportContent_SkipsWhenNoSourceType() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-4");
        item.setName("PlainCatalogItem");

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));

        store.exportContent();

        verify(restClient, never()).getCatalogItemForm(anyString(), anyString());
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(Collections.singletonList("OtherItem"));
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-1");
        item.setName("TestCatalogItem");

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "catalog-items"), "TestCatalogItem.json");
        assertFalse(exported.exists());
    }

    @Test
    void testExportContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());

        store.exportContent();

        File catalogDir = new File(pkg.getFilesystemPath(), "catalog-items");
        assertFalse(catalogDir.exists());
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewCatalogItem() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        File srcDir = createCatalogItemSource("NewItem",
                "{\"id\":\"ci-new\",\"name\":\"NewItem\",\"global\":true,\"bulkRequestLimit\":5}");

        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());
        VcfaCatalogItem created = new VcfaCatalogItem();
        created.setId("ci-created");
        created.setName("NewItem");
        when(restClient.createCatalogItem(any(VcfaCatalogItem.class))).thenReturn(created);

        store.importContent(srcDir);

        verify(restClient).createCatalogItem(argThat(item -> "NewItem".equals(item.getName()) && item.getId() == null));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        File srcDir = createCatalogItemSource("SameItem",
                "{\"id\":\"ci-same\",\"name\":\"SameItem\",\"global\":true,\"bulkRequestLimit\":5}");

        VcfaCatalogItem remote = new VcfaCatalogItem();
        remote.setId("ci-remote");
        remote.setName("SameItem");
        remote.setGlobal(true);
        remote.setBulkRequestLimit(5);

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient, never()).createCatalogItem(any());
        verify(restClient, never()).deleteCatalogItem(anyString());
    }

    @Test
    void testImportContent_RecreatesWhenChanged() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        File srcDir = createCatalogItemSource("ChangedItem",
                "{\"id\":\"ci-changed\",\"name\":\"ChangedItem\",\"global\":true,\"bulkRequestLimit\":10}");

        VcfaCatalogItem remote = new VcfaCatalogItem();
        remote.setId("ci-remote");
        remote.setName("ChangedItem");
        remote.setGlobal(true);
        remote.setBulkRequestLimit(5);

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(remote));
        VcfaCatalogItem recreated = new VcfaCatalogItem();
        recreated.setId("ci-recreated");
        recreated.setName("ChangedItem");
        when(restClient.createCatalogItem(any(VcfaCatalogItem.class))).thenReturn(recreated);

        store.importContent(srcDir);

        verify(restClient).deleteCatalogItem("ci-remote");
        verify(restClient, times(1)).createCatalogItem(any(VcfaCatalogItem.class));
    }

    @Test
    void testImportContent_PushesBlueprintFormUsingLocalDetailsId() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        File srcDir = createCatalogItemSource("BpItem",
                "{\"id\":\"ci-bp\",\"name\":\"BpItem\",\"type\":{\"id\":\"com.vmw.blueprint\"}}");

        File formsDir = new File(new File(srcDir, "catalog-items"), "forms");
        File formFile = new File(formsDir, "BpItem__FormData.json");
        try (FileWriter writer = new FileWriter(formFile)) {
            writer.write("{\"form\":{\"layout\":\"vertical\"},\"styles\":\".body{}\"}");
        }

        createBlueprintDetailsInSource(srcDir, "BpItem", "bp-local-1");

        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());
        VcfaCatalogItem created = new VcfaCatalogItem();
        created.setId("ci-created");
        created.setName("BpItem");
        when(restClient.createCatalogItem(any(VcfaCatalogItem.class))).thenReturn(created);

        store.importContent(srcDir);

        verify(restClient).updateCatalogItemForm(eq("com.vmw.blueprint"), eq("ci-created"), any(VcfaCatalogItemForm.class));
    }

    @Test
    void testImportContent_PushesNonBlueprintFormUsingCatalogItemId() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(null);
        File srcDir = createCatalogItemSource("WfItem",
                "{\"id\":\"ci-wf\",\"name\":\"WfItem\",\"type\":{\"id\":\"com.vmw.vro.workflow\"}}");

        File formsDir = new File(new File(srcDir, "catalog-items"), "forms");
        File formFile = new File(formsDir, "WfItem__FormData.json");
        try (FileWriter writer = new FileWriter(formFile)) {
            writer.write("{\"form\":{\"layout\":\"horizontal\"}}");
        }

        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());
        VcfaCatalogItem created = new VcfaCatalogItem();
        created.setId("ci-wf-created");
        created.setName("WfItem");
        when(restClient.createCatalogItem(any(VcfaCatalogItem.class))).thenReturn(created);

        store.importContent(srcDir);

        verify(restClient).updateCatalogItemForm(eq("com.vmw.vro.workflow"), eq("ci-wf-created"), any(VcfaCatalogItemForm.class));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(Collections.singletonList("OtherItem"));
        File srcDir = createCatalogItemSource("SkippedItem",
                "{\"id\":\"ci-skip\",\"name\":\"SkippedItem\"}");

        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getCatalogItems();
        verify(restClient, never()).createCatalogItem(any());
        verify(restClient, never()).deleteCatalogItem(anyString());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        when(descriptor.getCatalogItem()).thenReturn(Collections.emptyList());
        File srcDir = createCatalogItemSource("SkippedItem",
                "{\"id\":\"ci-skip\",\"name\":\"SkippedItem\"}");

        when(restClient.getCatalogItems()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getCatalogItems();
        verify(restClient, never()).createCatalogItem(any());
        verify(restClient, never()).deleteCatalogItem(anyString());
    }

    @Test
    void testImportContent_MissingCatalogItemsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getCatalogItems();
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaCatalogItem one = new VcfaCatalogItem();
        one.setId("ci-1");
        one.setName("One");
        VcfaCatalogItem two = new VcfaCatalogItem();
        two.setId("ci-2");
        two.setName("Two");

        when(restClient.getCatalogItems()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deleteCatalogItem("ci-1");
        verify(restClient).deleteCatalogItem("ci-2");
    }

    @Test
    void testDeleteContent_EmptyListSkips() throws IOException {
        writeContentYaml("catalog-item: []\n");
        VcfaCatalogItem item = new VcfaCatalogItem();
        item.setId("ci-1");
        item.setName("One");

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(item));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteCatalogItem(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("catalog-item:\n  - Target\n");
        VcfaCatalogItem target = new VcfaCatalogItem();
        target.setId("ci-target");
        target.setName("Target");
        VcfaCatalogItem other = new VcfaCatalogItem();
        other.setId("ci-other");
        other.setName("Other");

        when(restClient.getCatalogItems()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteCatalogItem("ci-target");
        verify(restClient, never()).deleteCatalogItem("ci-other");
    }

    @Test
    void testDeleteContent_FallsBackToCatalogItemsAlias() throws IOException {
        writeContentYaml("catalogItems:\n  - AliasTarget\n");
        VcfaCatalogItem target = new VcfaCatalogItem();
        target.setId("ci-alias");
        target.setName("AliasTarget");

        when(restClient.getCatalogItems()).thenReturn(Collections.singletonList(target));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteCatalogItem("ci-alias");
    }

    // ==================== Helpers ====================

    private File createCatalogItemSource(String name, String json) throws IOException {
        File srcDir = new File(tempDir, "src");
        File itemsDir = new File(srcDir, "catalog-items");
        File formsDir = new File(itemsDir, "forms");
        formsDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(itemsDir, name + ".json"))) {
            writer.write(json);
        }
        return srcDir;
    }

    private void createLocalBlueprintDetails(String name, String blueprintId) throws IOException {
        File bpDir = new File(new File(pkg.getFilesystemPath(), "blueprints"), name);
        bpDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(bpDir, "details.json"))) {
            writer.write("{\"id\":\"" + blueprintId + "\"}");
        }
    }

    private void createBlueprintDetailsInSource(File srcDir, String name, String blueprintId) throws IOException {
        File bpDir = new File(new File(srcDir, "blueprints"), name);
        bpDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(bpDir, "details.json"))) {
            writer.write("{\"id\":\"" + blueprintId + "\"}");
        }
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

    private static final ObjectMapper MAPPER = new ObjectMapper();
}
