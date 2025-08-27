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

public class VcfaSubscriptionStore extends AbstractVcfaStore {
    @Override
    public void importContent(File sourceDirectory) {
        logger.info("Importing subscriptions from {} - not supported (read-only)", sourceDirectory.getAbsolutePath());
    }

    @Override
    public void exportContent() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String,Object>> items = restClient.getSubscriptions();
            Package serverPackage = this.vcfaPackage;
            items.forEach(item -> {
                String name = item.get("name") != null ? item.get("name").toString() : item.get("id").toString();
                String folderPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), "subscriptions", name).toString();
                try {
                    Files.createDirectories(Paths.get(folderPath));
                    String detailsFile = folderPath + File.separator + "details.json";
                    String detailsJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
                    Files.write(Paths.get(detailsFile), detailsJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    logger.error("Unable to export subscription {}", name, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch subscriptions", e);
        }
    }

    @Override
    public void deleteContent() {
        logger.info("Deleting subscriptions not implemented");
    }
}
