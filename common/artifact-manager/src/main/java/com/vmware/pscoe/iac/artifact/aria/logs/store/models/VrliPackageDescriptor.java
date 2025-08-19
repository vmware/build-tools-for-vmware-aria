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
package com.vmware.pscoe.iac.artifact.aria.logs.store.models;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageDescriptor;

public class VrliPackageDescriptor extends PackageDescriptor {
	private List<String> alerts;
	private List<String> contentPacks;

	public List<String> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = alerts;
	}

	public List<String> getContentPacks() {
		return contentPacks;
	}

	public void setContentPacks(List<String> contentPacks) {
		this.contentPacks = contentPacks;
	}

	public List<String> getMembersForType(VrliPackageMemberType type) {
		if (VrliPackageMemberType.ALERTS.equals(type)) {
			return getAlerts();
		} else if (VrliPackageMemberType.CONTENT_PACKS.equals(type)) {
			return getContentPacks();
		} else {
			throw new RuntimeException(String.format("ContentType '%s' is not supported!", type));
		}
	}

	public static VrliPackageDescriptor getInstance(File filesystemPath) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
		try {
			return mapper.readValue(filesystemPath, VrliPackageDescriptor.class);
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to load VRLI Package Descriptor [" + filesystemPath.getAbsolutePath() + "]", e);
		}
	}

}
