package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertyGroupFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "property-groups";

	public PropertyGroupFsMocks(File tempDir) {
		super( tempDir );
	}

	@Override
	public File getWorkdir() {
		return Paths.get( this.tempDir.getPath(), WORKDIR ).toFile();
	}

	/**
	 * Adds the property group dir if it does not exist
	 *
	 * @param	propertyGroup - The Property group to add
	 */
	public void addPropertyGroup( VraNgPropertyGroup propertyGroup ) {
		File customPropertyGroupFile	= Paths.get(
			this.getWorkdir().getAbsolutePath(),
			propertyGroup.getName() + ".json"
		).toFile();

		Gson gson		= new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName	= Paths.get( customPropertyGroupFile.getPath() );
		writeFileToPath( itemName, gson.toJson( propertyGroup ).getBytes() );
	}
}
