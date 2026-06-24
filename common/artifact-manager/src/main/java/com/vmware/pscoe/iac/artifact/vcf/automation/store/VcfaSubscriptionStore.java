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
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;

public class VcfaSubscriptionStore extends AbstractVcfaStore {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing subscriptions from {}", sourceDirectory.getAbsolutePath());

        // --- OPTIMIZATION STEP 1: Short-circuit gate validation check ---
        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Subscription descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

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
                String subName = fileOrDir.isDirectory() ? fileOrDir.getName()
                        : fileOrDir.getName().replace(".json", "");

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

                String currentOrgId = restClient.getOrganizationId();
                String currentProjectId = restClient.getProjectId();
                String rawIdFromNode = subscriptionNode.has("id") && !subscriptionNode.get("id").isNull()
                        ? subscriptionNode.get("id").asText()
                        : null;

                String targetId;
                boolean hasValidOrgId = (currentOrgId != null && !currentOrgId.trim().isEmpty()
                        && !"null".equalsIgnoreCase(currentOrgId));

                if (rawIdFromNode != null && !rawIdFromNode.trim().isEmpty()
                        && !"null".equalsIgnoreCase(rawIdFromNode)) {
                    // --- REINFORCED REPLACEMENT LOGIC: Clean out tenant UUID prefixes as well as
                    // legacy literal "null-" artifacts ---
                    String strippedId = rawIdFromNode.replaceAll("^[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}-", "");
                    strippedId = strippedId.replaceAll("^null-", "");

                    VcfaSubscription matchOnServer = existingSubMap.get(subscriptionName);
                    if (matchOnServer == null || matchOnServer.getOrgId() == null
                            || !matchOnServer.getOrgId().equals(currentOrgId)) {
                        targetId = hasValidOrgId ? (currentOrgId + "-" + strippedId) : strippedId;
                    } else {
                        targetId = strippedId;
                    }
                } else {
                    targetId = hasValidOrgId
                            ? (currentOrgId + "-sub_" + Math.abs(subscriptionName.hashCode()))
                            : ("sub_" + Math.abs(subscriptionName.hashCode()));
                    logger.debug(
                            "Subscription Id missing or invalid in payload. Generated hashcode designation identity: {}",
                            targetId);
                }

                VcfaPayloadSanitizer.sanitize(subscriptionNode, currentOrgId, currentProjectId);
                subscriptionNode.put("id", targetId);

                substituteProjects(subscriptionNode);
                resolveRunnableId(subscriptionNode);

                VcfaSubscription subPayload = mapper.treeToValue(subscriptionNode, VcfaSubscription.class);

