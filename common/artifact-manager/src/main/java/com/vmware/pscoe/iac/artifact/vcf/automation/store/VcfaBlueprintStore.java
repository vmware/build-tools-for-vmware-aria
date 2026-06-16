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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.filters.CustomFolderFolderFilter;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

/**
 * Handles import, export, and deletion of VcfaBluePrints using
 * framework-injected descriptor context.
 */
public class VcfaBlueprintStore extends AbstractVcfaStore {

    /**
     * Import blueprint contents from the source directory.
     */
    @Override
    public void importContent(File sourceDirectory) {
        File bpFolder = new File(Paths.get(sourceDirectory.getPath(), "blueprints").toString());
        if (!bpFolder.exists()) {
            logger.info("No blueprints available - skip import");
            return;
        }

        File[] bpList = this.filterBasedOnConfiguration(bpFolder, new CustomFolderFolderFilter(null));
        if (bpList == null || bpList.length == 0) {
            logger.info("No blueprints available - skip import");
            return;
        }

        List<VcfaBlueprint> serverBps = fetchServerBlueprints();

        // --- DEFENSIVE SAFEGUARD: Intercept Server-Side Duplicates to Prevent Collision Errors ---
        Map<String, VcfaBlueprint> bpsOnServerByName = new HashMap<>();
        for (VcfaBlueprint bp : serverBps) {
            if (bpsOnServerByName.containsKey(bp.getName())) {
                logger.warn("Target host environment contains multiple distinct blueprint entities sharing the name '{}'. Content updates will target the first matched ID instance.", bp.getName());
            } else {
                bpsOnServerByName.put(bp.getName(), bp);
            }
        }

        for (File bpDir : bpList) {
            String bpName = bpDir.getName();
            if (isExcludedByDescriptor(bpName)) {
                logger.info("Blueprint folder '{}' is excluded by descriptor configuration rules. Skipping.", bpName);
                continue;
            }

            try {
                importBlueprint(bpDir, serverBps);
            } catch (IOException e) {
                logger.error("Failed to import blueprint {}", bpDir.getName(), e);
            }
        }
    }

    /**
     * Export all blueprints from the server to the filesystem.
     */
    @Override
    public void exportContent() {
        ObjectMapper mapper = new ObjectMapper();
        List<VcfaBlueprint> serverBps = fetchServerBlueprints();
        Package serverPackage = this.vcfaPackage;

        // --- DEFENSIVE SAFEGUARD: Track Duplicates Globally to Prevent Local Disk Overwrites ---
        java.util.Set<String> exportedNamesTracker = new java.util.HashSet<>();

        for (VcfaBlueprint bpSummary : serverBps) {
            String name = bpSummary.getName();
            if (isExcludedByDescriptor(name)) {
                logger.info("Blueprint '{}' is excluded by descriptor configuration rules. Skipping.", name);
                continue;
            }

            // Detect naming collision on server and break execution before silent data loss happens
            if (exportedNamesTracker.contains(name)) {
                throw new IllegalStateException("The remote server environment contains multiple cloud templates named '" + name + "'. Automatic export aborted to avoid corrupted local disk tracking files.");
            }
            exportedNamesTracker.add(name);

            try {
                VcfaBlueprint bp = restClient.getBlueprintById(bpSummary.getId());
                saveBlueprintToDisk(bp, serverPackage, mapper);
            } catch (IOException e) {
                logger.error("Unable to export blueprint {}", name, e);
            }
        }
    }

