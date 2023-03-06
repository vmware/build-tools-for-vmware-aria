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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgWorkflow;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgWorkflowContentSource;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgIntegrationUtils;
import org.apache.commons.lang3.StringUtils;

public class VraNgContentSourceStore extends AbstractVraNgStore {
	private String projectId;
	private VrangContentSourceUtils utils;
	private VraNgIntegration configuredIntegration;
	private List<VraNgContentSourceBase> existingSources = null;

	@Override
	public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config, VraNgPackageDescriptor vraNgPackageDescriptor) {
		super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.utils					= new VrangContentSourceUtils(restClient, vraNgPackage);
		this.projectId				= this.restClient.getProjectId();
		this.configuredIntegration	= !StringUtils.isEmpty(this.config.getVroIntegration())
									? this.restClient.getVraWorkflowIntegration(this.config.getVroIntegration())
									: new VraNgIntegration();
	}

	@Override
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", VraNgDirs.DIR_CONTENT_SOURCES);
		File contentSourceFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_CONTENT_SOURCES).toFile();
		if (!contentSourceFolder.exists()) {
			logger.info("Content Source folder is missing '{}' ", VraNgDirs.DIR_CONTENT_SOURCES);
			return;
		}
		// Check if there are any blueprints to import
		File[] localList = this.filterBasedOnConfiguration(contentSourceFolder, new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (localList == null || localList.length == 0) {
			logger.info("No Content Source available - skip import");
			return;
		}
		for (File cs : localList) {
			this.importFile(cs);
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of content sources
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getContentSource();
	}

	/**
	 * Exports all content sources from service broker.
	 * Passing null to exportStoreContent and by extension to fetchAllContentSources will not apply a filter to the
	 * 	content sources.
	 */
	@Override
	protected void exportStoreContent() {
		this.exportStoreContent( null );
	}

	/**
	 * Exports filtered content sources from service broker
	 * If the content source is a VRO_WORKFLOW then it is checked if it has unique
	 * workflow names.sssss
	 *
	 * @param contentSourceNames list of names
	 */
	@Override
	protected void exportStoreContent(List<String> contentSourceNames) {
		if (contentSourceNames != null) {
			List<VraNgContentSourceBase> contentSources = this.validateNoDuplicateWf(
					this.fetchAllContentSources(contentSourceNames));
			contentSourceNames.forEach(contentSourceName -> {
				VraNgContentSourceBase contentSource = contentSources.stream()
						.filter(cs -> contentSourceName.equals(cs.getName()))
						.findAny().orElse(null);
				if (contentSource == null) {
					throw new IllegalStateException(
							String.format(
									"Content Source [%s] not found on the server.",
									contentSourceName));
				}
				this.utils.storeContentSourceOnFilesystem(contentSource);
			});
		}
	}

	/**
	 * Checks for duplicate workflows in the given content sources
	 *
	 * @param	contentSources - Content sources to check
	 */
	private List<VraNgContentSourceBase> validateNoDuplicateWf( List<VraNgContentSourceBase> contentSources ) {
		for ( VraNgContentSourceBase contentSource: contentSources ) {
			if ( contentSource.getType() == VraNgContentSourceType.VRO_WORKFLOW ) {
				ArrayList<String> duplicateWorkflows	= getDuplicateWorkflows( ( VraNgWorkflowContentSource ) contentSource );

				if ( duplicateWorkflows.size() > 0 ) {
					throw new RuntimeException(
						"Cannot have workflows with the same name in one content source. Problematic Workflows: "
							+ String.join( ", ", duplicateWorkflows )
					);
				}
			}
		}

		return contentSources;
	}

	void importFile(File contentSourceFile) {
		try (JsonReader reader = new JsonReader(new FileReader(contentSourceFile.getPath()))) {
			JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
			VraNgContentSourceType type = VraNgContentSourceType.fromString(jsonObj.get("typeId").getAsString());
			VraNgContentSourceBase contentSource = new Gson().fromJson(jsonObj, type.getTypeClass());
			switch (type) {
				case VRO_WORKFLOW:
					this.prepareWorkflowContentSource((VraNgWorkflowContentSource) contentSource);
					break;
				case BLUEPRINT:
				case CODE_STREAM:
				case ABX_ACTIONS:
					this.prepareProjectContentSource((VraNgContentSource) contentSource);
					break;
				default:
					logger.info("Skipping import of content source {}. Type {} is not handled!", contentSource.getName(), contentSource.getType());
					return;
			}
			this.utils.syncContentSource(contentSource, this.config.getImportTimeout());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void prepareWorkflowContentSource(VraNgWorkflowContentSource contentSource) {
		VraNgIntegration defaultIntegration = VraNgIntegrationUtils.getInstance()
			.getDefaultVraIntegration(this.restClient);

		ArrayList<String> duplicateWorkflows	= getDuplicateWorkflows( contentSource );

		if ( duplicateWorkflows.size() > 0 ) {
			throw new RuntimeException(
				"Cannot have workflows with the same name in one content source. Problematic Workflows: "
					+ String.join( ", ", duplicateWorkflows )
			);
		}

		List<String> wfIds = getWfIds(contentSource);
		// Find same content source by comparing id or name or workflows ids in order list
		String newId = this.getExistingSourcesFilteredByType(contentSource.getType(), VraNgWorkflowContentSource.class)
			.filter(existingCS -> existingCS.getName().equals(contentSource.getName())
				|| existingCS.getId().equals(contentSource.getId())
				|| getWfIds(existingCS).equals(wfIds))
			.map(existingCS -> existingCS.getId())
			.findFirst()
			.orElse("");
		contentSource.setId(newId);

		for (VraNgWorkflow workflow : contentSource.getConfig().getWorkflows()) {
			if (workflow == null || workflow.getIntegration() == null) {
				continue;
			}
			if (!StringUtils.isEmpty(configuredIntegration.getName())) {
				setIntegration(workflow, configuredIntegration);
				continue;
			}
			VraNgIntegration workflowIntegration = this.restClient.getVraWorkflowIntegration(workflow.getIntegration().getName());
			if (!StringUtils.isEmpty(workflowIntegration.getName())) {
				setIntegration(workflow, workflowIntegration);
				continue;
			}
			if (!StringUtils.isEmpty(defaultIntegration.getName())) {
				logger.warn("Unable to find integration '{}' on host '{}' setting default integration to '{}'",
					workflow.getIntegration().getName(), this.config.getHost(), defaultIntegration.getName());
				setIntegration(workflow, defaultIntegration);
				continue;
			}

			logger.warn(
				"Unable to find all of the following integrations on host '{}' configured : '{}' , resource: '{}' , default : '{}' ",
				this.config.getHost(), this.config.getVroIntegration(), workflow.getIntegration().getName(),
				VraNgIntegrationUtils.DEFAULT_INTEGRATION_NAME);
		}

		this.deleteBeforeCreation(contentSource.getId());
	}

	private void setIntegration(VraNgWorkflow workflow, VraNgIntegration targetIntegration) {
		workflow.getIntegration().setEndpointConfigurationLink(targetIntegration.getEndpointConfigurationLink());
		workflow.getIntegration().setEndpointUri(targetIntegration.getEndpointUri());
		workflow.getIntegration().setName(targetIntegration.getName());
	}

	private List<String> getWfIds(VraNgWorkflowContentSource contentSource) {
		return contentSource
			.getConfig()
			.getWorkflows()
			.stream()
			.map(integration -> integration.getId())
			.sorted()
			.collect(Collectors.toList());
	}

	/**
	 * Checks if there are any duplicate named workflows.
	 * A set will return false when doing Set.add if the item already exists
	 *
	 * https://docs.oracle.com/javase/8/docs/api/java/util/Set.html#add-E-
	 *
	 * @return	ArrayList<String>
	 */
	private ArrayList<String> getDuplicateWorkflows(VraNgWorkflowContentSource contentSource) {
		ArrayList<String> duplicates	= new ArrayList<>();
		Set<String> items				= new HashSet<>();
		List<VraNgWorkflow> workflows	= contentSource.getConfig().getWorkflows();

		for ( VraNgWorkflow workflow: workflows ) {
			String workflowName	= workflow.getName();
			if ( ! items.add( workflowName ) ) {
				duplicates.add( workflowName );
			}
		}

		return duplicates;
	}

	public void prepareProjectContentSource(VraNgContentSource contentSource) {
		Map<String, String> config = new HashMap<>();
		config.put("sourceProjectId", this.projectId);
		contentSource.setConfig(config);
		contentSource.setProjectId(this.projectId);
		String newId = this.fetchAllContentSources( null )
			.stream()
			.filter(existingCS -> existingCS.getType().equals(contentSource.getType()))
			.map(existingCS -> existingCS.getId())
			.findFirst()
			.orElse("");
		contentSource.setId(newId);
	}

	protected void deleteBeforeCreation(String contentSourceId) {
		if (contentSourceId != null) {
			// VRA version 8.0 complains with message 'content source already exists' if try to update it
			// that's why delete content source prior updating it if VRA is 8.0
			// VRA version 8.1 and newer does update the content source without complaining
			if (!this.restClient.isVraAbove81()) {
				this.restClient.deleteContentSource(contentSourceId);
			}
		}
	}

	private <T> Stream<T> getExistingSourcesFilteredByType(VraNgContentSourceType type, Class<T> clazz) {
		return this.fetchAllContentSources( null ).stream().filter(src -> src.getType().equals(type)).map(clazz::cast);
	}

	/**
	 * Gets all the content sources on the server.
	 * This will filter content sources that are not of the configured project id.
	 * If the content source is not associated with a project, it will be fetched as well.
	 * Passing contentSourceNames will mean that only they will be fetched. Passing null means all are accepted.
	 * The reason why we need 2 streams and 2 filters is since we want to cache ALL existing sources but we want the filter to be
	 * 	dynamic.
	 *
	 * @param	contentSourceNames - names to filter for or null for accept all
	 *
	 * @return	List<VraNgContentSourceBase>
	 */
	private List<VraNgContentSourceBase> fetchAllContentSources( List<String> contentSourceNames ) {
		if ( this.existingSources == null ) {
			this.existingSources	= this.restClient
				.getContentSourcesForProject( this.projectId )
				.stream()
				.filter( src -> this.utils.isForSameOrNoneProject( src, this.projectId ) )
				.collect( Collectors.toList() );
		}

		return this.existingSources
			.stream()
			.filter( src -> contentSourceNames == null || contentSourceNames.contains( src.getName() ) )
			.collect( Collectors.toList() );
	}
}
