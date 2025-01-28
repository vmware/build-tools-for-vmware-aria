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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgItem;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;

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
	 * Content sharing type: content source.
	 */
	private static final String CONTENT_SOURCE = "CATALOG_SOURCE_IDENTIFIER";
	/**
	 * Content sharing type: catalog item.
	 */
	private static final String CATALOG_ITEM = "CATALOG_ITEM_IDENTIFIER";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgContentSharingPolicyStore.class);

	/**
	 * Get all the content sharing policies from the server.
	 *
	 * @return list of content sharing policies.
	 */
	protected List<VraNgContentSharingPolicy> getAllServerContents() {
		return this.restClient.getContentSharingPolicies();
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of content sharing policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/////////////////////////////////////
	// Delete
	////////////////////////////////////

	/**
	 * Delete the content sharing policy by id.
	 *
	 * @param resId the id of the policy to delete.
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	/////////////////////////////////////
	// Delete
	////////////////////////////////////

	/////////////////////////////////////
	// Import
	////////////////////////////////////

	/**
	 * Imports content.
	 * 
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", VraNgDirs.DIR_POLICIES);
		File contentSharingPolicyFolder = Paths
				.get(sourceDirectory.getPath(), VraNgDirs.DIR_POLICIES, CONTENT_SHARING_POLICY).toFile();

		if (!contentSharingPolicyFolder.exists()) {
			logger.info("No approval policies available - skip import");
			return;
		}

		File[] contentSharingPolicyFiles = this.filterBasedOnConfiguration(contentSharingPolicyFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));

		if (contentSharingPolicyFiles != null && contentSharingPolicyFiles.length == 0) {
			logger.info("No approval policies available - skip import");
			return;
		}

		Map<String, VraNgContentSharingPolicy> policiesOnServer = this
				.fetchPolicies(this.getItemListFromDescriptor());

		logger.info("Found Content Sharing Policies. Importing...");
		for (File policyFile : contentSharingPolicyFiles) {
			this.handlePolicyImport(policyFile, policiesOnServer);
		}
	}

	/**
	 * Handles logic to update or create a content sharing policy.
	 *
	 * NOTE: The `projectId` is only set if it originally existed in the policy. The
	 * API will not return a `projectId` if the policy is organization scoped
	 *
	 * @param contentSharingPolicyFile file where the policy is stored.
	 */
	private void handlePolicyImport(final File contentSharingPolicyFile,
			Map<String, VraNgContentSharingPolicy> policiesOnServer) {
		VraNgContentSharingPolicy policy = jsonFileToVraNgContentSharingPolicy(contentSharingPolicyFile);

		logger.info("Attempting to import content sharing policy '{}', from file '{}'", policy.getName(),
				contentSharingPolicyFile.getName());
		this.resolveEntitledUsersOrgAndScope(policy, true);

		if (policy.getProjectId() != null && !policy.getProjectId().isBlank()) {
			policy.setProjectId(this.restClient.getProjectId());
		}

		policy.setOrgId(VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId());

		if (policiesOnServer.containsKey(policy.getName())) {
			this.logger.warn("Content Sharing policy '{}' already exists on the server. Deleting it first.",
					policy.getName());
			this.deleteResourceById(policiesOnServer.get(policy.getName()).getId());
		}

		this.logger.info("Attempting to create approval policy '{}'", policy.getName());
		this.restClient.createContentSharingPolicy(policy);
	}

	/////////////////////////////////////
	// Import
	////////////////////////////////////

	/////////////////////////////////////
	// Export
	////////////////////////////////////

	/**
	 * Exports all the content for the given project.
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
		Map<String, VraNgContentSharingPolicy> policies = this.fetchPolicies(itemNames);

		itemNames.forEach(name -> {
			if (!policies.containsKey(name)) {
				throw new RuntimeException(
						String.format("Approval Policy with name: '%s' could not be found on the server.", name));
			}
		});

		policies.forEach((name, policy) -> {
			storePolicyOnFS(getPolicyFile(policyFolderPath, policy), policy);
		});
	}

	/**
	 * Store content sharing policy in JSON file.
	 * 
	 * @param policyFolderPath Path to the folder where to store the
	 *                         file.
	 * @param policy           the policy to store.
	 */
	private void storePolicyOnFS(final File policyFile,
			final VraNgContentSharingPolicy policy) {
		logger.info("Storing content sharing policy '{}'", policy.getName());

		// getting full policy information from server
		this.resolveEntitledUsersOrgAndScope(policy, false);

		try {
			// serialize the object
			Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();
			String policyJson = gson.toJson(policy);

			// write content sharing file
			logger.info("Created content sharing file {}",
					Files.write(Paths.get(policyFile.getPath()), policyJson.getBytes(),
							StandardOpenOption.CREATE));
		} catch (IOException e) {
			throw new RuntimeException(
					String.format(
							"Unable to store content sharing policy to file %s.", policyFile.getAbsolutePath()),
					e);
		}
	}

	/**
	 * Extract content-sharing policies sub-folder absolute file system path.
	 * 
	 * @return the content-sharing policies sub-folder absolute file system path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(store.getPath(), VraNgDirs.DIR_POLICIES, CONTENT_SHARING_POLICY);
		if (!policyFolderPath.toFile().isDirectory() && !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}

		return policyFolderPath;
	}

	/**
	 * @param policyFolderPath the correct sub folder path for the
	 *                         policy. type.
	 * @param policy           the policy that is exported.
	 * @return the file where to store the policy.
	 */
	private File getPolicyFile(Path policyFolderPath, VraNgContentSharingPolicy policy) {
		String filename = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}

		return Paths.get(
				String.valueOf(policyFolderPath),
				filename + CUSTOM_RESOURCE_SUFFIX).toFile();
	}

	/////////////////////////////////////
	// Export
	////////////////////////////////////

	/**
	 * Resolve the entitled users, organization and scope in the DTO object that
	 * will be serialized / deserialized (depending on the operation type (export or
	 * import)).
	 * 
	 * @param csPolicy         policy that need to be enriched with resolved data.
	 * @param isByNameResolved a flag whether to resolve the data by name. . In case
	 *                         import of policies the flag should be true otherwise
	 *                         it should be false.
	 *
	 */
	private void resolveEntitledUsersOrgAndScope(VraNgContentSharingPolicy csPolicy, boolean isByNameResolved) {
		if (csPolicy.getDefinition() == null || csPolicy.getDefinition().getEntitledUsers() == null) {
			return;
		}

		List<VraNgContentSourceBase> contentSources = this.restClient
				.getContentSourcesForProject(csPolicy.getProjectId());

		List<VraNgCatalogItem> catalogItems = this.restClient.getCatalogItemsForProject(csPolicy.getProjectId());
		csPolicy.getDefinition().getEntitledUsers().forEach(user -> {
			// resolve the name of the element based on its type
			if (user.getItems() != null) {
				user.getItems().forEach(item -> {
					this.resolveSingleItem(item, isByNameResolved, contentSources, catalogItems);
				});
			}
		});
	}

	/**
	 * Resolve the the single vra item name / id.
	 * 
	 * @param item             item where data should be resolved.
	 * @param isByNameResolved a flag whether to resolve the data by name. . In case
	 *                         import of policies the flag should be false otherwise
	 *                         it should be true.
	 * 
	 * @param contentSources   list of content sources per project
	 * @param catalogItems     list of catalog items per project
	 */
	private void resolveSingleItem(VraNgItem item, boolean isByNameResolved,
			List<VraNgContentSourceBase> contentSources, List<VraNgCatalogItem> catalogItems) {
		switch (item.getType()) {
			case CATALOG_ITEM: {
				VraNgCatalogItem foundCatalogItem;
				if (isByNameResolved) {
					foundCatalogItem = catalogItems.stream()
							.filter(catalogItem -> catalogItem.getName().equalsIgnoreCase(item.getName())).findFirst()
							.orElse(null);
					if (foundCatalogItem != null) {
						item.setId(foundCatalogItem.getId());
						item.setName(null);
					} else {
						throw new RuntimeException(
								String.format(
										"Catalog Item with name: '%s' could not be found. Cannot import/export Policy.",
										item.getName()));
					}
				} else {
					foundCatalogItem = catalogItems.stream()
							.filter(catalogItem -> catalogItem.getId().equalsIgnoreCase(item.getId())).findFirst()
							.orElse(null);
					if (foundCatalogItem != null) {
						item.setName(foundCatalogItem.getName());
						item.setId(null);
					} else {
						throw new RuntimeException(
								String.format(
										"Catalog Item with name: '%s' could not be found. Cannot import/export Policy.",
										item.getName()));
					}
				}
				break;
			}
			case CONTENT_SOURCE: {
				VraNgContentSourceBase foundContentSource;
				if (isByNameResolved) {
					foundContentSource = contentSources.stream()
							.filter(contentSource -> contentSource.getName().equalsIgnoreCase(item.getName()))
							.findFirst()
							.orElse(null);
					if (foundContentSource != null) {
						item.setId(foundContentSource.getId());
						item.setName(null);
					} else {
						throw new RuntimeException(
								String.format(
										"Content source with name: '%s' could not be found. Cannot import/export Policy.",
										item.getName()));
					}
				} else {
					foundContentSource = contentSources.stream()
							.filter(contentSource -> contentSource.getId().equalsIgnoreCase(item.getId())).findFirst()
							.orElse(null);
					if (foundContentSource != null) {
						item.setName(foundContentSource.getName());
						item.setId(null);
					} else {
						throw new RuntimeException(
								String.format(
										"Content source with name: '%s' could not be found. Cannot import/export Policy.",
										item.getName()));
					}
				}
				break;
			}
			default: {
				logger.warn("Type {}, for definition: {} is unsupported", item.getType(), item.getId());
			}
		}
	}

	/**
	 * Converts a JSON catalog item file to VraNgContentSharingPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgContentSharingPolicy jsonFileToVraNgContentSharingPolicy(final File jsonFile) {
		logger.debug("Converting content sharing policy file to VraNgContentSharingPolicies. Name: '{}'",
				jsonFile.getName());

		try {
			JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()));
			return new Gson().fromJson(reader, VraNgContentSharingPolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		} catch (JsonIOException | JsonSyntaxException e) {
			throw new RuntimeException(String.format("Error parsing file: %s", jsonFile.getPath()), e);
		} catch (Exception e) {
			throw new RuntimeException(String.format("General error processing file file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * This will fetch all the policies that need to be exported.
	 *
	 * Will validate that the no duplicate policies exist on the environment.
	 *
	 * @param itemNames - the list of policy names to fetch. If empty, will fetch
	 *                  all
	 */
	private Map<String, VraNgContentSharingPolicy> fetchPolicies(final List<String> itemNames) {
		Map<String, VraNgContentSharingPolicy> policies = new HashMap<>();
		boolean includeAll = itemNames.size() == 0;

		this.getAllServerContents().forEach(policy -> {
			if (policy.getTypeId().equals(VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE)
					&& (includeAll || itemNames.contains(policy.getName()))) {
				if (policies.containsKey(policy.getName())) {
					throw new RuntimeException(
							String.format(
									"More than one content sharing policy with name '%s' already exists. While Aria supports policies with the same type and name, the build tools cannot. This is because we need to distinguish policies.",
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
