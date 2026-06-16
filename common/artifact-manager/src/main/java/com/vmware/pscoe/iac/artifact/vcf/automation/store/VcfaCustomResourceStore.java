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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
     * Exports all Custom Resources from the target environment to the filesystem
     * package.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling custom resource configurations from the remote environment...");

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Custom Resource descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

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
                // --- REPRODUCED SYSTEM LOGIC: Align with native programmatic backend file
                // names ---
                String trackingName = resource.getName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Custom Resource '{}' is excluded by descriptor rules. Skipping export.", trackingName);
                    continue;
                }

                String safeFileName = trackingName.replaceAll("[^a-zA-Z0-9-_\\s\\.]", "_");
                File jsonFile = Paths.get(baseResourcesPath, safeFileName + ".json").toFile();

                // Convert model instance to JSON tree representation to sanitize system
                // tracking properties
                ObjectNode jsonNode = mapper.valueToTree(resource);
                jsonNode.remove("id");
                if (jsonNode.has("orgId")) {
                    jsonNode.remove("orgId");
                }

                logger.info("Successfully synchronized custom resource asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writeValueAsString(jsonNode);
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
     * Imports local Custom Resource configuration files back up to the target
     * environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing custom resources from {}", sourceDirectory.getAbsolutePath());

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Custom Resource descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

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
            // --- COMPILATION SAFEGUARD TRY-CATCH WRAPPER BLOCK ---
            List<VcfaCustomResourceType> remoteResources;
            try {
                remoteResources = restClient.getCustomResources();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to load remote custom resources due to an unexpected connection error", e);
            }

            File[] resourceFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (resourceFiles == null || resourceFiles.length == 0) {
                logger.info("Could not find any custom resource assets to import.");
                return;
            }

            for (File file : resourceFiles) {
                String jsonContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                ObjectNode customResourceJsonElement = (ObjectNode) mapper.readTree(jsonContent);

                // --- REPRODUCED SYSTEM LOGIC: Perform pre-flight naming regex verification
                // validations ---
                validateCustomResourceDay2ActionName(customResourceJsonElement);

                // --- REPRODUCED SYSTEM LOGIC: Re-anchor tenant scopes and endpoints across
                // environments ---
                fixCustomResourceDefinition(customResourceJsonElement);
                populateVroEndpoints(customResourceJsonElement);

                VcfaCustomResourceType localResource = mapper.treeToValue(customResourceJsonElement,
                        VcfaCustomResourceType.class);
                String trackingName = localResource.getName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info(
                            "Custom resource asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            trackingName);
                    continue;
                }

                logger.info("Processing local Custom Resource asset configuration: '{}'", file.getName());

                Optional<VcfaCustomResourceType> existingRemote = remoteResources.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(localResource.getName()) ||
                                (r.getDisplayName() != null
                                        && r.getDisplayName().equalsIgnoreCase(localResource.getDisplayName()))
                                ||
                                (r.getId() != null && r.getId().equals(localResource.getId())))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaCustomResourceType remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localResource)) {
                        logger.info(
                                "Custom Resource '{}' matches remote system configuration exactly. Skipping update.",
                                trackingName);
                    } else {
                        // --- REPRODUCED SYSTEM LOGIC: Safe platform delete-and-recreate lifecycle
                        // orchestration ---
                        logger.info("Delta detected for Custom Resource '{}'. Initiating deletion phase.",
                                trackingName);

                        try {
                            // --- COMPILATION SAFEGUARD TRY-CATCH WRAPPER BLOCK ---
                            try {
                                restClient.deleteCustomResourceType(remoteMatch.getId());
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "Failed to delete existing remote custom resource asset pattern context", e);
                            }
                            customResourceJsonElement.remove("id");
                        } catch (Exception ex) {
                            // Check if the asset is currently locked by active server processes or leases
                            if (isCustomResourceActiveAttached(ex)) {
                                logger.warn(
                                        "Cannot purge Custom Resource '{}' due to active deployments on the cluster. Switching to in-place override update fallback logic.",
                                        trackingName);
                                customResourceJsonElement.put("id", remoteMatch.getId());
                            } else {
                                throw ex;
                            }
                        }

                        logger.info("Pushing custom resource definition payload: '{}'", trackingName);
                        String payloadStr = mapper.writeValueAsString(customResourceJsonElement);

                        // --- COMPILATION SAFEGUARD TRY-CATCH WRAPPER BLOCK ---
                        try {
                            restClient.createCustomResourceType(
                                    mapper.readValue(payloadStr, VcfaCustomResourceType.class));
                        } catch (IOException e) {
                            throw new RuntimeException(
                                    "Failed to recreate existing remote custom resource asset pattern context", e);
                        }
                    }
                } else {
                    logger.info("Custom Resource '{}' not found on target server. Executing remote creation.",
                            trackingName);
                    customResourceJsonElement.remove("id");
                    String payloadStr = mapper.writeValueAsString(customResourceJsonElement);

                    // --- COMPILATION SAFEGUARD TRY-CATCH WRAPPER BLOCK ---
                    try {
                        restClient.createCustomResourceType(mapper.readValue(payloadStr, VcfaCustomResourceType.class));
                    } catch (IOException e) {
                        throw new RuntimeException(
                                "Failed to provision a fresh custom resource asset pattern context to target", e);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "CRITICAL: Failed to import Custom Resources from filesystem to target environment", e);
        }
    }

    private void validateCustomResourceDay2ActionName(ObjectNode customResourceJsonElement) {
        if (customResourceJsonElement.has("additionalActions")
                && customResourceJsonElement.get("additionalActions").isArray()) {
            ArrayNode additionalActionsArray = (ArrayNode) customResourceJsonElement.get("additionalActions");
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9:\\-_.]");

            for (JsonNode action : additionalActionsArray) {
                if (action != null && action.has("name")) {
                    String name = action.get("name").asText();
                    Matcher matcher = pattern.matcher(name);

                    if (matcher.find() || name.startsWith("_") || name.endsWith("_") ||
                            name.startsWith(".") || name.endsWith(".") || name.contains(" ")) {
                        throw new RuntimeException(String.format(
                                "Action's name: '%s' contains invalid symbols. Must not use spaces or start/end with points or underscores.",
                                name));
                    }
                }
            }
        }
    }

    private void fixCustomResourceDefinition(ObjectNode customResourceJsonElement) {
        String currentOrgId = restClient.getOrganizationId();
        customResourceJsonElement.put("orgId", currentOrgId);

        if (customResourceJsonElement.has("projectId") && !customResourceJsonElement.get("projectId").isNull()) {
            try {
                customResourceJsonElement.put("projectId", restClient.getProjectId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to recover target landscape project boundary mappings context info.",
                        e);
            }
        }

        if (customResourceJsonElement.has("additionalActions")
                && customResourceJsonElement.get("additionalActions").isArray()) {
            ArrayNode additionalActionsArray = (ArrayNode) customResourceJsonElement.get("additionalActions");
            for (JsonNode action : additionalActionsArray) {
                if (action instanceof ObjectNode) {
                    ObjectNode actionJson = (ObjectNode) action;
                    actionJson.put("orgId", currentOrgId);

                    if (actionJson.has("formDefinition") && actionJson.get("formDefinition").isObject()) {
                        ObjectNode formDefinition = (ObjectNode) actionJson.get("formDefinition");
                        formDefinition.remove("id");
                        formDefinition.put("tenant", currentOrgId);
                        if (formDefinition.has("projectId") && !formDefinition.get("projectId").isNull()) {
                            try {
                                formDefinition.put("projectId", restClient.getProjectId());
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "Failed to map form boundary definitions token references properties.", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private void populateVroEndpoints(ObjectNode customResourceJsonElement) {
        String targetVroEndpointLink = restClient.getVroTargetIntegrationEndpointLink();

        if (customResourceJsonElement.has("mainActions") && customResourceJsonElement.get("mainActions").isObject()) {
            ObjectNode mainActions = (ObjectNode) customResourceJsonElement.get("mainActions");
            for (String mainAct : new String[] { "create", "update", "delete" }) {
                if (mainActions.has(mainAct) && mainActions.get(mainAct).isObject()) {
                    ((ObjectNode) mainActions.get(mainAct)).put("endpointLink", targetVroEndpointLink);
                }
            }
        }

        if (customResourceJsonElement.has("additionalActions")
                && customResourceJsonElement.get("additionalActions").isArray()) {
            ArrayNode additionalActions = (ArrayNode) customResourceJsonElement.get("additionalActions");
            for (JsonNode action : additionalActions) {
                if (action instanceof ObjectNode && action.has("runnableItem")
                        && action.get("runnableItem").isObject()) {
                    ((ObjectNode) action.get("runnableItem")).put("endpointLink", targetVroEndpointLink);
                }
            }
        }
    }

    private boolean isCustomResourceActiveAttached(Exception clientException) {
        final String magicMessage = "Resource type cannot be deleted as there are active resources attached to it";
        StringBuilder builder = new StringBuilder();
        Throwable th = clientException;
        while (th != null) {
            builder.append(th.getMessage()).append("\n");
            th = th.getCause();
        }
        return builder.toString().contains(magicMessage);
    }

    private boolean isIdentical(VcfaCustomResourceType remote, VcfaCustomResourceType local) {
        if (remote == null || local == null) {
            return false;
        }

        return java.util.Objects.equals(remote.getName(), local.getName()) &&
                java.util.Objects.equals(remote.getDisplayName(), local.getDisplayName()) &&
                java.util.Objects.equals(remote.getDescription(), local.getDescription()) &&
                java.util.Objects.equals(remote.getExternalType(), local.getExternalType()) &&
                java.util.Objects.equals(remote.getSchemaType(), local.getSchemaType()) &&
                java.util.Objects.equals(remote.getMainActions(), local.getMainActions()) &&
                java.util.Objects.equals(remote.getAdditionalActions(), local.getAdditionalActions()) &&
                java.util.Objects.equals(remote.getProperties(), local.getProperties());
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

            // Fix the structural parsing engine variable from strict List<String> to
            // generic raw List
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
                        Object resourceListObj = rawMap.containsKey("custom-resource") ? rawMap.get("custom-resource")
                                : rawMap.get("customResources");

                        if (resourceListObj instanceof List) {
                            yamlParsedList = (List<?>) resourceListObj;
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
                logger.info("Custom Resource descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            // Scenario 2: Key is completely null/omitted -> Wildcard target mode active.
            // Purge Everything.
            if (yamlParsedList == null) {
                logger.info(
                        "Custom Resource descriptor is undefined/null. Omitted wildcard trigger: Purging ALL remote custom resources.");
                for (VcfaCustomResourceType remoteRes : remoteResources) {
                    logger.info("[WILDCARD DELETE] Deleting custom resource named '{}' matching ID: {}",
                            remoteRes.getName(), remoteRes.getId());
                    restClient.deleteCustomResourceType(remoteRes.getId());
                }
                return;
            }

            // Scenario 3: Explicit List -> Filter targeted matches sequentially by
            // programmatic Name or Display Name
            logger.info(
                    "Custom Resource targeted filter list active. Evaluating matching entries for deletion sequence...");

            // Surgically normalize YAML input elements into clean, space-stripped lowercase
            // strings
            List<String> sanitizedTargets = new java.util.ArrayList<>();
            for (Object rawItem : yamlParsedList) {
                if (rawItem != null) {
                    sanitizedTargets.add(rawItem.toString().trim().toLowerCase());
                }
            }

            // Trace statement to see exactly what strings the application code parsed out
            // of your YAML file
            logger.info("Normalized targets parsed from content.yaml: {}", sanitizedTargets);

            for (VcfaCustomResourceType remoteRes : remoteResources) {
                String remoteName = remoteRes.getName();
                String displayName = remoteRes.getDisplayName();

                // Print a debug trace for every item found on the live server to cross-verify
                // keys
                logger.info("Found remote Custom Resource candidate -> Programmatic Name: '{}', UI Display Name: '{}'",
                        remoteName, displayName);

                boolean isMatched = false;
                if (remoteName != null && sanitizedTargets.contains(remoteName.trim().toLowerCase())) {
                    isMatched = true;
                }
                if (displayName != null && sanitizedTargets.contains(displayName.trim().toLowerCase())) {
                    isMatched = true;
                }

                if (isMatched) {
                    logger.info(
                            "[TARGETED DELETE] Match successful! Deleting custom resource named '{}' matching ID: {}",
                            remoteName, remoteRes.getId());
                    try {
                        restClient.deleteCustomResourceType(remoteRes.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(
                                "Failed executing server request to drop targeted custom resource configuration element",
                                e);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure custom resource definitions", e);
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

        List<String> allowedResources = localDescriptor.getCustomResource();
        return allowedResources != null && allowedResources.isEmpty();
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
