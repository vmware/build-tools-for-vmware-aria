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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaPropertyGroupStore import, export, and delete behavior.
 */
class VcfaPropertyGroupStoreTest {

    @TempDir
    File tempDir;

    private VcfaPropertyGroupStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaPropertyGroupStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesGroupToDiskAndSanitizesFields() throws IOException {
        VcfaPropertyGroup group = new VcfaPropertyGroup();
        group.setId("pg-1");
        group.setName("TestGroup");
        group.setDisplayName("Test Group");
        group.setDescription("A group");
        group.setType("string");
        group.setOrgId("org-1");
        group.setProjectId("proj-1");
        group.setCreatedAt("2023-01-01");
        group.setCreatedBy("admin");
        group.setUpdatedAt("2023-02-01");
        group.setUpdatedBy("admin");
        group.setOrganization(null);
        group.setProperties(Collections.singletonMap("key", "value"));

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(group));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "property-groups"), "Test Group.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("Test Group"));
        assertFalse(content.contains("pg-1"));
        assertFalse(content.contains("org-1"));
        assertFalse(content.contains("proj-1"));
        assertFalse(content.contains("2023-01-01"));
        assertFalse(content.contains("admin"));
        assertFalse(content.contains("\"organization\""));
    }

    @Test
    void testExportContent_KeepsNonNullOrganization() throws IOException {
        VcfaPropertyGroup group = new VcfaPropertyGroup();
        group.setId("pg-1");
        group.setName("OrgGroup");
        group.setDisplayName("Org Group");
        group.setOrganization("My Org");
        group.setProperties(Collections.emptyMap());

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(group));

        store.exportContent();

        File exported = new File(new File(pkg.getFilesystemPath(), "property-groups"), "Org Group.json");
        assertTrue(exported.exists());
        String content = new String(Files.readAllBytes(exported.toPath()));
        assertTrue(content.contains("My Org"));
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("property-group:\n  - OtherGroup\n");
        VcfaPropertyGroup group = new VcfaPropertyGroup();
        group.setId("pg-1");
        group.setName("ExcludedGroup");
        group.setDisplayName("ExcludedGroup");

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(group));

        setUserDir(tempDir, () -> store.exportContent());

        File exported = new File(new File(pkg.getFilesystemPath(), "property-groups"), "ExcludedGroup.json");
        assertFalse(exported.exists());
    }

    @Test
    void testExportContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("property-group: []\n");

        store.exportContent();

        verify(restClient, never()).getPropertyGroups();
        File exportedDir = new File(pkg.getFilesystemPath(), "property-groups");
        assertFalse(exportedDir.exists());
    }

    @Test
    void testExportContent_EmptyRemoteListReturnsEarly() throws IOException {
        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());

        store.exportContent();

        File exportedDir = new File(pkg.getFilesystemPath(), "property-groups");
        assertFalse(exportedDir.exists());
    }

    @Test
    void testExportContent_RestClientNullSkips() {
        VcfaPropertyGroupStore bareStore = new VcfaPropertyGroupStore();
        bareStore.init(null, pkg, config, descriptor);
        assertDoesNotThrow(bareStore::exportContent);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewGroup() throws IOException {
        File srcDir = createPropertyGroupSource("NewGroup",
                "{\"id\":\"pg-1\",\"name\":\"NewGroup\",\"displayName\":\"NewGroup\",\"description\":\"new\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).createPropertyGroup(argThat(group -> "NewGroup".equals(group.getName())
                && "org-1".equals(group.getOrgId())));
    }

    @Test
    void testImportContent_UpdatesGroupWhenChanged() throws IOException {
        File srcDir = createPropertyGroupSource("ChangedGroup",
                "{\"id\":\"pg-1\",\"name\":\"ChangedGroup\",\"displayName\":\"ChangedGroup\",\"description\":\"local\",\"type\":\"string\",\"properties\":{}}");

        VcfaPropertyGroup remote = new VcfaPropertyGroup();
        remote.setId("pg-remote");
        remote.setName("ChangedGroup");
        remote.setDisplayName("ChangedGroup");
        remote.setDescription("remote");
        remote.setType("string");
        remote.setProperties(Collections.emptyMap());

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(remote));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).updatePropertyGroup(eq("pg-remote"), argThat(group -> "local".equals(group.getDescription())));
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        File srcDir = createPropertyGroupSource("SameGroup",
                "{\"id\":\"pg-1\",\"name\":\"SameGroup\",\"displayName\":\"SameGroup\",\"description\":\"same\",\"type\":\"string\",\"properties\":{\"key\":\"value\"}}");

        VcfaPropertyGroup remote = new VcfaPropertyGroup();
        remote.setId("pg-remote");
        remote.setName("SameGroup");
        remote.setDisplayName("SameGroup");
        remote.setDescription("same");
        remote.setType("string");
        remote.setProperties(Collections.singletonMap("key", "value"));

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(remote));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient, never()).createPropertyGroup(any());
        verify(restClient, never()).updatePropertyGroup(anyString(), any());
    }

    @Test
    void testImportContent_ResolvesOrgNameToId() throws IOException {
        File srcDir = createPropertyGroupSource("OrgGroup",
                "{\"id\":\"pg-1\",\"name\":\"OrgGroup\",\"displayName\":\"OrgGroup\",\"organization\":\"My Org\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-default");
        when(restClient.getOrganizationId("My Org")).thenReturn("org-resolved");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).getOrganizationId("My Org");
        verify(restClient).createPropertyGroup(argThat(group -> "org-resolved".equals(group.getOrgId())));
    }

    @Test
    void testImportContent_UsesOrgIdDirectlyWhenContainsHyphen() throws IOException {
        File srcDir = createPropertyGroupSource("OrgIdGroup",
                "{\"id\":\"pg-1\",\"name\":\"OrgIdGroup\",\"displayName\":\"OrgIdGroup\",\"organization\":\"org-1\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-default");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient, never()).getOrganizationId(anyString());
        verify(restClient).createPropertyGroup(argThat(group -> "org-1".equals(group.getOrgId())));
    }

    @Test
    void testImportContent_FallsBackToDefaultOrgWhenNameResolutionFails() throws IOException {
        File srcDir = createPropertyGroupSource("FallbackGroup",
                "{\"id\":\"pg-1\",\"name\":\"FallbackGroup\",\"displayName\":\"FallbackGroup\",\"organization\":\"Unknown Org\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-default");
        when(restClient.getOrganizationId("Unknown Org")).thenReturn(null);
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).createPropertyGroup(argThat(group -> "org-default".equals(group.getOrgId())));
    }

    @Test
    void testImportContent_OrgIdResolutionFailureUsesNull() throws IOException {
        File srcDir = createPropertyGroupSource("NoOrgGroup",
                "{\"id\":\"pg-1\",\"name\":\"NoOrgGroup\",\"displayName\":\"NoOrgGroup\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenThrow(new RuntimeException("no org"));
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).createPropertyGroup(argThat(group -> group.getOrgId() == null));
    }

    @Test
    void testImportContent_ThrowsWhenProjectScopeChanges() throws IOException {
        File srcDir = createPropertyGroupSource("ScopedGroup",
                "{\"id\":\"pg-1\",\"name\":\"ScopedGroup\",\"displayName\":\"ScopedGroup\",\"type\":\"string\",\"projectId\":\"proj-current\",\"properties\":{}}");

        VcfaPropertyGroup remote = new VcfaPropertyGroup();
        remote.setId("pg-remote");
        remote.setName("ScopedGroup");
        remote.setDisplayName("ScopedGroup");
        remote.setType("string");
        remote.setProjectId("proj-other");
        remote.setProperties(Collections.emptyMap());

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(remote));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-current");

        assertThrows(UnsupportedOperationException.class, () -> store.importContent(srcDir));
    }

    @Test
    void testImportContent_RemapsProjectIdOnUpdate() throws IOException {
        File srcDir = createPropertyGroupSource("RemapGroup",
                "{\"id\":\"pg-1\",\"name\":\"RemapGroup\",\"displayName\":\"RemapGroup\",\"description\":\"local\",\"type\":\"string\",\"projectId\":\"old-proj\",\"properties\":{}}");

        VcfaPropertyGroup remote = new VcfaPropertyGroup();
        remote.setId("pg-remote");
        remote.setName("RemapGroup");
        remote.setDisplayName("RemapGroup");
        remote.setDescription("remote");
        remote.setType("string");
        remote.setProjectId("proj-current");
        remote.setProperties(Collections.emptyMap());

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(remote));
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-current");

        store.importContent(srcDir);

        verify(restClient).updatePropertyGroup(eq("pg-remote"), argThat(group -> "proj-current".equals(group.getProjectId())));
    }

    @Test
    void testImportContent_RemapsProjectIdOnCreate() throws IOException {
        File srcDir = createPropertyGroupSource("CreateRemapGroup",
                "{\"id\":\"pg-1\",\"name\":\"CreateRemapGroup\",\"displayName\":\"CreateRemapGroup\",\"type\":\"string\",\"projectId\":\"old-proj\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-current");

        store.importContent(srcDir);

        verify(restClient).createPropertyGroup(argThat(group -> "proj-current".equals(group.getProjectId())));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        writeContentYaml("property-group:\n  - OtherGroup\n");
        File srcDir = createPropertyGroupSource("ExcludedGroup",
                "{\"id\":\"pg-1\",\"name\":\"ExcludedGroup\",\"displayName\":\"ExcludedGroup\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient).getPropertyGroups();
        verify(restClient, never()).createPropertyGroup(any());
        verify(restClient, never()).updatePropertyGroup(anyString(), any());
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        writeContentYaml("property-group: []\n");
        File srcDir = createPropertyGroupSource("SkippedGroup",
                "{\"id\":\"pg-1\",\"name\":\"SkippedGroup\",\"displayName\":\"SkippedGroup\",\"type\":\"string\",\"properties\":{}}");

        store.importContent(srcDir);

        verify(restClient, never()).getPropertyGroups();
    }

    @Test
    void testImportContent_MissingPropertyGroupsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getPropertyGroups();
    }

    @Test
    void testImportContent_EmptyPropertyGroupsFolder() throws IOException {
        File srcDir = new File(tempDir, "src");
        new File(srcDir, "property-groups").mkdirs();

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).getPropertyGroups();
        verify(restClient, never()).createPropertyGroup(any());
    }

    @Test
    void testImportContent_DescriptorAliasPropertyGroups() throws IOException {
        writeContentYaml("propertyGroups:\n  - OtherGroup\n");
        File srcDir = createPropertyGroupSource("AliasGroup",
                "{\"id\":\"pg-1\",\"name\":\"AliasGroup\",\"displayName\":\"AliasGroup\",\"type\":\"string\",\"properties\":{}}");

        when(restClient.getPropertyGroups()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        setUserDir(tempDir, () -> store.importContent(srcDir));

        verify(restClient).getPropertyGroups();
        verify(restClient, never()).createPropertyGroup(any());
        verify(restClient, never()).updatePropertyGroup(anyString(), any());
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        VcfaPropertyGroup one = new VcfaPropertyGroup();
        one.setId("pg-1");
        one.setDisplayName("One");
        VcfaPropertyGroup two = new VcfaPropertyGroup();
        two.setId("pg-2");
        two.setDisplayName("Two");

        when(restClient.getPropertyGroups()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deletePropertyGroup("pg-1");
        verify(restClient).deletePropertyGroup("pg-2");
    }

    @Test
    void testDeleteContent_EmptyDescriptorSkips() throws IOException {
        writeContentYaml("property-group: []\n");
        VcfaPropertyGroup group = new VcfaPropertyGroup();
        group.setId("pg-1");
        group.setDisplayName("Only");

        when(restClient.getPropertyGroups()).thenReturn(Collections.singletonList(group));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient, never()).deletePropertyGroup(anyString());
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        writeContentYaml("property-group:\n  - Target\n");
        VcfaPropertyGroup target = new VcfaPropertyGroup();
        target.setId("pg-target");
        target.setDisplayName("Target");
        VcfaPropertyGroup other = new VcfaPropertyGroup();
        other.setId("pg-other");
        other.setDisplayName("Other");

        when(restClient.getPropertyGroups()).thenReturn(Arrays.asList(target, other));

        setUserDir(tempDir, () -> store.deleteContent());

        verify(restClient).deletePropertyGroup("pg-target");
        verify(restClient, never()).deletePropertyGroup("pg-other");
    }

    // ==================== Helpers ====================

    private File createPropertyGroupSource(String name, String json) throws IOException {
        File srcDir = new File(tempDir, "src");
        File groupsDir = new File(srcDir, "property-groups");
        groupsDir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(groupsDir, name + ".json"))) {
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
