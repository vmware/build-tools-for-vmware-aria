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
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDeploymentLimitPolicy;
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

public final class VraNgDeploymentLimitPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for deployment limit policy.
	 */
	private static final String DEPLOYMENT_LIMIT = "deployment-limit";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgDeploymentLimitPolicyStore.class);

	/**
	 * Get all the deployment limit policies from the server.
	 *
	 * @return list of all deployment limit policies.
	 */
	protected List<VraNgDeploymentLimitPolicy> getAllServerContents() {
		return this.restClient.getDeploymentLimitPolicies();
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
			return this.vraNgPackageDescriptor.getPolicy().getDeploymentLimit();
		}
	}

	////////////////////////////////////////////////////
	// Delete
	////////////////////////////////////////////////////

	/**
	 * Delete a deployment limit policy by its id.
	 *
	 * @param resId the id of the policy to delete.
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	////////////////////////////////////////////////////
	// Delete
	////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// Import
	////////////////////////////////////////////////////

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
		File policyFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_POLICIES, DEPLOYMENT_LIMIT).toFile();

		if (!policyFolder.exists()) {
			logger.info("No deployment limit policies available - skip import");
			return;
		}

		File[] policyFiles = this.filterBasedOnConfiguration(policyFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));

		if (policyFiles != null && policyFiles.length == 0) {
			logger.info("No deployment limit policies available - skip import");
			return;
		}

		Map<String, VraNgDeploymentLimitPolicy> policiesOnServer = this
				.fetchPolicies(this.getItemListFromDescriptor());

		logger.info("Found Deployment Limit Policies. Importing ...");
		for (File policyFile : policyFiles) {
			this.handlePolicyImport(policyFile, policiesOnServer);
		}
	}

	/**
	 * Imports policy file to server, replacing the organization id.
	 *
	 * NOTE: The `projectId` is only set if it originally existed in the policy. The
	 * API will not return a `projectId` if the policy is organization scoped
	 *
	 * @param policyFile       the policy to import.
	 * @param policiesOnServer all the policies currently on the server
	 */
	private void handlePolicyImport(final File policyFile, Map<String, VraNgDeploymentLimitPolicy> policiesOnServer) {
		VraNgDeploymentLimitPolicy policy = jsonFileToVraNgDeploymentLimitPolicy(policyFile);

		logger.info("Attempting to import deployment limit policy '{}', from file '{}'", policy.getName(),
				policyFile.getName());

		if (policy.getProjectId() != null && !policy.getProjectId().isBlank()) {
			policy.setProjectId(this.restClient.getProjectId());
		}

		policy.setOrgId(VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId());

		if (policiesOnServer.containsKey(policy.getName())) {
			this.logger.warn("Deployment Limit policy '{}' already exists on the server. Deleting it first.",
					policy.getName());
			this.deleteResourceById(policiesOnServer.get(policy.getName()).getId());
		}

		this.logger.info("Attempting to create deployment limit policy '{}'", policy.getName());
		this.restClient.createDeploymentLimitPolicy(policy);
	}

	////////////////////////////////////////////////////
	// Import
	////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// Export
	////////////////////////////////////////////////////

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
		Map<String, VraNgDeploymentLimitPolicy> policies = this.fetchPolicies(itemNames);

		itemNames.forEach(name -> {
			if (!policies.containsKey(name)) {
				throw new RuntimeException(
						String.format("Deployment Limit with name: '%s' could not be found on the server.", name));
			}
		});

		policies.forEach((name, policy) -> {
			storePolicyOnFS(
					getPolicyFile(policyFolderPath, policy),
					policy);
		});
	}

	/*
	 * Store policy in JSON file.
	 *
	 * @param policyFile policy file
	 * 
	 * @param policy policy representation
	 */
	private void storePolicyOnFS(final File policyFile, final VraNgDeploymentLimitPolicy policy) {
		logger.debug("Storing deployment limit policy {}", policy.getName());

		try {
			Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(policy), JsonObject.class);

			sanitizePolicy(policyJsonObject);

			logger.info("Created deployment limit policy file {}",
					Files.write(Paths.get(policyFile.getPath()),
							gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			throw new RuntimeException(
					String.format(
							"Unable to store deployment limit policy to file %s.", policyFile.getAbsolutePath()),
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
	private File getPolicyFile(Path policyFolderPath, VraNgDeploymentLimitPolicy policy) {
		String filename = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}

		return Paths.get(
				String.valueOf(policyFolderPath),
				filename + CUSTOM_RESOURCE_SUFFIX).toFile();
	}

	/**
	 * Calculate deployment limit policies sub-folder absolute filesystem path.
	 * 
	 * @return the deployment limit policies sub-folder absolute filesystem path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_POLICIES,
				DEPLOYMENT_LIMIT);
		if (!policyFolderPath.toFile().isDirectory()
				&& !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}
		return policyFolderPath;
	}

	////////////////////////////////////////////////////
	// Export
	////////////////////////////////////////////////////

	/**
	 * Converts a json catalog item file to VraNgDeploymentLimitPolicy.
	 *
	 * @param jsonFile file to read an object from.
	 *
	 * @return VraNgDeploymentLimitPolicy .
	 */
	private VraNgDeploymentLimitPolicy jsonFileToVraNgDeploymentLimitPolicy(final File jsonFile) {
		logger.debug("Converting deployment limit policy file to VraNgDeploymentLimitPolicy. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgDeploymentLimitPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * This will fetch all the policies that need to be exported.
	 *
	 * Will validate that the no duplicate policies exist on the
	 * environment.
	 *
	 * @param itemNames - the list of policies to fetch. If empty, will
	 *                  fetch all
	 */
	private Map<String, VraNgDeploymentLimitPolicy> fetchPolicies(final List<String> itemNames) {
		Map<String, VraNgDeploymentLimitPolicy> policies = new HashMap<>();
		boolean includeAll = itemNames.size() == 0;

		this.getAllServerContents().forEach(policy -> {
			if (policy.getTypeId().equals(VraNgPolicyTypes.DEPLOYMENT_LIMIT_POLICY_TYPE)
					&& (includeAll || itemNames.contains(policy.getName()))) {
				if (policies.containsKey(policy.getName())) {
					throw new RuntimeException(
							String.format(
									"More than one deployment limit policy with name '%s' already exists. While Aria supports policies with the same type and name, the build tools cannot. This is because we need to distinguish policies.",
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
