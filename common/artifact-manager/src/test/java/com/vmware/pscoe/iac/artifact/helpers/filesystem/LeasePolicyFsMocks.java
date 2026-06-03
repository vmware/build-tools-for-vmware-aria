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
package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;

public class LeasePolicyFsMocks extends VraNgFsMock {
	/**
	 * Policies folder name constant.
	 */
	private static final String DIR_POLICIES = "policies";
	/**
	 * Day2-actions sub-folder name constant.
	 */
	private static final String LEASE_POLICY = "lease";

	/**
	 * Constructor.
	 * 
	 * @param tempDir temporary folder for test data.
	 */

	public LeasePolicyFsMocks(File tempDir) {
		super(tempDir);
	}

	/**
	 * Getter.
	 * 
	 * @return the full path where test policy json files are stored during tests.
	 */
	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), DIR_POLICIES, LEASE_POLICY).toFile();
	}

	/**
	 * JSON encodes a resource action and adds it to the resource actions directory.
	 * This will also create the content.yaml based on the resource action and
	 * alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @param policy - The policy to store
	 */
	public void addPolicy(VraNgLeasePolicy policy) {
		File file = Paths.get(
				this.getWorkdir().getAbsolutePath(),
				policy.getName() + ".json").toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, gson.toJson(policy).getBytes(StandardCharsets.UTF_8));
	}
}
