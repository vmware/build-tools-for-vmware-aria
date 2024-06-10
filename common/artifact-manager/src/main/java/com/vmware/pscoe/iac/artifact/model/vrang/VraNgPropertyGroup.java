package com.vmware.pscoe.iac.artifact.model.vrang;

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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Holds raw data for the property group and name to use as an identifier.
 */
public class VraNgPropertyGroup {
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
	 * @param name
	 * @param id
	 * @param rawData
	 */
	public VraNgPropertyGroup( String name, String id, String rawData ) {
		this.name		= name;
		this.id			= id;
		this.rawData	= rawData;
		JsonObject rawDataObj = this.getRawDataAsJsonObject();
		this.projectId = rawDataObj.has("projectId")? rawDataObj.get("projectId").getAsString():"";
		this.orgId =  rawDataObj.has("orgId")? rawDataObj.get("orgId").getAsString():"";
	}

	/**
	 * Returns the property group's JSON definition as string.
	 * @return
	 */
	public String getRawData() {
		return rawData;
	}

	/**
	 * Modifies the rawData by setting the orgId identifier.
	 *
	 * @param orgId organizaion uuid
	 */
	public void setOrgIdInRawData(String orgId) {
		this.modifyRawData("orgId", orgId);
	}

	/**
	 * Modifies the rawData properties.
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
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;

		this.modifyRawData( "id", id );
	}
	/**
	 * @return String
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Sets the organization id.
	 * @param id
	 */
	public void setOrgId(String id) {
		this.orgId = id;

		this.modifyRawData( "orgId", id );
	}
	/**
	 * @return String
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Sets the project id.
	 * @param id
	 */
	public void setProjectId(String id) {
		this.projectId = id;

		this.modifyRawData( "projectId", id );
	}

	/**
	 * Checks property group's organizaion id against the input value.
	 * @param value
	 * @return
	 */
	public boolean hasTheSameOrgId(String value) {
		JsonObject rawDataObj = this.getRawDataAsJsonObject();

		if (rawDataObj.has("orgId")) {
			String rawDataProjectId = rawDataObj.get("orgId").getAsString();
			return rawDataProjectId.equals(value);
		}
		return false;
	}

	/**
	 * Returns the property group's definition as a JsonObject instance.
	 * @return
	 */
	private JsonObject getRawDataAsJsonObject() {
		JsonElement rawData = JsonParser.parseString(this.rawData);
		JsonObject rawDataObj = rawData.getAsJsonObject();
		return rawDataObj;
	}
}
