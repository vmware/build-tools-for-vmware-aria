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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_CUSTOM_RESOURCES;

public class VraNgCustomResourceStore extends AbstractVraNgStore {

	/**
	 * Current Organization Id.
	 */
	private String currentOrganizationId;

	/**
	 * @param restClient
	 * @param vraNgPackage
	 * @param config
	 * @param vraNgPackageDescriptor
	 */
	@Override
	public void init(final RestClientVraNg restClient, final Package vraNgPackage, final ConfigurationVraNg config,
			final VraNgPackageDescriptor vraNgPackageDescriptor) {
		super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.currentOrganizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
	}

	/**
	 * @param restClient
	 * @param vraNgPackage
	 * @param config
	 * @param vraNgPackageDescriptor
	 * @param logger
	 */
	@Override
	public void init(final RestClientVraNg restClient, final Package vraNgPackage, final ConfigurationVraNg config,
			final VraNgPackageDescriptor vraNgPackageDescriptor, final Logger logger) {
		super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor, logger);
		this.currentOrganizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
	}

	/**
	 * Gets all the custom resources from the server.
	 *
	 * @return {List} of all custom resources on the server
	 */
	protected List<VraNgCustomResource> getAllServerContents() {
		return this.restClient.getAllCustomResources().values().stream().collect(Collectors.toList());
	}

	/**
	 * Deletes a custom resource by its ID.
	 *
	 * NOTE: The `deleteCustomResource` asks for both the name and the id, however
	 * the name is used only for logging
	 *
	 * @param resId - The resource ID to delete
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deleteCustomResource(resId, resId);
	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return list of custom resources
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return vraNgPackageDescriptor.getCustomResource();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty.
	 */
	@Override
	protected void exportStoreContent() {
		Map<String, VraNgCustomResource> customResourcesOnServer = this.restClient.getAllCustomResources();

		for (String customResourceId : customResourcesOnServer.keySet()) {
			storeCustomResourceOnFilesystem(
					vraNgPackage,
					customResourcesOnServer.get(customResourceId).getName(),
					customResourcesOnServer.get(customResourceId).getJson());
		}
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty.
	 *
	 * @param customResourcesToExport list of custom resources
	 */
	@Override
	protected void exportStoreContent(final List<String> customResourcesToExport) {
		Map<String, VraNgCustomResource> customResourcesOnServer = this.restClient.getAllCustomResources();
		Set<VraNgCustomResource> serverCustomResources = customResourcesOnServer.values().stream()
				.collect(Collectors.toSet());

		customResourcesToExport.forEach(customResourceName -> {
			VraNgCustomResource customResource = serverCustomResources.stream()
					.filter(cr -> customResourceName.equals(cr.getName()))
					.findAny()
					.orElse(null);
			if (customResource == null) {
				throw new IllegalStateException(
						String.format("Custom Resource [%s] not found on the server.", customResourceName));
			}
			storeCustomResourceOnFilesystem(
					vraNgPackage,
					customResourceName,
					customResource.getJson());
		});
	}

	/**
	 * @param sourceDirectory
	 */
	public void importContent(final File sourceDirectory) {
		File customResourcesFolder = Paths.get(sourceDirectory.getPath(), DIR_CUSTOM_RESOURCES).toFile();
		if (!customResourcesFolder.exists()) {
			logger.info("Custom Resource folder is missing '{}' ", VraNgDirs.DIR_CONTENT_SOURCES);
			return;
		}
		// Check if there are any blueprints to import
		File[] localList = this.filterBasedOnConfiguration(customResourcesFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (localList == null || localList.length == 0) {
			logger.info("No Custom Resource available - skip import");
			return;
		}
		for (File cr : localList) {
			this.importCustomResource(cr);
		}
	}

	/**
	 * Save a custom resource to a JSON file.
	 * Organization ID is removed to enable import in different organizations.
	 * Custom Resource ID is removed to prevent unintentional deletition
	 * during future imports of the same custom Resource to different tenant.
	 *
	 * @param vraNgPackage       source package
	 * @param customResourceName source custom resource name
	 * @param customResourceJson source custom resource json
	 * @return custom resource file
	 */
	private File storeCustomResourceOnFilesystem(final Package vraNgPackage, final String customResourceName,
			final String customResourceJson) {
		File store = new File(vraNgPackage.getFilesystemPath());
		File customResource = Paths.get(store.getPath(), DIR_CUSTOM_RESOURCES, customResourceName + ".json").toFile();
		customResource.getParentFile().mkdirs();

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject customResourceJsonElement = gson.fromJson(customResourceJson, JsonObject.class);

			this.fixOrgId(customResourceJsonElement, "orgId");

			customResourceJsonElement.remove("id");
			logger.info("Custom Resource ID is removed. "
					+ "Note that Custom Resource binding relies on Resource Type to "
					+ "prevent unintentional deletitions in multi-tenant environments. "
					+ "ID is a unique identifier for the entire vRA, not dependant on the tenant.");

			final String customResourceJsonString = gson.toJson(customResourceJsonElement);
			logger.info("Created file {}", Files.write(Paths.get(customResource.getPath()),
					customResourceJsonString.getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to store custom resource {} {}", customResourceName, customResource.getPath());
			throw new RuntimeException("Unable to store custom resource.", e);
		}

		return customResource;
	}

	/**
	 * Import custom resource from a file.
	 * In case custom resource with the same resource type already exists in the
	 * organization,
	 * the same is deleted prior the import. Custom resource ID is removed to
	 * prevent the deletition
	 * of existing custom resource with matching ID in another tenant in
	 * multi-tenant environments.
	 *
	 * @param jsonFile file of the resource action
	 */
	private void importCustomResource(final File jsonFile) {
		String customResourceName = FilenameUtils.removeExtension(jsonFile.getName());
		String resourceType = "[UNKNOWN]";
		String existingObjectId = null;
		Boolean couldNotDeleteCustomResource = false;
		try {
			logger.info("Importing custom resource {}...", customResourceName);
			String customResourceJson = FileUtils.readFileToString(jsonFile, "UTF-8");

			Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
			JsonObject customResourceJsonElement = gson.fromJson(customResourceJson, JsonObject.class);

			this.validateCustomResourceDay2ActionName(customResourceJsonElement);
			this.fixCustomResourceDefinition(customResourceJsonElement);
			this.populateVroEndpoints(customResourceJsonElement);

			JsonElement resourceTypeElement = customResourceJsonElement.get("resourceType");
			if (resourceTypeElement != null && resourceTypeElement.isJsonPrimitive()) {
				resourceType = resourceTypeElement.getAsString();
				// Search for already existing element with the given resource type.
				// Note that we will be able to list it and find it only if it have been created
				// by the given organization.
				// If not found, there is still a posibility that one exists under different
				// organization.
				existingObjectId = getIdOfCustomResourceOfType(resourceType);
			}
			// If Custom Resource with the same resource type exists - delete it first.
			// There can only be just one custom resource of a given resource type.
			if (existingObjectId != null) {
				logger.info("Custom resource '{}' already exists and has ID '{}'", customResourceName,
						existingObjectId);
				logger.info("Trying to delete custom resource '{}' first.", customResourceName);
				if (!tryDeleteCustomResource(customResourceName, existingObjectId, customResourceJsonElement)) {
					couldNotDeleteCustomResource = true;
					logger.error("Failed to update Custom Resource '{}'. "
							+ "Could not delete the existing custom resource due to active deployments. ",
							customResourceName);
					logger.warn("Will attempt to update Custom Resource '{}'", customResourceName);
				}
			}

			// Strip the Custom Resource ID. In case a Custom Resource with the same ID
			// exists in another tenant, the one would not be deleted.
			JsonElement idElement = customResourceJsonElement.get("id");
			if (idElement != null && idElement.isJsonPrimitive()) {
				logger.warn("Provided Custom Resource ID is removed before import execution. "
						+ "Custom Resource binding relies on Resource Type instead. "
						+ "This is required due to the risk of unintentional deletition of Custom Resources "
						+ "with matching IDs in multi-tenant environments.");
				customResourceJsonElement.remove("id");
			}

			// Adds the Pre-Fetched ID of the Custom Resource
			// in case if the initial CR deletion failed.
			if (existingObjectId != null && couldNotDeleteCustomResource) {
				customResourceJsonElement.add("id", new JsonPrimitive(existingObjectId));
			}

			customResourceJson = gson.toJson(customResourceJsonElement);
			logger.info("Create/update custom resource '{}'.", customResourceName);
			restClient.importCustomResource(customResourceName, customResourceJson);

		} catch (IOException e) {
			throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
		} catch (HttpClientErrorException clientException) {
			if (isCustomResourceExistingInDifferentOrganization(clientException)) {
				// Do warn the user, but don't fail.
				logger.warn(String.format("Cannot import Custom Resource '%s', because resource of that type (%s"
						+ ") already exists under  different organization. Please note that once a Custom Resource is defined in one organization it globally "
						+ "available in the whole vRA (one can use it in cloud template canvas regardless of organization), still it can be listed, updated or "
						+ "deleted only from the organization it was created in. Please make sure the correct version of Custom Resource is already in place or "
						+ "ask the administrator to delete it and then repeat the import. Original Error Message: %s",
						customResourceName, resourceType, clientException.getMessage()));
				return;
			}

			String message = String.format("Could not import custom resource with name '%s' : %s", customResourceName,
					clientException.getMessage());
			throw new RuntimeException(message, clientException);
		} catch (ConfigurationException e) {
			logger.error("Error importing custom resource {}...", customResourceName);
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			String str = e.getClass().getName() + " : " + e.getMessage();
			throw new RuntimeException("Error executing POST to server: " + str, e);
		}
	}

	/**
	 * Custom Resource Second Day action name validation.
	 *
	 * @param customResourceJsonElement - The CR to validate
	 */
	private void validateCustomResourceDay2ActionName(final JsonObject customResourceJsonElement) {
		JsonArray additionalActionsArray = customResourceJsonElement.get("additionalActions").getAsJsonArray();
		additionalActionsArray.forEach(action -> {
			if (action != null) {
				JsonObject actionJson = action.getAsJsonObject();
				String name = actionJson.get("name").getAsString();
				Pattern pattern = Pattern.compile("[^a-zA-Z0-9:\\-_.]");
				Matcher matcher = pattern.matcher(name);

				if (matcher.find()
						|| name.startsWith("_")
						|| name.endsWith("_")
						|| name.startsWith(".")
						|| name.endsWith(".")
						|| name.contains(" ")) {
					throw new RuntimeException(String.format(
							"Action's name: '%s' must not contain special symbols except . : -"
									+ "_and must not start or end with a dot or '_' and must not contain spaces.",
							name));
				}
			}
		});
	}

	private boolean tryDeleteCustomResource(final String customResourceName, final String existingObjectId,
			final JsonObject customResourceJsonElement) {
		try {
			restClient.deleteCustomResource(customResourceName, existingObjectId);
			customResourceJsonElement.remove("id");
			customResourceJsonElement.add("id", new JsonPrimitive(existingObjectId));
		} catch (Exception ex) {
			if (isCustomResourceActiveAttached(ex)) {
				// Do warn the user, but don't fail.
				logger.debug(
						"Cannot delete Custom Resource '{}', due to active deployments. Original Error Message: {}",
						customResourceName, ex.getMessage());
				return false;
			} else {
				throw ex;
			}
		}
		return true;
	}

	private String getIdOfCustomResourceOfType(final String type) {
		if (type == null) {
			return null;
		}
		Map<String, VraNgCustomResource> allResources = restClient.getAllCustomResources();
		for (String id : allResources.keySet()) {
			VraNgCustomResource customResource = allResources.get(id);
			String json = customResource.getJson();
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject customResourceObject = gson.fromJson(json, JsonObject.class);
			JsonElement resourceTypeElement = customResourceObject.get("resourceType");
			if (resourceTypeElement != null && resourceTypeElement.isJsonPrimitive()
					&& type.equals(resourceTypeElement.getAsString())) {
				return id;
			}
		}
		return null;
	}

	private static boolean isCustomResourceExistingInDifferentOrganization(
			final HttpClientErrorException clientException) {
		HttpStatus status = HttpStatus.valueOf(clientException.getStatusCode().value());
		final String magicMessage = "Custom resource with the same resource type already exists! Use a different resource type name!";
		String message = clientException.getMessage();
		return (status == HttpStatus.BAD_REQUEST && message != null && message.contains(magicMessage));
	}

	/**
	 * Check if the exception is due to active resources attached to the custom
	 * resource.
	 *
	 * The reason why we fetch all the messages is that the exception can be nested,
	 * which it is currently.
	 *
	 * @param clientException - The exception to check
	 * @return true if the exception is due to active resources attached to the
	 *         custom
	 */
	private static boolean isCustomResourceActiveAttached(final Exception clientException) {
		final String magicMessage = "Resource type cannot be deleted as there are active resources attached to it";

		Throwable e = clientException;
		StringBuilder builder = new StringBuilder(e.getMessage()).append("\n");

		Throwable cause = e.getCause();
		while (cause != null) {
			builder.append(cause.getMessage()).append("\n");
			cause = cause.getCause();
		}

		String message = builder.toString();
		return message.contains(magicMessage);
	}

	/**
	 * In vRA versions 8.8.x+ the tenant needs to be present in the formDefinition,
	 * otherwise the import will fail.
	 *
	 * @param customResourceJsonElement - The CR to update
	 */
	private void fixCustomResourceDefinition(final JsonObject customResourceJsonElement) {
		// Remove the organization from the general json object
		this.fixOrgId(customResourceJsonElement, "orgId");

		VraNgProjectUtil.changeProjectIdBetweenOrganizations(this.restClient, customResourceJsonElement, "projectId");

		// Remove foreach additional action the organization id and the
		// formDefinition.id property

		// Replace the tenant property in the formDefinition with the correct one from
		// the configuration
		JsonArray additionalActionsArray = customResourceJsonElement.get("additionalActions").getAsJsonArray();
		additionalActionsArray.forEach(action -> {
			if (action != null) {
				JsonObject actionJson = action.getAsJsonObject();
				this.fixOrgId(actionJson, "orgId");
				if (actionJson.get("formDefinition") != null) {
					JsonObject formDefinition = actionJson.get("formDefinition").getAsJsonObject();
					formDefinition.remove("id");

					this.fixOrgId(formDefinition, "tenant");

					VraNgProjectUtil.changeProjectIdBetweenOrganizations(this.restClient, formDefinition, "projectId");
				}
			}
		});
	}

	/**
	 * Fixes the organization id / tenant id in the given object with the one set in
	 * the configuration.
	 * 
	 * @param jsonObject
	 * @param key
	 */
	private void fixOrgId(final JsonObject jsonObject, final String key) {
		if (jsonObject.has(key)) {
			jsonObject.remove(key);
		}

		jsonObject.addProperty(key, this.currentOrganizationId);
	}

	private void populateVroEndpoints(final JsonObject customResourceJsonElement) throws ConfigurationException {
		String runnableItemName = "runnableItem";
		String endpointLinkName = "endpointLink";
		// endpointLink fetched from the target environment/configuration
		String endpointLink = this.getVroTargetIntegrationEndpointLink();

		JsonObject mainActions = customResourceJsonElement.getAsJsonObject("mainActions");
		JsonElement additionalActions = customResourceJsonElement.get("additionalActions");

		// Iterate through the main actions and update endpointLink
		for (String mainAct : new String[] { "create", "update", "delete" }) {
			if (mainActions.getAsJsonObject(mainAct) != null) {
				mainActions.getAsJsonObject(mainAct).remove(endpointLinkName);
				mainActions.getAsJsonObject(mainAct).addProperty(endpointLinkName, endpointLink);
			}
		}

		// Iterate through the additional actions and update endpointLink
		if (additionalActions != null) {
			additionalActions.getAsJsonArray().forEach(action -> {
				JsonObject act = action.getAsJsonObject();
				if (act != null) {
					act.getAsJsonObject(runnableItemName).remove(endpointLinkName);
					act.getAsJsonObject(runnableItemName).addProperty(endpointLinkName, endpointLink);
				}
			});
		}
	}
}
