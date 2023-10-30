package com.vmware.pscoe.iac.artifact.rest.model.vrops;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * PolicyDTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyDTO {

	/**
	 * policies.
	 */
	@JsonProperty("policy-summaries")
	private List<Policy> policies;

	/**
	 * policySummaries.
	 */
	@JsonProperty("policySummaries")
	private List<Policy> policySummaries;

	/**
	 * additionalProperties.
	 */
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * getPolicies().
	 * 
	 * @return list of policies.
	 */
	@JsonProperty("policy-summaries")
	public List<Policy> getPolicies() {
		return this.policies;
	}

	/**
	 * setPolicySummaries().
	 * 
	 * @param policySummaries policies to be set.
	 */
	@JsonProperty("policySummaries")
	public void setPolicySummaries(List<Policy> policySummaries) {
		this.policySummaries = policySummaries;
	}

	/**
	 * policySummaries().
	 * 
	 * @return list of policySummaries.
	 */
	@JsonProperty("policySummaries")
	public List<Policy> getPolicySummaries() {
		return this.policySummaries;
	}

	/**
	 * setPolicies().
	 * 
	 * @param policies policies to be set.
	 */
	@JsonProperty("policy-summaries")
	public void setPolicies(List<Policy> policies) {
		this.policies = policies;
	}	

	/**
	 * getAdditionalProperties().
	 * 
	 * @return map of additional properties.
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * setAdditionalProperties().
	 * 
	 * @param propName name of the property.
	 * @param value    value of the property.
	 */
	@JsonAnySetter
	public void setAdditionalProperties(String propName, Object value) {
		this.additionalProperties.put(propName, value);
	}

	/**
	 * Policy.
	 */
	@JsonPropertyOrder({ "id", "name", "defaultPolicy" })
	public static class Policy {

		/**
		 * id.
		 */
		@JsonProperty("id")
		private String id;

		/**
		 * name.
		 */
		@JsonProperty("name")
		private String name;

		/**
		 * zipFile.
		 */
		@JsonIgnore
		private byte[] zipFile;

		/**
		 * defaultPolicy.
		 */
		@JsonProperty("defaultPolicy")
		private boolean defaultPolicy;

		/**
		 * additionalProperties.
		 */
		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<>();

		/**
		 * getId().
		 * 
		 * @return string with id.
		 */
		@JsonProperty("id")
		public String getId() {
			return id;
		}

		/**
		 * setId().
		 * 
		 * @param id id to be set.
		 */
		@JsonProperty("id")
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * getName().
		 * 
		 * @return string with name.
		 */
		@JsonProperty("name")
		public String getName() {
			return name;
		}

		/**
		 * setName().
		 * 
		 * @param name name of the policy.
		 */
		@JsonProperty("name")
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * setZipFile().
		 * 
		 * @param file byte array of the file.
		 */
		@JsonIgnore
		public void setZipFile(byte[] file) {
			this.zipFile = file;
		}

		/**
		 * getZipFile().
		 * 
		 * @return byte array of the file.
		 */
		@JsonIgnore
		public byte[] getZipFile() {
			return this.zipFile;
		}

		/**
		 * getDefaultPolicy().
		 * 
		 * @return whether it is default policy.
		 */
		@JsonProperty("defaultPolicy")
		public boolean getDefaultPolicy() {
			return defaultPolicy;
		}

		/**
		 * setDefaultPolicy().
		 * 
		 * @param defaultPolicy default policy flag.
		 */
		@JsonProperty("defaultPolicy")
		public void setDefaultPolicy(boolean defaultPolicy) {
			this.defaultPolicy = defaultPolicy;
		}

		/**
		 * getAdditionalProperties().
		 * 
		 * @return map with additional properties.
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperties().
		 * 
		 * @param propName name of the property.
		 * @param value    value of the property.
		 */
		@JsonAnySetter
		public void setAdditionalProperties(String propName, Object value) {
			this.additionalProperties.put(propName, value);
		}
	}
}
