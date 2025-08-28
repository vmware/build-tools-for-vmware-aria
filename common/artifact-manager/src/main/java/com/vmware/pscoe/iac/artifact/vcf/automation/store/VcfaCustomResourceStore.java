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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CustomResourceType;

public class VcfaCustomResourceStore extends AbstractVcfaStore {
    @Override
    public void importContent(File sourceDirectory) {
        File folder = new File(Paths.get(sourceDirectory.getPath(), "custom-resources").toString());
        if (!folder.exists()) return;
        File[] items = folder.listFiles();
        if (items == null) return;
        for (File item : items) {
            CustomResourceType details = readDetailsJson(item, CustomResourceType.class);
            if (details == null) continue;
            try {
                restClient.createCustomResourceType(details);
            } catch (IOException e) {
                throw new RuntimeException("Failed importing custom resource " + item.getName(), e);
            }
        }
    }

    @Override
    public void exportContent() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<CustomResourceType> items = restClient.getCustomResources();
            Package serverPackage = this.vcfaPackage;
            items.forEach(item -> {
                String name = item.getName() != null ? item.getName() : item.getId();
                String folderPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), "custom-resources", name).toString();
                try {
                    Files.createDirectories(Paths.get(folderPath));
                    String detailsFile = folderPath + File.separator + "details.json";
                    String detailsJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
                    Files.write(Paths.get(detailsFile), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    logger.error("Unable to export custom resource {}", name, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch custom resources", e);
        }
    }

    @Override
    public void deleteContent() {
        File folder = new File(this.vcfaPackage.getFilesystemPath() + File.separator + "custom-resources");
        if (!folder.exists()) return;
        File[] items = folder.listFiles();
        if (items == null) return;
        for (File item : items) {
            java.util.Map<String,Object> details = readDetailsJson(item);
            if (details == null) continue;
            Object idObj = details.get("id");
            if (idObj == null) {
                logger.warn("No id found for custom resource {} - skipping delete", item.getName());
                continue;
            }
            try {
                restClient.deleteCustomResourceType(idObj.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed deleting custom resource " + item.getName(), e);
            }
        }
    }
}
