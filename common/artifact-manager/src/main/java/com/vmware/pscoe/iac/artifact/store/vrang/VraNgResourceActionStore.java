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
import com.vmware.pscoe.iac.artifact.model.vrang.*;
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

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.*;

public class VraNgResourceActionStore extends AbstractVraNgStore {

	public void importContent(File sourceDirectory) {
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
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of resource actions
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getResourceAction();
	}

	/**
	 * Exports all resource actions
	 */
	@Override
	protected void exportStoreContent() {
		Map<String, VraNgResourceAction> resourceActionOnServer	= this.restClient.getAllResourceActions();

		for ( String resourceActionId : resourceActionOnServer.keySet() ) {
			storeResourceActionOnFilesystem(
				vraNgPackage,
				resourceActionOnServer.get( resourceActionId) .getName(),
				resourceActionOnServer.get( resourceActionId ).getJson()
			);
		}
	}

	/**
	 * Exports all resource actions that do not match the filter passed
	 * 
	 * @param resourceActionsToExport filtered list of resource actions to export
	 */
	protected void exportStoreContent(List<String> resourceActionsToExport) {
		Map<String, VraNgResourceAction> resourceActionOnServer = this.restClient.getAllResourceActions();
		Set<VraNgResourceAction> serverResourceActions = resourceActionOnServer.values().stream()
				.collect(Collectors.toSet());

		resourceActionsToExport.forEach(resourceActionName -> {
			VraNgResourceAction serverResourceAction = serverResourceActions.stream()
					.filter(ra -> resourceActionName.equals(ra.getName()))
					.findAny()
					.orElse(null);
			if (serverResourceAction == null) {
				throw new IllegalStateException(
						String.format("Resource Action [%s] not found on the server.", resourceActionName));
			}
			storeResourceActionOnFilesystem(
					vraNgPackage,
					resourceActionName,
					serverResourceAction.getJson());
		});
	}

    /**
     * Sanitize ResourceAction json from unnecessary elements that prevent store or
     * publish later the content.
     */
    private void sanitizeResourceActionJsonElement(JsonObject resourceActionJsonElement) {

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
     * Save a resource action to a JSON file
     * 
     * @param pkg                source package
     * @param resourceActionName source resource action name
     * @param resourceActionJson source resource action json
     */
    private File storeResourceActionOnFilesystem(Package pkg, String resourceActionName, String resourceActionJson) {
        File store = new File(pkg.getFilesystemPath());
        File resourceAction = Paths.get(store.getPath(), DIR_RESOURCE_ACTIONS, resourceActionName + ".json").toFile();
        resourceAction.getParentFile().mkdirs();

        try {
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
            final JsonObject resourceActionJsonElement = gson.fromJson(resourceActionJson, JsonObject.class);

            this.sanitizeResourceActionJsonElement(resourceActionJsonElement);

            resourceActionJson = gson.toJson(resourceActionJsonElement);
            logger.info("Created file {}", Files.write(Paths.get(resourceAction.getPath()),
                    resourceActionJson.getBytes(), StandardOpenOption.CREATE));
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
    private void importResourceAction(File jsonFile) {
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

    private JsonObject updateFormInfoOnTopOfResult(JsonObject resultJsonObject, JsonObject sourceJsonObject) {

        String newFormId = resultJsonObject.getAsJsonObject("formDefinition").getAsJsonPrimitive("id").getAsString();
        JsonObject sourceForm = sourceJsonObject.getAsJsonObject("formDefinition");
        sourceForm.addProperty("id", newFormId);
        resultJsonObject.add("formDefinition", sourceForm);
        return resultJsonObject;
    }
}
