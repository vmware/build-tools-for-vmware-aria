package com.vmware.pscoe.iac.artifact.helpers.stubs;

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
