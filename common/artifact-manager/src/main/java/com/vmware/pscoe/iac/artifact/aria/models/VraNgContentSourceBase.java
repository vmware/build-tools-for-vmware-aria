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
package com.vmware.pscoe.iac.artifact.aria.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for all content sources.
 */
public class VraNgContentSourceBase implements Identifiable {

	/**
	 * the id to set.
	 */
	@JsonProperty("id")
	protected String id;

	/**
	 * the name to set.
	 */
	@JsonProperty("name")
	protected String name;

	/**
	 * the description to set.
	 */
	@JsonProperty("description")
	protected String description;

	/**
	 * the typeId to set.
	 */
	@JsonProperty("typeId")
	protected String typeId;

	/**
	 * the itemsImported to set.
	 */
	@JsonProperty("itemsImported")
	protected int itemsImported;

	/**
	 * the itemsFound to set.
	 */
	@JsonProperty("itemsFound")
	protected int itemsFound;

	/**
	 * the lastImportErrors to set.
	 */
	@JsonProperty("lastImportErrors")
	protected transient List<String> lastImportErrors = new ArrayList<>();

	/**
	 * the global to set.
	 */
	@JsonProperty("global")
	protected boolean global;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the itemsImported
	 */
	public Integer getItemsFound() {
		return itemsFound;
	}

	/**
	 * @param itemsFound the itemsFound to set
	 */
	public void setItemsFound(Integer itemsFound) {
		this.itemsFound = itemsFound;
	}

	/**
	 * @return the itemsImported
	 */
	public Integer getItemsImported() {
		return itemsImported;
	}

	/**
	 * @param itemsImported the itemsImported to set
	 */
	public void setItemsImported(Integer itemsImported) {
		this.itemsImported = itemsImported;
	}

	/**
	 * @return the lastImportErrors
	 */
	public List<String> getLastImportErrors() {
		return lastImportErrors;
	}

	/**
	 * @param lastImportErrors the lastImportErrors to set
	 */
	public void setLastImportErrors(List<String> lastImportErrors) {
		this.lastImportErrors = lastImportErrors;
	}

	/**
	 * @return the type
	 */
	@JsonIgnore
	public VraNgContentSourceType getType() {
		return VraNgContentSourceType.fromString(this.typeId);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the global
	 */
	public boolean isGlobal() {
		return global;
	}

	/**
	 * @param global the global to set
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}

}
