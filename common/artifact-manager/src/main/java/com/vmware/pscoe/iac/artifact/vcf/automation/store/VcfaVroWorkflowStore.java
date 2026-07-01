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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

        // OPTIMIZATION: Fetch ONLY names and IDs to run host existence matching
        List<JsonObject> serverCatalogItems = fetchServerWorkflowCatalogItemNames();

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
                resolveOrganizationSharingsForPush(localPayload);
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
                    String catalogItemId = existingItem.get("id").getAsString();
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
        // 1. Lightweight discovery phase: Fetch only names and IDs from the inventory
        // API
        List<JsonObject> lightweightItems = fetchServerWorkflowCatalogItemNames();
        Package serverPackage = this.vcfaPackage;
        java.util.Set<String> exportedNamesTracker = new java.util.HashSet<>();
        List<String> idsToFetch = new java.util.ArrayList<>();

        // 2. Evaluate targets and track exclusions
        for (JsonObject item : lightweightItems) {
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

            if (item.has("id") && !item.get("id").isJsonNull()) {
                idsToFetch.add(item.get("id").getAsString());
            }
        }

        // Quick escape if there's nothing matched by descriptor rules
        if (idsToFetch.isEmpty()) {
            logger.info("No active workflow catalog items matched the criteria for export processing.");
            return;
        }

        // 3. Deep harvest phase (Round 1): Fetch payloads needed to feed the republish
        // method
        List<JsonObject> itemsToRepublish = fetchServerWorkflowCatalogItems(idsToFetch);

        // 4. Trigger pre-export republishing for all targeted items to bake UI changes
        // on the server
        for (JsonObject itemToRepublish : itemsToRepublish) {
            if (itemToRepublish.has("id") && !itemToRepublish.get("id").isJsonNull()) {
                String catalogItemId = itemToRepublish.get("id").getAsString();
                String name = itemToRepublish.has("name") ? itemToRepublish.get("name").getAsString()
                        : "unnamed-catalog-item";

                try {
                    logger.info(
                            "Executing pre-export republish for Catalog Item '{}' (ID: {}) to refresh UI form layouts...",
                            name, catalogItemId);
                    restClient.republishCatalogItem(catalogItemId, itemToRepublish);
                } catch (Exception e) {
                    logger.warn(
                            "Non-fatal exception encountered during pre-export republish for '{}': {}. Proceeding with pull using current server state.",
                            name, e.getMessage());
                }
            }
        }

        // 5. Deep harvest phase (Round 2): Re-fetch the freshly baked schemas and
        // layout configurations
        logger.info("Re-harvesting catalog configurations to capture post-republish form layouts...");
        List<JsonObject> freshCatalogItems = fetchServerWorkflowCatalogItems(idsToFetch);

        // 6. Run serialization on the fresh target data
        for (JsonObject freshItem : freshCatalogItems) {
            String name = freshItem.has("name") ? freshItem.get("name").getAsString() : "unnamed-catalog-item";
            try {
                saveCatalogItemToDisk(freshItem, serverPackage, name);
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
            List<JsonObject> remoteItems = fetchServerWorkflowCatalogItemNames();
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

    private List<JsonObject> fetchServerWorkflowCatalogItemNames() {
        try {
            return restClient.getCatalogItemNamesByType(CATALOG_TYPE_VRO_WORKFLOW);
        } catch (IOException e) {
            logger.error("Failed to fetch catalog structures from server path: /catalog/api/items", e);
            throw new RuntimeException("Unable to synchronize configuration items from target Aria Automation server",
                    e);
        }
    }

    private List<JsonObject> fetchServerWorkflowCatalogItems(List<String> ids) {
        try {
            return restClient.getCatalogItemsByType(ids, CATALOG_TYPE_VRO_WORKFLOW);
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

        // 1. Extract the form subproperty cleanly
        com.google.gson.JsonElement formElement = item.remove("form");

        // 2. Handle cross-module routing for the .wf.form.json sidecar file
        if (formElement != null && !formElement.isJsonNull()) {
            // Get the actual directory where the user executed the pull command
            Path projectRoot = Paths.get(System.getProperty("user.dir"));

            // Guard: If executed from inside the 'vra' sub-module folder, step up to the
            // repository root
            if (projectRoot.getFileName() != null && projectRoot.getFileName().toString().equalsIgnoreCase("vra")) {
                projectRoot = projectRoot.getParent();
            }

            // Safely resolve the true vro/src codebase path in your actual workspace
            Path vroSrcPath = projectRoot.resolve("vro").resolve("src");

            Path formTargetDir = null;
            String targetTsFileName = name + ".wf.ts";

            if (Files.exists(vroSrcPath)) {
                logger.info("Scanning true vRO workspace tree at '{}' for companion file '{}'...",
                        vroSrcPath.toAbsolutePath(), targetTsFileName);

                // Recursively search for the matching .wf.ts file
                try (java.util.stream.Stream<Path> pathStream = Files.walk(vroSrcPath)) {
                    formTargetDir = pathStream
                            .filter(Files::isRegularFile)
                            .filter(p -> p.getFileName().toString().equals(targetTsFileName))
                            .map(Path::getParent)
                            .findFirst()
                            .orElse(null);
                }
            } else {
                logger.warn("Could not find the 'vro/src' directory relative to execution context: {}",
                        vroSrcPath.toAbsolutePath());
            }

            // Fallback: If no matching file is found, place it in the "Orphaned request
            // forms" directory
            if (formTargetDir == null) {
                formTargetDir = vroSrcPath.resolve("Orphaned request forms");
                Files.createDirectories(formTargetDir);
                logger.info("No matching vRO workflow script file found in workspace. Routing form asset to folder: {}",
                        formTargetDir.toAbsolutePath());
            }

            // Resolve exact output file path and write the isolated form JSON payload
            Path formFilePath = formTargetDir.resolve(name + ".wf.form.json");

            // Extract just the inner "form" layout canvas graph
            com.google.gson.JsonElement exactFormCanvas = formElement;
            if (formElement.isJsonObject() && formElement.getAsJsonObject().has("form")) {
                exactFormCanvas = formElement.getAsJsonObject().get("form");
            }

            // Serialize only the specific inner form structure
            String formattedFormPayload = gson.toJson(exactFormCanvas);

            Files.write(formFilePath, formattedFormPayload.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.info("[SUCCESS] Serialized custom request form asset directly to destination: {}",
                    formFilePath.toAbsolutePath());

            Files.write(formFilePath, formattedFormPayload.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.info("[SUCCESS] Serialized custom request form asset directly to destination: {}",
                    formFilePath.toAbsolutePath());
        } else {
            logger.warn("No form layout map detected for catalog item '{}'. Skipping form file generation.", name);
        }

        // 3. Convert organization-level sharing IDs to portable organization names
        transformOrganizationSharingsForDisk(item);

        // 4. Write the cleaned core config payload back to original
        // workflows/<Workflow_Name>.json location
        String jsonFilePath = workflowsDirPath + File.separator + name + ".json";
        String formattedJsonPayload = gson.toJson(item);

        Files.write(Paths.get(jsonFilePath), formattedJsonPayload.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        logger.info("Successfully serialized catalog item mapping asset structure directly to flat file: {}",
                jsonFilePath);
    }

    /**
     * Converts portable organization names in organizationSharings to target
     * server orgIds before publish/republish.
     */
    private void resolveOrganizationSharingsForPush(JsonObject payload) throws IOException {
        if (!payload.has("organizationSharings") || payload.get("organizationSharings").isJsonNull()) {
            return;
        }
        JsonArray sharings = payload.getAsJsonArray("organizationSharings");
        if (sharings == null || sharings.size() == 0) {
            payload.remove("organizationSharings");
            return;
        }
        JsonArray resolvedSharings = new JsonArray();
        for (com.google.gson.JsonElement element : sharings) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject sharing = element.getAsJsonObject();
            if (!sharing.has("organization") || sharing.get("organization").isJsonNull()) {
                continue;
            }
            String organizationName = sharing.get("organization").getAsString();
            String orgId;
            if ("ALL".equalsIgnoreCase(organizationName)) {
                orgId = "ALL";
            } else {
                orgId = restClient.getOrganizationId(organizationName);
            }
            JsonObject resolved = new JsonObject();
            resolved.addProperty("orgId", orgId);
            resolvedSharings.add(resolved);
        }
        if (resolvedSharings.size() == 0) {
            payload.remove("organizationSharings");
        } else {
            payload.add("organizationSharings", resolvedSharings);
        }
    }

    /**
     * Converts server orgIds in organizationSharings to portable organization
     * names before writing to disk.
     */
    private void transformOrganizationSharingsForDisk(JsonObject item) throws IOException {
        if (!item.has("organizationSharings") || item.get("organizationSharings").isJsonNull()) {
            return;
        }
        JsonArray sharings = item.getAsJsonArray("organizationSharings");
        if (sharings == null || sharings.size() == 0) {
            item.remove("organizationSharings");
            return;
        }
        JsonArray transformedSharings = new JsonArray();
        for (com.google.gson.JsonElement element : sharings) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject sharing = element.getAsJsonObject();
            if (!sharing.has("orgId") || sharing.get("orgId").isJsonNull()) {
                continue;
            }
            String orgId = sharing.get("orgId").getAsString();
            JsonObject transformed = new JsonObject();
            if ("ALL".equalsIgnoreCase(orgId)) {
                transformed.addProperty("organization", "ALL");
            } else {
                String orgName = restClient.getOrganizationName(orgId);
                transformed.addProperty("organization", orgName);
            }
            transformedSharings.add(transformed);
        }
        if (transformedSharings.size() == 0) {
            item.remove("organizationSharings");
        } else {
            item.add("organizationSharings", transformedSharings);
        }
    }
}
