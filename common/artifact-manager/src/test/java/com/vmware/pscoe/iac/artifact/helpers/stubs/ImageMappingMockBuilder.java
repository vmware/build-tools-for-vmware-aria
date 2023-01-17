package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgImageMapping;

import org.apache.commons.io.IOUtils;

public class ImageMappingMockBuilder {
	private JsonElement mockData;
	private String name = "mockedImageMappingName";

	public ImageMappingMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString( cl.getResourceAsStream("test/fixtures/imageMapping.json"), StandardCharsets.UTF_8 );;
			this.mockData = JsonParser.parseString(read);
		}
		catch (IOException ex) {
			throw ex;
		}
	}

	public ImageMappingMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public ImageMappingMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if(customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}

	public VraNgImageMapping build() {
		return new VraNgImageMapping(this.name, this.mockData.toString());
	}
	
}
