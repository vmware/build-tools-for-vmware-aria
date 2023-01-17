package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;

import java.io.File;
import java.nio.file.Paths;

public class BlueprintFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "blueprints";

	public BlueprintFsMocks(File tempDir) {
		super(tempDir);
	}

	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
	}

	/**
	 * @param blueprint
	 * @see BlueprintFsMocks#addBlueprint(VraNgBlueprint, String)
	 */
	public void addBlueprint(VraNgBlueprint blueprint) {
		this.addBlueprint(blueprint, null);
	}

	/**
	 * JSON encodes a blueprint and adds it to the blueprints directory.
	 * This will also create the content.yaml based on the blueprint and alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @see    com.vmware.pscoe.iac.artifact.helpers.stubs.BlueprintVersionsMockBuilder
	 * @param    blueprint - The blueprint to store
	 * @param    versionsData - A string containing the versioning data
	 */
	public void addBlueprint(VraNgBlueprint blueprint, String versionsData) {
		File blueprintFolder = Paths.get(this.getWorkdir().getAbsolutePath(),
			blueprint.getName()).toFile();

		if (!blueprintFolder.exists()) {
			if (!blueprintFolder.mkdirs()) {
				throw new RuntimeException("Error while creating " + blueprintFolder.getAbsolutePath() + " dir");
			}
		}

		File blueprintContent = Paths.get(
			blueprintFolder.getAbsolutePath(),
			"content.yaml"
		).toFile();

		File blueprintDetails = Paths.get(
			blueprintFolder.getAbsolutePath(),
			"details.json"
		).toFile();

		File blueprintVersions = Paths.get(
			blueprintFolder.getAbsolutePath(),
			"versions.json"
		).toFile();

		JsonObject bpDetails = new JsonObject();
		bpDetails.add("id", new JsonPrimitive(blueprint.getId()));
		bpDetails.add("name", new JsonPrimitive(blueprint.getName()));
		bpDetails.add("description", new JsonPrimitive(blueprint.getDescription()));
		bpDetails.add("requestScopeOrg", new JsonPrimitive(blueprint.getRequestScopeOrg()));
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

		writeFileToPath(
			Paths.get(blueprintContent.getPath()),
			blueprint.getContent().getBytes()
		);

		writeFileToPath(
			Paths.get(blueprintDetails.getPath()),
			gson.toJson(gson.fromJson(bpDetails.toString(), JsonObject.class)).getBytes()
		);

		if ( versionsData != null ){
			writeFileToPath(
				Paths.get(blueprintVersions.getPath()),
				versionsData.getBytes()
			);
		}
	}
}