    /**
     * Log deletion info (actual deletion handled elsewhere).
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Blueprint entities...");
        try {
            // Fetch all remote cloud template/blueprint definitions from the target
            // platform
            List<com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint> remoteBlueprints = restClient
                    .getBlueprints();
            if (remoteBlueprints == null || remoteBlueprints.isEmpty()) {
                logger.info("No remote blueprints identified to delete.");
                return;
            }

            List<String> itemsToDelete = null;
            boolean isExplicitlyEmpty = false;

            // Locate and parse content.yaml manually to ensure exact tracking control
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
                        // Accept both singular and plural iterations of the mapping key tokens
                        Object blueprintListObj = rawMap.containsKey("blueprint") ? rawMap.get("blueprint")
                                : rawMap.get("blueprints");

                        if (rawMap.containsKey("blueprint") || rawMap.containsKey("blueprints")) {
                            if (blueprintListObj instanceof List) {
                                itemsToDelete = (List<String>) blueprintListObj;
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
                logger.info("Blueprint descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            // Scenario 2: Key is completely null/omitted -> Wildcard trigger mode active.
            // Delete Everything.
            if (itemsToDelete == null) {
                logger.info(
                        "Blueprint descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote blueprints.");
                for (com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint remoteBP : remoteBlueprints) {
                    logger.info("[WILDCARD DELETE] Deleting blueprint named '{}' matching ID: {}", remoteBP.getName(),
                            remoteBP.getId());
                    restClient.deleteBlueprint(remoteBP.getId());
                }
                return;
            }

            // Scenario 3: Explicit List -> Filter targeted matches sequentially.
            logger.info("Blueprint targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint remoteBP : remoteBlueprints) {
                String remoteName = remoteBP.getName();
                if (itemsToDelete.contains(remoteName)) {
                    logger.info("[TARGETED DELETE] Deleting blueprint named '{}' matching ID: {}", remoteName,
                            remoteBP.getId());
                    restClient.deleteBlueprint(remoteBP.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure blueprint definitions",
                    e);
        }
    }

    private boolean isExcludedByDescriptor(String bpName) {
        com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;

        // 1. Try to use pre-loaded framework descriptor
        if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
            localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
        }

        // 2. Read from targeted execution root directory
        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
                                .getInstance(contentYamlFile);
                    } catch (Exception e) {
                    }
                }
            }
        }

        // 3. Fallback evaluation logic rules matrix
        if (localDescriptor == null) {
            return false;
        }

        // --- DIRECT PROPERTY LOOKUP ---
        List<String> allowedBps = localDescriptor.getBlueprint();
        if (allowedBps == null) {
            return false;
        }

        if (allowedBps.isEmpty()) {
            return true;
        }

        return !allowedBps.contains(bpName);
    }

    /**
     * Import a single blueprint directory, creating or updating as needed.
     * Captures tracking handles to immediately release a version over the wire.
     */
    private void importBlueprint(File bpDir, List<VcfaBlueprint> serverBps) throws IOException {
        String bpName = bpDir.getName();
        VcfaBlueprint bp = loadBlueprintMeta(bpDir, bpName);

        // --- DEFENSIVE SANITY CHECK: Ensure Directory Name Matches Metadata Declaration ---
        if (!Objects.equals(bp.getName(), bpName)) {
            throw new IllegalStateException(String.format("Blueprint workspace corruption identified! Local folder name '%s' does not match the 'name' attribute value declared inside details.json ('%s'). Check configuration.", bpName, bp.getName()));
        }

        setContentIfPresent(bp, bpDir);

        bp.setProjectId(restClient.getProjectId());

        VcfaBlueprint existing = serverBps.stream()
                .filter(m -> bpName.equals(m.getName()))
                .findFirst()
                .orElse(null);

        VcfaBlueprint processingTarget = null;

        if (existing == null) {
            logger.info("Blueprint '{}' not found on target host server. Executing draft creation.", bpName);
            processingTarget = restClient.createBlueprint(bp);
        } else {
            logger.info("Blueprint '{}' found on target host server. Overwriting active working draft.", bpName);
            processingTarget = restClient.updateBlueprint(existing.getId(), bp);
        }

        // --- PIPELINE ADDITION: AUTOMATED VERSION & RELEASE ACTION ---
        if (processingTarget != null && processingTarget.getId() != null) {
            logger.info("Triggering mandatory lifecycle version mapping release sequence for blueprint: {}", bpName);
            restClient.versionBlueprint(processingTarget.getId(), processingTarget.toMap());

            // --- REPRODUCED SYSTEM LOGIC: Unrelease Outdated Historic Versions to Prevent Target Catalog Bloat ---
            if (this.config != null && this.config.getUnreleaseBlueprintVersions()) {
                unreleaseOldVersions(processingTarget.getId());
            }
        } else {
            logger.warn("Skipping blueprint version release sequence: Client did not return a valid structural instance object tracking ID for '{}'.", bpName);
        }
    }

