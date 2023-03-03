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
 * Service Broker catalog item
 */
public class VraNgCatalogItem {
	private final String sourceId;
	private final String sourceName;
	private final String name;
	private String id;
	private String iconId;
	private String iconExtension;
	private String formId;

	public VraNgCatalogItem( String id, String sourceId, String name, String sourceName ) {
		this.id = id;
		this.sourceId = sourceId;
		this.name = name;
		this.sourceName = sourceName;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * Catalog Item id gets updated when we need to fetch the correct id from the server to do operations like
	 * patching an iconID
	 * @param	id is
	 */
	public void setId( String id ) {
		this.id	= id;
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public String getName() {
		return this.name;
	}

	public String getIconId() {
		return this.iconId;
	}

	public void setIconId( String iconId ) {
		this.iconId	= iconId;
	}

	public boolean hasIcon() {
		return this.iconId != null && this.iconExtension != null;
	}

	public void setIconExtension( String iconExtension ) {
		this.iconExtension	= iconExtension;
	}

	public String getIconExtension() {
		return this.iconExtension;
	}

	public String getFormId() {
		return this.formId;
	}

	public void setFormId( String formId ) {
		this.formId	= formId;
	}

	public boolean hasForm() {
		return this.formId != null;
	}
}
