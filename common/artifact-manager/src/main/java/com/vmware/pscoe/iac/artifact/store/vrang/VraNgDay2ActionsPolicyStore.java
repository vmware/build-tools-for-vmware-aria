package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDay2ActionsPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;
import org.apache.commons.io.FilenameUtils;
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
import java.util.stream.Collectors;

public final class VraNgDay2ActionsPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for day 2 actions policy.
	 */
	private static final String DAY2_ACTIONS_POLICY = "day2-actions";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgDay2ActionsPolicyStore.class);

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
				DAY2_ACTIONS_POLICY)
			.toFile();
		if (!policyFolder.exists()) {
			logger.info("Day 2 Actions policy directory not found.");
			return;
		}

		List<String> policies = this.getItemListFromDescriptor();

		File[] day2ActionsolicyFiles = this.filterBasedOnConfiguration(policyFolder,
			new CustomFolderFileFilter(policies));

		if (day2ActionsolicyFiles != null && day2ActionsolicyFiles.length == 0) {
			logger.info("Could not find any Day 2 Actions Policies.");
			return;
		}

		logger.info("Found Day 2 Actions  Policies. Importing ...");

		for (File policyFile : day2ActionsolicyFiles) {
			//exclude hidden files e.g. .DS_Store
			if (!policyFile.getName().startsWith(".")) {
				this.handleDay2ActionsPolicyImport(policyFile);
			}
		}
	}

	/**
	 * .
	 * Handles logic to update or create a day 2 actions policy.
	 *
	 * @param day2ActionspolicyFile
	 */
	private void handleDay2ActionsPolicyImport(final File day2ActionspolicyFile) {
		VraNgDay2ActionsPolicy policy = jsonFileToVraNgDay2ActionsPolicy(day2ActionspolicyFile);
		//replace object organization id with target organization Id
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		logger.info("Attempting to import day 2 actions policy '{}', from file'{}'", policy.getName(), day2ActionspolicyFile.getName());
		//if the policy has a project property, replace it with current project id.
		//if the policy does not have a project property - replacing it will change the policy,
		// so do not replace a null or blank value.
		if ( policy.getProjectId() != null && !(policy.getProjectId().isBlank()) && !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
		this.restClient.createDay2ActionsPolicy(policy);
	}
	/**
	 * getItemListFromDescriptor.
	 * @return list of policy names to import or export.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		logger.info("{}->getItemListFromDescriptor", this.getClass());

		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			logger.info("Descriptor policy is null");
			return null;
		} else {
			logger.info("Found items {}", this.vraNgPackageDescriptor.getPolicy().getDay2Actions());
			return this.vraNgPackageDescriptor.getPolicy().getDay2Actions();
		}
	}
	/**
	 * Exporting every policy of this type found on server.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		List<VraNgDay2ActionsPolicy> rqPolicies = this.restClient.getDay2ActionsPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgDay2ActionsPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		rqPolicies.forEach(
			policy -> {
				storeDay2ActionsPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
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
		List<VraNgDay2ActionsPolicy> policies = this.restClient.getDay2ActionsPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgDay2ActionsPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);


		policies.forEach(
			policy -> {
				if (itemNames.contains(policy.getName())) {
					logger.debug("exporting '{}'", policy.getName());
					storeDay2ActionsPolicyOnFilesystem(policyFolderPath, policy, currentPoliciesOnFileSystem);
				}
			});
	}
	/**
	 * Store day 2 actions policy in JSON file.
	 *
	 * @param policyFolderPath,        day 2 actions folder path
	 * @param day2ActionsPolicy day2ActionsPolicy representation
	 * @param currentPoliciesOnFileSystem  a map of file names and policies, already present in the folder, used to avoid duplicate file names.
	 */
	private void storeDay2ActionsPolicyOnFilesystem(final Path policyFolderPath,
													final VraNgDay2ActionsPolicy day2ActionsPolicy,
													Map<String, VraNgDay2ActionsPolicy> currentPoliciesOnFileSystem ) {



		File policyFile = getPolicyFile(policyFolderPath, day2ActionsPolicy, currentPoliciesOnFileSystem);
		logger.info("Storing day2ActionsPolicy '{}', to file '{}", day2ActionsPolicy.getName(), policyFile.getPath());


		try {
			//is serializeNulls() needed?
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject d2aPolicyJsonObject = gson.fromJson(new Gson().toJson(day2ActionsPolicy), JsonObject.class);
			
			logger.info("Created day 2 actions policy file {}",
				Files.write(Paths.get(policyFile.getPath()),
					gson.toJson(d2aPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
			//after write, put currently policy on the map for the next iteration

			String fileName = policyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, day2ActionsPolicy);

		} catch (IOException e) {
			logger.error("Unable to create day 2 actions policy  {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
				String.format(
					"Unable to store day 2 actions policy to file %s.", policyFile.getAbsolutePath()),
				e);
		}

	}
	/**
	 s	 * @param policyFolderPath the correct subfolder path for the policy type.
	 * @param policy the policy that is exported.
	 * @param currentPoliciesOnFileSystem  all the other policies currently in the folder.
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, VraNgDay2ActionsPolicy policy, Map<String, VraNgDay2ActionsPolicy> currentPoliciesOnFileSystem) {
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
	private Map<String, VraNgDay2ActionsPolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {

		//First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.toFile().getAbsolutePath());
		}

		File[] policyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgDay2ActionsPolicy> currentPoliciesOnFileSystem = new HashMap<>();

		for (File policyFile : policyFiles) {
			String fileNameWithExt = policyFile.getName();
			//exclude hidden files e.g. .DS_Store
			if (!fileNameWithExt.startsWith(".")) {
				String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
				currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgDay2ActionsPolicy(policyFile));
			}
		}
		return currentPoliciesOnFileSystem;
	}

	/**
	 * Calculate day 2 actions policies sub-folder absolute filesystem path.
	 * @return the day 2 actions policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
			store.getPath(),
			VraNgDirs.DIR_POLICIES,
			DAY2_ACTIONS_POLICY);
		if (!policyFolderPath.toFile().isDirectory()
			&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}


	/**
	 * Converts a json catalog item file to VraNgDay2ActionsPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgDay2ActionsPolicy
	 */
	private VraNgDay2ActionsPolicy jsonFileToVraNgDay2ActionsPolicy(final File jsonFile) {
		logger.debug("Converting day 2 actions policy file to VraNgDay2ActionsPolicy. Name: '{}'",
			jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgDay2ActionsPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}
}
