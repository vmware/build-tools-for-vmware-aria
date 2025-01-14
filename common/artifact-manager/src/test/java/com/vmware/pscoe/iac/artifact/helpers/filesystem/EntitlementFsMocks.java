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
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogEntitlement;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public class EntitlementFsMocks extends VraNgFsMock {
	private static final String WORKDIR = "entitlements";

	public EntitlementFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
	}

	/**
	 * JSON encodes a entitlement and adds it to the entitlements directory.
	 * This will also create the content.yaml based on the entitlement and
	 * alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @see com.vmware.pscoe.iac.artifact.helpers.stubs.entitlementVersionsMockBuilder
	 * @param entitlement  - The entitlement to store
	 * @param versionsData - A string containing the versioning data
	 */
	public void addEntitlement(VraNgCatalogEntitlement entitlement) {
		File file = Paths.get(
				this.getWorkdir().getAbsolutePath(),
				entitlement.getName() + ".json").toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, gson.toJson(entitlement).getBytes());
	}
}
