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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import com.vmware.pscoe.iac.artifact.aria.automation.models.IVraNgPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class that unify the way Policies are imported, exported and deleted for all subclasses.
 */
public abstract class AbstractVraNgPolicyStore<T extends IVraNgPolicy> extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	static final String POLICY_BACKUP_SUFFIX = "_PL_TMP_BKUP";
	/** Gson utility */
	private static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();

	/** Policy type (see VraNgPolicyTypes) */
	protected final String policyType;
	/** Policy folder (relative to VraNgDirs.DIR_POLICIES) */
	protected final String policyDir;
	/** Policy type description - for logging and error handling */
	protected final String policyDesc;
	/** Policy class */
	protected final Class<T> policyClass;
	/**
	 * Abstract parent to all Policy Store classes
	 * 
	 * @param policyType   - policy type (see VraNgPolicyTypes)
	 * @param policyDir - subfolder of the policies directory (see VraNgDirs.DIR_POLICIES)
	 * @param policyDesc   - description - for logging/error handling
	 */
	protected AbstractVraNgPolicyStore(String policyType, String policyDir, String policyDesc, Class<T> policyClass) {
		this.policyType = policyType; 
		this.policyDir = policyDir;
		this.policyDesc = policyDesc;
		this.policyClass = policyClass;
	}

	/**
	 * Makes an API call to create a Policy. Throws RuntimeException on failure
	 * 
	 * @param policy - policy entry to create; will be updated with ID, etc. from API response
	 */
	protected abstract void createPolicy(T policy);

	/**
	 * Imports policies found in specified folder on server, according to filter
	 * specified in content.yml file.
	 * If there is a list of policy names - import only the specified policies.
	 * If there is no list, import everything.
	 * 
	 * @param sourceDirectory the folder where policy files are stored.
	 */
	@Override
	public void importContent(File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", VraNgDirs.DIR_POLICIES);
		File policyFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_POLICIES, policyDir)
				.toFile();

		if (!policyFolder.exists()) {
			logger.info("No {} policies available - skip import", policyDesc.toLowerCase());
			return;
		}

		File[] policyFiles = this.filterBasedOnConfiguration(policyFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));

		if (policyFiles != null && policyFiles.length == 0) {
			logger.info("No {} policies available - skip import", policyDesc.toLowerCase());
			return;
		}

		Map<String, T> policiesOnServer = this.fetchPolicies(this.getItemListFromDescriptor(), true);

		logger.info("Found {} Policies. Importing ...", policyDesc);
		for (File policyFile : policyFiles) {
			this.handlePolicyImport(policyFile, policiesOnServer);
		}
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty.
	 */
	@Override
	protected void exportStoreContent() {
		this.exportStoreContent(new ArrayList<>());
	}

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty.
	 * 
	 * @param itemNames list of names
	 * @throws RuntimeException if policy with given name cannot be found on the server
	 */
	@Override
	protected void exportStoreContent(final List<String> itemNames) {
		Path policyFolderPath = getPolicyFolderPath();
		Map<String, T> policies = this.fetchPolicies(itemNames, false);

		itemNames.forEach(name -> {
			if (!policies.containsKey(name)) {
				throw errorFrom(null, "%s policy with name: '%s' could not be found on the server.", policyDesc, name);
			}
		});

		policies.forEach((name, policy) -> {
			storePolicyOnFS(
					getPolicyFile(policyFolderPath, policy),
					policy);
		});
	}

	/**
	 * Deletes the resource quota policy by id.
	 *
	 * @param resId the id of the policy to delete.
	 */
	protected void deleteResourceById(String resId) {
		this.restClient.deletePolicy(resId);
	}

	/**
	 * Enriches Policy DTO with data to be (de)serialized depending on the operation
	 * type (export or import).
	 * 
	 * @param policyItem    policy that need to be enriched with resolved data.
	 * @param resolveByName a flag whether to resolve the data by name. In case
	 *                      import of policies the flag should be true otherwise
	 *                      it should be false.
	 */
	protected void resolvePolicyItem(T policyItem, boolean resolveByName) {
		// no-op
	}

	/**
	 * Returns a RuntimeException with formatted message
	 * 
	 * @param cause - cause (can be null)
	 * @param fmt   - formatting string
	 * @param args  - arguments for the formatting string
	 */
	protected RuntimeException errorFrom(Exception cause, String fmt, Object... args) {
		String msg = String.format(fmt, args);
		return cause != null ? new RuntimeException(msg, cause) : new RuntimeException(msg);
	}

	////////////////////////////////////////////////////
	// Helper methods
	////////////////////////////////////////////////////

	/**
	 * This will fetch all the policies that need to be exported.
	 *
	 * Will validate that the no duplicate policies exist on the
	 * environment.
	 *
	 * @param itemNames          - the list of policies to fetch. If empty, will fetch all
	 * @param throwOnBackupFound - if true, a Runtime Exception will be thrown if the fetched policies contain a backup.
	 *                             Otherwise backups will be excluded
	 * 
	 * @return Map with keys - policy names and values - the policy objects
	 */
	private Map<String, T> fetchPolicies(final List<String> itemNames, boolean throwOnBackupFound) {
		Map<String, T> policies = new HashMap<>();
		boolean includeAll = itemNames.size() == 0;

		this.<T>getAllServerContents().forEach(policy -> {
			if (policy.getTypeId().equals(policyType)
					&& (includeAll || itemNames.contains(policy.getName()))) {
				if (policies.containsKey(policy.getName())) {
					throw errorFrom(null, "More than one %s policy with name '%s' already exists. While Aria Automation supports policies "
						+ "with the same type and name, Build Tools for Aria does not support duplicate policy names of the same type "
						+ "in order to properly resolve the desired policy.", policyDesc.toLowerCase(), policy.getName());
				}
				if (!policy.getName().contains(POLICY_BACKUP_SUFFIX)) {
					logger.debug("Found policy: {}", policy.getName());
					policies.put(policy.getName(), policy);
				} else if (!throwOnBackupFound) {
					logger.debug("Skipping policy backup: {}", policy.getName());
				} else {
					throw errorFrom(null, "%s Policy update is in progress, found backup on server: '%s'", policyDesc, policy.getName());
				}
			}
		});

		return policies;
	}

	/**
	 * Imports policy file to server, replacing the organization id.
	 *
	 * NOTE: The `projectId` is only set if it originally existed in the policy. The
	 * API will not return a `projectId` if the policy is organization scoped
	 *
	 * @param policyFile       the policy to import.
	 * @param policiesOnServer all the policies currently on the server
	 */
	private void handlePolicyImport(final File policyFile, Map<String, T> policiesOnServer) {
		T policy = policyFromJsonFile(policyFile);
		logger.info("Attempting to import {} policy '{}' from file '{}'",
				policyDesc.toLowerCase(),
				policy.getName(),
				policyFile.getName());

		populatePolicyDetails(policy);

		// prepare backup for existing policy and delete it
		T existingPolicy = policiesOnServer.get(policy.getName());
		boolean isNew = existingPolicy == null;
		T backupPolicy = createExistingPolicyBackup(existingPolicy);
		if (!isNew) {
			try {
				this.logger.warn("Backup '{}' created with ID='{}'. Attempting to delete {} policy '{}' before recreating it.",
				backupPolicy.getName(), backupPolicy.getId(), policyDesc, policy.getName());
				deleteResourceById(existingPolicy.getId());
			} catch (Exception e) {
				deleteExistingPolicyBackup(backupPolicy);
				throw errorFrom(e, "Failed to delete previous version of %s policy '%s'", policyDesc, existingPolicy.getName());
			}
		}

		// Save the new/updated policy
		try {
			this.logger.info("Attempting to {}create {} policy '{}'", isNew ? "" : "re-", policyDesc.toLowerCase(), policy.getName());
			createPolicy(policy);
			logger.info("Successfully {}created {} Policy '{}', ID='{}'", isNew ? "" : "re-", policyDesc, policy.getName(), policy.getId());
			deleteExistingPolicyBackup(backupPolicy);
		} catch (Exception e) {
			if (backupPolicy == null) {
				throw errorFrom(e, "Failed to create %s Policy '%s'", policyDesc, policy.getName());
			}
			// attempt to restore
			String restoreDetails = restorePolicy(existingPolicy, backupPolicy);
			throw errorFrom(e, "Failed to update %s Policy '%s' - %s.", policyDesc, existingPolicy.getName(), restoreDetails);
		}
	}

	/**
	 * Extract policies sub-folder absolute file system path.
	 * 
	 * @return the sub-folder absolute file system path.
	 */
	private Path getPolicyFolderPath() {
		File store = new File(vraNgPackage.getFilesystemPath());

		Path policyFolderPath = Paths.get(store.getPath(), VraNgDirs.DIR_POLICIES, policyDir);
		if (!policyFolderPath.toFile().isDirectory() && !policyFolderPath.toFile().mkdirs()) {
			logger.warn("Could not create folder: {}", policyFolderPath.getFileName());
		}

		return policyFolderPath;
	}

	/**
	 * Store policy in JSON file.
	 *
	 * @param policyFile policy file
	 * @param policy     policy representation
	 */
	private void storePolicyOnFS(final File policyFile, final T policy) {
		String policiesDesc = policyDesc.toLowerCase();
		logger.debug("Storing {} policy {}", policiesDesc, policy.getName());
		this.resolvePolicyItem(policy, false);
		try {
			JsonObject policyJsonObject = policyToJsonObject(policy);

			logger.info("Created {} policy file {}",
					policiesDesc,
					Files.write(
							Paths.get(policyFile.getPath()),
							GSON.toJson(policyJsonObject).getBytes(),
							StandardOpenOption.CREATE));
		} catch (IOException e) {
			throw errorFrom(e, "Unable to store %s policy to file %s.", policiesDesc, policyFile.getAbsolutePath());
		}
	}

	/**
	 * @param policyFolderPath the correct subfolder path for the policy type.
	 * @param policy           the policy that is exported.
	 *
	 * @return the file where to store the policy.
	 */

	private File getPolicyFile(Path policyFolderPath, T policy) {
		String filename = policy.getName();
		if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
			throw new IllegalArgumentException("Invalid filename " + filename);
		}

		return Paths.get(
				String.valueOf(policyFolderPath),
				filename + CUSTOM_RESOURCE_SUFFIX).toFile();
	}

	/**
	 * Converts a json catalog item file to a Policy object.
	 *
	 * @param jsonFile - JSON file from which to extract the policy object
	 *
	 * @return T
	 */
	private T policyFromJsonFile(final File jsonFile) {
		logger.debug("Converting {} policy file to {}. Name: '{}'",
				policyDesc.toLowerCase(), policyClass.getSimpleName(), jsonFile.getName());

		try {
			JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()));
			return GSON.fromJson(reader, policyClass);
		} catch (IOException e) {
			throw errorFrom(e, "Error reading from file: %s", jsonFile.getPath());
		} catch (JsonIOException | JsonSyntaxException e) {
			throw errorFrom(e, "Error parsing file: %s", jsonFile.getPath());
		} catch (Exception e) {
			throw errorFrom(e, "General error processing file file: %s", jsonFile.getPath());
		}
	}

	/**
	 * Populates the policy with projectId, orgId and other details (see resolvePolicyItem)
	 * @param policy - policy to populate
	 */
	private void populatePolicyDetails(T policy) {
		if (policy.getProjectId() != null && !policy.getProjectId().isBlank()) {
			policy.setProjectId(this.restClient.getProjectId());
		}

		this.resolvePolicyItem(policy, true);

		policy.setOrgId(VraNgOrganizationUtil.getOrganization(this.restClient, this.config).getId());
	}

	/**
	 * Creates on the server a backup of the policy with name, suffixed by
	 * POLICY_BACKUP_SUFFIX. Deletes the original.
	 * 
	 * @param existingPolicy - policy to backup
	 * @return null, if existingPolicy is not provided.
	 *         Backup of the policy (expected to have an ID) otherwise.
	 * @throws RuntimeException if backup creation fails
	 */
	private T createExistingPolicyBackup(T existingPolicy) {
		if (existingPolicy == null) {
			return null;
		}
		String backupName = existingPolicy.getName() + POLICY_BACKUP_SUFFIX;
		this.logger.debug(
				"{} policy '{}' already exists on the server. Attempting to store backup '{}'",
				policyDesc, backupName);
		try {
			T backupPolicy = GSON.fromJson(policyToJsonObject(existingPolicy), policyClass); // sanitized copy
			backupPolicy.setName(backupName);
			// set orgId?
			this.createPolicy(backupPolicy);
			return backupPolicy;
		} catch (Exception e) {
			throw errorFrom(e, "Failed to create backup for %s policy '%s'.", policyDesc, existingPolicy.getName());
		}
	}

	/**
	 * Deletes the backup policy; logs a warning on failure (does not throw)
	 * If a policy isn't provided, does nothing.
	 * @param existingPolicyBackup - policy backup
	 */
	private void deleteExistingPolicyBackup(T existingPolicyBackup) {
		if (existingPolicyBackup != null) {
			try {
				deleteResourceById(existingPolicyBackup.getId());
			} catch (Exception e2) {
				logger.warn("Failed to delete backup for {} Policy: '{}'", policyDesc, existingPolicyBackup.getName());
			}
		}
	}

	/**
	 * Restores original policy on failure to update.
	 * 
	 * @param policy               - original policy to restore
	 * @param existingPolicyBackup - backup; will be deleted on success
	 * @return "restored from backup" or "backup is available for manual restore:..."
	 */
	private String restorePolicy(T policy, T existingPolicyBackup) {
		String backupDetails = "restored from backup";
		try {
			createPolicy(policy);
			deleteExistingPolicyBackup(existingPolicyBackup);
		} catch (Exception e) {
			backupDetails = String.format("backup is available for manual restore: '%s'",
					existingPolicyBackup.getName());
		}
		return backupDetails;
	}

	/**
	 * Converts a policy to JsonObject for serialization/comparison
	 * 
	 * @param policy - policy
	 * @return JsonObject, sanitized (see sanitizePolicy)
	 */
	private JsonObject policyToJsonObject(T policy) {
		JsonObject policyJsonObject = GSON.fromJson(GSON.toJson(policy), JsonObject.class);
		return sanitizePolicy(policyJsonObject);
	}

	/**
	 * Sanitizes the given JSON object policy by removing properties that should not
	 * be stored.
	 *
	 * The `projectId` must not be removed, as it controls whether a policy is
	 * project scoped or not
	 *
	 * @param policy - the policy to sanitize
	 */
	private JsonObject sanitizePolicy(JsonObject policy) {
		policy.remove("orgId");
		policy.remove("createdBy");
		policy.remove("createdAt");
		policy.remove("lastUpdatedBy");
		policy.remove("lastUpdatedAt");
		policy.remove("id");
		return policy;
	}
}
