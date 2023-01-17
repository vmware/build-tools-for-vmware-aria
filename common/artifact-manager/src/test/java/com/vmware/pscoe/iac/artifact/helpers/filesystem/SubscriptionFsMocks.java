package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSubscription;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public class SubscriptionFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "subscription";

	public SubscriptionFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
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
	public void addSubscription(VraNgSubscription subscription) {
		File file = Paths.get(
			this.getWorkdir().getAbsolutePath(),
			subscription.getName() + ".json"
		).toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, gson.toJson(subscription).getBytes());
	}
}
