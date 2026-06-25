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
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaDescriptorHelper;
import com.vmware.pscoe.iac.artifact.vcf.automation.common.VcfaPayloadSanitizer;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.common.store.Package;

public class VcfaPropertyGroupStore extends AbstractVcfaStore {

    private static final String DIR_PROPERTY_GROUPS = "property-groups";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public VcfaPropertyGroupStore() {
        super();
    }

    /**
     * Exports all Property Groups from the remote target system to the local
     * package filesystem workspace using the cosmetic Display Name attribute
     * (preserving spaces).
     */
    @Override
    public void exportContent() {
        logger.info("Pulling property group configurations from the remote environment...");

        List<String> allowedGroups = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "property-group",
                "propertyGroups");
        if (allowedGroups != null && allowedGroups.isEmpty()) {
            logger.info(
                    "Property Group descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping export entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in PropertyGroup Store. Skipping export.");
            return;
        }

        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            if (remoteGroups == null || remoteGroups.isEmpty()) {
                logger.info("No remote Property Groups found to export.");
                return;
            }

            Package serverPackage = this.vcfaPackage;
            String baseGroupsPath = Paths
                    .get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_PROPERTY_GROUPS)
                    .toString();

            Files.createDirectories(Paths.get(baseGroupsPath));

            for (VcfaPropertyGroup group : remoteGroups) {
                String trackingName = group.getDisplayName();

                if (isExcludedByDescriptor(trackingName)) {
                    logger.info("Property Group '{}' is excluded by descriptor configuration rules. Skipping export.",
                            trackingName);
                    continue;
                }

                this.verifyAssetPathSafety(trackingName, "Property Group");
                File jsonFile = Paths.get(baseGroupsPath, trackingName + ".json").toFile();

                ObjectNode jsonNode = mapper.valueToTree(group);
                jsonNode.remove("id");
                jsonNode.remove("projectName");
                jsonNode.remove("createdAt");
                jsonNode.remove("createdBy");
                jsonNode.remove("updatedAt");
                jsonNode.remove("updatedBy");

                if (jsonNode.has("organization") && jsonNode.get("organization").isNull()) {
                    jsonNode.remove("organization");
                }

                // Sanitize orgId and projectId for cross-org portability
                VcfaPayloadSanitizer.sanitize(jsonNode);

                logger.info("Successfully synchronized property group asset: {}", jsonFile.getAbsolutePath());
                String serializedJson = mapper.writeValueAsString(jsonNode);
                Files.write(
                        jsonFile.toPath(),
                        serializedJson.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal operational exception processing property group sync export stream", e);
        }
    }

    /**
     * Imports and synchronizes local Property Group JSON configurations back up to
     * the remote instance environment, mapped via friendly display names.
     */
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing property groups from {}", sourceDirectory.getAbsolutePath());

        List<String> allowedGroups = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "property-group",
                "propertyGroups");
        if (allowedGroups != null && allowedGroups.isEmpty()) {
            logger.info(
                    "Property Group descriptor is explicitly empty '[]' in configuration. Bypassing server lookups and skipping import entirely.");
            return;
        }

        if (restClient == null) {
            logger.warn("RestClient not initialized in PropertyGroup Store. Skipping import.");
            return;
        }

        File localDir = Paths.get(sourceDirectory.getPath(), DIR_PROPERTY_GROUPS).toFile();

        // --- NEW STRATEGIC COUPLING: Call the flexible hybrid asset validator
        // (Files/Folders mixed) ---
        VcfaDescriptorHelper.validateAssetsPresent(this.vcfaPackage, localDir, "Property Group", false,
                "property-group", "propertyGroups");

        if (!localDir.exists() || !localDir.isDirectory()) {
            logger.info("Property groups directory not found. Skipping.");
            return;
        }

        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            File[] groupFiles = localDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (groupFiles == null || groupFiles.length == 0) {
                logger.info("Could not find any property group assets to import.");
                return;
            }

            String currentOrgId;
            try {
                currentOrgId = restClient.getOrganizationId();
            } catch (Exception e) {
                logger.warn(
                        "Unable to resolve organization ID from target server. Property groups will be created without explicit org scoping: {}",
                        e.getMessage());
                currentOrgId = null;
            }
            String currentProjectId = restClient.getProjectId();

            for (File file : groupFiles) {
                String groupDisplayName = file.getName().replace(".json", "");

                if (isExcludedByDescriptor(groupDisplayName)) {
                    logger.info(
                            "Property group asset '{}' is excluded by descriptor configuration rules. Skipping import.",
                            groupDisplayName);
                    continue;
                }

                logger.info("Processing local Property Group asset configuration: '{}'", file.getName());
                VcfaPropertyGroup localGroup = mapper.readValue(file, VcfaPropertyGroup.class);

                Optional<VcfaPropertyGroup> existingRemote = remoteGroups.stream()
                        .filter(r -> r.getDisplayName().equalsIgnoreCase(localGroup.getDisplayName()))
                        .findFirst();

                if (localGroup.getOrganization() != null && !localGroup.getOrganization().isBlank()) {
                    String orgName = localGroup.getOrganization();
                    if (orgName.matches(".*-.*")) {
                        localGroup.setOrgId(orgName);
                        logger.debug("Using orgId from local file for property group '{}': {}", localGroup.getName(),
                                orgName);
                    } else {
                        String resolvedOrgId = resolveOrgNameToId(orgName);
                        if (resolvedOrgId != null) {
                            currentOrgId = resolvedOrgId;
                            localGroup.setOrgId(resolvedOrgId);
                            logger.debug("Resolved org name '{}' to orgId '{}' for property group '{}'", orgName,
                                    resolvedOrgId, localGroup.getName());
                        } else {
                            logger.warn(
                                    "Could not resolve org name '{}' to orgId for property group '{}'. Using default: {}",
                                    orgName, localGroup.getName(), currentOrgId);
                            localGroup.setOrgId(currentOrgId);
                        }
                    }
                } else {
                    localGroup.setOrgId(currentOrgId);
                }

                if (existingRemote.isPresent()) {
                    VcfaPropertyGroup remoteMatch = existingRemote.get();

                    String existingProjectId = remoteMatch.getProjectId();
                    if (existingProjectId != null && !existingProjectId.isBlank()
                            && !existingProjectId.equals(currentProjectId)) {
                        throw new UnsupportedOperationException(String.format(
                                "The property group '%s' is already assigned to a different project scope (%s). Scope change operations are not supported by the platform API.",
                                localGroup.getDisplayName(), existingProjectId));
                    }

                    if (localGroup.getProjectId() != null && !localGroup.getProjectId().isBlank()) {
                        logger.debug("Re-mapping local group project alignment pointer to current target scope: {}",
                                currentProjectId);
                        localGroup.setProjectId(currentProjectId);
                    }

                    if (isIdentical(remoteMatch, localGroup)) {
                        logger.info("Property Group '{}' matches remote system configuration exactly. Skipping update.",
                                localGroup.getDisplayName());
                    } else {
                        logger.info("Delta detected for Property Group '{}'. Initiating remote update context.",
                                localGroup.getDisplayName());
                        localGroup.setId(remoteMatch.getId());
                        restClient.updatePropertyGroup(remoteMatch.getId(), localGroup);
                    }
                } else {
                    if (localGroup.getProjectId() != null && !localGroup.getProjectId().isBlank()) {
                        logger.debug("Re-mapping local group project alignment pointer to current target scope: {}",
                                currentProjectId);
                        localGroup.setProjectId(currentProjectId);
                    }
                    logger.info("Property Group '{}' not found on target server. Executing remote creation.",
                            localGroup.getDisplayName());
                    restClient.createPropertyGroup(localGroup);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "CRITICAL: Failed to import Property Groups from filesystem to target environment", e);
        }
    }

    private boolean isIdentical(VcfaPropertyGroup remote, VcfaPropertyGroup local) {
        if (remote == null || local == null) {
            return false;
        }

        return Objects.equals(remote.getName(), local.getName())
                && Objects.equals(remote.getDisplayName(), local.getDisplayName())
                && Objects.equals(remote.getDescription(), local.getDescription())
                && Objects.equals(remote.getType(), local.getType())
                && Objects.equals(remote.getProperties(), local.getProperties());
    }

    /**
     * Executes cleanup deletion scanning for registered property groups targeting
     * Display Names.
     */
    @Override
    public void deleteContent() {
        logger.info("Executing cleanup deletion scanning for Property Group entities...");
        try {
            List<VcfaPropertyGroup> remoteGroups = restClient.getPropertyGroups();
            if (remoteGroups == null || remoteGroups.isEmpty()) {
                logger.info("No remote property groups identified to delete.");
                return;
            }

            List<String> itemsToDelete = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "property-group",
                    "propertyGroups");

            if (itemsToDelete == null) {
                logger.info(
                        "Property Group descriptor is undefined/null. Omitted wildcard trigger: Initiating purge for ALL remote groups.");
                for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                    logger.info("[WILDCARD DELETE] Deleting property group named '{}' matching ID: {}",
                            remoteGroup.getDisplayName(), remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
                return;
            }

            if (itemsToDelete.isEmpty()) {
                logger.info("Property Group descriptor is explicitly empty '[]'. Skipping deletion entirely.");
                return;
            }

            logger.info("Targeted filter list active. Evaluating matching entries for deletion sequence...");
            for (VcfaPropertyGroup remoteGroup : remoteGroups) {
                String remoteDisplayName = remoteGroup.getDisplayName();
                if (itemsToDelete.contains(remoteDisplayName)) {
                    logger.info("[TARGETED DELETE] Deleting property group named '{}' matching ID: {}",
                            remoteDisplayName, remoteGroup.getId());
                    restClient.deletePropertyGroup(remoteGroup.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Fatal error encountered clearing existing infrastructure property group definitions", e);
        }
    }

    private boolean isExcludedByDescriptor(String groupDisplayName) {
        List<String> allowedGroups = VcfaDescriptorHelper.getTargetedItems(this.vcfaPackage, "property-group",
                "propertyGroups");
        if (allowedGroups == null) {
            return false;
        }
        if (allowedGroups.isEmpty()) {
            return true;
        }
        return !allowedGroups.contains(groupDisplayName);
    }

    private String resolveOrgNameToId(String orgName) {
        if (restClient == null) {
            return null;
        }
        try {
            return restClient.getOrganizationId(orgName);
        } catch (IOException e) {
            logger.debug("Failed to resolve org name '{}' to ID: {}", orgName, e.getMessage());
            return null;
        }
    }
}
