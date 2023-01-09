package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomResource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomResourceFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "custom-resources";

	public CustomResourceFsMocks(File tempDir) {
		super( tempDir );
	}

	@Override
	public File getWorkdir() {
		return Paths.get( this.tempDir.getPath(), WORKDIR ).toFile();
	}

	/**
	 * JSON encodes a CR item and adds it to the DIR_CUSTOM_RESOURCES.
	 * Adds the CR items dir if it does not exist
	 *
	 * @param	cr - The custom resource to add
	 */
	public void addCustomResource(VraNgCustomResource cr) {
		File crFile = Paths.get(
			this.getWorkdir().getAbsolutePath(),
			cr.getName() + ".json").toFile();

		Path crName = Paths.get(crFile.getPath());
		writeFileToPath(crName, cr.getJson().getBytes());
	}

}
