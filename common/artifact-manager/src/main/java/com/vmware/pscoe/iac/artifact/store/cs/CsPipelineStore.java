package com.vmware.pscoe.iac.artifact.store.cs;

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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsPipelineStore extends AbstractCsStore {
	private static final String DIR_PIPELINES = "pipelines";
	private final Logger logger = LoggerFactory.getLogger(CsPipelineStore.class);
	private List<JsonObject> projectPipelines;

	List<JsonObject> getProjectPipelines() {
		if (projectPipelines == null) {
			this.projectPipelines = this.restClient.getProjectPipelines();
		}
		return this.projectPipelines;

	}

	/**
	* Exporting the contents of all blueprints listed in the content.yaml file, available for the configured project
	*/
	public void exportContent() {
		List<String> pipelineNames = this.descriptor.getPipeline();
		if (pipelineNames == null) {
			logger.info("No pipelines found in content.yaml");
			return;
		}
		this.getProjectPipelines()
				.stream()
				.filter(el -> pipelineNames.contains(el.get("name").getAsString()))
				.forEach(this::exportPipeline);

	}

	/**
	 * Importing content into vRA target environment
	 * @param sourceDirectory sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File pipelinesFolder = Paths.get(sourceDirectory.getPath(), DIR_PIPELINES).toFile();
		if (!pipelinesFolder.exists()) {
			return;
		}

		Collection<File> pipelineFiles = FileUtils.listFiles(pipelinesFolder, new String[] {"yaml"}, false);
		if (pipelineFiles == null || pipelineFiles.isEmpty()) {
			return;
		}

		pipelineFiles.stream().forEach(this::importPipeline);
	}

	private void exportPipeline(JsonObject obj) {
		String pipelineName = obj.get("name").getAsString();
		logger.info("Exporting pipeline : {}", pipelineName);
		CsStoreHelper.sanitizeDefaultProperties(obj);
		String jsonString = obj.toString();
		CsStoreHelper.storeToYamlFile(csPackage.getFilesystemPath(), DIR_PIPELINES, pipelineName, jsonString);
		CsStoreHelper.addVarsToExtractionContext(jsonString, descriptor);
	}

	private void importPipeline(File pipelineFile) {
		String jsonString = CsStoreHelper.loadFromYamlFile(pipelineFile);
		JsonElement el = JsonParser.parseString(jsonString);
		JsonObject obj = el.getAsJsonObject();
		String name = obj.get("name").getAsString();
		String state = obj.get("state").getAsString();
		obj.addProperty("project", restClient.getProjectName());
		Optional<JsonObject> optional = CsStoreHelper.findObjectByName(getProjectPipelines(), name);
		if (optional.isPresent()) {
			obj.addProperty("id", optional.get().get("id").getAsString());
			restClient.updatePipeline(name, obj);
		} else {
			restClient.createPipeline(obj);

		}
		// On creation the enabled and release statuses are never updated.
		// To change to released you should go first through enabled.
		JsonObject statusContent = new JsonObject();
		if ("RELEASED".equals(state)) {
			statusContent.addProperty("state", "ENABLED");
			restClient.patchPipeline(name, statusContent);
		}
		statusContent.addProperty("state", state);
		restClient.patchPipeline(name, statusContent);
	}

}
