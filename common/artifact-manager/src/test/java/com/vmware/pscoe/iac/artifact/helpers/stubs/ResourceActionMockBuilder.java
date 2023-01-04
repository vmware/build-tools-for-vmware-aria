package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;

import org.apache.commons.io.IOUtils;

public class ResourceActionMockBuilder {
	private String	id;
	private String	name;
	private JsonElement mockData;

	public ResourceActionMockBuilder() throws IOException{
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString( cl.getResourceAsStream("test/fixtures/resourceAction.json"), StandardCharsets.UTF_8 );
			this.mockData = JsonParser.parseString(read);
		}
		catch (IOException ex) {
			throw ex;
		}
	}

	public ResourceActionMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public ResourceActionMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public ResourceActionMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if(customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}


	public VraNgResourceAction build() {
		return new VraNgResourceAction(this.id, this.name, this.mockData.toString());
	}
}
