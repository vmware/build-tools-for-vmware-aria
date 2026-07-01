package com.vmware.pscoe.iac.artifact.vcf.automation.store;

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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPolicy;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaPolicyStore extends AbstractVcfaStore {

    private static final String DIR_POLICIES = "policies";
    private static final String POLICY_BACKUP_SUFFIX = "_PL_TMP_BKUP";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaPolicyStore() {
        super();
    }

    /**
     * Exports policies from the target environment to the filesystem package.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling policy configurations from the remote environment...");

        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Policy descriptor is explicitly empty in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in Policy Store. Skipping export.");
            return;
        }

        try {
            List<VcfaPolicy> remotePolicies = restClient.getPolicies();
            if (remotePolicies == null || remotePolicies.isEmpty()) {
                logger.info("No remote Policies found to export.");
                return;
            }

            List<String> activeBackups = new ArrayList<>();
            for (VcfaPolicy policy : remotePolicies) {
                if (policy.getName() != null && policy.getName().contains(POLICY_BACKUP_SUFFIX)) {
                    activeBackups.add(policy.getName());
                }
            }
            if (!activeBackups.isEmpty()) {
                throw new RuntimeException(
                        "Policy backups found on server indicate that either an update is in progress or a previous update failure needs manual resolution: "
                                + String.join(", ", activeBackups));
            }

            Package serverPackage = this.vcfaPackage;
            String basePoliciesPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_POLICIES)
                    .toString();

            for (VcfaPolicy policy : remotePolicies) {
                if (isExcludedByDescriptor(policy)) {
                    logger.info("Policy '{}' is excluded by descriptor rules. Skipping export.", policy.getName());
                    continue;
                }

                String manifestCategory = translateTypeIdToManifestKey(policy.getTypeId());
                String categoryFolderPath = Paths.get(basePoliciesPath, manifestCategory).toString();
                Files.createDirectories(Paths.get(categoryFolderPath));

                String trackingName = policy.getName();
                this.verifyAssetPathSafety(trackingName, "Policy Item");
                File jsonFile = Paths.get(categoryFolderPath, trackingName + ".json").toFile();

                ObjectNode jsonNode = mapper.valueToTree(policy);
                VcfaPayloadSanitizer.sanitize(jsonNode);

                logger.info("Successfully synchronized policy asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writeValueAsString(jsonNode);
                Files.write(
                        jsonFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing policy sync export stream", e);
        }
    }

    /**
     * Imports policy files back to the target environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing policies from {}", sourceDirectory.getAbsolutePath());

        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Policy descriptor is explicitly empty in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in Policy Store. Skipping import.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_POLICIES).toFile();
        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Policies directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaPolicy> remotePolicies = restClient.getPolicies();
            File[] categoryDirs = localDir.listFiles(File::isDirectory);
            if (categoryDirs == null || categoryDirs.length == 0) {
                logger.info("No policy category sub-directories found to import.");
                return;
            }

            // --- STRICT ENFORCEMENT FIX: Verify every single explicitly declared policy
            // exists on disk ---
            if (this.descriptor instanceof VcfaPackageDescriptor) {
                VcfaPackageDescriptor localDescriptor = (VcfaPackageDescriptor) this.descriptor;
                Map<String, List<String>> declaredPoliciesMap = localDescriptor.getPolicy();
                if (declaredPoliciesMap != null) {
                    for (Map.Entry<String, List<String>> entry : declaredPoliciesMap.entrySet()) {
                        String category = entry.getKey();
                        List<String> policyNames = entry.getValue();

                        // Skip if the array is omitted/blank wildcard or explicitly empty '[]'
                        if (policyNames == null || policyNames.isEmpty()) {
                            continue;
                        }

                        File categoryDir = new File(localDir, category);
                        for (String expectedName : policyNames) {
                            File expectedFile = new File(categoryDir, expectedName + ".json");
                            if (!expectedFile.exists()) {
                                throw new IOException(String.format(
                                        "CRITICAL WORKSPACE ERROR: Policy asset '%s.json' declared in manifest is missing from workspace category folder '%s/'.",
                                        expectedName, category));
                            }
                        }
                    }
                }
            }

            for (File subDir : categoryDirs) {
                File[] policyFiles = subDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (policyFiles == null || policyFiles.length == 0) {
                    continue;
                }

                for (File file : policyFiles) {
                    String jsonContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    VcfaPolicy localPolicy = mapper.readValue(jsonContent, VcfaPolicy.class);

                    if (isExcludedByDescriptor(localPolicy)) {
                        logger.info("Policy asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                                localPolicy.getName());
                        continue;
                    }

                    String trackingName = localPolicy.getName();
                    logger.info("Processing local Policy asset configuration: '{}/{}'", subDir.getName(),
                            file.getName());

                    localPolicy.setOrgId(restClient.getOrganizationId());
                    if (localPolicy.getProjectId() != null && !localPolicy.getProjectId().isBlank()) {
                        localPolicy.setProjectId(restClient.getProjectId());
                    }

                    Optional<VcfaPolicy> existingRemote = remotePolicies.stream()
                            .filter(r -> r.getName().equalsIgnoreCase(localPolicy.getName()) ||
                                    (r.getId() != null && r.getId().equalsIgnoreCase(localPolicy.getId())))
                            .findFirst();

                    if (existingRemote.isPresent()) {
                        VcfaPolicy remoteMatch = existingRemote.get();
                        if (isIdentical(remoteMatch, localPolicy)) {
                            logger.info("Policy '{}' matches remote system configuration exactly. Skipping update.",
                                    trackingName);
                        } else {
                            logger.info(
                                    "Delta detected for Policy '{}'. Executing atomic transaction update lifecycle.",
                                    trackingName);
                            executeAtomicPolicyUpdate(remoteMatch, localPolicy);
                        }
                    } else {
                        logger.info("Policy '{}' not found on target server. Executing remote creation via POST.",
                                trackingName);
                        localPolicy.setId(null);
                        restClient.createOrUpdatePolicy(localPolicy);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to import Policies from filesystem to target environment", e);
        }
    }

    private void executeAtomicPolicyUpdate(VcfaPolicy remoteMatch, VcfaPolicy localPolicy) {
        String originalName = remoteMatch.getName();
        String backupName = originalName + POLICY_BACKUP_SUFFIX;

        logger.debug("Backing up existing Policy state with ID={} to tracking name '{}'", remoteMatch.getId(),
                backupName);
        try {
            remoteMatch.setName(backupName);
            restClient.createOrUpdatePolicy(remoteMatch);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Transaction Interrupted: Failed creating staging rollback checkpoint for policy '%s'",
                    originalName), e);
        }

        try {
            logger.info("Pushing refreshed configuration structure for policy asset: '{}'", originalName);
            localPolicy.setId(null);
            restClient.createOrUpdatePolicy(localPolicy);

            try {
                logger.debug("Purging transient asset backup reference ID: {}", remoteMatch.getId());
                restClient.deletePolicy(remoteMatch.getId());
            } catch (Exception e) {
                logger.warn("Cleanup Warning: Failed purging active backup resource placeholder: '{}'", backupName, e);
            }
        } catch (Exception e) {
            logger.error("Creation failed for update record. Attempting atomic cluster state rollback restoration...",
                    e);
            try {
                remoteMatch.setName(originalName);
                restClient.createOrUpdatePolicy(remoteMatch);
                logger.info("Rollback complete. Original environment configuration state successfully restored.");
            } catch (Exception rollbackException) {
                logger.error(
                        "CRITICAL: Rollback failed. Asset cluster definition is out of sync. Please resolve from UI at placeholder: '{}'",
                        backupName, rollbackException);
            }
            throw new RuntimeException("Policy deployment failed: " + e.getMessage(), e);
        }
    }

    private void sanitizePolicyPayload(ObjectNode policyJson) {
        // orgId is handled by VcfaPayloadSanitizer.sanitize() above
        policyJson.remove("id");
        policyJson.remove("createdBy");
        policyJson.remove("createdAt");
        policyJson.remove("lastUpdatedBy");
        policyJson.remove("lastUpdatedAt");
    }

    private boolean isIdentical(VcfaPolicy remote, VcfaPolicy local) {
        if (remote == null || local == null) {
            return false;
        }
        return Objects.equals(remote.getName(), local.getName())
                && Objects.equals(remote.getTypeId(), local.getTypeId())
                && Objects.equals(remote.getCriteria(), local.getCriteria())
                && Objects.equals(remote.getScopeCriteria(), local.getScopeCriteria())
                && Objects.equals(remote.getDescription(), local.getDescription())
                && Objects.equals(remote.getEnforcementType(), local.getEnforcementType())
                && Objects.equals(remote.getDefinition(), local.getDefinition());
    }

    /**
     * Executes Tristate scanning logic for policy cleanup tasks.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Policy entities...");
        try {
            List<VcfaPolicy> remotePolicies = restClient.getPolicies();
            if (remotePolicies == null || remotePolicies.isEmpty()) {
                logger.info("No remote policies identified to delete.");
                return;
            }

            Map<String, List<String>> policyYamlBlock = null;
            if (this.descriptor instanceof VcfaPackageDescriptor) {
                policyYamlBlock = ((VcfaPackageDescriptor) this.descriptor).getPolicy();
            }

            if (policyYamlBlock == null) {
                logger.info("Policy descriptor is completely omitted. Wildcard trigger: Purging ALL remote policies.");
                for (VcfaPolicy remotePol : remotePolicies) {
                    logger.info("[WILDCARD DELETE] Deleting policy '{}' matching ID: {}", remotePol.getName(),
                            remotePol.getId());
                    restClient.deletePolicy(remotePol.getId());
                }
                return;
            }

            for (VcfaPolicy remotePol : remotePolicies) {
                String manifestKey = translateTypeIdToManifestKey(remotePol.getTypeId());

                if (!policyYamlBlock.containsKey(manifestKey)) {
                    logger.info("[CATEGORY OMITTED] Policy category '{}' omitted from manifest. Purging policy: {}",
                            manifestKey, remotePol.getName());
                    restClient.deletePolicy(remotePol.getId());
                    continue;
                }

                List<String> targetedNames = policyYamlBlock.get(manifestKey);

                if (targetedNames == null) {
                    logger.info(
                            "[CATEGORY WILDCARD] Policy category '{}' is blank. Deleting remote infrastructure instance: {}",
                            manifestKey, remotePol.getName());
                    restClient.deletePolicy(remotePol.getId());
                    continue;
                }

                if (targetedNames.isEmpty()) {
                    continue;
                }

                if (targetedNames.contains(remotePol.getName())) {
                    logger.info("[TARGETED DELETE] Deleting policy '{}' found in manifest category '{}'",
                            remotePol.getName(), manifestKey);
                    restClient.deletePolicy(remotePol.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure policy definitions",
                    e);
        }
    }

    private boolean isExplicitlyEmptyInDescriptor() {
        if (!(this.descriptor instanceof VcfaPackageDescriptor)) {
            return false;
        }
        Map<String, List<String>> allowedPoliciesMap = ((VcfaPackageDescriptor) this.descriptor).getPolicy();
        if (allowedPoliciesMap == null) {
            return false;
        }

        if (allowedPoliciesMap.isEmpty()) {
            return true;
        }

        for (List<String> list : allowedPoliciesMap.values()) {
            if (list == null || !list.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean isExcludedByDescriptor(VcfaPolicy policy) {
        if (policy == null) {
            return true;
        }

        if (!(this.descriptor instanceof VcfaPackageDescriptor)) {
            return false;
        }

        String trackingName = policy.getName();
        Map<String, List<String>> allowedPoliciesMap = ((VcfaPackageDescriptor) this.descriptor).getPolicy();
        if (allowedPoliciesMap == null) {
            return false;
        }

        if (allowedPoliciesMap.isEmpty()) {
            return true;
        }

        String manifestKey = translateTypeIdToManifestKey(policy.getTypeId());

        if (!allowedPoliciesMap.containsKey(manifestKey)) {
            return true;
        }

        List<String> policyNamesList = allowedPoliciesMap.get(manifestKey);

        if (policyNamesList == null) {
            return false;
        }

        if (policyNamesList.isEmpty()) {
            return true;
        }

        return !policyNamesList.contains(trackingName);
    }

    private String translateTypeIdToManifestKey(String typeId) {
        if (typeId == null) {
            return "";
        }
        switch (typeId) {
            case "com.vmware.policy.approval":
                return "approval";
            case "com.vmware.policy.day2-actions":
            case "com.vmware.policy.day2":
            case "com.vmware.policy.deployment.action":
                return "day2-actions";
            case "com.vmware.policy.lease":
                return "lease";
            case "com.vmware.policy.resource-quota":
            case "com.vmware.policy.quota":
            case "com.vmware.policy.supervisor.iaas":
                return "iaas-resource";
            default:
                return typeId.substring(typeId.lastIndexOf('.') + 1).toLowerCase();
        }
    }
}
