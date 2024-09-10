/*
 * #%L
 * common
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
package com.vmware.pscoe.maven.plugins;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

public class MetadataResolver {
	// the order of the items in the array is important
	private static final String[] AVAILABLE_PLACEHOLDERS = new String[] { "$CUSTOMER", "$PROJECT", "$RELEASE" };
	private static final String UNRESOLVED_VALUE = "n/a";
	private static final String SPLIT_SEPARATOR = "\\.";
	private Map<String, String> placeholdersMap = new LinkedHashMap<>();
	private String[] projectInfo = new String[] {};
	protected MavenProject project;

	public MetadataResolver(MavenProject project) {
		this.project = project;
		this.parseProjectMetaData();
		this.initPlaceholderMap();
	}

	protected String extractMetaData(String description) {
		if (StringUtils.isEmpty(description)) {
			return null;
		}
		List<String> availablePlaceholderValues = placeholdersMap.keySet().stream().map(key -> placeholdersMap.get(key)).collect(Collectors.toList());
		description = StringUtils.replaceEachRepeatedly(description, AVAILABLE_PLACEHOLDERS, availablePlaceholderValues.toArray(new String[] {}));

		return description;
	}

	private String extractVersion() {
		return StringUtils.isEmpty(project.getVersion()) ? "" : project.getVersion().replaceAll("-SNAPSHOT$", "");
	}

	private String extractCustomer() {
		return projectInfo == null || projectInfo.length == 0 ? UNRESOLVED_VALUE : projectInfo[0].toUpperCase();
	}

	private String extractProject() {
		return project.getName();
	}

	private void initPlaceholderMap() {
		placeholdersMap.put(AVAILABLE_PLACEHOLDERS[0], extractCustomer());
		placeholdersMap.put(AVAILABLE_PLACEHOLDERS[1], extractProject());
		placeholdersMap.put(AVAILABLE_PLACEHOLDERS[2], extractVersion());
	}

	private void parseProjectMetaData() {
		if (project != null) {
			this.projectInfo = project.getName().split(SPLIT_SEPARATOR);
		}
	}
}
