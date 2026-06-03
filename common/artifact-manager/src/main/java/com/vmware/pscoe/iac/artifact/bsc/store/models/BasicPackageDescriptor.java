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
package com.vmware.pscoe.iac.artifact.bsc.store.models;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageDescriptor;

public class BasicPackageDescriptor extends PackageDescriptor {
	private List<String> content;

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public List<String> getMembersForType(BasicPackageMemberType type) {
		if (BasicPackageMemberType.CONTENT.equals(type)) {
			return getContent();
		} else {
			throw new RuntimeException(String.format("ContentType '%s' is not supported!", type));
		}
	}

	public static BasicPackageDescriptor getInstance(File filesystemPath) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
		try {
			return mapper.readValue(filesystemPath, BasicPackageDescriptor.class);
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to load Basic Package Descriptor [" + filesystemPath.getAbsolutePath() + "]", e);
		}
	}

}
