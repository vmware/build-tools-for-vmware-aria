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
import java.util.List;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPolicy;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaPolicyStore import, export, and delete behavior.
 */
class VcfaPolicyStoreTest {

    @TempDir
    File tempDir;

    private VcfaPolicyStore store;
    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;

    @BeforeEach
    void setUp() {
        store = new VcfaPolicyStore();
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        store.init(restClient, pkg, config, descriptor);
    }

    // ==================== Import Tests ====================

    @Test
    void testImportContent_CreatesNewPolicy() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{\"level\":\"org\"}}");

        when(restClient.getPolicies()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");

        store.importContent(srcDir);

        verify(restClient).createOrUpdatePolicy(any(VcfaPolicy.class));
    }

    @Test
    void testImportContent_UpdatesPolicyWhenChanged() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{\"level\":\"org\"}}");

        VcfaPolicy existing = new VcfaPolicy();
        existing.setId("pol-existing");
        existing.setName("ApproveLease");
        existing.setTypeId("com.vmware.policy.approval");
        existing.setEnforcementType("HARD");
        existing.setDefinition(Collections.singletonMap("level", "project"));

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(existing));
        when(restClient.getOrganizationId()).thenReturn("org-1");

        store.importContent(srcDir);

        verify(restClient, times(2)).createOrUpdatePolicy(any(VcfaPolicy.class));
        verify(restClient).deletePolicy("pol-existing");
    }

    @Test
    void testImportContent_SkipsWhenIdentical() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{\"level\":\"org\"}}");

        VcfaPolicy existing = new VcfaPolicy();
        existing.setId("pol-existing");
        existing.setName("ApproveLease");
        existing.setTypeId("com.vmware.policy.approval");
        existing.setEnforcementType("HARD");
        existing.setDefinition(Collections.singletonMap("level", "org"));

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(existing));
        when(restClient.getOrganizationId()).thenReturn("org-1");

        store.importContent(srcDir);

        verify(restClient, never()).createOrUpdatePolicy(any());
        verify(restClient, never()).deletePolicy(anyString());
    }

    @Test
    void testImportContent_SetsOrgAndProjectId() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        File srcDir = createPolicySource("approval", "ProjectPolicy",
                "{\"id\":\"pol-2\",\"name\":\"ProjectPolicy\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"projectId\":\"old-proj\",\"definition\":{}}");

        when(restClient.getPolicies()).thenReturn(Collections.emptyList());
        when(restClient.getOrganizationId()).thenReturn("org-1");
        when(restClient.getProjectId()).thenReturn("proj-1");

        store.importContent(srcDir);

        verify(restClient).createOrUpdatePolicy(argThat(policy ->
                "org-1".equals(policy.getOrgId()) && "proj-1".equals(policy.getProjectId())));
    }

    @Test
    void testImportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getPolicy()).thenReturn(
                Collections.singletonMap("approval", Collections.singletonList("OtherPolicy")));
        // Satisfy strict enforcement by creating the declared policy file.
        createPolicySource("approval", "OtherPolicy",
                "{\"id\":\"pol-other\",\"name\":\"OtherPolicy\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{}}");
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{}}");

        VcfaPolicy remoteOther = new VcfaPolicy();
        remoteOther.setId("pol-other");
        remoteOther.setName("OtherPolicy");
        remoteOther.setTypeId("com.vmware.policy.approval");
        remoteOther.setEnforcementType("HARD");
        remoteOther.setDefinition(Collections.emptyMap());

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(remoteOther));
        when(restClient.getOrganizationId()).thenReturn("org-1");

        store.importContent(srcDir);

        verify(restClient, never()).createOrUpdatePolicy(argThat(policy -> "ApproveLease".equals(policy.getName())));
    }

    @Test
    void testImportContent_EmptyDescriptorSkipsAll() throws IOException {
        when(descriptor.getPolicy()).thenReturn(Collections.emptyMap());
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{}}");

        store.importContent(srcDir);

        verify(restClient, never()).getPolicies();
        verify(restClient, never()).createOrUpdatePolicy(any());
    }

    @Test
    void testImportContent_MissingPoliciesFolder() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        File srcDir = new File(tempDir, "src");
        srcDir.mkdirs();

        store.importContent(srcDir);

        verify(restClient, never()).getPolicies();
    }

    @Test
    void testImportContent_FailsWhenDeclaredPolicyMissing() throws IOException {
        when(descriptor.getPolicy()).thenReturn(
                Collections.singletonMap("approval", Collections.singletonList("MissingPolicy")));
        File srcDir = createPolicySource("approval", "ApproveLease",
                "{\"id\":\"pol-1\",\"name\":\"ApproveLease\",\"typeId\":\"com.vmware.policy.approval\",\"enforcementType\":\"HARD\",\"definition\":{}}");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> store.importContent(srcDir));

        assertTrue(ex.getCause() instanceof IOException);
        assertTrue(ex.getCause().getMessage().contains("MissingPolicy"));
    }

    // ==================== Export Tests ====================

    @Test
    void testExportContent_WritesPolicyToDisk() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        VcfaPolicy policy = new VcfaPolicy();
        policy.setId("pol-1");
        policy.setName("ApproveLease");
        policy.setTypeId("com.vmware.policy.approval");
        policy.setEnforcementType("HARD");
        policy.setOrgId("org-1");
        policy.setDefinition(Collections.singletonMap("level", "org"));

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(policy));

        store.exportContent();

        File policyFile = new File(new File(new File(pkg.getFilesystemPath(), "policies"), "approval"),
                "ApproveLease.json");
        assertTrue(policyFile.exists());
        String content = new String(Files.readAllBytes(policyFile.toPath()));
        assertTrue(content.contains("ApproveLease"));
        assertFalse(content.contains("org-1"));
    }

    @Test
    void testExportContent_ExcludesByDescriptor() throws IOException {
        when(descriptor.getPolicy()).thenReturn(
                Collections.singletonMap("approval", Collections.singletonList("OtherPolicy")));
        VcfaPolicy policy = new VcfaPolicy();
        policy.setId("pol-1");
        policy.setName("ApproveLease");
        policy.setTypeId("com.vmware.policy.approval");

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(policy));

        store.exportContent();

        File policyFile = new File(new File(new File(pkg.getFilesystemPath(), "policies"), "approval"),
                "ApproveLease.json");
        assertFalse(policyFile.exists());
    }

    @Test
    void testExportContent_ThrowsWhenBackupFound() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        VcfaPolicy policy = new VcfaPolicy();
        policy.setId("pol-1");
        policy.setName("ApproveLease_PL_TMP_BKUP");
        policy.setTypeId("com.vmware.policy.approval");

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(policy));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> store.exportContent());

        assertTrue(ex.getMessage().contains("Policy backups found"));
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteContent_WildcardDeletesAll() throws IOException {
        when(descriptor.getPolicy()).thenReturn(null);
        VcfaPolicy one = new VcfaPolicy();
        one.setId("pol-1");
        one.setName("One");
        one.setTypeId("com.vmware.policy.approval");
        VcfaPolicy two = new VcfaPolicy();
        two.setId("pol-2");
        two.setName("Two");
        two.setTypeId("com.vmware.policy.lease");

        when(restClient.getPolicies()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deletePolicy("pol-1");
        verify(restClient).deletePolicy("pol-2");
    }

    @Test
    void testDeleteContent_EmptyDescriptorDeletesAll() throws IOException {
        when(descriptor.getPolicy()).thenReturn(Collections.emptyMap());
        VcfaPolicy policy = new VcfaPolicy();
        policy.setId("pol-1");
        policy.setName("One");
        policy.setTypeId("com.vmware.policy.approval");

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(policy));

        store.deleteContent();

        verify(restClient).deletePolicy("pol-1");
    }

    @Test
    void testDeleteContent_TargetedDeletesMatching() throws IOException {
        when(descriptor.getPolicy()).thenReturn(
                Collections.singletonMap("approval", Collections.singletonList("Target")));
        VcfaPolicy target = new VcfaPolicy();
        target.setId("pol-target");
        target.setName("Target");
        target.setTypeId("com.vmware.policy.approval");
        VcfaPolicy other = new VcfaPolicy();
        other.setId("pol-other");
        other.setName("Other");
        other.setTypeId("com.vmware.policy.approval");

        when(restClient.getPolicies()).thenReturn(Arrays.asList(target, other));

        store.deleteContent();

        verify(restClient).deletePolicy("pol-target");
        verify(restClient, never()).deletePolicy("pol-other");
    }

    @Test
    void testDeleteContent_CategoryOmittedDeletesAllInCategory() throws IOException {
        when(descriptor.getPolicy()).thenReturn(
                Collections.singletonMap("approval", Collections.singletonList("Target")));
        VcfaPolicy lease = new VcfaPolicy();
        lease.setId("pol-lease");
        lease.setName("LeasePolicy");
        lease.setTypeId("com.vmware.policy.lease");

        when(restClient.getPolicies()).thenReturn(Collections.singletonList(lease));

        store.deleteContent();

        verify(restClient).deletePolicy("pol-lease");
    }

    @Test
    void testDeleteContent_CategoryWildcardDeletesAllInCategory() throws IOException {
        Map<String, List<String>> policyMap = new HashMap<>();
        policyMap.put("approval", null);
        when(descriptor.getPolicy()).thenReturn(policyMap);
        VcfaPolicy one = new VcfaPolicy();
        one.setId("pol-1");
        one.setName("One");
        one.setTypeId("com.vmware.policy.approval");
        VcfaPolicy two = new VcfaPolicy();
        two.setId("pol-2");
        two.setName("Two");
        two.setTypeId("com.vmware.policy.approval");

        when(restClient.getPolicies()).thenReturn(Arrays.asList(one, two));

        store.deleteContent();

        verify(restClient).deletePolicy("pol-1");
        verify(restClient).deletePolicy("pol-2");
    }

    // ==================== Helpers ====================

    private File createPolicySource(String category, String name, String json) throws IOException {
        File srcDir = new File(tempDir, "src");
        File policiesDir = new File(srcDir, "policies");
        File categoryDir = new File(policiesDir, category);
        File policyFile = new File(categoryDir, name + ".json");
        categoryDir.mkdirs();

        try (FileWriter writer = new FileWriter(policyFile)) {
            writer.write(json);
        }
        return srcDir;
    }
}
