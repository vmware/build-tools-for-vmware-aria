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
