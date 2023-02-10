package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.*;

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

public class VraNgContentSharingPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for content sharing policy
	 */
	private static final String CONTENT_SHARING_POLICY = "content-sharing";
	private final Logger logger = LoggerFactory.getLogger(VraNgContentSharingPolicyStore.class);

	@Override
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_CONTENT_SHARING_POLICIES);

		// verify directory exists
		File contentSharingPolicyFolder = Paths
				.get(sourceDirectory.getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();
		if (!contentSharingPolicyFolder.exists()) {
			logger.info("Content Sharing Policy Folder Directory not found.");
			return;
		}
		File[] contentSharingPolicyFiles = this.filterBasedOnConfiguration(contentSharingPolicyFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (contentSharingPolicyFiles != null && contentSharingPolicyFiles.length == 0) {
			logger.info("Could not find any Content Sharing Policies.");
			return;
		}

		logger.info("Found Content Sharing Policies. Importing...");

		//TO DO
		// Map<String, VraNgContentSharingPolicy> csPolicyOnServerByName = this.restClient.getContentSharingPolicies()
		// 		.stream()
		// 		.collect(Collectors.toMap(VraNgContentSharingPolicy::getName, item -> item));

		// List<VraNgContentSharingPolicy> contentSharingPolicies = new ArrayList<>();

		// for (File contentSharingPolicyFile : contentSharingPolicyFiles) {
		// 	this.handleContentSharingPolicyImport(contentSharingPolicyFile, csPolicyOnServerByName);
		// }

	}

	private void handleContentSharingPolicyImport(File contentSharingPolicyFile,
			Map<String, VraNgContentSharingPolicy> csPolicyOnServerByName) {
		String csPolicyName = contentSharingPolicyFile.getName();
		logger.debug("Attempting to import cs policy \"" + csPolicyName + "\"");
		VraNgContentSharingPolicy csPolicy= jsonFileToVraNgContentSharingPolicy(contentSharingPolicyFile);
		String csPolicyID;
		// Check if the cs policy exists
		VraNgContentSharingPolicy existingRecord = null;
		if (csPolicyOnServerByName.containsKey(csPolicyName)) {
			existingRecord = csPolicyOnServerByName.get(csPolicyName);
		}
		if (existingRecord == null) {
			csPolicyID = restClient.createContentSharingPolicy(csPolicy);
			csPolicy.setId(csPolicyID);
		} else {
			csPolicyID = existingRecord.getId();
			csPolicy.setId(csPolicyID);
			restClient.updateContentSharingPolicy(csPolicy);
		}

	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getContentSharingPolicy();
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgContentSharingPolicy jsonFileToVraNgContentSharingPolicy(File jsonFile) {
		logger.debug("Converting catalog item file to VraNgCatalogItem. Name: '{}'", jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgContentSharingPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	@Override
	protected void exportStoreContent() {
		List<String> contentSharingPoliciesId = this.restClient.getContentSharingPolicyIds();
		
		contentSharingPoliciesId.forEach(
				policyId ->  { 
					VraNgContentSharingPolicy csPolicy= this.restClient.getContentSharingPolicy(policyId);
					storeContentSharingPolicyOnFilesystem(vraNgPackage, csPolicy);
				 } );
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
	}

	/**
	 * Store content sharing policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param contentSharingPolicy contentSharingPolicy representation
	 */
	private void storeContentSharingPolicyOnFilesystem(Package serverPackage, VraNgContentSharingPolicy contentSharingPolicy) {
		logger.debug("Storing contentSharingPolicy {}", contentSharingPolicy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		File contentSharingPolicyFile = Paths.get(
				store.getPath(),
				DIR_CONTENT_SHARING_POLICIES,
				CONTENT_SHARING_POLICY,
				contentSharingPolicy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!contentSharingPolicyFile.getParentFile().isDirectory()
				&& !contentSharingPolicyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", contentSharingPolicyFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			// write content sharing file
			logger.info("Created content sharing file {}",
					Files.write(Paths.get(contentSharingPolicyFile.getPath()),
							gson.toJson(contentSharingPolicy).getBytes(),
							StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store custom form to file %s.", contentSharingPolicyFile.getAbsolutePath()),
					e);
		}

	}
}
