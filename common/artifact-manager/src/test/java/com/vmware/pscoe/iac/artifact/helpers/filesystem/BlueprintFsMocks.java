package com.vmware.pscoe.iac.artifact.helpers.filesystem;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;

import java.io.File;
import java.nio.file.Paths;

public class BlueprintFsMocks extends VraNgFsMock {
	/**
	 * WORKDIR is defined with value- blueprints.
	 * 
	 * @param WORKDIR
	 */
	private static final String WORKDIR = "blueprints";

	/**
	 * Contructor to initialize base class.
	 * 
	 * @param tempDir
	 */
	public BlueprintFsMocks(File tempDir) {
		super(tempDir);
	}

	
	/** 
	 * @return File
	 */
	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), WORKDIR).toFile();
	}


	/**
	 * JSON encodes a blueprint and adds it to the blueprints directory.
	 * This will also create the content.yaml based on the blueprint and alternatively accepts a versions' data containing
	 * information about the versions.
	 *
	 * @param    blueprint - The blueprint to store
	 */
	public void addBlueprint(final VraNgBlueprint blueprint) {
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
	}
}
