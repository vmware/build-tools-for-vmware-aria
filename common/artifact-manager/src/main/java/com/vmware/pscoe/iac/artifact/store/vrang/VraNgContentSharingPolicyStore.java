package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;


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
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for content sharing policy.
	 */
	private static final String CONTENT_SHARING_POLICY = "content-sharing";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgContentSharingPolicyStore.class);

	/**
	 * Imports content.
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory",
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES);
		// verify directory exists
		File contentSharingPolicyFolder = Paths
				.get(sourceDirectory.getPath(),
						com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
						CONTENT_SHARING_POLICY)
				.toFile();
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

		for (File contentSharingPolicyFile : contentSharingPolicyFiles) {
			if (!contentSharingPolicyFile.getName().startsWith(".")) {
				//exclude hidden files e.g. .DS_Store
				this.handleContentSharingPolicyImport(contentSharingPolicyFile);
			}
		}
	}

	/**
	 * .
	 * Handles logic to update or create a content sharing policy.
	 *
	 * @param contentSharingPolicyFile file where the policy is stored.
	 */
	private void handleContentSharingPolicyImport(final File contentSharingPolicyFile) {

		VraNgContentSharingPolicy csPolicy = jsonFileToVraNgContentSharingPolicy(contentSharingPolicyFile);
		logger.info("Attempting to import content sharing policy '{}', from file '{}'", csPolicy.getName(), contentSharingPolicyFile.getName());
		this.enrichContentSharingPolicy(csPolicy);
		this.restClient.createContentSharingPolicy(csPolicy);
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of content sharing policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		logger.debug("{}->getItemListFromDescriptor", VraNgContentSharingPolicyStore.class);

		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			logger.debug("Descriptor policy is null");
			return null;
		} else {
			logger.debug("Found items {}", this.vraNgPackageDescriptor.getPolicy().getContentSharing());
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgContentSharingPolicy jsonFileToVraNgContentSharingPolicy(final File jsonFile) {
		logger.debug("Converting content sharing policy file to VraNgContentSharingPolicies. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgContentSharingPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Used while import to override the orgId and ProjectId for the receiving vRA instance.
	 *  
	 * @param csPolicy
	 */
	private void enrichContentSharingPolicy(final VraNgContentSharingPolicy csPolicy) {
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
		csPolicy.setOrgId(organizationId);
		csPolicy.setProjectId(this.restClient.getProjectId());
	}

	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		List<VraNgContentSharingPolicy> csPolicies = this.restClient.getContentSharingPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		csPolicies.forEach(
				policy -> {
					storeContentSharingPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in content.yaml.
	 * 
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		this.logger.debug("{}->exportStoreContent({})", this.getClass(), itemNames.toString());
		List<VraNgContentSharingPolicy> csPolicies = this.restClient.getContentSharingPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);


		csPolicies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						storeContentSharingPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
					}
				});
	}

	/**
	 * Store content sharing policy in JSON file.
	 * @param policyFolderPath Path to the folder where to store the file.
	 * @param contentSharingPolicy the policy to store.
	 * @param currentPoliciesOnFileSystem a map of file names and policies, already present in the folder, used to avoid duplicate file names.
	 */
	private void storeContentSharingPolicyOnFilesystem(final Path policyFolderPath,
			final VraNgContentSharingPolicy contentSharingPolicy, Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem) {

		File contentSharingPolicyFile = getPolicyFile(policyFolderPath, contentSharingPolicy, currentPoliciesOnFileSystem);

		logger.info("Storing contentSharingPolicy '{}', to file '{}", contentSharingPolicy.getName(), contentSharingPolicyFile.getPath());
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
			//after write, put currently policy on the map for the next iteration
			String fileName = contentSharingPolicyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, contentSharingPolicy);

		} catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store content sharing to file %s.", contentSharingPolicyFile.getAbsolutePath()),
					e);
		}

	}
	/**
	 * @param policyFolderPath the correct subfolder path for the policy type.
	 * @param policy the policy that is exported.
	 * @param currentPoliciesOnFileSystem  all the other policies currently in the folder.
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, VraNgContentSharingPolicy policy, Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem) {
		String filename = policy.getName();
		String policyName = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}
		//see if filename already exists in map.
		int index = 1;
		boolean match = false;
		while (!match) {
			//logger.debug("File name {}, index {} , name with Index {};", filename, index, filenameWithIndex);

			if (currentPoliciesOnFileSystem.containsKey(filename)) {
				//check if policy is our policy from previous run or a different one.
				if (policy.getId().equals(currentPoliciesOnFileSystem.get(filename).getId())) {
					match = true;
				} else {
					filename = policyName + "_" + index;
					index++;

				}
			} else {
				match = true;
			}
		}

		logger.debug("Final Filename is {}, for policy with name {}", filename, policy.getName());

		File policyFile = Paths.get(
			String.valueOf(policyFolderPath),
			filename + CUSTOM_RESOURCE_SUFFIX).toFile();
		return policyFile;
	}

	/**
	 * Read the filesystem where policies will be stored, make a map of pre existing files there, to avoid duplication and/or unintentional overwriting.
	 * @param policyFolderPath get actual path where policies should be stored.
	 * @return a map of filenames and policies, found in the path.
	 */
	private Map<String, VraNgContentSharingPolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {

		//First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.toFile().getAbsolutePath());
		}

		File[] policyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = new HashMap<>();

		for (File policyFile : policyFiles) {
			String fileNameWithExt = policyFile.getName();
			//exclude hidden files e.g. .DS_Store
			if (!fileNameWithExt.startsWith(".")) {
				String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
				currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgContentSharingPolicy(policyFile));
			}
		}
		return currentPoliciesOnFileSystem;
	}

	/**
	 * Calculate content-sharing policies sub-folder absolute filesystem path.
	 * @return the content-sharing policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
			store.getPath(),
			VraNgDirs.DIR_POLICIES,
			CONTENT_SHARING_POLICY);
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}

}
