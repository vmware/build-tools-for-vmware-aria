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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgItem;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;

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
	 * Imports content.
	 * 
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES);
		// verify directory exists
		File contentSharingPolicyFolder = Paths
				.get(sourceDirectory.getPath(), com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES, CONTENT_SHARING_POLICY).toFile();
		if (!contentSharingPolicyFolder.exists()) {
			logger.info("Content sharing policy directory not found.");
			return;
		}

		List<String> csPolicies = this.getItemListFromDescriptor();
		File[] contentSharingPolicyFiles = this.filterBasedOnConfiguration(contentSharingPolicyFolder, new CustomFolderFileFilter(csPolicies));
		if (contentSharingPolicyFiles != null && contentSharingPolicyFiles.length == 0) {
			logger.info("Could not find any Content Sharing Policies.");
			return;
		}

		logger.info("Found Content Sharing Policies. Importing...");
		for (File policyFile : contentSharingPolicyFiles) {
			// exclude hidden files e.g. .DS_Store
			// exclude files that do not end with a '.json' extension as defined in
			// CUSTOM_RESOURCE_SUFFIX
			String filename = policyFile.getName();
			if (!filename.startsWith(".") && filename.endsWith(CUSTOM_RESOURCE_SUFFIX)) {
				this.handleContentSharingPolicyImport(policyFile);
			} else {
				logger.warn("Skipped unexpected file '{}'", filename);
			}
		}
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of content sharing policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			logger.debug("Descriptor policy is null");
			return null;
		} else {
			logger.debug("Found items {}", this.vraNgPackageDescriptor.getPolicy().getContentSharing());
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		List<VraNgContentSharingPolicy> csPolicies = this.restClient.getContentSharingPolicies();
		Path policyFolderPath = getPolicyFolderPath();

		this.handleContentSharingPolicyExport(csPolicies, policyFolderPath);
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
		List<VraNgContentSharingPolicy> csPolicies = this.restClient.getContentSharingPolicies();
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);

		csPolicies.forEach(policy -> {
			if (itemNames.contains(policy.getName())) {
				// getting policy from server
				VraNgContentSharingPolicy csPolicy = this.restClient.getContentSharingPolicy(policy.getId());
				storeContentSharingPolicyOnFilesystem(policyFolderPath, csPolicy, currentPoliciesOnFileSystem);
			}
		});
	}

	/**
	 * . Handles logic to update or create a content sharing policy.
	 *
	 * @param contentSharingPolicyFile file where the policy is stored.
	 */
	private void handleContentSharingPolicyImport(final File contentSharingPolicyFile) {
		VraNgContentSharingPolicy csPolicy = jsonFileToVraNgContentSharingPolicy(contentSharingPolicyFile);
		logger.info("Attempting to import content sharing policy '{}', from file '{}'", csPolicy.getName(), contentSharingPolicyFile.getName());
		this.resolveEntitledUsersOrgAndScope(csPolicy, true);
		this.enrichContentSharingPolicy(csPolicy);
		this.restClient.createContentSharingPolicy(csPolicy);
	}

	/**
	 * Handles the export of the content sharing policies.
	 *
	 * @param List<VraNgContentSharingPolicy> csPolicies policies that need to be
	 *                                        exported.
	 * @param Path                            policyFolderPath target directory
	 *                                        where the policies should be exported
	 *                                        into.
	 *
	 */
	private void handleContentSharingPolicyExport(List<VraNgContentSharingPolicy> csPolicies, Path policyFolderPath) {
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = getCurrentPoliciesOnFileSystem(policyFolderPath);
		csPolicies.forEach(policy -> {
			// getting full policy information from server
			VraNgContentSharingPolicy csPolicy = this.restClient.getContentSharingPolicy(policy.getId());
			this.resolveEntitledUsersOrgAndScope(csPolicy, false);
			storeContentSharingPolicyOnFilesystem(policyFolderPath, csPolicy, currentPoliciesOnFileSystem);
		});
	}

	/**
	 * Resolve the entitled users, organization and scope in the DTO object that
	 * will be serialized / deserialized (depending on the operation type (export or
	 * import)).
	 * 
	 * @param VraNgContentSharingPolicy policy that need to be enriched with
	 *                                  resolved data.
	 * @param isByNameResolved          a flag whether to resolve the data by name.
	 *                                  . In case import of policies the flag should
	 *                                  be true otherwise it should be false.
	 *
	 */
	private void resolveEntitledUsersOrgAndScope(VraNgContentSharingPolicy csPolicy, boolean isByNameResolved) {
		if (csPolicy.getDefinition().getEntitledUsers() == null) {
			return;
		}
		// enrich entitled users with the item names.
		List<VraNgContentSourceBase> contentSources = this.restClient.getContentSourcesForProject(csPolicy.getProjectId());
		List<VraNgCatalogItem> catalogItems = this.restClient.getCatalogItemsForProject(csPolicy.getProjectId());
		if (csPolicy.getDefinition() != null && csPolicy.getDefinition().getEntitledUsers() != null) {
			csPolicy.getDefinition().getEntitledUsers().forEach(user -> {
				// resolve the name of the element based on its type
				if (user.items != null) {
					user.items.forEach(item -> {
						this.resolveSingleItem(item, isByNameResolved, contentSources, catalogItems);
					});
				}
			});
		}

		if (!isByNameResolved) {
			if (StringUtils.hasLength(csPolicy.getProjectId())) {
				csPolicy.setScope(this.restClient.getProjectNameById(csPolicy.getProjectId()));
				csPolicy.setProjectId(null);
			}
			if (StringUtils.hasLength(csPolicy.getOrgId())) {
				csPolicy.setOrganization(this.restClient.getOrganizationById(csPolicy.getOrgId()).getName());
				csPolicy.setOrgId(null);
			}
		}
	}

	/**
	 * Resolve the the single vra item name / id.
	 * 
	 * @param VraNgItem        item where data should be resolved.
	 * @param isByNameResolved a flag whether to resolve the data by name. . In case
	 *                         import of policies the flag should be false otherwise
	 *                         it should be true.
	 * 
	 * @param contentSources   list of content sources per project
	 * @param catalogItems     list of catalog items per project
	 */
	private void resolveSingleItem(VraNgItem item, boolean isByNameResolved, List<VraNgContentSourceBase> contentSources, List<VraNgCatalogItem> catalogItems) {
		switch (item.type) {
			case CATALOG_ITEM: {
				VraNgCatalogItem foundCatalogItem;
				if (isByNameResolved) {
					foundCatalogItem = catalogItems.stream().filter(catalogItem -> catalogItem.getName().equalsIgnoreCase(item.name)).findFirst().orElse(null);
					if (foundCatalogItem != null) {
						item.id = foundCatalogItem.getId();
						item.name = null;
					}
				} else {
					foundCatalogItem = catalogItems.stream().filter(catalogItem -> catalogItem.getId().equalsIgnoreCase(item.id)).findFirst().orElse(null);
					if (foundCatalogItem != null) {
						item.name = foundCatalogItem.getName();
						item.id = null;
					}
				}
				break;
			}
			case CONTENT_SOURCE: {
				VraNgContentSourceBase foundContentSource;
				if (isByNameResolved) {
					foundContentSource = contentSources.stream().filter(contentSource -> contentSource.getName().equalsIgnoreCase(item.name)).findFirst().orElse(null);
					if (foundContentSource != null) {
						item.id = foundContentSource.getId();
						item.name = null;
					}
				} else {
					foundContentSource = contentSources.stream().filter(contentSource -> contentSource.getId().equalsIgnoreCase(item.id)).findFirst().orElse(null);
					if (foundContentSource != null) {
						item.name = foundContentSource.getName();
						item.id = null;
					}
				}
				break;
			}
			default: {
				this.logger.warn("Type {}, for definition: {} is unsupported", item.type, item.id);
			}
		}
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgContentSharingPolicy jsonFileToVraNgContentSharingPolicy(final File jsonFile) {
		logger.debug("Converting content sharing policy file to VraNgContentSharingPolicies. Name: '{}'", jsonFile.getName());

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
	 * Used while import to override the orgId and ProjectId for the receiving vRA.
	 * instance.
	 * 
	 * @param csPolicy
	 */
	private void enrichContentSharingPolicy(final VraNgContentSharingPolicy csPolicy) {
		if (StringUtils.hasLength(csPolicy.getOrganization())) {
			csPolicy.setOrgId(this.restClient.getOrganizationByName(csPolicy.getOrganization()).getId());
		} else {
			csPolicy.setOrgId(VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId());
		}
		if (StringUtils.hasLength(csPolicy.getScope())) {
			csPolicy.setProjectId(this.restClient.getProjectIdByName(csPolicy.getScope()));
		} else {
			csPolicy.setProjectId(this.restClient.getProjectId());
		}
		csPolicy.setScope(null);
		csPolicy.setOrganization(null);
	}

	/**
	 * Store content sharing policy in JSON file.
	 * 
	 * @param policyFolderPath            Path to the folder where to store the
	 *                                    file.
	 * @param contentSharingPolicy        the policy to store.
	 * @param currentPoliciesOnFileSystem a map of file names and policies, already
	 *                                    present in the folder, used to avoid
	 *                                    duplicate file names.
	 */
	private void storeContentSharingPolicyOnFilesystem(final Path policyFolderPath, final VraNgContentSharingPolicy contentSharingPolicy,
			Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem) {

		File contentSharingPolicyFile = getPolicyFile(policyFolderPath, contentSharingPolicy, currentPoliciesOnFileSystem);
		logger.info("Storing contentSharingPolicy '{}', to file '{}", contentSharingPolicy.getName(), contentSharingPolicyFile.getPath());
		try {
			if (contentSharingPolicy.getDefinition().getEntitledUsers() != null) {
				contentSharingPolicy.getDefinition().getEntitledUsers().forEach(user -> {
					if (user.items != null) {
						user.items.forEach(item -> item.id = null);
					}
				});
			}
			// serialize the object
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
			String policyJson = gson.toJson(contentSharingPolicy);
			// write content sharing file
			logger.info("Created content sharing file {}",
					Files.write(Paths.get(contentSharingPolicyFile.getPath()), policyJson.getBytes(), StandardOpenOption.CREATE));
			// after write, put currently policy on the map for the next iteration
			String fileName = contentSharingPolicyFile.getName().replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, contentSharingPolicy);
		} catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(String.format("Unable to store content sharing to file %s.", contentSharingPolicyFile.getAbsolutePath()), e);
		}
	}

	/**
	 * @param policyFolderPath            the correct sub folder path for the
	 *                                    policy. type.
	 * @param policy                      the policy that is exported.
	 * @param currentPoliciesOnFileSystem all the other policies currently in the
	 *                                    folder.
	 * @return the file where to store the policy.
	 */
	private File getPolicyFile(Path policyFolderPath, VraNgContentSharingPolicy policy, Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem) {
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
		File policyFile = Paths.get(String.valueOf(policyFolderPath), filename + CUSTOM_RESOURCE_SUFFIX).toFile();

		return policyFile;
	}

	/**
	 * Read the file system where policies will be stored, make a map of pre
	 * existing files there, to avoid duplication and/or unintentional overwriting.
	 * 
	 * @param policyFolderPath get actual path where policies should be stored.
	 * @return a map of filenames and policies, found in the path.
	 */
	private Map<String, VraNgContentSharingPolicy> getCurrentPoliciesOnFileSystem(Path policyFolderPath) {
		// First make sure path exists and is a folder.
		if (!policyFolderPath.toFile().isDirectory()) {
			logger.warn("Could find directory: {}", policyFolderPath.toFile().getAbsolutePath());
			return new HashMap<String, VraNgContentSharingPolicy>();
		}

		File[] policyFiles = policyFolderPath.toFile().listFiles();
		Map<String, VraNgContentSharingPolicy> currentPoliciesOnFileSystem = new HashMap<>();
		for (File policyFile : policyFiles) {
			String fileNameWithExt = policyFile.getName();
			// exclude hidden files e.g. .DS_Store
			if (fileNameWithExt.startsWith(".")) {
				continue;
			}
			String fileName = fileNameWithExt.replace(CUSTOM_RESOURCE_SUFFIX, "");
			currentPoliciesOnFileSystem.put(fileName, this.jsonFileToVraNgContentSharingPolicy(policyFile));
		}

		return currentPoliciesOnFileSystem;
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
}
