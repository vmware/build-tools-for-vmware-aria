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
package com.vmware.pscoe.iac.artifact.aria.automation.rest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogEntitlement;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDay2ActionsPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDeploymentLimitPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgProject;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgRegion;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceQuotaPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgSecret;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgSubscription;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgWorkflowContentSource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;

public class RestClientVraNg extends RestClientVraNgPrimitive {
	private static final String SUBSCRIPTION_BASE_QUERY = "type ne 'SUBSCRIBABLE'";
	private static final String SUBSCRIPTION_QUERY_PARAM = "%s eq '%s'";

	/**
	 * logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(RestClientVraNg.class);

	/**
	 * Constructor for RestClientVraNg.
	 * 
	 * @param configuration configuration vra
	 * @param restTemplate  rest template
	 */
	public RestClientVraNg(final ConfigurationVraNg configuration, final RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	// =================================================
	// ICON OPERATIONS
	// =================================================

	/**
	 * downloadIcon.
	 * 
	 * @param iconId icon id
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
	 * @param iconFile icon file
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
	 * @param catalogItem catalog item
	 * @param iconId      icon id
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
	 * @param blueprint vra blueprint
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
	 * Deletes a blueprint.
	 *
	 * @param bpId blueprint id
	 * @throws RuntimeException if the blueprint could not be deleted
	 */
	public void deleteBlueprint(final String bpId) {
		try {
			logger.info("Deleting blueprint with id '{}'", bpId);
			ResponseEntity<String> res = deleteBlueprintPrimitive(bpId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete blueprint with id '{}'", bpId);
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Blueprint with id '%s'.", bpId));
		}
	}

	/**
	 * Updates the blueprint.
	 * 
	 * @param blueprint vra blueprint
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
	 * @param blueprintId blueprint id
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
	 * @param blueprintId blueprint id
	 * @param version     version
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
	 * @param blueprintId blueprint id
	 * @param version     version
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
	 * @param blueprintId blueprint id
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
	 * @param blueprintId blueprint id
	 * @param version     version
	 */
	public void releaseBlueprintVersion(final String blueprintId, final String version) {
		try {
			this.releaseBlueprintVersionPrimitive(blueprintId, version);
		} catch (URISyntaxException e) {
			logger.error("Could not release blueprint version {}", blueprintId);
			throw new RuntimeException(e);
		}
	}

	/**
	 * releaseBlueprintVersion.
	 *
	 * @param blueprintId blueprint id
	 * @param versionId   version id
	 */
	public void unreleaseBlueprintVersion(final String blueprintId, final String versionId) {
		try {
			this.unreleaseBlueprintVersionPrimitive(blueprintId, versionId);
		} catch (URISyntaxException e) {
			logger.error("Could not unrelease blueprint version {}:{}", blueprintId, versionId);
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
	 * @param subscriptionName subscription name
	 * @param subscriptionJson subscription json
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
		return getAllSubscriptionsPrimitive(SUBSCRIPTION_BASE_QUERY);
	}

	/**
	 * Deletes a subscription.
	 *
	 * @param subscriptionId subscription id
	 */
	public void deleteSubscription(final String subscriptionId) {
		try {
			logger.info("Deleting subscription with id '{}'", subscriptionId);
			ResponseEntity<String> res = deleteSubscriptionPrimitive(subscriptionId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete subscription with id '{}'", subscriptionId);
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Subscription with id '%s'.", subscriptionId), e);
		}
	}

	/**
	 * getSubscriptionsByName.
	 *
	 * @param name subscription name
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByName(final String name) {
		return getAllSubscriptionsPrimitive(
				SUBSCRIPTION_BASE_QUERY + " and " + String.format(SUBSCRIPTION_QUERY_PARAM, "name", name));
	}

	/**
	 * getSubscriptionsByOrgId.
	 *
	 * @param orgId org id
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByOrgId(final String orgId) {
		return getAllSubscriptionsPrimitive(
				SUBSCRIPTION_BASE_QUERY + " and " + String.format(SUBSCRIPTION_QUERY_PARAM, "orgId", orgId));
	}

	/**
	 * getSubscriptionsByOrgIdAndName.
	 *
	 * @param orgId org id
	 * @param name  subscription name
	 * @return subscriptions
	 */
	public Map<String, VraNgSubscription> getSubscriptionsByOrgIdAndName(final String orgId, final String name) {
		String query = SUBSCRIPTION_BASE_QUERY
				+ " and " + String.format(SUBSCRIPTION_QUERY_PARAM, "orgId", orgId)
				+ " and " + String.format(SUBSCRIPTION_QUERY_PARAM, "name", name);
		return getAllSubscriptionsPrimitive(query);
	}

	// =================================================
	// CLOUD ACCOUNT OPERATIONS
	// =================================================

	/**
	 * getCloudAccounts.
	 * 
	 * NOTE: This is not used anymore. It's a leftoever from the deprecation of
	 * regional content.
	 * Leaving it here for now in case it's needed.
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
	 * NOTE: This is not used anymore. It's a leftoever from the deprecation of
	 * regional content.
	 * Leaving it here for now in case it's needed.
	 *
	 * @param id cloud account id
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
	 * @param id cloud region id
	 * @return region
	 */
	public VraNgRegion getRegion(final String id) {
		try {
			return getRegionPrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get region with id '%s'.", id), e);
		}
	}

	/**
	 * getFabricEntityName.
	 *
	 * @param fabricUrl fabric url
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
	 * @param fabricType fabric type
	 * @param fabricName fabric name
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
	 * @param project project
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
	 * Deletes a catalog item.
	 *
	 * @param catalogItemId catalog item id
	 */
	public void deleteCatalogItem(final String catalogItemId) {
		try {
			logger.info("Deleting catalog item with id '{}'", catalogItemId);
			ResponseEntity<String> res = this.deleteCatalogItemPrimitive(catalogItemId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete catalog item with id '{}'", catalogItemId);
			}
		} catch (Exception e) {
			logger.error("Error deleting catalog item '{}': {}", catalogItemId, e.getMessage());
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
	 * Deletes a property group.
	 *
	 * @param propertyGroupId property group id
	 */
	public void deletePropertyGroup(final String propertyGroupId) {
		try {
			logger.info("Deleting property group with id '{}'", propertyGroupId);
			ResponseEntity<String> res = this.deletePropertyGroupPrimitive(propertyGroupId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete property group with id '{}'", propertyGroupId);
			}
		} catch (Exception e) {
			logger.error("Error deleting property group '{}': {}", propertyGroupId, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createPropertyGroup.
	 *
	 * @param propertyGroup vra property group
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
	 * @param propertyGroup vra property group
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
	 * @param projects list of projects
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
	 * @param id content source id
	 * @return contentSourceBase
	 */
	public VraNgContentSourceBase getContentSource(final String id) {
		try {
			return this.getContentSourcePrimitive(id);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not get content source by id '%s', error was: '%s'.", id, e.getMessage()), e);
		}
	}

	/**
	 * getVraWorkflowContentSource.
	 *
	 * @param id content source id
	 * @return workflowContentSource
	 */
	public VraNgWorkflowContentSource getVraWorkflowContentSource(final String id) {
		try {
			return this.getVraWorkflowContentSourcePrimitive(id);
		} catch (Exception e) {
			logger.error("Could not find VRA workflow content source {}", id);
			throw new RuntimeException(e.getMessage(), e);
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
	 * @param project project
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
	 * @param projects list of projects
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
	 * @param type     source type
	 * @param sourceId source id
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
	 * @param catalogItemId catalog item id.
	 * @return catalogItemVersion
	 */
	public JsonArray getCatalogItemVersions(final String catalogItemId) {
		try {
			return this.getCatalogItemVersionsPrimitive(catalogItemId);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not get custom form by id '%s'.", catalogItemId, e));
		}
	}

	/**
	 * fetchRequestForm.
	 *
	 * @param type     source type
	 * @param sourceId source id
	 * @param formId   form id
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
	 * @param contentSource vra content source
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
	 * @param formName form name
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
	 * @param name workflow name
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
	 * @param customForm vra custom form
	 * @param sourceId   source id
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
	 * Deletes a catalog entitlement.
	 *
	 * @param entitlementId catalog entitlement id
	 */
	public void deleteCatalogEntitlement(final String entitlementId) {
		try {
			logger.info("Deleting catalog entitlement with id '{}'", entitlementId);
			ResponseEntity<String> res = this.deleteCatalogEntitlementPrimitive(entitlementId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete catalog entitlement with id '{}'", entitlementId);
			}
		} catch (Exception e) {
			logger.error("Error deleting catalog entitlement '{}': {}", entitlementId, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createCatalogEntitlement.
	 *
	 * @param entitlement catalog entitlement
	 * @param project     project id
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
	 * @param projectName project name
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
	 * @param projectName project name
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
	 * @param projectId project id
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
	 * @param customResourceName custom resource name
	 * @param customResourceJson custom resource json
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
	 * @param customResourceName custom resource name
	 * @param customResourceId   custom resource id
	 */
	public void deleteCustomResource(final String customResourceName, final String customResourceId) {
		try {
			logger.info("Deleting custom resource with id '{}'", customResourceId);
			ResponseEntity<String> res = deleteCustomResourcePrimitive(customResourceId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete custom resource with id '{}'", customResourceId);
			}
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
	 * @param resourceActionName resource action name
	 * @param resourceActionJson resource action json
	 * @return id
	 */
	public String importResourceAction(final String resourceActionName, final String resourceActionJson) {
		try {
			return importResourceActionPrimitive(resourceActionJson);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not import resource action with name '%s'.", resourceActionName), e);
		}
	}

	/**
	 * deleteResourceAction.
	 * 
	 * @param resourceActionName resource action name
	 * @param resourceActionId   resource action id
	 */
	public void deleteResourceAction(final String resourceActionName, final String resourceActionId) {
		try {
			logger.info("Deleting resource action with id '{}'", resourceActionId);
			ResponseEntity<String> res = deleteResourceActionPrimitive(resourceActionId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Failed to delete resource action with id '{}'", resourceActionId);
			}
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
	 * @param action abx action
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
	 * @param actionId action id
	 * @param action   action
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
	 * @param action abx action
	 * @return version
	 */
	public AbxActionVersion getAbxLastUpdatedVersion(final AbxAction action) {
		try {
			return getAbxLastUpdatedVersionPrimitive(action.getId());
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not get latest version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	/**
	 * createAbxVersion.
	 * 
	 * @param action  abx action
	 * @param version version
	 * @return abxVersion
	 */
	public AbxActionVersion createAbxVersion(final AbxAction action, final String version) {
		try {
			return createAbxVersionPrimitive(action.getId(), version);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create version of ABX action with name '%s'.", action.getName()), e);
		}
	}

	/**
	 * releaseAbxVersion.
	 * 
	 * @param action    action
	 * @param versionId version id
	 * @return version
	 */
	public AbxActionVersion releaseAbxVersion(final AbxAction action, final String versionId) {
		try {
			return releaseAbxVersionPrimitive(action.getId(), versionId);
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
	 * Delete policy.
	 *
	 * @param policyId policy id
	 */
	public void deletePolicy(final String policyId) {
		try {
			logger.info("Deleting policy with id '{}'", policyId);
			ResponseEntity<String> res = deletePolicyPrimitive(policyId);

			if (!res.getStatusCode().is2xxSuccessful()) {
				logger.error("Error deleting policy with id '{}'. Response: {}", policyId, res);
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete policy with id '%s'.", policyId), e);
		}
	}

	/**
	 * getContentSharingPolicyIds.
	 * 
	 * @return policies
	 */
	public List<VraNgContentSharingPolicy> getContentSharingPolicies() {
		try {
			return this.getAllContentSharingPoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching content sharing policies", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createContentSharingPolicy.
	 * 
	 * @param csPolicy content sharing policy
	 */
	public void createContentSharingPolicy(final VraNgContentSharingPolicy csPolicy) {
		try {
			this.createContentSharingPolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Content Sharing policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	/**
	 * getContentSharingPolicy.
	 * 
	 * @param policyId content sharing policy id
	 * @return policy
	 */
	public VraNgContentSharingPolicy getContentSharingPolicy(final String policyId) {
		try {
			return this.getContentSharingPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching content sharing policy - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * getResourceQuotaPolicyIds.
	 * 
	 * @return policies
	 */
	public List<VraNgResourceQuotaPolicy> getResourceQuotaPolicies() {
		try {
			return this.getAllResourceQuotaPoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching resource quota policies", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getLeasePolicies.
	 * 
	 * @return lease policies
	 */
	public List<VraNgLeasePolicy> getLeasePolicies() {
		try {
			return this.getAllLeasePoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching lease policies", e.getMessage());

			throw new RuntimeException(e);
		}
	}

	/**
	 * createResourceQuotaPolicy.
	 * 
	 * @param rqPolicy resource quota policy
	 */
	public void createResourceQuotaPolicy(final VraNgResourceQuotaPolicy rqPolicy) {
		try {
			createResourceQuotaPolicyPrimitive(rqPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Resource Quota policy with name '%s'.", rqPolicy.getName()), e);
		}
	}

	/**
	 * createLeasePolicy.
	 * 
	 * @param csPolicy lease policy
	 */
	public void createLeasePolicy(final VraNgLeasePolicy csPolicy) {
		try {
			createLeasePolicyPrimitive(csPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create lease policy with name '%s'.", csPolicy.getName()), e);
		}
	}

	/**
	 * getResourceQuotaPolicy.
	 * 
	 * @param policyId resource quota policy id
	 * @return policy
	 */
	public VraNgResourceQuotaPolicy getResourceQuotaPolicy(final String policyId) {
		try {
			return this.getResourceQuotaPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching resource quota policy - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getLeasePolicy.
	 * 
	 * @param policyId lease policy id
	 * @return policy
	 */
	public VraNgLeasePolicy getLeasePolicy(final String policyId) {
		try {
			return this.getLeasePolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching lease policy - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getDay2ActionsPolicies.
	 *
	 * @return policies
	 */
	public List<VraNgDay2ActionsPolicy> getDay2ActionsPolicies() {
		try {
			return this.getAllDay2ActionsPoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching day 2 actions policies", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createDay2ActionsPolicy.
	 *
	 * @param d2aPolicy day 2 actions policy
	 */
	public void createDay2ActionsPolicy(final VraNgDay2ActionsPolicy d2aPolicy) {
		try {
			createDay2ActionsPolicyPrimitive(d2aPolicy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Day 2 Actions policy with name '%s'.", d2aPolicy.getName()), e);
		}
	}

	/**
	 * getDay2ActionsPolicy.
	 *
	 * @param policyId day 2 actions policy id
	 * @return policy
	 */
	public VraNgDay2ActionsPolicy getDay2ActionsPolicy(final String policyId) {
		try {
			return this.getDay2ActionsPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching resource day 2 actions - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getDeploymentLimitPolicies.
	 *
	 * @return policies
	 */
	public List<VraNgDeploymentLimitPolicy> getDeploymentLimitPolicies() {
		try {
			return this.getAllDeploymentLimitPoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching deployemnt limit policies", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createDeploymentLimitPolicy.
	 *
	 * @param policy day 2 actions policy
	 */
	public void createDeploymentLimitPolicy(final VraNgDeploymentLimitPolicy policy) {
		try {
			createDeploymentLimitPolicyPrimitive(policy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Deployment Limit with name '%s'.", policy.getName()), e);
		}
	}

	/**
	 * getDeploymentLimitPolicy.
	 *
	 * @param policyId day 2 actions policy id
	 * @return policy
	 */
	public VraNgDeploymentLimitPolicy getDeploymentLimitPolicy(final String policyId) {
		try {
			return this.getDeploymentLimitPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching deployment limit policy - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * getApprovalPolicies.
	 *
	 * @return policies
	 */
	public List<VraNgApprovalPolicy> getApprovalPolicies() {
		try {
			return this.getAllApprovalPoliciesPrimitive();
		} catch (Exception e) {
			logger.error("Error fetching Approval policies", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * createApprovalPolicy.
	 *
	 * @param policy policy to be created or updated.
	 */
	public void createApprovalPolicy(final VraNgApprovalPolicy policy) {
		try {
			createApprovalPolicyPrimitive(policy);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Could not create Approval policy with name '%s'.", policy.getName()), e);
		}
	}

	/**
	 * getApprovalPolicy.
	 *
	 * @param policyId policy id
	 * @return policy
	 */
	public VraNgApprovalPolicy getApprovalPolicy(final String policyId) {
		try {
			return this.getApprovalPolicyPrimitive(policyId);
		} catch (Exception e) {
			logger.error("Error fetching approval policy - {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
