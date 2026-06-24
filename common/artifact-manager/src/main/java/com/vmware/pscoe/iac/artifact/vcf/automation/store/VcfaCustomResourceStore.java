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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaDescriptorHelper;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import java.util.Objects;

public class VcfaCustomResourceStore extends AbstractVcfaStore {
    private static final String DIR_CUSTOM_RESOURCES = "custom-resources";
    private static final String SUBDIR_ADDITIONAL_ACTIONS = "additional-actions";
    private static final String PAYLOAD_ADDITIONAL_ACTIONS = "additionalActions";
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

        List<String> allowedResources = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "custom-resource",
                "customResources");
        if (allowedResources != null && allowedResources.isEmpty()) {
            logger.info(
                    "Custom Resource descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
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

                this.verifyAssetPathSafety(trackingName, "Custom Resource");

                File resourceFolder = Paths.get(baseResourcesPath, trackingName).toFile();
                if (!resourceFolder.exists()) {
                    resourceFolder.mkdirs();
                }

                Map<String, Object> resourceMap = mapper.convertValue(resource,
                        new TypeReference<Map<String, Object>>() {
                        });
                List<Map<String, Object>> extractedActions = null;

                if (resourceMap.containsKey(PAYLOAD_ADDITIONAL_ACTIONS)
                        && resourceMap.get(PAYLOAD_ADDITIONAL_ACTIONS) != null) {
                    extractedActions = (List<Map<String, Object>>) resourceMap.get(PAYLOAD_ADDITIONAL_ACTIONS);
                    resourceMap.remove(PAYLOAD_ADDITIONAL_ACTIONS);
                }

                File detailsFile = Paths.get(resourceFolder.getPath(), "details.json").toFile();
                String detailsJson = mapper.writeValueAsString(resourceMap);
                Files.write(detailsFile.toPath(), detailsJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                if (extractedActions != null && !extractedActions.isEmpty()) {
                    File actionsSubFolder = Paths.get(resourceFolder.getPath(), SUBDIR_ADDITIONAL_ACTIONS).toFile();
                    if (!actionsSubFolder.exists()) {
                        actionsSubFolder.mkdirs();
                    }

                    for (Map<String, Object> action : extractedActions) {
                        String actionDisplayName = (String) action.get("displayName");
                        this.verifyAssetPathSafety(actionDisplayName, "Additional Action");

                        File singleActionFolder = Paths.get(actionsSubFolder.getPath(), actionDisplayName).toFile();
                        if (!singleActionFolder.exists()) {
                            singleActionFolder.mkdirs();
                        }

                        Map<String, Object> formDefinitionMap = null;
                        String cssStyles = "";

                        if (action.containsKey("formDefinition") && action.get("formDefinition") != null) {
                            formDefinitionMap = (Map<String, Object>) action.get("formDefinition");
                            action.remove("formDefinition");

                            if (formDefinitionMap.containsKey("styles") && formDefinitionMap.get("styles") != null) {
                                cssStyles = formDefinitionMap.get("styles").toString().trim();
                                formDefinitionMap.remove("styles");
                            }

                            if (formDefinitionMap.containsKey("form") && formDefinitionMap.get("form") != null) {
                                Object rawForm = formDefinitionMap.get("form");
                                if (rawForm instanceof String) {
                                    Map<String, Object> parsedForm = mapper.readValue((String) rawForm,
                                            new TypeReference<Map<String, Object>>() {
                                            });
                                    formDefinitionMap.put("form", parsedForm);
                                }
                            }
                        }

                        File actionDetailsFile = Paths.get(singleActionFolder.getPath(), "details.json").toFile();
                        Files.write(actionDetailsFile.toPath(),
                                mapper.writeValueAsString(action).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        if (formDefinitionMap != null) {
                            String formFileName = actionDisplayName + "__FormData.json";
                            File actionFormFile = Paths.get(singleActionFolder.getPath(), formFileName).toFile();
                            Files.write(actionFormFile.toPath(),
                                    mapper.writeValueAsString(formDefinitionMap).getBytes(StandardCharsets.UTF_8),
                                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        }

                        File stylesFile = Paths.get(singleActionFolder.getPath(), "styles.css").toFile();
                        Files.write(stylesFile.toPath(), cssStyles.getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

        List<String> allowedResources = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "custom-resource",
                "customResources");
        if (allowedResources != null && allowedResources.isEmpty()) {
            logger.info(
                    "Custom Resource descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_CUSTOM_RESOURCES).toFile();

        // --- NEW STRATEGIC COUPLING: Call the unified asset validator (Folders only)
        // ---
        VcfaDescriptorHelper.validateAssetsPresent(this.vcfaPackage, localDir, "Custom Resource", true,
                "custom-resource", "customResources");

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

                File actionsSubFolder = Paths.get(folder.getPath(), SUBDIR_ADDITIONAL_ACTIONS).toFile();
                if (actionsSubFolder.exists() && actionsSubFolder.isDirectory()) {
                    File[] singleActionFolders = actionsSubFolder.listFiles(File::isDirectory);
                    if (singleActionFolders != null) {
                        for (File actionFolder : singleActionFolders) {
                            String actionName = actionFolder.getName();
                            File actionDetailsFile = new File(actionFolder, "details.json");
                            File actionFormFile = new File(actionFolder, actionName + "__FormData.json");
                            File stylesFile = new File(actionFolder, "styles.css");

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

                            if (formDefMap.containsKey("form") && formDefMap.get("form") instanceof Map) {
                                Map<String, Object> innerFormMap = (Map<String, Object>) formDefMap.get("form");
                                if (innerFormMap.containsKey("layout") || innerFormMap.containsKey("schema")) {
                                    logger.info(
                                            "Identified and unwrapped nested 'form' sub-block pattern inside custom resource additional action payload schema.");
                                    formDefMap = innerFormMap;
                                }
                            }

                            if (formDefMap.containsKey("form") && formDefMap.get("form") != null) {
                                Object formObj = formDefMap.get("form");
                                if (!(formObj instanceof String)) {
                                    formDefMap.put("form", mapper.writeValueAsString(formObj));
                                }
                            }

                            String localStyles = "";
                            if (stylesFile.exists()) {
                                localStyles = new String(Files.readAllBytes(stylesFile.toPath()),
                                        StandardCharsets.UTF_8).trim();
                            }
                            formDefMap.put("styles", localStyles);

                            actionMap.put("formDefinition", formDefMap);
                            combinedActionsList.add(actionMap);
                        }
                    }
                }

                resourceMap.put(PAYLOAD_ADDITIONAL_ACTIONS, combinedActionsList);
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

    private void fixCustomResourceDefinition(ObjectNode customResourceJsonElement) throws IOException {
        String currentOrgId = restClient.getOrganizationId();
        customResourceJsonElement.put("orgId", currentOrgId);

        if (customResourceJsonElement.has("projectId") && !customResourceJsonElement.get("projectId").isNull()) {
            customResourceJsonElement.put("projectId", restClient.getProjectId());
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
                            formDefinition.put("projectId", restClient.getProjectId());
                        }
                    }
                }
            }
        }
    }

    private void populateVroEndpoints(ObjectNode customResourceJsonElement) throws IOException {
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
        return Objects.equals(remote.getDisplayName(), local.getDisplayName())
                && Objects.equals(remote.getDescription(), local.getDescription())
                && Objects.equals(remote.getExternalType(), local.getExternalType())
                && Objects.equals(remote.getSchemaType(), local.getSchemaType())
                && Objects.equals(remote.getMainActions(), local.getMainActions())
                && Objects.equals(remote.getAdditionalActions(), local.getAdditionalActions())
                && Objects.equals(remote.getProperties(), local.getProperties());
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

            List<String> itemsToDelete = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "custom-resource",
                    "customResources");

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

            if (itemsToDelete.isEmpty()) {
                logger.info("Custom Resource descriptor is explicitly empty '[]'. Skipping deletion entirely.");
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
        List<String> allowedResources = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "custom-resource",
                "customResources");
        if (allowedResources == null) {
            return false;
        }
        if (allowedResources.isEmpty()) {
            return true;
        }
        return !allowedResources.contains(trackingName);
    }
}