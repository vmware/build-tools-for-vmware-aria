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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.rest.model.cs.Endpoint;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsEndpointStore extends AbstractCsStore {
	private static final String DIR_ENDPOINTS = "endpoints";
	private final Logger logger = LoggerFactory.getLogger(CsEndpointStore.class);

	/**
	* Exporting the contents of all blueprints listed in the content.yaml file, available for the configured project
	*/
	public void exportContent() {
		List<String> endpointNames = this.descriptor.getEndpoint();
		if (endpointNames != null) {
			this.exportEndpoints(csPackage, endpointNames);
		} else {
			logger.info("No endpoints found in content.yaml");
		}
	}

	/**
	 * Importing content into vRA target environment
	 * @param sourceDirectory sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File endpointsFolder = Paths.get(sourceDirectory.getPath(), DIR_ENDPOINTS).toFile();
		if (!endpointsFolder.exists()) {
			logger.info("No endpoints folder found in content.");
			return;
		}

		Collection<File> endpointFiles = FileUtils.listFiles(endpointsFolder, new String[] {"yaml"}, false);
		if (endpointFiles == null || endpointFiles.isEmpty()) {
			logger.info("No endpoints files found in content.");
			return;
		}
		importEndpoints(endpointFiles);
	}

	private void exportEndpoints(Package csPackage, List<String> endpointNames) {
		restClient.getProjectEndpoints()
				.stream()
				.filter(ep -> endpointNames.contains(ep.getName()))
				.forEach(endpoint -> {
					logger.info("Exporting endpoint: {}", endpoint.getName());
					endpoint.setId(null);
					endpoint.setProject(null);
					endpoint.setCloudProxyId(null);
					CsStoreHelper.storeToYamlFile(csPackage.getFilesystemPath(), DIR_ENDPOINTS, endpoint.getName(), endpoint);
					addEndpointVarsToExtractionContext(endpoint);
				});
	}

	private void importEndpoints(Collection<File> endpointFiles) {
		List<Endpoint> existingEndpoints = restClient.getProjectEndpoints();

		endpointFiles.stream()
				.map(file -> CsStoreHelper.loadFromYamlFile(file, Endpoint.class))
				.forEach(endpoint -> {
					endpoint.setProject(restClient.getProjectName());
					endpoint.setCloudProxyId(restClient.getCloudProxyId());
					Optional<Endpoint> optional = existingEndpoints.stream()
							.filter(ex -> ex.getName().equals(endpoint.getName()))
							.findFirst();
					if (optional.isPresent()) {
						endpoint.setId(optional.get().getId());
						restClient.updateEndpoint(endpoint);

					} else {
						restClient.createEndpoint(endpoint);
					}
				});
	}

	private void addEndpointVarsToExtractionContext(Endpoint endpoint) {
		Set<String> allMatches = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile("\\$\\{var\\.(.*?)\\}");
		endpoint.getProperties().values().forEach(val -> {
			Matcher m = pattern.matcher(val);
			while (m.find()) {
				allMatches.add(m.group(1));
			}
		});

		List<String> newVars = allMatches.stream()
				.filter(el -> !descriptor.getVariable().contains(el))
				.collect(Collectors.toList());
		if (newVars.size() > 0) {
			logger.info("Variables from endpoint:" + newVars.toString());
			descriptor.getVariable().addAll(newVars);
		}
	}

}
