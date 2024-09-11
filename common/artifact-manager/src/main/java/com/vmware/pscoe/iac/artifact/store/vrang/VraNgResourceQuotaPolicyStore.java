/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceQuotaPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;

import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VraNgResourceQuotaPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for resource quota policy.
	 */
	private static final String RESOURCE_QUOTA_POLICY = "resource-quota";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgResourceQuotaPolicyStore.class);

	/**
	 * Imports policies found in specified folder on server, according to filter specified in content.yml file.
	 * If there is a list of policy names - import only the specified policies.
	 * If there is no list, import everything.
	 * @param sourceDirectory the folder where policy files are stored.
	 */
	@Override
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory",
			com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES);
		// verify directory exists
		File policyFolder = Paths
			.get(sourceDirectory.getPath(),
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
				RESOURCE_QUOTA_POLICY)
			.toFile();
		if (!policyFolder.exists()) {
			logger.info("Resource Quota policy directory not found.");
			return;
		}

		List<String> rqPolicies = this.getItemListFromDescriptor();

		File[] resourceQuotaPolicyFiles = this.filterBasedOnConfiguration(policyFolder,
			new CustomFolderFileFilter(rqPolicies));

		if (resourceQuotaPolicyFiles != null && resourceQuotaPolicyFiles.length == 0) {
			logger.info("Could not find any Resource Quota Policies.");
			return;
		}

		logger.info("Found Resource Quota Policies. Importing ...");

		for (File policyFile : resourceQuotaPolicyFiles) {
			//exclude hidden files e.g. .DS_Store
			//exclude files that do not end with a '.json' extension as defined in CUSTOM_RESOURCE_SUFFIX
			String filename = policyFile.getName();
			if (!filename.startsWith(".") && filename.endsWith(CUSTOM_RESOURCE_SUFFIX)) {
				this.handleResourceQuotaPolicyImport(policyFile);
			} else {
				logger.warn("Skipped unexpected file '{}'", filename);
			}
		}
	}

	/**
	 * .
	 * Handles logic to update or create a resource quota policy.
	 *
	 * @param resourceQuotaPolicyFile to read from
	 */
	private void handleResourceQuotaPolicyImport(final File resourceQuotaPolicyFile) {

		VraNgResourceQuotaPolicy policy = jsonFileToVraNgResourceQuotaPolicy(resourceQuotaPolicyFile);

		logger.info("Attempting to import resource quota policy '{}'", policy.getName());

		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		//if the policy has a project property, replace it with current project id.
		//if the policy does not have a project property - replacing it will change the policy,
		// so do not replace a null or blank value.
		if (policy.getProjectId() != null && !(policy.getProjectId().isBlank()) && !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
		this.restClient.createResourceQuotaPolicy(policy);
	}

	/**
	 * Converts a json catalog item file to VraNgResourceQuotaPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgResourceQuotaPolicy
	 */
	private VraNgResourceQuotaPolicy jsonFileToVraNgResourceQuotaPolicy(final File jsonFile) {
		logger.debug("Converting resource quota policy file to VraNgResourceQuotaPolicies. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgResourceQuotaPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}
	/**
	 * getItemListFromDescriptor.
	 * @return list of policy names to import or export.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			logger.info("Descriptor policy is null");
			return null;
		} else {
			logger.info("Found items {}", this.vraNgPackageDescriptor.getPolicy().getResourceQuota());
			return this.vraNgPackageDescriptor.getPolicy().getResourceQuota();
		}
	}
	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {
		logger.debug(this.getClass() + "->exportStoreContent()");
		List<VraNgResourceQuotaPolicy> rqPolicies = this.restClient.getResourceQuotaPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgResourceQuotaPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		rqPolicies.forEach(
				policy -> {
					storeResourceQuotaPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in content.yaml.
	 * 
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		logger.debug(this.getClass() + "->exportStoreContent({})", itemNames.toString());
		List<VraNgResourceQuotaPolicy> rqPolicies = this.restClient.getResourceQuotaPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgResourceQuotaPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		rqPolicies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						storeResourceQuotaPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
					}
				});
	}



	/**
	 * Store resource quota policy in JSON file.
	 *
	 * @param policyFolderPath   the subfolder where the policy will be stored.
	 * @param policy the policy object to store
	 * @param currentPoliciesOnFileSystem  a map of file names and policies, already present in the folder, used to avoid duplicate file names.
	 */
	private void storeResourceQuotaPolicyOnFilesystem(final Path policyFolderPath,
													  final VraNgResourceQuotaPolicy policy,
													  Map<String, VraNgResourceQuotaPolicy> currentPoliciesOnFileSystem) {
		File policyFile = getPolicyFile(policyFolderPath, policy, currentPoliciesOnFileSystem);
		logger.debug("Storing resource quota policy '{}', to file '{}", policy.getName(), policyFile.getPath());


		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject rqPolicyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);
			logger.info("Created resource quota file {}",
					Files.write(Paths.get(policyFile.getPath()),
							gson.toJson(rqPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
			//after write, put currently policy on the map for the next iteration
			String fileName = policyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, policy);
		} catch (IOException e) {
			logger.error("Unable to create resource quota {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store resource quota to file %s.", policyFile.getAbsolutePath()),
					e);
		}

	}
	/**
	 * @param policyFolderPath the correct subfolder path for the policy type.
	 * @param policy the policy that is exported.
	 * @param currentPoliciesOnFileSystem  all the other policies currently in the folder.
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, VraNgResourceQuotaPolicy policy, Map<String, VraNgResourceQuotaPolicy> currentPoliciesOnFileSystem) {
		String filename = policy.getName();
		String policyName = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}
		//see if filename already exists in map.
		int index = 1;
		boolean match = false;
		while (!match) {
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
	private Map<String, VraNgResourceQuotaPolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {

		//First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.toFile().getAbsolutePath());
		}

		File[] policyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgResourceQuotaPolicy> currentPoliciesOnFileSystem = new HashMap<>();

		for (File policyFile : policyFiles) {
			String fileNameWithExt = policyFile.getName();
			//exclude hidden files e.g. .DS_Store
			if (!fileNameWithExt.startsWith(".")) {
				String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
				currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgResourceQuotaPolicy(policyFile));
			}
		}
		return currentPoliciesOnFileSystem;
	}

	/**
	 * Calculate resource quota policies sub-folder absolute filesystem path.
	 * @return the resource quota policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
			store.getPath(),
			VraNgDirs.DIR_POLICIES,
			RESOURCE_QUOTA_POLICY);
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}

}
