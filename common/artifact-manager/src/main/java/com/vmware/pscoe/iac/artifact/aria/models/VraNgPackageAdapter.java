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
package com.vmware.pscoe.iac.artifact.aria.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmware.pscoe.iac.artifact.model.Package;

/**
 * An adapter used to extract information from a package.
 */
public class VraNgPackageAdapter {
	/**
	 * @param pkg the package to adapt
	 */
	private Package pkg;

	/**
	 * @param pkg the package to adapt
	 */
	public VraNgPackageAdapter(final Package pkg) {
		this.pkg = pkg;
	}

	/**
	 * This will return a descriptor for the given package.
	 *
	 * The descriptor class is used to extract what kind of data is stored in the
	 * `content.yaml`
	 *
	 * @return the descriptor
	 * @throws IOException if the content.yaml file is not found or not readable
	 */
	public VraNgPackageDescriptor getDescriptor() throws IOException {
		Path packagePath = Paths.get(this.pkg.getFilesystemPath());
		Path parentDir = packagePath.getParent();
		Path contentYamlPath = parentDir.resolve("content.yaml");

		if (!Files.exists(contentYamlPath) || !Files.isReadable(contentYamlPath)) {
			throw new IOException("content.yaml file is not found or not readable at " + contentYamlPath);
		}

		return VraNgPackageDescriptor.getInstance(contentYamlPath.toFile());
	}
}
