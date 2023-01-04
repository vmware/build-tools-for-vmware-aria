package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlement;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public class EntitlementFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "entitlements";

	public EntitlementFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
	}

	/**
	 * JSON encodes a entitlement and adds it to the entitlements directory.
	 * This will also create the content.yaml based on the entitlement and alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @see    com.vmware.pscoe.iac.artifact.helpers.stubs.entitlementVersionsMockBuilder
	 * @param    entitlement - The entitlement to store
	 * @param    versionsData - A string containing the versioning data
	 */
	public void addEntitlement(VraNgCatalogEntitlement entitlement) {
		File file	= Paths.get(
			this.getWorkdir().getAbsolutePath(),
			entitlement.getName() + ".json"
		).toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, gson.toJson(entitlement).getBytes());
	}
}
