package com.vmware.pscoe.iac.artifact.vcf.automation.store;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2025 VMware
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
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.filters.CustomFolderFolderFilter;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

/**
 * Handles import, export, and deletion of VcfaBluePrints.
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

        for (File bpDir : bpList) {
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

        for (VcfaBlueprint bpSummary : serverBps) {
            String name = bpSummary.getName();
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
        logger.info("Deleting blueprints");
        // Deletion not implemented here
    }

    /**
     * Import a single blueprint directory, creating or updating as needed.
     */
    private void importBlueprint(File bpDir, List<VcfaBlueprint> serverBps) throws IOException {
        String bpName = bpDir.getName();
        VcfaBlueprint bp = loadBlueprintMeta(bpDir, bpName);
        setContentIfPresent(bp, bpDir);

        bp.setProjectId(restClient.getProjectId());

        VcfaBlueprint existing = serverBps.stream()
                .filter(m -> bpName.equals(m.getName()))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            restClient.createBlueprint(bp);
        } else {
            restClient.updateBlueprint(existing.getId(), bp);
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
                new File(serverPackage.getFilesystemPath()).getPath(), "blueprints", bp.getName()
        ).toString();

        File bpFolder = new File(blueprintDir);
        if (!bpFolder.exists()) {
            bpFolder.mkdirs();
        }

        // Write details.json
        String detailsFile = blueprintDir + File.separator + "details.json";
        String detailsJson = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(bp.asExportMap());
        Files.write(Paths.get(detailsFile), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        // Write content.yaml
        if (bp.getContent() != null) {
            String contentFile = blueprintDir + File.separator + "content.yaml";
            Files.write(Paths.get(contentFile), bp.getContent().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        }
    }
}
