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

import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;
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

public final class VraNgLeasePolicyStore extends AbstractVraNgStore {
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
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES);
		// verify directory exists
		File leasePolicyFolder = Paths
				.get(sourceDirectory.getPath(),
						com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
						LEASE_POLICY)
				.toFile();
		if (!leasePolicyFolder.exists()) {
			logger.info("Lease policy directory not found.");
			return;
		}

		List<String> policies = this.getItemListFromDescriptor();

		File[] leasePolicyFiles = this.filterBasedOnConfiguration(leasePolicyFolder,
				new CustomFolderFileFilter(policies));

		if (leasePolicyFiles != null && leasePolicyFiles.length == 0) {
			logger.info("Could not find any Lease Policies.");
			return;
		}

		logger.info("Found Lease Policies. Importing...");
		for (File leasePolicyFile : leasePolicyFiles) {
			this.handleLeasePolicyImport(leasePolicyFile);
		}
	}

	private void handleLeasePolicyImport(final File leasePolicyFile) {
		VraNgLeasePolicy policy = jsonFileToVraNgLeasePolicy(leasePolicyFile);
		logger.info("Attempting to import Lease policy '{}'", policy.getName());

		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();

		//if the policy has a project property, replace it with current project id.
		//if the policy does not have a project property - replacing it will change the policy,
		// so do not replace a null or blank value.
		if ( policy.getProjectId() != null && !(policy.getProjectId().isBlank()) && !policy.getOrgId().equals(organizationId)) {
			logger.debug("Replacing policy projectId with projectId from configuration.");
			policy.setProjectId(this.restClient.getProjectId());
		}
		policy.setOrgId(organizationId);
		this.restClient.createLeasePolicy(policy);
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
			return this.vraNgPackageDescriptor.getPolicy().getLease();
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
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {

		List<VraNgLeasePolicy> policies = this.restClient.getLeasePolicies();
		this.logger.debug("{}->exportStoreContent()", this.getClass());
		policies.forEach(
				policy -> {
					storeLeasePolicyOnFilesystem(vraNgPackage, policy);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in content.yaml.
	 * 
	 * @param itemNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		List<VraNgLeasePolicy> policies = this.restClient.getLeasePolicies();
		this.logger.debug("{}->exportStoreContent({})", this.getClass(), itemNames.toString());
		policies.forEach(
				policy -> {
					if (itemNames.contains(policy.getName())) {
						logger.info("exporting '{}'", policy.getName());
						storeLeasePolicyOnFilesystem(vraNgPackage, policy);
					}
				});
	}

	/**
	 * Store Lease policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param leasePolicy LeasePolicy representation
	 */
	private void storeLeasePolicyOnFilesystem(final Package serverPackage,
			final VraNgLeasePolicy leasePolicy) {
		logger.debug("Storing Lease Policy {}", leasePolicy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		String filename = leasePolicy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}
		File leasePolicyFile = Paths.get(
				store.getPath(),
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
				LEASE_POLICY,
			filename + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!leasePolicyFile.getParentFile().isDirectory()
				&& !leasePolicyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", leasePolicyFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject policyJsonObject = gson.fromJson(new Gson().toJson(leasePolicy), JsonObject.class);
			// write Lease file
			logger.info("Created Lease file {}",
					Files.write(Paths.get(leasePolicyFile.getPath()),
							gson.toJson(policyJsonObject).getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create Lease {}", leasePolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store Lease to file %s.", leasePolicyFile.getAbsolutePath()),
					e);
		}

	}
}
