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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.Package;

public class VcfaPropertyGroupStore extends AbstractVcfaStore {

    private static final String DIR_PROPERTY_GROUPS = "property-groups";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaPropertyGroupStore() {
        super();
    }

    /**
     * Exports all Property Groups from the remote target system to the local
     * package filesystem workspace using the cosmetic Display Name attribute
     * (preserving spaces).
     */
    @Override
    public void exportContent() {
        logger.info("Pulling property group configurations from the remote environment...");

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Property Group descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in PropertyGroup Store. Skipping export.");
            return;
        }

        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            if (remoteGroups == null || remoteGroups.isEmpty()) {
                logger.info("No remote Property Groups found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseGroupsPath = Paths
                    .get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_PROPERTY_GROUPS)
                    .toString();

            Files.createDirectories(Paths.get(baseGroupsPath));

            for (VcfaPropertyGroup group : remoteGroups) {
                String trackingName = group.getDisplayName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Property Group '{}' is excluded by descriptor configuration rules. Skipping export.",
                            trackingName);
                    continue;
                }

                // CHANGED: Strip illegal filesystem characters, but explicitly preserve spaces
                String safeName = trackingName.replaceAll("[^a-zA-Z0-9-\\.\\s]", "");
                File jsonFile = Paths.get(baseGroupsPath, safeName + ".json").toFile();

                // --- REPRODUCED SYSTEM LOGIC: Sanitize environmental/auditing fields to allow
                // multi-tenant migration ---
                ObjectNode jsonNode = mapper.valueToTree(group);
                jsonNode.remove("id");
                jsonNode.remove("projectName");
                jsonNode.remove("createdAt");
                jsonNode.remove("createdBy");
                jsonNode.remove("updatedAt");
                jsonNode.remove("updatedBy");

                logger.info("Successfully synchronized property group asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writeValueAsString(jsonNode);
                Files.write(
                        jsonFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing property group sync export stream", e);
        }
    }

    /**
     * Imports and synchronizes local Property Group JSON configurations back up to
     * the remote instance environment, mapped via friendly display names.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing property groups from {}", sourceDirectory.getAbsolutePath());

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Property Group descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in PropertyGroup Store. Skipping import.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_PROPERTY_GROUPS).toFile();
        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Property groups directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            File[] groupFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (groupFiles == null || groupFiles.length == 0) {
                logger.info("Could not find any property group assets to import.");
                return;
            }

            String currentOrgId = restClient.getOrganizationId();
            String currentProjectId = restClient.getProjectId();

            for (File file : groupFiles) {
                // Friendly display identifiers retain spaces flawlessly here
                String groupDisplayName = file.getName().replace(".json", "");

                if (isExcludedByDescriptor(groupDisplayName)) {
                    logger.info(
                            "Property group asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            groupDisplayName);
                    continue;
                }

                logger.info("Processing local Property Group asset configuration: '{}'", file.getName());
                VcfaPropertyGroup localGroup = mapper.readValue(file, VcfaPropertyGroup.class);

                // Track down matching records on the target environment by friendly Display
                // Name attributes
                Optional<VcfaPropertyGroup> existingRemote = remoteGroups.stream()
                        .filter(r -> r.getDisplayName().equalsIgnoreCase(localGroup.getDisplayName()))
                        .findFirst();

                // Re-stitch Organization scopes
                localGroup.setOrgId(currentOrgId);

                if (existingRemote.isPresent()) {
                    VcfaPropertyGroup remoteMatch = existingRemote.get();

                    // --- REPRODUCED SYSTEM LOGIC: Prevent illegal project/scope mutations ---
                    String existingProjectId = remoteMatch.getProjectId();
                    if (existingProjectId != null && !existingProjectId.isBlank()
                            && !existingProjectId.equals(currentProjectId)) {
                        throw new UnsupportedOperationException(String.format(
                                "The property group '%s' is already assigned to a different project scope (%s). Scope change operations are not supported by the platform API.",
                                localGroup.getDisplayName(), existingProjectId));
                    }

                    // If local asset expects a non-blank project allocation, inject target
                    // environment project mapping parameters
                    if (localGroup.getProjectId() != null && !localGroup.getProjectId().isBlank()) {
                        logger.debug("Re-mapping local group project alignment pointer to current target scope: {}",
                                currentProjectId);
                        localGroup.setProjectId(currentProjectId);
                    }

                    if (isIdentical(remoteMatch, localGroup)) {
                        logger.info("Property Group '{}' matches remote system configuration exactly. Skipping update.",
                                localGroup.getDisplayName());
                    } else {
                        logger.info("Delta detected for Property Group '{}'. Initiating remote update context.",
                                localGroup.getDisplayName());
                        localGroup.setId(remoteMatch.getId());
                        restClient.updatePropertyGroup(remoteMatch.getId(), localGroup);
                    }
                } else {
                    // CREATE PIPELINE
                    if (localGroup.getProjectId() != null && !localGroup.getProjectId().isBlank()) {
                        logger.debug("Re-mapping local group project alignment pointer to current target scope: {}",
                                currentProjectId);
                        localGroup.setProjectId(currentProjectId);
                    }
                    logger.info("Property Group '{}' not found on target server. Executing remote creation.",
                            localGroup.getDisplayName());
                    restClient.createPropertyGroup(localGroup);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "CRITICAL: Failed to import Property Groups from filesystem to target environment", e);
        }
    }

    /**
     * Property evaluation matching field paths verified inside model layout.
     */
    private boolean isIdentical(VcfaPropertyGroup remote, VcfaPropertyGroup local) {
        if (remote == null || local == null) {
            return false;
        }

        boolean sameName = java.util.Objects.equals(remote.getName(), local.getName());
        boolean sameDisplayName = java.util.Objects.equals(remote.getDisplayName(), local.getDisplayName());
        boolean sameDescription = java.util.Objects.equals(remote.getDescription(), local.getDescription());
        boolean sameType = java.util.Objects.equals(remote.getType(), local.getType());
        boolean sameProperties = java.util.Objects.equals(remote.getProperties(), local.getProperties());

        return sameName && sameDisplayName && sameDescription && sameType && sameProperties;
    }

