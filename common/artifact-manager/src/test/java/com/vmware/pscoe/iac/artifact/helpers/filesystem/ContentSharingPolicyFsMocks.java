package com.vmware.pscoe.iac.artifact.helpers.filesystem;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ContentSharingPolicyFsMocks extends VraNgFsMock {
	private final static String DIR_CONTENT_SHARING_POLICIES = "policies";
	private final static String CONTENT_SHARING_POLICY = "content-sharing";


	public ContentSharingPolicyFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();
	}

	/**
	 * JSON encodes a resource action and adds it to the resource actions directory.
	 * This will also create the content.yaml based on the resource action and alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @see    com.vmware.pscoe.iac.artifact.helpers.stubs.resource actionVersionsMockBuilder
	 * @param    resourceAction - The resource action to store
	 * @param    versionsData - A string containing the versioning data
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
