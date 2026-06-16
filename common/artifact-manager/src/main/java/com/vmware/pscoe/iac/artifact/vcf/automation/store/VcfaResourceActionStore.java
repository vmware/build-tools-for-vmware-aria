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
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaResourceActionStore extends AbstractVcfaStore {

    private static final String DIR_RESOURCE_ACTIONS = "resource-actions";
    private static final String RESOURCE_ACTION_SEPARATOR = "__";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaResourceActionStore() {
        super();
    }

    /**
     * Exports all Resource Actions from the target environment to the filesystem package.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling resource action configurations from the remote environment...");

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info("Resource Action descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

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
            String baseActionsPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_RESOURCE_ACTIONS).toString();
            Files.createDirectories(Paths.get(baseActionsPath));

            for (VcfaResourceAction action : remoteActions) {
                // --- REPRODUCED SYSTEM LOGIC: Construct compound name format to prevent name collisions ---
                String complexTrackingName = action.getResourceType() + RESOURCE_ACTION_SEPARATOR + action.getName();

                if (isExcludedByDescriptor(complexTrackingName)) {
                    logger.info("Resource Action '{}' is excluded by descriptor rules. Skipping export.", complexTrackingName);
                    continue;
                }

                String safeFileName = complexTrackingName.replaceAll("[^a-zA-Z0-9-_\\s\\.]", "_");
                File jsonFile = Paths.get(baseActionsPath, safeFileName + ".json").toFile();

                // Sanitize platform indicators before writing to storage
                ObjectNode jsonNode = mapper.valueToTree(action);
                jsonNode.remove("orgId");
                if (jsonNode.has("formDefinition") && jsonNode.get("formDefinition").isObject()) {
                    ((ObjectNode) jsonNode.get("formDefinition")).remove("id");
                }

                logger.info("Successfully synchronized resource action asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writeValueAsString(jsonNode);
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
     * Imports local Resource Action configuration files back up to the target environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing resource actions from {}", sourceDirectory.getAbsolutePath());

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info("Resource Action descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

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

            String targetVroLink = restClient.getVroTargetIntegrationEndpointLink();
            String currentProjectId = restClient.getProjectId();

            for (File file : actionFiles) {
                String jsonContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                ObjectNode actionJsonElement = (ObjectNode) mapper.readTree(jsonContent);

                // --- REPRODUCED SYSTEM LOGIC: Environment normalization & validation phases ---
                actionJsonElement.remove("orgId");
                actionJsonElement.put("projectId", currentProjectId);

                if (actionJsonElement.has("runnableItem") && actionJsonElement.get("runnableItem").isObject()) {
                    ObjectNode runnableNode = (ObjectNode) actionJsonElement.get("runnableItem");
                    runnableNode.remove("endpointLink");
                    runnableNode.put("endpointLink", targetVroLink);
                }

                VcfaResourceAction localAction = mapper.treeToValue(actionJsonElement, VcfaResourceAction.class);
                String complexTrackingName = localAction.getResourceType() + RESOURCE_ACTION_SEPARATOR + localAction.getName();

                if (isExcludedByDescriptor(complexTrackingName)) {
                    logger.info("Resource action asset '{}' is excluded by descriptor configuration rules. Skipping import.", complexTrackingName);
                    continue;
                }

                logger.info("Processing local Resource Action asset configuration: '{}'", file.getName());

                Optional<VcfaResourceAction> existingRemote = remoteActions.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(localAction.getName()) && 
                                     r.getResourceType().equalsIgnoreCase(localAction.getResourceType()))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaResourceAction remoteMatch = existingRemote.get();
                    
                    // --- REPRODUCED SYSTEM LOGIC: Execute pre-cleanup operations using the matching remote ID ---
                    try {
                        logger.info("Purging remote trace elements for resource action '{}' matching ID: {}", complexTrackingName, remoteMatch.getId());
                        restClient.deleteResourceAction(remoteMatch.getId());
                    } catch (Exception e) {
                        logger.error("Pre-cleanup cycle for resource action '{}' encountered an issue: {}. Attempting overwrite.", complexTrackingName, e.getMessage());
                    }
                }

                // --- REPRODUCED SYSTEM LOGIC: Execute the asynchronous two-step platform import sequence ---
                if (actionJsonElement.has("formDefinition") && actionJsonElement.get("formDefinition").isObject()) {
                    ((ObjectNode) actionJsonElement.get("formDefinition")).remove("id");
                }

                logger.info("Executing Primary Pass Creation for Action: '{}'", complexTrackingName);
                String primaryPayload = mapper.writeValueAsString(actionJsonElement);
                String resultRawJson = restClient.createResourceAction(mapper.readValue(primaryPayload, VcfaResourceAction.class));

                // Step 2: Inject the newly generated form metadata mapping identifiers back into the definition tree
                ObjectNode resultNode = (ObjectNode) mapper.readTree(resultRawJson);
                if (resultNode.has("formDefinition") && resultNode.get("formDefinition").has("id")) {
                    String generatedFormId = resultNode.get("formDefinition").get("id").asText();
                    
                    if (actionJsonElement.has("formDefinition") && actionJsonElement.get("formDefinition").isObject()) {
                        ((ObjectNode) actionJsonElement.get("formDefinition")).put("id", generatedFormId);
                        
                        logger.info("Executing Secondary Pass Form Adjustment update for action: '{}'", complexTrackingName);
                        String secondaryPayload = mapper.writeValueAsString(actionJsonElement);
                        restClient.createResourceAction(mapper.readValue(secondaryPayload, VcfaResourceAction.class));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("CRITICAL: Failed to import Resource Actions from filesystem to target environment", e);
        }
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

            // Fix the structural parsing engine variable from strict List<String> to generic raw List
            List<?> yamlParsedList = null;
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

                        if (actionListObj instanceof List) {
                            yamlParsedList = (List<?>) actionListObj;
                            if (yamlParsedList.isEmpty()) {
                                isExplicitlyEmpty = true;
                            }
                        }
                    }
                }
            }

            // --- TRISTATE EVALUATION MATRIX ---

            // Scenario 1: Explicitly Empty "[]" -> Safety bypass toggle. Delete Nothing.
            if (isExplicitlyEmpty) {
                logger.info("Resource Action descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            // Scenario 2: Key is completely null/omitted -> Wildcard target mode active. Purge Everything.
            if (yamlParsedList == null) {
                logger.info("Resource Action descriptor is undefined/null. Omitted wildcard trigger: Purging ALL remote resource actions.");
                for (VcfaResourceAction remoteAct : remoteActions) {
                    logger.info("[WILDCARD DELETE] Deleting resource action named '{}__{}' matching ID: {}",
                            remoteAct.getResourceType(), remoteAct.getName(), remoteAct.getId());
                    restClient.deleteResourceAction(remoteAct.getId());
                }
                return;
            }

            // Scenario 3: Explicit List -> Filter targeted matches sequentially by programmatic Name or Complex Name
            logger.info("Resource Action targeted filter list active. Evaluating matching entries for deletion sequence...");
            
            // Surgically normalize YAML input elements into clean, space-stripped lowercase strings
            List<String> sanitizedTargets = new java.util.ArrayList<>();
            for (Object rawItem : yamlParsedList) {
                if (rawItem != null) {
                    sanitizedTargets.add(rawItem.toString().trim().toLowerCase());
                }
            }

            // Trace statement to see exactly what strings the application code parsed out of your YAML file
            logger.info("Normalized Resource Action targets parsed from content.yaml: {}", sanitizedTargets);

            for (VcfaResourceAction remoteAct : remoteActions) {
                String complexName = remoteAct.getResourceType() + RESOURCE_ACTION_SEPARATOR + remoteAct.getName();
                String plainActionName = remoteAct.getName();
                
                // Print a debug trace for every item found on the live server to cross-verify keys
                logger.info("Found remote Resource Action candidate -> Complex Name: '{}', Plain Name: '{}'", complexName, plainActionName);

                boolean isMatched = false;
                if (complexName != null && sanitizedTargets.contains(complexName.trim().toLowerCase())) {
                    isMatched = true;
                }
                if (plainActionName != null && sanitizedTargets.contains(plainActionName.trim().toLowerCase())) {
                    isMatched = true;
                }

                if (isMatched) {
                    logger.info("[TARGETED DELETE] Match successful! Deleting resource action named '{}' matching ID: {}", complexName, remoteAct.getId());
                    try {
                        restClient.deleteResourceAction(remoteAct.getId());
                    } catch (Exception ex) {
                        logger.warn("Failed executing deletion endpoint for action resource entity '{}': {}", complexName, ex.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure resource action definitions", e);
        }
    }

    /**
     * Helper to safely extract and determine if the configuration block array is explicitly initialized to '[]'.
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

        List<String> allowedActions = localDescriptor.getResourceAction();
        return allowedActions != null && allowedActions.isEmpty();
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
}