    /**
     * Load blueprint metadata from details.json.
     */
    private VcfaBlueprint loadBlueprintMeta(File bpDir, String bpName) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(new File(bpDir, "details.json")))) {
            return new Gson().fromJson(reader, VcfaBlueprint.class);
        } catch (FileNotFoundException e) {
            logger.error("details.json not found for blueprint {}", bpName);
            throw e;
        }
    }

    /**
     * Set YAML content on blueprint, if content.yaml exists.
     */
    private void setContentIfPresent(VcfaBlueprint bp, File bpDir) throws IOException {
        File contentFile = new File(bpDir, "content.yaml");
        if (contentFile.exists()) {
            String content = Files.readAllLines(contentFile.toPath(), StandardCharsets.UTF_8)
                    .stream().collect(Collectors.joining(System.lineSeparator()));
            bp.setContent(content);
        }
    }

    /**
     * Retrieve all blueprints from server, handle errors.
     */
    private List<VcfaBlueprint> fetchServerBlueprints() {
        try {
            return restClient.getBlueprints();
        } catch (IOException e) {
            logger.error("Unable to fetch blueprints from server", e);
            throw new RuntimeException("Unable to fetch blueprints from server", e);
        }
    }

    /**
     * Write blueprint meta and YAML content to disk.
     */
    private void saveBlueprintToDisk(VcfaBlueprint bp, Package serverPackage, ObjectMapper mapper) throws IOException {
        String blueprintDir = Paths.get(
                new File(serverPackage.getFilesystemPath()).getPath(), "blueprints", bp.getName()).toString();

        File bpFolder = new File(blueprintDir);
        if (!bpFolder.exists()) {
            bpFolder.mkdirs();
        }

        // Write details.json
        String detailsFile = blueprintDir + File.separator + "details.json";

        // --- REPRODUCED SYSTEM LOGIC: Environment Scrubbing Isolation Matrix ---
        // Explicitly extract functional definitions to isolate file configurations from transient read-only system tracking data
        JsonObject filteredDetails = new JsonObject();
        filteredDetails.add("id", new JsonPrimitive(bp.getId() != null ? bp.getId() : ""));
        filteredDetails.add("name", new JsonPrimitive(bp.getName() != null ? bp.getName() : ""));
        filteredDetails.add("description", new JsonPrimitive(bp.getDescription() != null ? bp.getDescription() : ""));
        
        // Dynamic mapping handle verification for execution parameters across different target instances
        if (bp.getRequestScopeOrg() != null) {
            filteredDetails.add("requestScopeOrg", new JsonPrimitive(bp.getRequestScopeOrg()));
        } else if (bp.asExportMap().containsKey("requestScopeOrg")) {
            Object rawScope = bp.asExportMap().get("requestScopeOrg");
            filteredDetails.add("requestScopeOrg", new JsonPrimitive(rawScope != null ? rawScope.toString() : "false"));
        }

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
        String detailsJson = gson.toJson(filteredDetails);

        Files.write(Paths.get(detailsFile), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        // Write content.yaml
        if (bp.getContent() != null) {
            String contentFile = blueprintDir + File.separator + "content.yaml";
            Files.write(Paths.get(contentFile), bp.getContent().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    /**
     * Helper logic to unrelease historical version trees from target servers, leaving only the newest deployment active.
     */
    private void unreleaseOldVersions(String blueprintId) {
        try {
            logger.info("Evaluating historical released version tree to unpublish outdated assets for blueprint ID: {}", blueprintId);
            String rawVersionsJson = restClient.getBlueprintVersions(blueprintId);
            if (rawVersionsJson == null || rawVersionsJson.trim().isEmpty()) {
                return;
            }

            com.google.gson.JsonArray versionsArray = new com.google.gson.Gson().fromJson(rawVersionsJson, com.google.gson.JsonArray.class);
            if (versionsArray == null || versionsArray.size() <= 1) {
                return; // Nothing to unrelease if there's only 1 version total
            }

            // Order array sequentially via creation timestamp
            java.util.List<com.google.gson.JsonElement> orderedList = new java.util.ArrayList<>();
            versionsArray.forEach(orderedList::add);
            orderedList.sort((one, two) -> {
                String dateOne = one.getAsJsonObject().get("createdAt").getAsString();
                String dateTwo = two.getAsJsonObject().get("createdAt").getAsString();
                return java.time.Instant.parse(dateOne).compareTo(java.time.Instant.parse(dateTwo));
            });

            // Retain the newest version at index length-1, unpublish all prior entries
            orderedList.remove(orderedList.size() - 1);

            for (com.google.gson.JsonElement versionElement : orderedList) {
                String versionId = versionElement.getAsJsonObject().get("id").getAsString();
                String versionValue = versionElement.getAsJsonObject().get("version").getAsString();
                logger.info("Unreleasing outdated cloud template version '{}' (Internal ID: {})", versionValue, versionId);
                restClient.unreleaseBlueprintVersion(blueprintId, versionId);
            }
        } catch (Exception e) {
            logger.error("Non-fatal exception encountered during version tree cleanup processing sequence: {}", e.getMessage());
        }
    }
}
