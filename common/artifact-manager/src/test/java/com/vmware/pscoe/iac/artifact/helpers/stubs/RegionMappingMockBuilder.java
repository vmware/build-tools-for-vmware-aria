package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegionMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.objectmapping.VraNgCloudAccountTag;


public class RegionMappingMockBuilder {
	private String exportTag;
	private List<String> importTags;

	public RegionMappingMockBuilder() {
		this.exportTag = "";
		this.importTags = new ArrayList<String>();
	}

	public RegionMappingMockBuilder setExportTag(String exportTag){
		this.exportTag = exportTag;
		return this;
	}

	public RegionMappingMockBuilder setImportTags(List<String> importTags){
		this.importTags = importTags;
		return this;
	}

	public VraNgRegionMapping build() {
		VraNgCloudAccountTag tags;
		if(this.exportTag == null && this.importTags.isEmpty() ){
			tags = new VraNgCloudAccountTag();
		} else {
			tags = new VraNgCloudAccountTag(this.exportTag, this.importTags);
		}

		return new VraNgRegionMapping(tags);
	}
}
