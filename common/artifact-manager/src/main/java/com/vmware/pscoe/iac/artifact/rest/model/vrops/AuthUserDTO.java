package com.vmware.pscoe.iac.artifact.rest.model.vrops;

/*-
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "username", "firstName", "lastName", "password", "emailAddress", "distinguishedName", "enabled", "groupIds", "roleNames",
		"role-permissions", "lastLoginTime", "links" })
public class AuthUserDTO {

	@JsonProperty("id")
	private String id;

	@JsonProperty("username")
	private String username;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("password")
	private Object password;

	@JsonProperty("emailAddress")
	private String emailAddress;

	@JsonProperty("distinguishedName")
	private String distinguishedName;

	@JsonProperty("enabled")
	private Boolean enabled;

	@JsonProperty("groupIds")
	private List<String> groupIds;

	@JsonProperty("roleNames")
	private List<String> roleNames;

	@JsonProperty("role-permissions")
	private List<RolePermission> rolePermissions;

	@JsonProperty("lastLoginTime")
	private Long lastLoginTime;

	@JsonProperty("links")
	private List<Link> links;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@JsonProperty("firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@JsonProperty("lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@JsonProperty("password")
	public Object getPassword() {
		return password;
	}

	@JsonProperty("password")
	public void setPassword(Object password) {
		this.password = password;
	}

	@JsonProperty("emailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}

	@JsonProperty("emailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@JsonProperty("distinguishedName")
	public String getDistinguishedName() {
		return distinguishedName;
	}

	@JsonProperty("distinguishedName")
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	@JsonProperty("enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	@JsonProperty("enabled")
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonProperty("groupIds")
	public List<String> getGroupIds() {
		return groupIds;
	}

	@JsonProperty("groupIds")
	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}

	@JsonProperty("roleNames")
	public List<String> getRoleNames() {
		return roleNames;
	}

	@JsonProperty("roleNames")
	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	@JsonProperty("role-permissions")
	public List<RolePermission> getRolePermissions() {
		return rolePermissions;
	}

	@JsonProperty("role-permissions")
	public void setRolePermissions(List<RolePermission> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}

	@JsonProperty("lastLoginTime")
	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	@JsonProperty("lastLoginTime")
	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}

	@JsonProperty("links")
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "roleName", "scopeId", "allowAllObjects" })
	public static class RolePermission {

		@JsonProperty("roleName")
		private String roleName;

		@JsonProperty("scopeId")
		private String scopeId;

		@JsonProperty("allowAllObjects")
		private Boolean allowAllObjects;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

		@JsonProperty("roleName")
		public String getRoleName() {
			return roleName;
		}

		@JsonProperty("roleName")
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}

		@JsonProperty("scopeId")
		public String getScopeId() {
			return scopeId;
		}

		@JsonProperty("scopeId")
		public void setScopeId(String scopeId) {
			this.scopeId = scopeId;
		}

		@JsonProperty("allowAllObjects")
		public Boolean getAllowAllObjects() {
			return allowAllObjects;
		}

		@JsonProperty("allowAllObjects")
		public void setAllowAllObjects(Boolean allowAllObjects) {
			this.allowAllObjects = allowAllObjects;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "href", "rel", "name" })
	public static class Link {

		@JsonProperty("href")
		private String href;

		@JsonProperty("rel")
		private String rel;

		@JsonProperty("name")
		private String name;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

		@JsonProperty("href")
		public String getHref() {
			return href;
		}

		@JsonProperty("href")
		public void setHref(String href) {
			this.href = href;
		}

		@JsonProperty("rel")
		public String getRel() {
			return rel;
		}

		@JsonProperty("rel")
		public void setRel(String rel) {
			this.rel = rel;
		}

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}
	}
}
