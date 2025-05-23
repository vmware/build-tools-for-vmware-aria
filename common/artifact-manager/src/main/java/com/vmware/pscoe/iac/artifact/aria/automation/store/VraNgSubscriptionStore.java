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
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgProject;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgSubscription;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_SUBSCRIPTIONS;

public class VraNgSubscriptionStore extends AbstractVraNgStore {

	/**
	 * Contains Id of the current organization.
	 */
	private String currentOrganizationId;

	/**
	 * Contains information about the projects affected by import/export.
	 */
	private List<VraNgProject> projects;

	/**
	 * Contains Id of the main project.
	 */
	private String configProjectId;

	/**
	 * Init method, set initial values for base variables.
	 */
	@Override
	public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
			VraNgPackageDescriptor vraNgPackageDescriptor) {
		super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.currentOrganizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
		this.configProjectId = this.restClient.getProjectId();
		this.projects = this.restClient.getProjects();
	}

	/**
	 * @return all subscriptions from the server
	 */
	protected List<VraNgSubscription> getAllServerContents() {
		return this.restClient.getAllSubscriptions().values().stream().collect(Collectors.toList());
	}

	/**
	 * Deletes the subscription by its id.
	 *
	 * @param resId - id of the subscription
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deleteSubscription(resId);
	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return a list of subscriptions
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getSubscription();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty.
	 */
	@Override
	protected void exportStoreContent() {
		Map<String, VraNgSubscription> subscriptionsOnServer = this.getAllSubscriptions();

		for (String subscriptionId : subscriptionsOnServer.keySet()) {
			storeSubscriptionOnFilesystem(
					vraNgPackage,
					subscriptionsOnServer.get(subscriptionId).getName(),
					subscriptionsOnServer.get(subscriptionId).getJson());
		}
	}

	/**
	 * Exports all subscription names that match the filter.
	 *
	 * @param subscriptionNames - filter
	 */
	@Override
	protected void exportStoreContent(List<String> subscriptionNames) {
		Map<String, VraNgSubscription> subscriptionsOnServer = this.getAllSubscriptions();

		Map<String, String> namesToIdsOnServer = subscriptionsOnServer.keySet().stream()
				.collect(Collectors.toMap(subscriptionId -> subscriptionsOnServer.get(subscriptionId).getName(),
						subscriptionId -> subscriptionId));

		for (String subscriptionName : subscriptionNames) {
			// Check the export the content.yaml Subscriptions and try to find them on the
			// server
			if (!namesToIdsOnServer.containsKey(subscriptionName)) {
				throw new IllegalStateException(
						"Subscription with name [" + subscriptionName + "] doesn't exist on the remote");
			}
			String subscriptionId = namesToIdsOnServer.get(subscriptionName);
			storeSubscriptionOnFilesystem(
					vraNgPackage,
					subscriptionName,
					subscriptionsOnServer.get(subscriptionId).getJson());
		}
	}

	/**
	 * Imports all subscriptions from the source directory.
	 * 
	 * @param sourceDirectory - directory with the subscriptions.
	 */
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_SUBSCRIPTIONS);
		File folder = Paths.get(sourceDirectory.getPath(), DIR_SUBSCRIPTIONS).toFile();
		if (!folder.exists()) {
			logger.info("Subscription Dir not found.");
			return;
		}
		File[] files = this.filterBasedOnConfiguration(folder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (files == null || files.length == 0) {
			logger.info("Could not find any Subscriptions.");
			return;
		}
		logger.info("Found subscriptions. Importing...");

		final Map<String, VraNgSubscription> allSubscriptions = this.getAllSubscriptions();
		for (File file : files) {
			importSubscription(file, allSubscriptions);
		}
	}

	private File storeSubscriptionOnFilesystem(Package serverPackage, String subscriptionName,
			String subscriptionJson) {
		File store = new File(serverPackage.getFilesystemPath());
		File subscription = Paths.get(store.getPath(), DIR_SUBSCRIPTIONS, subscriptionName + ".json").toFile();
		subscription.getParentFile().mkdirs();

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject subscriptionJsonElement = gson.fromJson(subscriptionJson, JsonObject.class);
			// leaving orgId in the JSON prevents pushing to different vRA organizations
			// orgId is optional when importing in vRA, so it can be safely removed
			subscriptionJsonElement.remove("orgId");
			addProjectNamesToStorage(subscriptionJsonElement);
			addRunnableNameToStorage(subscriptionJsonElement);
			subscriptionJson = gson.toJson(subscriptionJsonElement);
			logger.info("Created file {}", Files.write(Paths.get(subscription.getPath()), subscriptionJson.getBytes(),
					StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to store subscription {} {}", subscriptionName, subscription.getPath());
			throw new RuntimeException("Unable to store subscription.", e);
		}

		return subscription;
	}

	private Map<String, VraNgSubscription> getAllSubscriptions() {
		return this.restClient.getAllSubscriptions();
	}

	private void importSubscription(File jsonFile, final Map<String, VraNgSubscription> allSubscriptions) {
		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			String subscriptionContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			final JsonObject subscriptionJsonElement = gson.fromJson(subscriptionContent, JsonObject.class);
			// Get the subscription name from the JSON not from the filename
			String subscriptionName = subscriptionJsonElement.get("name").getAsString();
			String subscriptionId = generateId(subscriptionJsonElement, allSubscriptions);
			subscriptionJsonElement.remove("orgId");
			subscriptionJsonElement.addProperty("id", subscriptionId);
			logger.info("Trying to importing subscription '{}' with ID {}...", subscriptionName, subscriptionId);
			substituteProjects(subscriptionJsonElement);
			addRunnableId(subscriptionJsonElement);
			subscriptionContent = gson.toJson(subscriptionJsonElement);
			restClient.importSubscription(subscriptionName, subscriptionContent);
			logger.debug("Subscription '{}' imported successfully.", subscriptionName);
		} catch (

		IOException e) {
			throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
		}
	}

	private void addRunnableId(JsonObject subscriptionJsonElement) {
		JsonElement runnableTypeElement = subscriptionJsonElement.get("runnableType");
		if (!runnableTypeElement.getAsString().contains("extensibility.abx")) {
			return;
		}
		JsonElement runnableNameElement = subscriptionJsonElement.get("runnableName");
		List<AbxAction> actions = restClient.getAllAbxActions().stream()
				.filter(a -> a.getName().equals(runnableNameElement.getAsString())).collect(Collectors.toList());

		if (actions.size() == 0) {
			throw new RuntimeException("Abx actions with the specified Name can not be found");
		}

		subscriptionJsonElement.remove("runnableName");
		subscriptionJsonElement.addProperty("runnableId", actions.get(0).getId());
	}

	private String generateId(JsonObject subscriptionJsonElement, Map<String, VraNgSubscription> allSubscriptions) {
		String subscriptionId = subscriptionJsonElement.get("id").getAsString();
		String subscriptionName = subscriptionJsonElement.get("name").getAsString();

		if (subscriptionJsonElement.get("id") != null && subscriptionId != null) {
			subscriptionId = subscriptionId.replaceAll("^[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}-", "");
			if (allSubscriptions.get(subscriptionId) == null
					|| !JsonParser.parseString(allSubscriptions.get(subscriptionId).getJson()).getAsJsonObject()
							.get("orgId").getAsString().equals(currentOrganizationId)) {
				subscriptionId = this.currentOrganizationId + "-" + subscriptionId;
				logger.debug(
						"Generating new subscription ID '{}' because subscription exists in different organization.",
						subscriptionId);

			}
		} else {
			subscriptionId = this.currentOrganizationId + "-" + "sub_" + subscriptionName.hashCode();
			logger.debug("Subscription Id is missing. Generate id from organization id and name hashcode: "
					+ subscriptionId);
		}
		return subscriptionId;
	}

	private void addProjectNamesToStorage(JsonObject subscriptionJsonElement) {
		JsonElement constraintElement = subscriptionJsonElement.get("constraints");
		if (constraintElement != null) {
			JsonElement projectElement = constraintElement.getAsJsonObject().get("projectId");
			if (projectElement != null && projectElement.isJsonArray() && projectElement.getAsJsonArray().size() > 0) {
				JsonArray projectNames = new JsonArray(projectElement.getAsJsonArray().size());
				projectElement.getAsJsonArray().forEach(el -> projectNames.add(projectIdToName(el.getAsString())));
				// constraintElement.getAsJsonObject().remove("projectId");
				constraintElement.getAsJsonObject().add("projectNames", projectNames);
			}
		}
	}

	private void addRunnableNameToStorage(JsonObject subscriptionJsonElement) {

		JsonElement runnableTypeElement = subscriptionJsonElement.get("runnableType");
		if (!runnableTypeElement.getAsString().contains("extensibility.abx")) {
			return;
		}

		JsonElement runnableIdElement = subscriptionJsonElement.get("runnableId");
		List<AbxAction> actions = restClient.getAllAbxActions().stream()
				.filter(a -> a.getId().equals(runnableIdElement.getAsString()))
				.collect(Collectors.toList());

		if (actions.size() == 0) {
			throw new RuntimeException("Abx actions with the specified Id can not be found");
		}

		subscriptionJsonElement.remove("runnableId");
		subscriptionJsonElement.addProperty("runnableName", actions.get(0).getName());
	}

	private void substituteProjects(JsonObject content) {
		JsonElement constraintElement = content.get("constraints");
		if (constraintElement != null) {
			JsonObject constraintJsonObject = constraintElement.getAsJsonObject();
			JsonElement projectNamesElement = constraintJsonObject.get("projectNames");
			JsonElement projectIdElement = constraintJsonObject.get("projectId");

			if (projectNamesElement != null && projectNamesElement.getAsJsonArray().size() > 0) {
				constraintJsonObject.remove("projectNames");
				if (projectIdElement != null) {
					constraintJsonObject.remove("projectId");
				}

				List<String> projectNames = new ArrayList<>();
				projectNames.add(projectIdToName(this.configProjectId));
				JsonArray newProjectIdElements = new JsonArray();
				newProjectIdElements.add(configProjectId);
				projectNamesElement.getAsJsonArray().forEach(el -> projectNames.add(el.getAsString()));
				projectNames.stream().distinct().map(this::projectNameToId).filter(Objects::nonNull)
						.forEach(id -> newProjectIdElements.add(id));

				constraintJsonObject.add("projectId", newProjectIdElements);
			} else if (projectIdElement != null && projectIdElement.isJsonArray()
					&& projectIdElement.getAsJsonArray().size() > 0) {
				JsonArray newProjectIdElements = new JsonArray();
				List<String> projectIds = new ArrayList<>();
				projectIds.add(this.configProjectId);
				projectIdElement.getAsJsonArray().forEach(el -> projectIds.add(el.getAsString()));
				projectIds.stream().distinct().filter(this::isProjectIdPresent)
						.forEach(id -> newProjectIdElements.add(id));
				constraintJsonObject.remove("projectId");
				constraintJsonObject.add("projectId", newProjectIdElements);
			}
		}
	}

	private String projectIdToName(String projectId) {
		return projects.stream().filter(prj -> prj.getId().equals(projectId)).map(prj -> prj.getName()).findFirst()
				.orElse(null);
	}

	private Boolean isProjectIdPresent(String projectId) {
		return projects.stream().anyMatch(prj -> prj.getId().equals(projectId));
	}

	private String projectNameToId(String name) {
		return projects.stream().filter(prj -> prj.getName().equals(name)).map(prj -> prj.getId()).findFirst()
				.orElse(null);
	}
}