    /**
     * Executes cleanup deletion scanning for registered property groups targeting
     * Display Names.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Property Group entities...");
        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            if (remoteGroups == null || remoteGroups.isEmpty()) {
                logger.info("No remote property groups identified to delete.");
                return;
            }

            List<String> itemsToDelete = null;
            boolean isExplicitlyEmpty = false;

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
                    if (rawMap != null) {
                        Object groupListObj = rawMap.containsKey("property-group") ? rawMap.get("property-group")
                                : rawMap.get("propertyGroups");

                        if (rawMap.containsKey("property-group") || rawMap.containsKey("propertyGroups")) {
                            if (groupListObj instanceof List) {
                                itemsToDelete = (List<String>) groupListObj;
                                if (itemsToDelete.isEmpty()) {
                                    isExplicitlyEmpty = true;
                                }
                            }
                        }
                    }
                }
            }

            // --- TRISTATE EVALUATION MATRIX ---

            if (isExplicitlyEmpty) {
                logger.info("Property Group descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            if (itemsToDelete == null) {
                logger.info(
                        "Property Group descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote groups.");
                for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                    logger.info("[WILDCARD DELETE] Deleting property group named '{}' matching ID: {}",
                            remoteGroup.getDisplayName(), remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
                return;
            }

            logger.info("Targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                String remoteDisplayName = remoteGroup.getDisplayName();
                if (itemsToDelete.contains(remoteDisplayName)) {
                    logger.info("[TARGETED DELETE] Deleting property group named '{}' matching ID: {}",
                            remoteDisplayName, remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure property group definitions", e);
        }
    }

    /**
     * Helper to safely extract and determine if the configuration block array is
     * explicitly initialized to '[]'.
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

        List<String> allowedGroups = localDescriptor.getPropertyGroup();
        return allowedGroups != null && allowedGroups.isEmpty();
    }

    /**
     * Evaluates property group filtering rules straight against friendly display
     * name lists inside content.yaml.
     */
    private boolean isExcludedByDescriptor(String groupDisplayName) {
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
                        logger.error("Failed parsing content.yaml within property group descriptor check block", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        List<String> allowedGroups = localDescriptor.getPropertyGroup();

        if (allowedGroups == null) {
            return false;
        }

        if (allowedGroups.isEmpty()) {
            return true;
        }

        return !allowedGroups.contains(groupDisplayName);
    }
}
