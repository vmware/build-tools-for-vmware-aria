
package com.vmware.pscoe.iac.artifact.model.abx;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
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
@JsonPropertyOrder({ "runtime", "action", "tags", "entrypoint", "base", "memoryLimitMb", "timeoutSec", "provider" })
public class Platform {
	@JsonProperty("runtime")
	private String runtime;

	@JsonProperty("runtimeVersion")
	private String runtimeVersion;

	@JsonProperty("action")
	private String action;

	@JsonProperty("tags")
	private List<Object> tags;

	@JsonProperty("entrypoint")
	private String entrypoint;

	@JsonProperty("base")
	private String base;

	@JsonProperty("memoryLimitMb")
	private Integer memoryLimitMb;

	@JsonProperty("timeoutSec")
	private Integer timeoutSec;

	@JsonProperty("provider")
	private String provider;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("runtime")
	public String getRuntime() {
		return runtime;
	}

	@JsonProperty("runtime")
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public String getRuntimeVersion() {
		return runtimeVersion;
	}

	public void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}

	@JsonProperty("action")
	public String getAction() {
		return action;
	}

	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}

	@JsonProperty("tags")
	public List<Object> getTags() {
		return tags;
	}

	@JsonProperty("tags")
	public void setTags(List<Object> tags) {
		this.tags = tags;
	}

	@JsonProperty("entrypoint")
	public String getEntrypoint() {
		return entrypoint;
	}

	@JsonProperty("entrypoint")
	public void setEntrypoint(String entrypoint) {
		this.entrypoint = entrypoint;
	}

	@JsonProperty("base")
	public String getBase() {
		return base;
	}

	@JsonProperty("base")
	public void setBase(String base) {
		this.base = base;
	}

	@JsonProperty("memoryLimitMb")
	public Integer getMemoryLimitMb() {
		return memoryLimitMb;
	}

	@JsonProperty("memoryLimitMb")
	public void setMemoryLimitMb(Integer memoryLimitMb) {
		this.memoryLimitMb = memoryLimitMb;
	}

	@JsonProperty("timeoutSec")
	public Integer getTimeoutSec() {
		return timeoutSec;
	}

	@JsonProperty("timeoutSec")
	public void setTimeoutSec(Integer timeoutSec) {
		this.timeoutSec = timeoutSec;
	}

	@JsonProperty("provider")
	public String getProvider() {
		return provider;
	}

	@JsonProperty("provider")
	public void setProvider(String provider) {
		this.provider = provider;
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
