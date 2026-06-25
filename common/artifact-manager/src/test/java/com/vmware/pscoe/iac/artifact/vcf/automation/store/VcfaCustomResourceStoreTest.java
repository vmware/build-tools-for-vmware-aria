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
import java.util.HashMap;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaCustomResourceStore import, export, and delete behavior.
 */
class VcfaCustomResourceStoreTest {

    @TempDir
    File tempDir;

    private VcfaCustomResourceStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaCustomResourceStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewResource() throws IOException {
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\",\"status\":\"RELEASED\",\"schemaType\":\"ABSTRACT\",\"mainActions\":{\"create\":\"action-1\"}}",
                null);

        when(restClient.getCustomResources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createCustomResourceType(any(VcfaCustomResourceType.class));
    }

    @Test
    void testImportContent_RecreatesResourceWhenChanged() throws IOException {
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\",\"status\":\"RELEASED\",\"schemaType\":\"ABSTRACT\",\"mainActions\":{\"create\":\"action-1\"},\"description\":\"local\"}",
                null);

        VcfaCustomResourceType existing = new VcfaCustomResourceType();
        existing.setId("crt-existing");
        existing.setDisplayName("MyResource");
        existing.setName("Custom.MyResource");
        existing.setResourceType("Custom.MyResource");
        existing.setExternalType("vRO.MyResource");
        existing.setSchemaType("ABSTRACT");
        existing.setMainActions(Collections.singletonMap("create", "action-1"));
        existing.setDescription("remote");

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(existing));

        store.importContent(srcDir);

        verify(restClient).deleteCustomResourceType("crt-existing");
        verify(restClient).createCustomResourceType(any(VcfaCustomResourceType.class));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\",\"status\":\"RELEASED\",\"schemaType\":\"ABSTRACT\",\"mainActions\":{\"create\":\"action-1\"}}",
                null);

        VcfaCustomResourceType existing = new VcfaCustomResourceType();
        existing.setId("crt-existing");
        existing.setDisplayName("MyResource");
        existing.setName("Custom.MyResource");
        existing.setResourceType("Custom.MyResource");
        existing.setExternalType("vRO.MyResource");
        existing.setSchemaType("ABSTRACT");
        existing.setMainActions(Collections.singletonMap("create", "action-1"));
        existing.setAdditionalActions(Collections.emptyList());

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(existing));

        store.importContent(srcDir);

        verify(restClient, never()).deleteCustomResourceType(anyString());
        verify(restClient, never()).createCustomResourceType(any());
    }

    @Test
    void testImportContent_WithAdditionalActions() throws IOException {
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\",\"status\":\"RELEASED\",\"schemaType\":\"ABSTRACT\",\"mainActions\":{\"create\":\"action-1\"}}",
                Collections.singletonMap("Scale",
                        new ActionFiles(
                                "{\"id\":\"action-scale\",\"displayName\":\"Scale\",\"name\":\"Scale\"}",
                                "{\"form\":{\"layout\":\"vertical\"},\"styles\":\".body{}\"}",
                                ".body{}")));

        when(restClient.getCustomResources()).thenReturn(Collections.emptyList());

        store.importContent(srcDir);

        verify(restClient).createCustomResourceType(any(VcfaCustomResourceType.class));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("custom-resource:\n  - OtherResource\n");
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\"}",
                null);

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient, never()).createCustomResourceType(any());
        verify(restClient, never()).deleteCustomResourceType(anyString());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("custom-resource: []\n");
        File srcDir = createCustomResourceSource("MyResource",
                "{\"id\":\"crt-1\",\"displayName\":\"MyResource\",\"resourceType\":\"Custom.MyResource\",\"externalType\":\"vRO.MyResource\"}",
                null);

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient, never()).getCustomResources();
        verify(restClient, never()).createCustomResourceType(any());
    }

    @Test
    void testImportContent_MissingCustomResourcesFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getCustomResources();
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesResourceToDisk() throws IOException {
        VcfaCustomResourceType resource = new VcfaCustomResourceType();
        resource.setId("crt-1");
        resource.setDisplayName("MyResource");
        resource.setResourceType("Custom.MyResource");
        resource.setExternalType("vRO.MyResource");
        resource.setStatus("RELEASED");
        resource.setSchemaType("ABSTRACT");
        resource.setMainActions(Collections.singletonMap("create", "action-1"));

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(resource));

        store.exportContent();

        File resourceFolder = new File(new File(pkg.getFilesystemPath(), "custom-resources"), "MyResource");
        assertTrue(resourceFolder.exists());
        File detailsFile = new File(resourceFolder, "details.json");
        assertTrue(detailsFile.exists());
        String content = new String(Files.readAllBytes(detailsFile.toPath()));
        assertTrue(content.contains("MyResource"));
    }

    @Test
    void testExportContent_WritesAdditionalActions() throws IOException {
        VcfaCustomResourceType resource = new VcfaCustomResourceType();
        resource.setId("crt-1");
        resource.setDisplayName("MyResource");
        resource.setResourceType("Custom.MyResource");
        resource.setExternalType("vRO.MyResource");
        resource.setStatus("RELEASED");
        resource.setSchemaType("ABSTRACT");
        resource.setMainActions(Collections.singletonMap("create", "action-1"));

        Map<String, Object> action = new HashMap<>();
        action.put("id", "action-scale");
        action.put("displayName", "Scale");
        action.put("name", "Scale");

        Map<String, Object> formDefinition = new HashMap<>();
        Map<String, Object> form = new HashMap<>();
        form.put("layout", "vertical");
        formDefinition.put("form", form);
        formDefinition.put("styles", ".body{}");
        action.put("formDefinition", formDefinition);

        resource.setAdditionalActions(Collections.singletonList(action));

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(resource));

        store.exportContent();

        File resourceFolder = new File(new File(pkg.getFilesystemPath(), "custom-resources"), "MyResource");
        File actionsFolder = new File(resourceFolder, "additional-actions");
        assertTrue(actionsFolder.exists());
        File scaleFolder = new File(actionsFolder, "Scale");
        assertTrue(scaleFolder.exists());
        assertTrue(new File(scaleFolder, "details.json").exists());
        assertTrue(new File(scaleFolder, "Scale__FormData.json").exists());
        assertTrue(new File(scaleFolder, "styles.css").exists());
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("custom-resource:\n  - OtherResource\n");
        VcfaCustomResourceType resource = new VcfaCustomResourceType();
        resource.setId("crt-1");
        resource.setDisplayName("MyResource");

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(resource));

        setUserDir(tempDir, () -> store.exportContent());

        File resourceFolder = new File(new File(pkg.getFilesystemPath(), "custom-resources"), "MyResource");
        assertFalse(resourceFolder.exists());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaCustomResourceType one = new VcfaCustomResourceType();
        one.setId("crt-1");
        one.setDisplayName("One");
        VcfaCustomResourceType two = new VcfaCustomResourceType();
        two.setId("crt-2");
        two.setDisplayName("Two");

        when(restClient.getCustomResources()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deleteCustomResourceType("crt-1");
        verify(restClient).deleteCustomResourceType("crt-2");
    }

    @Test
    void testDeleteContent_EmptyListSkips() throws IOException {
        writeContentYaml("custom-resource: []\n");
        VcfaCustomResourceType resource = new VcfaCustomResourceType();
        resource.setId("crt-1");
        resource.setDisplayName("One");

        when(restClient.getCustomResources()).thenReturn(Collections.singletonList(resource));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteCustomResourceType(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("custom-resource:\n  - Target\n");
        VcfaCustomResourceType target = new VcfaCustomResourceType();
        target.setId("crt-target");
        target.setDisplayName("Target");
        VcfaCustomResourceType other = new VcfaCustomResourceType();
        other.setId("crt-other");
        other.setDisplayName("Other");

        when(restClient.getCustomResources()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteCustomResourceType("crt-target");
        verify(restClient, never()).deleteCustomResourceType("crt-other");
    }

    // ==================== Helpers ====================

    private File createCustomResourceSource(String name, String detailsJson, Map<String, ActionFiles> actions)
            throws IOException {
        File srcDir = new File(tempDir, "src");
        File resourcesDir = new File(srcDir, "custom-resources");
        File resourceDir = new File(resourcesDir, name);
        resourceDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(resourceDir, "details.json"))) {
            writer.write(detailsJson);
        }

        if (actions != null && !actions.isEmpty()) {
            File actionsDir = new File(resourceDir, "additional-actions");
            for (Map.Entry<String, ActionFiles> entry : actions.entrySet()) {
                File actionDir = new File(actionsDir, entry.getKey());
                actionDir.mkdirs();
                ActionFiles files = entry.getValue();
                try (FileWriter writer = new FileWriter(new File(actionDir, "details.json"))) {
                    writer.write(files.details);
                }
                try (FileWriter writer = new FileWriter(new File(actionDir, entry.getKey() + "__FormData.json"))) {
                    writer.write(files.formData);
                }
                try (FileWriter writer = new FileWriter(new File(actionDir, "styles.css"))) {
                    writer.write(files.styles);
                }
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

    private static final class ActionFiles {
        private final String details;
        private final String formData;
        private final String styles;

        private ActionFiles(String details, String formData, String styles) {
            this.details = details;
            this.formData = formData;
            this.styles = styles;
        }
    }
}
