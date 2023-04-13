/** 
 * Package
 */
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_RESOURCE_ACTIONS;

public class VraNgResourceActionStore extends AbstractVraNgStore {
		
	/**
	 * Separator for the Resource Type and the Resource Action Name. Used so we can have unique names even if we have
	 * 	two resource actions of same name with different types. You can have a resource action with __ in the name
	 * 	but not Source name with it.
	 */
	private static final String RESOURCE_ACTION_SEPARATOR	= "__";

    /**
     * Import Content.
     * @param sourceDirectory source directory
    */
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_RESOURCE_ACTIONS);
		File folder = Paths.get(sourceDirectory.getPath(), DIR_RESOURCE_ACTIONS).toFile();
		if (!folder.exists()) {
			logger.warn("Resource Actions Dir not found.");
			return;
		}
		File[] files = this.filterBasedOnConfiguration(folder, new CustomFolderFileFilter(this.getItemListFromDescriptor()));
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
		Map<String, VraNgResourceAction> resourceActionOnServer	= this.restClient.getAllResourceActions();

		for (String resourceActionId : resourceActionOnServer.keySet()) {
			storeResourceActionOnFilesystem(
				vraNgPackage,
				resourceActionOnServer.get(resourceActionId) .getName(),
				resourceActionOnServer.get(resourceActionId).getJson()
			);
		}
	}

	/**
	 * Exports all resource actions that do not match the filter passed.
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
						complexName
					)
				);
			}

			String resourceType		= nameParts[0];
			String resourceActionName	= nameParts[1];

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
     * @param resourceActionJsonElement Resource Action Json Element
     */
    private void sanitizeResourceActionJsonElement(final JsonObject resourceActionJsonElement) {

        // leaving orgId in the JSON prevents pushing to different vRA organizations
        // orgId is optional when importing in vRA, so it can be safely removed
        resourceActionJsonElement.remove("orgId");

        logger.debug("Removing id property from formDefinition element ...");
        String formDefinitionItemName = "formDefinition";
        String formDefinitionIdName = "id";
        // When create new resource action, formDefinition element do not have to
        // contain id property. See IAC-400.
        resourceActionJsonElement.getAsJsonObject(formDefinitionItemName).remove(formDefinitionIdName);
    }

    /**
     * Save a resource action to a JSON file.
     * 
     * @param pkg                source package
     * @param resourceActionName source resource action name
     * @param resourceActionJson source resource action json
     * @return Resoruce Action File
     */
    private File storeResourceActionOnFilesystem(final Package pkg, final String resourceActionName, final String resourceActionJson) {
        File store = new File(pkg.getFilesystemPath());
        File resourceAction = Paths.get(store.getPath(), DIR_RESOURCE_ACTIONS, resourceActionName + ".json").toFile();
        resourceAction.getParentFile().mkdirs();

        try {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            final JsonObject resourceActionJsonElement = gson.fromJson(resourceActionJson, JsonObject.class);

            this.sanitizeResourceActionJsonElement(resourceActionJsonElement);

            String resourceActionJSON = gson.toJson(resourceActionJsonElement);
            logger.info("Created file {}", Files.write(Paths.get(resourceAction.getPath()),
                    resourceActionJSON.getBytes(), StandardOpenOption.CREATE));
        } catch (IOException e) {
            logger.error("Unable to store resource action {} {}", resourceActionName, resourceAction.getPath());
            throw new RuntimeException("Unable to store resource action.", e);
        }

        return resourceAction;
    }

    /**
     * Import resource actions from a file.
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

            // Get resource action id property and use it to try to delete existing one
            // resource action
            String resourceActionId = resourceActionJsonElement.get("id").getAsString();
            resourceActionJson = gson.toJson(resourceActionJsonElement);
            // Let's try to delete resource action first before import it.
            try {
                logger.info("Deleting resource action '{}' ('{}') if exists ...", resourceActionName, resourceActionId);
                restClient.deleteResourceAction(resourceActionName, resourceActionId);
            } catch (RuntimeException e) {
                logger.error("Delete resource action '{}' ('{}') failed. Error: {}", resourceActionName,
                        resourceActionId, e);
            }

            String resultResourceActionJson = restClient.importResourceAction(resourceActionName, resourceActionJson);
            JsonObject resultJsonObject = updateFormInfoOnTopOfResult(
                    gson.fromJson(resultResourceActionJson, JsonObject.class), resourceActionJsonElement);
            restClient.importResourceAction(resourceActionName, gson.toJson(resultJsonObject));

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
     * @param resourceActionJsonElement file of the resource action
     */
    private void populateVroEndpoint(final JsonObject resourceActionJsonElement) throws ConfigurationException {
        String runnableItemName = "runnableItem";
        String endpointLinkName = "endpointLink";

        // remove endpointLink from the runnable item, it will be populated automaticaly
        resourceActionJsonElement.getAsJsonObject(runnableItemName).remove(endpointLinkName);

        // add the updated endpointLink fetched from the target
        // environment/configuration
        resourceActionJsonElement.getAsJsonObject(runnableItemName).addProperty(endpointLinkName,
                this.getVroTargetIntegrationEndpointLink());
    }

    /**
     * Update Form Info On Top Of Result.
     * 
     * @param resultJsonObject file of the resource action
     * @param sourceJsonObject file of the resource action
     * @return Json Object
     */
    private JsonObject updateFormInfoOnTopOfResult(final JsonObject resultJsonObject, final JsonObject sourceJsonObject) {

        String newFormId = resultJsonObject.getAsJsonObject("formDefinition").getAsJsonPrimitive("id").getAsString();
        JsonObject sourceForm = sourceJsonObject.getAsJsonObject("formDefinition");
        sourceForm.addProperty("id", newFormId);
        resultJsonObject.add("formDefinition", sourceForm);
        return resultJsonObject;
    }
}
