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
package com.vmware.pscoe.iac.artifact.helpers.stubs;

import com.google.gson.*;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlueprintVersionsMockBuilder {
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

	private final JsonElement mockData;
	private int versionsCount = 1;
	private VraNgBlueprint blueprint;

	public BlueprintVersionsMockBuilder(VraNgBlueprint blueprint) throws IOException {
		this.blueprint = blueprint;

		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString(cl.getResourceAsStream("test/fixtures/blueprintVersion.json"), StandardCharsets.UTF_8);
			this.mockData = JsonParser.parseString(read);
		} catch (IOException ex) {
			throw ex;
		}
	}

	public BlueprintVersionsMockBuilder setVersions(int versionsCount) {
		this.versionsCount = versionsCount;
		return this;
	}

	/**
	 * Return a string array with blueprint versions
	 *
	 * @return String
	 */
	public JsonArray build() {
		JsonObject baseVersion = this.mockData.getAsJsonObject();

		baseVersion.remove("blueprintId");
		baseVersion.addProperty("blueprintId", blueprint.getId());

		baseVersion.remove("name");
		baseVersion.addProperty("name", blueprint.getName());
		JsonArray versionsArray = new JsonArray();

		for (int i = 1; i <= this.versionsCount; i++) {
			JsonObject newVersion = baseVersion.deepCopy();

			newVersion.remove("version");
			newVersion.remove("id");
			newVersion.remove("createdAt");

			newVersion.addProperty("version", String.valueOf(i));
			newVersion.addProperty("id", String.valueOf(i));
			newVersion.addProperty("createdAt", new SimpleDateFormat(DATE_FORMAT).format(
				new Date(i*1000L)));

			versionsArray.add(newVersion);
		}

		return versionsArray;

	}
}
