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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;

public class VraCustomFormPackageStoreExtention implements PackageStoreExtention<VraPackageDescriptor> {

	private final Logger logger = LoggerFactory.getLogger(VraCustomFormPackageStoreExtention.class);
	private final RestClientVra restClient;
	
	public VraCustomFormPackageStoreExtention(RestClientVra restClient) {
	    this.restClient = restClient;
	}

    @Override
    public Package exportPackage(Package serverPackage,  VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
        logger.debug("vRA Custom Form export extention is enabled.");
        List<File> customFormFiles = new ArrayList<>();

        if (vraPackageDescriptor == null) {
            logger.debug("vRA Custom Form package export requires vRA package descriptor.");
            return serverPackage;
        }
        
        List<String> compositeBlueprintNames = vraPackageDescriptor.getCompositeBlueprint();
        if (compositeBlueprintNames == null) {
            logger.debug("No Blueprints defined for export. Skipping Custom Form export.");
            return serverPackage;
        }

        for (Map<String, String> contentObj : restClient.getPackageContents(serverPackage.getId())) {
            if (!"composite-blueprint".equals(contentObj.get("contentTypeId"))) { continue; }
                
            String formId = contentObj.get("contentId");
            String formJson = restClient.getBlueprintCustomForm(formId);
            
            if (formJson == null) {
                logger.debug("Blueprint '{}' is not associated with custom form.", formId);
                continue;
            }
            
            // PrettyPrint JSON. Setting "Lenient" is important as vRA returns the JSON non-complied form.  
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            formJson = gson.toJson(gson.fromJson(formJson, JsonObject.class));
                        
            if (dryrun) {
                logger.info(formJson);
                continue;
            }
            
            customFormFiles.add(storeCustomFormOnFileSystem(serverPackage, formId, formJson));
        }
        
        try {
            new PackageManager(serverPackage).addToExistingZip(customFormFiles);
        } catch (IOException e) {
            throw new RuntimeException("Error adding files to zip", e);
        }
        
        return serverPackage;
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun) {
        logger.debug("vRA Custom Form import extention is enabled.");
        if (dryrun) {
            logger.debug("vRA does not support Custom From validation through the REST API.");
            return pkg;
        }
        
        File tmp;
        try {
            tmp = Files.createTempDirectory("iac-custom-forms").toFile();
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }
        
        File formsFolder = Paths.get(tmp.getPath(), "custom-form").toFile();
        if(formsFolder.exists()) {
            FileUtils.listFiles(formsFolder, new String[]{"json"}, false).stream().forEach(form -> storeCustomFormOnServer(form));
        }
        return pkg;
    }
    
    
    private File storeCustomFormOnFileSystem(Package serverPackage, String formId, String formJson) {
        File store = new File(serverPackage.getFilesystemPath()).getParentFile();
        File form = Paths.get(store.getPath(), "custom-form", formId + ".json").toFile(); 
        form.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(form.getPath()), formJson.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            logger.error("Unable to store Blueprint Custom From '{}' at '{}'", formId, form.getPath());
            throw new RuntimeException("Unable to store custom form.", e);
        }
        return form;
    }
    
    private void storeCustomFormOnServer(File jsonFile) {
        try {
            String formId = FilenameUtils.removeExtension(jsonFile.getName());
            String formJson = FileUtils.readFileToString(jsonFile, "UTF-8");
            restClient.setBlueprintCustomForm(formId, formJson);
            restClient.activateBlueprintCustomForm(formId);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
        }
    }
}
