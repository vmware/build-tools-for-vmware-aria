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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.rest.helpers.JsonHelper;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageMemberType;

public class VraGlobalPropertyDefinitionPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

    private final Logger logger = LoggerFactory.getLogger(VraGlobalPropertyDefinitionPackageStoreExtention.class);
    private final RestClientVra restClient;

    public VraGlobalPropertyDefinitionPackageStoreExtention(RestClientVra restClient) {
        this.restClient = restClient;
    }

    @Override
    public Package exportPackage(Package serverPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Global Property Definition export extention is enabled.");
        if (vraPackageDescriptor == null) {
            logger.debug("vRA Global Property Definition package export requires vRA package descriptor.");
            return serverPackage;
        }

        List<String> propertyDefinitionNames = vraPackageDescriptor.getGlobalPropertyDefinition();
        if (propertyDefinitionNames == null) {
            return serverPackage;
        }

        List<File> propertyDefinitionFiles = new ArrayList<>();
        List<Map<String, Object>> propertyDefinitions = restClient.getGlobalPropertyDefinitions();

        for (String propertyDefinitionName : propertyDefinitionNames) {
            Map<String, Object> propertyDefinition = propertyDefinitions.stream()
                .filter(pd -> pd.get("id").equals(propertyDefinitionName))
                .findFirst()
                .orElse(null);

            if (propertyDefinition == null) {
                logger.warn("Content with Type[" + VraPackageMemberType.GLOBAL_PROPERTY_DEFINITION.toString() + "], " + 
                    "Name[" + propertyDefinitionName + "] and supplied credentials cannot be found on the sever. Note that name is case sensitive.");
                continue;                
            }

            propertyDefinition.remove("tenantId");

            String propertyDefinitionJson = JsonHelper.toSortedJson(propertyDefinition);

            if (dryrun) {
                logger.info(propertyDefinitionJson);
                continue;
            }

            propertyDefinitionFiles.add(storePropertyDefinitionOnFileSystem(serverPackage, propertyDefinitionName, propertyDefinitionJson));
        }

        try {
            new PackageManager(serverPackage).addToExistingZip(propertyDefinitionFiles);
        } catch (IOException e) {
            throw new RuntimeException("Error adding files to zip", e);
        }

        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Global Property Definition import extention is enabled.");
        if (dryrun) {
            return pkg;
        }

        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-global-property-definitions").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        File propertyDefinitionsFolder = Paths.get(tmp.getPath(),  VraPackageMemberType.GLOBAL_PROPERTY_DEFINITION.toString()).toFile();
        if (propertyDefinitionsFolder.exists()) {
            FileUtils.listFiles(propertyDefinitionsFolder, new String[] { "json" }, false).stream()
                    .forEach(propertyDefinition -> storePropertyDefinitionOnServer(propertyDefinition));
        }
        return pkg;
    }

    private File storePropertyDefinitionOnFileSystem(Package serverPackage, String propertyDefinitionName, String propertyDefinitionJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File propertyDefinition = Paths.get(
            store.getPath(), 
            VraPackageMemberType.GLOBAL_PROPERTY_DEFINITION.toString(),
            propertyDefinitionName.replaceAll("[\\*|:\"'\\/\\\\]", "") + ".json").toFile();
        propertyDefinition.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(propertyDefinition.getPath()), propertyDefinitionJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Global Property Definition", propertyDefinitionName, propertyDefinition.getPath());
            throw new RuntimeException("Unable to store Global Property Definition.", e);
        }
        return propertyDefinition;
    }

    private void storePropertyDefinitionOnServer(File jsonFile) {
        try {
            String propertyDefinitionJson = FileUtils.readFileToString(jsonFile, "UTF-8");
            String propertyDefinitionName = JsonPath.parse(propertyDefinitionJson).read("$.id");
            restClient.importGlobalPropertyDefinition(propertyDefinitionName, propertyDefinitionJson);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
        }
    }
}
