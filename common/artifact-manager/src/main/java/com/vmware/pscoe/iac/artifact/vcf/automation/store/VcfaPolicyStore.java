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
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
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

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info("Policy descriptor is explicitly empty in configuration. Bypassing server lookups and skipping export entirely.");
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

            // --- REPRODUCED SYSTEM LOGIC: Check for existing unrecovered environment backups ---
            List<String> activeBackups = new ArrayList<>();
            for (VcfaPolicy policy : remotePolicies) {
                if (policy.getName() != null && policy.getName().contains(POLICY_BACKUP_SUFFIX)) {
                    activeBackups.add(policy.getName());
                }
            }
            if (!activeBackups.isEmpty()) {
                throw new RuntimeException("Policy backups found on server indicate that either an update is in progress or a previous update failure needs manual resolution: " + String.join(", ", activeBackups));
            }

            Package serverPackage = this.vcfaPackage;
            String basePoliciesPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_POLICIES)
                    .toString();

            for (VcfaPolicy policy : remotePolicies) {
                if (isExcludedByDescriptor(policy)) {
                    logger.info("Policy '{}' is excluded by descriptor rules. Skipping export.", policy.getName());
                    continue;
                }

                // Determine the correct sub-folder name based on your content.yaml properties
                String manifestCategory = translateTypeIdToManifestKey(policy.getTypeId());

                // Construct the targeted category path (e.g., target/policies/approval/)
                String categoryFolderPath = Paths.get(basePoliciesPath, manifestCategory).toString();
                Files.createDirectories(Paths.get(categoryFolderPath));

                String trackingName = policy.getName();
                String safeFileName = trackingName.replaceAll("[^a-zA-Z0-9-_\\s\\.]", "_");
                File jsonFile = Paths.get(categoryFolderPath, safeFileName + ".json").toFile();

                // Convert model back to clean JSON node layout to scrub system properties
                ObjectNode jsonNode = mapper.valueToTree(policy);
                sanitizePolicyPayload(jsonNode);

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

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info("Policy descriptor is explicitly empty in configuration. Bypassing server lookups and skipping import entirely.");
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

            // Look for files across all category sub-folders
            File[] categoryDirs = localDir.listFiles(File::isDirectory);
            if (categoryDirs == null || categoryDirs.length == 0) {
                logger.info("No policy category sub-directories found to import.");
                return;
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

                    // --- REPRODUCED SYSTEM LOGIC: Populate multi-tenant boundaries ---
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
                            logger.info("Delta detected for Policy '{}'. Executing atomic transaction update lifecycle.",
                                    trackingName);
                            executeAtomicPolicyUpdate(remoteMatch, localPolicy);
                        }
                    } else {
                        logger.info("Policy '{}' not found on target server. Executing remote creation via POST.",
                                trackingName);
                        localPolicy.setId(null);
                        restClient.createPolicy(localPolicy);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to import Policies from filesystem to target environment", e);
        }
    }

    /**
     * Executes the legacy safe policy change engine to prevent data loss or drift.
     */
    private void executeAtomicPolicyUpdate(VcfaPolicy remoteMatch, VcfaPolicy localPolicy) {
        String originalName = remoteMatch.getName();
        String backupName = originalName + POLICY_BACKUP_SUFFIX;
        
        logger.debug("Backing up existing Policy state with ID={} to tracking name '{}'", remoteMatch.getId(), backupName);
        try {
            remoteMatch.setName(backupName);
            restClient.updatePolicy(remoteMatch.getId(), remoteMatch);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Transaction Interrupted: Failed creating staging rollback checkpoint for policy '%s'", originalName), e);
        }

        try {
            logger.info("Pushing refreshed configuration structure for policy asset: '{}'", originalName);
            localPolicy.setId(null); // Clear reference to create a fresh record node
            restClient.createPolicy(localPolicy);
            
            // Clean up old checkpoint payload upon transaction success
            try {
                logger.debug("Purging transient asset backup reference ID: {}", remoteMatch.getId());
                restClient.deletePolicy(remoteMatch.getId());
            } catch (Exception e) {
                logger.warn("Cleanup Warning: Failed purging active backup resource placeholder: '{}'", backupName, e);
            }
        } catch (Exception e) {
            logger.error("Creation failed for update record. Attempting atomic cluster state rollback restoration...", e);
            try {
                remoteMatch.setName(originalName);
                restClient.updatePolicy(remoteMatch.getId(), remoteMatch);
                logger.info("Rollback complete. Original environment configuration state successfully restored.");
            } catch (Exception rollbackException) {
                logger.error("CRITICAL: Rollback failed. Asset cluster definition is out of sync. Please resolve from UI at placeholder: '{}'", backupName, rollbackException);
            }
            throw new RuntimeException("Policy deployment failed: " + e.getMessage(), e);
        }
    }

    private void sanitizePolicyPayload(ObjectNode policyJson) {
        policyJson.remove("orgId");
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
        boolean sameName = java.util.Objects.equals(remote.getName(), local.getName());
        boolean sameTypeId = java.util.Objects.equals(remote.getTypeId(), local.getTypeId());
        boolean sameEnforcement = java.util.Objects.equals(remote.getEnforcementType(), local.getEnforcementType());
        boolean sameDefinition = java.util.Objects.equals(remote.getDefinition(), local.getDefinition());

        return sameName && sameTypeId && sameEnforcement && sameDefinition;
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

            Map<String, Object> policyYamlBlock = null;
            boolean isGlobalOmitted = true;

            File contentYamlFile = new File(System.getProperty("user.dir"), "content.yaml");
            if (!contentYamlFile.exists() && this.vcfaPackage != null) {
                File packageDir = new File(this.vcfaPackage.getFilesystemPath());
                File projectRoot = packageDir.getParentFile() != null ? packageDir.getParentFile() : packageDir;
                contentYamlFile = new File(projectRoot, "content.yaml");
            }

            if (contentYamlFile.exists()) {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                try (java.io.InputStream inputStream = new java.io.FileInputStream(contentYamlFile)) {
                    Map<String, Object> rawMap = yaml.load(inputStream);
                    if (rawMap != null && (rawMap.containsKey("policy") || rawMap.containsKey("policies"))) {
                        Object policyObj = rawMap.containsKey("policy") ? rawMap.get("policy") : rawMap.get("policies");
                        if (policyObj instanceof Map) {
                            policyYamlBlock = (Map<String, Object>) policyObj;
                            isGlobalOmitted = false;
                        }
                    }
                }
            }

            // Scenario 1: Root policy key completely missing -> Wildcard Purge All
            if (isGlobalOmitted || policyYamlBlock == null) {
                logger.info("Policy descriptor is completely omitted. Wildcard trigger: Purging ALL remote policies.");
                for (VcfaPolicy remotePol : remotePolicies) {
                    logger.info("[WILDCARD DELETE] Deleting policy '{}' matching ID: {}", remotePol.getName(),
                            remotePol.getId());
                    restClient.deletePolicy(remotePol.getId());
                }
                return;
            }

            // Scenario 2 & 3: Process entries based on category listings
            for (VcfaPolicy remotePol : remotePolicies) {
                String manifestKey = translateTypeIdToManifestKey(remotePol.getTypeId());

                // If a category type is omitted from the map entirely, treat it as a wildcard
                // for that category
                if (!policyYamlBlock.containsKey(manifestKey)) {
                    logger.info("[CATEGORY OMITTED] Policy category '{}' omitted from manifest. Purging policy: {}",
                            manifestKey, remotePol.getName());
                    restClient.deletePolicy(remotePol.getId());
                    continue;
                }

                Object categoryListObj = policyYamlBlock.get(manifestKey);

                // --- TUNING LOGIC FOR BLANK PROPERTIES ---
                // If the category key is present but blank (null), it's a category wildcard!
                // Delete it.
                if (categoryListObj == null) {
                    logger.info(
                            "[CATEGORY WILDCARD] Policy category '{}' is blank. Deleting remote infrastructure instance: {}",
                            manifestKey, remotePol.getName());
                    restClient.deletePolicy(remotePol.getId());
                    continue;
                }

                if (categoryListObj instanceof List) {
                    List<String> targetedNames = (List<String>) categoryListObj;

                    // If explicitly empty '[]', user wants to keep everything on server. Skip
                    // deletion!
                    if (targetedNames.isEmpty()) {
                        continue;
                    }

                    // If target list matches, execute deletion sequence
                    if (targetedNames.contains(remotePol.getName())) {
                        logger.info("[TARGETED DELETE] Deleting policy '{}' found in manifest category '{}'",
                                remotePol.getName(), manifestKey);
                        restClient.deletePolicy(remotePol.getId());
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure policy definitions",
                    e);
        }
    }

    /**
     * Helper to safely extract and determine if the configuration block array is explicitly initialized to '[]' or empty map loops.
     */
    private boolean isExplicitlyEmptyInDescriptor() {
        VcfaPackageDescriptor localDescriptor = null;

        if (this.descriptor instanceof VcfaPackageDescriptor) {
            localDescriptor = (VcfaPackageDescriptor) this.descriptor;
        }

        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = VcfaPackageDescriptor.getInstance(contentYamlFile);
                    } catch (Exception e) {
                        logger.error("Failed parsing manifest layout rules map token details.", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        Map<String, List<String>> allowedPoliciesMap = localDescriptor.getPolicy();
        if (allowedPoliciesMap == null) {
            return false;
        }

        if (allowedPoliciesMap.isEmpty()) {
            return true;
        }

        // Verify if all declared internal configuration mappings are structurally marked explicitly blank/empty lists
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

        String trackingName = policy.getName();
        VcfaPackageDescriptor localDescriptor = null;

        if (this.descriptor instanceof VcfaPackageDescriptor) {
            localDescriptor = (VcfaPackageDescriptor) this.descriptor;
        }

        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = VcfaPackageDescriptor.getInstance(contentYamlFile);
                    } catch (Exception e) {
                        logger.error("Failed parsing content.yaml within policy descriptor check block", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        Map<String, List<String>> allowedPoliciesMap = localDescriptor.getPolicy();
        if (allowedPoliciesMap == null) {
            return false;
        }

        if (allowedPoliciesMap.isEmpty()) {
            return true;
        }

        // Translate the backend policy typeId to your friendly yaml key (e.g. "approval")
        String manifestKey = translateTypeIdToManifestKey(policy.getTypeId());

        // If the category key isn't even declared in the yaml, exclude its contents
        if (!allowedPoliciesMap.containsKey(manifestKey)) {
            return true;
        }

        List<String> policyNamesList = allowedPoliciesMap.get(manifestKey);

        // --- NEW TUNING LOGIC ---
        // If the key exists but is null/blank (e.g., "approval: "), it's an open wildcard. Do not exclude!
        if (policyNamesList == null) {
            return false;
        }

        // If explicitly initialized as an empty array "approval: []", it means allow nothing. Exclude!
        if (policyNamesList.isEmpty()) {
            return true;
        }

        // If it's a populated array, check if it contains our specific policy name
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
            case "com.vmware.policy.deployment.action":
            case "com.vmware.policy.day2":
                return "day2-actions";
            case "com.vmware.policy.lease":
                return "lease";
            case "com.vmware.policy.resource-quota":
            case "com.vmware.policy.quota":
            case "com.vmware.policy.supervisor.iaas":
                return "iaas-resource";
            default:
                // Fallback to lowercase stripped name if an unexpected type comes up
                return typeId.substring(typeId.lastIndexOf('.') + 1).toLowerCase();
        }
    }
}
