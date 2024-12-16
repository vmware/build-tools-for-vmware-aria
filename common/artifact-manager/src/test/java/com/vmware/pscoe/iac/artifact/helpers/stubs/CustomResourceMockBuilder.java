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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgCustomResource;
import org.apache.commons.io.IOUtils;


public class CustomResourceMockBuilder {
	private JsonElement mockData;
	private String id = "2b098c4b-6ebe-4976-9129-dd0504d6bdf6";
	private String name = "Avi Load Balancer L3DSR";
	private String orgId = "3c012c2b-6ebe-4976-9129-dd0504d6bdf7";

	private JsonArray additionalActions = null;

	private boolean withId = true;
	private boolean removeFormDefinitionIds = false;

	public CustomResourceMockBuilder() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		try {
			String read = IOUtils.toString(cl.getResourceAsStream("test/fixtures/customResource.json"), StandardCharsets.UTF_8);
			this.mockData = JsonParser.parseString(read);
		} catch (IOException ex) {
			throw ex;
		}
	}

	public CustomResourceMockBuilder withoutIdInRawData() {
		this.withId = false;
		return this;
	}

	public CustomResourceMockBuilder setOrgId(String orgId) {
		this.orgId = orgId;
		return this;
	}

	public CustomResourceMockBuilder removeFormDefinitionIds() {
		this.removeFormDefinitionIds = true;
		return this;
	}

	/**
	 * This will overwrite other methods ( like setOrgId )
	 */
	public CustomResourceMockBuilder setAdditionalActions(JsonArray additionalActions) {
		this.additionalActions = additionalActions;
		return this;
	}

	public CustomResourceMockBuilder setDisplayNameInRawData(String name) {
		this.name = name;
		return this;
	}

	public CustomResourceMockBuilder setPropertyInRawData(String key, String value) {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if (customResource.has(key)) {
			customResource.remove(key);
			customResource.addProperty(key, value);
		}
		this.mockData = customResource.getAsJsonObject();
		return this;
	}

	public VraNgCustomResource build() {
		JsonObject customResource = this.mockData.getAsJsonObject();
		if (!this.withId) {
			customResource.remove("id");
		}

		if (!this.name.isEmpty()) {
			customResource.remove("displayName");
			customResource.addProperty("displayName", this.name);
		}

		if (this.orgId != null) {
			JsonArray additionalActionsArray = customResource.get("additionalActions").getAsJsonArray();
			additionalActionsArray.forEach(action -> {
				if (action != null) {
					JsonObject actionJson = action.getAsJsonObject();
					this.fixOrgId(actionJson, "orgId");
					if (actionJson.get("formDefinition") != null) {
						JsonObject formDefinition = actionJson.get("formDefinition").getAsJsonObject();

						this.fixOrgId(formDefinition, "tenant");
					}
				}
			});

			this.fixOrgId(customResource, "orgId");
		}

		if (this.removeFormDefinitionIds) {
			JsonArray additionalActionsArray = customResource.get("additionalActions").getAsJsonArray();
			additionalActionsArray.forEach(action -> {
				if (action != null) {
					JsonObject actionJson = action.getAsJsonObject();
					if (actionJson.get("formDefinition") != null) {
						JsonObject formDefinition = actionJson.get("formDefinition").getAsJsonObject();
						formDefinition.remove("id");
					}
				}
			});
		}

		if (this.additionalActions != null) {
			customResource.remove("additionalActions");
			customResource.add("additionalActions", this.additionalActions);
		}

		return new VraNgCustomResource(this.id, this.name, customResource.toString());
	}

	/**
	 * Fixes the organization id / tenant id in the given object with the one set in the configuration
	 */
	private void fixOrgId(final JsonObject jsonObject, String key) {
		if (jsonObject.has(key) && this.orgId != null) {
			jsonObject.remove(key);
			jsonObject.addProperty(key, this.orgId);
		}
	}
}
