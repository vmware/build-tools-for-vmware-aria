package com.vmware.pscoe.iac.artifact.rest;

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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.model.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlement;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgImageMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegion;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSecret;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgStorageProfile;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSubscription;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgWorkflowContentSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;

public class RestClientVraNg extends RestClientVraNgPrimitive {
	/**
	 * logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(RestClientVraNg.class);

	/**
	 * Constructor for RestClientVraNg.
	 * 
	 * @param configuration
	 * @param restTemplate
	 */
	protected RestClientVraNg(final ConfigurationVraNg configuration, final RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	// =================================================
	// ICON OPERATIONS
	// =================================================

	/**
	 * downloadIcon.
	 * 
	 * @param iconId
	 * @return ResponseEntity
	 */
	public ResponseEntity<byte[]> downloadIcon(final String iconId) {
		try {
			return downloadIconPrimitive(iconId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch icon with id '%s'.", iconId), e);
		}
	}

	/**
	 * Upload an Icon.
	 * 
	 * @param iconFile
	 * @return ResponseEntity
	 */
	public ResponseEntity<String> uploadIcon(final File iconFile) {
		try {
			return uploadIconPrimitive(iconFile);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not upload icon file '%s'.", iconFile.getAbsolutePath()),
					e);
		}
	}

	/**
	 * Patch a catalog item.
	 * 
	 * @param catalogItem
	 * @param iconId
	 * @return ResponseEntity
	 */
	public ResponseEntity<String> patchCatalogItemIcon(final VraNgCatalogItem catalogItem, final String iconId) {
		try {
			return patchCatalogItemIconPrimitive(catalogItem, iconId);
		} catch (Exception e) {
			throw new RuntimeException("Could not patch icon for catalogItem", e);
		}
	}

	// =================================================
	// BLUEPRINT OPERATIONS
	// =================================================

	/**
	 * Creates a blueprint.
	 * 
	 * @param blueprint
	 * @return id
	 */
	public String createBlueprint(final VraNgBlueprint blueprint) {
		try {
			return createBlueprintPrimitive(blueprint);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Blueprint with name '%s'.", blueprint.getName()),
					e);
		}
	}

