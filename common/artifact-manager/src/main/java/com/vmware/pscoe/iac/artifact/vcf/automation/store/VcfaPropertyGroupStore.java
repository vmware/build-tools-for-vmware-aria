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
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.Package;

public class VcfaPropertyGroupStore extends AbstractVcfaStore {

    private static final String DIR_PROPERTY_GROUPS = "property-groups";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // Default or Empty Constructor requested by VcfaTypeStoreFactory
    public VcfaPropertyGroupStore() {
        super();
    }

    /**
     * Exports all Property Groups from the remote target system to the local
     * package filesystem workspace.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling property group configurations from the remote environment...");
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
            String baseGroupsPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_PROPERTY_GROUPS)
                    .toString();

            Files.createDirectories(Paths.get(baseGroupsPath));

            for (VcfaPropertyGroup group : remoteGroups) {
                String name = group.getName();

                // Check descriptor rules before saving to disk
                if (isExcludedByDescriptor(name)) {
                    logger.info("Property Group '{}' is excluded by descriptor configuration rules. Skipping export.", name);
                    continue;
                }

                String safeName = name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
                File jsonFile = Paths.get(baseGroupsPath, safeName + ".json").toFile();

                logger.info("Successfully synchronized property group asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(group);
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
     * the remote instance environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing property groups from {}", sourceDirectory.getAbsolutePath());
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

            for (File file : groupFiles) {
                String groupName = file.getName().replace(".json", "");

                if (isExcludedByDescriptor(groupName)) {
                    logger.info("Property group asset '{}' is excluded by descriptor configuration rules. Skipping import.", groupName);
                    continue;
                }

                logger.info("Processing local Property Group asset configuration: '{}'", file.getName());
                VcfaPropertyGroup localGroup = mapper.readValue(file, VcfaPropertyGroup.class);

                Optional<VcfaPropertyGroup> existingRemote = remoteGroups.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(localGroup.getName()) ||
                                (r.getId() != null && r.getId().equals(localGroup.getId())))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaPropertyGroup remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localGroup)) {
                        logger.info("Property Group '{}' matches remote system configuration exactly. Skipping update.", localGroup.getName());
                    } else {
                        logger.info("Delta detected for Property Group '{}'. Initiating remote update context.", localGroup.getName());
                        restClient.updatePropertyGroup(remoteMatch.getId(), localGroup);
                    }
                } else {
                    logger.info("Property Group '{}' not found on target server. Executing remote creation.", localGroup.getName());
                    restClient.createPropertyGroup(localGroup);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to import Property Groups from filesystem to target environment", e);
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

        // Deep properties configuration map verification check
        boolean sameProperties = java.util.Objects.equals(remote.getProperties(), local.getProperties());

        return sameName && sameDisplayName && sameDescription && sameType && sameProperties;
    }

    /**
     * Executes cleanup deletion scanning for registered property groups.
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

            // Locate and parse content.yaml manually to guarantee absolute control over null vs [] empty arrays
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
                        // Check for both structural permutations of the key name
                        Object groupListObj = rawMap.containsKey("property-group") ? rawMap.get("property-group") : rawMap.get("propertyGroups");
                        
                        if (rawMap.containsKey("property-group") || rawMap.containsKey("propertyGroups")) {
                            if (groupListObj instanceof List) {
                                itemsToDelete = (List<String>) groupListObj;
                                if (itemsToDelete.isEmpty()) {
                                    isExplicitlyEmpty = true;
                                }
                            }
                            // Note: If the key exists but groupListObj is null, itemsToDelete remains null (Delete All)
                        }
                    }
                }
            }

            // --- TRISTATE EVALUATION MATRIX ---
            
            // Scenario 1: Explicitly Empty "[]" -> Target absolute safety bypass. Delete Nothing.
            if (isExplicitlyEmpty) {
                logger.info("Property Group descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            // Scenario 2: Key is completely null/omitted -> Wildcard target mode active. Delete Everything.
            if (itemsToDelete == null) {
                logger.info("Property Group descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote groups.");
                for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                    logger.info("[WILDCARD DELETE] Deleting property group named '{}' matching ID: {}", remoteGroup.getName(), remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
                return;
            }

            // Scenario 3: Explicit List -> Filter targeted matches sequentially.
            logger.info("Targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                String remoteName = remoteGroup.getName();
                if (itemsToDelete.contains(remoteName)) {
                    logger.info("[TARGETED DELETE] Deleting property group named '{}' matching ID: {}", remoteName, remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure property group definitions", e);
        }
    }

    /**
     * Evaluates property group filtering rules straight against the content.yaml file.
     */
    private boolean isExcludedByDescriptor(String groupName) {
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

        // --- Aligned to VcfaPackageDescriptor property-group mapping field getter ---
        List<String> allowedGroups = localDescriptor.getPropertyGroup();

        if (allowedGroups == null) {
            return false;
        }

        if (allowedGroups.isEmpty()) {
            return true;
        }

        return !allowedGroups.contains(groupName);
    }
}
