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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.filters.CustomFolderFolderFilter;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaDescriptorHelper;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.OrganizationSharing;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

/**
 * Handles import, export, and deletion of VcfaBluePrints using
 * framework-injected descriptor context.
 */
public class VcfaBlueprintStore extends AbstractVcfaStore {
    private static final String DIR_BLUEPRINTS = "blueprints";

    /**
     * Import blueprint contents from the source directory.
     */
    @Override
    public void importContent(File sourceDirectory) {
        File bpFolder = Paths.get(sourceDirectory.getPath(), DIR_BLUEPRINTS).toFile();

        // --- NEW: COUPLING WORKSPACE DEPLOYMENT ACCURACY VALIDATION ---
        VcfaDescriptorHelper.validateAssetsPresent(this.vcfaPackage, bpFolder, "Blueprint", true, "blueprint",
                "blueprints");

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

        Map<String, VcfaBlueprint> bpsOnServerByName = new HashMap<>();
        for (VcfaBlueprint bp : serverBps) {
            if (bpsOnServerByName.containsKey(bp.getName())) {
                logger.warn(
                        "Target host environment contains multiple distinct blueprint entities sharing the name '{}'. Content updates will target the first matched ID instance.",
                        bp.getName());
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

        java.util.Set<String> exportedNamesTracker = new java.util.HashSet<>();

        for (VcfaBlueprint bpSummary : serverBps) {
            String name = bpSummary.getName();
            if (isExcludedByDescriptor(name)) {
                logger.info("Blueprint '{}' is excluded by descriptor configuration rules. Skipping.", name);
                continue;
            }

            if (exportedNamesTracker.contains(name)) {
                throw new IllegalStateException(
                        "The remote server environment contains multiple cloud templates named '" + name
                                + "'. Automatic export aborted to avoid corrupted local disk tracking files.");
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
     * Wipes remote infrastructure blueprints based on Tristate descriptor rules.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Blueprint entities...");
        try {
            List<VcfaBlueprint> remoteBlueprints = restClient.getBlueprints();
            if (remoteBlueprints == null || remoteBlueprints.isEmpty()) {
                logger.info("No remote blueprints identified to delete.");
                return;
            }

            // --- STREAMLINED: FETCH DELETION TARGETS VIA SHARED UTILITY ---
            List<String> itemsToDelete = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "blueprint",
                    "blueprints");

            if (itemsToDelete == null) {
                logger.info(
                        "Blueprint descriptor is undefined/null. Wildcard trigger active: Purging ALL remote blueprints.");
                for (VcfaBlueprint bp : remoteBlueprints) {
                    logger.info("[WILDCARD DELETE] Deleting blueprint named '{}'", bp.getName());
                    restClient.deleteBlueprint(bp.getId());
                }
                return;
            }

            if (itemsToDelete.isEmpty()) {
                logger.info("Blueprint descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            logger.info("Blueprint targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaBlueprint bp : remoteBlueprints) {
                String name = bp.getName();
                if (itemsToDelete.contains(name)) {
                    logger.info("[TARGETED DELETE] Deleting blueprint named '{}'", name);
                    restClient.deleteBlueprint(bp.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure blueprint definitions",
                    e);
        }
    }

    /**
     * Streamlined local exclusion rule evaluator.
     */
    private boolean isExcludedByDescriptor(String bpName) {
        List<String> allowedBps = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "blueprint", "blueprints");

        if (allowedBps == null) {
            return false; // Wildcard active, nothing is excluded
        }

        if (allowedBps.isEmpty()) {
            return true; // Explicitly empty means exclude everything
        }

        return !allowedBps.contains(bpName);
    }

    /**
     * Import a single blueprint directory, creating or updating as needed.
     * Defers the final lifecycle release gate to process form mutations.
     */
    private void importBlueprint(File bpDir, List<VcfaBlueprint> serverBps) throws IOException {
        String bpName = bpDir.getName();
        ObjectMapper mapper = new ObjectMapper();
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();

        VcfaBlueprint bp = loadBlueprintMeta(bpDir, bpName);

        if (!Objects.equals(bp.getName(), bpName)) {
            throw new IllegalStateException(String.format(
                    "Blueprint workspace corruption identified! Local folder name '%s' does not match the 'name' attribute value declared inside details.json ('%s'). Check configuration.",
                    bpName, bp.getName()));
        }

        setContentIfPresent(bp, bpDir);
        bp.setProjectId(restClient.getProjectId());

        List<OrganizationSharing> organizationSharings = bp.getOrganizationSharings();

        if (organizationSharings != null) {
            for (OrganizationSharing sharing: organizationSharings) {
                if (sharing.getOrganization().equalsIgnoreCase("ALL")) {
                    sharing.setOrgId("ALL");
                } else {
                    sharing.setOrgId(restClient.getOrganizationId(sharing.getOrganization()));
                }
            }
        }

        VcfaBlueprint existing = serverBps.stream()
                .filter(m -> bpName.equals(m.getName()))
                .findFirst()
                .orElse(null);

        VcfaBlueprint processingTarget = null;
        boolean shouldReleaseVersion = false;

        if (existing == null) {
            logger.info("Blueprint '{}' not found on target host server. Executing draft creation.", bpName);
            processingTarget = restClient.createBlueprint(bp);
            shouldReleaseVersion = true;
        } else {
            VcfaBlueprint fullServerBp = restClient.getBlueprintById(existing.getId());

            String localContent = bp.getContent() != null ? bp.getContent().trim().replace("\r\n", "\n") : "";
            String serverContent = (fullServerBp != null && fullServerBp.getContent() != null)
                    ? fullServerBp.getContent().trim().replace("\r\n", "\n")
                    : "";
            Set<String> localOrgSharings = bp.getOrganizationSharings() == null 
                    ? new HashSet<>() 
                    : bp.getOrganizationSharings().stream().map(OrganizationSharing::getOrgId).collect(Collectors.toSet());
            Set<String> serverOrgSharings = (fullServerBp == null || fullServerBp.getOrganizationSharings() == null)
                    ? new HashSet<>() 
                    : fullServerBp.getOrganizationSharings().stream().map(OrganizationSharing::getOrgId).collect(Collectors.toSet());

            if (localContent.equals(serverContent) 
                    && localOrgSharings.equals(serverOrgSharings) 
                    && Objects.equals(bp.getRequestScopeOrg(), fullServerBp.getRequestScopeOrg())) {
                logger.info(
                        "Blueprint '{}' working draft content matches server content exactly. Checking custom request forms...",
                        bpName);
                processingTarget = fullServerBp;
            } else {
                logger.info(
                        "Blueprint '{}' found on target host server with content changes. Overwriting active working draft.",
                        bpName);
                processingTarget = restClient.updateBlueprint(existing.getId(), bp);
                shouldReleaseVersion = true;
            }
        }

        String blueprintId = (processingTarget != null) ? processingTarget.getId()
                : (existing != null ? existing.getId() : null);

        if (blueprintId != null) {
            // --- CUSTOM REQUEST FORM PROCESSING BLOCK ---
            String formFileName = bpName + "__FormData.json";
            File formDataFile = new File(bpDir, formFileName);
            File stylesFile = new File(bpDir, "styles.css");

            if (formDataFile.exists()) {
                logger.info("Custom request form artifact layout definition found: '{}'. Evaluating variations...",
                        formFileName);
                try {
                    String formFileContent = new String(java.nio.file.Files.readAllBytes(formDataFile.toPath()),
                            java.nio.charset.StandardCharsets.UTF_8);

                    JsonObject formWrapperElement = com.google.gson.JsonParser.parseString(formFileContent)
                            .getAsJsonObject();

                    String localFormJsonNormalized = gson.toJson(formWrapperElement);
                    Map<String, Object> localFormPayload = gson.fromJson(localFormJsonNormalized, Map.class);
                    boolean formChanged = true;
                    try {
                        Object rawFormResponse = restClient.getCatalogItemForm("com.vmw.blueprint", blueprintId);
                        if (rawFormResponse != null) {
                            Map<String, Object> formMetaMap = mapper.convertValue(rawFormResponse, Map.class);
                            if (formMetaMap != null && formMetaMap.containsKey("form")
                                    && formMetaMap.get("form") != null) {
                                Object serverFormObj = formMetaMap.get("form");
                                String serverFormJsonNormalized = "";

                                if (serverFormObj instanceof String) {
                                    serverFormJsonNormalized = gson
                                            .toJson(com.google.gson.JsonParser.parseString((String) serverFormObj));
                                } else {
                                    serverFormJsonNormalized = gson.toJson(com.google.gson.JsonParser
                                            .parseString(mapper.writeValueAsString(serverFormObj)));
                                }

                                String serverStyles = formMetaMap.containsKey("styles")
                                        && formMetaMap.get("styles") != null
                                                ? formMetaMap.get("styles").toString().trim()
                                                : "";
                                String localCssContent = "";
                                if (stylesFile.exists()) {
                                    localCssContent = new String(java.nio.file.Files.readAllBytes(stylesFile.toPath()),
                                            java.nio.charset.StandardCharsets.UTF_8).trim();
                                }

                                if (localFormJsonNormalized.equals(serverFormJsonNormalized)
                                        && localCssContent.equals(serverStyles)) {
                                    logger.info(
                                            "Custom request form layouts and sibling styles match perfectly on server for blueprint '{}'. Skipping redundant updates.",
                                            bpName);
                                    formChanged = false;
                                }
                            }
                        }
                    } catch (Exception fe) {
                        logger.info("No active form found on server or unable to parse. Treating form as updated/new.");
                    }

                    if (formChanged) {
                        logger.info(
                                "Form configuration updates detected for blueprint '{}'. Triggering updates sequence...",
                                bpName);

                        String cssStylesPayload = "";
                        if (stylesFile.exists()) {
                            cssStylesPayload = new String(java.nio.file.Files.readAllBytes(stylesFile.toPath()),
                                    java.nio.charset.StandardCharsets.UTF_8).trim();
                        }

                        restClient.createBlueprintForm(
                                blueprintId,
                                localFormPayload,
                                bp.getContent(),
                                bp.getName(),
                                bp.getDescription(),
                                bp.getRequestScopeOrg(),
                                cssStylesPayload);

                        logger.info(
                                "Successfully bound updated custom request form configurations to Blueprint entity '{}'.",
                                bpName);
                        shouldReleaseVersion = true;
                    }

                } catch (Exception e) {
                    logger.error("Failed to compile custom request form lifecycle sequence for blueprint: " + bpName,
                            e);
                }
            } else {
                logger.info("No custom request form file layout ('{}') found for asset workspace. Skipping form sync.",
                        formFileName);
            }

            // --- FINAL DEFERRED RELEASE SEQUENCE GATE ---
            if (shouldReleaseVersion) {
                logger.info("Triggering lifecycle version mapping release sequence for blueprint: {}",
                        bpName);
                try {
                    // Only pass styles if the file exists and is populated
                    if (stylesFile.exists()) {
                        String localCssContent = new String(java.nio.file.Files.readAllBytes(stylesFile.toPath()),
                                java.nio.charset.StandardCharsets.UTF_8).trim();
                        if (!localCssContent.isEmpty()) {
                            processingTarget.setStyles(localCssContent);
                        }
                    }

                    restClient.versionBlueprint(blueprintId, processingTarget.toMap());

                    if (this.config != null && this.config.getUnreleaseBlueprintVersions()) {
                        unreleaseOldVersions(blueprintId);
                    }
                } catch (Exception e) {
                    logger.warn(
                            "Blueprint '{}' draft was created/updated, but versioning failed (content validation error on target). Blueprint remains in draft state: {}",
                            bpName, e.getMessage());
                }
            } else {
                logger.info(
                        "Skipped redundant cloud template version creation for blueprint asset '{}' (Blueprint and Form are up-to-date).",
                        bpName);
            }
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
     * Write blueprint meta, YAML content, and server-side custom request forms to
     * disk.
     * Handles automatic deletion of forms and stylesheet layouts if they are
     * missing from the server.
     */
    private void saveBlueprintToDisk(VcfaBlueprint bp, Package serverPackage, ObjectMapper mapper) throws IOException {
        String blueprintDir = Paths.get(
                new File(serverPackage.getFilesystemPath()).getPath(), DIR_BLUEPRINTS, bp.getName()).toString();

        File bpFolder = new File(blueprintDir);
        if (!bpFolder.exists()) {
            bpFolder.mkdirs();
        }

        String detailsFile = blueprintDir + File.separator + "details.json";
        JsonObject filteredDetails = new JsonObject();
        filteredDetails.add("id", new JsonPrimitive(bp.getId() != null ? bp.getId() : ""));
        filteredDetails.add("name", new JsonPrimitive(bp.getName() != null ? bp.getName() : ""));
        filteredDetails.add("description", new JsonPrimitive(bp.getDescription() != null ? bp.getDescription() : ""));

        if (bp.getRequestScopeOrg() != null) {
            filteredDetails.add("requestScopeOrg", new JsonPrimitive(bp.getRequestScopeOrg()));
        } else if (bp.asExportMap().containsKey("requestScopeOrg")) {
            Object rawScope = bp.asExportMap().get("requestScopeOrg");
            filteredDetails.add("requestScopeOrg", new JsonPrimitive(rawScope != null ? rawScope.toString() : "false"));
        }
        
        List<OrganizationSharing> organizationSharings = bp.getOrganizationSharings();

        if (organizationSharings != null) {
            JsonArray transformedSharings = new JsonArray();
            for (OrganizationSharing sharing: organizationSharings) {
                JsonObject transformedSharing = new JsonObject();
                if (sharing.getOrgId().equalsIgnoreCase("ALL")) {
                    transformedSharing.add("organization", new JsonPrimitive("ALL"));
                } else {
                    transformedSharing.add("organization", new JsonPrimitive(restClient.getOrganizationName(sharing.getOrgId())));
                }
                transformedSharings.add(transformedSharing);
            }
            if (transformedSharings.size() > 0) {
                filteredDetails.add("organizationSharings", transformedSharings);
            }
        }

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setLenient().setPrettyPrinting().serializeNulls()
                .create();
        String detailsJson = gson.toJson(filteredDetails);
        Files.write(Paths.get(detailsFile), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        if (bp.getContent() != null) {
            String contentFile = blueprintDir + File.separator + "content.yaml";
            Files.write(Paths.get(contentFile), bp.getContent().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        // Target references for form asset management
        String formFileName = bp.getName() + "__FormData.json";
        File formFile = new File(blueprintDir, formFileName);
        File stylesFile = new File(blueprintDir, "styles.css");
        boolean formExistsOnServer = false;

        try {
            this.verifyAssetPathSafety(bp.getName(), "Blueprint");
            logger.info("Evaluating custom request form availability on server for blueprint: {}", bp.getName());

            Object rawFormResponse = restClient.getCatalogItemForm("com.vmw.blueprint", bp.getId());

            if (rawFormResponse != null) {
                Map<String, Object> formMetaMap = mapper.convertValue(rawFormResponse, Map.class);

                if (formMetaMap != null && formMetaMap.containsKey("form") && formMetaMap.get("form") != null) {
                    formExistsOnServer = true;
                    logger.info(
                            "Custom request form discovered for blueprint '{}'. Generating artifact payload layout...",
                            bp.getName());

                    Object internalFormObj = formMetaMap.get("form");
                    JsonObject formElement = null;

                    if (internalFormObj instanceof String) {
                        formElement = com.google.gson.JsonParser.parseString((String) internalFormObj)
                                .getAsJsonObject();
                    } else {
                        String rawJson = mapper.writeValueAsString(internalFormObj);
                        formElement = com.google.gson.JsonParser.parseString(rawJson).getAsJsonObject();
                    }

                    String cssStyles = "";
                    if (formMetaMap.containsKey("styles") && formMetaMap.get("styles") != null) {
                        cssStyles = formMetaMap.get("styles").toString();
                    }

                    String serializedFormJson = gson.toJson(formElement);

                    // Save custom form metadata schema
                    Files.write(formFile.toPath(),
                            serializedFormJson.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);

                    // Save styles.css layout configuration
                    Files.write(stylesFile.toPath(),
                            cssStyles.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);

                    logger.info("Successfully exported custom request form layout file asset: {} and styles.css",
                            formFileName);
                }
            }
        } catch (Exception e) {
            logger.warn("Non-fatal exception encountered pulling request form metadata allocations for '{}': {}",
                    bp.getName(), e.getMessage());
        }

        // Sync local filesystem drift if the server returned no active form
        // configuration
        if (!formExistsOnServer) {
            logger.info(
                    "No customized form layout metadata found on the server for blueprint '{}'. Processing workspace cleanup...",
                    bp.getName());

            // Track the real project root directory where the command was executed
            String realProjectWorkspace = System.getProperty("user.dir");

            // Build a list of potential targets to check (both temp directory and actual
            // local workspace layouts)
            java.util.List<File> formFilesToPurge = java.util.Arrays.asList(
                    formFile, // Temporary directory path
                    java.nio.file.Paths.get(realProjectWorkspace, "blueprints", bp.getName(), formFileName).toFile(), // Local
                                                                                                                      // flat
                                                                                                                      // workspace
                                                                                                                      // layout
                    java.nio.file.Paths.get(realProjectWorkspace, "src", "main", "resources", "blueprints",
                            bp.getName(), formFileName).toFile() // Standard Maven source layout
            );

            java.util.List<File> stylesFilesToPurge = java.util.Arrays.asList(
                    stylesFile, // Temporary directory path
                    java.nio.file.Paths.get(realProjectWorkspace, "blueprints", bp.getName(), "styles.css").toFile(),
                    java.nio.file.Paths.get(realProjectWorkspace, "src", "main", "resources", "blueprints",
                            bp.getName(), "styles.css").toFile());

            // Scan and destroy form file targets across directories
            for (File f : formFilesToPurge) {
                if (f.exists()) {
                    logger.warn("Custom request form file is orphaned. Purging from disk: {}", f.getAbsolutePath());
                    if (f.delete()) {
                        logger.info("Successfully removed orphaned form file.");
                    } else {
                        logger.error("Failed to delete form file asset: {}", f.getAbsolutePath());
                    }
                }
            }

            // Scan and destroy matching stylesheet targets across directories
            for (File s : stylesFilesToPurge) {
                if (s.exists()) {
                    logger.warn("Custom styles file 'styles.css' is orphaned. Purging from disk: {}",
                            s.getAbsolutePath());
                    if (s.delete()) {
                        logger.info("Successfully removed orphaned styles file.");
                    } else {
                        logger.error("Failed to delete styles file asset: {}", s.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Helper logic to unrelease historical version trees from target servers.
     */
    private void unreleaseOldVersions(String blueprintId) {
        try {
            logger.info("Evaluating historical released version tree to unpublish outdated assets for blueprint ID: {}",
                    blueprintId);
            String rawVersionsJson = restClient.getBlueprintVersions(blueprintId);
            if (rawVersionsJson == null || rawVersionsJson.trim().isEmpty()) {
                return;
            }
            com.google.gson.JsonObject versionsObj = new com.google.gson.Gson().fromJson(rawVersionsJson,
                    com.google.gson.JsonObject.class);
            com.google.gson.JsonArray versionsArray = versionsObj.has("content") ? versionsObj.getAsJsonArray("content")
                    : new com.google.gson.JsonArray();
            if (versionsArray == null || versionsArray.size() <= 1) {
                return;
            }

            java.util.List<com.google.gson.JsonElement> orderedList = new java.util.ArrayList<>();
            versionsArray.forEach(orderedList::add);
            orderedList.sort((one, two) -> {
                String dateOne = one.getAsJsonObject().get("createdAt").getAsString();
                String dateTwo = two.getAsJsonObject().get("createdAt").getAsString();
                return java.time.Instant.parse(dateOne).compareTo(java.time.Instant.parse(dateTwo));
            });

            orderedList.remove(orderedList.size() - 1);

            for (com.google.gson.JsonElement versionElement : orderedList) {
                com.google.gson.JsonObject versionObj = versionElement.getAsJsonObject();
                String versionId = versionObj.get("id").getAsString();
                String versionValue = versionObj.get("version").getAsString();

                boolean isReleased = true;
                if (versionObj.has("released")) {
                    isReleased = versionObj.get("released").getAsBoolean();
                } else if (versionObj.has("status")) {
                    isReleased = "RELEASED".equalsIgnoreCase(versionObj.get("status").getAsString());
                }

                if (!isReleased) {
                    logger.info(
                            "Cloud template version '{}' (Internal ID: {}) is already unpublished. Skipping request.",
                            versionValue, versionId);
                    continue;
                }

                logger.info("Unreleasing outdated cloud template version '{}' (Internal ID: {})", versionValue,
                        versionId);
                restClient.unreleaseBlueprintVersion(blueprintId, versionId);
            }
        } catch (Exception e) {
            logger.error("Non-fatal exception encountered during version tree cleanup processing sequence: {}",
                    e.getMessage());
        }
    }
}
