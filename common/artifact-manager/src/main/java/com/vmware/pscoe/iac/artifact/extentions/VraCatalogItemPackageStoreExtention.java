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
import java.util.HashMap;
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

public class VraCatalogItemPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

    private final Logger logger = LoggerFactory.getLogger(VraCatalogItemPackageStoreExtention.class);
    private final RestClientVra restClient;

    public VraCatalogItemPackageStoreExtention(RestClientVra restClient) {
        this.restClient = restClient;
    }

    @Override
    public Package exportPackage(Package serverPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Catalog Item export extention is enabled.");
        List<File> catalogItemFiles = new ArrayList<>();

        if (vraPackageDescriptor == null) {
            logger.debug("vRA Catalog Item package export requires vRA package descriptor.");
            return serverPackage;
        }

        for (Map<String, String> contentObj : restClient.getPackageContents(serverPackage.getId())) {
            String contentTypeId = contentObj.get("contentTypeId");
            if ("composite-blueprint".equals(contentTypeId)) {
                loadCatalogItem(catalogItemFiles, serverPackage, contentObj, dryrun);
            } else if ("xaas-blueprint".equals(contentTypeId)) {
                loadCatalogItem(catalogItemFiles, serverPackage, contentObj, dryrun);
            }
        }

        if (!catalogItemFiles.isEmpty()) {
            try {
                new PackageManager(serverPackage).addToExistingZip(catalogItemFiles);
            } catch (IOException e) {
                throw new RuntimeException("Error adding files to zip", e);
            }
        }

        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Catalog Item import extention is enabled.");
        if (dryrun) {
            logger.debug("vRA does not support Catalog Item validation through the REST API.");
            return pkg;
        }

        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-catalog-items").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        File catalogItemsFolder = Paths.get(tmp.getPath(), "catalog-item").toFile();
        if (catalogItemsFolder.exists()) {
            FileUtils.listFiles(catalogItemsFolder, new String[] { "json" }, false).stream()
                    .forEach(catalogItem -> storeCatalogItemOnServer(catalogItem));
        }
        return pkg;
    }

    @SuppressWarnings("unchecked")
    private void loadCatalogItem(List<File> catalogItems, Package serverPackage, Map<String, String> contentObj,
            boolean dryrun) {
        String name = contentObj.get("name");
        Map<String, Object> serverCatalogItem = restClient.getCatalogItemByName(name);

        if (serverCatalogItem == null) {
            logger.debug("Catalog Item '{}' does not exist.", name);
            return;
        }

        Map<String, Object> catalogItem = new HashMap<>();
        catalogItem.put("name", serverCatalogItem.get("name"));
        catalogItem.put("iconId", serverCatalogItem.get("iconId"));
        catalogItem.put("quota", serverCatalogItem.get("quota"));
        catalogItem.put("status", serverCatalogItem.get("status"));
        catalogItem.put("statusName", serverCatalogItem.get("statusName"));

        Map<String, Object> catalogItemService = (Map<String, Object>) serverCatalogItem.get("serviceRef");
        if (catalogItemService != null) {
            catalogItem.put("service", catalogItemService.get("label"));
        }

        String catalogItemJson = JsonHelper.toSortedJson(catalogItem);

        if (dryrun) {
            logger.info(catalogItemJson);
            return;
        }

        catalogItems.add(storeCatalogItemOnFileSystem(serverPackage, name, catalogItemJson));
    }

    private File storeCatalogItemOnFileSystem(Package serverPackage, String name, String catalogItemJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File catalogItemFile = Paths.get(store.getPath(), "catalog-item", name + ".json").toFile();
        catalogItemFile.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(catalogItemFile.getPath()), catalogItemJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Catalog Item '{}' at '{}'", name, catalogItemFile.getPath());
            throw new RuntimeException("Unable to store catalog item.", e);
        }
        return catalogItemFile;
    }

    private void storeCatalogItemOnServer(File catalogItemFile) {
        String catalogItemJson;
        try {
            catalogItemJson = FileUtils.readFileToString(catalogItemFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + catalogItemFile.getPath(), e);
        }

        Map<String, Object> catalogItem = JsonPath.parse(catalogItemJson).read("$");
        String catalogItemName = (String) catalogItem.get("name");

        Map<String, Object> serverCatalogItem = restClient.getCatalogItemByName(catalogItemName);
        serverCatalogItem.put("iconId", catalogItem.get("iconId"));
        serverCatalogItem.put("quota", catalogItem.get("quota"));
        serverCatalogItem.put("status", catalogItem.get("status"));
        serverCatalogItem.put("statusName", catalogItem.get("statusName"));

        String serviceName = (String) catalogItem.get("service");
        if (serviceName != null) {
            Map<String, Object> serverCatalogService = restClient.getCatalogServiceByName(serviceName);
            if (serverCatalogService == null) {
                throw new RuntimeException("Unable to find Catalog Service by name: " + serviceName + ". Please pre-create it first in vRA Catalog.");
            }
            Map<String, Object> serviceRef = new HashMap<>();
            serviceRef.put("id", serverCatalogService.get("id"));
            serviceRef.put("label", serverCatalogService.get("name"));
            serverCatalogItem.put("serviceRef", serviceRef);
        } else {
            serverCatalogItem.put("serviceRef", null);
        }

        restClient.setCatalogItem(serverCatalogItem);
    }
}