	/**
	 * Updates the blueprint.
	 * 
	 * @param blueprint
	 * @return id
	 */
	public String updateBlueprint(final VraNgBlueprint blueprint) {
		try {
			return updateBlueprintPrimitive(blueprint);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Blueprint with name '%s'.", blueprint.getName()),
					e);
		}
	}

	/**
	 * getAllBlueprints.
	 * 
	 * @return blueprint
	 */
	public List<VraNgBlueprint> getAllBlueprints() {
		try {
			return getAllBlueprintsPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not fetch blueprints.", e);
		}
	}

	/**
	 * getBlueprintLastUpdatedVersion.
	 * 
	 * @param blueprintId
	 * @return id
	 */
	public String getBlueprintLastUpdatedVersion(final String blueprintId) {
		try {
			return this.getBlueprintLastUpdatedVersionPrimitive(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not fetch Blueprint last version for id '%s'.", blueprintId), e);
		}
	}

	/**
	 * getBlueprintVersionContent.
	 * 
	 * @param blueprintId
	 * @param version
	 * @return content
	 */
	public String getBlueprintVersionContent(final String blueprintId, final String version) {
		try {
			return this.getBlueprintVersionContentPrimitive(blueprintId, version);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch Blueprint content with version '%s' and id '%s'.",
					blueprintId, version), e);
		}
	}

	/**
	 * Fetching the version details for a blueprint using the vRA REST API.
	 * 
	 * @param blueprintId blueprintId
	 * @return version
	 */
	public String getBlueprintVersions(final String blueprintId) {
		try {
			return this.getBlueprintVersionsContent(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch Blueprint version for id '%s'.", blueprintId), e);
		}
	}

	/**
	 * isBlueprintVersionPresent.
	 * 
	 * @param blueprintId
	 * @param version
	 * @return boolean
	 */
	public Boolean isBlueprintVersionPresent(final String blueprintId, final String version) {
		try {
			return this.isBlueprintVersionPresentPrimitive(blueprintId, version);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("No Blueprint present for version '%s' and id '%s'.", blueprintId, version), e);
		}
	}

	/**
	 * isBlueprintReleased.
	 * 
	 * @param blueprintId
	 * @return boolean
	 */
	public boolean isBlueprintReleased(final String blueprintId) {
		try {
			return this.isBlueprintReleasedPrimitive(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("No Blueprint released with id '%s'.", blueprintId), e);
		}
	}

	/**
	 * releaseBlueprintVersion.
	 *
	 * @param blueprintId
	 * @param version
	 */
	public void releaseBlueprintVersion(final String blueprintId, final String version) {
		try {
			this.releaseBlueprintVersionPrimitive(blueprintId, version);
		} catch (URISyntaxException e) {
			logger.error("Could not release blueprint {}", blueprintId);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Import missing versions into vRA for a specified blueprint.
	 *
	 * @param blueprintId blueprintId
	 * @param version     version
	 */
	public void importBlueprintVersion(final String blueprintId, final JsonObject version) {
		try {
			// There is no update or remove endpoints, so we're only importing ones that are
			// not present
			if (!this.isBlueprintVersionPresentPrimitive(blueprintId, version.get("id").getAsString())) {
				String changelog = version.has("versionChangeLog") ? version.get("versionChangeLog").getAsString() : "";
				String description = version.has("versionDescription") ? version.get("versionDescription").getAsString()
						: "";
				Map<String, Object> versionDetails = new LinkedHashMap<>();
				versionDetails.put("version", version.get("id").getAsString());
				versionDetails.put("release", (version.get("status").getAsString().equals("RELEASED")));
				versionDetails.put("changeLog", changelog);
				versionDetails.put("description", description);
				this.createBlueprintVersionPrimitive(blueprintId, versionDetails);
			}
		} catch (URISyntaxException e) {
			logger.error("Could not import blueprint {}", blueprintId);
			throw new RuntimeException(e);
		}
	}

	// =================================================
	// SUBSCRIPTION OPERATIONS
	// =================================================

	/**
	 * importSubscription.
	 *
	 * @param subscriptionName
	 * @param subscriptionJson
	 */
	public void importSubscription(final String subscriptionName, final String subscriptionJson) {
		try {
			importSubscriptionPrimitive(subscriptionName, subscriptionJson);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not import Subscription with name '%s'.", subscriptionName),
					e);
		}
	}

	/**
	 * getAllSubscriptions.
	 *
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getAllSubscriptions() {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE'");
	}

	/**
	 * getSubscriptionsByName.
	 *
	 * @param name
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByName(final String name) {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE' and name eq '" + name + "'");
	}

	/**
	 * getSubscriptionsByOrgId.
	 *
	 * @param orgId
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByOrgId(final String orgId) {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE' and orgId eq '" + orgId + "'");
	}

	/**
	 * getSubscriptionsByOrgIdAndName.
	 *
	 * @param orgId
	 * @param name
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByOrgIdAndName(final String orgId, final String name) {
		return getAllSubscriptionsPrimitive(
				"type ne 'SUBSCRIBABLE' and orgId eq '" + orgId + "' and name eq '" + name + "'");
	}

	// =================================================
	// CLOUD ACCOUNT OPERATIONS
	// =================================================

	/**
	 * getCloudAccounts.
	 *
	 * @return cloudAccounts
	 */
	public List<VraNgCloudAccount> getCloudAccounts() {
		try {
			return getAllCloudAccounts();
		} catch (Exception e) {
			throw new RuntimeException("Could not get cloud accounts", e);
		}
	}

	/**
	 * getCloudAccount.
	 *
	 * @param id
	 * @return cloudAccount
	 */
	public VraNgCloudAccount getCloudAccount(final String id) {
		try {
			return getCloudAccountPrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get cloud account with id '%s'.", id), e);
		}
	}

	// =================================================
	// SECRET OPERATIONS
	// =================================================

	/**
	 * Retrieve Secret by name (name is unique for secrets).
	 * 
	 * @param name of the secret
	 * @return item
	 */
	public VraNgSecret getSecret(final String name) {
		try {
			return getSecretPrimitive(name);
		} catch (Exception e) {
			logger.error("Error fetching secrets.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	// =================================================
	// REGION OPERATIONS
	// =================================================

	/**
	 * getRegion.
	 *
	 * @param id
	 * @return region
	 */
	public VraNgRegion getRegion(final String id) {
		try {
			return getRegionPrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get region with id '%s'.", id), e);
		}
	}

	// =================================================
	// FLAVOR PROFILES OPERATIONS
	// =================================================

	/**
	 * getAllFlavorMappingsByRegion.
	 *
	 * @return flavorMappings
	 */
	public Map<String, List<VraNgFlavorMapping>> getAllFlavorMappingsByRegion() {
		try {
			return getAllFlavorMappingsByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all flavour mappings by region", e);
		}
	}

	/**
	 * getAllFlavorProfilesByRegion.
	 *
	 * @return flavourProfiles
	 */
	public Map<String, List<String>> getAllFlavorProfilesByRegion() {
		try {
			return getAllFlavorProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all flavour profiles by region.", e);
		}
	}

	/**
	 * createFlavor.
	 *
	 * @param regionId
	 * @param flavorProfileName
	 * @param flavorMappings
	 */
	public void createFlavor(final String regionId, final String flavorProfileName,
			final List<VraNgFlavorMapping> flavorMappings) {
		try {
			createFlavorPrimitive(regionId, flavorProfileName, flavorMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Flavor with name '%s'.", flavorProfileName), e);
		}
	}

	/**
	 * updateFlavor.
	 *
	 * @param flavorId
	 * @param flavorMappings
	 */
	public void updateFlavor(final String flavorId, final List<VraNgFlavorMapping> flavorMappings) {
		try {
			updateFlavorPrimitive(flavorId, flavorMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Flavor Profile with id '%s'.", flavorId), e);
		}
	}

	// =================================================
	// IMAGE PROFILES OPERATIONS
	// =================================================

	/**
	 * getAllImageMappingsByRegion.
	 * 
	 * @return imageMappings
	 */
	public Map<String, List<VraNgImageMapping>> getAllImageMappingsByRegion() {
		try {
			return getAllImageMappingsByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all image mappings by region.", e);
		}
	}

	/**
	 * getAllImageProfilesByRegion.
	 *
	 * @return imageProfiles
	 */
	public Map<String, List<String>> getAllImageProfilesByRegion() {
		try {
			return getAllImageProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all image profiles by region.", e);
		}
	}

	/**
	 * createImageProfile.
	 *
	 * @param regionId
	 * @param profileName
	 * @param imageMappings
	 */
	public void createImageProfile(final String regionId, final String profileName,
			final List<VraNgImageMapping> imageMappings) {
		try {
			createImageProfilePrimitive(regionId, profileName, imageMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Image Profile with name '%s'.", profileName), e);
		}
	}

	/**
	 * updateImageProfile.
	 *
	 * @param profileId
	 * @param imageMappings
	 */
	public void updateImageProfile(final String profileId, final List<VraNgImageMapping> imageMappings) {
		try {
			updateImageProfilePrimitive(profileId, imageMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Image Profile with id '%s'.", profileId), e);
		}
	}

	// =================================================
	// STORAGE PROFILES OPERATIONS
	// =================================================

	/**
	 * getAllStorageProfilesByRegion.
	 *
	 * @return storageProfile
	 */
	public Map<String, List<VraNgStorageProfile>> getAllStorageProfilesByRegion() {
		try {
			return getAllStorageProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all storage profiles by region.", e);
		}
	}

	/**
	 * updateStorageProfile.
	 *
	 * @param profileId
	 * @param profile
	 */
	public void updateStorageProfile(final String profileId, final VraNgStorageProfile profile) {
		try {
			updateStorageProfilePrimitive(profileId, profile);
		} catch (Exception e) {
			logger.debug("Could not update Storage Profile with name '{}' and id '{}' using payload\n{}",
					profile.getName(), profileId, profile.getJson());
			throw new RuntimeException(
					String.format("Could not update Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	/**
	 * createStorageProfile.
	 *
	 * @param profile
	 * @return storageProfile
	 */
	public String createStorageProfile(final VraNgStorageProfile profile) {
		try {
			return createStorageProfilePrimitive(profile);
		} catch (Exception e) {
			logger.debug("Could not create Storage Profile with name '{}' using payload\n{}", profile.getName(),
					profile.getJson());
			throw new RuntimeException(
					String.format("Could not create Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	/**
	 * getSpecificStorageProfile.
	 *
	 * @param targetPool
	 * @param profileId
	 * @return storageProfile
	 */
	public VraNgStorageProfile getSpecificStorageProfile(final String targetPool, final String profileId) {
		try {
			return getSpecificProfilePrimitive(targetPool, profileId);
		} catch (Exception e) {
			logger.debug("Could not get Storage Profile with id '{}'", profileId);
			throw new RuntimeException(String.format("Could not get Storage Profile with id '%s'.", profileId), e);
		}
	}

	/**
	 * updateSpecificProfile.
	 *
	 * @param patchTarget
	 * @param profileId
	 * @param profile
	 */
	public void updateSpecificProfile(final String patchTarget, final String profileId,
			final VraNgStorageProfile profile) {
		try {
			updateSpecificProfilePrimitive(patchTarget, profileId, profile);
		} catch (Exception e) {
			logger.debug("Could not update Storage Profile with name '{}' and id '{}' using payload\n{}",
					profile.getName(), profileId, profile.getJson());
			throw new RuntimeException(
					String.format("Could not update Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	/**
	 * getFabricEntityName.
	 *
	 * @param fabricUrl
	 * @return name
	 */
	public String getFabricEntityName(final String fabricUrl) {
		try {
			return getFabricEntityNamePrimitive(fabricUrl);
		} catch (Exception e) {
			logger.debug("Could not get name from fabric '{}'", fabricUrl);
			throw new RuntimeException(String.format("Could not get name from fabric '%s'.", fabricUrl), e);
		}
	}

	/**
	 * getFabricEntityId.
	 *
	 * @param fabricType
	 * @param fabricName
	 * @return id
	 */
	public String getFabricEntityId(final String fabricType, final String fabricName) {
		try {
			return getFabricEntityIdPrimitive(fabricType, fabricName);
		} catch (Exception e) {
			logger.debug("Could not get id from {} '{}'", fabricType, fabricName);
			throw new RuntimeException(String.format("Could not get id from %s '%s'.", fabricType, fabricName), e);
		}
	}

	// =================================================
	// CATALOG OPERATIONS
	// =================================================

	/**
	 * NOTE: Blueprint name is always going to be the catalog item name, method name
	 * seems misleading.
	 *
	 * @param blueprintName bp name
	 *
	 * @return VraNgCatalogItem
	 */
	public VraNgCatalogItem getCatalogItemByBlueprintName(final String blueprintName) {
		try {
			return this.getCatalogItemByBlueprintNamePrimitive(blueprintName);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not get catalog item by blueprint name '%s'.", blueprintName), e);
		}
	}

	/**
	 * getCatalogItemsForProject.
	 *
	 * @param project
	 * @return catalogItem
	 */
	public List<VraNgCatalogItem> getCatalogItemsForProject(final String project) {
		try {
			return this.getCatalogItemsForProjectPrimitive(project);
		} catch (Exception e) {
			logger.error("Error fetching catalog items for project '{}' : {}", project, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getPropertyGroups.
	 *
	 * @return propertyGroup
	 */
	public List<VraNgPropertyGroup> getPropertyGroups() {
		try {
			return this.getAllPropertyGroupsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching property groups: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createPropertyGroup.
	 *
	 * @param propertyGroup
	 */
	public void createPropertyGroup(final VraNgPropertyGroup propertyGroup) {
		try {
			this.createPropertyGroupPrimitive(propertyGroup);
		} catch (Exception e) {
			logger.error("Error importing property group {}. Create operation has failed with error: {}",
					propertyGroup.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * updatePropertyGroup.
	 *
	 * @param propertyGroup
	 */
	public void updatePropertyGroup(final VraNgPropertyGroup propertyGroup) {
		try {
			this.updatePropertyGroupPrimitive(propertyGroup);
		} catch (Exception e) {
			logger.error("Error importing property group {}. Update operation has failed with error: {}",
					propertyGroup.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getCatalogItemsForProjects.
	 *
	 * @param projects
	 * @return catalogItems
	 */
	public Map<String, List<VraNgCatalogItem>> getCatalogItemsForProjects(final List<String> projects) {
		try {
			return this.getCatalogItemsForProjectsPrimitive(projects);
		} catch (Exception e) {
			logger.error("Error fetching catalog items for projects '{}' : {}",
					projects.stream().collect(Collectors.joining(", ")), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getContentSource.
	 *
	 * @param id
	 * @return contentSourceBase
	 */
	public VraNgContentSourceBase getContentSource(final String id) {
		try {
			return this.getContentSourcePrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get content source by id '%s'.", id, e));
		}
	}

	/**
	 * getVraWorkflowContentSource.
	 *
	 * @param id
	 * @return workflowContentSource
	 */
	public VraNgWorkflowContentSource getVraWorkflowContentSource(final String id) {
		try {
			return this.getVraWorkflowContentSourcePrimitive(id);
		} catch (Exception e) {
			logger.error("Could not find VRA workflow content source {}", id);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * getBlueprintContentSourceForProject.
	 *
	 * @return contentSource
	 */
	public VraNgContentSource getBlueprintContentSourceForProject() {
		try {
			return this.getBlueprintContentSourceForProjectPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching blueprint content sources for project: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getContentSourcesForProject.
	 *
	 * @param project
	 * @return contentSourceBase
	 */
	public List<VraNgContentSourceBase> getContentSourcesForProject(final String project) {
		try {
			return this.getContentSourcesForProjectPrimitive(project);
		} catch (Exception e) {
			logger.error("Error fetching content sources for project '{}': {}", project, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getContentSourcesForProjects.
	 *
	 * @param projects
	 * @return contentSourceBase
	 */
	public Map<String, List<VraNgContentSourceBase>> getContentSourcesForProjects(final List<String> projects) {
		try {
			return this.getContentSourcesForProjectsPrimitive(projects);
		} catch (Exception e) {
			logger.error("Error fetching content sources for project '{}': {}",
					projects.stream().collect(Collectors.joining(", ")), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getCustomFormByTypeAndSource.
	 *
	 * @param type
	 * @param sourceId
	 * @return customForm
	 */
	public VraNgCustomForm getCustomFormByTypeAndSource(final String type, final String sourceId) {
		try {
			return this.getCustomFormByTypeAndSourcePrimitive(type, sourceId);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not get custom form by type '%s' and source '%s'.", type, sourceId, e));
		}
	}

	/**
	 * getCatalogItemVersions.
	 *
	 * @param sourceId
	 * @return catalogItemVersion
	 */
	public JsonArray getCatalogItemVersions(final String sourceId) {
		try {
			return this.getCatalogItemVersionsPrimitive(sourceId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get custom form by source '%s'.", sourceId, e));
		}
	}

	/**
	 * fetchRequestForm.
	 *
	 * @param type
	 * @param sourceId
	 * @param formId
	 * @return customForm
	 */
	public VraNgCustomForm fetchRequestForm(final String type, final String sourceId, final String formId) {
		try {
			return this.fetchRequestFormPrimitive(type, sourceId, formId);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Could not get custom form by type '%s', source '%s' and formId '$s'.", type, sourceId, formId, e));
		}
	}

	/**
	 * createOrUpdateContentSource.
	 *
	 * @param contentSource
	 * @return id
	 */
	public String createOrUpdateContentSource(final VraNgContentSourceBase contentSource) {
		try {
			return this.createOrUpdateContentSourcePrimitive(contentSource);
		} catch (Exception e) {
			logger.error("Could not create or update content source {} : {}", contentSource.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getVraWorkflowCustomForm.
	 *
	 * @param formName
	 * @return customForm
	 */
	public VraNgCustomForm getVraWorkflowCustomForm(final String formName) {
		try {
			return this.getVraWorkflowCustomFormPrimitive(formName);
		} catch (Exception e) {
			logger.error("Error fetching VRA workflow custom form {} : {}", formName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getVraWorkflowIntegration.
	 *
	 * @param name
	 * @return integration
	 */
	public VraNgIntegration getVraWorkflowIntegration(final String name) {
		try {
			return this.getVraWorkflowIntegrationPrimitive(name);
		} catch (Exception e) {
			logger.error("Error fetching VRA workflow integration {} : {}", name, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getVraWorkflowIntegrations.
	 *
	 * @return integrations
	 */
	public List<VraNgIntegration> getVraWorkflowIntegrations() {
		try {
			return this.getVraWorkflowIntegrationsPrimitive();
		} catch (Exception e) {
			logger.error("Error retrieving list of VRA workflow integrations: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * importCustomForm.
	 *
	 * @param customForm
	 * @param sourceId
	 */
	public void importCustomForm(final VraNgCustomForm customForm, final String sourceId) {
		try {
			this.importCustomFormPrimitive(customForm, sourceId);
		} catch (Exception e) {
			logger.error("Could not import custom form {} : {}", customForm.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getAllCatalogEntitlements.
	 *
	 * @return catalogEntitlements
	 */
	public List<VraNgCatalogEntitlement> getAllCatalogEntitlements() {
		try {
			return this.getAllCatalogEntitlementsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all catalog entitlements: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createCatalogEntitlement.
	 *
	 * @param entitlement
	 * @param project
	 */
	public void createCatalogEntitlement(final VraNgCatalogEntitlement entitlement, final String project) {
		try {
			this.createCatalogEntitlementPrimitive(entitlement, project);
		} catch (Exception e) {
			logger.error("Error creating catalog entitlement '{}': {}", entitlement.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getProjectsByName.
	 *
	 * @param projectName
	 * @return projects
	 */
	public List<VraNgProject> getProjectsByName(final String projectName) {
		try {
			return this.getProjectsPrimitive(projectName);
		} catch (Exception e) {
			logger.error("Error fetching VRA projects with name '{}': {}", projectName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getProjects.
	 * 
	 * @return projects
	 */
	public List<VraNgProject> getProjects() {
		try {
			return this.getProjectsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching VRA projects : {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getProjectIdByName.
	 *
	 * @param projectName
	 * @return id
	 */
	public String getProjectIdByName(final String projectName) {
		try {
			return this.getProjectIdPrimitive(projectName);
		} catch (Exception e) {
			logger.error("Error fetching VRA project id for project name '{}': {}", projectName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getProjectNameById.
	 *
	 * @param projectId
	 * @return id
	 */
	public String getProjectNameById(final String projectId) {
		try {
			return this.getProjectNamePrimitive(projectId);
		} catch (Exception e) {
			logger.error("Error fetching VRA project name for project id '{}': {}", projectId, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	// =================================================
	// CUSTOM RESOURCES OPERATIONS
	// =================================================

	/**
	 * getAllCustomResources.
	 * 
	 * @return customResources
	 */
	public Map<String, VraNgCustomResource> getAllCustomResources() {
		try {
			return getAllCustomResourcesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all custom resources.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * importCustomResource.
	 *
	 * @param customResourceName
	 * @param customResourceJson
	 */
	public void importCustomResource(final String customResourceName, final String customResourceJson) {
		try {
			importCustomResourcePrimitive(customResourceJson);
		} catch (HttpClientErrorException httpClientErrorException) {
			throw httpClientErrorException;
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not import custom resource with name '%s'.", customResourceName), e);
		}
	}

	/**
	 * deleteCustomResource.
	 * 
	 * @param customResourceName
	 * @param customResourceId
	 */
	public void deleteCustomResource(final String customResourceName, final String customResourceId) {
		try {
			deleteCustomResourcePrimitive(customResourceId);
		} catch (HttpClientErrorException httpClientErrorException) {
			throw httpClientErrorException;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete custom resource with name '%s' (id:%s).",
					customResourceName, customResourceId), e);
		}
	}

	// =================================================
	// RESOURCE ACTIONS OPERATIONS
	// =================================================

	/**
	 * getAllResourceActions.
	 * 
	 * @return resourceActionsMap
	 */
	public Map<String, VraNgResourceAction> getAllResourceActions() {
		try {
			return getAllResourceActionsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all resource actions.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * importResourceAction.
	 * 
	 * @param resourceActionName
	 * @param resourceActionJson
	 * @return id
	 */
	public String importResourceAction(final String resourceActionName, final String resourceActionJson) {
		try {
			return importResourceActionPrimitive(resourceActionJson);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not import resource action with name '%s'.", resourceActionName),
					e);
		}
	}

	/**
	 * deleteResourceAction.
	 * 
	 * @param resourceActionName
	 * @param resourceActionId
	 */
	public void deleteResourceAction(final String resourceActionName, final String resourceActionId) {
		try {
			deleteResourceActionPrimitive(resourceActionId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete resource action with name '%s' and id '%s'.",
					resourceActionName, resourceActionId), e);
		}
	}

	// =================================================
	// ABX OPERATIONS
	// =================================================

	/**
	 * createAbxAction.
	 * 
	 * @param action
	 * @return id
	 */
	public String createAbxAction(final AbxAction action) {
		try {
			return createAbxActionPrimitive(action);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create ABX action with name '%s'.", action.getName()),
					e);
		}
	}

	/**
	 * updateAbxAction.
	 * 
	 * @param actionId
	 * @param action
	 * @return actionId
	 */
	public String updateAbxAction(final String actionId, final AbxAction action) {
		try {
			return updateAbxActionPrimitive(actionId, action);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update ABX action with name '%s'.", action.getName()),
					e);
		}
	}

	/**
	 * getAbxLastUpdatedVersion.
	 * 
	 * @param action
	 * @return version
	 */
	public AbxActionVersion getAbxLastUpdatedVersion(final AbxAction action) {
		try {
			return getAbxLastUpdatedVersionPrimitive(action.id);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not get latest version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	/**
	 * createAbxVersion.
	 * 
	 * @param action
	 * @param version
	 * @return abxVersion
	 */
	public AbxActionVersion createAbxVersion(final AbxAction action, final String version) {
		try {
			return createAbxVersionPrimitive(action.id, version);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	/**
	 * releaseAbxVersion.
	 * 
	 * @param action
	 * @param versionId
	 * @return version
	 */
	public AbxActionVersion releaseAbxVersion(final AbxAction action, final String versionId) {
		try {
			return releaseAbxVersionPrimitive(action.id, versionId);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not release version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	/**
	 * getAllAbxActions.
	 * 
	 * @return actions
	 */
	public List<AbxAction> getAllAbxActions() {
		try {
			return getAllAbxActionsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all abx actions.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve ABX Constant by name (name is unique for the constants).
	 * 
	 * @param name of the constant
	 * @return item
	 */
	public AbxConstant getAbxConstant(final String name) {
		try {
			return getAbxConstantPrimitive(name);
		} catch (Exception e) {
			logger.error("Error fetching abx constant.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	// =================================================
	// POLICIES
	// =================================================

	/**
	 * getContentSharingPolicyIds.
	 * 
	 * @return ids
	 */
	public List<String> getContentSharingPolicyIds() {
		try {
			return this.getAllContentSharingPolicyIdsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching content sharing policy Ids", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createContentSharingPolicy.
	 * 
	 * @param csPolicy
	 */
	public void createContentSharingPolicy(final VraNgContentSharingPolicy csPolicy) {
		try {
			createContentSharingPolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Content Sharing policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	/**
	 * updateContentSharingPolicy.
	 * 
	 * @param csPolicy
	 */
	public void updateContentSharingPolicy(final VraNgContentSharingPolicy csPolicy) {
		try {
			updateContentSharingPolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not update Content Sharing policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	/**
	 * getContentSharingPolicy.
	 * 
	 * @param policyId
	 * @return policy
	 */
	public VraNgContentSharingPolicy getContentSharingPolicy(final String policyId) {
		try {
			return this.getContentSharingPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching content sharing policy", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
