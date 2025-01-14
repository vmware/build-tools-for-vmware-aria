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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

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

import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;

public final class VraNgLeasePolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for Lease policy.
	 */
	private static final String LEASE_POLICY = "lease";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgLeasePolicy.class);

	protected List<VraNgLeasePolicy> getAllServerContents() {
		return this.restClient.getLeasePolicies();
	}

	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	/**
	 * Imports content.
	 * 
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory",
				com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_POLICIES);
		// verify directory exists
		File leasePolicyFolder = Paths
				.get(sourceDirectory.getPath(),
						com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_POLICIES,
						LEASE_POLICY)
				.toFile();
		if (!leasePolicyFolder.exists()) {
			logger.info("Lease policy directory not found.");
			return;
		}

		List<String> policies = this.getItemListFromDescriptor();

		File[] leasePolicyFiles = this.filterBasedOnConfiguration(leasePolicyFolder,
				new CustomFolderFileFilter(policies));

		if (leasePolicyFiles != null && leasePolicyFiles.length == 0) {
			logger.info("Could not find any Lease Policies.");
			return;
		}

		logger.info("Found Lease Policies. Importing...");
		for (File policyFile : leasePolicyFiles) {
			// exclude hidden files e.g. .DS_Store
			// exclude files that do not end with a '.json' extension as defined in
			// CUSTOM_RESOURCE_SUFFIX
			String filename = policyFile.getName();
			if (!filename.startsWith(".") && filename.endsWith(CUSTOM_RESOURCE_SUFFIX)) {
				this.handleLeasePolicyImport(policyFile);
			} else {
				logger.warn("Skipped unexpected file '{}'", filename);
			}
		}
	}

	private void handleLeasePolicyImport(final File leasePolicyFile) {
		VraNgLeasePolicy policy = jsonFileToVraNgLeasePolicy(leasePolicyFile);
		logger.info("Attempting to import Lease policy '{}'", policy.getName());

		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		// if the policy has a project property, replace it with current project id.
		// if the policy does not have a project property - replacing it will change the
		// policy,
		// so do not replace a null or blank value.
		if (policy.getProjectId() != null && !(policy.getProjectId().isBlank())
				&& !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
		this.restClient.createLeasePolicy(policy);
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of Lease policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getLease();
		}
	}

	/**
	 * Converts a json catalog item file to VraNgLeasePolicy.
	 *
	 * @param jsonFile to read from
	 *
	 * @return VraNgLeasePolicy instance
	 */
	private VraNgLeasePolicy jsonFileToVraNgLeasePolicy(final File jsonFile) {
		logger.debug("Converting Lease policy file to VraNgLeasePolicy. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgLeasePolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		List<VraNgLeasePolicy> policies = this.restClient.getLeasePolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgLeasePolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		policies.forEach(
				policy -> {
					storeLeasePolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in
	 * content.yaml.
	 * 
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		this.logger.debug("{}->exportStoreContent({})", this.getClass(), itemNames.toString());
		List<VraNgLeasePolicy> policies = this.restClient.getLeasePolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgLeasePolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);
		policies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						storeLeasePolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
					}
				});
	}

	/**
	 * Store Lease policy in JSON file.
	 *
	 * @param policyFolderPath            the subfolder where the policy will be
	 *                                    stored.
	 * @param policy                      the policy object to store
	 * @param currentPoliciesOnFileSystem a map of file names and policies, already
	 *                                    present in the folder, used to avoid
	 *                                    duplicate file names.
	 */
	private void storeLeasePolicyOnFilesystem(final Path policyFolderPath,
			final VraNgLeasePolicy policy,
			Map<String, VraNgLeasePolicy> currentPoliciesOnFileSystem) {
		File policyFile = getPolicyFile(policyFolderPath, policy, currentPoliciesOnFileSystem);
		logger.info("Storing lease  policy '{}', to file '{}", policy.getName(), policyFile.getPath());

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);
			// write Lease file
			logger.info("Created Lease file {}",
					Files.write(Paths.get(policyFile.getPath()),
							gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
			// after write, put currently policy on the map for the next iteration
			String fileName = policyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, policy);
		} catch (IOException e) {
			logger.error("Unable to create Lease {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store Lease to file %s.", policyFile.getAbsolutePath()),
					e);
		}

	}

	/**
	 * @param policyFolderPath            the correct subfolder path for the policy
	 *                                    type.
	 * @param policy                      the policy that is exported.
	 * @param currentPoliciesOnFileSystem all the other policies currently in the
	 *                                    folder.
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, VraNgLeasePolicy policy,
			Map<String, VraNgLeasePolicy> currentPoliciesOnFileSystem) {
		String filename = policy.getName();
		String policyName = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}
		// see if filename already exists in map.
		int index = 1;
		boolean match = false;
		while (!match) {
			// logger.debug("File name {}, index {} , name with Index {};", filename, index,
			// filenameWithIndex);

			if (currentPoliciesOnFileSystem.containsKey(filename)) {
				// check if policy is our policy from previous run or a different one.
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
	 * Read the filesystem where policies will be stored, make a map of pre existing
	 * files there, to avoid duplication and/or unintentional overwriting.
	 * 
	 * @param policyFolderPath get actual path where policies should be stored.
	 * @return a map of filenames and policies, found in the path.
	 */
	private Map<String, VraNgLeasePolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {

		// First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()
				&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.toFile().getAbsolutePath());
		}

		File[] policyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgLeasePolicy> currentPoliciesOnFileSystem = new HashMap<>();

		for (File policyFile : policyFiles) {
			String fileNameWithExt = policyFile.getName();
			// exclude hidden files e.g. .DS_Store
			if (!fileNameWithExt.startsWith(".")) {
				String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
				currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgLeasePolicy(policyFile));
			}
		}
		return currentPoliciesOnFileSystem;
	}

	/**
	 * Calculate lease policies sub-folder absolute filesystem path.
	 * 
	 * @return the lease policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_POLICIES,
				LEASE_POLICY);
		if (!policyFolderPath.toFile().isDirectory()
				&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}

}
