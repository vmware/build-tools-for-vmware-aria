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
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaDescriptorHelper;

/**
 * Handles publication, replication, and republish lifecycle of vRO Workflow
 * Catalog Items directly using flat JSON asset layouts mapped to the Service
 * Broker API.
 */
public class VcfaVroWorkflowStore extends AbstractVcfaStore {
    private static final String DIR_WORKFLOWS = "workflows";
    private static final String CATALOG_TYPE_VRO_WORKFLOW = "com.vmw.vro.workflow";
    private final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

    /**
     * Pushes local flat workflow JSON configuration files directly to the target
     * environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        File workflowFolder = Paths.get(sourceDirectory.getPath(), DIR_WORKFLOWS).toFile();

        // Use the native framework validator with isFolder set to false (just like
        // subscriptions)
        VcfaDescriptorHelper.validateAssetsPresent(this.vcfaPackage, workflowFolder, "vRO Workflow", false, "workflow",
                "workflows");

        if (!workflowFolder.exists()) {
            logger.info("No vRO workflows workspace directory found - skipping import");
            return;
        }

        // Scan directly for flat JSON payload files instead of nested asset
        // subdirectories
        File[] workflowFiles = workflowFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
        if (workflowFiles == null || workflowFiles.length == 0) {
            logger.info("No vRO workflow item flat files available to import");
            return;
        }

        List<JsonObject> serverCatalogItems = fetchServerWorkflowCatalogItems();

        for (File wfFile : workflowFiles) {
            String filename = wfFile.getName();
            // Strip the extension to resolve the foundational tracking asset name
            String wfName = filename.substring(0, filename.lastIndexOf(".json"));

            if (isExcludedByDescriptor(wfName)) {
                logger.info("vRO Workflow Catalog Item '{}' is excluded by descriptor configuration rules. Skipping.",
                        wfName);
                continue;
            }

            try {
                JsonObject localPayload = loadCatalogItemPayload(wfFile);
                String declaredName = localPayload.has("name") ? localPayload.get("name").getAsString() : "";

                if (!Objects.equals(declaredName, wfName)) {
                    throw new IllegalStateException(String.format(
                            "Workspace filesystem corruption identified! Local JSON file name '%s' does not match the 'name' property declared inside the payload ('%s').",
                            filename, declaredName));
                }

                // Locate matching catalog item on target host by matching names
                JsonObject existingItem = serverCatalogItems.stream()
                        .filter(item -> item.has("name") && wfName.equalsIgnoreCase(item.get("name").getAsString()))
                        .findFirst()
                        .orElse(null);

                if (existingItem == null) {
                    logger.info(
                            "Catalog Item '{}' does not exist on target instance. Executing publish: POST /catalog/api/items:publish",
                            wfName);
                    restClient.publishCatalogItem(localPayload);
                } else {
                    String catalogItemId = existingItem.get("id").getAsJsonPrimitive().getAsString();
                    logger.info(
                            "Catalog Item '{}' exists (ID: {}). Executing updates: POST /catalog/api/items/{}:republish",
                            wfName, catalogItemId, catalogItemId);
                    restClient.republishCatalogItem(catalogItemId, localPayload);
                }
            } catch (IOException e) {
                logger.error("Failed to import vRO workflow catalog entry from file: {}", filename, e);
            }
        }
    }

    /**
     * Pulls active vRO Workflow Catalog Items from server and writes them as flat
     * file targets.
     */
    @Override
    public void exportContent() {
        List<JsonObject> serverCatalogItems = fetchServerWorkflowCatalogItems();
        Package serverPackage = this.vcfaPackage;
        java.util.Set<String> exportedNamesTracker = new java.util.HashSet<>();

        for (JsonObject item : serverCatalogItems) {
            String name = item.has("name") ? item.get("name").getAsString() : "unnamed-catalog-item";

            if (isExcludedByDescriptor(name)) {
                logger.info(
                        "vRO Workflow Catalog Item '{}' is excluded by descriptor configuration rules. Skipping export.",
                        name);
                continue;
            }

            if (exportedNamesTracker.contains(name)) {
                throw new IllegalStateException(
                        "The remote server environment contains duplicate catalog item definitions sharing the name '"
                                + name
                                + "'. Automatic export aborted to prevent workspace tracking corruption.");
            }
            exportedNamesTracker.add(name);

            try {
                saveCatalogItemToDisk(item, serverPackage, name);
            } catch (IOException e) {
                logger.error("Unable to export vRO workflow catalog details for item: {}", name, e);
            }
        }
    }

    /**
     * Handles infrastructure cleanup routine sequences via Service Broker linkages.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for vRO Workflow Catalog references...");
        try {
            List<JsonObject> remoteItems = fetchServerWorkflowCatalogItems();
            if (remoteItems == null || remoteItems.isEmpty()) {
                logger.info("No remote vRO workflow catalog items found to clear.");
                return;
            }

            List<String> itemsToDelete = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "workflow",
                    "workflows");

            if (itemsToDelete == null) {
                logger.info(
                        "vRO Workflow descriptor is completely omitted. Wildcard active: Purging all published references.");
                for (JsonObject item : remoteItems) {
                    String catalogItemId = item.get("id").getAsString();
                    logger.info("[WILDCARD CLEAN] Processing cleanup status mapping for item ID: {}", catalogItemId);
                    restClient.unpublishCatalogItem(catalogItemId);
                }
                return;
            }

            if (itemsToDelete.isEmpty()) {
                logger.info("vRO Workflow descriptor is explicitly empty '[]'. Skipping deletion cycles entirely.");
                return;
            }

            for (JsonObject item : remoteItems) {
                String name = item.get("name").getAsString();
                if (itemsToDelete.contains(name)) {
                    String catalogItemId = item.get("id").getAsString();
                    logger.info("[TARGETED CLEAN] Removing catalog publication entry for item: {}", name);
                    restClient.unpublishCatalogItem(catalogItemId);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure catalog item layouts",
                    e);
        }
    }

    private boolean isExcludedByDescriptor(String wfName) {
        List<String> allowedWfs = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "workflow", "workflows");
        if (allowedWfs == null) {
            return false;
        }
        if (allowedWfs.isEmpty()) {
            return true;
        }
        return !allowedWfs.contains(wfName);
    }

    private JsonObject loadCatalogItemPayload(File wfFile) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(wfFile))) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            logger.error("JSON structure missing or corrupted in file target: {}", wfFile.getName());
            throw e;
        }
    }

    private List<JsonObject> fetchServerWorkflowCatalogItems() {
        try {
            return restClient.getCatalogItemsByType(CATALOG_TYPE_VRO_WORKFLOW);
        } catch (IOException e) {
            logger.error("Failed to fetch catalog structures from server path: /catalog/api/items", e);
            throw new RuntimeException("Unable to synchronize configuration items from target Aria Automation server",
                    e);
        }
    }

    private void saveCatalogItemToDisk(JsonObject item, Package serverPackage, String name) throws IOException {
        String workflowsDirPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_WORKFLOWS)
                .toString();
        File workflowsDir = new File(workflowsDirPath);
        if (!workflowsDir.exists()) {
            workflowsDir.mkdirs();
        }

        this.verifyAssetPathSafety(name, "vRO Workflow Catalog Item");

        // Write the payload directly to workflows/<Workflow_Name>.json
        String jsonFilePath = workflowsDirPath + File.separator + name + ".json";

        String formattedJsonPayload = gson.toJson(item);
        Files.write(Paths.get(jsonFilePath), formattedJsonPayload.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        logger.info("Successfully serialized catalog item mapping asset structure directly to flat file: {}",
                jsonFilePath);
    }
}
