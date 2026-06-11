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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaResourceActionStore extends AbstractVcfaStore {

    private static final String DIR_RESOURCE_ACTIONS = "resource-actions";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaResourceActionStore() {
        super();
    }

    /**
     * Exports all Resource Actions from the target environment to the filesystem
     * package.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling resource action configurations from the remote environment...");
        if (restClient == null) {
            logger.warn("RestClient not initialized in ResourceAction Store. Skipping export.");
            return;
        }

        try {
            List<VcfaResourceAction> remoteActions = restClient.getResourceActions();
            if (remoteActions == null || remoteActions.isEmpty()) {
                logger.info("No remote Resource Actions found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseActionsPath = Paths
                    .get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_RESOURCE_ACTIONS).toString();
            Files.createDirectories(Paths.get(baseActionsPath));

            for (VcfaResourceAction action : remoteActions) {
                String trackingName = action.getDisplayName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Resource Action '{}' is excluded by descriptor rules. Skipping export.", trackingName);
                    continue;
                }

                String safeFileName = trackingName.replaceAll("[^a-zA-Z0-9-_\\s\\.]", "_");
                File jsonFile = Paths.get(baseActionsPath, safeFileName + ".json").toFile();

                logger.info("Successfully synchronized resource action asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(action);
                Files.write(
                        jsonFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing resource action sync export stream", e);
        }
    }

    /**
     * Imports local Resource Action configuration files back up to the target
     * environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing resource actions from {}", sourceDirectory.getAbsolutePath());
        if (restClient == null) {
            logger.warn("RestClient not initialized in ResourceAction Store. Skipping import.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_RESOURCE_ACTIONS).toFile();
        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Resource actions directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaResourceAction> remoteActions = restClient.getResourceActions();
            File[] actionFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (actionFiles == null || actionFiles.length == 0) {
                logger.info("Could not find any resource action assets to import.");
                return;
            }

            for (File file : actionFiles) {
                VcfaResourceAction localAction = mapper.readValue(file, VcfaResourceAction.class);
                String trackingName = localAction.getDisplayName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info(
                            "Resource action asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            trackingName);
                    continue;
                }

                logger.info("Processing local Resource Action asset configuration: '{}'", file.getName());

                Optional<VcfaResourceAction> existingRemote = remoteActions.stream()
                        .filter(r -> r.getDisplayName().equalsIgnoreCase(localAction.getDisplayName()) ||
                                (r.getId() != null && r.getId().equalsIgnoreCase(localAction.getId())))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaResourceAction remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localAction)) {
                        logger.info(
                                "Resource Action '{}' matches remote system configuration exactly. Skipping update.",
                                trackingName);
                    } else {
                        // We already designed this to bypass the missing PUT method!
                        logger.info(
                                "Delta detected for Resource Action '{}'. Utilizing clean delete-and-recreate flow.",
                                trackingName);
                        restClient.deleteResourceAction(remoteMatch.getId());

                        logger.info("Re-creating updated Resource Action '{}' on target server.", trackingName);
                        preparePayloadForCreation(localAction);
                        restClient.createResourceAction(localAction);
                    }
                } else {
                    logger.info("Resource Action '{}' not found on target server. Executing remote creation.",
                            trackingName);
                    preparePayloadForCreation(localAction);
                    restClient.createResourceAction(localAction);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "CRITICAL: Failed to import Resource Actions from filesystem to target environment", e);
        }
    }

    /**
     * Deep configuration evaluation check.
     */
    private boolean isIdentical(VcfaResourceAction remote, VcfaResourceAction local) {
        if (remote == null || local == null) {
            return false;
        }

        boolean sameDisplayName = java.util.Objects.equals(remote.getDisplayName(), local.getDisplayName());
        boolean sameDescription = java.util.Objects.equals(remote.getDescription(), local.getDescription());
        boolean sameProviderName = java.util.Objects.equals(remote.getProviderName(), local.getProviderName());
        boolean sameResourceType = java.util.Objects.equals(remote.getResourceType(), local.getResourceType());
        boolean sameRunnableItem = java.util.Objects.equals(remote.getRunnableItem(), local.getRunnableItem());
        boolean sameFormDefinition = java.util.Objects.equals(remote.getFormDefinition(), local.getFormDefinition());
        boolean sameCriteria = java.util.Objects.equals(remote.getCriteria(), local.getCriteria());

        return sameDisplayName && sameDescription && sameProviderName && sameResourceType
                && sameRunnableItem && sameFormDefinition && sameCriteria;
    }

    /**
     * Wipes remote infrastructure resource actions based on Tristate rules.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Resource Action entities...");
        try {
            List<VcfaResourceAction> remoteActions = restClient.getResourceActions();
            if (remoteActions == null || remoteActions.isEmpty()) {
                logger.info("No remote resource actions identified to delete.");
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
                        Object actionListObj = rawMap.containsKey("resource-action") ? rawMap.get("resource-action")
                                : rawMap.get("resourceActions");

                        if (rawMap.containsKey("resource-action") || rawMap.containsKey("resourceActions")) {
                            if (actionListObj instanceof List) {
                                itemsToDelete = (List<String>) actionListObj;
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
                logger.info("Resource Action descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            if (itemsToDelete == null) {
                logger.info(
                        "Resource Action descriptor is undefined/null. Omitted wildcard trigger: Purging ALL remote resource actions.");
                for (VcfaResourceAction remoteAct : remoteActions) {
                    logger.info("[WILDCARD DELETE] Deleting resource action named '{}' matching ID: {}",
                            remoteAct.getDisplayName(), remoteAct.getId());
                    restClient.deleteResourceAction(remoteAct.getId());
                }
                return;
            }

            logger.info(
                    "Resource Action targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaResourceAction remoteAct : remoteActions) {
                String remoteDisplayName = remoteAct.getDisplayName();
                if (itemsToDelete.contains(remoteDisplayName)) {
                    logger.info("[TARGETED DELETE] Deleting resource action named '{}' matching ID: {}",
                            remoteDisplayName, remoteAct.getId());
                    restClient.deleteResourceAction(remoteAct.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure resource action definitions", e);
        }
    }

    /**
     * Manifest resolution check matching friendly display names.
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
                        logger.error("Failed parsing content.yaml within resource action descriptor check block", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        // Assumes your VcfaPackageDescriptor mapping method follows your pattern (e.g.,
        // getResourceAction)
        List<String> allowedActions = localDescriptor.getResourceAction();
        if (allowedActions == null) {
            return false;
        }

        if (allowedActions.isEmpty()) {
            return true;
        }

        return !allowedActions.contains(trackingName);
    }

    /**
     * Strips colliding form definition metadata IDs before hitting creation
     * endpoints.
     */
    private void preparePayloadForCreation(VcfaResourceAction action) {
        // Unlike Custom Resources, the custom action ID can remain intact if it carries
        // the composite name template
        if (action.getFormDefinition() != null) {
            Map<String, Object> formDefMap = action.getFormDefinition();
            if (formDefMap.containsKey("id")) {
                formDefMap.remove("id");
                logger.debug(
                        "Stripped nested formDefinition ID from resource action configuration layout to avoid collision.");
            }
        }
    }
}
