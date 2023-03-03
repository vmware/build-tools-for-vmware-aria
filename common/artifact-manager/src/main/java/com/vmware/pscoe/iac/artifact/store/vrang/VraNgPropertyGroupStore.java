package com.vmware.pscoe.iac.artifact.store.vrang;

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

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_PROPERTY_GROUPS;

/**
 * Store responsible for exporting and importing Property groups from vRA 8.3+
 */
public class VraNgPropertyGroupStore extends AbstractVraNgStore {
	private List<VraNgPropertyGroup> existingPropertyGroups;
	private String projectId;
	/**
	 * Suffix used for the property groups
	 */
	private static final String PROPERTY_GROUP_SUFFIX	= ".json";

	@Override
    public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config, VraNgPackageDescriptor vraNgPackageDescriptor) {
        super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.existingPropertyGroups = this.restClient.getPropertyGroups();
		this.projectId = this.restClient.getProjectId();
	}

	/**
	 * Imports all content in content.yaml.
	 * WIll do nothing if property group dir does not exist
	 *
	 * @param	sourceDirectory path of source directory
	 */
	@Override
	public void importContent( File sourceDirectory ) {
		logger.info( "Importing files from the '{}' directory", DIR_PROPERTY_GROUPS );

		// verify directory exists
		File propertyGroupFolder = Paths.get( sourceDirectory.getPath(), DIR_PROPERTY_GROUPS ).toFile();
		if ( ! propertyGroupFolder.exists() ) {
			logger.info( "Property Group Dir not found." );
			return;
		}

		File[] propertyGroupFiles= this.filterBasedOnConfiguration(propertyGroupFolder, new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if ( propertyGroupFiles == null || propertyGroupFiles.length == 0) {
			logger.info( "Could not find any property groups." );
			return;
		}

		logger.info( "Found property groups. Importing..." );

		for (File file : propertyGroupFiles) {
			logger.info( file.getName() );
			logger.info( file.getAbsolutePath() );
			VraNgPropertyGroup propertyGroup	= jsonFileToVraPropertyGroup( file );
			importPropertyGroup( propertyGroup, propertyGroupFolder );
		}
	}

	/**
	 * Imports a specific property group.
	 * First checks if a property group with given name already exists,
	 * so it gets its id and based on that performs either create or update operation.
	 * If a property group doesn't exist set project ID in request.
	 * If a property group exists, set the property group id in the request, so vRA knows which PG to update
	 *
	 * @param propertyGroup
	 * @param propertyGroupsFolder
	 */
	private void importPropertyGroup(VraNgPropertyGroup propertyGroup, File propertyGroupsFolder) {
		File customPropertyGroupFile = Paths.get(propertyGroupsFolder.getPath(), getName(propertyGroup)).toFile();
		VraNgPropertyGroup existingPropertyGroup = this.getPropertyGroupByName(propertyGroup.getName());

		if (existingPropertyGroup != null) {
			if (!existingPropertyGroup.hasTheSameProjectId(this.projectId)) {
				logger.warn("The property group {} is already assigned to a different project. Property group scope will NOT be changed and the group will NOT be updated.", getName(propertyGroup));
			} else {
				propertyGroup.setId(existingPropertyGroup.getId());
				logger.info("Updating property group: {}", customPropertyGroupFile.getAbsolutePath());
				this.restClient.updatePropertyGroup(propertyGroup);
			}

		} else {
			propertyGroup.setProjectIdInRawData(this.projectId);

			logger.info("Creating property group: {}", customPropertyGroupFile.getAbsolutePath());
			this.restClient.createPropertyGroup(propertyGroup);
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of property groups
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getPropertyGroup();
	}

	/**
	 * Exports all the property groups, case when the user has not defined any
	 */
	protected void exportStoreContent() {
		logger.info( "Fetched propertyGroups: {}", new Gson().toJson( this.existingPropertyGroups ) );

		this.existingPropertyGroups.forEach( propertyGroup -> storePropertyGroupOnFileSystem( vraNgPackage, propertyGroup ) );
	}

	/**
	 * Export specific propertyGroupNames
	 *
	 * @param	propertyGroupNames list of group names
	 */
	protected void exportStoreContent( List<String> propertyGroupNames ) {
		logger.info( "Filtering Property Groups" );
		propertyGroupNames.forEach( propertyGroupName -> {
			VraNgPropertyGroup propertyGroup = this.existingPropertyGroups.stream()
				.filter(gr -> propertyGroupName.equals(gr.getName()))
				.findAny()
				.orElse(null);
			if (propertyGroup == null) {
				throw new IllegalStateException(
					String.format(
							"Property Group [%s] not found on the server.",
							propertyGroupName
				));
			}
			storePropertyGroupOnFileSystem( vraNgPackage, propertyGroup );
		});
	}

	/**
	 * Get property group by given name or null if not existing
	 *
	 * @param	propertyGroupName
	 */
	private VraNgPropertyGroup getPropertyGroupByName(String propertyGroupName) {
		// Name is unique, so there should always be either one or empty
		return this.existingPropertyGroups.stream().filter(
				item -> item.getName().equalsIgnoreCase( propertyGroupName )
			).findFirst().orElse(null);
	}

	/**
	 * Stores teh property group on the file system
	 *
	 * @param	serverPackage
	 * @param	propertyGroup
	 */
	private void storePropertyGroupOnFileSystem( Package serverPackage, VraNgPropertyGroup propertyGroup ) {
		logger.info( "Storing PropertyGroup: {}", propertyGroup.getName() );

		File store						= new File( serverPackage.getFilesystemPath() );
		File customPropertyGroupFile	= Paths.get(
			store.getPath(),
			VraNgDirs.DIR_PROPERTY_GROUPS,
			propertyGroup.getName() + PROPERTY_GROUP_SUFFIX
		).toFile();

		logger.debug( "Creating folder: {}", customPropertyGroupFile.getParentFile().getAbsolutePath() );

		if ( ! customPropertyGroupFile.getParentFile().mkdirs() ) {
			logger.warn( "Could not create folder: {}", customPropertyGroupFile.getParentFile().getAbsolutePath() );
		}

		try {
			Gson gson	= new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject propertyGroupJsonElement = gson.fromJson(propertyGroup.getRawData(), JsonObject.class);

            this.sanitizePropertyGroupJsonElement(propertyGroupJsonElement);

            String propertyGroupJson = gson.toJson(propertyGroupJsonElement);
            logger.info("Created file {}", Files.write(
				Paths.get(customPropertyGroupFile.getPath()),
				propertyGroupJson.getBytes(), 
				StandardOpenOption.CREATE
			));
		} catch ( IOException e ) {
			logger.error( "Unable to create property group {}", customPropertyGroupFile.getPath() );
			throw new RuntimeException(
				String.format(
					"Unable to store property group to file %s.",
					customPropertyGroupFile.getPath()
				),
				e
			);
		}
	}

	/**
     * Sanitize PropertyGroup json from unnecessary elements that prevent store or
     * publish later the content to different vRA environments.
	 * 
	 * @param propertyGroupRawData
     */
    private void sanitizePropertyGroupJsonElement(JsonObject propertyGroupRawData) {
        propertyGroupRawData.remove("orgId");
        propertyGroupRawData.remove("projectId");
        propertyGroupRawData.remove("projectName");
        propertyGroupRawData.remove("id");
        propertyGroupRawData.remove("createdAt");
        propertyGroupRawData.remove("createdBy");
        propertyGroupRawData.remove("updatedAt");
        propertyGroupRawData.remove("updatedBy");
    }

	/**
	 * Converts a json property group file to VraNgPropertyGroup
	 *
	 * @param	jsonFile
	 *
	 * @return	VraNgPropertyGroup
	 */
	private VraNgPropertyGroup jsonFileToVraPropertyGroup( File jsonFile ) {
		logger.debug( "Converting property group file to VraNgPropertyGroup. Name: " + jsonFile.getName() );

		try ( JsonReader reader = new JsonReader( new FileReader( jsonFile.getPath() ) ) ) {
			JsonObject propertyGroupJson = JsonParser.parseReader(reader).getAsJsonObject();
			return new VraNgPropertyGroup(
				propertyGroupJson.get("name").getAsString(),
				null,
				new Gson().toJson(propertyGroupJson)
			);
		} catch ( IOException e ) {
			throw new RuntimeException( String.format( "Error reading from file: %s", jsonFile.getPath() ), e );
		}
	}

	/**
	 * Returns custom property group resource name
	 *
	 * @param	propertyGroup
	 *
	 * @return	String
	 */
	private String getName( VraNgPropertyGroup propertyGroup ) {
		return propertyGroup.getName() + PROPERTY_GROUP_SUFFIX;
	}
}
