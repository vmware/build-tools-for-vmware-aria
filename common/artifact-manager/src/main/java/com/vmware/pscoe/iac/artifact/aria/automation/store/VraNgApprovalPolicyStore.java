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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
	 * Retrieves all approval policies from the server.
	 *
	 * Used for deletion
	 *
	 * @return A List of approval policies
	 */
	protected List<VraNgApprovalPolicy> getAllServerContents() {
		return this.restClient.getApprovalPolicies();
	}

	///////////////////////////////////////////
	// Delete
	///////////////////////////////////////////

	/**
	 * Deletes an approval policy by id.
	 *
	 * @param resId - the approval policy id
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	///////////////////////////////////////////
	// Delete
	///////////////////////////////////////////

	///////////////////////////////////////////
	// Import
	///////////////////////////////////////////

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
		logger.info("Importing files from the '{}' directory", VraNgDirs.DIR_POLICIES);
		File policyFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_POLICIES, APPROVAL).toFile();

		if (!policyFolder.exists()) {
			logger.info("No approval policies available - skip import");
			return;
		}

		File[] approvalPolicyFiles = this.filterBasedOnConfiguration(policyFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));

		if (approvalPolicyFiles != null && approvalPolicyFiles.length == 0) {
			logger.info("No approval policies available - skip import");
			return;
		}

		Map<String, VraNgApprovalPolicy> policiesOnServer = this
				.fetchPolicies(this.getItemListFromDescriptor());

		logger.info("Found Approval Policies. Importing ...");
		for (File policyFile : approvalPolicyFiles) {
			this.handlePolicyImport(policyFile, policiesOnServer);
		}
	}

	/**
	 * Imports policy file to server, replacing the organization id.
	 *
	 * NOTE: The `projectId` is only set if it originally existed in the policy. The
	 * API will not return a `projectId` if the policy is organization scoped
	 *
	 * @param approvalPolicyFile the policy to import.
	 */
	private void handlePolicyImport(final File approvalPolicyFile, Map<String, VraNgApprovalPolicy> policiesOnServer) {
		VraNgApprovalPolicy policy = jsonFileToVraNgApprovalPolicy(approvalPolicyFile);

		logger.info("Attempting to import approval policy '{}', from file '{}'", policy.getName(),
				approvalPolicyFile.getName());

		if (policy.getProjectId() != null && !policy.getProjectId().isBlank()) {
			policy.setProjectId(this.restClient.getProjectId());
		}

		policy.setOrgId(VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId());

		if (policiesOnServer.containsKey(policy.getName())) {
			// policy.setId(policiesOnServer.get(policy.getName()).getId());
			// policy.setProjectId(policiesOnServer.get(policy.getName()).getProjectId());

			this.deleteResourceById(policiesOnServer.get(policy.getName()).getId());
		}

		this.logger.info("Attempting to create approval policy '{}'", policy.getName());
		this.restClient.createApprovalPolicy(policy);
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

	///////////////////////////////////////////
	// Import
	///////////////////////////////////////////

	////////////////////////////////////////////
	// STORE
	////////////////////////////////////////////

	/**
	 * Exporting every policy of this type found on server.
	 */
	@Override
	protected void exportStoreContent() {
		this.exportStoreContent(new ArrayList<String>());
	}

	/**
	 * Exports all the content for the given project based on the names in
	 * content.yaml.
	 *
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgApprovalPolicy> policies = this.fetchPolicies(itemNames);

		itemNames.forEach(name -> {
			if (!policies.containsKey(name)) {
				throw new RuntimeException(
						String.format("Approval Policy with name: '%s' could not be found on the server.", name));
			}
		});

		policies.forEach((name, policy) -> {
			storeApprovalPolicyOnFilesystem(
					getPolicyFile(policyFolderPath, policy),
					policy);
		});
	}

	/**
	 * Store approval policy in JSON file.
	 *
	 * @param policyFile where to store the policy.
	 * @param policy     the policy object to export.
	 */
	private void storeApprovalPolicyOnFilesystem(final File policyFile, final VraNgApprovalPolicy policy) {
		logger.debug("Storing  {}", policy.getName());

		try {
			Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);

			sanitizePolicy(policyJsonObject);

			logger.info("Created approval policy file {}",
					Files.write(Paths.get(policyFile.getPath()),
							gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			throw new RuntimeException(
					String.format(
							"Unable to store approval policy to file %s.", policyFile.getAbsolutePath()),
					e);
		}

	}

	/**
	 * @param policyFolderPath the correct subfolder path for the policy
	 *                         type.
	 * @param policy           the policy that is exported.
	 *
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, VraNgApprovalPolicy policy) {
		String filename = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}

		return Paths.get(
				String.valueOf(policyFolderPath),
				filename + CUSTOM_RESOURCE_SUFFIX).toFile();
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

	////////////////////////////////////////////
	// STORE
	////////////////////////////////////////////

	/**
	 * This will fetch all the policies that need to be exported.
	 *
	 * Will validate that the no duplicate approval policies exist on the
	 * environment.
	 *
	 * @param itemNames - the list of approval policies to fetch. If empty, will
	 *                  fetch all
	 */
	private Map<String, VraNgApprovalPolicy> fetchPolicies(final List<String> itemNames) {
		Map<String, VraNgApprovalPolicy> policies = new HashMap<>();
		boolean includeAll = itemNames.size() == 0;

		this.getAllServerContents().forEach(policy -> {
			if (policy.getTypeId().equals(VraNgPolicyTypes.APPROVAL_POLICY_TYPE)
					&& (includeAll || itemNames.contains(policy.getName()))) {
				if (policies.containsKey(policy.getName())) {
					throw new RuntimeException(
							String.format(
									"More than one approval policy with name '%s' already exists. While Aria supports policies with the same type and name, the build tools cannot. This is because we need to distinguish policies.",
									policy.getName()));
				}

				logger.debug("Found policy: {}", policy.getName());
				policies.put(policy.getName(), policy);
			}
		});

		return policies;
	}

	/**
	 * Sanitizes the give JSON object policy by removing properties that should not
	 * be stored.
	 *
	 * The `projectId` must not be removed, as it controls wether a policy is
	 * project scoped or not
	 *
	 * @param policy - the policy to sanitize
	 */
	private void sanitizePolicy(JsonObject policy) {
		policy.remove("orgId");
		policy.remove("createdBy");
		policy.remove("createdAt");
		policy.remove("lastUpdatedBy");
		policy.remove("lastUpdatedAt");
		policy.remove("id");
	}
}
