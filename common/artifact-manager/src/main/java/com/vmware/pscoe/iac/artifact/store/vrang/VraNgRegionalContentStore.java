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
package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.model.vrang.Identifiable;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegionMapping;

import org.yaml.snakeyaml.Yaml;

/**
 * This class is responsible for managing the regional content in vRA NG.
 */
public class VraNgRegionalContentStore extends AbstractVraNgStore {

	/**
	 * Need refactoring, intentionally left empty.
	 */
	public void deleteContent() {
	}

	/**
	 * Unused, it's for `deleteContent` method.
	 */
	@Override
	protected <T extends Identifiable> List<T> getAllServerContents() {
		return null;
	}

	/**
	 * Unused, it's for `deleteContent` method.
	 *
	 * @param resId the id of the resource to delete
	 */
	@Override
	protected void deleteResourceById(String resId) {
	}

	/**
	 * Import region-specific content based on region mapping defined in the
	 * package.
	 *
	 * @param sourceDirectory temporary directory containing the files
	 */
	@Override
	public void importContent(File sourceDirectory) {
		importRegionalContent(sourceDirectory);
	}

	/**
	 * The regionalContent exports all flavor mapping, image mapping and storage
	 * profile in partucular region.
	 */
	@Override
	public void exportContent() {
		VraNgRegionMapping regionMapping = vraNgPackageDescriptor.getRegionMapping();
		if (regionMapping != null) {
			exportRegionalContent(regionMapping);
		} else {
			logger.info("No region mapping found in content.yaml");
		}
	}

	/**
	 * Unused because the class overwrites directly exportContent.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return null;
	}

	/**
	 * Unused because the class overwrites directly exportContent.
	 */
	@Override
	protected void exportStoreContent() {
	}

	/**
	 * Unused because the class overwrites directly exportContent.
	 *
	 * @param itemNames the list of items to export
	 */
	@Override
	protected void exportStoreContent(List<String> itemNames) {

	}

	/**
	 * Export region-specific content based on region mappings defined in the
	 * package manifest. This includes: * flavor mappings * image mappings * storage
	 * profiles.
	 * 
	 * @param regionMapping region mapping structure describing the regions
	 *                      (cloud zones) that have related exportable
	 *                      content defined in the package manifest.
	 */
	private void exportRegionalContent(VraNgRegionMapping regionMapping) {

		// determine from which cloud account to export the content (based on the export
		// tag)
		String exportTag = regionMapping.getCloudAccountTags().getExportTag();
		List<VraNgCloudAccount> cloudAccounts = this.restClient
				.getCloudAccounts().stream().filter(cloudAccount -> VraNgRegionalContentUtils
						.isIntersecting(cloudAccount.getTags(), new ArrayList<String>(Arrays.asList(exportTag))))
				.collect(Collectors.toList());

		logger.info(
				"Found {} cloud accounts from which to export regional content (image mappings, flavor mappings, storage profiles) based on tag {}",
				cloudAccounts.size(), exportTag);

		// no need to export regional content when no exportable cloud accounts are
		// found
		if (cloudAccounts.isEmpty()) {
			logger.info(
					"No cloud accounts found based on export tag {}. Skipping export of regional content (image mappings, flavor mappings, storage profiles)",
					exportTag);
			return;
		}

		// export flavor mappings
		VraNgFlavorMappingStore flavorMappingStore = new VraNgFlavorMappingStore();
		flavorMappingStore.init(restClient, vraNgPackage, vraNgPackageDescriptor);
		flavorMappingStore.exportContent(cloudAccounts);

		// export storage profiles
		VraNgStorageProfileStore storageProfileStore = new VraNgStorageProfileStore();
		storageProfileStore.init(restClient, vraNgPackage, vraNgPackageDescriptor);
		storageProfileStore.exportContent(cloudAccounts);
	}

	/**
	 * Import region-specific content based on region mapping defined in the package
	 * manifest. The manifest (content.yaml) is part of the vra-ng package and read
	 * on the fly.
	 * 
	 * @param sourceDirectory temporary directory containing the files
	 */
	private void importRegionalContent(File sourceDirectory) {

		try {

			// read content.yaml
			Yaml yaml = new Yaml();
			File content = Paths.get(sourceDirectory.getPath() + File.separator + "content.yaml").toFile();
			Map<String, Object> yamlContent = yaml.load(new FileInputStream(content));

			// get region mapping
			if (!yamlContent.containsKey("region-mapping") || yamlContent.get("region-mapping") == null) {
				logger.info(
						"content.yaml does not contain 'region-mapping' entry. Skipping import of regional content...");
				return;
			}
			Map<String, Object> regionMapping = (Map<String, Object>) yamlContent.get("region-mapping");
			logger.debug("Region mapping: {}", regionMapping);

			// get cloud-account-tags
			if (!regionMapping.containsKey("cloud-account-tags") || regionMapping.get("cloud-account-tags") == null) {
				logger.warn("region-mapping entry does not contain 'cloud-account-tags");
				return;
			}
			Map<String, Object> cloudAccountTags = (Map<String, Object>) regionMapping.get("cloud-account-tags");

			// get import tags
			if (!cloudAccountTags.containsKey("import-tags") || cloudAccountTags.get("import-tags") == null) {
				logger.warn("cloud-account-tags entry does not contain 'import-tags");
				return;
			}
			List<String> importTags = (ArrayList<String>) cloudAccountTags.get("import-tags");

			// flavor mappings
			VraNgFlavorMappingStore flavorMappingStore = new VraNgFlavorMappingStore();
			flavorMappingStore.init(restClient, vraNgPackage, vraNgPackageDescriptor);
			flavorMappingStore.importContent(sourceDirectory, importTags);

			// storage profiles
			VraNgStorageProfileStore storageProfileStore = new VraNgStorageProfileStore();
			storageProfileStore.init(restClient, vraNgPackage, vraNgPackageDescriptor);
			storageProfileStore.importContent(sourceDirectory, importTags);

		} catch (FileNotFoundException e) {
			logger.info("content.yaml is not part of the package. Skipping import of regional content...");
		}
	}
}
