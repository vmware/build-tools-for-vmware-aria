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
     * package using the new folder pattern.
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
                String displayName = action.getDisplayName();

                if (isExcludedByDescriptor(displayName)) {
                    logger.info("Resource Action '{}' is excluded by descriptor rules. Skipping export.", displayName);
                    continue;
                }

                // Create a subfolder for this specific resource action based on its display
                // name
                File actionFolder = Paths.get(baseActionsPath, displayName).toFile();
                if (!actionFolder.exists()) {
                    actionFolder.mkdirs();
                }

                // Convert VcfaResourceAction to a fully editable Map for clean file writing
                Map<String, Object> actionMap = mapper.convertValue(action, new TypeReference<Map<String, Object>>() {
                });
                Map<String, Object> formDefinitionMap = null;

                // Extract and un-stringify form data if it exists
                if (actionMap.containsKey("formDefinition") && actionMap.get("formDefinition") != null) {
                    formDefinitionMap = (Map<String, Object>) actionMap.get("formDefinition");
                    actionMap.remove("formDefinition"); // Strip from metadata file

                    if (formDefinitionMap.containsKey("form") && formDefinitionMap.get("form") != null) {
                        Object rawForm = formDefinitionMap.get("form");
                        if (rawForm instanceof String) {
                            // Parse stringified form back into an object tree for local disk readability
                            Map<String, Object> unescapedFormObj = mapper.readValue((String) rawForm,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            formDefinitionMap.put("form", unescapedFormObj);
                        }
                    }
                }

                // File 1: details.json (Metadata only)
                File detailsFile = Paths.get(actionFolder.getPath(), "details.json").toFile();
                String detailsJson = mapper.writeValueAsString(actionMap);
                Files.write(detailsFile.toPath(), detailsJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // File 2: <DisplayName>__FormData.json
                if (formDefinitionMap != null) {
                    String formFileName = displayName + "__FormData.json";
                    File formFile = Paths.get(actionFolder.getPath(), formFileName).toFile();
                    String formJson = mapper.writeValueAsString(formDefinitionMap);
                    Files.write(formFile.toPath(), formJson.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }

                logger.info("Successfully exported resource action folder asset: {}", actionFolder.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing resource action sync export stream", e);
        }
    }

    /**
     * Imports local Resource Action folder configurations back up to the target
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
            File[] actionFolders = localDir.listFiles(File::isDirectory);
            if (actionFolders == null || actionFolders.length == 0) {
                logger.info("Could not find any resource action folders to import.");
                return;
            }

            for (File folder : actionFolders) {
                String displayName = folder.getName();

                if (isExcludedByDescriptor(displayName)) {
                    logger.info(
                            "Resource action asset folder '{}' is excluded by descriptor configuration rules. Skipping import.",
                            displayName);
                    continue;
                }

                File detailsFile = new File(folder, "details.json");
                File formDataFile = new File(folder, displayName + "__FormData.json");

                // Enforce mandatory file coexistence check
                if (!detailsFile.exists() || !formDataFile.exists()) {
                    throw new IOException(String.format(
                            "CRITICAL WORKSPACE ERROR: Resource Action '%s' requires both 'details.json' and '%s__FormData.json' inside its workspace folder structure.",
                            displayName, displayName));
                }

                logger.info("Processing local Resource Action folder layout: '{}'", displayName);

                // Read and recombine files
                Map<String, Object> actionMap = mapper.readValue(detailsFile, new TypeReference<Map<String, Object>>() {
                });
                Map<String, Object> formDefMap = mapper.readValue(formDataFile,
                        new TypeReference<Map<String, Object>>() {
                        });

                // Re-stringify the form block layout precisely for API transmission compliance
                if (formDefMap.containsKey("form") && formDefMap.get("form") != null) {
                    Object formObj = formDefMap.get("form");
                    if (!(formObj instanceof String)) {
                        String stringifiedForm = mapper.writeValueAsString(formObj);
                        formDefMap.put("form", stringifiedForm);
                    }
                }

                // Inject combined formDefinition back into payload map
                actionMap.put("formDefinition", formDefMap);

                // Re-serialize fully into object template model instance
                VcfaResourceAction localAction = mapper.convertValue(actionMap, VcfaResourceAction.class);

                Optional<VcfaResourceAction> existingRemote = remoteActions.stream()
                        .filter(r -> r.getDisplayName().equalsIgnoreCase(localAction.getDisplayName()) ||
                                (r.getId() != null && r.getId().equalsIgnoreCase(localAction.getId())))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaResourceAction remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localAction)) {
                        logger.info(
                                "Resource Action '{}' matches remote system configuration exactly. Skipping update.",
                                displayName);
                    } else {
                        logger.info(
                                "Delta detected for Resource Action '{}'. Utilizing clean delete-and-recreate flow.",
                                displayName);
                        restClient.deleteResourceAction(remoteMatch.getId());

                        logger.info("Re-creating updated Resource Action '{}' on target server.", displayName);
                        preparePayloadForCreation(localAction);
                        restClient.createResourceAction(localAction);
                    }
                } else {
                    logger.info("Resource Action '{}' not found on target server. Executing remote creation.",
                            displayName);
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

        List<String> allowedActions = localDescriptor.getResourceAction();
        if (allowedActions == null) {
            return false;
        }

        if (allowedActions.isEmpty()) {
            return true;
        }

        return !allowedActions.contains(trackingName);
    }

    private void preparePayloadForCreation(VcfaResourceAction action) {
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