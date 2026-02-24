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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomResource;

public class CustomResourceFsMocks extends VraNgFsMock {
	private static final String WORKDIR = "custom-resources";

	public CustomResourceFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
	}

	/**
	 * JSON encodes a CR item and adds it to the DIR_CUSTOM_RESOURCES.
	 * Adds the CR items dir if it does not exist
	 *
	 * @param cr - The custom resource to add
	 */
	public void addCustomResource(VraNgCustomResource cr) {
		File crFile = Paths.get(
				this.getWorkdir().getAbsolutePath(),
				cr.getName() + ".json").toFile();

		Path crName = Paths.get(crFile.getPath());
		writeFileToPath(crName, cr.getJson().getBytes(StandardCharsets.UTF_8));
	}

}
