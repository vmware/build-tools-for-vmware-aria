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
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.model.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;

public class RestClientVraNg extends RestClientVraNgPrimitive {
	private final Logger logger = LoggerFactory.getLogger(RestClientVraNg.class);

	protected RestClientVraNg(ConfigurationVraNg configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	// =================================================
	// ICON OPERATIONS
	// =================================================

	public ResponseEntity<byte[]> downloadIcon( String iconId ) {
		try {
			return downloadIconPrimitive( iconId );
		} catch ( Exception e ) {
			throw new RuntimeException( String.format( "Could not fetch icon with id '%s'.", iconId ), e );
		}
	}

	public ResponseEntity<String> uploadIcon( File iconFile ) {
		try {
			return uploadIconPrimitive( iconFile );
		} catch ( Exception e ) {
			throw new RuntimeException( String.format( "Could not upload icon file '%s'.", iconFile.getAbsolutePath() ), e );
		}
	}

	public ResponseEntity<String> patchCatalogItemIcon( VraNgCatalogItem catalogItem, String iconId ) {
		try {
			return patchCatalogItemIconPrimitive( catalogItem, iconId );
		} catch ( Exception e ) {
			throw new RuntimeException( "Could not patch icon for catalogItem", e );
		}
	}

	// =================================================
	// BLUEPRINT OPERATIONS
	// =================================================

	public String createBlueprint(VraNgBlueprint blueprint) {
		try {
			return createBlueprintPrimitive(blueprint);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Blueprint with name '%s'.", blueprint.getName()), e);
		}
	}

	public String updateBlueprint(VraNgBlueprint blueprint) {
		try {
			return updateBlueprintPrimitive(blueprint);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Blueprint with name '%s'.", blueprint.getName()), e);
		}
	}

	public List<VraNgBlueprint> getAllBlueprints() {
		try {
			return getAllBlueprintsPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not fetch blueprints.", e);
		}
	}

	public String getBlueprintLastUpdatedVersion(String blueprintId) {
		try {
			return this.getBlueprintLastUpdatedVersionPrimitive(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch Blueprint last version for id '%s'.", blueprintId), e);
		}
	}

	public String getBlueprintVersionContent(String blueprintId, String version) {
		try {
			return this.getBlueprintVersionContentPrimitive(blueprintId, version);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch Blueprint content with version '%s' and id '%s'.", blueprintId, version), e);
		}
	}

	/**
	 * Fetching the version details for a blueprint using the vRA REST API
	 * @param blueprintId blueprintId
	 * @return version
	 */
	public String getBlueprintVersions(String blueprintId) {
		try {
			return this.getBlueprintVersionsContent(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch Blueprint version for id '%s'.", blueprintId), e);
		}
	}

	public Boolean isBlueprintVersionPresent(String blueprintId, String version) {
		try {
			return this.isBlueprintVersionPresentPrimitive(blueprintId, version);
		} catch (Exception e) {
			throw new RuntimeException(String.format("No Blueprint present for version '%s' and id '%s'.", blueprintId, version), e);
		}
	}

	public boolean isBlueprintReleased(String blueprintId) {
		try {
			return this.isBlueprintReleasedPrimitive(blueprintId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("No Blueprint released with id '%s'.", blueprintId), e);
		}
	}

	public void releaseBlueprintVersion(String blueprintId, String version) {
		try {
			this.releaseBlueprintVersionPrimitive(blueprintId, version);
		} catch (URISyntaxException e) {
			logger.error("Could not release blueprint {}", blueprintId);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Import missing versions into vRA for a specified blueprint
	 * @param blueprintId blueprintId
	 * @param version version
	 */
	public void importBlueprintVersion(String blueprintId, JsonObject version) {
		try {
			// There is no update or remove endpoints, so we're only importing ones that are not present
			if (!this.isBlueprintVersionPresentPrimitive(blueprintId, version.get("id").getAsString())) {
				String changelog = version.has("versionChangeLog") ? version.get("versionChangeLog").getAsString() : "";
				String description = version.has("versionDescription") ? version.get("versionDescription").getAsString() : "";
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

	public void importSubscription(String subscriptionName, String subscriptionJson) {
		try {
			importSubscriptionPrimitive(subscriptionName, subscriptionJson);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not import Subscription with name '%s'.", subscriptionName),
					e);
		}
	}

	public Map<String, VraNgSubscription> getAllSubscriptions() {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE'");
	}

	public Map<String, VraNgSubscription> getSubscriptionsByName(String name) {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE' and name eq '"+name+"'");
	}

	public Map<String, VraNgSubscription> getSubscriptionsByOrgId(String orgId) {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE' and orgId eq '"+orgId+"'");
	}

	public Map<String, VraNgSubscription> getSubscriptionsByOrgIdAndName(String orgId, String name) {
		return getAllSubscriptionsPrimitive("type ne 'SUBSCRIBABLE' and orgId eq '"+orgId+"' and name eq '"+name+"'");
	}

	// =================================================
	// CLOUD ACCOUNT OPERATIONS
	// =================================================

	public List<VraNgCloudAccount> getCloudAccounts() {
		try {
			return getAllCloudAccounts();
		} catch (Exception e) {
			throw new RuntimeException("Could not get cloud accounts", e);
		}
	}

	public VraNgCloudAccount getCloudAccount(String id) {
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
	 * Retrieve Secret by name (name is unique for secrets)
	 * @param name of the secret 
	 * @return VraNgSecret item
	 */
	public VraNgSecret getSecret(String name) {
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

	public VraNgRegion getRegion(String id) {
		try {
			return getRegionPrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get region with id '%s'.", id), e);
		}
	}

	// =================================================
	// FLAVOR PROFILES OPERATIONS
	// =================================================

	public Map<String, List<VraNgFlavorMapping>> getAllFlavorMappingsByRegion() {
		try {
			return getAllFlavorMappingsByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all flavour mappings by region", e);
		}
	}

	public Map<String, List<String>> getAllFlavorProfilesByRegion() {
		try {
			return getAllFlavorProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all flavour profiles by region.", e);
		}
	}

	public void createFlavor(String regionId, String flavorProfileName, List<VraNgFlavorMapping> flavorMappings) {
		try {
			createFlavorPrimitive(regionId, flavorProfileName, flavorMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Flavor with name '%s'.", flavorProfileName), e);
		}
	}

	public void updateFlavor(String flavorId, List<VraNgFlavorMapping> flavorMappings) {
		try {
			updateFlavorPrimitive(flavorId, flavorMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Flavor Profile with id '%s'.", flavorId), e);
		}
	}

	// =================================================
	// IMAGE PROFILES OPERATIONS
	// =================================================

	public Map<String, List<VraNgImageMapping>> getAllImageMappingsByRegion() {
		try {
			return getAllImageMappingsByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all image mappings by region.", e);
		}
	}

	public Map<String, List<String>> getAllImageProfilesByRegion() {
		try {
			return getAllImageProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all image profiles by region.", e);
		}
	}

	public void createImageProfile(String regionId, String profileName, List<VraNgImageMapping> imageMappings) {
		try {
			createImageProfilePrimitive(regionId, profileName, imageMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Image Profile with name '%s'.", profileName), e);
		}
	}

	public void updateImageProfile(String profileId, List<VraNgImageMapping> imageMappings) {
		try {
			updateImageProfilePrimitive(profileId, imageMappings);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Image Profile with id '%s'.", profileId), e);
		}
	}

	// =================================================
	// STORAGE PROFILES OPERATIONS
	// =================================================

	public Map<String, List<VraNgStorageProfile>> getAllStorageProfilesByRegion() {
		try {
			return getAllStorageProfilesByRegionPrimitive();
		} catch (Exception e) {
			throw new RuntimeException("Could not get all storage profiles by region.", e);
		}
	}

	public void updateStorageProfile(String profileId, VraNgStorageProfile profile) {
		try {
			updateStorageProfilePrimitive(profileId, profile);
		} catch (Exception e) {
			logger.debug("Could not update Storage Profile with name '{}' and id '{}' using payload\n{}", profile.getName(), profileId, profile.getJson());
			throw new RuntimeException(String.format("Could not update Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	public String createStorageProfile(VraNgStorageProfile profile) {
		try {
			return createStorageProfilePrimitive(profile);
		} catch (Exception e) {
			logger.debug("Could not create Storage Profile with name '{}' using payload\n{}", profile.getName(), profile.getJson());
			throw new RuntimeException(String.format("Could not create Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	public VraNgStorageProfile getSpecificStorageProfile(String targetPool, String profileId) {
		try {
			return getSpecificProfilePrimitive(targetPool, profileId);
		} catch (Exception e) {
			logger.debug("Could not get Storage Profile with id '{}'", profileId);
			throw new RuntimeException(String.format("Could not get Storage Profile with id '%s'.", profileId), e);
		}
	}

	public void updateSpecificProfile(String patchTarget, String profileId, VraNgStorageProfile profile) {
		try {
			updateSpecificProfilePrimitive(patchTarget, profileId, profile);
		} catch (Exception e) {
			logger.debug("Could not update Storage Profile with name '{}' and id '{}' using payload\n{}", profile.getName(), profileId, profile.getJson());
			throw new RuntimeException(String.format("Could not update Storage Profile with name '%s'.", profile.getName()), e);
		}
	}

	public String getFabricEntityName(String fabricUrl) {
		try {
			return getFabricEntityNamePrimitive(fabricUrl);
		} catch (Exception e) {
			logger.debug("Could not get name from fabric '{}'", fabricUrl);
			throw new RuntimeException(String.format("Could not get name from fabric '%s'.", fabricUrl), e);
		}
	}

	public String getFabricEntityId(String fabricType, String fabricName) {
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
	 * NOTE: Blueprint name is always going to be the catalog item name, method name seems misleading
	 *
	 * @param	blueprintName bp name
	 *
	 * @return	VraNgCatalogItem
	 */
	public VraNgCatalogItem getCatalogItemByBlueprintName(String blueprintName) {
		try {
			return this.getCatalogItemByBlueprintNamePrimitive(blueprintName);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get catalog item by blueprint name '%s'.", blueprintName), e);
		}
	}

	public List<VraNgCatalogItem> getCatalogItemsForProject(String project) {
		try {
			return this.getCatalogItemsForProjectPrimitive(project);
		} catch (Exception e) {
			logger.error("Error fetching catalog items for project '{}' : {}", project, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<VraNgPropertyGroup> getPropertyGroups() {
		try {
			return this.getAllPropertyGroupsPrimitive();
		} catch ( Exception e ) {
			logger.error( "Error fetching property groups: {}", e.getMessage() );
			throw new RuntimeException( e );
		}
	}

	public void createPropertyGroup( VraNgPropertyGroup propertyGroup ) {
		try {
			this.createPropertyGroupPrimitive( propertyGroup );
		} catch ( Exception e ) {
			logger.error( "Error importing property group {}. Create operation has failed with error: {}", propertyGroup.getName(), e.getMessage() );
			throw new RuntimeException( e );
		}
	}

	public void updatePropertyGroup( VraNgPropertyGroup propertyGroup ) {
		try {
			this.updatePropertyGroupPrimitive( propertyGroup );
		} catch ( Exception e ) {
			logger.error( "Error importing property group {}. Update operation has failed with error: {}", propertyGroup.getName(), e.getMessage() );
			throw new RuntimeException( e );
		}
	}

	public Map<String, List<VraNgCatalogItem>> getCatalogItemsForProjects(List<String> projects) {
		try {
			return this.getCatalogItemsForProjectsPrimitive(projects);
		} catch (Exception e) {
			logger.error("Error fetching catalog items for projects '{}' : {}", projects.stream().collect(Collectors.joining(", ")), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public VraNgContentSourceBase getContentSource(String id) {
		try {
			return this.getContentSourcePrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get content source by id '%s'.", id, e));
		}
	}

	public VraNgWorkflowContentSource getVraWorkflowContentSource(String id) {
		try {
			return this.getVraWorkflowContentSourcePrimitive(id);
		} catch (Exception e) {
			logger.error("Could not find VRA workflow content source {}", id);
			throw new RuntimeException(e.getMessage());
		}
	}

	public VraNgContentSource getBlueprintContentSourceForProject() {
		try {
			return this.getBlueprintContentSourceForProjectPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching blueprint content sources for project: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<VraNgContentSourceBase> getContentSourcesForProject(String project) {
		try {
			return this.getContentSourcesForProjectPrimitive(project);
		} catch (Exception e) {
			logger.error("Error fetching content sources for project '{}': {}", project, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public Map<String, List<VraNgContentSourceBase>> getContentSourcesForProjects(List<String> projects) {
		try {
			return this.getContentSourcesForProjectsPrimitive(projects);
		} catch (Exception e) {
			logger.error("Error fetching content sources for project '{}': {}", projects.stream().collect(Collectors.joining(", ")), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public VraNgCustomForm getCustomFormByTypeAndSource(String type, String sourceId) {
		try {
			return this.getCustomFormByTypeAndSourcePrimitive(type, sourceId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get custom form by type '%s' and source '%s'.", type, sourceId, e));
		}
	}

	public String createOrUpdateContentSource(VraNgContentSourceBase contentSource) {
		try {
			return this.createOrUpdateContentSourcePrimitive(contentSource);
		} catch (Exception e) {
			logger.error("Could not create or update content source {} : {}", contentSource.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public VraNgCustomForm getVraWorkflowCustomForm(String formName) {
		try {
			return this.getVraWorkflowCustomFormPrimitive(formName);
		} catch (Exception e) {
			logger.error("Error fetching VRA workflow custom form {} : {}", formName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public VraNgIntegration getVraWorkflowIntegration(String name) {
		try {
			return this.getVraWorkflowIntegrationPrimitive(name);
		} catch (Exception e) {
			logger.error("Error fetching VRA workflow integration {} : {}", name, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<VraNgIntegration> getVraWorkflowIntegrations() {
		try {
			return this.getVraWorkflowIntegrationsPrimitive();
		} catch (Exception e) {
			logger.error("Error retrieving list of VRA workflow integrations: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void importCustomForm( VraNgCustomForm customForm, String sourceId ) {
		try {
			this.importCustomFormPrimitive( customForm, sourceId );
		} catch ( Exception e ) {
			logger.error( "Could not import custom form {} : {}", customForm.getName(), e.getMessage() );
			throw new RuntimeException( e );
		}
	}

	public List<VraNgCatalogEntitlement> getAllCatalogEntitlements() {
		try {
			return this.getAllCatalogEntitlementsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all catalog entitlements: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void createCatalogEntitlement(VraNgCatalogEntitlement entitlement, String project) {
		try {
			this.createCatalogEntitlementPrimitive(entitlement, project);
		} catch (Exception e) {
			logger.error("Error creating catalog entitlement '{}': {}", entitlement.getName(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<VraNgProject> getProjectsByName(String projectName) {
		try {
			return this.getProjectsPrimitive(projectName);
		} catch (Exception e) {
			logger.error("Error fetching VRA projects with name '{}': {}", projectName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public List<VraNgProject> getProjects() {
		try {
			return this.getProjectsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching VRA projects : {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public String getProjectIdByName(String projectName) {
		try {
			return this.getProjectIdPrimitive(projectName);
		} catch (Exception e) {
			logger.error("Error fetching VRA project id for project name '{}': {}", projectName, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public String getProjectNameById(String projectId) {
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

	public Map<String, VraNgCustomResource> getAllCustomResources() {
		try {
			return getAllCustomResourcesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all custom resources.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void importCustomResource(String customResourceName, String customResourceJson) {
		try {
			importCustomResourcePrimitive(customResourceJson);
		} catch (HttpClientErrorException httpClientErrorException) {
			throw httpClientErrorException;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not import custom resource with name '%s'.", customResourceName), e);
		}
	}

	public void deleteCustomResource(String customResourceName, String customResourceId) {
		try {
			deleteCustomResourcePrimitive(customResourceId);
		} catch (HttpClientErrorException httpClientErrorException) {
			throw httpClientErrorException;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete custom resource with name '%s' (id:%s).", customResourceName, customResourceId), e);
		}
	}

	// =================================================
	// RESOURCE ACTIONS OPERATIONS
	// =================================================

	public Map<String, VraNgResourceAction> getAllResourceActions() {
		try {
			return getAllResourceActionsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all resource actions.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public String importResourceAction(String resourceActionName, String resourceActionJson) {
		try {
			return importResourceActionPrimitive(resourceActionJson);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not import resource action with name '%s'.", resourceActionName),
					e);
		}
	}

	public void deleteResourceAction(String resourceActionName, String resourceActionId) {
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

	public String createAbxAction(AbxAction action) {
		try {
			return createAbxActionPrimitive(action);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create ABX action with name '%s'.", action.getName()), e);
		}
	}

	public String updateAbxAction(String actionId, AbxAction action) {
		try {
			return updateAbxActionPrimitive(actionId, action);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update ABX action with name '%s'.", action.getName()), e);
		}
	}

	public AbxActionVersion getAbxLastUpdatedVersion(AbxAction action) {
		try {
			return getAbxLastUpdatedVersionPrimitive(action.id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get latest version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	public AbxActionVersion createAbxVersion(AbxAction action, String version) {
		try {
			return createAbxVersionPrimitive(action.id, version);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	public AbxActionVersion releaseAbxVersion(AbxAction action, String versionId) {
		try {
			return releaseAbxVersionPrimitive(action.id, versionId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not release version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	public List<AbxAction> getAllAbxActions() {
		try {
			return getAllAbxActionsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching all abx actions.", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve ABX Constant by name (name is unique for the constants)
	 * @param name of the constant
	 * @return AbxConstant item
	 */
	public AbxConstant getAbxConstant(String name) {
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

	public List<String> getContentSharingPolicyIds() {
		try {
			return this.getAllContentSharingPolicyIdsPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching content sharing policy Ids", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void createContentSharingPolicy(VraNgContentSharingPolicy csPolicy) {
		try {
			createContentSharingPolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not create Content Sharing policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	public void updateContentSharingPolicy(VraNgContentSharingPolicy csPolicy) {
		try {
			updateContentSharingPolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not update Content Sharing policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	public VraNgContentSharingPolicy getContentSharingPolicy(String policyId) {
		try {
			return this.getContentSharingPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching content sharing policy", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
