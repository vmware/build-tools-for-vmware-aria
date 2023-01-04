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
