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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Store for Approval Policies.
 */
public final class VraNgApprovalPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for approval policy.
	 */
	private static final String APPROVAL = "approval";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgApprovalPolicyStore.class);

	/**
	 * Retrieves all approval policies from the server.
	 *
	 * Used for deletion
	 *
	 * @return A List of approval policies
	 */
	protected List<VraNgApprovalPolicy> getAllServerContents() {
		return this.restClient.getApprovalPolicies();
	}

	/**
	 * Deletes an approval policy by id.
	 *
	 * @param resId - the approval policy id
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	/**
	 * Imports policies found in specified folder on server, according to filter
	 * specified in content.yml file.
	 * If there is a list of policy names - import only the specified policies.
	 * If there is no list, import everything.
	 * 
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
						APPROVAL)
				.toFile();
		if (!policyFolder.exists()) {
			logger.info("Approval policy directory not found.");
			return;
		}

		List<String> policies = this.getItemListFromDescriptor();

		File[] approvalPolicyFiles = this.filterBasedOnConfiguration(policyFolder,
				new CustomFolderFileFilter(policies));

		if (approvalPolicyFiles != null && approvalPolicyFiles.length == 0) {
			logger.info("Could not find any Approval Policies.");
			return;
		}
		logger.info("Found Approval Policies. Importing ...");
		for (File policyFile : approvalPolicyFiles) {
			// exclude hidden files e.g. .DS_Store
			// exclude files that do not end with a '.json' extension as defined in
			// CUSTOM_RESOURCE_SUFFIX
			String filename = policyFile.getName();
			if (!filename.startsWith(".") && filename.endsWith(CUSTOM_RESOURCE_SUFFIX)) {
				this.handlePolicyImport(policyFile);
			} else {
				logger.warn("Skipped unexpected file '{}'", filename);
			}
		}
	}

	/**
	 * Imports policy file to server , replacing the organization id.
	 *
	 * @param approvalPolicyFile the policy to import.
	 */
	private void handlePolicyImport(final File approvalPolicyFile) {
		// convert file to policy object.
		VraNgApprovalPolicy policy = jsonFileToVraNgApprovalPolicy(approvalPolicyFile);
		// replace object organization id with target organization Id
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		logger.info("Attempting to import approval policy '{}', from file '{}'", policy.getName(),
				approvalPolicyFile.getName());

		// if the policy has a project property, replace it with current project id.
		// if the policy does not have a project property - replacing it will change the
		// policy,
		// so do not replace a null or blank value.
		// also, if the policy organization matches current organization, this means the
		// project also matches either the current project or another project in this
		// organization, no need to overwrite, because it is either the current project,
		// or it is another project in the organization, and the push will fail.
		if (policy.getProjectId() != null && !(policy.getProjectId().isBlank())
				&& !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
		this.restClient.createApprovalPolicy(policy);
	}

	/**
	 * getItemListFromDescriptor.
	 * 
	 * @return list of policy names to import or export.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getApproval();
		}
	}

	/**
	 * Exporting every policy of this type found on server.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgApprovalPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);
		List<VraNgApprovalPolicy> rqPolicies = this.restClient.getApprovalPolicies();

		rqPolicies.forEach(
				policy -> {
					logger.debug("exporting '{}'", policy.getName());
					storeApprovalPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
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
		List<VraNgApprovalPolicy> policies = this.restClient.getApprovalPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgApprovalPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		policies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						logger.debug("exporting '{}'", policy.getName());
						storeApprovalPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
					}
				});
	}

	/**
	 * Store approval policy in JSON file.
	 *
	 * @param policyFolderPath            the folder path where to export.
	 * @param policy                      the policy object to export.
	 * @param currentPoliciesOnFileSystem map of other policy files in the same
	 *                                    folder, to escape overwriting duplicate
	 *                                    files..
	 */
	private void storeApprovalPolicyOnFilesystem(final Path policyFolderPath,
			final VraNgApprovalPolicy policy,
			Map<String, VraNgApprovalPolicy> currentPoliciesOnFileSystem) {
		logger.info("Storing  {}", policy.getName());

		File policyFile = getPolicyFile(policyFolderPath, policy, currentPoliciesOnFileSystem);

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);

			logger.info("Created approval policy file {}",
					Files.write(Paths.get(policyFile.getPath()),
							gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
			// after write, put currently policy on the map for the next iteration
			String fileName = policyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, policy);
		} catch (IOException e) {
			logger.error("Unable to create approval policy  {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store approval policy to file %s.", policyFile.getAbsolutePath()),
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

	private File getPolicyFile(Path policyFolderPath, VraNgApprovalPolicy policy,
			Map<String, VraNgApprovalPolicy> currentPoliciesOnFileSystem) {
		String filename = policy.getName();
		String policyName = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}
		// see if filename already exists in map.
		int index = 1;
		boolean match = false;
		while (!match) {
			if (currentPoliciesOnFileSystem.containsKey(filename)) {
				// check if policy is our policy from previous run or a different one.
				if (policy.getId().equals(currentPoliciesOnFileSystem.get(filename).getId())) {
					// if this is the same policy overwrite
					logger.debug("Same policy, overwriting file {}.", filename);
					match = true;
				} else {
					filename = policyName + "_" + index;
					index++;
					logger.debug("Different policy, add index and check the new file name: {}.", filename);
				}
			} else {
				logger.debug("File not found in folder, create with {} name.", filename);
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
	private Map<String, VraNgApprovalPolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {

		// First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()
				&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.toFile().getAbsolutePath());
		}

		File[] approvalPolicyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgApprovalPolicy> currentPoliciesOnFileSystem = new HashMap<>();

		for (File policyFile : approvalPolicyFiles) {
			String fileNameWithExt = policyFile.getName();
			// exclude hidden files e.g. .DS_Store
			if (!fileNameWithExt.startsWith(".")) {
				String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
				currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgApprovalPolicy(policyFile));
			}
		}
		return currentPoliciesOnFileSystem;
	}

	/**
	 * Calculate approval policies sub-folder absolute filesystem path.
	 * 
	 * @return the approval policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_POLICIES,
				APPROVAL);
		if (!policyFolderPath.toFile().isDirectory()
				&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}

	/**
	 * Converts a json catalog item file to VraNgApprovalPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgApprovalPolicy
	 */
	private VraNgApprovalPolicy jsonFileToVraNgApprovalPolicy(final File jsonFile) {
		logger.debug("Converting approval policy file to VraNgApprovalPolicy. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgApprovalPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}
}
