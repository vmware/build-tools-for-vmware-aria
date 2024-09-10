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
package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public final class ContentSharingPolicyFsMocks extends VraNgFsMock {
	/**
	 * Policies folder name constant.
	 */
	private static final String DIR_CONTENT_SHARING_POLICIES = "policies";
	/**
	 * Content-sharing sub-folder name constant.
	 */
	private static final String CONTENT_SHARING_POLICY = "content-sharing";

	/**
	 * Constructor.
	 * @param tempDir a folder to store temporary test data
	 */
	public ContentSharingPolicyFsMocks(File tempDir) {
		super(tempDir);
	}

	/**
	 * Calculates working directory path.
	 * @return the working directory folder as a File object.
	 */
	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();
	}

	/**
	 * JSON encodes a content sharing policy and adds it to the policies/content-sharing directory.
	 * @param    csPolicy - The resource action to store
	 */
	public void addContentSharingPolicy(VraNgContentSharingPolicy csPolicy) {
		File file = Paths.get(
			this.getWorkdir().getAbsolutePath(),
			csPolicy.getName() + ".json"
		).toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, gson.toJson(csPolicy).getBytes());
	}
}
