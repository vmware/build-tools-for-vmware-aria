/* 
 * Package
 */
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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;

import org.apache.commons.io.IOUtils;

public class ResourceActionMockBuilder {
	/**
	 * Resource Action Id.
	 */
	private String	id;
	/**
	 * Resource Action Name.
	 */
	private String	name;
	/**
	 * Resource Action mockData.
	 */
	private JsonElement mockData;
	/**
	 * Resource Action resourceType.
	 */
	private String	resourceType;

	/**
	 * Create ResourceActionMockBuilder.
	 * @throws IOException exception
	 */
	public ResourceActionMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString(cl.getResourceAsStream("test/fixtures/resourceAction.json"), StandardCharsets.UTF_8);
			this.mockData = JsonParser.parseString(read);
			this.resourceType = this.mockData.getAsJsonObject().get("resourceType").getAsString();
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
	 * Set name for Resource Action.
	 * @param actionName name
	 * @return ResourceActionMockBuilder
	 */
	public ResourceActionMockBuilder setName(final String actionName) {
		this.name = actionName;
		return this;
	}

	/**
	 * Set ID for Resource Action.
	 * @param identifier Id
	 * @return ResourceActionMockBuilder
	 */
	public ResourceActionMockBuilder setId(final String identifier) {
		this.id = identifier;
		return this;
	}

	/**
	 * Set JSON property to VraNgResourceAction.
	 * @param key key
	 * @param value value
	 * @return ResourceActionMockBuilder
	 */
	public ResourceActionMockBuilder setPropertyInRawData(final String key, final String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if (customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}

	/**
	 * Build VraNgResourceAction.
	 * @return VraNgResourceAction
	 */
	public VraNgResourceAction build() {
		return new VraNgResourceAction(this.id, this.name, this.mockData.toString(), this.resourceType);
	}
}
