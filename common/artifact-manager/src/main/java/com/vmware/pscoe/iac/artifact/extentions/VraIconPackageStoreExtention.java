package com.vmware.pscoe.iac.artifact.extentions;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.rest.helpers.JsonHelper;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VraIconPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

    private final Logger logger = LoggerFactory.getLogger(VraIconPackageStoreExtention.class);
    private final RestClientVra restClient;

    public VraIconPackageStoreExtention(RestClientVra restClient) {
        this.restClient = restClient;
    }

    @Override
    public Package exportPackage(Package serverPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Icon export extention is enabled.");
        List<File> iconFiles = new ArrayList<>();

        if (vraPackageDescriptor == null) {
            logger.debug("vRA Icon package export requires vRA package descriptor.");
            return serverPackage;
        }

        for (Map<String, String> contentObj : restClient.getPackageContents(serverPackage.getId())) {
            String contentTypeId = contentObj.get("contentTypeId");
            if ("composite-blueprint".equals(contentTypeId)) {
                loadCatalogItemIcon(iconFiles, serverPackage, contentObj, dryrun);
            } else if ("xaas-blueprint".equals(contentTypeId)) {
                loadCatalogItemIcon(iconFiles, serverPackage, contentObj, dryrun);
            }
        }

        if (!iconFiles.isEmpty()) {
            try {
                new PackageManager(serverPackage).addToExistingZip(iconFiles);
            } catch (IOException e) {
                throw new RuntimeException("Error adding files to zip", e);
            }
        }

        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Icon import extention is enabled.");
        if (dryrun) {
            logger.debug("vRA does not support Icon validation through the REST API.");
            return pkg;
        }

        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-icons").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        File iconsFolder = Paths.get(tmp.getPath(), "icon").toFile();
        if (iconsFolder.exists()) {
            FileUtils.listFiles(iconsFolder, new String[] { "json" }, false).stream()
                    .forEach(icon -> storeIconOnServer(icon));
        }
        return pkg;
    }

    private void loadCatalogItemIcon(List<File> icons, Package serverPackage, Map<String, String> contentObj,
            boolean dryrun) {
        String name = contentObj.get("name");
        Map<String, Object> catalogItem = restClient.getCatalogItemByName(name);

        if (catalogItem == null) {
            return;
        }

        String iconId = (String) catalogItem.get("iconId");
        if (iconId == null) {
            return;
        }

        Map<String, Object> icon = restClient.getIcon(iconId);
        if (icon == null) {
            return;
        }

        icon.remove("organization");

        String iconJson = JsonHelper.toSortedJson(icon);

        if (dryrun) {
            logger.info(iconJson);
            return;
        }

        icons.add(storeIconOnFileSystem(serverPackage, iconId, iconJson));
    }

    private File storeIconOnFileSystem(Package serverPackage, String iconId, String iconJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File iconFile = Paths.get(store.getPath(), "icon", iconId.replaceAll("[^A-Za-z0-9]", "_") + ".json").toFile();
        iconFile.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(iconFile.getPath()), iconJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Icon '{}' at '{}'", iconId, iconFile.getPath());
            throw new RuntimeException("Unable to store icon.", e);
        }
        return iconFile;
    }

    private void storeIconOnServer(File iconFile) {
        String iconJson;
        try {
            iconJson = FileUtils.readFileToString(iconFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + iconFile.getPath(), e);
        }

        Map<String, Object> icon = JsonPath.parse(iconJson).read("$");
        
        restClient.setIcon(icon);
    }
}
