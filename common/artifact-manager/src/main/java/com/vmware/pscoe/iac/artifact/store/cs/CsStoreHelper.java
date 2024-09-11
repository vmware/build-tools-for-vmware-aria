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
package com.vmware.pscoe.iac.artifact.store.cs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsStoreHelper {
	private final static Logger logger = LoggerFactory.getLogger(CsStoreHelper.class);

	public static void addVarsToExtractionContext(String content, CsPackageDescriptor descriptor) {

		Set<String> allMatches = new LinkedHashSet<String>();
		Matcher m = Pattern
				.compile("\\$\\{var\\.(.*?)\\}")
				.matcher(content);
		while (m.find()) {
			allMatches.add(m.group(1));
		}
		List<String> newVars = allMatches.stream()
				.filter(el -> !descriptor.getVariable().contains(el))
				.collect(Collectors.toList());
		if (newVars.size() > 0) {
			descriptor.getVariable().addAll(newVars);
		}

	}

	public static void sanitizeDefaultProperties(JsonObject obj) {
		String[] myIntArray = {"id", "project", "_link", "_createdBy", "_updatedBy", "_updateTimeInMicros", "_createTimeInMicros", "_projectId"};
		Arrays.stream(myIntArray).forEach(obj::remove);
	}

	public static Optional<JsonObject> findObjectByName(List<JsonObject> objects, String name) {
		return objects
				.stream()
				.filter(ex -> ex.get("name").getAsString().equals(name))
				.findFirst();
	}



	public static void storeToYamlFile(String path, String subPath, String name, String jsonString) {
		File store = new File(path);
		File file = Paths.get(store.getPath(), subPath, name + ".yaml").toFile();
		file.getParentFile().mkdirs();
		try {
			JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
			YAMLMapper yamlMapper = new YAMLMapper();
			yamlMapper.setSerializationInclusion(Include.NON_NULL);
			String yamlString = yamlMapper.writeValueAsString(jsonNodeTree);
			// StringWriter writer = new StringWriter();
			// writer.write(triggerYaml);
			Files.write(Paths.get(file.getPath()), yamlString.getBytes(),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			logger.error("Unable to store file {} {}", name, file.getPath());
			throw new RuntimeException("Unable to store file.", e);
		}
	}

	public static void storeToYamlFile(String path, String subPath, String name, Object obj) {


		File store = new File(path);
		File file = Paths.get(store.getPath(), subPath, name + ".yaml").toFile();
		file.getParentFile().mkdirs();
		try {
			YAMLMapper yamlMapper = new YAMLMapper();
			yamlMapper.setSerializationInclusion(Include.NON_NULL);
			yamlMapper.enable(Feature.LITERAL_BLOCK_STYLE);
			String yamlString = yamlMapper.writeValueAsString(obj);
			// StringWriter writer = new StringWriter();
			// writer.write(triggerYaml);
			Files.write(Paths.get(file.getPath()), yamlString.getBytes(),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			logger.error("Unable to store file {} {}", name, file.getPath());
			throw new RuntimeException("Unable to store file.", e);
		}
	}

	public static String loadFromYamlFile(File file) {
		YAMLMapper yaml = new YAMLMapper();
		try (FileInputStream stream = new FileInputStream(file)) {
			ObjectNode yamlContent = (ObjectNode) yaml.readTree(stream);
			ObjectMapper jsonMapper = new ObjectMapper();
			jsonMapper.setSerializationInclusion(Include.NON_NULL);
			String json = jsonMapper.writeValueAsString(yamlContent);
			return json;
		} catch (IOException e) {
			throw new RuntimeException("Failed to import trigger.", e);
		}
	}

	public static <T> T loadFromYamlFile(File varFile, Class<T> clazz) {
		System.out.println(varFile.getAbsolutePath());
		try {
			YAMLMapper yamlMapper = new YAMLMapper();
			return yamlMapper.readValue(varFile, clazz);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error while reading file '%s'", varFile.getPath()));
		}
	}
}
