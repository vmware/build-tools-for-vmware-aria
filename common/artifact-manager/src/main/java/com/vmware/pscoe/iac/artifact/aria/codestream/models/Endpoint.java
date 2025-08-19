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
package com.vmware.pscoe.iac.artifact.aria.codestream.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Endpoint {
	private static final int PRIME_NUMBER_7 = 7;
	private static final int PRIME_NUMBER_31 = 31;

	private String project;
	private String id;
	private String name;
	private String description;
	private String version;
	private String type;
	private Boolean isRestricted;
	private String cloudProxyId;

	private Map<String, String> properties;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsRestricted() {
		return isRestricted;
	}

	public void setIsRestricted(Boolean isRestricted) {
		this.isRestricted = isRestricted;
	}

	public Map<String, String> getProperties() {
		properties = properties == null ? new LinkedHashMap<>() : properties;
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getCloudProxyId() {
		return cloudProxyId;
	}

	public void setCloudProxyId(String cloudProxyId) {
		this.cloudProxyId = cloudProxyId;
	}

	@Override
	public int hashCode() {
		int hash = PRIME_NUMBER_7;
		hash = PRIME_NUMBER_31 * hash + (name == null ? 0 : name.hashCode());
		hash = PRIME_NUMBER_31 * hash + (project == null ? 0 : project.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (this.getClass() != o.getClass()) {
			return false;
		}

		Variable var = (Variable) o;

		return (name.equals(var.name)
				&& project.equals(var.project));
	}

	@Override
	public String toString() {
		return String.format("Endpoint: name=%s, project=%s, type=%s", name, project, type);
	}
}
