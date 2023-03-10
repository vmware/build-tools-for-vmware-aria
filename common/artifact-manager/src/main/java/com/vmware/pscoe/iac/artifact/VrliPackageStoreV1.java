package com.vmware.pscoe.iac.artifact;

/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
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

import com.vmware.pscoe.iac.artifact.rest.client.vrli.RestClientVrliV1;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.ContentPackDTO;

public class VrliPackageStoreV1 extends AbstractVrliPackageStore {
    private final RestClientVrliV1 restClient;

    public VrliPackageStoreV1(RestClientVrliV1 restClient) {
		this.restClient = restClient;
		logger = LoggerFactory.getLogger(VrliPackageStoreV1.class);
    }

    protected void exportAlerts(Package vrliPakage, List<String> alertNames) {
        if (alertNames == null || alertNames.isEmpty()) {
            return;
        }
        List<AlertDTO> allAlerts = this.restClient.getAllAlerts();
        if (allAlerts == null || allAlerts.isEmpty()) {
            return;
        }
        for (AlertDTO alert : allAlerts) {
            if (alertNames.stream().anyMatch(name -> this.isPackageAssetMatching(name, alert.getName()))) {
                this.exportAlert(vrliPakage, alert);
            }
        }
    }

    protected void exportContentPacks(Package vrliPakage, List<String> contentPackNames) {
        if (contentPackNames == null || contentPackNames.isEmpty()) {
			logger.warn("No content packs defined for export.");
            return;
        }
        List<ContentPackDTO> allContentPacks = this.restClient.getAllContentPacks();
        if (allContentPacks == null || allContentPacks.isEmpty()) {
			logger.warn("No content packs defined on vRLI server.");
            return;
        }
        for (ContentPackDTO contentPack : allContentPacks) {
            if (contentPackNames.stream().anyMatch(name -> this.isPackageAssetMatching(name, contentPack.getName()))) {
                String contentPackData = this.restClient.getContentPack(contentPack.getNamespace());
                if (StringUtils.isEmpty(contentPackData)) {
                    logger.warn("No data found for content pack '{}'", contentPack.getName());
                    continue;
                }
                exportContentPack(vrliPakage, contentPack.getName(), contentPackData);
            } else {
				logger.warn("No content pack match with name '{}'", contentPack.getName());
			}
        }
    }

	@Override
    protected void importAlert(File alertFile) {
        try {
            String alertJson = FileUtils.readFileToString(alertFile, StandardCharsets.UTF_8);
            restClient.importAlert(alertJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + alertFile.getPath(), e);
        }
    }

	@Override
    protected void importContentPack(File contentPackFile) {
        try {
            logger.info("Importing content pack from file '{}'", contentPackFile.getName());
            String contentPackJson = FileUtils.readFileToString(contentPackFile, StandardCharsets.UTF_8);
            restClient.importContentPack(contentPackFile.getName(), contentPackJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + contentPackFile.getPath(), e);
        }
    }

	protected File exportAlert(Package vrliPakage, AlertDTO alert) {
		File store = new File(vrliPakage.getFilesystemPath());
		File alertFile = Paths.get(store.getPath(), DIR_ALERTS, alert.getId() + ".json").toFile();
		alertFile.getParentFile().mkdirs();

		try {
			logger.info("Exporting alert '{}'", alert.getName());
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			String alertJson = gson.toJson(alert, AlertDTO.class);
			Files.write(Paths.get(alertFile.getPath()), alertJson.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error("Unable to store alert {} {}", alert.getName(), alertFile.getPath());
			throw new RuntimeException(String.format("Unable to store alert '%s' : %s.", alert.getName(), e.getMessage()));
		}

		return alertFile;
	}

}
