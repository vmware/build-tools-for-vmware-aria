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

/**
 * Service Broker catalog item.
 */
public class VraNgCatalogItem {
	/**
	 * sourceId.
	 */
	private String sourceId;
	/**
	 * sourceName.
	 */
	private String sourceName;
	/**
	 * name.
	 */
	private String name;
	/**
	 * id.
	 */
	private String id;
	/**
	 * iconId.
	 */
	private String iconId;
	/**
	 * iconExtension.
	 */
	private String iconExtension;
	/**
	 * formId.
	 */
	private String formId;
	/**
	 * type.
	 */
	private final VraNgCatalogItemType type;

	/**
	 * Constructor function.
	 * 
	 * @param idIn
	 * @param sourceIdIn
	 * @param nameIn
	 * @param sourceNameIn
	 * @param typeIn
	 */
	public VraNgCatalogItem(
			final String idIn,
			final String sourceIdIn,
			final String nameIn,
			final String sourceNameIn,
			final VraNgCatalogItemType typeIn) {
		this.id = idIn;
		this.sourceId = sourceIdIn;
		this.name = nameIn;
		this.sourceName = sourceNameIn;
		this.type = typeIn;
	}

	/**
	 * Getter for id.
	 * 
	 * @return String id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Catalog Item id gets updated when we need to fetch the correct id from the
	 * server to do operations like.
	 * patching an iconID
	 * 
	 * @param idIn is
	 */
	public void setId(final String idIn) {
		this.id = idIn;
	}

	/**
	 * Getter for sourceId.
	 * 
	 * @return String
	 */
	public String getSourceId() {
		return this.sourceId;
	}

	/**
	 * Getter for sourceName.
	 * 
	 * @return String
	 */
	public String getSourceName() {
		return this.sourceName;
	}

	/**
	 * Getter for name.
	 * 
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * setName.
	 *
	 * @param nameIn
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Getter for iconId.
	 * 
	 * @return String
	 */
	public String getIconId() {
		return this.iconId;
	}

	/**
	 * Setter for iconId.
	 * @param iconIdIn
	 */
	public void setIconId(final String iconIdIn) {
		this.iconId = iconIdIn;
	}

	/**
	 * Is there a icon associated with this catalog item.
	 * 
	 * @return boolean
	 */
	public boolean hasIcon() {
		return this.iconId != null && this.iconExtension != null;
	}

	/**
	 * Setter for icon extension.
	 * @param iconExtensionIn
	 */
	public void setIconExtension(final String iconExtensionIn) {
		this.iconExtension = iconExtensionIn;
	}

	/**
	 * Getter for getIconExtension.
	 * 
	 * @return String
	 */
	public String getIconExtension() {
		return this.iconExtension;
	}

	/**
	 * Getter for formId.
	 * 
	 * @return String
	 */
	public String getFormId() {
		return this.formId;
	}

	/**
	 * Setter for formId.
	 * @param formIdIn String
	 */
	public void setFormId(final String formIdIn) {
		this.formId = formIdIn;
	}

	/**
	 * Is there a form associated with this catalog item.
	 * 
	 * @return boolean
	 */
	public boolean hasForm() {
		return this.formId != null;
	}

	/**
	 * Decision based on the type needs to performed to see which call we need to
	 * perform in 812.
	 * 
	 * @return VraNgContentSourceType
	 */
	public VraNgContentSourceType getType() {
		return this.type.getId();
	}
}
