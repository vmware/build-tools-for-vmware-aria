package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;

import org.apache.commons.io.FilenameUtils;

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
			logger.info("Content sharing policy directory not found.");
			return;
		}

		List<String> csPolicies = this.getItemListFromDescriptor();

		File[] contentSharingPolicyFiles = this.filterBasedOnConfiguration(contentSharingPolicyFolder,
				new CustomFolderFileFilter(csPolicies));

		if (contentSharingPolicyFiles != null && contentSharingPolicyFiles.length == 0) {
			logger.info("Could not find any Content Sharing Policies.");
			return;
		}

		logger.info("Found Content Sharing Policies. Importing...");

		List<String> contentSharingPoliciesId = this.restClient.getContentSharingPolicyIds();
		List<VraNgContentSharingPolicy> contentSharingPolicies = new ArrayList<>();

		contentSharingPoliciesId.forEach(
				policyId -> {
					VraNgContentSharingPolicy csPolicy = this.restClient.getContentSharingPolicy(policyId);
					contentSharingPolicies.add(csPolicy);
				});

		Map<String, VraNgContentSharingPolicy> csPolicyOnServerByName = contentSharingPolicies.stream()
				.collect(Collectors.toMap(VraNgContentSharingPolicy::getName, item -> item));

		for (File contentSharingPolicyFile : contentSharingPolicyFiles) {
			this.handleContentSharingPolicyImport(contentSharingPolicyFile, csPolicyOnServerByName);
		}
	}

	/**
	 * Handles logic to update or create a content sharing policy
	 *
	 * @param contentSharingPolicyFile
	 * 
	 * @param csPolicyOnServerByName
	*/
	private void handleContentSharingPolicyImport(File contentSharingPolicyFile,
			Map<String, VraNgContentSharingPolicy> csPolicyOnServerByName) {
		String csPolicyNameWithExt = contentSharingPolicyFile.getName();
		String csPolicyName = FilenameUtils.removeExtension(csPolicyNameWithExt);
		logger.info("Attempting to import content sharing policy '{}'", csPolicyName);
		VraNgContentSharingPolicy csPolicy = jsonFileToVraNgContentSharingPolicy(contentSharingPolicyFile);
		// Check if the content sharing policy exists
		VraNgContentSharingPolicy existingRecord = null;
		if (csPolicyOnServerByName.containsKey(csPolicyName)) {
			existingRecord = csPolicyOnServerByName.get(csPolicyName);
		}
		if (existingRecord != null) {
			restClient.updateContentSharingPolicy(csPolicy);
		} else {
			restClient.createContentSharingPolicy(csPolicy);
		}
	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgContentSharingPolicy jsonFileToVraNgContentSharingPolicy(File jsonFile) {
		logger.debug("Converting content sharing policy file to VraNgContentSharingPolicies. Name: '{}'",
				jsonFile.getName());

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
				policyId -> {
					VraNgContentSharingPolicy csPolicy = this.restClient.getContentSharingPolicy(policyId);
					storeContentSharingPolicyOnFilesystem(vraNgPackage, csPolicy);
				});
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
		List<String> contentSharingPoliciesId = this.restClient.getContentSharingPolicyIds();

		contentSharingPoliciesId.forEach(
				policyId -> {
					VraNgContentSharingPolicy csPolicy = this.restClient.getContentSharingPolicy(policyId);
					if (itemNames.contains(csPolicy.getName())) {
						logger.info("exporting '{}'", csPolicy.getName());
						storeContentSharingPolicyOnFilesystem(vraNgPackage, csPolicy);
					}
				});
	}

	/**
	 * Store content sharing policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param contentSharingPolicy contentSharingPolicy representation
	 */
	private void storeContentSharingPolicyOnFilesystem(Package serverPackage,
			VraNgContentSharingPolicy contentSharingPolicy) {
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
			JsonObject csPolicyJsonObject = gson.fromJson(new Gson().toJson(contentSharingPolicy), JsonObject.class);
			JsonObject definition = csPolicyJsonObject.getAsJsonObject("definition");
			JsonArray euArr = definition.getAsJsonArray("entitledUsers");
			if (euArr != null) {
				for (JsonElement eu : euArr) {
					JsonObject entitledUserObj = eu.getAsJsonObject();
					JsonArray itemsArr = entitledUserObj.getAsJsonArray("items");
					for (JsonElement item : itemsArr) {
						JsonObject itemObj = item.getAsJsonObject();
						itemObj.remove("id");
					}
				}
			}
			definition.add("entitledUsers", euArr);
			csPolicyJsonObject.add("definition", definition);
			// write content sharing file
			logger.info("Created content sharing file {}",
					Files.write(Paths.get(contentSharingPolicyFile.getPath()),
							gson.toJson(csPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store content sharing to file %s.", contentSharingPolicyFile.getAbsolutePath()),e);
		}

	}
}
