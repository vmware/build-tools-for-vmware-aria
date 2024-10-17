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
package com.vmware.pscoe.iac.artifact.model.vrang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Holds raw data for the property group and name to use as an identifier.
 */
public class VraNgPropertyGroup implements Identifiable {

	/**
	 * Project Id JSON key.
	 */
	private static final String PROJECT_ID_KEY = "projectId";
	/**
	 * Organisation Id JSON key.
	 */
	private static final String ORG_ID_KEY = "orgId";
	/**
	 * Property group Name.
	 */
	private final String name;
	/**
	 * Property group Id.
	 */
	private String id;
	/**
	 * Property group raw JSON Data as String.
	 */
	private String rawData;
	/**
	 * Property group projectId if any.
	 */
	private String projectId;
	/**
	 * Property group organizationId.
	 */
	private String orgId;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param id
	 * @param rawData
	 */
	public VraNgPropertyGroup(String name, String id, String rawData) {
		this.name = name;
		this.id = id;
		this.rawData = rawData;
		JsonObject rawDataObj = this.getRawDataAsJsonObject();
		this.projectId = rawDataObj.has(PROJECT_ID_KEY) ? rawDataObj.get(PROJECT_ID_KEY).getAsString() : "";
		this.orgId = rawDataObj.has(ORG_ID_KEY) ? rawDataObj.get(ORG_ID_KEY).getAsString() : "";
	}

	/**
	 * Returns the property group's JSON definition as string.
	 * 
	 * @return String
	 */
	public String getRawData() {
		return rawData;
	}

	/**
	 * Modifies the rawData by setting the orgId identifier.
	 * 
	 * @param value organizaion uuid
	 */
	private void setOrgIdInRawData(String value) {
		this.modifyRawData(ORG_ID_KEY, value);
	}

	/**
	 * Modifies the rawData properties.
	 * 
	 * @param key   key
	 * @param value value
	 */
	private void modifyRawData(String key, String value) {
		JsonObject rawDataObj = this.getRawDataAsJsonObject();

		if (rawDataObj.has(key)) {
			rawDataObj.remove(key);
		}
		rawDataObj.addProperty(key, value);

		this.rawData = rawDataObj.toString();
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the Id.
	 * 
	 * @param value
	 */
	public void setId(String value) {
		this.id = value;

		this.modifyRawData("id", value);
	}

	/**
	 * @return String
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Sets the organization id.
	 * 
	 * @param value
	 */
	public void setOrgId(String value) {
		this.orgId = value;

		this.modifyRawData(ORG_ID_KEY, value);
	}

	/**
	 * @return String
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Sets the project id.
	 * 
	 * @param value
	 */
	public void setProjectId(String value) {
		this.projectId = value;

		this.modifyRawData(PROJECT_ID_KEY, value);
	}

	/**
	 * Checks property group's organizaion id against the input value.
	 * 
	 * @param value
	 * @return true if the organization id matches the input.
	 */
	public boolean hasTheSameOrgId(String value) {
		JsonObject rawDataObj = this.getRawDataAsJsonObject();

		if (rawDataObj.has(ORG_ID_KEY)) {
			String rawDataProjectId = rawDataObj.get(ORG_ID_KEY).getAsString();
			return rawDataProjectId.equals(value);
		}
		return false;
	}

	/**
	 * Returns the property group's definition as a JsonObject instance.
	 * 
	 * @return property group definition as a JsonObject.
	 */
	private JsonObject getRawDataAsJsonObject() {
		JsonElement rawDataElement = JsonParser.parseString(this.rawData);
		return rawDataElement.getAsJsonObject();
	}
}
