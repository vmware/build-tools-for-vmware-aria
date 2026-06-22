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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaCatalogItemStore extends AbstractVcfaStore {

    private static final String DIR_CATALOG_ITEMS = "catalog-items";
    private static final String SUBDIR_FORMS = "forms";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaCatalogItemStore() {
        super();
    }

    @Override
    public void exportContent() {
        logger.info("Pulling catalog item configurations from the remote environment...");
        if (restClient == null)
            return;

        try {
            List<VcfaCatalogItem> remoteItems = restClient.getCatalogItems();
            if (remoteItems == null || remoteItems.isEmpty())
                return;

            Package serverPackage = this.vcfaPackage;
            String baseCatalogPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_CATALOG_ITEMS)
                    .toString();
            String formsFolderPath = Paths.get(baseCatalogPath, SUBDIR_FORMS).toString();

            Files.createDirectories(Paths.get(baseCatalogPath));

            for (VcfaCatalogItem item : remoteItems) {
                String trackingName = item.getName();
                if (isExcludedByDescriptor(trackingName))
                    continue;

                this.verifyAssetPathSafety(trackingName, "Catalog Item");
                String catalogFileName = trackingName + ".json";
                // --- SUFFIX MODIFICATION FOR PULL ---
                String formFileName = trackingName + "__FormData.json";

                File jsonFile = Paths.get(baseCatalogPath, catalogFileName).toFile();

                logger.info("Successfully synchronized catalog item asset: {}", jsonFile.getAbsolutePath());
                Files.write(jsonFile.toPath(), mapper.writeValueAsString(item).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                String sourceType = (item.getType() != null) ? String.valueOf(item.getType().get("id")) : null;

                if (sourceType != null) {
                    String targetFormLookupId = item.getId();

                    if ("com.vmw.blueprint".equals(sourceType)) {
                        File packageRootDir = new File(serverPackage.getFilesystemPath());
                        File detailsJsonFile = Paths
                                .get(packageRootDir.getAbsolutePath(), "blueprints", trackingName, "details.json")
                                .toFile();

                        if (detailsJsonFile.exists()) {
                            try {
                                Map<?, ?> detailsData = mapper.readValue(detailsJsonFile, Map.class);
                                if (detailsData != null && detailsData.containsKey("id")) {
                                    targetFormLookupId = String.valueOf(detailsData.get("id"));
                                    logger.info("Extracted accurate Blueprint Template ID from local bundle: {}",
                                            targetFormLookupId);
                                }
                            } catch (Exception e) {
                                logger.warn("Found details.json for blueprint bundle but failed parsing the ID: "
                                        + trackingName, e);
                            }
                        } else {
                            logger.warn(
                                    "Form extraction skipped: Backing blueprint metadata folder not found at path: {}",
                                    detailsJsonFile.getAbsolutePath());
                        }
                    }

                    logger.info("Querying custom request form using resolved source mapping ID: {}",
                            targetFormLookupId);
                    VcfaCatalogItemForm associatedForm = restClient.getCatalogItemForm(sourceType, targetFormLookupId);

                    if (associatedForm != null) {
                        Files.createDirectories(Paths.get(formsFolderPath));
                        File formJsonFile = Paths.get(formsFolderPath, formFileName).toFile();
                        logger.info("Successfully synchronized associated catalog custom form layout: {}",
                                formJsonFile.getAbsolutePath());
                        Files.write(formJsonFile.toPath(),
                                mapper.writeValueAsString(associatedForm).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    } else {
                        logger.info("Catalog item '{}' is using a standard default layout. No customized JSON written.",
                                trackingName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal failure executing catalog export processes", e);
        }
    }

    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing catalog items from {}", sourceDirectory.getAbsolutePath());
        if (restClient == null)
            return;

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_CATALOG_ITEMS).toFile();
        if (!localDir.exists() || !localDir.isDirectory())
            return;

        try {
            List<VcfaCatalogItem> remoteItems = restClient.getCatalogItems();
            File[] itemFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (itemFiles == null || itemFiles.length == 0)
                return;

            File localFormsDir = Paths.get(localDir.getPath(), SUBDIR_FORMS).toFile();

            for (File file : itemFiles) {
                VcfaCatalogItem localItem = mapper.readValue(file, VcfaCatalogItem.class);
                String trackingName = localItem.getName();
                if (isExcludedByDescriptor(trackingName))
                    continue;

                Optional<VcfaCatalogItem> existingRemote = remoteItems.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(localItem.getName()) ||
                                (r.getId() != null && r.getId().equalsIgnoreCase(localItem.getId())))
                        .findFirst();

                String finalCatalogItemId = null;

                if (existingRemote.isPresent()) {
                    VcfaCatalogItem remoteMatch = existingRemote.get();
                    finalCatalogItemId = remoteMatch.getId();
                    if (!isIdentical(remoteMatch, localItem)) {
                        logger.info("Delta detected for Catalog Item '{}'. Processing field replacement updates.",
                                trackingName);
                        restClient.deleteCatalogItem(remoteMatch.getId());
                        VcfaCatalogItem recreated = restClient.createCatalogItem(localItem);
                        if (recreated != null)
                            finalCatalogItemId = recreated.getId();
                    }
                } else {
                    logger.info("Catalog Item '{}' not found on target server. Executing creation flow.", trackingName);
                    localItem.setId(null);
                    VcfaCatalogItem createdItem = restClient.createCatalogItem(localItem);
                    if (createdItem != null)
                        finalCatalogItemId = createdItem.getId();
                }

                // --- REFINED PUSH FORM SYNC LAYER WITH SUFFIX HANDLING ---
                if (finalCatalogItemId != null && localFormsDir.exists()) {
                    String safeBaseName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    String targetFormFileName = safeBaseName + "__FormData.json";
                    File matchingFormFile = Paths.get(localFormsDir.getPath(), targetFormFileName).toFile();

                    if (matchingFormFile.exists()) {
                        VcfaCatalogItemForm localForm = mapper.readValue(matchingFormFile, VcfaCatalogItemForm.class);
                        String sourceType = (localItem.getType() != null)
                                ? String.valueOf(localItem.getType().get("id"))
                                : null;

                        if (sourceType != null) {
                            String formTargetLookupId = finalCatalogItemId;

                            if ("com.vmw.blueprint".equals(sourceType)) {
                                File blueprintDetailsFile = Paths
                                        .get(sourceDirectory.getAbsolutePath(), "blueprints", trackingName,
                                                "details.json")
                                        .toFile();
                                if (blueprintDetailsFile.exists()) {
                                    try {
                                        Map<?, ?> detailsData = mapper.readValue(blueprintDetailsFile, Map.class);
                                        if (detailsData != null && detailsData.containsKey("id")) {
                                            formTargetLookupId = String.valueOf(detailsData.get("id"));
                                            logger.info(
                                                    "Resolved target server Blueprint Source ID for form allocation: {}",
                                                    formTargetLookupId);
                                        }
                                    } catch (Exception e) {
                                        logger.warn(
                                                "Failed parsing target blueprint details.json for form upload context: "
                                                        + trackingName,
                                                e);
                                    }
                                }
                            }

                            logger.info("Syncing matching custom request form layout definitions for item: {}",
                                    trackingName);
                            restClient.updateCatalogItemForm(sourceType, finalCatalogItemId, localForm);
                        }
                    } else {
                        logger.debug(
                                "No customized custom form layout file variant found for name template context: {}",
                                targetFormFileName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to import catalog assets and corresponding custom forms", e);
        }
    }

    private boolean isIdentical(VcfaCatalogItem remote, VcfaCatalogItem local) {
        if (remote == null || local == null)
            return false;
        return java.util.Objects.equals(remote.getName(), local.getName()) &&
                java.util.Objects.equals(remote.getGlobal(), local.getGlobal()) &&
                java.util.Objects.equals(remote.getBulkRequestLimit(), local.getBulkRequestLimit());
    }

    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Catalog Item entities...");
        try {
            List<VcfaCatalogItem> remoteItems = restClient.getCatalogItems();
            if (remoteItems == null || remoteItems.isEmpty())
                return;

            List<String> itemsToDelete = null;
            boolean isExplicitlyEmpty = false;

            File contentYamlFile = new File(System.getProperty("user.dir"), "content.yaml");
            if (contentYamlFile.exists()) {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                try (java.io.InputStream inputStream = new java.io.FileInputStream(contentYamlFile)) {
                    Map<String, Object> rawMap = yaml.load(inputStream);
                    if (rawMap != null) {
                        Object catListObj = rawMap.containsKey("catalog-item") ? rawMap.get("catalog-item")
                                : rawMap.get("catalogItems");
                        if (catListObj instanceof List) {
                            itemsToDelete = (List<String>) catListObj;
                            if (itemsToDelete.isEmpty())
                                isExplicitlyEmpty = true;
                        }
                    }
                }
            }

            if (isExplicitlyEmpty)
                return;

            if (itemsToDelete == null) {
                for (VcfaCatalogItem remoteItem : remoteItems) {
                    restClient.deleteCatalogItem(remoteItem.getId());
                }
                return;
            }

            for (VcfaCatalogItem remoteItem : remoteItems) {
                if (itemsToDelete.contains(remoteItem.getName())) {
                    restClient.deleteCatalogItem(remoteItem.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational error clearing catalog item assets", e);
        }
    }

    private boolean isExcludedByDescriptor(String trackingName) {
        VcfaPackageDescriptor localDescriptor = null;
        if (this.descriptor instanceof VcfaPackageDescriptor) {
            localDescriptor = (VcfaPackageDescriptor) this.descriptor;
        }
        if (localDescriptor == null)
            return false;

        List<String> allowedItems = localDescriptor.getCatalogItem();
        if (allowedItems == null)
            return false;
        if (allowedItems.isEmpty())
            return true;

        return !allowedItems.contains(trackingName);
    }
}