                if (existingSubMap.containsKey(subscriptionName)) {
                    VcfaSubscription existingSub = existingSubMap.get(subscriptionName);
                    String existingId = existingSub.getId();
                    subPayload.setId(existingId);

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
            throw new RuntimeException(e);
        }
    }

    private boolean isIdentical(VcfaSubscription remote, VcfaSubscription local) {
        if (remote == null || local == null) {
            return false;
        }

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
            JsonNode projectIdNode = constraintObj.get("projectId");

            // Preserve explicit null projectId — means "use default", do not override
            if (projectIdNode != null && projectIdNode.isNull()) {
                return;
            }

            List<String> combinedProjectIds = new ArrayList<>();
            try {
                String activeConfigProjectId = restClient.getProjectId();
                if (activeConfigProjectId != null) {
                    combinedProjectIds.add(activeConfigProjectId);
                }
            } catch (IOException e) {
                logger.error(
                        "Failed to extract operational configuration tracking context project reference ID handle.", e);
            }

            if (projectNamesNode != null && projectNamesNode.isArray() && projectNamesNode.size() > 0) {
                for (JsonNode nameNode : projectNamesNode) {
                    String projectId = projectNameToId(nameNode.asText());
                    if (projectId != null) {
                        combinedProjectIds.add(projectId);
                    } else {
                        logger.warn("Project name mapping failed: '{}' cannot be verified inside target vCF endpoint.",
                                nameNode.asText());
                    }
                }
                constraintObj.remove("projectNames");
            } else if (projectIdNode != null && projectIdNode.isArray() && projectIdNode.size() > 0) {
                for (JsonNode idNode : projectIdNode) {
                    combinedProjectIds.add(idNode.asText());
                }
            }

            List<String> uniqueProjectIds = combinedProjectIds.stream().distinct().collect(Collectors.toList());
            if (!uniqueProjectIds.isEmpty()) {
                ArrayNode newProjectIdElements = mapper.createArrayNode();
                uniqueProjectIds.forEach(newProjectIdElements::add);
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

                if (actionId == null) {
                    throw new RuntimeException("Import failed: ABX Action dependency named '" + abxName
                            + "' does not exist on target infrastructure target environment.");
                }

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
        try {
            return restClient.getAbxActionIdByName(name);
        } catch (IOException e) {
            logger.error("Failed resolving infrastructure abxActionId mapping lookup for string name: {}", name, e);
            return null;
        }
    }

    @Override
    public void exportContent() {
        logger.info("Pulling subscription configurations from the remote environment...");

        if (isExplicitlyEmptyInDescriptor()) {
            logger.info(
                    "Subscription descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

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

                if (isExcludedByDescriptor(name)) {
                    logger.info("Subscription '{}' is excluded by descriptor configuration rules. Skipping export.",
                            name);
                    continue;
                }

                File flatFile = Paths.get(baseSubsPath, name + ".json").toFile();
                File nestedDir = Paths.get(baseSubsPath, name).toFile();
                String finalTargetFilePath;

                if (nestedDir.exists() && nestedDir.isDirectory()) {
                    finalTargetFilePath = Paths.get(nestedDir.getPath(), "details.json").toString();
                } else if (flatFile.exists()) {
                    finalTargetFilePath = flatFile.getPath();
                } else {
                    Files.createDirectories(Paths.get(baseSubsPath));
                    finalTargetFilePath = flatFile.getPath();
                }

                try {
                    ObjectNode exportNode = mapper.valueToTree(item);

                    VcfaPayloadSanitizer.sanitize(exportNode,
                            restClient.getOrganizationId(),
                            restClient.getProjectId());

                    JsonNode runnableTypeNode = exportNode.get("runnableType");
                    if (runnableTypeNode != null && runnableTypeNode.asText().contains("extensibility.abx")) {
                        JsonNode runnableIdNode = exportNode.get("runnableId");
                        if (runnableIdNode != null) {
                            String rId = runnableIdNode.asText();
                            String abxName = restClient.getAbxActionNameById(rId);
                            if (abxName == null) {
                                throw new RuntimeException(
                                        "Export failed: Subscription targets an internal ABX Action ID '" + rId
                                                + "' which does not exist on this environment server source instance.");
                            }
                            exportNode.remove("runnableId");
                            exportNode.put("runnableName", abxName);
                        }
                    }

                    JsonNode constraintsNode = exportNode.get("constraints");
                    if (constraintsNode != null && constraintsNode.isObject() && constraintsNode.has("projectId")) {
                        JsonNode projectIdNode = constraintsNode.get("projectId");
                        if (projectIdNode.isArray() && projectIdNode.size() > 0) {
                            ArrayNode projectNamesNode = mapper.createArrayNode();
                            for (JsonNode idNode : projectIdNode) {
                                String pName = restClient.getProjectNameById();
                                if (pName != null) {
                                    projectNamesNode.add(pName);
                                }
                            }
                            ((ObjectNode) constraintsNode).set("projectNames", projectNamesNode);
                        }
                    }

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
                        Object subListObj = rawMap.containsKey("subscription") ? rawMap.get("subscription")
                                : rawMap.get("subscriptions");

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

            if (isExplicitlyEmpty) {
                logger.info("Subscription descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            if (itemsToDelete == null) {
                logger.info(
                        "Subscription descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote subscriptions.");
                for (VcfaSubscription remoteSub : remoteSubscriptions) {
                    logger.info("[WILDCARD DELETE] Deleting subscription named '{}' matching ID: {}",
                            remoteSub.getName(), remoteSub.getId());
                    restClient.deleteSubscription(remoteSub.getId());
                }
                return;
            }

            logger.info(
                    "Subscription targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaSubscription remoteSub : remoteSubscriptions) {
                String remoteName = remoteSub.getName();
                if (itemsToDelete.contains(remoteName)) {
                    logger.info("[TARGETED DELETE] Deleting subscription named '{}' matching ID: {}", remoteName,
                            remoteSub.getId());
                    restClient.deleteSubscription(remoteSub.getId());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure subscription definitions", e);
        }
    }

    private boolean isExplicitlyEmptyInDescriptor() {
        com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;

        if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
            localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
        }

        if (localDescriptor == null) {
            String workingDir = System.getProperty("user.dir");
            if (workingDir != null) {
                File contentYamlFile = new File(workingDir, "content.yaml");
                if (contentYamlFile.exists()) {
                    try {
                        localDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
                                .getInstance(contentYamlFile);
                    } catch (Exception e) {
                        logger.error("Failed parsing manifest layout rules map token details.", e);
                    }
                }
            }
        }

        if (localDescriptor == null) {
            return false;
        }

        List<String> allowedSubscriptions = localDescriptor.getSubscription();
        return allowedSubscriptions != null && allowedSubscriptions.isEmpty();
    }

    private boolean isExcludedByDescriptor(String subscriptionName) {
        com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor localDescriptor = null;

        if (this.descriptor instanceof com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) {
            localDescriptor = (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor) this.descriptor;
        }

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

        if (localDescriptor == null) {
            return false;
        }

        List<String> allowedSubscriptions = localDescriptor.getSubscription();

        if (allowedSubscriptions == null) {
            return false;
        }

        if (allowedSubscriptions.isEmpty()) {
            return true;
        }

        return !allowedSubscriptions.contains(subscriptionName);
    }
}
