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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.vrli.VrliPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVrli;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.ContentPackDTO;

public class VrliPackageStore extends GenericPackageStore<VrliPackageDescriptor> {
    private static final String DIR_ALERTS = "alerts";
    private static final String DIR_CONTENT_PACKS = "content_packs";
    private static final Logger logger = LoggerFactory.getLogger(VrliPackageStore.class);
    private final RestClientVrli restClient;

    protected VrliPackageStore(RestClientVrli restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Package> importAllPackages(List<Package> pkgs, boolean dryrun, boolean mergePackages) {
        this.validateFilesystem(pkgs);

        List<Package> sourceEndpointPackages = pkgs;
        if (sourceEndpointPackages.isEmpty()) {
            return new ArrayList<>();
        }
        List<Package> importedPackages = new ArrayList<>();
        for (Package pkg : sourceEndpointPackages) {
            importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
        }

        return importedPackages;
    }

    @Override
    public List<Package> exportAllPackages(List<Package> pkgs, boolean dryrun) {
        this.vlidateServer(pkgs);

        List<Package> sourceEndpointPackages = pkgs;
        if (sourceEndpointPackages.isEmpty()) {
            return new ArrayList<>();
        }
        List<Package> exportedPackages = new ArrayList<>();
        for (Package pkg : pkgs) {
            VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));
            exportedPackages.add(this.exportPackage(pkg, descriptor, dryrun));
        }

        return exportedPackages;
    }

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun,false);
	}

	@Override
    public Package importPackage(Package pkg, boolean dryrun, boolean mergePackages) {
        logger.info(String.format(PackageStore.PACKAGE_IMPORT, pkg));

        File tmp;
        try {
            tmp = Files.createTempDirectory("vrbt-temp-import-dir").toFile();
            logger.info("Created temp dir {}", tmp.getAbsolutePath());
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }
        importAlerts(tmp);
        importContentPacks(tmp);

        return pkg;
    }

    @Override
    public Package exportPackage(Package pkg, boolean dryrun) {
        VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));

        return this.exportPackage(pkg, descriptor, dryrun);
    }

    @Override
    public Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun) {
        VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(exportDescriptor);

        return this.exportPackage(pkg, descriptor, dryrun);
    }

    @Override
    public Package exportPackage(Package pkg, VrliPackageDescriptor vrliPackageDescriptor, boolean dryrun) {
        logger.info(String.format(PackageStore.PACKAGE_EXPORT, pkg));
        List<String> alertNames = vrliPackageDescriptor.getAlerts();
        if (alertNames != null) {
            this.exportAlerts(pkg, alertNames);
        } else {
            logger.info("No alerts found in content.yaml");
        }
        List<String> contentPackNames = vrliPackageDescriptor.getContentPacks();
        if (contentPackNames != null) {
            this.exportContentPacks(pkg, contentPackNames);
        } else {
            logger.info("No content packs found in content.yaml");
        }

        return pkg;
    }

    @Override
    public List<Package> getPackages() {
        throw new RuntimeException("VRLI does not support packages");
    }

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        throw new RuntimeException("Deleting packages is not supported");
    }

    @Override
    protected PackageContent getPackageContent(Package pkg) {
        throw new RuntimeException("Parsing package content is not supported");
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        throw new RuntimeException("Delete content is not supported");
    }

    private void exportAlerts(Package vrliPakage, List<String> alertNames) {
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

    private void exportContentPacks(Package vrliPakage, List<String> contentPackNames) {
        if (contentPackNames == null || contentPackNames.isEmpty()) {
            return;
        }
        List<ContentPackDTO> allContentPacks = this.restClient.getAllContentPacks();
        if (allContentPacks == null || allContentPacks.isEmpty()) {
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
            }
        }
    }

    private File exportAlert(Package vrliPakage, AlertDTO alert) {
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

    private File exportContentPack(Package vrliPakage, String contentPackName, String contentPackData) {
        File store = new File(vrliPakage.getFilesystemPath());
        File contentPacksFile = Paths.get(store.getPath(), DIR_CONTENT_PACKS, contentPackName + ".json").toFile();
        contentPacksFile.getParentFile().mkdirs();

        try {
            logger.info("Exporting content pack '{}'", contentPackName);
            Files.write(Paths.get(contentPacksFile.getPath()), contentPackData.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store content pack {} {}", contentPackName, contentPacksFile.getPath());
            throw new RuntimeException(String.format("Unable to store content pack '%s' : %s.", contentPackName, e.getMessage()));
        }

        return contentPacksFile;
    }

    private void importAlerts(File tmp) {
        File alertsDirectory = Paths.get(tmp.getPath(), DIR_ALERTS).toFile();

        if (alertsDirectory.exists()) {
            FileUtils.listFiles(alertsDirectory, new String[] { "json" }, false).stream().forEach(this::importAlert);
        }
    }

    private void importAlert(File alertFile) {
        try {
            String alertJson = FileUtils.readFileToString(alertFile, StandardCharsets.UTF_8);
            restClient.importAlert(alertJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + alertFile.getPath(), e);
        }
    }

    private void importContentPacks(File tmp) {
        File contentPacksDirectory = Paths.get(tmp.getPath(), DIR_CONTENT_PACKS).toFile();

        if (contentPacksDirectory.exists()) {
            FileUtils.listFiles(contentPacksDirectory, new String[] { "json" }, false).stream().forEach(this::importContentPack);
        }
    }

    private void importContentPack(File contentPackFile) {
        try {
            logger.info("Importing content pack from file '{}'", contentPackFile.getName());
            String contentPackJson = FileUtils.readFileToString(contentPackFile, StandardCharsets.UTF_8);
            restClient.importContentPack(contentPackFile.getName(), contentPackJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + contentPackFile.getPath(), e);
        }
    }

}
