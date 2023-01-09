package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;

import org.apache.commons.io.IOUtils;

public class PropertyGroupMockBuilder {
	private JsonElement mockData;
	private String		id			= "mockedId";
	private	String		name		= "mockedPropertyGroup";

	public PropertyGroupMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString( cl.getResourceAsStream("test/fixtures/propertyGroup.json"), StandardCharsets.UTF_8 );;
			this.mockData = JsonParser.parseString(read);
		}
		catch (IOException ex) {
			throw ex;
		}
	}

	public PropertyGroupMockBuilder setName(String name){
		this.name = name;
		return this;
	}

	public PropertyGroupMockBuilder setId(String id){
		this.id = id;
		return this;
	}

	public PropertyGroupMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject propertyGroup = this.mockData.getAsJsonObject();
		if(propertyGroup.has(key)) {
			propertyGroup.remove(key);
			propertyGroup.addProperty(key, value);
		}
		this.mockData = propertyGroup.getAsJsonObject();
		return this;
	}

	public VraNgPropertyGroup build() {
		JsonObject propertyGroup = this.mockData.getAsJsonObject();

		if(!this.name.isEmpty()){
			this.setPropertyInRawData("name", this.name);
		}

		if(!this.id.isEmpty()){
			this.setPropertyInRawData("id", this.id);
		}
		
		return new VraNgPropertyGroup(this.name, this.id, propertyGroup.toString());
	}
}
