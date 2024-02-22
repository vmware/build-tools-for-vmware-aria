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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
		Map<String, VraNgDay2ActionsPolicy> d2aPolicyOnServerByName = this.restClient.getDay2ActionsPolicies()
			.stream()
			.map(d2a -> this.restClient.getDay2ActionsPolicy(d2a.getId()))
			.collect(Collectors.toMap(VraNgDay2ActionsPolicy::getName, item -> item));

		for (File policyFile : day2ActionsolicyFiles) {
			this.handleDay2ActionsPolicyImport(policyFile, d2aPolicyOnServerByName);
		}
	}

	/**
	 * .
	 * Handles logic to update or create a day 2 actions policy.
	 *
	 * @param day2ActionspolicyFile
	 *
	 * @param policyOnServerByName
	 */
	private void handleDay2ActionsPolicyImport(final File day2ActionspolicyFile,
												final Map<String, VraNgDay2ActionsPolicy> policyOnServerByName) {
		String policyNameWithExt = day2ActionspolicyFile.getName();
		String policyName = FilenameUtils.removeExtension(policyNameWithExt);
		logger.info("Attempting to import day 2 actions policy '{}'", policyName);
		VraNgDay2ActionsPolicy d2aPolicy = jsonFileToVraNgDay2ActionsPolicy(day2ActionspolicyFile);

		// Check if the day 2 actions policy exists
		//if it exists, set the id to tell the API to update existing policy
		//if it does not exists, remove the iD to tell the API to create a new policy
		VraNgDay2ActionsPolicy existingRecord = null;
		if (policyOnServerByName.containsKey(policyName)) {
			existingRecord = policyOnServerByName.get(policyName);
		}
		if (existingRecord != null && !existingRecord.getId().isBlank()) {
			d2aPolicy.setId(existingRecord.getId());
		} else {
			d2aPolicy.setId(null);
		}

		this.restClient.createDay2ActionsPolicy(d2aPolicy);
	}
	/**
	 * getItemListFromDescriptor
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

		rqPolicies.forEach(
			policy -> {
				logger.info("exporting '{}'", policy.getName());
				storeDay2ActionsPolicyOnFilesystem(vraNgPackage, policy);
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

		policies.forEach(
			policy -> {
				if (itemNames.contains(policy.getName())) {
					logger.info("exporting '{}'", policy.getName());
					storeDay2ActionsPolicyOnFilesystem(vraNgPackage, policy);
				}
			});
	}
	/**
	 * Store da 2 actions policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param day2ActionsPolicy day2ActionsPolicy representation
	 */
	private void storeDay2ActionsPolicyOnFilesystem(final Package serverPackage,
													  final VraNgDay2ActionsPolicy day2ActionsPolicy) {
		logger.debug("Storing day2ActionsPolicy {}", day2ActionsPolicy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		File policyFile = Paths.get(
			store.getPath(),
			com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
			DAY2_ACTIONS_POLICY,
			day2ActionsPolicy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!policyFile.getParentFile().isDirectory()
			&& !policyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFile.getParentFile().getAbsolutePath());
		}

		try {
			//is serializeNulls() needed?
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject d2aPolicyJsonObject = gson.fromJson(new Gson().toJson(day2ActionsPolicy), JsonObject.class);
			
			logger.info("Created day 2 actions policy file {}",
				Files.write(Paths.get(policyFile.getPath()),
					gson.toJson(d2aPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create day 2 actions policy  {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
				String.format(
					"Unable to store day 2 actions policy to file %s.", policyFile.getAbsolutePath()),
				e);
		}

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
