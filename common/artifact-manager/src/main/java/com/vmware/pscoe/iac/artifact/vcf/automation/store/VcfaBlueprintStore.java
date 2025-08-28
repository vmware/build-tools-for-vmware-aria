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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.filters.CustomFolderFolderFilter;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

public class VcfaBlueprintStore extends AbstractVcfaStore {
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

        // Fetch existing blueprints on server
        List<VcfaBlueprint> serverBps;
        try {
            serverBps = restClient.getBlueprints();
        } catch (IOException e) {
            logger.error("Unable to fetch blueprints from server", e);
            throw new RuntimeException("Unable to fetch blueprints from server", e);
        }

        for (File bpDir : bpList) {
            try {
                handleImport(bpDir, serverBps);
            } catch (Exception e) {
                logger.error("Failed to import blueprint {}", bpDir.getName(), e);
            }
        }
    }

    @Override
    public void exportContent() {
        ObjectMapper mapper = new ObjectMapper();
        List<VcfaBlueprint> serverBps;
        try {
            serverBps = restClient.getBlueprints();
        } catch (IOException e) {
            logger.error("Unable to fetch blueprints from server", e);
            throw new RuntimeException("Unable to fetch blueprints from server", e);
        }
        Package serverPackage = this.vcfaPackage;
        serverBps.forEach(bpSummary -> {
            String name = bpSummary.getName();
            String id = bpSummary.getId();
            try {
                VcfaBlueprint details = restClient.getBlueprintById(id);
                String content = details.getContent();

                String bpFolderPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), "blueprints", name)
                        .toString();
                File bpFolder = new File(bpFolderPath);
                if (!bpFolder.exists()) bpFolder.mkdirs();

                String detailsFileName = bpFolderPath + File.separator + "details.json";
                String detailsJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(details);
                Files.write(Paths.get(detailsFileName), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

                String contentFileName = bpFolderPath + File.separator + "content.yaml";
                if (content != null) {
                    Files.write(Paths.get(contentFileName), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                }
            } catch (IOException e) {
                logger.error("Unable to export blueprint {}", name, e);
            }
        });
    }

    @Override
    public void deleteContent() {
        logger.info("Deleting blueprints");
        // Deletion not implemented generically here; handled via VcfaPackageStore delete flow
    }

    private void handleImport(final File bpDir, final List<VcfaBlueprint> serverBps) throws IOException {
        String bpName = bpDir.getName();
        VcfaBlueprint bp;
        try (JsonReader reader = new JsonReader(new FileReader(Paths.get(bpDir.getPath(), "details.json").toString()))) {
            bp = new Gson().fromJson(reader, VcfaBlueprint.class);
        } catch (FileNotFoundException e) {
            logger.error("details.json not found for blueprint {}", bpName);
            throw e;
        }

        // read content.yaml
        File contentFile = new File(Paths.get(bpDir.getPath(), "content.yaml").toString());
        if (contentFile.exists()) {
            String content = Files.readAllLines(contentFile.toPath(), StandardCharsets.UTF_8).stream().collect(Collectors.joining(System.lineSeparator()));
            bp.setContent(content);
        }

        // find by name on server
        VcfaBlueprint existing = serverBps.stream().filter(m -> bpName.equals(m.getName())).findFirst().orElse(null);
        if (existing == null) {
            // create
            restClient.createBlueprint(bp);
        } else {
            String id = (String) existing.getId();
            restClient.updateBlueprint(id, bp);
        }
    }
}
