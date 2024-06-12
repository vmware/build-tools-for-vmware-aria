package com.vmware.pscoe.iac.artifact.store.cs;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public final class CsPipelineStore extends AbstractCsStore {
	/**
	 * The pipelines root folder.
	 */
	private static final String DIR_PIPELINES = "pipelines";

	/**
	 * logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(CsPipelineStore.class);

	/**
	 * projectPipelines.
	 */
	private List<JsonObject> projectPipelines;

	List<JsonObject> getProjectPipelines() {
		if (projectPipelines == null) {
			this.projectPipelines = this.restClient.getProjectPipelines();
		}
		return this.projectPipelines;

	}

	/**
	 * Exporting the contents of all blueprints listed in the content.yaml file,
	 * available for the configured project.
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
	 * Importing content into vRA target environment.
	 * 
	 * @param sourceDirectory sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File pipelinesFolder = Paths.get(sourceDirectory.getPath(), DIR_PIPELINES).toFile();
		if (!pipelinesFolder.exists()) {
			return;
		}

		Collection<File> pipelineFiles = FileUtils.listFiles(pipelinesFolder, new String[] { "yaml" }, false);
		if (pipelineFiles == null || pipelineFiles.isEmpty()) {
			return;
		}

		// Map to store dependencies extracted from YAML files
		Map<String, Set<String>> dependencies = new HashMap<>();

		// Parse YAML files and extract dependencies
		for (File file : pipelineFiles) {
			Set<String> deps = extractPipeRollbackDependencies(file);
			dependencies.put(file.getName(), deps);
		}

		// Perform topological sort
		List<String> orderedFiles = topologicalSort(dependencies);

		// Import pipelines in the ordered sequence
		for (String fileName : orderedFiles) {
			File file = new File(pipelinesFolder, fileName);
			importPipeline(file);
		}
	}

	private Set<String> extractPipeRollbackDependencies(File yamlFile) {
		Set<String> dependencies = new HashSet<>();
		try (InputStream inputStream = new FileInputStream(yamlFile)) {
			Yaml yaml = new Yaml();
			Map<String, Object> data = yaml.load(inputStream);
			if (data.containsKey("rollbacks")) {
				Object rollbacksObj = data.get("rollbacks");
				if (rollbacksObj instanceof List) {
					for (Object rollback : (List<?>) rollbacksObj) {
						if (rollback instanceof Map) {
							Object nameObj = ((Map<?, ?>) rollback).get("name");
							if (nameObj instanceof String) {
								String dependencyFileName = (String) nameObj;
								dependencies.add(dependencyFileName + ".yaml");
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return dependencies;
	}

	// Topological sort algorithm
	private List<String> topologicalSort(Map<String, Set<String>> dependencies) {
		Map<String, Integer> vertexMap = new HashMap<>();
		List<String> fileList = new ArrayList<>(dependencies.keySet());
		for (int i = 0; i < fileList.size(); i++) {
			vertexMap.put(fileList.get(i), i);
		}

		List<List<Integer>> graph = new ArrayList<>();
		for (int i = 0; i < fileList.size(); i++) {
			graph.add(new ArrayList<>());
		}
		for (Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
			int from = vertexMap.get(entry.getKey());
			for (String dep : entry.getValue()) {
				int to = vertexMap.get(dep);
				graph.get(from).add(to);
			}
		}

		List<String> orderedFiles = new ArrayList<>();
		boolean[] visited = new boolean[fileList.size()];
		for (int i = 0; i < fileList.size(); i++) {
			if (!visited[i]) {
				topologicalSortUtil(graph, i, visited, orderedFiles, fileList);
			}
		}
		return orderedFiles;
	}

	private void topologicalSortUtil(List<List<Integer>> graph, int v, boolean[] visited, List<String> orderedFiles,
			List<String> fileList) {
		visited[v] = true;
		for (int i : graph.get(v)) {
			if (!visited[i]) {
				topologicalSortUtil(graph, i, visited, orderedFiles, fileList);
			}
		}
		orderedFiles.add(fileList.get(v));
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
