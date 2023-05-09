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

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItemType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceType;

public class CatalogItemMockBuilder {
	/**
	 * id.
	 */
	private String id = "mockedItemId";
	/**
	 * name.
	*/
	private String name;
	/**
	 * sourceId.
	 */
	private String sourceId = "mockedSourceId";
	/**
	 * sourceName.
	 */
	private String sourceName;
	/**
	 * iconId.
	 */
	private String iconId;
	/**
	 * iconExt.
	 */
	private String iconExt;
	/**
	 * customFormId.
	 */
	private String customFormId;
	/**
	 * type.
	 */
	private VraNgCatalogItemType type;

	/**
	 * CatalogItemMockBuilder.
	 *
	 * @param nameIn
	 * @param sourceNameIn
	 */
	public CatalogItemMockBuilder(final String nameIn, final String sourceNameIn) {
		this.name = nameIn;
		this.sourceName = sourceNameIn;
		this.type = new VraNgCatalogItemType(VraNgContentSourceType.VRO_WORKFLOW, "mockedName", "mockedLink");
	}

	/**
	 * setId.
	 *
	 * @param idIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setId(final String idIn) {
		this.id = idIn;
		return this;
	}

	/**
	 * setSourceId.
	 *
	 * @param sourceIdIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setSourceId(final String sourceIdIn) {
		this.sourceId = sourceIdIn;
		return this;
	}

	/**
	 * setIconId.
	 *
	 * @param iconIdIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setIconId(final String iconIdIn) {
		this.iconId = iconIdIn;
		return this;
	}

	/**
	 * setIconExt.
	 *
	 * @param iconExtIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setIconExt(final String iconExtIn) {
		this.iconExt = iconExtIn;
		return this;
	}

	/**
	 * setCustomFormId.
	 *
	 * @param customFormIdIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setCustomFormId(final String customFormIdIn) {
		this.customFormId = customFormIdIn;
		return this;
	}

	/**
	 * setType.
	 *
	 * @param typeIn
	 * @return this CatalogItemMockBuilder
	 */
	public CatalogItemMockBuilder setType(final VraNgCatalogItemType typeIn) {
		this.type = typeIn;
		return this;
	}

	/**
	 * build.
	 *
	 * @return catalogItem VraNgCatalogItem
	 */
	public VraNgCatalogItem build() {
		VraNgCatalogItem catalogItem = new VraNgCatalogItem(this.id, this.sourceId, this.name, this.sourceName,
				this.type);

		if (this.iconExt != null) {
			catalogItem.setIconExtension(iconExt);
		}

		if (this.iconId != null) {
			catalogItem.setIconId(iconId);
		}

		if (this.customFormId != null) {
			catalogItem.setFormId(customFormId);
		}

		return catalogItem;
	}

}
