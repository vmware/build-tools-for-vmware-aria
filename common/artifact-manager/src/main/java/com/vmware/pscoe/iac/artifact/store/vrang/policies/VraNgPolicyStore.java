package com.vmware.pscoe.iac.artifact.store.vrang.policies;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies.*;
import com.vmware.pscoe.iac.artifact.store.vrang.AbstractVraNgStore;
import com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;

import org.apache.commons.io.FilenameUtils;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent.*;

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

public class VraNgPolicyStore extends AbstractVraNgStore {
	private class PolicyFile {
		PolicyFile(File file, PolicyType policyType) {
			this.file = file;
			this.policyType = policyType;
		}

		public File file;

		public PolicyType policyType;
	}

	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgPolicyStore.class);

	/**
	 * Imports content.
	 * @param sourceDirectory the directory.
	 */
	@Override
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory",
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES);

		String sourcePath = sourceDirectory.getPath();

		Map<PolicyType, List<String>> policies = this.getPoliciesFromDescriptor();
		if (policies == null) {
			return;
		}

		List<PolicyFile> policyFiles = this.getPolicyFiles(policies, sourcePath);

		if (policyFiles.size() == 0) {
			logger.info("Could not find any Policies.");

			return;
		}

		logger.info("Found Policies. Importing...");
		// here need to get all policies
		Map<String, VraNgPolicyBase> policiesOnServerByName = this.restClient.getVraPolicies()
				.stream()
				.map(policy -> this.restClient.getPolicy(policy.getId()))
				.collect(Collectors.toMap(VraNgPolicyBase::getName, item -> item));

		for (PolicyFile contentSharingPolicyFile : policyFiles) {
			this.handlePolicyImport(contentSharingPolicyFile, policiesOnServerByName);
		}
	}

	/**
	 * .
	 * Handles logic to update or create a content sharing policy.
	 *
	 * @param policyFile
	 *
	 * @param policiesOnServerByName
	 */
	private void handlePolicyImport(final PolicyFile policyFile,
									final Map<String, VraNgPolicyBase> policiesOnServerByName) {
		String policyNameWithExt = policyFile.file.getName();
		String policyName = FilenameUtils.removeExtension(policyNameWithExt);
		logger.info("Attempting to import content sharing policy '{}'", policyName);
		VraNgPolicyBase policy = this.jsonFileToVraNgPolicy(policyFile.file, policyFile.policyType);
		this.enrichPolicy(policy);
		// Check if the content sharing policy exists
		VraNgPolicyBase existingRecord = null;
		if (policiesOnServerByName.containsKey(policyName)) {
			existingRecord = policiesOnServerByName.get(policyName);
		}
		if (existingRecord != null) {
			policy.setId(this.restClient.getPolicyIdByName(policy.getName()));
		}

		// create according to type
		this.restClient.createVraNgPolicy(policy);
	}

	/**
	 * Get List from descriptor.
	 *
	 * @return null or list of content sharing policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		throw new NotImplementedException("Not implemented!");
	}

	/**
	 * Exports all the content for the given project.
	 */
	@Override
	protected void exportStoreContent() {
		List<VraNgPolicyBase> allPolicies = this.restClient.getVraPolicies();

		allPolicies.forEach(
				policy -> {
					VraNgPolicyBase vraNgPolicy = this.restClient.getPolicy(policy.getId());

					this.storePolicyOnFilesystem(vraNgPackage, vraNgPolicy);
				});
	}

	/**
	 * Exports all the content for the given project based on the names in content.yaml.
	 *
	 * @param policyNames Item names for export
	 */
	@Override
	protected void exportStoreContent(final List<String> policyNames) {
		List<VraNgPolicyBase> allPolicies = this.restClient.getVraPolicies();

		allPolicies.forEach(
				policy -> {
					if (policyNames.contains(policy.getName())) {
						VraNgPolicyBase csPolicy = this.restClient.getPolicy(policy.getId());
						logger.info("exporting '{}'", csPolicy.getName());

						this.storePolicyOnFilesystem(vraNgPackage, csPolicy);
					}
				});
	}

	/**
	 * Store content sharing policy in JSON file.
	 *
	 * @param serverPackage        vra package
	 * @param vraNgPolicy contentSharingPolicy representation
	 */
	private void storePolicyOnFilesystem(final Package serverPackage,
										 final VraNgPolicyBase vraNgPolicy) {
		logger.debug("Storing policy {}", vraNgPolicy.getName());
		File store = new File(serverPackage.getFilesystemPath());
		Map<PolicyType, String> policyTypesToSubDirs = VraNgDirs.getPolicySubDirs();
		PolicyType policyType = PolicyType.findMember(vraNgPolicy.getTypeId());

		File policyFile = Paths.get(
				store.getPath(),
				com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
				policyTypesToSubDirs.get(policyType),
				vraNgPolicy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!policyFile.getParentFile().isDirectory()
				&& !policyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFile.getParentFile().getAbsolutePath());
		}

		if (policyType == PolicyType.CONTENT_SHARING_POLICY_TYPE) {
			this.storeContentSharingPolicy(vraNgPolicy, policyFile);
		} else if (policyType == PolicyType.RESOURCE_QUOTA_POLICY_TYPE) {
			this.storeResourceQuotaPolicy(vraNgPolicy, policyFile);
		}
	}

	private void storeContentSharingPolicy(VraNgPolicyBase vraNgPolicy, File contentSharingPolicyFile) {
		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			JsonObject csPolicyJsonObject = gson.fromJson(new Gson().toJson(vraNgPolicy), JsonObject.class);
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
			// write content sharing file
			logger.info("Created content sharing file {}",
				Files.write(Paths.get(contentSharingPolicyFile.getPath()),
					gson.toJson(csPolicyJsonObject).getBytes(), StandardOpenOption.CREATE));
		}
		catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(
				String.format(
					"Unable to store content sharing to file %s.", contentSharingPolicyFile.getAbsolutePath()),
				e);
		 }
	}

	private void storeResourceQuotaPolicy(VraNgPolicyBase vraNgPolicy, File resourceQuotaPolicyFile) {
		throw new NotImplementedException("The method is not yet implemented");
	}

	private Map<PolicyType, List<String>> getPoliciesFromDescriptor() {
		VraNgPolicy policy = this.vraNgPackageDescriptor.getPolicy();
		if (policy == null) {
			return null;
		}

		Map<PolicyType, List<String>> policiesMap = new HashMap<>();
		policiesMap.put(PolicyType.CONTENT_SHARING_POLICY_TYPE, policy.getContentSharingPolicies());
		policiesMap.put(PolicyType.RESOURCE_QUOTA_POLICY_TYPE, policy.getResourceQuotaPolicies());
		policiesMap.put(PolicyType.LEASE_POLICY_TYPE, policy.getLeasePolicies());
		policiesMap.put(PolicyType.DAY_2_ACTION_POLICY_TYPE, policy.getDay2ActionsPolicies());
		policiesMap.put(PolicyType.APPROVAL_POLICY_TYPE, policy.getApprovalPolicies());
		policiesMap.put(PolicyType.DEPLOYMENT_LIMIT_POLICY_TYPE, policy.getDeploymentLimitPolicies());

		return policiesMap;
	}

	private List<PolicyFile> getPolicyFiles(Map<PolicyType, List<String>> policies, String sourcePath) {
		Map<PolicyType, String> policyTypesToSubDirs = VraNgDirs.getPolicySubDirs();

		List<PolicyFile> allPolicyFiles = new ArrayList<>();
		for(PolicyType policyType : policies.keySet()) {
			List<String> policyTypeDescriptionNames = policies.get(policyType);

			File policyTypeSubFolder = Paths
				.get(sourcePath,
					com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_POLICIES,
					policyTypesToSubDirs.get(policyType)) //How to get all policies?
				.toFile();

			// verify directory exists
			if (!policyTypeSubFolder.exists()) {
				logger.info("Content Sharing Policies directory not found!");

				continue;
			}

			File[] policyFiles = this.filterBasedOnConfiguration(policyTypeSubFolder,
				new CustomFolderFileFilter(policyTypeDescriptionNames));

			for(File file : policyFiles) {
				PolicyFile policyFile = new PolicyFile(file, policyType);
				allPolicyFiles.add(policyFile);
			}
		}

		return allPolicyFiles;
	}

	/**
	 * Converts a json catalog item file to VraNgContentSharingPolicy.
	 *
	 * @param jsonFile
	 *
	 * @return VraNgContentSharingPolicy
	 */
	private VraNgPolicyBase jsonFileToVraNgPolicy(final File jsonFile, PolicyType policyType) {
		logger.debug("Converting content sharing policy file to VraNgContentSharingPolicies. Name: '{}'",
			jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			switch (policyType) {
				case CONTENT_SHARING_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgContentSharingPolicy.class);
				case RESOURCE_QUOTA_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgResourceQuotaPolicy.class);
				case LEASE_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgLeasePolicy.class);
				case DAY_2_ACTION_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgDay2ActionsPolicy.class);
				case APPROVAL_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgApprovalPolicy.class);
				case DEPLOYMENT_LIMIT_POLICY_TYPE:
					return new Gson().fromJson(reader, VraNgDeploymentLimitPolicy.class);
				default:
					throw new IllegalArgumentException("Invalid policy type: " + policyType.getTypeValue());
			}
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Used while import to override the orgId and ProjectId for the receiving vRA instance.
	 *
	 * @param vraNgPolicy
	 */
	private void enrichPolicy(final VraNgPolicyBase vraNgPolicy) {
		String organizationId = VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId();
		vraNgPolicy.setOrgId(organizationId);
		vraNgPolicy.setProjectId(this.restClient.getProjectId());
	}
}
