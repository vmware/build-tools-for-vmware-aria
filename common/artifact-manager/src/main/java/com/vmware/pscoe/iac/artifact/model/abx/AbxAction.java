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
package com.vmware.pscoe.iac.artifact.model.abx;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.LinkedHashMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "version", "description", "private", "scripts", "keywords", "author", "license",
		"devDependencies", "dependencies", "platform", "abx", "files" })
public class AbxAction {
	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("version")
	private String version;

	@JsonProperty("description")
	private String description;

	@JsonProperty("private")
	private Boolean _private;

	@JsonProperty("scripts")
	private Scripts scripts;

	@JsonProperty("keywords")
	private List<String> keywords;

	@JsonProperty("author")
	private String author;

	@JsonProperty("license")
	private String license;

	@JsonProperty("devDependencies")
	private DevDependencies devDependencies;

	@JsonProperty("dependencies")
	private Dependencies dependencies;

	@JsonProperty("platform")
	private Platform platform;

	@JsonProperty("abx")
	private Abx abx;

	@JsonProperty("files")
	private List<String> files;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<>();

	// The bundle is not part of the ABX package.json, however its content should be
	// stored here as a byte array
	@JsonIgnore
	private byte[] bundle = new byte[] {};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return (this.platform != null && this.platform.getAction() != null) ? this.platform.getAction() : this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("private")
	public Boolean getPrivate() {
		return _private;
	}

	@JsonProperty("private")
	public void setPrivate(Boolean _private) {
		this._private = _private;
	}

	public Scripts getScripts() {
		return scripts;
	}

	public void setScripts(Scripts scripts) {
		this.scripts = scripts;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public DevDependencies getDevDependencies() {
		return devDependencies;
	}

	public void setDevDependencies(DevDependencies devDependencies) {
		this.devDependencies = devDependencies;
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

	public void setDependencies(Dependencies dependencies) {
		this.dependencies = dependencies;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Abx getAbx() {
		return abx;
	}

	public void setAbx(Abx abx) {
		this.abx = abx;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public byte[] getBundle() {
		return this.bundle;
	}

	public void setBundle(byte[] bundle) {
		this.bundle = bundle;
	}

	public String getBundleAsBase64() throws IOException {
		return Base64.getEncoder().encodeToString(this.bundle);
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public static class DevDependencies {
		@JsonProperty("run-script-os")
		private String runScriptOs;
		
		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<>();

		@JsonProperty("run-script-os")
		public String getRunScriptOs() {
			return runScriptOs;
		}

		@JsonProperty("run-script-os")
		public void setRunScriptOs(String runScriptOs) {
			this.runScriptOs = runScriptOs;
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
	@JsonPropertyOrder({ "build", "clean", "clean:win32", "clean:default", "test" })
	public static class Scripts {
		@JsonProperty("build")
		private String build;

		@JsonProperty("clean")
		private String clean;

		@JsonProperty("clean:win32")
		private String cleanWin32;

		@JsonProperty("clean:default")
		private String cleanDefault;

		@JsonProperty("test")
		private String test;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<>();

		@JsonProperty("build")
		public String getBuild() {
			return build;
		}

		@JsonProperty("build")
		public void setBuild(String build) {
			this.build = build;
		}

		@JsonProperty("clean")
		public String getClean() {
			return clean;
		}

		@JsonProperty("clean")
		public void setClean(String clean) {
			this.clean = clean;
		}

		@JsonProperty("clean:win32")
		public String getCleanWin32() {
			return cleanWin32;
		}

		@JsonProperty("clean:win32")
		public void setCleanWin32(String cleanWin32) {
			this.cleanWin32 = cleanWin32;
		}

		@JsonProperty("clean:default")
		public String getCleanDefault() {
			return cleanDefault;
		}

		@JsonProperty("clean:default")
		public void setCleanDefault(String cleanDefault) {
			this.cleanDefault = cleanDefault;
		}

		@JsonProperty("test")
		public String getTest() {
			return test;
		}

		@JsonProperty("test")
		public void setTest(String test) {
			this.test = test;
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
	@JsonPropertyOrder({ "@vmware-pscoe/polyglotpkg", "@vmware-pscoe/vropkg" })
	public static class Dependencies {
		@JsonProperty("@vmware-pscoe/polyglotpkg")
		private String vmwarePscoePolyglotpkg;

		@JsonProperty("@vmware-pscoe/vropkg")
		private String vmwarePscoeVropkg;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<>();

		@JsonProperty("@vmware-pscoe/polyglotpkg")
		public String getVmwarePscoePolyglotpkg() {
			return vmwarePscoePolyglotpkg;
		}

		@JsonProperty("@vmware-pscoe/polyglotpkg")
		public void setVmwarePscoePolyglotpkg(String vmwarePscoePolyglotpkg) {
			this.vmwarePscoePolyglotpkg = vmwarePscoePolyglotpkg;
		}

		@JsonProperty("@vmware-pscoe/vropkg")
		public String getVmwarePscoeVropkg() {
			return vmwarePscoeVropkg;
		}

		@JsonProperty("@vmware-pscoe/vropkg")
		public void setVmwarePscoeVropkg(String vmwarePscoeVropkg) {
			this.vmwarePscoeVropkg = vmwarePscoeVropkg;
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
