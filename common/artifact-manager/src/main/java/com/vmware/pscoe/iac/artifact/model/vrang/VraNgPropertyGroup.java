package com.vmware.pscoe.iac.artifact.model.vrang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Holds raw data for the property group and name to use as an identifier
 */
public class VraNgPropertyGroup {
	private final String name;
	private String id;
	private String rawData;

	public VraNgPropertyGroup( String name, String id, String rawData ) {
		this.name		= name;
		this.id			= id;
		this.rawData	= rawData;
	}

	/**
	 * @return	String
	 */
	public String getRawData() {
		return rawData;
	}

	/**
	 * Modifies the rawData by setting the projectId identifier
	 *
	 * @param projectId
	 */
	public void setProjectIdInRawData(String projectId) {
		this.modifyRawData("projectId", projectId);
	}

	/**
	 * Modifies the rawData properties
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

	public void setId(String id) {
		this.id = id;

		this.modifyRawData( "id", id );
	}

	public boolean hasTheSameProjectId(String value) {
		JsonObject rawDataObj = this.getRawDataAsJsonObject();

		if (rawDataObj.has("projectId")) {
				String rawDataProjectId = rawDataObj.get("projectId").getAsString();
				if (rawDataProjectId == value) {
					return true;
				}
				return false;
		}
		return true;
	}

	private JsonObject getRawDataAsJsonObject() {
		JsonElement rawData = JsonParser.parseString(this.rawData);
		JsonObject rawDataObj = rawData.getAsJsonObject();
		return rawDataObj;
	}
}
