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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaResourceActionStore import, export, and delete behavior.
 */
class VcfaResourceActionStoreTest {

    @TempDir
    File tempDir;

    private VcfaResourceActionStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaResourceActionStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesActionFolderWithDetails() throws IOException {
        VcfaResourceAction action = createAction("ra-1", "PowerOn", "Power on VM", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(action));

        store.exportContent();

        File detailsFile = new File(new File(new File(pkg.getFilesystemPath(), "resource-actions"), "PowerOn"), "details.json");
        assertTrue(detailsFile.exists());
        String content = new String(Files.readAllBytes(detailsFile.toPath()));
        assertTrue(content.contains("Power on VM"));
        assertTrue(content.contains("Cloud.vSphere.Machine"));
    }

    @Test
    void testExportContent_WritesFormDataAndStylesFiles() throws IOException {
        VcfaResourceAction action = createAction("ra-1", "PowerOn", "Power on VM", "Cloud.vSphere.Machine");
        Map<String, Object> form = new HashMap<>();
        form.put("form", Collections.singletonMap("layout", "vertical"));
        form.put("styles", ".body{}");
        action.setFormDefinition(form);

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(action));

        store.exportContent();

        File actionFolder = new File(new File(pkg.getFilesystemPath(), "resource-actions"), "PowerOn");
        File formFile = new File(actionFolder, "PowerOn__FormData.json");
        File stylesFile = new File(actionFolder, "styles.css");
        assertTrue(formFile.exists());
        assertTrue(stylesFile.exists());
        String formContent = new String(Files.readAllBytes(formFile.toPath()));
        assertTrue(formContent.contains("vertical"));
        assertFalse(formContent.contains("styles"));
        String stylesContent = new String(Files.readAllBytes(stylesFile.toPath()));
        assertEquals(".body{}", stylesContent);
    }

    @Test
    void testExportContent_UnescapesStringFormInFormDefinition() throws IOException {
        VcfaResourceAction action = createAction("ra-1", "PowerOn", "Power on VM", "Cloud.vSphere.Machine");
        Map<String, Object> form = new HashMap<>();
        form.put("form", "{\"layout\":\"vertical\"}");
        form.put("styles", ".body{}");
        action.setFormDefinition(form);

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(action));

        store.exportContent();

        File formFile = new File(new File(new File(pkg.getFilesystemPath(), "resource-actions"), "PowerOn"), "PowerOn__FormData.json");
        String content = new String(Files.readAllBytes(formFile.toPath()));
        assertTrue(content.contains("\"layout\" : \"vertical\""));
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("resource-action:\n  - OtherAction\n");
        VcfaResourceAction action = createAction("ra-1", "ExcludedAction", "Excluded", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(action));

        setUserDir(tempDir, () -> store.exportContent());

        File actionFolder = new File(new File(pkg.getFilesystemPath(), "resource-actions"), "ExcludedAction");
        assertFalse(actionFolder.exists());
    }

    @Test
    void testExportContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("resource-action: []\n");

        store.exportContent();

        verify(restClient, never()).getResourceActions();
        File actionsDir = new File(pkg.getFilesystemPath(), "resource-actions");
        assertFalse(actionsDir.exists());
    }

    @Test
    void testExportContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.exportContent();

        File actionsDir = new File(pkg.getFilesystemPath(), "resource-actions");
        assertFalse(actionsDir.exists());
    }

    @Test
    void testExportContent_RestClientNullSkips() {
        VcfaResourceActionStore bareStore = new VcfaResourceActionStore();
        bareStore.init(null, pkg, config, descriptor);
        assertDoesNotThrow(bareStore::exportContent);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewAction() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"description\":\"Power on VM\",\"resourceType\":\"Cloud.vSphere.Machine\",\"runnableItem\":{\"id\":\"wf-1\"}}",
                "{\"form\":{\"layout\":\"vertical\"},\"styles\":\".body{}\"}",
                ".body{}");

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createResourceAction(argThat(action -> "PowerOn".equals(action.getDisplayName())
                && "Cloud.vSphere.Machine".equals(action.getResourceType())));
    }

    @Test
    void testImportContent_RecreatesActionWhenChanged() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"description\":\"local\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":{\"layout\":\"vertical\"}}",
                "");

        VcfaResourceAction remote = createAction("ra-remote", "PowerOn", "remote", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient).deleteResourceAction("ra-remote");
        verify(restClient).createResourceAction(argThat(action -> "local".equals(action.getDescription())));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"description\":\"same\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":null,\"styles\":\"\"}",
                "");

        VcfaResourceAction remote = createAction("ra-remote", "PowerOn", "same", "Cloud.vSphere.Machine");
        Map<String, Object> formDef = new HashMap<>();
        formDef.put("form", null);
        formDef.put("styles", "");
        remote.setFormDefinition(formDef);

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(remote));

        store.importContent(srcDir);

        verify(restClient, never()).deleteResourceAction(anyString());
        verify(restClient, never()).createResourceAction(any());
    }

    @Test
    void testImportContent_UnwrapsNestedFormBlock() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":{\"form\":{\"layout\":\"vertical\"},\"schema\":{}},\"styles\":\".body{}\"}",
                "");

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createResourceAction(argThat(action -> {
            Map<String, Object> formDef = action.getFormDefinition();
            return formDef != null && formDef.containsKey("form")
                    && formDef.get("form") instanceof String
                    && formDef.get("form").toString().contains("layout");
        }));
    }

    @Test
    void testImportContent_StringifiesFormObject() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":{\"foo\":\"bar\"}}",
                "");

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createResourceAction(argThat(action -> {
            Map<String, Object> formDef = action.getFormDefinition();
            return formDef != null && formDef.get("form") instanceof String
                    && formDef.get("form").toString().contains("bar");
        }));
    }

    @Test
    void testImportContent_StripsFormDefinitionIdOnCreate() throws IOException {
        File srcDir = createResourceActionSource("PowerOn",
                "{\"id\":\"ra-1\",\"displayName\":\"PowerOn\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"id\":\"form-1\",\"form\":{\"layout\":\"vertical\"}}",
                "");

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createResourceAction(argThat(action -> {
            Map<String, Object> formDef = action.getFormDefinition();
            return formDef != null && !formDef.containsKey("id");
        }));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("resource-action:\n  - OtherAction\n");
        File srcDir = createResourceActionSource("ExcludedAction",
                "{\"id\":\"ra-1\",\"displayName\":\"ExcludedAction\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":{}}",
                "");

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient).getResourceActions();
        verify(restClient, never()).createResourceAction(any());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("resource-action: []\n");
        File srcDir = createResourceActionSource("SkippedAction",
                "{\"id\":\"ra-1\",\"displayName\":\"SkippedAction\",\"resourceType\":\"Cloud.vSphere.Machine\"}",
                "{\"form\":{}}",
                "");

        store.importContent(srcDir);

        verify(restClient, never()).getResourceActions();
    }

    @Test
    void testImportContent_MissingResourceActionsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getResourceActions();
    }

    @Test
    void testImportContent_EmptyResourceActionsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        new File(srcDir, "resource-actions").mkdirs();

        when(restClient.getResourceActions()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).getResourceActions();
        verify(restClient, never()).createResourceAction(any());
    }

    @Test
    void testImportContent_MissingDetailsFileThrows() throws IOException {
        File srcDir = new File(tempDir, "src");
        File actionsDir = new File(srcDir, "resource-actions");
        File actionDir = new File(actionsDir, "PowerOn");
        actionDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(actionDir, "PowerOn__FormData.json"))) {
            writer.write("{\"form\":{}}");
        }

        assertThrows(RuntimeException.class, () -> store.importContent(srcDir));
    }

    @Test
    void testImportContent_MissingFormDataFileThrows() throws IOException {
        File srcDir = new File(tempDir, "src");
        File actionsDir = new File(srcDir, "resource-actions");
        File actionDir = new File(actionsDir, "PowerOn");
        actionDir.mkdirs();
        try (FileWriter writer = new FileWriter(new File(actionDir, "details.json"))) {
            writer.write("{\"id\":\"ra-1\",\"displayName\":\"PowerOn\"}");
        }

        assertThrows(RuntimeException.class, () -> store.importContent(srcDir));
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaResourceAction one = createAction("ra-1", "One", "First", "Cloud.vSphere.Machine");
        VcfaResourceAction two = createAction("ra-2", "Two", "Second", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deleteResourceAction("ra-1");
        verify(restClient).deleteResourceAction("ra-2");
    }

    @Test
    void testDeleteContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("resource-action: []\n");
        VcfaResourceAction action = createAction("ra-1", "Only", "Only", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Collections.singletonList(action));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteResourceAction(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("resource-action:\n  - Target\n");
        VcfaResourceAction target = createAction("ra-target", "Target", "Target", "Cloud.vSphere.Machine");
        VcfaResourceAction other = createAction("ra-other", "Other", "Other", "Cloud.vSphere.Machine");

        when(restClient.getResourceActions()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteResourceAction("ra-target");
        verify(restClient, never()).deleteResourceAction("ra-other");
    }

    // ==================== Helpers ====================

    private VcfaResourceAction createAction(String id, String displayName, String description, String resourceType) {
        VcfaResourceAction action = new VcfaResourceAction();
        action.setId(id);
        action.setDisplayName(displayName);
        action.setDescription(description);
        action.setResourceType(resourceType);
        action.setStatus("RELEASED");
        return action;
    }

    private File createResourceActionSource(String name, String detailsJson, String formDataJson, String styles)
            throws IOException {
        File srcDir = new File(tempDir, "src");
        File actionsDir = new File(srcDir, "resource-actions");
        File actionDir = new File(actionsDir, name);
        actionDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(actionDir, "details.json"))) {
            writer.write(detailsJson);
        }
        try (FileWriter writer = new FileWriter(new File(actionDir, name + "__FormData.json"))) {
            writer.write(formDataJson);
        }
        if (styles != null) {
            try (FileWriter writer = new FileWriter(new File(actionDir, "styles.css"))) {
                writer.write(styles);
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
