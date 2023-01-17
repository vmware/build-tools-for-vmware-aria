package com.vmware.pscoe.iac.artifact.helpers.stubs;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;

public class ContentSourceBaseMockBuilder {
	private String id = "mockedSourceId";
	private String name = "mockedSourceName";
	private String typeId = "com.vmw.vro.workflow";


	public ContentSourceBaseMockBuilder setName(String name){
		this.name = name;
		return this;
	}

	public ContentSourceBaseMockBuilder setId(String id){
		this.id = id;
		return this;
	}

	public ContentSourceBaseMockBuilder setTypeId(String typeId){
		this.typeId = typeId;
		return this;
	}

	public VraNgContentSourceBase build(){
		VraNgContentSourceBase contentSource = new VraNgContentSourceBase();
		contentSource.setId(this.id);
		contentSource.setName(this.name);
		contentSource.setTypeId(this.typeId);
		return contentSource;
	}
}
