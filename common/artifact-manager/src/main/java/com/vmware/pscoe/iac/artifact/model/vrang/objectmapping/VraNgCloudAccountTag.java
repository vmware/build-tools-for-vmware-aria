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
package com.vmware.pscoe.iac.artifact.model.vrang.objectmapping;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

public class VraNgCloudAccountTag {

	private final String exportTag;
	private final List<String> importTags;

	public VraNgCloudAccountTag() {
		super();
		this.exportTag = "";
		this.importTags = new ArrayList<>();
	}

	public VraNgCloudAccountTag(String exportTag, List<String> importTags) {
		this.exportTag = exportTag;
		this.importTags = importTags;
	}

	public String getExportTag() {
		return this.exportTag;
	}

	public List<String> getImportTags() {
		return this.importTags;
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
