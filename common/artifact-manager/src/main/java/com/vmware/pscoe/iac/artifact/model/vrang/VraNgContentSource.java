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

import java.util.List;
import java.util.Map;

/**
 * VraNgContentSource.
 */
public class VraNgContentSource extends VraNgContentSourceBase {
	/**
	 * The project Id.
	 */
	protected String projectId;

	/**
	 * The configuration map.
	 */
	protected Map<String, String> config;

	/**
	 * @param id            the id
	 * @param name          the name
	 * @param typeId        the type id
	 * @param projectId     the project id
	 * @param itemsFound    the items found
	 * @param itemsImported the items imported
	 * @param importErrors  the import errors
	 */
	public VraNgContentSource(String id, String name, VraNgContentSourceType typeId,
			String projectId, Integer itemsFound, Integer itemsImported,
			List<String> importErrors) {
		this.id = id;
		this.name = name;
		this.typeId = typeId.toString();
		this.projectId = projectId;
		this.itemsFound = itemsFound;
		this.itemsImported = itemsImported;
		this.lastImportErrors = importErrors;
	}

	/**
	 * @param id            the id
	 * @param name          the name
	 * @param typeId        the type id
	 * @param projectId     the project id
	 * @param itemsFound    the items found
	 * @param itemsImported the items imported
	 */
	public VraNgContentSource(String id, String name, VraNgContentSourceType typeId,
			String projectId, Integer itemsFound, Integer itemsImported) {
		this.id = id;
		this.name = name;
		this.typeId = typeId.toString();
		this.projectId = projectId;
		this.itemsFound = itemsFound;
		this.itemsImported = itemsImported;
	}

	/**
	 * @return the project id
	 */
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * @param projectId the project id
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the config
	 */
	public Map<String, String> getConfig() {
		return config;
	}

	/**
	 * @param config the config
	 */
	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

}
