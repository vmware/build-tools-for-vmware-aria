/*-
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

import static com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_RESOURCE_ACTIONS;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.aria.automation.store.helpers.VraNgCustomFormSerializer;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgProjectUtil;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.filters.CustomFolderFileFilter;

public class VraNgResourceActionStore extends AbstractVraNgStore {

	/**
	 * Separator for the Resource Type and the Resource Action Name. Used so we can
	 * have unique names even if we have two resource actions of same name with
	 * different types.
	 */
	private static final String RESOURCE_ACTION_SEPARATOR = "__";

	/**
	 * The sourceType string used by vRA NG when a custom form belongs to a
	 * resource action.
	 */
	private static final String RESOURCE_ACTION_FORM_SOURCE_TYPE = "resource.action";

	/**
	 * Get all server contents.
	 *
	 * @return list of resource actions
	 */
	protected List<VraNgResourceAction> getAllServerContents() {
		return this.restClient.getAllResourceActions().values().stream().collect(Collectors.toList());
	}

	/**
	 * The deleteResourceAction takes in name just for logging purposes.
	 *
	 * @param resId resource id
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deleteResourceAction(resId, resId);
	}

	/**
	 * Import Content.
	 *
	 * @param sourceDirectory source directory
	 */
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_RESOURCE_ACTIONS);
		File folder = Paths.get(sourceDirectory.getPath(), DIR_RESOURCE_ACTIONS).toFile();
		if (!folder.exists()) {
			logger.warn("Resource Actions Dir not found.");
			return;
		}
		File[] files = this.filterBasedOnConfiguration(folder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (files == null || files.length == 0) {
			logger.warn("Could not find any resource actions.");
			return;
		}

		logger.info("Found Resource Actions. Importing...");
		for (File file : files) {
			this.importResourceAction(file);
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return list of resource actions
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getResourceAction();
	}

	/**
	 * Exports all resource actions.
	 */
	@Override
	protected void exportStoreContent() {
		Map<String, VraNgResourceAction> resourceActionOnServer = this.restClient.getAllResourceActions();

		for (String resourceActionId : resourceActionOnServer.keySet()) {
			storeResourceActionOnFilesystem(
					vraNgPackage,
					resourceActionOnServer.get(resourceActionId).getName(),
					resourceActionOnServer.get(resourceActionId).getJson());
		}
	}

	/**
	 * Exports all resource actions that match the filter passed.
	 *
	 * @param resourceActionsToExport filtered list of resource actions to export
	 */
	protected void exportStoreContent(final List<String> resourceActionsToExport) {
		Map<String, VraNgResourceAction> resourceActionOnServer = this.restClient.getAllResourceActions();
		Set<VraNgResourceAction> serverResourceActions = resourceActionOnServer.values().stream()
				.collect(Collectors.toSet());

		resourceActionsToExport.forEach(complexName -> {
			String[] nameParts = complexName.split(RESOURCE_ACTION_SEPARATOR, 2);
			if (nameParts.length != 2) {
				throw new RuntimeException(
						String.format(
								"Incorrect resourceAction name convention. Use: RESOURCE_TYPE__RESOURCE_ACTION_NAME, actual %s",
								complexName));
			}

			String resourceType = nameParts[0];
			String resourceActionName = nameParts[1];

			VraNgResourceAction serverResourceAction = serverResourceActions.stream()
					.filter(ra -> resourceActionName.equals(ra.getName())
							&& resourceType.equals(ra.getResourceType()))
					.findAny()
					.orElse(null);

			if (serverResourceAction == null) {
				throw new IllegalStateException(
						String.format("Resource Action [%s] not found on the server.", resourceActionName));
			}
			storeResourceActionOnFilesystem(
					vraNgPackage,
					complexName,
					serverResourceAction.getJson());
		});
	}

	/**
	 * Sanitize ResourceAction json from unnecessary elements that prevent store or
	 * publish later the content.
	 *
	 * @param resourceActionJsonElement Resource Action Json Element
	 */
	private void sanitizeResourceActionJsonElement(final JsonObject resourceActionJsonElement) {
		// leaving orgId in the JSON prevents pushing to different vRA organizations
		resourceActionJsonElement.remove("orgId");

		logger.debug("Removing id property from formDefinition element ...");
		String formDefinitionItemName = "formDefinition";
		String formDefinitionIdName = "id";
		// When creating a new resource action, formDefinition element must not
		// contain an id property. See IAC-400.
		resourceActionJsonElement.getAsJsonObject(formDefinitionItemName).remove(formDefinitionIdName);
	}

	/**
	 * Save a resource action to a JSON file.
	 *
	 * @param pkg                source package
	 * @param resourceActionName source resource action name
	 * @param resourceActionJson source resource action json
	 * @return Resource Action File
	 */
	private File storeResourceActionOnFilesystem(final Package pkg, final String resourceActionName,
			final String resourceActionJson) {
		File store = new File(pkg.getFilesystemPath());
		File resourceAction = Paths.get(store.getPath(), DIR_RESOURCE_ACTIONS, resourceActionName + ".json").toFile();
		resourceAction.getParentFile().mkdirs();

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject resourceActionJsonElement = gson.fromJson(resourceActionJson, JsonObject.class);

			this.sanitizeResourceActionJsonElement(resourceActionJsonElement);

			String resourceActionJSON = gson.toJson(resourceActionJsonElement);
			logger.info("Created file {}", Files.write(Paths.get(resourceAction.getPath()),
					resourceActionJSON.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to store resource action {} {}", resourceActionName, resourceAction.getPath());
			throw new RuntimeException("Unable to store resource action.", e);
		}

		return resourceAction;
	}

	/**
	 * Import resource action from a file. After the two-phase import the companion
	 * catalog-item custom form (if present) is applied via
	 * {@link #processResourceActionCustomForm}.
	 *
	 * @param jsonFile file of the resource action
	 */
	private void importResourceAction(final File jsonFile) {
		String resourceActionName = "";
		try {
			resourceActionName = FilenameUtils.removeExtension(jsonFile.getName());
			logger.info("Importing resource action {}...", resourceActionName);
			String resourceActionJson = FileUtils.readFileToString(jsonFile, "UTF-8");

			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject resourceActionJsonElement = gson.fromJson(resourceActionJson, JsonObject.class);

			this.sanitizeResourceActionJsonElement(resourceActionJsonElement);

			this.populateVroEndpoint(resourceActionJsonElement);

			VraNgProjectUtil.changeProjectIdBetweenOrganizations(this.restClient, resourceActionJsonElement,
					"projectId");

			// Get resource action id and try to delete existing one
			String resourceActionId = resourceActionJsonElement.get("id").getAsString();
			try {
				logger.info("Deleting resource action '{}' ('{}') if exists ...", resourceActionName, resourceActionId);
				restClient.deleteResourceAction(resourceActionName, resourceActionId);
			} catch (RuntimeException e) {
				logger.error("Delete resource action '{}' ('{}') failed. Error: {}", resourceActionName,
						resourceActionId, e);
			}

			VraNgCustomFormSerializer.serialize(resourceActionJsonElement);
			resourceActionJson = gson.toJson(resourceActionJsonElement);

			// Phase 1: create the resource action → server returns the assigned form id
			String resultResourceActionJson = restClient.importResourceAction(resourceActionName, resourceActionJson);
			JsonObject resultJsonObject = updateFormInfoOnTopOfResult(
					gson.fromJson(resultResourceActionJson, JsonObject.class), resourceActionJsonElement);

			// Phase 2: re-import with the correct form id wired in
			restClient.importResourceAction(resourceActionName, gson.toJson(resultJsonObject));

			// Apply catalog-item-level custom form if one exists alongside this action
			File sourceDirectory = jsonFile.getParentFile().getParentFile();
			String resultResourceActionId = resultJsonObject.get("id").getAsString();
			this.processResourceActionCustomForm(sourceDirectory, resourceActionName, resultResourceActionId);

		} catch (ConfigurationException e) {
			logger.error("Error importing resource action {}...", resourceActionName);
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
		} catch (RuntimeException e) {
			throw new RuntimeException("Error executing POST to server: ", e);
		}
	}

	/**
	 * Populate Vro Endpoint.
	 *
	 * @param resourceActionJsonElement the resource action JSON element
	 */
	private void populateVroEndpoint(final JsonObject resourceActionJsonElement) throws ConfigurationException {
		String runnableItemName = "runnableItem";
		String endpointLinkName = "endpointLink";

		// remove endpointLink from the runnable item, it will be populated automatically
		resourceActionJsonElement.getAsJsonObject(runnableItemName).remove(endpointLinkName);

		// add the updated endpointLink fetched from the target environment/configuration
		resourceActionJsonElement.getAsJsonObject(runnableItemName).addProperty(endpointLinkName,
				this.getVroTargetIntegrationEndpointLink());
	}

	/**
	 * Update Form Info On Top Of Result.
	 *
	 * @param resultJsonObject the JSON returned by the first import call
	 * @param sourceJsonObject the local JSON that was sent
	 * @return updated Json Object
	 */
	private JsonObject updateFormInfoOnTopOfResult(final JsonObject resultJsonObject,
			final JsonObject sourceJsonObject) {

		String newFormId = resultJsonObject.getAsJsonObject("formDefinition").getAsJsonPrimitive("id").getAsString();
		JsonObject sourceForm = sourceJsonObject.getAsJsonObject("formDefinition");
		sourceForm.addProperty("id", newFormId);
		resultJsonObject.add("formDefinition", sourceForm);
		return resultJsonObject;
	}

	/**
	 * Mirrors the blueprint companion-form detection pattern for resource actions.
	 * <p>
	 * Looks for {@code catalog-items/custom-forms/<resourceActionName>__FormData.json}
	 * (and an optional {@code __FormStyles.css}) relative to the package root.
	 * If the file does not exist, any orphaned server form is logged but left
	 * unchanged. If the file exists and differs from the server form, the updated
	 * form is applied via {@link com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg#importCustomForm}.
	 * </p>
	 *
	 * @param sourceDirectory    the package root directory (parent of resource-actions/)
	 * @param resourceActionName the resource action name, without the .json extension
	 * @param resourceActionId   the resource action id returned after import
	 */
	private void processResourceActionCustomForm(final File sourceDirectory, final String resourceActionName,
			final String resourceActionId) {

		File customFormsFolder = Paths.get(sourceDirectory.getPath(), "catalog-items", "custom-forms").toFile();
		File customFormDataFile = new File(customFormsFolder, resourceActionName + "__FormData.json");
		File customFormStylesFile = new File(customFormsFolder, resourceActionName + "__FormStyles.css");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		// Step 1: If no local form file exists, check whether an orphaned one is
		//         still on the server and log a notice.
		if (!customFormDataFile.exists()) {
			try {
				VraNgCustomForm serverForm = this.restClient.getCustomFormByTypeAndSource(
						RESOURCE_ACTION_FORM_SOURCE_TYPE, resourceActionId);
				if (serverForm != null) {
					logger.info(
							"Orphaned catalog-item custom form detected on server for resource action '{}'. "
									+ "No local form file found; server form remains unchanged.",
							resourceActionName);
				}
			} catch (Exception e) {
				logger.debug(
						"No catalog-item custom form on server for resource action '{}'. Skipping.",
						resourceActionName);
			}
			return;
		}

		// Step 2: Read local form, compare with server, apply if different.
		try {
			String localFormContent = new String(Files.readAllBytes(customFormDataFile.toPath()),
					StandardCharsets.UTF_8);
			JsonElement localFormJson = JsonParser.parseString(localFormContent);
			String localFormNormalized = gson.toJson(localFormJson);

			String localCssContent = "";
			if (customFormStylesFile.exists()) {
				localCssContent = new String(Files.readAllBytes(customFormStylesFile.toPath()),
						StandardCharsets.UTF_8).trim();
			}

			boolean formChanged = true;
			try {
				VraNgCustomForm serverForm = this.restClient.getCustomFormByTypeAndSource(
						RESOURCE_ACTION_FORM_SOURCE_TYPE, resourceActionId);
				if (serverForm != null) {
					String serverFormJsonNormalized = "";
					String serverStyles = "";

					if (serverForm.getForm() != null) {
						serverFormJsonNormalized = gson.toJson(JsonParser.parseString(serverForm.getForm()));
					}
					if (serverForm.getStyles() != null) {
						serverStyles = serverForm.getStyles().trim();
					}

					if (localFormNormalized.equals(serverFormJsonNormalized)
							&& localCssContent.equals(serverStyles)) {
						logger.debug(
								"Catalog-item custom form for resource action '{}' matches server. Skipping update.",
								resourceActionName);
						formChanged = false;
					}
				}
			} catch (Exception fe) {
				logger.info(
						"No catalog-item custom form on server for resource action '{}'. Treating as new.",
						resourceActionName);
			}

			if (formChanged) {
				logger.info("Applying catalog-item custom form for resource action '{}'.", resourceActionName);
				VraNgCustomForm customForm = new VraNgCustomForm(
						null,
						resourceActionName,
						localFormContent,
						localCssContent.isEmpty() ? null : localCssContent,
						resourceActionId,
						RESOURCE_ACTION_FORM_SOURCE_TYPE,
						"requestForm",
						"ON",
						"JSON");
				this.restClient.importCustomForm(customForm, resourceActionId);
			}

		} catch (Exception e) {
			logger.error("Failed to process catalog-item custom form for resource action '{}': {}",
					resourceActionName, e.getMessage());
		}
	}
}
