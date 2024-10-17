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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Abstract class to be implemented by the other FS Mocks.
 */
abstract public class VraNgFsMock {
	protected final File tempDir;

	public VraNgFsMock(File tempDir) {
		this.tempDir = tempDir;

		this.ensureWorkdirExists();
	}

	/**
	 * Ensures the workdir exists. Will throw an error if the workdir cannot be created
	 */
	protected void ensureWorkdirExists() {
		File workdir = this.getWorkdir();

		if (workdir.exists()) {
			return;
		}

		if (!workdir.mkdirs()) {
			throw new RuntimeException("Error while creating " + workdir.getAbsolutePath() + " dir");
		}
	}

	/**
	 * Writes a given byte array body to path
	 *
	 * @param    path - the absolute path to the file ( with filename )
	 * @param    body - body to save
	 */
	protected void writeFileToPath(Path path, byte[] body) {
		try {
			Files.write(
				path,
				body,
				StandardOpenOption.CREATE
			);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error while creating \"%s\". Message was: %s", path, e.getMessage()));
		}
	}

	/**
	 * Returns the work sub dir for the current type
	 *
	 * @return File
	 */
	public abstract File getWorkdir();
}
