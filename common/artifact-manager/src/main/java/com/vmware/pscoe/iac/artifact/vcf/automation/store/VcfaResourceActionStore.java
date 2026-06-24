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
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaDescriptorHelper;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;

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

        List<String> allowedActions = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "resource-action",
                "resourceActions");
        if (allowedActions != null && allowedActions.isEmpty()) {
            logger.info(
                    "Resource Action descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
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
                this.verifyAssetPathSafety(displayName, "Policy Item");

                File actionFolder = Paths.get(baseActionsPath, displayName).toFile();
                if (!actionFolder.exists()) {
                    actionFolder.mkdirs();
                }

                Map<String, Object> actionMap = mapper.convertValue(action, new TypeReference<Map<String, Object>>() {
                });
                Map<String, Object> formDefinitionMap = null;
                String cssStyles = "";

                if (actionMap.containsKey("formDefinition") && actionMap.get("formDefinition") != null) {
                    formDefinitionMap = (Map<String, Object>) actionMap.get("formDefinition");
                    actionMap.remove("formDefinition");

                    if (formDefinitionMap.containsKey("styles") && formDefinitionMap.get("styles") != null) {
                        cssStyles = formDefinitionMap.get("styles").toString().trim();
                        formDefinitionMap.remove("styles");
                    }

                    if (formDefinitionMap.containsKey("form") && formDefinitionMap.get("form") != null) {
                        Object rawForm = formDefinitionMap.get("form");
                        if (rawForm instanceof String) {
                            Map<String, Object> unescapedFormObj = mapper.readValue((String) rawForm,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            formDefinitionMap.put("form", unescapedFormObj);
                        }
                    }
                }

                File detailsFile = Paths.get(actionFolder.getPath(), "details.json").toFile();
                String detailsJson = mapper.writeValueAsString(actionMap);
                Files.write(detailsFile.toPath(), detailsJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                if (formDefinitionMap != null) {
                    String formFileName = displayName + "__FormData.json";
                    File formFile = Paths.get(actionFolder.getPath(), formFileName).toFile();
                    String formJson = mapper.writeValueAsString(formDefinitionMap);
                    Files.write(formFile.toPath(), formJson.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }

                File stylesFile = Paths.get(actionFolder.getPath(), "styles.css").toFile();
                Files.write(stylesFile.toPath(), cssStyles.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

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

        List<String> allowedActions = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "resource-action",
                "resourceActions");
        if (allowedActions != null && allowedActions.isEmpty()) {
            logger.info(
                    "Resource Action descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_RESOURCE_ACTIONS).toFile();

        // --- NEW STRATEGIC COUPLING: Call the unified asset validator (Folders only)
        // ---
        VcfaDescriptorHelper.validateAssetsPresent(this.vcfaPackage, localDir, "Resource Action", true,
                "resource-action", "resourceActions");

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
                File stylesFile = new File(folder, "styles.css");

                if (!detailsFile.exists() || !formDataFile.exists()) {
                    throw new IOException(String.format(
                            "CRITICAL WORKSPACE ERROR: Resource Action '%s' requires both 'details.json' and '%s__FormData.json' inside its workspace folder structure.",
                            displayName, displayName));
                }

                logger.info("Processing local Resource Action folder layout: '{}'", displayName);

                Map<String, Object> actionMap = mapper.readValue(detailsFile, new TypeReference<Map<String, Object>>() {
                });
                Map<String, Object> formDefMap = mapper.readValue(formDataFile,
                        new TypeReference<Map<String, Object>>() {
                        });

                if (formDefMap.containsKey("form") && formDefMap.get("form") instanceof Map) {
                    Map<String, Object> innerFormMap = (Map<String, Object>) formDefMap.get("form");
                    if (innerFormMap.containsKey("layout") || innerFormMap.containsKey("schema")) {
                        logger.info(
                                "Identified and unwrapped nested 'form' sub-block pattern inside resource action payload schema.");
                        formDefMap = innerFormMap;
                    }
                }

                if (formDefMap.containsKey("form") && formDefMap.get("form") != null) {
                    Object formObj = formDefMap.get("form");
                    if (!(formObj instanceof String)) {
                        String stringifiedForm = mapper.writeValueAsString(formObj);
                        formDefMap.put("form", stringifiedForm);
                    }
                }

                String localStyles = "";
                if (stylesFile.exists()) {
                    localStyles = new String(Files.readAllBytes(stylesFile.toPath()), StandardCharsets.UTF_8).trim();
                }
                formDefMap.put("styles", localStyles);

                actionMap.put("formDefinition", formDefMap);

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

    private boolean isIdentical(VcfaResourceAction remote, VcfaResourceAction local) {
        if (remote == null || local == null) {
            return false;
        }

        return Objects.equals(remote.getDisplayName(), local.getDisplayName())
                && Objects.equals(remote.getDescription(), local.getDescription())
                && Objects.equals(remote.getProviderName(), local.getProviderName())
                && Objects.equals(remote.getResourceType(), local.getResourceType())
                && Objects.equals(remote.getRunnableItem(), local.getRunnableItem())
                && Objects.equals(remote.getFormDefinition(), local.getFormDefinition())
                && Objects.equals(remote.getCriteria(), local.getCriteria());
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

            List<String> itemsToDelete = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "resource-action",
                    "resourceActions");

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

            if (itemsToDelete.isEmpty()) {
                logger.info("Resource Action descriptor is explicitly empty '[]'. Skipping deletion entirely.");
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
        List<String> allowedActions = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "resource-action",
                "resourceActions");
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