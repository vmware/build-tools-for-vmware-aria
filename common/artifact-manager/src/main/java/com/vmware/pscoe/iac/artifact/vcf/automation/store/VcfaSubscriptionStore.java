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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;

public class VcfaSubscriptionStore extends AbstractVcfaStore {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing subscriptions from {}", sourceDirectory.getAbsolutePath());

        File subsFolder = Paths.get(sourceDirectory.getPath(), "subscriptions").toFile();
        if (!subsFolder.exists() || !subsFolder.isDirectory()) {
            logger.info("Subscriptions directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaSubscription> existingSubscriptions = restClient.getSubscriptions();
            Map<String, VcfaSubscription> existingSubMap = existingSubscriptions.stream()
                    .collect(Collectors.toMap(
                            sub -> sub.getName() != null ? sub.getName() : sub.getId(),
                            sub -> sub,
                            (existing, replacement) -> existing));

            File[] subFiles = subsFolder.listFiles();
            if (subFiles == null || subFiles.length == 0) {
                logger.info("Could not find any subscription assets to import.");
                return;
            }

            for (File fileOrDir : subFiles) {
                // Determine the clean subscription asset name based on if it's flat json or a
                // folder directory
                String subName = fileOrDir.isDirectory() ? fileOrDir.getName()
                        : fileOrDir.getName().replace(".json", "");

                // --- FIXED: Check descriptor filter before importing local files ---
                if (isExcludedByDescriptor(subName)) {
                    logger.info(
                            "Subscription asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            subName);
                    continue;
                }

                File jsonFile = fileOrDir.isDirectory()
                        ? Paths.get(fileOrDir.getPath(), "details.json").toFile()
                        : fileOrDir;
                if (jsonFile.exists() && jsonFile.getName().endsWith(".json")) {
                    importSubscription(jsonFile, existingSubMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to pull remote environment details during subscription import execution",
                    e);
        }
    }

    private void importSubscription(File jsonFile, Map<String, VcfaSubscription> existingSubMap) {
        try {
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);
            JsonNode rootNode = mapper.readTree(jsonContent);

            if (rootNode.isObject()) {
                ObjectNode subscriptionNode = (ObjectNode) rootNode;
                String subscriptionName = subscriptionNode.has("name") ? subscriptionNode.get("name").asText()
                        : "Unknown";

                logger.info("Processing subscription: '{}'", subscriptionName);

                // Strip orgId context to support multi-tenant/multi-org target environments
                // cleanly
                subscriptionNode.remove("orgId");

                // Handle infrastructure dependencies mapping rules
                substituteProjects(subscriptionNode);
                resolveRunnableId(subscriptionNode);

                VcfaSubscription subPayload = mapper.treeToValue(subscriptionNode, VcfaSubscription.class);

                if (existingSubMap.containsKey(subscriptionName)) {
                    VcfaSubscription existingSub = existingSubMap.get(subscriptionName);
                    String existingId = existingSub.getId();
                    subPayload.setId(existingId);

                    // OPTIMIZATION: Compare server copy vs incoming copy before attempting a heavy
                    // write operation
                    if (isIdentical(existingSub, subPayload)) {
                        logger.info(
                                "Subscription '{}' is already up to date on target endpoint. Skipping update operation.",
                                subscriptionName);
                        return;
                    }

                    logger.info("Updating existing subscription: '{}' (ID: {})", subscriptionName, existingId);
                    restClient.updateSubscription(existingId, subPayload);
                } else {
                    restClient.createSubscription(subPayload);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading subscription JSON payload data from file: " + jsonFile.getPath(),
                    e);
        } catch (Exception e) {
            logger.error("Failed to successfully evaluate and push subscription from asset: {}", jsonFile.getName(), e);
        }
    }

    /**
     * Determines whether a local file-based subscription configuration payload
     * is functionally identical to the server-side deployed infrastructure
     * instance.
     */
    private boolean isIdentical(VcfaSubscription remote, VcfaSubscription local) {
        if (remote == null || local == null) {
            return false;
        }

        // Compare critical mutable functional properties of the Event Broker state
        // engine
        boolean sameDisabledState = java.util.Objects.equals(remote.getDisabled(), local.getDisabled());
        boolean sameBlockingState = java.util.Objects.equals(remote.getBlocking(), local.getBlocking());
        boolean samePriority = java.util.Objects.equals(remote.getPriority(), local.getPriority());
        boolean sameTopic = java.util.Objects.equals(remote.getEventTopicId(), local.getEventTopicId());
        boolean sameCriteria = java.util.Objects.equals(remote.getCriteria(), local.getCriteria());
        boolean sameRunnableType = java.util.Objects.equals(remote.getRunnableType(), local.getRunnableType());
        boolean sameRunnableId = java.util.Objects.equals(remote.getRunnableId(), local.getRunnableId());

        return sameDisabledState && sameBlockingState && samePriority && sameTopic && sameCriteria && sameRunnableType
                && sameRunnableId;
    }

    private void substituteProjects(ObjectNode content) {
        JsonNode constraintsNode = content.get("constraints");
        if (constraintsNode != null && constraintsNode.isObject()) {
            ObjectNode constraintObj = (ObjectNode) constraintsNode;
            JsonNode projectNamesNode = constraintObj.get("projectNames");

            if (projectNamesNode != null && projectNamesNode.isArray() && projectNamesNode.size() > 0) {
                ArrayNode newProjectIdElements = mapper.createArrayNode();

                for (JsonNode nameNode : projectNamesNode) {
                    String projectId = projectNameToId(nameNode.asText());
                    if (projectId != null) {
                        newProjectIdElements.add(projectId);
                    } else {
                        logger.warn("Project name mapping failed: '{}' cannot be verified inside target vCF endpoint.",
                                nameNode.asText());
                    }
                }
                constraintObj.remove("projectNames");
                constraintObj.set("projectId", newProjectIdElements);
            }
        }
    }

    private void resolveRunnableId(ObjectNode subscriptionNode) {
        JsonNode runnableTypeNode = subscriptionNode.get("runnableType");
        if (runnableTypeNode != null && runnableTypeNode.asText().contains("extensibility.abx")) {
            JsonNode runnableNameNode = subscriptionNode.get("runnableName");
            if (runnableNameNode != null) {
                String abxName = runnableNameNode.asText();
                String actionId = abxNameToId(abxName);

                subscriptionNode.remove("runnableName");
                subscriptionNode.put("runnableId", actionId);
            }
        }
    }

    private String projectNameToId(String name) {
        try {
            return restClient.getProjectId(name);
        } catch (IOException e) {
            logger.error("Failed resolving infrastructure projectId mapping lookup for string name: {}", name, e);
            return null;
        }
    }

    private String abxNameToId(String name) {
        return "vcf-resolved-abx-action-placeholder-id";
    }

    @Override
    public void exportContent() {
        logger.info("Pulling subscription configurations from the remote environment...");
        try {
            List<VcfaSubscription> items = restClient.getSubscriptions();
            if (items == null || items.isEmpty()) {
                logger.info("No remote subscriptions found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseSubsPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), "subscriptions")
                    .toString();

            for (VcfaSubscription item : items) {
                String name = item.getName() != null ? item.getName() : item.getId();

                // --- FIXED: Check descriptor filter rules before evaluating/writing to disk
                // ---
                if (isExcludedByDescriptor(name)) {
                    logger.info("Subscription '{}' is excluded by descriptor configuration rules. Skipping export.",
                            name);
                    continue;
                }

                // Check if a flat file or a nested directory already exists for this
                // subscription
                File flatFile = Paths.get(baseSubsPath, name + ".json").toFile();
                File nestedDir = Paths.get(baseSubsPath, name).toFile();

                String finalTargetFilePath;

                if (nestedDir.exists() && nestedDir.isDirectory()) {
                    // Scenario A: Workspace uses nested folders (name/details.json)
                    finalTargetFilePath = Paths.get(nestedDir.getPath(), "details.json").toString();
                } else if (flatFile.exists()) {
                    // Scenario B: Workspace uses flat files (name.json)
                    finalTargetFilePath = flatFile.getPath();
                } else {
                    // Scenario C: Brand new subscription created in UI. Default to flat file for
                    // your project style.
                    Files.createDirectories(Paths.get(baseSubsPath));
                    finalTargetFilePath = flatFile.getPath();
                }

                try {
                    // Convert the model back to a clean JSON tree so we can scrub telemetry fields
                    ObjectNode exportNode = mapper.valueToTree(item);

                    // Write it back out to the calculated path
                    String sanitizedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportNode);
                    Files.write(
                            Paths.get(finalTargetFilePath),
                            sanitizedJson.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);

                    logger.info("Successfully synchronized subscription asset: {}", finalTargetFilePath);
                } catch (IOException e) {
                    logger.error("Unable to write synchronized subscription file artifact: {}", name, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing subscription sync export stream", e);
        }
    }

    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Subscription entities...");
        try {
            List<VcfaSubscription> remoteSubscriptions = restClient.getSubscriptions();
            if (remoteSubscriptions == null || remoteSubscriptions.isEmpty()) {
                logger.info("No remote subscriptions identified to delete.");
                return;
            }

            List<String> itemsToDelete = null;
            boolean isExplicitlyEmpty = false;

            // Locate and parse content.yaml manually to guarantee absolute control over null vs [] empty arrays
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
                        // Support both potential mapping schemas for subscriptions
                        Object subListObj = rawMap.containsKey("subscription") ? rawMap.get("subscription") : rawMap.get("subscriptions");
                        
                        if (rawMap.containsKey("subscription") || rawMap.containsKey("subscriptions")) {
                            if (subListObj instanceof List) {
                                itemsToDelete = (List<String>) subListObj;
                                if (itemsToDelete.isEmpty()) {
                                    isExplicitlyEmpty = true;
                                }
                            }
                        }
                    }
                }
            }

            // --- TRISTATE EVALUATION MATRIX ---
            
            // Scenario 1: Explicitly Empty "[]" -> Target absolute safety bypass. Delete Nothing.
            if (isExplicitlyEmpty) {
                logger.info("Subscription descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            // Scenario 2: Key is completely null/omitted -> Wildcard target mode active. Delete Everything.
            if (itemsToDelete == null) {
                logger.info("Subscription descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote subscriptions.");
                for (VcfaSubscription remoteSub : remoteSubscriptions) {
                    logger.info("[WILDCARD DELETE] Deleting subscription named '{}' matching ID: {}", remoteSub.getName(), remoteSub.getId());
                    restClient.deleteSubscription(remoteSub.getId());
                }
                return;
            }

            // Scenario 3: Explicit List -> Filter targeted matches sequentially.
            logger.info("Subscription targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaSubscription remoteSub : remoteSubscriptions) {
                String remoteName = remoteSub.getName();
                if (itemsToDelete.contains(remoteName)) {
                    logger.info("[TARGETED DELETE] Deleting subscription named '{}' matching ID: {}", remoteName, remoteSub.getId());
                    restClient.deleteSubscription(remoteSub.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Fatal error encountered clearing existing infrastructure subscription definitions", e);
        }
    }

    /**
     * Evaluates subscription filtering rules straight against the flat layout of
     * the real content.yaml file.
     */
    private boolean isExcludedByDescriptor(String subscriptionName) {
        com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;

        // 1. Fallback check for parent-injected framework instance
        if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
            localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
        }

        // 2. Direct filesystem lookup against the verified execution workspace
        // directory
        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
                                .getInstance(contentYamlFile);
                    } catch (Exception e) {
                        logger.error("Failed parsing content.yaml within subscription descriptor check block", e);
                    }
                }
            }
        }

        // 3. Exclusion decision matrix
        if (localDescriptor == null) {
            return false; // No configuration manifest found: process/download everything
        }

        List<String> allowedSubscriptions = localDescriptor.getSubscription();

        // 1. If it's completely missing or undefined (null), treat it as a wildcard ->
        // Process/Download everything
        if (allowedSubscriptions == null) {
            return false;
        }

        // 2. If it's explicitly empty [], the user is intentionally turning it off ->
        // Skip everything
        if (allowedSubscriptions.isEmpty()) {
            return true;
        }

        // If subscriptionName is missing from the list, exclude it
        return !allowedSubscriptions.contains(subscriptionName);
    }
}
