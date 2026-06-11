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
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaContentSource;

public class VcfaContentSourceStore extends AbstractVcfaStore {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing content sources from {}", sourceDirectory.getAbsolutePath());

        File folder = Paths.get(sourceDirectory.getPath(), "content-sources").toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            logger.info("Content sources directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaContentSource> existingSources = restClient.getContentSources();
            Map<String, VcfaContentSource> existingMap = existingSources.stream()
                    .collect(Collectors.toMap(
                            src -> src.getName() != null ? src.getName() : src.getId(),
                            src -> src,
                            (existing, replacement) -> existing));

            File[] sourceFiles = folder.listFiles();
            if (sourceFiles == null || sourceFiles.length == 0) {
                logger.info("Could not find any content source assets to import.");
                return;
            }

            for (File fileOrDir : sourceFiles) {
                // Determine clean asset key based on file styling configurations
                String srcName = fileOrDir.isDirectory() ? fileOrDir.getName()
                        : fileOrDir.getName().replace(".json", "");

                // --- FIXED: Filter out assets based on explicit descriptor mapping exclusions
                // ---
                if (isExcludedByDescriptor(srcName)) {
                    logger.info(
                            "Content source asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            srcName);
                    continue;
                }

                File jsonFile = fileOrDir.isDirectory()
                        ? Paths.get(fileOrDir.getPath(), "details.json").toFile()
                        : fileOrDir;
                if (jsonFile.exists() && jsonFile.getName().endsWith(".json")) {
                    importContentSource(jsonFile, existingMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to list remote environment content sources during import", e);
        }
    }

    private void importContentSource(File jsonFile, Map<String, VcfaContentSource> existingMap) {
        try {
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);
            JsonNode rootNode = mapper.readTree(jsonContent);

            if (rootNode.isObject()) {
                ObjectNode sourceNode = (ObjectNode) rootNode;
                String sourceName = sourceNode.has("name") ? sourceNode.get("name").asText() : "Unknown";

                logger.info("Processing content source: '{}'", sourceName);

                // Handle mapping projectId from global configuration context if needed
                if (sourceNode.has("projectName")) {
                    String projectId = restClient.getProjectId(sourceNode.get("projectName").asText());
                    if (projectId != null) {
                        sourceNode.put("projectId", projectId);
                    }
                    sourceNode.remove("projectName");
                }

                VcfaContentSource subPayload = mapper.treeToValue(sourceNode, VcfaContentSource.class);

                if (existingMap.containsKey(sourceName)) {
                    VcfaContentSource existing = existingMap.get(sourceName);
                    String existingId = existing.getId();
                    subPayload.setId(existingId);

                    // Prevent 405 Method Not Allowed exceptions if the asset matches the server
                    // completely
                    if (isIdentical(existing, subPayload)) {
                        logger.info("Content source '{}' is already up to date. Skipping update operation.",
                                sourceName);
                        return;
                    }

                    logger.info("Updating existing content source: '{}' (ID: {})", sourceName, existingId);
                    restClient.updateContentSource(existingId, subPayload);
                } else {
                    restClient.createContentSource(subPayload);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error handling content source JSON data from file: " + jsonFile.getPath(), e);
        } catch (Exception e) {
            logger.error("Failed to evaluate and push content source asset: {}", jsonFile.getName(), e);
        }
    }

    private boolean isIdentical(VcfaContentSource remote, VcfaContentSource local) {
        if (remote == null || local == null) {
            return false;
        }

        // Extract type IDs to compare strings securely rather than object instances
        String remoteTypeId = (remote.getType() != null) ? remote.getType().getId() : null;
        String localTypeId = (local.getType() != null) ? local.getType().getId() : null;

        boolean sameType = java.util.Objects.equals(remoteTypeId, localTypeId);
        boolean sameProject = java.util.Objects.equals(remote.getProjectId(), local.getProjectId());
        boolean sameConfig = java.util.Objects.equals(remote.getConfig(), local.getConfig());
        
        // NEW: Check content-item specific descriptors
        boolean sameName = java.util.Objects.equals(remote.getName(), local.getName());
        boolean sameDescription = java.util.Objects.equals(remote.getDescription(), local.getDescription());
        boolean sameGlobal = java.util.Objects.equals(remote.getGlobal(), local.getGlobal());
        boolean sameRequestable = java.util.Objects.equals(remote.getIsRequestable(), local.getIsRequestable());

        return sameType && sameProject && sameConfig && sameName && sameDescription && sameGlobal && sameRequestable;
    }

    @Override
    public void exportContent() {
        logger.info("Pulling content source configurations from the remote environment...");
        try {
            List<VcfaContentSource> items = restClient.getContentSources();
            if (items == null || items.isEmpty()) {
                logger.info("No remote content sources found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseOutPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), "content-sources")
                    .toString();

            for (VcfaContentSource item : items) {
                String name = item.getName() != null ? item.getName() : item.getId();

                // --- FIXED: Skip execution if the explicit server item is excluded by filter
                // matrix rules ---
                if (isExcludedByDescriptor(name)) {
                    logger.info("Content source '{}' is excluded by descriptor configuration rules. Skipping export.",
                            name);
                    continue;
                }

                File flatFile = Paths.get(baseOutPath, name + ".json").toFile();
                File nestedDir = Paths.get(baseOutPath, name).toFile();
                String finalTargetFilePath;

                if (nestedDir.exists() && nestedDir.isDirectory()) {
                    finalTargetFilePath = Paths.get(nestedDir.getPath(), "details.json").toString();
                } else if (flatFile.exists()) {
                    finalTargetFilePath = flatFile.getPath();
                } else {
                    Files.createDirectories(Paths.get(baseOutPath));
                    finalTargetFilePath = flatFile.getPath();
                }

                try {
                    ObjectNode exportNode = mapper.valueToTree(item);
                    String sanitizedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportNode);
                    Files.write(
                            Paths.get(finalTargetFilePath),
                            sanitizedJson.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                    logger.info("Successfully synchronized content source asset: {}", finalTargetFilePath);
                } catch (IOException e) {
                    logger.error("Unable to write synchronized content source file: {}", name, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operation handling export execution for content sources", e);
        }
    }

    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Content Source entities...");
        try {
            List<VcfaContentSource> remoteSources = restClient.getContentSources();
            if (remoteSources == null || remoteSources.isEmpty())
                return;

            com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;
            if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
                localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
            }

            if (localDescriptor == null) {
                String workingDir = System.getProperty("user.dir");
                if (workingDir != null) {
                    File contentYamlFile = new File(workingDir, "content.yaml");
                    if (contentYamlFile.exists()) {
                        localDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
                                .getInstance(contentYamlFile);
                    }
                }
            }

            if (localDescriptor == null || localDescriptor.getContentSource() == null)
                return;

            List<String> itemsToDelete = localDescriptor.getContentSource();
            if (itemsToDelete == null || itemsToDelete.isEmpty())
                return;

            for (VcfaContentSource remote : remoteSources) {
                if (itemsToDelete.contains(remote.getName())) {
                    logger.info("Deleting content source '{}' matching ID: {}", remote.getName(), remote.getId());
                    restClient.deleteContentSource(remote.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing content source definitions", e);
        }
    }

    /**
     * Evaluates content-source filtering matrix logic.
     * null / Undefined property -> returns false (Downloads everything)
     * Empty Array [] -> returns true (Skips everything)
     * Named Values List -> matches items securely against containment checks
     */
    private boolean isExcludedByDescriptor(String contentSourceName) {
        com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;

        // 1. Framework context instance conversion fallback
        if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
            localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
        }

        // 2. Real command execution location path tracking
        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
                                .getInstance(contentYamlFile);
                    } catch (Exception e) {
                        logger.error("Error evaluating content.yaml descriptor configuration paths context", e);
                    }
                }
            }
        }

        // 3. Fallback evaluation logic execution loop mapping rules
        if (localDescriptor == null) {
            return false; // File missing: Process/Download everything
        }

        List<String> allowedSources = localDescriptor.getContentSource();

        // --- SEPARATED EVALUATION MATRIX STATE ENGINE ---
        // State A: Undefined / Left Blank in YAML (null) -> Wildcard download all
        // elements
        if (allowedSources == null) {
            return false;
        }

        // State B: Explicitly empty array node specified [] -> Deactivate pipeline
        // completely
        if (allowedSources.isEmpty()) {
            return true;
        }

        // State C: Populated items tracking list -> Exclude if item isn't in the list
        return !allowedSources.contains(contentSourceName);
    }
}
