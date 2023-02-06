package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.*;

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

public class VraNgContentSharingPolicyStore extends AbstractVraNgStore {
	/**
	 * Suffix used for all of the resources saved by this store
	 */
	private static final String CUSTOM_RESOURCE_SUFFIX = ".json";
	/**
	 * Sub folder path for content sharing policy
	 */
	private static final String CONTENT_SHARING_POLICY = "content-sharing";
	private final Logger logger = LoggerFactory.getLogger(VraNgContentSharingPolicyStore.class);

	@Override
	public void importContent(File sourceDirectory) {

	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getContentSharingPolicy();
	}

	@Override
	protected void exportStoreContent() {
		List<VraNgContentSharingPolicy> contentSharingPolicies = this.restClient.getContentSharingPolicies();
		contentSharingPolicies.forEach(
				contentSharingPolicy -> storeContentSharingPolicyOnFilesystem(vraNgPackage, contentSharingPolicy));
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
	}

	/**
	 * Store content sharing policy in JSON file. 
	 *
	 * @param serverPackage vra package
	 * @param contentSharingPolicy   contentSharingPolicy representation
	 */
	private void storeContentSharingPolicyOnFilesystem(Package serverPackage, VraNgContentSharingPolicy contentSharingPolicy) {
		logger.debug("Storing contentSharingPolicy {}", contentSharingPolicy.getName());
		// Map<String, String> data = new LinkedHashMap<String, String>();
		// data.put("id", contentSharingPolicy.getId() != null ? contentSharingPolicy.getId() : "");
		// data.put("name", contentSharingPolicy.getName() != null ? contentSharingPolicy.getName() : "");
		// data.put("typeId", contentSharingPolicy.getTypeId() != null ? contentSharingPolicy.getTypeId() : "");
		// data.put("enforcementType",
		// 		contentSharingPolicy.getEnforcementType() != null ? contentSharingPolicy.getEnforcementType() : "");
		// data.put("orgId", contentSharingPolicy.getOrgId() != null ? contentSharingPolicy.getOrgId() : "");
		// data.put("projectId", contentSharingPolicy.getProjectId() != null ? contentSharingPolicy.getProjectId() : "");

		File store = new File(serverPackage.getFilesystemPath());
		File contentSharingPolicyFile = Paths.get(
				store.getPath(),
				DIR_CONTENT_SHARING_POLICIES,
				CONTENT_SHARING_POLICY,
				contentSharingPolicy.getName() + CUSTOM_RESOURCE_SUFFIX).toFile();

		if (!contentSharingPolicyFile.getParentFile().isDirectory()
				&& !contentSharingPolicyFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", contentSharingPolicyFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			// write content sharing file
			logger.info("Created content sharing file {}",
					Files.write(Paths.get(contentSharingPolicyFile.getPath()), gson.toJson(contentSharingPolicy).getBytes(),
							StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create content sharing {}", contentSharingPolicyFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store custom form to file %s.", contentSharingPolicyFile.getAbsolutePath()),
					e);
		}

	}
}
