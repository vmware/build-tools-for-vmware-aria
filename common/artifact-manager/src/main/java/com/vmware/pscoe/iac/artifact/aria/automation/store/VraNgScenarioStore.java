/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2025 VMware
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
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgScenario;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vmware.pscoe.iac.artifact.aria.automation.store.VraNgDirs.DIR_SCENARIOS;

public class VraNgScenarioStore extends AbstractVraNgStore {

	/**
	 * @return all scenarios from the server
	 */
	protected List<VraNgScenario> getAllServerContents() {
		return this.restClient.getAllScenarios();
	}

	/**
	 * Deletes the scenario by its id.
	 *
	 * @param resId - id of the scenario
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deleteScenario(resId);
	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return a list of scenarios
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getScenario();
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty.
	 */
	@Override
	protected void exportStoreContent() {
		List<VraNgScenario> scenariosOnServer = this.restClient.getAllScenarios();

		for (VraNgScenario scenario : scenariosOnServer) {
			storeScenarioOnFilesystem(vraNgPackage, scenario);
		}
	}

	/**
	 * Exports all scenario names that match the filter.
	 *
	 * @param scenarioNames - filter
	 */
	@Override
	protected void exportStoreContent(List<String> scenarioNames) {
		List<VraNgScenario> scenariosOnServer = this.restClient.getAllScenarios();

		Map<String, VraNgScenario> namesToObjsOnServer = scenariosOnServer.stream()
				.collect(Collectors.toMap(scenario -> scenario.getName(),
						scenario -> scenario));

		for (String scenarioName : scenarioNames) {
			// Check the export the content.yaml Scenarios and try to find them on the
			// server
			if (!namesToObjsOnServer.containsKey(scenarioName)) {
				throw new IllegalStateException(
						"Scenario with name [" + scenarioName + "] doesn't exist on the remote");
			}
			VraNgScenario scenario = namesToObjsOnServer.get(scenarioName);
			storeScenarioOnFilesystem(vraNgPackage, scenario);
		}
	}

	/**
	 * Imports all scenarios from the source directory.
	 * 
	 * @param sourceDirectory - directory with the scenarios.
	 */
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", DIR_SCENARIOS);
		File folder = Paths.get(sourceDirectory.getPath(), DIR_SCENARIOS).toFile();
		if (!folder.exists()) {
			logger.info("Scenario Dir not found.");
			return;
		}
		File[] files = this.filterBasedOnConfiguration(folder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (files == null || files.length == 0) {
			logger.info("Could not find any Scenarios.");
			return;
		}
		logger.info("Found scenarios. Importing...");

		final List<VraNgScenario> allScenarios = this.restClient.getAllScenarios();
		for (File file : files) {
			importScenario(file, allScenarios);
		}
	}

	private File storeScenarioOnFilesystem(Package serverPackage, VraNgScenario scenario) {
		File store = new File(serverPackage.getFilesystemPath());
		File scenarioFile = Paths.get(store.getPath(), DIR_SCENARIOS, scenario.getName() + ".json").toFile();
		scenarioFile.getParentFile().mkdirs();

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			final JsonObject scenarioJsonElement = gson.fromJson(new Gson().toJson(scenario), JsonObject.class);

			logger.info("Created file {}", Files.write(Paths.get(scenarioFile.getPath()), gson.toJson(scenarioJsonElement).getBytes(),
					StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to store scenario {} {}", scenario.getName(), scenarioFile.getPath());
			throw new RuntimeException("Unable to store scenario.", e);
		}

		return scenarioFile;
	}

	private void importScenario(File jsonFile, final List<VraNgScenario> allScenarios) {
		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			String scenarioContent = FileUtils.readFileToString(jsonFile, "UTF-8");
			final JsonObject scenarioJsonElement = gson.fromJson(scenarioContent, JsonObject.class);
			// Get the scenario name from the JSON not from the filename
			String scenarioName = scenarioJsonElement.get("scenarioName").getAsString();
			String scenarioId = scenarioJsonElement.get("scenarioId").getAsString();
			logger.info("Trying to importing scenario '{}' with ID {}...", scenarioName, scenarioId);
			scenarioContent = gson.toJson(scenarioJsonElement);
			restClient.importScenario(scenarioName, scenarioContent);
			logger.debug("Scenario '{}' imported successfully.", scenarioName);
		} catch (

		IOException e) {
			throw new RuntimeException("Error reading from file: " + jsonFile.getPath(), e);
		}
	}
}
