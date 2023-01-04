package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;

import org.apache.commons.io.IOUtils;

public class FlavorMappingMockBuilder {
	private JsonElement mockData;
	private String name = "mockedFlavorMappingName";

	public FlavorMappingMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString( cl.getResourceAsStream("test/fixtures/flavorMapping.json"), StandardCharsets.UTF_8 );
			this.mockData = JsonParser.parseString(read);
		}
		catch (IOException ex) {
			throw ex;
		}
	}

	public FlavorMappingMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public FlavorMappingMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if(customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}

	public VraNgFlavorMapping build() {
		return new VraNgFlavorMapping(this.name, this.mockData.toString());
	}
	
}
