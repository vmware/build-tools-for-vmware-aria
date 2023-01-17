package com.vmware.pscoe.iac.artifact.helpers.stubs;

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
