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
package com.vmware.pscoe.iac.artifact.rest.model.vrops;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "authSourceId", "name", "description", "displayName", "userIds", "roleNames", "role-permissions", "links" })
public class AuthGroupDTO implements Serializable {
	private static final long serialVersionUID = -5001015063559031411L;

	@JsonProperty("id")
	private String id;

	@JsonProperty("authSourceId")
	private String authSourceId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("displayName")
	private String displayName;

	@JsonProperty("userIds")
	private List<String> userIds = new ArrayList<>();

	@JsonProperty("roleNames")
	private List<String> roleNames = new ArrayList<>();

	@JsonProperty("role-permissions")
	private List<RolePermission> rolePermissions = new ArrayList<>();

	@JsonProperty("links")
	private List<Link> links = new ArrayList<>();

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("authSourceId")
	public String getAuthSourceId() {
		return authSourceId;
	}

	@JsonProperty("authSourceId")
	public void setAuthSourceId(String authSourceId) {
		this.authSourceId = authSourceId;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("displayName")
	public String getDisplayName() {
		return displayName;
	}

	@JsonProperty("displayName")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonProperty("userIds")
	public List<String> getUserIds() {
		return userIds;
	}

	@JsonProperty("userIds")
	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
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
	@JsonPropertyOrder({ "roleName", "traversal-spec-instances", "allowAllObjects" })
	public static class RolePermission implements Serializable {
		private static final long serialVersionUID = -8932971877839018059L;

		@JsonProperty("roleName")
		private String roleName;

		@JsonProperty("traversal-spec-instances")
		private List<TraversalSpecInstance> traversalSpecInstances = new ArrayList<>();

		@JsonProperty("allowAllObjects")
		private Boolean allowAllObjects;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<>();

		@JsonProperty("roleName")
		public String getRoleName() {
			return roleName;
		}

		@JsonProperty("roleName")
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}

		@JsonProperty("traversal-spec-instances")
		public List<TraversalSpecInstance> getTraversalSpecInstances() {
			return traversalSpecInstances;
		}

		@JsonProperty("traversal-spec-instances")
		public void setTraversalSpecInstances(List<TraversalSpecInstance> traversalSpecInstances) {
			this.traversalSpecInstances = traversalSpecInstances;
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

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyOrder({ "adapterKind", "resourceKind", "name", "resourceSelection", "selectAllResources" })
		public static class TraversalSpecInstance implements Serializable {
			private static final long serialVersionUID = -4749828660631156039L;

			@JsonProperty("adapterKind")
			private String adapterKind;

			@JsonProperty("resourceKind")
			private String resourceKind;

			@JsonProperty("name")
			private String name;

			@JsonProperty("resourceSelection")
			private List<ResourceSelection> resourceSelection = new ArrayList<>();

			@JsonProperty("selectAllResources")
			private Boolean selectAllResources;

			@JsonIgnore
			private Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("adapterKind")
			public String getAdapterKind() {
				return adapterKind;
			}

			@JsonProperty("adapterKind")
			public void setAdapterKind(String adapterKind) {
				this.adapterKind = adapterKind;
			}

			@JsonProperty("resourceKind")
			public String getResourceKind() {
				return resourceKind;
			}

			@JsonProperty("resourceKind")
			public void setResourceKind(String resourceKind) {
				this.resourceKind = resourceKind;
			}

			@JsonProperty("name")
			public String getName() {
				return name;
			}

			@JsonProperty("name")
			public void setName(String name) {
				this.name = name;
			}

			@JsonProperty("resourceSelection")
			public List<ResourceSelection> getResourceSelection() {
				return resourceSelection;
			}

			@JsonProperty("resourceSelection")
			public void setResourceSelection(List<ResourceSelection> resourceSelection) {
				this.resourceSelection = resourceSelection;
			}

			@JsonProperty("selectAllResources")
			public Boolean getSelectAllResources() {
				return selectAllResources;
			}

			@JsonProperty("selectAllResources")
			public void setSelectAllResources(Boolean selectAllResources) {
				this.selectAllResources = selectAllResources;
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
		@JsonPropertyOrder({ "type", "resourceId" })
		public static class ResourceSelection implements Serializable {
			private static final long serialVersionUID = 757388741865799633L;

			@JsonProperty("type")
			private String type;

			@JsonProperty("resourceId")
			private List<String> resourceId = new ArrayList<>();

			@JsonIgnore
			private Map<String, Object> additionalProperties = new HashMap<>();

			@JsonProperty("type")
			public String getType() {
				return type;
			}

			@JsonProperty("type")
			public void setType(String type) {
				this.type = type;
			}

			@JsonProperty("resourceId")
			public List<String> getResourceId() {
				return resourceId;
			}

			@JsonProperty("resourceId")
			public void setResourceId(List<String> resourceId) {
				this.resourceId = resourceId;
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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "href", "rel", "name" })
	public static class Link implements Serializable {
		private static final long serialVersionUID = 5366630360818372853L;

		@JsonProperty("href")
		private String href;

		@JsonProperty("rel")
		private String rel;

		@JsonProperty("name")
		private String name;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<>();

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
