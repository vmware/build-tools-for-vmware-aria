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

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.VraNgReleaseManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFolderFilter;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VraNgBlueprintStore extends AbstractVraNgStore {

	/* ============================
	 * Static properties
	 ============================ */

	/**
	 * Files representing the blueprint definition and it's versions details
	 */
	private static final String BP_DETAILS_FILE_NAME = "details.json";
	private static final String BP_CONTENT_FILE_NAME = "content.yaml";
	private static final String BP_VERSIONS_FILE_NAME = "versions.json";

	/* ============================
	 * Publicly available interface
	 ============================ */

	/**
	 * Importing content into vRA target environment
	 *
	 * @param sourceDirectory target path
	 */
	public void importContent(File sourceDirectory) {
		// Collect available blueprint definitions
		File bpFolder = new File(Paths.get(sourceDirectory.getPath(), DIR_BLUEPRINTS).toString());
		if (!bpFolder.exists()) {
			logger.info("No blueprints available - skip import");
			return;
		}

		// Check if there are any blueprints to import
		File[] localBpList = this.filterBasedOnConfiguration(bpFolder, new CustomFolderFolderFilter(this.getItemListFromDescriptor()));

		if (localBpList == null || localBpList.length == 0) {
			logger.info("No blueprints available - skip import");
			return;
		}

		Map<String, VraNgBlueprint> bpsOnServerByName = this.getAllBlueprints().stream()
				.collect(Collectors.toMap(VraNgBlueprint::getName, item -> item));

		// Iterating blueprints by folder
		for (File bpDir : localBpList) {
			this.handleBlueprintImport(bpDir, bpsOnServerByName);
		}
	}

	/* ==============
	 * Export logic
	 ============== */

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of bps
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getBlueprint();
	}

	/**
	 * Fetching all blueprints and stores them on the filesystem
	 */
	@Override
	protected void exportStoreContent() {
		Map<String, VraNgBlueprint> blueprintsOnServer = this.fetchBlueprintsOnServer(new HashSet<String>());

		for (String blueprintName : blueprintsOnServer.keySet()) {
			storeBlueprintsOnFilesystem(vraNgPackage, blueprintsOnServer.get(blueprintName));
		}
	}

	/**
	 * Fetches filtered blueprints and stores them on the filesystem
	 *
	 * @param blueprintNames list of bp names
	 */
	@Override
	protected void exportStoreContent(List<String> blueprintNames) {
		Set<String> blueprintsBucket = new HashSet<String>(blueprintNames);
		Map<String, VraNgBlueprint> blueprintsOnServer = this.fetchBlueprintsOnServer(blueprintsBucket);

		Set<String> blueprintNamesOnServer = blueprintsOnServer.keySet();
		for (String blueprintName : blueprintNames) {
			// Check the export the content.yaml BPs and try to find them on the server
			if (!blueprintNamesOnServer.contains(blueprintName)) {
				throw new IllegalStateException("Blueprints with name [" + blueprintName + "] doesn't exist on the remote");
			}
			storeBlueprintsOnFilesystem(vraNgPackage, blueprintsOnServer.get(blueprintName));
		}
	}

	/**
	 * Fetches blueprints from the server.
	 * Checks if there are duplicated blueprints in vRA and ignores them.
	 * If there are blueprintNames provided, they will be used to check for duplicates as well
	 *
	 * @param blueprintsBucket - Set of blueprint names
	 * @return Map<String, VraNgBlueprint>
	 */
	private Map<String, VraNgBlueprint> fetchBlueprintsOnServer(Set<String> blueprintsBucket) {
		// Check if there are duplicates in project
		// If content.yaml has blueprints contained multiple times in project, error is thrown
		// Otherwise duplicates are reported without errors
		Map<String, VraNgBlueprint> blueprintsOnServer = new HashMap<>();

		this.getAllBlueprints().forEach(bp -> {
			if (blueprintsOnServer.containsKey(bp.getName())) {
				if (blueprintsBucket.contains(bp.getName())) {
					throw new IllegalStateException("Project contains multiple blueprints with name " + bp.getName());
				}

				logger.warn("Project contains multiple blueprints with name '{}'", bp.getName());
			} else {
				blueprintsOnServer.put(bp.getName(), bp);
			}
		});

		return blueprintsOnServer;
	}

	/**
	 * Creating the file structure and files representing a blueprint
	 *
	 * @param serverPackage
	 * @param blueprint
	 */
	private void storeBlueprintsOnFilesystem(Package serverPackage, VraNgBlueprint blueprint) {
		String bpName = blueprint.getName();
		logger.debug("Exporting '{}'", bpName);

		// Creating the blueprint folder
		String bpFolderPath = Paths.get(new File(serverPackage.getFilesystemPath()).getPath(), DIR_BLUEPRINTS, bpName).toString();
		File bpFolder = new File(bpFolderPath);
		if (!bpFolder.exists()) {
			bpFolder.mkdirs();
		}

		// Storing blueprint details
		String detailsFileName = bpFolderPath + "/" + BP_DETAILS_FILE_NAME;
		JsonObject bpDetails = new JsonObject();
		bpDetails.add("id", new JsonPrimitive(blueprint.getId()));
		bpDetails.add("name", new JsonPrimitive(bpName));
		bpDetails.add("description", new JsonPrimitive(blueprint.getDescription()));
		bpDetails.add("requestScopeOrg", new JsonPrimitive(blueprint.getRequestScopeOrg()));
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		byte[] detailsContent = gson.toJson(gson.fromJson(bpDetails.toString(), JsonObject.class)).getBytes();
		logger.debug("Creating details file: " + detailsFileName);
		try {
			logger.debug("Created file: {}", Files.write(Paths.get(detailsFileName), detailsContent, StandardOpenOption.CREATE));
		} catch (Exception e) {
			System.out.println(e);
		}

		// Storing blueprint content
		String contentFileName = bpFolderPath + "/" + BP_CONTENT_FILE_NAME;
		logger.debug("Creating content file: " + contentFileName);
		try {
			byte[] contentBytes = blueprint.getContent().getBytes();
			logger.debug("Created file: {}", Files.write(Paths.get(contentFileName), contentBytes, StandardOpenOption.CREATE));
		} catch (Exception e) {
			logger.error("Unable to store blueprint content file {} {}", bpName, contentFileName);
			throw new RuntimeException("Unable to store blueprint.", e);
		}

		if (!config.getIgnoreBlueprintVersions()) {
			// Storing blueprint versions
			String versionsFileName = bpFolderPath + "/" + BP_VERSIONS_FILE_NAME;
			String versionsJSON = this.restClient.getBlueprintVersions(blueprint.getId());

			JsonArray versionsArray = gson.fromJson(versionsJSON, JsonArray.class);
			JsonArray orderedVersions = getVersionsInCorrectOrder(versionsArray);

			byte[] versionsContent = gson.toJson(orderedVersions).getBytes();
			logger.debug("Creating content file " + contentFileName);
			try {
				logger.debug("Created file: {}", Files.write(Paths.get(versionsFileName), versionsContent, StandardOpenOption.CREATE));
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	/* ==============
	 * Import logic
	 ============== */

	/**
	 * Handling import of a single blueprint - reading files from a directory, it's name would match
	 * the blueprint name and contain files describing its details, content and versions
	 *
	 * @param bpDir
	 * @param bpsOnServerByName
	 */
	private void handleBlueprintImport(File bpDir, Map<String, VraNgBlueprint> bpsOnServerByName) {
		String bpName = bpDir.getName();
		logger.info("Attempting to import blueprint \"" + bpDir.getName() + "\"");
		VraNgBlueprint bp = loadBlueprintFromFilesystem(bpDir);
		String bpID;

		// Check if the blueprint exists
		VraNgBlueprint existingRecord = null;
		if (bpsOnServerByName.containsKey(bpName)) {
			existingRecord = bpsOnServerByName.get(bpName);
		}

		if (existingRecord == null) {
			bpID = restClient.createBlueprint(bp);
			bp.setId(bpID);
		} else {
			bpID = existingRecord.getId();
			bp.setId(bpID);
			restClient.updateBlueprint(bp);
		}

		// Importing blueprint versions
		File versionsFile = new File(Paths.get(bpDir.getPath(), BP_VERSIONS_FILE_NAME).toString());
		// In case the client decides not to keep versions of the blueprints (IAC-567)
		if (versionsFile.exists()) {
			String bpVersions = readFileToString(versionsFile);
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonArray versionsJson = gson.fromJson(bpVersions, JsonArray.class).getAsJsonArray();
			versionsJson = this.getVersionsInCorrectOrder(versionsJson);
			for (int i = 0; i < versionsJson.size(); i++) {
				restClient.importBlueprintVersion(bpID, versionsJson.get(i).getAsJsonObject());
				// Sleep so versions can be ordered correctly. Milliseconds parsing in JAVA is not very good, so we are
				// forcing a one second difference between versions
				try {
					TimeUnit.SECONDS.sleep(1L);
				} catch (InterruptedException ignored) {}
			}

			if (existingRecord != null) {
				// If the blueprint has already been released at least once a next release
				// version will be attempted to be created.
				// ReleaseManager performs a diff check against the latest released version and
				// will prevent the release if there are no differences in the content.
				if (config.getBlueprintRelease() && this.restClient.isBlueprintReleased(bpID)) {
					logger.info("Blueprint {} has already been released. Attempting to release a new version...", bpName);
					VraNgReleaseManager releaseManager = new VraNgReleaseManager(this.restClient);
					releaseManager.releaseNextVersion(bp);
				}
			}
		} else {
			// Since there is no versions file we just release the blueprint.
			VraNgReleaseManager releaseManager = new VraNgReleaseManager(this.restClient);
			releaseManager.releaseNextVersion(bp);
		}
	}

	/* ==============
	 * Helper methods
	 ============== */

	/**
	 * Read contents from a file - return as string value
	 *
	 * @param file
	 * @return String
	 */
	private String readFileToString(File file) {
		try {
			return Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8).stream()
				.collect(Collectors.joining(System.lineSeparator())).toString();
		} catch (IOException e) {
			logger.error("Unable to read blueprint {}", file.getPath());
			throw new RuntimeException("Unable to read blueprint.", e);
		}
	}

	/**
	 * Fetching blueprint details + content from the filesystem exports
	 *
	 * @param bpDir
	 * @return VraNgBlueprint
	 */
	private VraNgBlueprint loadBlueprintFromFilesystem(File bpDir) {
		try {
			// Fetching blueprint details from filesystem
			File detailsFile = new File(Paths.get(bpDir.getPath(), BP_DETAILS_FILE_NAME).toString());
			File contentFile = new File(Paths.get(bpDir.getPath(), BP_CONTENT_FILE_NAME).toString());

			// Importing blueprint content
			JsonReader detailsReader = new JsonReader(new FileReader(detailsFile.getPath()));
			VraNgBlueprint bp = new Gson().fromJson(detailsReader, VraNgBlueprint.class);
			bp.setContent(readFileToString(contentFile));

			return bp;
		} catch (FileNotFoundException e) {
			logger.error("Unable to load blueprint {}", bpDir.getName());
			throw new RuntimeException("Unable to read file.", e);
		}
	}

	/**
	 * Fetching a list of blueprints from vRA
	 *
	 * @return List<VraNgBlueprint>
	 */
	private List<VraNgBlueprint> getAllBlueprints() {
		return this.restClient.getAllBlueprints();
	}

	/**
	 * A helper method that will order the versions JsonArray returned by id, so when importing they are imported in the correct order
	 * and when importing or creating a new version no errors are thrown.
	 *
	 * @param versionsArray versions
	 * @return orderedVersions
	 */
	protected JsonArray getVersionsInCorrectOrder(JsonArray versionsArray) {
		// Create an ArrayList from the JsonArray, so we can compare the elements via Collections
		ArrayList<JsonElement> newList = new ArrayList<>();
		JsonArray orderedVersions = new JsonArray();

		try {
			versionsArray.forEach(newList::add);

			// Implementing a custom comparator for the Versions based on createdAt, so we can guarantee the order of export and therefore import.
			newList.sort((one, two) -> {
				// Extract the createdAt attribute and remove the start and end quote from it
				String creationDateOne = one.getAsJsonObject().get("createdAt").toString()
					.replaceAll("^\"|\"$", "");
				String creationDateTwo = two.getAsJsonObject().get("createdAt").toString()
					.replaceAll("^\"|\"$", "");

				// Date is returned in ISO-8601, e.g. "2022-08-22T14:17:00.073876Z";
				return Instant.parse(creationDateOne).compareTo(Instant.parse(creationDateTwo));
			});
		} catch (NullPointerException npe) {
			logger.error("Provided versions array is null");
		}
		newList.forEach(orderedVersions::add);
		return orderedVersions;
	}
}
