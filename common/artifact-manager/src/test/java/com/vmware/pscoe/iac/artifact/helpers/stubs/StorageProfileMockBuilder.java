package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgStorageProfile;

import org.apache.commons.io.IOUtils;

public class StorageProfileMockBuilder {
	private JsonElement mockData;
	private String name = "mockedStorageProfileMName";

	public StorageProfileMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString(cl.getResourceAsStream("test/fixtures/storageProfile.json"),
					StandardCharsets.UTF_8);
			this.mockData = JsonParser.parseString(read);
		} catch (IOException ex) {
			throw ex;
		}
	}

	public StorageProfileMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public StorageProfileMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if (customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}

	public VraNgStorageProfile build() {
		return new VraNgStorageProfile(this.name, this.mockData.toString());
	}

}
