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

public class CatalogItemMockBuilder {
	String id			= "mockedItemId";
	String name;
	String sourceId 	= "mockedSourceId";
	String sourceName;
	String iconId;
	String iconExt;
	String customFormId;

	public CatalogItemMockBuilder(String name, String sourceName){
		this.name = name;
		this.sourceName = sourceName;
	}

	public CatalogItemMockBuilder setId(String id){
		this.id = id;
		return this;
	}

	public CatalogItemMockBuilder setSourceId(String sourceId){
		this.sourceId = sourceId;
		return this;
	}

	public CatalogItemMockBuilder setIconId(String iconId){
		this.iconId = iconId;
		return this;
	}

	public CatalogItemMockBuilder setIconExt(String iconExt){
		this.iconExt = iconExt;
		return this;
	}

	public CatalogItemMockBuilder setCustomFormId(String customFormId){
		this.customFormId = customFormId;
		return this;
	}

	public VraNgCatalogItem build(){
		VraNgCatalogItem catalogItem = new VraNgCatalogItem(this.id, this.sourceId, this.name, this.sourceName);

		if( this.iconExt != null ) {
			catalogItem.setIconExtension( iconExt );
		}

		if( this.iconId != null ) {
			catalogItem.setIconId( iconId );
		}

		if( this.customFormId != null ) {
			catalogItem.setFormId( customFormId );
		}

		return catalogItem;
	}

}
