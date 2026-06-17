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
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaCustomResourceStore extends AbstractVcfaStore {
    private static final String DIR_CUSTOM_RESOURCES = "custom-resources";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaCustomResourceStore() {
        super();
    }

    /**
     * 
     * Exports all Custom Resources from the target environment to the filesystem
     * 
     * package.
     * 
     */
    @Override
    public void exportContent() {
        logger.info("Pulling custom resource configurations from the remote environment...");
        if (restClient == null) {
            logger.warn("RestClient not initialized in CustomResource Store. Skipping export.");
            return;
        }
        try {
            List<VcfaCustomResourceType> remoteResources = restClient.getCustomResources();
            if (remoteResources == null || remoteResources.isEmpty()) {
                logger.info("No remote Custom Resources found to export.");
                return;
            }
            Package serverPackage = this.vcfaPackage;
            String baseResourcesPath = Paths
                    .get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_CUSTOM_RESOURCES).toString();
            Files.createDirectories(Paths.get(baseResourcesPath));
            for (VcfaCustomResourceType resource : remoteResources) {
                String trackingName = resource.getDisplayName(); // e.g., "Load Balancer"
                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Custom Resource '{}' is excluded by descriptor rules. Skipping export.", trackingName);
                    continue;
                }
                // CHANGED: Sanitize the cosmetic Display Name for the filename instead of the
                // backend type
                String safeFileName = trackingName.replaceAll("[^a-zA-Z0-9-_\\s\\.]", "_");
                File jsonFile = Paths.get(baseResourcesPath, safeFileName + ".json").toFile();
                logger.info("Successfully synchronized custom resource asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resource);
                Files.write(
                        jsonFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing custom resource sync export stream", e);
        }
    }

    /**
     * 
     * Imports local Custom Resource configuration files back up to the target
     * 
     * environment.
     * 
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing custom resources from {}", sourceDirectory.getAbsolutePath());
        if (restClient == null) {
            logger.warn("RestClient not initialized in CustomResource Store. Skipping import.");
            return;
        }
        File localDir = Paths.get(sourceDirectory.getPath(), DIR_CUSTOM_RESOURCES).toFile();
        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Custom resources directory not found. Skipping.");
            return;
        }
        try {
            List<VcfaCustomResourceType> remoteResources = restClient.getCustomResources();
            File[] resourceFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (resourceFiles == null || resourceFiles.length == 0) {
                logger.info("Could not find any custom resource assets to import.");
                return;
            }
            for (File file : resourceFiles) {
                VcfaCustomResourceType localResource = mapper.readValue(file, VcfaCustomResourceType.class);
                String trackingName = localResource.getDisplayName();
                if (isExcludedByDescriptor(trackingName)) {
                    logger.info(
                            "Custom resource asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            trackingName);
                    continue;
                }
                logger.info("Processing local Custom Resource asset configuration: '{}'", file.getName());
                Optional<VcfaCustomResourceType> existingRemote = remoteResources.stream()
                        .filter(r -> r.getDisplayName().equalsIgnoreCase(localResource.getDisplayName()) ||
                                (r.getName() != null && r.getName().equalsIgnoreCase(localResource.getName())) ||
                                (r.getId() != null && r.getId().equals(localResource.getId())))
                        .findFirst();
                if (existingRemote.isPresent()) {
                    VcfaCustomResourceType remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localResource)) {
                        logger.info(
                                "Custom Resource '{}' matches remote system configuration exactly. Skipping update.",
                                trackingName);
                    } else {
                        // FIXing the 405: Delete existing object first, then clear fields and recreate
                        // it freshly
                        logger.info(
                                "Delta detected for Custom Resource '{}'. Platform restrictions require delete-and-recreate lifecycle. Initiating removal.",
                                trackingName);
                        restClient.deleteCustomResourceType(remoteMatch.getId());
                        logger.info("Re-creating updated Custom Resource '{}' on target server.", trackingName);
                        preparePayloadForCreation(localResource);
                        restClient.createCustomResourceType(localResource);
                    }
                } else {
                    logger.info("Custom Resource '{}' not found on target server. Executing remote creation.",
                            trackingName);
                    preparePayloadForCreation(localResource);
                    restClient.createCustomResourceType(localResource);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "CRITICAL: Failed to import Custom Resources from filesystem to target environment", e);
        }
    }

    /**
     * 
     * Helper to clean system metadata IDs from the payload to prevent 400 Bad
     * 
     * Request collisions.
     * 
     */
    private void preparePayloadForCreation(VcfaCustomResourceType resource) {
        // Clear root level database tracking ID
        resource.setId(null);
        // Loop through any nested additional actions and remove embedded form
        // definition IDs
        if (resource.getAdditionalActions() != null) {
            for (Map<String, Object> action : resource.getAdditionalActions()) {
                if (action.containsKey("formDefinition")) {
                    Object formDefObj = action.get("formDefinition");
                    if (formDefObj instanceof Map) {
                        Map<String, Object> formDefMap = (Map<String, Object>) formDefObj;
                        if (formDefMap.containsKey("id")) {
                            formDefMap.remove("id");
                            logger.debug(
                                    "Stripped nested formDefinition ID from additional action layout to prevent collision.");
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * Deep evaluation tracking properties schemas, actions maps, and key
     * 
     * structures.
     * 
     */
    private boolean isIdentical(VcfaCustomResourceType remote, VcfaCustomResourceType local) {
        if (remote == null || local == null) {
            return false;
        }
        boolean sameDisplayName = java.util.Objects.equals(remote.getDisplayName(), local.getDisplayName());
        boolean sameDescription = java.util.Objects.equals(remote.getDescription(), local.getDescription());
        boolean sameExternalType = java.util.Objects.equals(remote.getExternalType(), local.getExternalType());
        boolean sameSchemaType = java.util.Objects.equals(remote.getSchemaType(), local.getSchemaType());
        boolean sameMainActions = java.util.Objects.equals(remote.getMainActions(), local.getMainActions());
        boolean sameAdditionalActions = java.util.Objects.equals(remote.getAdditionalActions(),
                local.getAdditionalActions());
        boolean sameProperties = java.util.Objects.equals(remote.getProperties(), local.getProperties());
        return sameDisplayName && sameDescription && sameExternalType && sameSchemaType
                && sameMainActions && sameAdditionalActions && sameProperties;
    }

    /**
     * 
     * Wipes remote infrastructure custom resource components based on Tristate
     * 
     * rules.
     * 
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Custom Resource entities...");
        try {
            List<VcfaCustomResourceType> remoteResources = restClient.getCustomResources();
            if (remoteResources == null || remoteResources.isEmpty()) {
                logger.info("No remote custom resources identified to delete.");
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
                        Object resourceListObj = rawMap.containsKey("custom-resource") ? rawMap.get("custom-resource")
                                : rawMap.get("customResources");
                        if (rawMap.containsKey("custom-resource") || rawMap.containsKey("customResources")) {
                            if (resourceListObj instanceof List) {
                                itemsToDelete = (List<String>) resourceListObj;
                                if (itemsToDelete.isEmpty()) {
                                    isExplicitlyEmpty = true;
                                }
                            }
                        }
                    }
                }
            }
            // --- TRISTATE EVALUATION MATRIX ---
            // Scenario 1: Explicitly Empty "[]" -> Safety bypass toggle. Delete Nothing.
            if (isExplicitlyEmpty) {
                logger.info("Custom Resource descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }
            // Scenario 2: Key is completely null/omitted -> Wildcard target mode active.
            // Purge Everything.
            if (itemsToDelete == null) {
                logger.info(
                        "Custom Resource descriptor is undefined/null. Omitted wildcard trigger: Purging ALL remote custom resources.");
                for (VcfaCustomResourceType remoteRes : remoteResources) {
                    // CHANGED: Log the friendly displayName instead of the backend type name
                    logger.info("[WILDCARD DELETE] Deleting custom resource named '{}' matching ID: {}",
                            remoteRes.getDisplayName(), remoteRes.getId());
                    restClient.deleteCustomResourceType(remoteRes.getId());
                }
                return;
            }
            // Scenario 3: Explicit List -> Filter targeted matches sequentially by Display
            // Name
            logger.info(
                    "Custom Resource targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaCustomResourceType remoteRes : remoteResources) {
                String remoteDisplayName = remoteRes.getDisplayName();
                if (itemsToDelete.contains(remoteDisplayName)) {
                    // CHANGED: Log the friendly displayName here as well
                    logger.info("[TARGETED DELETE] Deleting custom resource named '{}' matching ID: {}",
                            remoteDisplayName, remoteRes.getId());
                    restClient.deleteCustomResourceType(remoteRes.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure custom resource definitions", e);
        }
    }

    /**
     * 
     * Resolves manifest tracking parameters for filtering by Display Name.
     * 
     */
    private boolean isExcludedByDescriptor(String trackingName) {
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
                        logger.error("Failed parsing content.yaml within custom resource descriptor check block", e);
                    }
                }
            }
        }
        if (localDescriptor == null) {
            return false;
        }
        List<String> allowedResources = localDescriptor.getCustomResource();
        if (allowedResources == null) {
            return false;
        }
        if (allowedResources.isEmpty()) {
            return true;
        }
        return !allowedResources.contains(trackingName);
    }
}
