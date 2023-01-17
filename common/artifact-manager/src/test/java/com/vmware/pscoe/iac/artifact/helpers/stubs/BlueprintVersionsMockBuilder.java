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
