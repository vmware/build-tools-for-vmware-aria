package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;

import org.apache.commons.io.FilenameUtils;

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

public class VraNgLeasePolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for Lease policy.
	 */
	private static final String LEASE_POLICY = "lease";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgLeasePolicy.class);

	/**
	 * Imports content.
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory",
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_CONTENT_SHARING_POLICIES);
		// verify directory exists
		File leasePolicyFolder = Paths
				.get(sourceDirectory.getPath(),
						com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_CONTENT_SHARING_POLICIES,
						LEASE_POLICY)
				.toFile();
		if (!leasePolicyFolder.exists()) {
			logger.info("Lease policy directory not found.");
			return;
		}

		List<String> csPolicies = this.getItemListFromDescriptor();

		File[] leasePolicyFiles = this.filterBasedOnConfiguration(leasePolicyFolder,
				new CustomFolderFileFilter(csPolicies));

		if (leasePolicyFiles != null && leasePolicyFiles.length == 0) {
			logger.info("Could not find any Lease Policies.");
			return;
		}

		logger.info("Found Lease Policies. Importing...");
		Map<String, VraNgLeasePolicy> csPolicyOnServerByName = this.restClient.getLeasePolicies()
				.stream()
				.map(csp -> this.restClient.getLeasePolicy(csp.getId()))
				.collect(Collectors.toMap(VraNgLeasePolicy::getName, item -> item));

		for (File leasePolicyFile : leasePolicyFiles) {
			this.handleLeasePolicyImport(leasePolicyFile, csPolicyOnServerByName);
		}
	}

	private void handleLeasePolicyImport(final File leasePolicyFile,
			final Map<String, VraNgLeasePolicy> csPolicyOnServerByName) {
		String csPolicyNameWithExt = leasePolicyFile.getName();
		String csPolicyName = FilenameUtils.removeExtension(csPolicyNameWithExt);
		logger.info("Attempting to import Lease policy '{}'", csPolicyName);
		VraNgLeasePolicy csPolicy = jsonFileToVraNgLeasePolicy(leasePolicyFile);
		this.enrichLeasePolicy(csPolicy);
		// Check if the Lease policy exists
		VraNgLeasePolicy existingRecord = null;
		if (csPolicyOnServerByName.containsKey(csPolicyName)) {
			existingRecord = csPolicyOnServerByName.get(csPolicyName);
		}
		if (existingRecord != null) {
			csPolicy.setId(this.restClient.getLeasePolicyIdByName(csPolicy.getName()));
		}

		this.restClient.createLeasePolicy(csPolicy);
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of Lease policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgLeasePolicy jsonFileToVraNgLeasePolicy(final File jsonFile) {
		logger.debug("Converting Lease policy file to VraNgLeasePolicy. Name: '{}'",
				jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgLeasePolicy.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Used while import to override the orgId and ProjectId for the receiving vRA instance.
	 *  
	 * @param csPolicy
	 */
	private void enrichLeasePolicy(final VraNgLeasePolicy csPolicy) {
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
		csPolicy.setOrgId(organizationId);
		csPolicy.setProjectId(this.restClient.getProjectId());
	}

	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {

		List<VraNgLeasePolicy> csPolicies = this.restClient.getLeasePolicies();

		csPolicies.forEach(
				policy -> {
					VraNgLeasePolicy csPolicy = this.restClient.getLeasePolicy(policy.getId());
					storeLeasePolicyOnFilesystem(vraNgPackage, csPolicy);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in content.yaml.
	 * 
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		List<VraNgLeasePolicy> csPolicies = this.restClient.getLeasePolicies();

		csPolicies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						VraNgLeasePolicy csPolicy = this.restClient.getLeasePolicy(policy.getId());
						logger.info("exporting '{}'", csPolicy.getName());
						storeLeasePolicyOnFilesystem(vraNgPackage, csPolicy);
					}
				});
	}

	/**
	 * Store Lease policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param contentSharingPolicy contentSharingPolicy representation
	 */
	private void storeLeasePolicyOnFilesystem(final Package serverPackage,
			final VraNgLeasePolicy leasePolicy) {
		logger.debug("Storing contentSharingPolicy {}", leasePolicy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		File leasePolicyFile = Paths.get(
				store.getPath(),
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_CONTENT_SHARING_POLICIES,
				LEASE_POLICY,
				leasePolicy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!leasePolicyFile.getParentFile().isDirectory()
				&& !leasePolicyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", leasePolicyFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject csPolicyJsonObject = gson.fromJson(new Gson().toJson(leasePolicy), JsonObject.class);
			JsonObject definition = csPolicyJsonObject.getAsJsonObject("definition");
			JsonArray euArr = definition.getAsJsonArray("entitledUsers");
			if (euArr != null) {
				for (JsonElement eu : euArr) {
					JsonObject entitledUserObj = eu.getAsJsonObject();
					JsonArray itemsArr = entitledUserObj.getAsJsonArray("items");
					for (JsonElement item : itemsArr) {
						JsonObject itemObj = item.getAsJsonObject();
						itemObj.remove("id");
					}
				}
			}
			definition.add("entitledUsers", euArr);
			csPolicyJsonObject.add("definition", definition);
			// write Lease file
			logger.info("Created Lease file {}",
					Files.write(Paths.get(leasePolicyFile.getPath()),
							gson.toJson(csPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create Lease {}", leasePolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store Lease to file %s.", leasePolicyFile.getAbsolutePath()),
					e);
		}

	}
}
