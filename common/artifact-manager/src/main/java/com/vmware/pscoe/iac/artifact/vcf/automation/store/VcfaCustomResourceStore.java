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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaCustomResourceStore extends AbstractVcfaStore {
    private static final String DIR_CUSTOM_RESOURCES = "custom-resources";
    private static final String SUBDIR_ADDITIONAL_ACTIONS = "additionalActions";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaCustomResourceStore() {
        super();
    }

    /**
     * Exports all Custom Resources from the target environment to the filesystem
     * package using split layouts.
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
                String trackingName = resource.getDisplayName();
                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Custom Resource '{}' is excluded by descriptor rules. Skipping export.", trackingName);
                    continue;
                }

                // Create individual subfolder based on the resource display name
                File resourceFolder = Paths.get(baseResourcesPath, trackingName).toFile();
                if (!resourceFolder.exists()) {
                    resourceFolder.mkdirs();
                }

                // Map conversion for manual payload field manipulation
                Map<String, Object> resourceMap = mapper.convertValue(resource,
                        new TypeReference<Map<String, Object>>() {
                        });
                List<Map<String, Object>> extractedActions = null;

                if (resourceMap.containsKey(SUBDIR_ADDITIONAL_ACTIONS)
                        && resourceMap.get(SUBDIR_ADDITIONAL_ACTIONS) != null) {
                    extractedActions = (List<Map<String, Object>>) resourceMap.get(SUBDIR_ADDITIONAL_ACTIONS);
                    resourceMap.remove(SUBDIR_ADDITIONAL_ACTIONS); // Clean out additionalActions from root file
                }

                // File 1: Write root details.json
                File detailsFile = Paths.get(resourceFolder.getPath(), "details.json").toFile();
                String detailsJson = mapper.writeValueAsString(resourceMap);
                Files.write(detailsFile.toPath(), detailsJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Export Day-2 additional actions if they are present
                if (extractedActions != null && !extractedActions.isEmpty()) {
                    File actionsSubFolder = Paths.get(resourceFolder.getPath(), SUBDIR_ADDITIONAL_ACTIONS).toFile();
                    if (!actionsSubFolder.exists()) {
                        actionsSubFolder.mkdirs();
                    }

                    for (Map<String, Object> action : extractedActions) {
                        String actionDisplayName = (String) action.get("displayName");
                        File singleActionFolder = Paths.get(actionsSubFolder.getPath(), actionDisplayName).toFile();
                        if (!singleActionFolder.exists()) {
                            singleActionFolder.mkdirs();
                        }

                        Map<String, Object> formDefinitionMap = null;
                        if (action.containsKey("formDefinition") && action.get("formDefinition") != null) {
                            formDefinitionMap = (Map<String, Object>) action.get("formDefinition");
                            action.remove("formDefinition"); // Separate from action metadata

                            if (formDefinitionMap.containsKey("form") && formDefinitionMap.get("form") != null) {
                                Object rawForm = formDefinitionMap.get("form");
                                if (rawForm instanceof String) {
                                    // Parse stringified layout back into raw map structures
                                    Map<String, Object> parsedForm = mapper.readValue((String) rawForm,
                                            new TypeReference<Map<String, Object>>() {
                                            });
                                    formDefinitionMap.put("form", parsedForm);
                                }
                            }
                        }

                        // Write additional action metadata file
                        File actionDetailsFile = Paths.get(singleActionFolder.getPath(), "details.json").toFile();
                        Files.write(actionDetailsFile.toPath(),
                                mapper.writeValueAsString(action).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        // Write clean decoupled form layout file
                        if (formDefinitionMap != null) {
                            String formFileName = actionDisplayName + "__FormData.json";
                            File actionFormFile = Paths.get(singleActionFolder.getPath(), formFileName).toFile();
                            Files.write(actionFormFile.toPath(),
                                    mapper.writeValueAsString(formDefinitionMap).getBytes(StandardCharsets.UTF_8),
                                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        }
                    }
                }

                logger.info("Successfully synchronized custom resource folder layout asset: {}",
                        resourceFolder.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing custom resource sync export stream", e);
        }
    }

    /**
     * Imports local Custom Resource folder structures back up to the target
     * environment.
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
            File[] resourceFolders = localDir.listFiles(File::isDirectory);
            if (resourceFolders == null || resourceFolders.length == 0) {
                logger.info("Could not find any custom resource folder mappings to import.");
                return;
            }

            for (File folder : resourceFolders) {
                String trackingName = folder.getName();
                if (isExcludedByDescriptor(trackingName)) {
                    logger.info(
                            "Custom resource asset folder '{}' is excluded by descriptor configuration rules. Skipping import.",
                            trackingName);
                    continue;
                }

                File detailsFile = new File(folder, "details.json");
                if (!detailsFile.exists()) {
                    logger.warn("Skipping directory {}. Root details.json could not be resolved.", folder.getPath());
                    continue;
                }

                logger.info("Processing local Custom Resource asset layout definition: '{}'", trackingName);
                Map<String, Object> resourceMap = mapper.readValue(detailsFile,
                        new TypeReference<Map<String, Object>>() {
                        });
                List<Map<String, Object>> combinedActionsList = new ArrayList<>();

                // Evaluate and recombine nested additionalActions files if present
                File actionsSubFolder = Paths.get(folder.getPath(), SUBDIR_ADDITIONAL_ACTIONS).toFile();
                if (actionsSubFolder.exists() && actionsSubFolder.isDirectory()) {
                    File[] singleActionFolders = actionsSubFolder.listFiles(File::isDirectory);
                    if (singleActionFolders != null) {
                        for (File actionFolder : singleActionFolders) {
                            String actionName = actionFolder.getName();
                            File actionDetailsFile = new File(actionFolder, "details.json");
                            File actionFormFile = new File(actionFolder, actionName + "__FormData.json");

                            if (!actionDetailsFile.exists() || !actionFormFile.exists()) {
                                throw new IOException(String.format(
                                        "CRITICAL WORKSPACE ERROR: Additional Action '%s' inside Custom Resource '%s' requires both 'details.json' and '%s__FormData.json' layout definitions.",
                                        actionName, trackingName, actionName));
                            }

                            Map<String, Object> actionMap = mapper.readValue(actionDetailsFile,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            Map<String, Object> formDefMap = mapper.readValue(actionFormFile,
                                    new TypeReference<Map<String, Object>>() {
                                    });

                            // Re-stringify the raw layout form properties back for transmission
                            // compatibility
                            if (formDefMap.containsKey("form") && formDefMap.get("form") != null) {
                                Object formObj = formDefMap.get("form");
                                if (!(formObj instanceof String)) {
                                    formDefMap.put("form", mapper.writeValueAsString(formObj));
                                }
                            }

                            actionMap.put("formDefinition", formDefMap);
                            combinedActionsList.add(actionMap);
                        }
                    }
                }

                // Bind array list back to master properties payload map
                resourceMap.put(SUBDIR_ADDITIONAL_ACTIONS, combinedActionsList);
                VcfaCustomResourceType localResource = mapper.convertValue(resourceMap, VcfaCustomResourceType.class);

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
                        logger.info(
                                "Delta detected for Custom Resource '{}'. Utilizing clean delete-and-recreate lifecycle flow.",
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
     * Helper to clean system metadata IDs from the payload to prevent collisions.
     */
    private void preparePayloadForCreation(VcfaCustomResourceType resource) {
        resource.setId(null);
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
     * Deep evaluation tracking properties schemas, actions maps, and key
     * structures.
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
     * Wipes remote infrastructure custom resource components based on Tristate
     * rules.
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

            if (isExplicitlyEmpty) {
                logger.info("Custom Resource descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            if (itemsToDelete == null) {
                logger.info(
                        "Custom Resource descriptor is undefined/null. Omitted wildcard trigger: Purging ALL remote custom resources.");
                for (VcfaCustomResourceType remoteRes : remoteResources) {
                    logger.info("[WILDCARD DELETE] Deleting custom resource named '{}' matching ID: {}",
                            remoteRes.getDisplayName(), remoteRes.getId());
                    restClient.deleteCustomResourceType(remoteRes.getId());
                }
                return;
            }

            logger.info(
                    "Custom Resource targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaCustomResourceType remoteRes : remoteResources) {
                String remoteDisplayName = remoteRes.getDisplayName();
                if (itemsToDelete.contains(remoteDisplayName)) {
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