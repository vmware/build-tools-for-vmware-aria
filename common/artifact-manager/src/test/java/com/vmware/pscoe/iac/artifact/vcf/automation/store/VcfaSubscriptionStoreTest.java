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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaSubscriptionStore import, export, and delete behavior.
 */
class VcfaSubscriptionStoreTest {

    @TempDir
    File tempDir;

    private VcfaSubscriptionStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaSubscriptionStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewSubscription() throws IOException {
        File srcDir = createSubscriptionSource("SubOne",
                "{\"id\":\"sub-1\",\"name\":\"SubOne\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.vro\",\"runnableId\":\"wf-1\"}");

        when(restClient.getSubscriptions()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).createSubscription(any(VcfaSubscription.class));
    }

    @Test
    void testImportContent_UpdatesSubscriptionWhenChanged() throws IOException {
        File srcDir = createSubscriptionSource("SubOne",
                "{\"id\":\"sub-1\",\"name\":\"SubOne\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.vro\",\"runnableId\":\"wf-1\",\"disabled\":false}");

        VcfaSubscription existing = new VcfaSubscription();
        existing.setId("existing-sub-1");
        existing.setName("SubOne");
        existing.setEventTopicId("compute.allocation.pre");
        existing.setRunnableType("extensibility.vro");
        existing.setRunnableId("wf-1");
        existing.setDisabled(true);

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(existing));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).updateSubscription(eq("existing-sub-1"), any(VcfaSubscription.class));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        File srcDir = createSubscriptionSource("SubOne",
                "{\"id\":\"sub-1\",\"name\":\"SubOne\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.vro\",\"runnableId\":\"wf-1\"}");

        VcfaSubscription existing = new VcfaSubscription();
        existing.setId("existing-sub-1");
        existing.setName("SubOne");
        existing.setEventTopicId("compute.allocation.pre");
        existing.setRunnableType("extensibility.vro");
        existing.setRunnableId("wf-1");

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(existing));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient, never()).createSubscription(any());
        verify(restClient, never()).updateSubscription(anyString(), any());
    }

    @Test
    void testImportContent_ResolvesAbxRunnableName() throws IOException {
        File srcDir = createSubscriptionSource("SubAbx",
                "{\"id\":\"sub-2\",\"name\":\"SubAbx\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.abx\",\"runnableName\":\"MyAction\"}");

        when(restClient.getSubscriptions()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(restClient.getAbxActionIdByName("MyAction")).thenReturn("abx-id-1");

        store.importContent(srcDir);

        verify(restClient).getAbxActionIdByName("MyAction");
        verify(restClient).createSubscription(any(VcfaSubscription.class));
    }

    @Test
    void testImportContent_SubstitutesProjectNames() throws IOException {
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("projectNames", Collections.singletonList("My Project"));
        String json = subscriptionJson("sub-3", "SubProject", "extensibility.vro", "wf-1", constraints);
        File srcDir = createSubscriptionSource("SubProject", json);

        when(restClient.getSubscriptions()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");
        when(restClient.getProjectId("My Project")).thenReturn("proj-2");

        store.importContent(srcDir);

        verify(restClient).getProjectId("My Project");
        verify(restClient).createSubscription(any(VcfaSubscription.class));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("subscription:\n  - OtherSub\n");
        File srcDir = createSubscriptionSource("SubOne",
                "{\"id\":\"sub-1\",\"name\":\"SubOne\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.vro\",\"runnableId\":\"wf-1\"}");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient, never()).createSubscription(any());
        verify(restClient, never()).updateSubscription(anyString(), any());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("subscription: []\n");
        File srcDir = createSubscriptionSource("SubOne",
                "{\"id\":\"sub-1\",\"name\":\"SubOne\",\"eventTopicId\":\"compute.allocation.pre\",\"runnableType\":\"extensibility.vro\",\"runnableId\":\"wf-1\"}");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient, never()).getSubscriptions();
        verify(restClient, never()).createSubscription(any());
    }

    @Test
    void testImportContent_MissingSubscriptionsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getSubscriptions();
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesSubscriptionToDisk() throws IOException {
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-1");
        sub.setName("SubOne");
        sub.setEventTopicId("compute.allocation.pre");
        sub.setRunnableType("extensibility.vro");
        sub.setRunnableId("wf-1");

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(sub));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "subscriptions"), "SubOne.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("SubOne"));
        assertTrue(content.contains("compute.allocation.pre"));
    }

    @Test
    void testExportContent_ResolvesAbxRunnableName() throws IOException {
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-2");
        sub.setName("SubAbx");
        sub.setEventTopicId("compute.allocation.pre");
        sub.setRunnableType("extensibility.abx");
        sub.setRunnableId("abx-id-1");

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(sub));
        when(restClient.getAbxActionNameById("abx-id-1")).thenReturn("MyAction");

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "subscriptions"), "SubAbx.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("MyAction"));
        assertFalse(content.contains("abx-id-1"));
    }

    @Test
    void testExportContent_ResolvesProjectNames() throws IOException {
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-3");
        sub.setName("SubProject");
        sub.setEventTopicId("compute.allocation.pre");
        sub.setRunnableType("extensibility.vro");
        sub.setRunnableId("wf-1");
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("projectId", Collections.singletonList("proj-1"));
        sub.setConstraints(constraints);

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(sub));
        when(restClient.getProjectNameById()).thenReturn("My Project");

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "subscriptions"), "SubProject.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("My Project"));
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("subscription:\n  - OtherSub\n");
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-1");
        sub.setName("SubOne");

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(sub));

        setUserDir(tempDir, () -> store.exportContent());

        File exported = new File(new File(pkg.getFilesystemPath(), "subscriptions"), "SubOne.json");
        assertFalse(exported.exists());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaSubscription one = new VcfaSubscription();
        one.setId("sub-1");
        one.setName("One");
        VcfaSubscription two = new VcfaSubscription();
        two.setId("sub-2");
        two.setName("Two");

        when(restClient.getSubscriptions()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deleteSubscription("sub-1");
        verify(restClient).deleteSubscription("sub-2");
    }

    @Test
    void testDeleteContent_EmptyListSkips() throws IOException {
        writeContentYaml("subscription: []\n");
        VcfaSubscription sub = new VcfaSubscription();
        sub.setId("sub-1");
        sub.setName("One");

        when(restClient.getSubscriptions()).thenReturn(Collections.singletonList(sub));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deleteSubscription(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("subscription:\n  - Target\n");
        VcfaSubscription target = new VcfaSubscription();
        target.setId("sub-target");
        target.setName("Target");
        VcfaSubscription other = new VcfaSubscription();
        other.setId("sub-other");
        other.setName("Other");

        when(restClient.getSubscriptions()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deleteSubscription("sub-target");
        verify(restClient, never()).deleteSubscription("sub-other");
    }

    // ==================== Helpers ====================

    private File createSubscriptionSource(String name, String json) throws IOException {
        File srcDir = new File(tempDir, "src");
        File subsDir = new File(srcDir, "subscriptions");
        subsDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(subsDir, name + ".json"))) {
            writer.write(json);
        }
        return srcDir;
    }

    private String subscriptionJson(String id, String name, String runnableType, String runnableId,
            Map<String, Object> constraints) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(id).append("\",");
        sb.append("\"name\":\"").append(name).append("\",");
        sb.append("\"eventTopicId\":\"compute.allocation.pre\",");
        sb.append("\"runnableType\":\"").append(runnableType).append("\",");
        sb.append("\"runnableId\":\"").append(runnableId).append("\"");
        if (constraints != null && !constraints.isEmpty()) {
            sb.append(",\"constraints\":").append(mapToJson(constraints));
        }
        sb.append("}");
        return sb.toString();
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof java.util.List) {
                sb.append("[");
                java.util.List<?> list = (java.util.List<?>) value;
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append("\"").append(list.get(i)).append("\"");
                }
                sb.append("]");
            } else {
                sb.append("null");
            }
        }
        sb.append("}");
        return sb.toString();
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
