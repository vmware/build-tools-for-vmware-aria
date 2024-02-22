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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
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

public final class VraNgApprovalPolicyStore  extends AbstractVraNgStore {
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
		Map<String, VraNgApprovalPolicy> dlPolicyOnServerByName = this.restClient.getApprovalPolicies()
			.stream()
			.map(dl -> this.restClient.getApprovalPolicy(dl.getId()))
			.collect(Collectors.toMap(VraNgApprovalPolicy::getName, item -> item));

		for (File policyFile : approvalPolicyFiles) {
			this.handleApprovalPolicyImport(policyFile, dlPolicyOnServerByName);
		}
	}

	/**
	 * .
	 * Handles logic to update or create an approval policy.
	 *
	 * @param approvalPolicyFile
	 *
	 * @param policyOnServerByName
	 */
	private void handleApprovalPolicyImport(final File approvalPolicyFile,
												   final Map<String, VraNgApprovalPolicy> policyOnServerByName)  {
		String policyNameWithExt = approvalPolicyFile.getName();
		String policyName = FilenameUtils.removeExtension(policyNameWithExt);
		logger.info("Attempting to import approval policy '{}'", policyName);
		VraNgApprovalPolicy policy = jsonFileToVraNgApprovalPolicy(approvalPolicyFile);

		// Check if the policy exists
		//if it exists, set the id to tell the API to update existing policy
		//if it does not exists, remove the iD to tell the API to create a new policy
		VraNgApprovalPolicy existingRecord = null;
		if (policyOnServerByName.containsKey(policyName)) {
			existingRecord = policyOnServerByName.get(policyName);
		}
		if (existingRecord != null && !existingRecord.getId().isBlank()) {
			policy.setId(existingRecord.getId());
		} else {
			policy.setId(null);
		}

		this.restClient.createApprovalPolicy(policy);
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
			logger.info("Found items {}", this.vraNgPackageDescriptor.getPolicy().getApproval());
			return this.vraNgPackageDescriptor.getPolicy().getApproval();
		}
	}

	/**
	 * Exporting every policy of this type found on server.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		List<VraNgApprovalPolicy> rqPolicies = this.restClient.getApprovalPolicies();

		rqPolicies.forEach(
			policy -> {
				logger.info("exporting '{}'", policy.getName());
				storeApprovalPolicyOnFilesystem(vraNgPackage, policy);
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
		List<VraNgApprovalPolicy> policies = this.restClient.getApprovalPolicies();

		policies.forEach(
			policy -> {
				if (itemNames.contains(policy.getName())) {
					logger.info("exporting '{}'", policy.getName());
					storeApprovalPolicyOnFilesystem(vraNgPackage, policy);
				}
			});
	}
	/**
	 * Store approval policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param   policy
	 */
	private void storeApprovalPolicyOnFilesystem(final Package serverPackage,
													final VraNgApprovalPolicy policy) {
		logger.debug("Storing  {}", policy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		File policyFile = Paths.get(
			store.getPath(),
			com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
			APPROVAL,
			policy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!policyFile.getParentFile().isDirectory()
			&& !policyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFile.getParentFile().getAbsolutePath());
		}

		try {
			//is serializeNulls() needed?
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject d2aPolicyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);

			logger.info("Created approval policy file {}",
				Files.write(Paths.get(policyFile.getPath()),
					gson.toJson(d2aPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create approval policy  {}", policyFile.getAbsolutePath());
			throw new RuntimeException(
				String.format(
					"Unable to store approval policy to file %s.", policyFile.getAbsolutePath()),
				e);
		}

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
