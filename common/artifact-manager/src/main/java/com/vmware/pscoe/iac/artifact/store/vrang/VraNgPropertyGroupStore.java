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
package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_PROPERTY_GROUPS;

/**
 * Store responsible for exporting and importing Property groups from vRA 8.3+.
 */
public final class VraNgPropertyGroupStore extends AbstractVraNgStore {
	/**
	 * List of existing property groups on target VRA.
	 */
	private List<VraNgPropertyGroup> existingPropertyGroups;
	/**
	 * Project Id from configuration.
	 */
	private String projectId;
	/**
	 * Suffix used for the property groups.
	 */
	private static final String PROPERTY_GROUP_SUFFIX = ".json";

	/**
	 * Initialize store.
	 * Cache some items from VRA - e.g. porjectId and existing proeprty groups.
	 */
	@Override
	public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
			VraNgPackageDescriptor vraNgPackageDescriptor) {
		super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.existingPropertyGroups = this.restClient.getPropertyGroups();
		this.projectId = this.restClient.getProjectId();
	}

	public void deleteContent() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Imports all content in content.yaml.
	 * WIll do nothing if property group dir does not exist
	 *
	 * @param sourceDirectory path of source directory
	 */
	@Override
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_PROPERTY_GROUPS);

		// verify directory exists
		File propertyGroupFolder = Paths.get(sourceDirectory.getPath(), DIR_PROPERTY_GROUPS).toFile();
		if (!propertyGroupFolder.exists()) {
			logger.info("Property Group Dir not found.");
			return;
		}

		File[] propertyGroupFiles = this.filterBasedOnConfiguration(propertyGroupFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (propertyGroupFiles == null || propertyGroupFiles.length == 0) {
			logger.info("Could not find any property groups.");
			return;
		}

		logger.info("Found property groups. Importing...");

		for (File file : propertyGroupFiles) {
			logger.info(file.getName());
			logger.info(file.getAbsolutePath());
			VraNgPropertyGroup propertyGroup = jsonFileToVraPropertyGroup(file);
			importPropertyGroup(propertyGroup, propertyGroupFolder);
		}
	}

	/**
	 * Imports or update a specific property group.
	 *
	 * Note: a property group cannot be deleted immediately before import or update.
	 * Because when property group delete is requested, the property group is only
	 * marked for deletion.
	 * Then it is deleted by the server at a later unspecified time.
	 * While the property group exists and is marked for deletion, all update
	 * operations succeed,
	 * however, all get operations for this property group fail.
	 * There is no way to restore such a property group, you have to wait for the
	 * deletion, and then create it again.
	 * 
	 * 
	 * Therefore, if deleting everything from an environment with the purpose of
	 * having a clean slate for import,
	 * one should make sure that enough time has passed, and everything is really
	 * deleted.
	 * 
	 * @param propertyGroup
	 * @param propertyGroupsFolder
	 * @throws Exception
	 */
	private void importPropertyGroup(VraNgPropertyGroup propertyGroup, File propertyGroupsFolder)
			throws UnsupportedOperationException {
		File customPropertyGroupFile = Paths.get(propertyGroupsFolder.getPath(), getName(propertyGroup)).toFile();
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		VraNgPropertyGroup existingPropertyGroup = this.getPropertyGroupByName(propertyGroup.getName());
		// Replace existing organisation id with the id of target organisation
		propertyGroup.setOrgId(organizationId);
		if (existingPropertyGroup != null) {
			// Property group with the same name already exists on the server
			// Update
			// First check for change of scope:
			// If a non-blank projectID is different from the configuration project ID, we
			// will request change of scope and the API will return an error.
			// Scope change is not an operation supported by the API.
			String existingPropertyGroupProjectId = existingPropertyGroup.getProjectId();
			if (existingPropertyGroupProjectId != null && !existingPropertyGroupProjectId.isBlank()
					&& !existingPropertyGroupProjectId.equals(this.projectId)) {

				String message = "The property group" + getName(propertyGroup)
						+ "is already assigned to a different project. Property group scope will NOT be changed and the group will NOT be updated.";
				throw new UnsupportedOperationException(message);
			}

			// If projectId is blank, then it is a global property group for all projects,
			// do not change the scope.
			// If projectId is not blank - replace it with target projectId
			if (propertyGroup.getProjectId() != null && !(propertyGroup.getProjectId().isBlank())) {
				logger.debug("Replacing propertyGroup projectId with projectId from configuration.");
				propertyGroup.setProjectId(this.projectId);
			}

			propertyGroup.setId(existingPropertyGroup.getId());
			logger.info("Updating property group: {}", customPropertyGroupFile.getAbsolutePath());
			this.restClient.updatePropertyGroup(propertyGroup);
		} else { // CREATE
			if (propertyGroup.getProjectId() != null && !(propertyGroup.getProjectId().isBlank())) {
				logger.debug("Replacing propertyGroup projectId with projectId from configuration.");
				propertyGroup.setProjectId(this.projectId);
			}
			logger.info("Creating property group: {}", customPropertyGroupFile.getAbsolutePath());
			this.restClient.createPropertyGroup(propertyGroup);
		}

	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return list of property groups
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getPropertyGroup();
	}

	/**
	 * Exports all the property groups, case when the user has not listed specific
	 * items for export.
	 */
	protected void exportStoreContent() {
		logger.info("Fetched propertyGroups: {}", new Gson().toJson(this.existingPropertyGroups));

		this.existingPropertyGroups
				.forEach(propertyGroup -> storePropertyGroupOnFileSystem(vraNgPackage, propertyGroup));
	}

	/**
	 * Export specific propertyGroupNames.
	 *
	 * @param propertyGroupNames list of group names
	 */
	protected void exportStoreContent(List<String> propertyGroupNames) {
		logger.info("Filtering Property Groups");
		propertyGroupNames.forEach(propertyGroupName -> {
			VraNgPropertyGroup propertyGroup = this.existingPropertyGroups.stream()
					.filter(gr -> propertyGroupName.equals(gr.getName()))
					.findAny()
					.orElse(null);
			if (propertyGroup == null) {
				throw new IllegalStateException(
						String.format(
								"Property Group [%s] not found on the server.",
								propertyGroupName));
			}
			storePropertyGroupOnFileSystem(vraNgPackage, propertyGroup);
		});
	}

	/**
	 * Get property group by given name or null if not existing.
	 * 
	 * @param propertyGroupName name of group to search for
	 * @return property grop or null
	 */
	private VraNgPropertyGroup getPropertyGroupByName(String propertyGroupName) {
		// Name is unique, so there should always be either one or empty
		return this.existingPropertyGroups.stream().filter(
				item -> item.getName().equalsIgnoreCase(propertyGroupName)).findFirst().orElse(null);
	}

	/**
	 * Stores the property group on the file system.
	 *
	 * @param serverPackage
	 * @param propertyGroup
	 */
	private void storePropertyGroupOnFileSystem(Package serverPackage, VraNgPropertyGroup propertyGroup) {
		logger.info("Storing PropertyGroup: {}", propertyGroup.getName());

		File store = new File(serverPackage.getFilesystemPath());
		File customPropertyGroupFile = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_PROPERTY_GROUPS,
				propertyGroup.getName() + PROPERTY_GROUP_SUFFIX).toFile();

		logger.debug("Creating folder: {}", customPropertyGroupFile.getParentFile().getAbsolutePath());

		if (!customPropertyGroupFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", customPropertyGroupFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject propertyGroupJsonElement = gson.fromJson(propertyGroup.getRawData(), JsonObject.class);

			this.sanitizePropertyGroupJsonElement(propertyGroupJsonElement);

			String propertyGroupJson = gson.toJson(propertyGroupJsonElement);
			logger.info("Created file {}", Files.write(
					Paths.get(customPropertyGroupFile.getPath()),
					propertyGroupJson.getBytes(),
					StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create property group {}", customPropertyGroupFile.getPath());
			throw new RuntimeException(
					String.format(
							"Unable to store property group to file %s.",
							customPropertyGroupFile.getPath()),
					e);
		}
	}

	/**
	 * Sanitize PropertyGroup json from unnecessary data.
	 * The data could prevent store or
	 * publish later the content to different vRA environments.
	 * 
	 * @param propertyGroupRawData
	 */
	private void sanitizePropertyGroupJsonElement(JsonObject propertyGroupRawData) {
		propertyGroupRawData.remove("projectName");
		propertyGroupRawData.remove("id");
		propertyGroupRawData.remove("createdAt");
		propertyGroupRawData.remove("createdBy");
		propertyGroupRawData.remove("updatedAt");
		propertyGroupRawData.remove("updatedBy");
	}

	/**
	 * Converts a json property group file to VraNgPropertyGroup.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgPropertyGroup
	 */
	private VraNgPropertyGroup jsonFileToVraPropertyGroup(File jsonFile) {
		logger.debug("Converting property group file to VraNgPropertyGroup. Name: " + jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			JsonObject propertyGroupJson = JsonParser.parseReader(reader).getAsJsonObject();
			return new VraNgPropertyGroup(
					propertyGroupJson.get("name").getAsString(),
					null,
					new Gson().toJson(propertyGroupJson));
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Returns custom property group resource name.
	 *
	 * @param propertyGroup
	 *
	 * @return String
	 */
	private String getName(VraNgPropertyGroup propertyGroup) {
		return propertyGroup.getName() + PROPERTY_GROUP_SUFFIX;
	}
}
