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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.vmware.pscoe.iac.artifact.rest.model.cs.CustomIntegrationVersion;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsCustomIntegrationStore extends AbstractCsStore {
	private static final String DIR_VAR = "custom-integrations";
	private final Logger logger = LoggerFactory.getLogger(CsPipelineStore.class);
	private List<CustomIntegrationVersion> items;

	/**
	* Exporting the contents of customIntegrations
	*/
	public void exportContent() {
		List<String> customIntegrationNames = this.descriptor.getCustomIntegration();
		if (customIntegrationNames == null) {
			logger.info("No Custom Integrations found in content.yaml");
			return;
		}
		customIntegrationNames.forEach(this::exportCustomIntegration);
	}

	/**
	 * Importing content into vRA target environment
	 * @param sourceDirectory sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File varFolder = Paths.get(sourceDirectory.getPath(), DIR_VAR).toFile();
		if (!varFolder.exists()) {
			logger.info("Missing custom integrations folder.");
			return;
		}
		Collection<File> varFiles = FileUtils.listFiles(varFolder, new String[] {"yaml"}, false);
		if (varFiles == null || varFiles.isEmpty()) {
			logger.info("Empty custom integrations folder.");
			return;
		}
		varFiles.forEach(this::importCustomIntegration);
	}

	private void exportCustomIntegration(String ciName) {
		logger.info("Exporting custom integration: {}", ciName);
		Optional<CustomIntegrationVersion> optionalCi = this.getAllItems()
				.stream()
				.filter(el -> el.getName().equals(ciName))
				.findFirst();
		if (!optionalCi.isPresent()) {
			logger.warn("Custom Integration '{}' not found!", ciName);
			return;
		}
		CustomIntegrationVersion ci = optionalCi.get();
		List<CustomIntegrationVersion> versions = this.restClient.getCustomIntegrationVersions(ci.getId());
		List<CustomIntegrationVersion> all = new LinkedList<>();
		all.add(ci);
		all.addAll(versions);
		all.forEach(v -> v.setId(null));
		CsStoreHelper.storeToYamlFile(csPackage.getFilesystemPath(), DIR_VAR, ciName, all);
	}



	private void importCustomIntegration(File file) {
		logger.info("Importing custom integration '{}'.", file.getName());
		List<CustomIntegrationVersion> versions = Arrays.asList(CsStoreHelper.loadFromYamlFile(file, CustomIntegrationVersion[].class));
		final CustomIntegrationVersion ci = versions.get(0);
		Optional<CustomIntegrationVersion> existingCiOptional = this.getAllItems().stream()
				.filter(el -> el.getName().equals(ci.getName()))
				.findFirst();

		if (existingCiOptional.isPresent()) {
			logger.info("Updating custom integration: '{}'", file.getName());
			ci.setId(existingCiOptional.get().getId());
			List<CustomIntegrationVersion> existingVersions = restClient.getCustomIntegrationVersions(ci.getId());
			List<String> existingIds = existingVersions.stream().map(CustomIntegrationVersion::getVersion).collect(Collectors.toList());
			restClient.updateCustomIntegration(ci);
			versions.stream()
					.skip(1)
					.filter(ver -> !existingIds.contains(ver.getVersion()))
					.forEach(version -> restClient.createCustomIntegrationVersion(ci.getId(), version));

		} else {
			logger.info(String.format("Creating custom integration '{}'."));
			final CustomIntegrationVersion newCi = restClient.createCustomIntegration(ci);
			versions.stream()
					.skip(1)
					.forEach(version -> restClient.createCustomIntegrationVersion(newCi.getId(), version));
		}
	}

	List<CustomIntegrationVersion> getAllItems() {
		if (items == null) {
			this.items = this.restClient.getCustomIntegrations();
		}
		return this.items;

	}
}
