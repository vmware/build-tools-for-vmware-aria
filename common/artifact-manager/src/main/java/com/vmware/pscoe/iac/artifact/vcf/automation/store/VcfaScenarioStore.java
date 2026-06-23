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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaScenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;

public class VcfaScenarioStore extends AbstractVcfaStore {

    private static final String DIR_SCENARIOS = "scenarios";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaScenarioStore() {
        super();
    }

    /**
     * Exports Scenario settings from target environment to local package
     * filesystem.
     */
    @Override
    public void exportContent() {
        logger.info("Pulling scenario configurations from the remote environment...");

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Scenario descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in Scenario Store. Skipping export.");
            return;
        }

        try {
            List<VcfaScenario> remoteScenarios = restClient.getScenarios();
            if (remoteScenarios == null || remoteScenarios.isEmpty()) {
                logger.info("No remote Scenarios found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseScenariosPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_SCENARIOS)
                    .toString();
            Files.createDirectories(Paths.get(baseScenariosPath));

            for (VcfaScenario scenario : remoteScenarios) {
                String trackingName = scenario.getName(); // scenarioName

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Scenario '{}' is excluded by descriptor rules. Skipping export.", trackingName);
                    continue;
                }

                // --- CHANGED LOGIC: Preserve the exact scenario name for the filename ---
                // Only strips characters that are strictly illegal across all major OS
                // filesystems (\ / : * ? " < > |)
                this.verifyAssetPathSafety(trackingName, "Scenatio");

                // Create individual subfolder based on the scenario display name
                File scenarioFolder = Paths.get(baseScenariosPath, trackingName).toFile();
                if (!scenarioFolder.exists()) {
                    scenarioFolder.mkdirs();
                }

                // --- REPRODUCED SYSTEM LOGIC: Align underlying payload schema maps with native
                // keys ---
                ObjectNode jsonNode = mapper.valueToTree(scenario);
                if (!jsonNode.has("scenarioName") && scenario.getName() != null) {
                    jsonNode.put("scenarioName", scenario.getName());
                }
                if (!jsonNode.has("scenarioId") && scenario.getId() != null) {
                    jsonNode.put("scenarioId", scenario.getId());
                }

                // Extract HTML content body if present to save separately
                String htmlBody = "";
                if (jsonNode.has("body") && !jsonNode.get("body").isNull()) {
                    htmlBody = jsonNode.get("body").asText();
                    jsonNode.remove("body"); // Strip from metadata details.json file
                }

                // File 1: details.json (Metadata only)
                File detailsFile = Paths.get(scenarioFolder.getPath(), "details.json").toFile();
                String serializedJson = mapper.writeValueAsString(jsonNode);
                Files.write(
                        detailsFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                // File 2: template.html (HTML payload content body)
                File htmlFile = Paths.get(scenarioFolder.getPath(), "template.html").toFile();
                Files.write(
                        htmlFile.toPath(),
                        htmlBody.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                logger.info("Successfully synchronized scenario folder asset layout: {}",
                        scenarioFolder.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing scenario sync export stream", e);
        }
    }

    /**
     * Imports Scenario configurations back to the target environment.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing scenario from {}", sourceDirectory.getAbsolutePath());

        // --- OPTIMIZATION STEP: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Scenario descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in Scenario Store. Skipping import.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_SCENARIOS).toFile();
        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Scenarios directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaScenario> remoteScenarios = restClient.getScenarios();
            File[] scenarioFolders = localDir.listFiles(File::isDirectory);
            if (scenarioFolders == null || scenarioFolders.length == 0) {
                logger.info("Could not find any scenario folders to import.");
                return;
            }

            for (File folder : scenarioFolders) {
                File detailsFile = new File(folder, "details.json");
                File htmlFile = new File(folder, "template.html");

                // Enforce mandatory file check inside workspace layout folder
                if (!detailsFile.exists()) {
                    logger.warn("Skipping directory {}. Root details.json could not be resolved.", folder.getPath());
                    continue;
                }

                // --- REPRODUCED SYSTEM LOGIC: Process file using flexible schema token
                // fallbacks ---
                String jsonContent = new String(Files.readAllBytes(detailsFile.toPath()), StandardCharsets.UTF_8);
                ObjectNode rootNode = (ObjectNode) mapper.readTree(jsonContent);

                // Read and re-stitch HTML content body back into payload map if it exists
                if (htmlFile.exists()) {
                    String htmlContent = new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8);
                    rootNode.put("body", htmlContent);
                }

                String trackingName = rootNode.has("scenarioName") ? rootNode.get("scenarioName").asText() : null;
                String trackingId = rootNode.has("scenarioId") ? rootNode.get("scenarioId").asText() : null;

                VcfaScenario localScenario = mapper.treeToValue(rootNode, VcfaScenario.class);
                if (localScenario.getName() == null && trackingName != null) {
                    localScenario.setName(trackingName);
                }
                if (localScenario.getId() == null && trackingId != null) {
                    localScenario.setId(trackingId);
                }

                if (localScenario.getName() == null) {
                    logger.warn(
                            "Unable to extract valid target name context identifier for folder asset {}. Skipping import.",
                            folder.getName());
                    continue;
                }

                String actualName = localScenario.getName();
                if (isExcludedByDescriptor(actualName)) {
                    logger.info("Scenario asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            actualName);
                    continue;
                }

                logger.info("Processing local Scenario asset folder layout: '{}'", folder.getName());

                Optional<VcfaScenario> existingRemote = remoteScenarios.stream()
                        .filter(r -> r.getName().equalsIgnoreCase(localScenario.getName()) ||
                                (r.getId() != null && localScenario.getId() != null
                                        && r.getId().equalsIgnoreCase(localScenario.getId())))
                        .findFirst();

                if (existingRemote.isPresent()) {
                    VcfaScenario remoteMatch = existingRemote.get();
                    if (isIdentical(remoteMatch, localScenario)) {
                        logger.info("Scenario '{}' matches remote system configuration exactly. Skipping update.",
                                actualName);
                    } else {
                        logger.info("Delta detected for Scenario '{}'. Executing replacement update lifecycle.",
                                actualName);
                        restClient.deleteScenario(remoteMatch.getId());
                        restClient.createScenario(mapper.writeValueAsString(rootNode));
                    }
                } else {
                    logger.info("Scenario '{}' not found on target server. Executing remote creation.", actualName);
                    restClient.createScenario(mapper.writeValueAsString(rootNode));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to import Scenarios from filesystem to target environment", e);
        }
    }

    private boolean isIdentical(VcfaScenario remote, VcfaScenario local) {
        if (remote == null || local == null) {
            return false;
        }
        boolean sameEnabled = java.util.Objects.equals(remote.getEnabled(), local.getEnabled());
        boolean sameCategory = java.util.Objects.equals(remote.getScenarioCategory(), local.getScenarioCategory());
        boolean sameName = java.util.Objects.equals(remote.getName(), local.getName());
        boolean sameBody = java.util.Objects.equals(remote.getBody(), local.getBody());

        return sameEnabled && sameCategory && sameName && sameBody;
    }

    /**
     * Executes Tristate scanning logic for cleanup tasks.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Scenario entities...");
        try {
            List<VcfaScenario> remoteScenarios = restClient.getScenarios();
            if (remoteScenarios == null || remoteScenarios.isEmpty()) {
                logger.info("No remote scenario identified to delete.");
                return;
            }

            List<String> itemsToDelete = null;
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
                        // --- FIXED: Fetch the value using .get() instead of evaluating containsKey()
                        // to a Boolean ---
                        Object scenarioListObj = rawMap.containsKey("scenarios") ? rawMap.get("scenarios")
                                : rawMap.get("scenario");

                        if (rawMap.containsKey("scenarios") || rawMap.containsKey("scenario")) {
                            if (scenarioListObj instanceof List) {
                                itemsToDelete = (List<String>) scenarioListObj;
                                if (itemsToDelete.isEmpty()) {
                                    isExplicitlyEmpty = true;
                                }
                            }
                        }
                    }
                }
            }

            // --- TRISTATE EVALUATION ---
            if (isExplicitlyEmpty) {
                logger.info("Scenario descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            if (itemsToDelete == null) {
                logger.info("Scenario descriptor is undefined/null. Purging ALL remote scenario.");
                for (VcfaScenario remoteScen : remoteScenarios) {
                    logger.info("[WILDCARD DELETE] Deleting scenario named '{}' matching ID: {}", remoteScen.getName(),
                            remoteScen.getId());
                    restClient.deleteScenario(remoteScen.getId());
                }
                return;
            }

            logger.info("Scenario targeted filter list active. Evaluating matching entries...");
            for (VcfaScenario remoteScen : remoteScenarios) {
                String remoteName = remoteScen.getName();
                if (itemsToDelete.contains(remoteName)) {
                    logger.info("[TARGETED DELETE] Deleting scenario named '{}' matching ID: {}", remoteName,
                            remoteScen.getId());
                    restClient.deleteScenario(remoteScen.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure scenario definitions",
                    e);
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

        List<String> allowedScenarios = localDescriptor.getScenarios();
        return allowedScenarios != null && allowedScenarios.isEmpty();
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
                        logger.error("Failed parsing content.yaml within scenario descriptor check block", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        List<String> allowedScenarios = localDescriptor.getScenarios();
        if (allowedScenarios == null) {
            return false;
        }

        if (allowedScenarios.isEmpty()) {
            return true;
        }

        return !allowedScenarios.contains(trackingName);
    }
}
