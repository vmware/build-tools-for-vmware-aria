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
		for (File policyFile : approvalPolicyFiles) {
			this.handlePolicyImport(policyFile);
		}
	}

	/**
	 * Imports policy file to server , replacing the organization id.
	 *
	 * @param approvalPolicyFile   the policy to import.
	 */
	private void handlePolicyImport(final File approvalPolicyFile) {
		//convert file to policy object.
		VraNgApprovalPolicy policy = jsonFileToVraNgApprovalPolicy(approvalPolicyFile);
		//replace object organization id with target organization Id
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		logger.info("Attempting to import approval policy '{}'", policy.getName());

		//if the policy has a project property, replace it with current project id.
		//if the policy does not have a project property - replacing it will change the policy,
		// so do not replace a null or blank value.
		//also, if the policy organization matches current organization, this means the project also matches either the current project or another project in this organization, no need to overwrite, because it is either the current project, or it is another project in the organization, and the push will fail.
		if ( policy.getProjectId() != null && !(policy.getProjectId().isBlank()) && !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
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
				if (itemNames.contains(policy.getId())) {
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

		String filename = policy.getId();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}

		File policyFile = Paths.get(
			store.getPath(),
			com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
			APPROVAL,
			filename + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!policyFile.getParentFile().isDirectory()
			&& !policyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);

			logger.info("Created approval policy file {}",
				Files.write(Paths.get(policyFile.getPath()),
					gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
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
