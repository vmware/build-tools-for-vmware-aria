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
	private Boolean prvt;

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

	/**
	 * getId().
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * setId().
	 * 
	 * @param id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * getName().
	 * 
	 * @return platform action value or the name of the action.
	 */
	public String getName() {
		return (this.platform != null && this.platform.getAction() != null) ? this.platform.getAction() : this.name;
	}

	/**
	 * setName().
	 * 
	 * @param name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getVersion().
	 * 
	 * @return version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * setVersion().
	 * 
	 * @param version.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * getDescription().
	 * 
	 * @return description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription().
	 * 
	 * @param description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * getPrivate().
	 * 
	 * @return private flag of the action.
	 */
	@JsonProperty("private")
	public Boolean getPrivate() {
		return prvt;
	}

	/**
	 * setPrivate().
	 * 
	 * @param pvt flag to be set.
	 */
	@JsonProperty("private")
	public void setPrivate(Boolean pvt) {
		this.prvt = pvt;
	}

	/**
	 * getScripts().
	 * 
	 * @return scripts of the action.
	 */
	public Scripts getScripts() {
		return scripts;
	}

	/**
	 * setScripts().
	 * 
	 * @param scripts.
	 */
	public void setScripts(Scripts scripts) {
		this.scripts = scripts;
	}

	/**
	 * getKeywords().
	 * 
	 * @return keywords of the action.
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * setKeywords().
	 * 
	 * @param keywords.
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * getAuthor().
	 * 
	 * @return author of the action.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * setAuthor().
	 * 
	 * @param author.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * getAuthor().
	 * 
	 * @return license of the action.
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * setLicense().
	 * 
	 * @param license.
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * getDevDependencies().
	 * 
	 * @return devDependencies of the action.
	 */
	public DevDependencies getDevDependencies() {
		return devDependencies;
	}

	/**
	 * setDevDependencies().
	 * 
	 * @param devDependencies.
	 */
	public void setDevDependencies(DevDependencies devDependencies) {
		this.devDependencies = devDependencies;
	}

	/**
	 * getDependencies().
	 * 
	 * @return dependencies of the action.
	 */
	public Dependencies getDependencies() {
		return dependencies;
	}

	/**
	 * setDependencies().
	 * 
	 * @param dependencies.
	 */
	public void setDependencies(Dependencies dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * getPlatform().
	 * 
	 * @return platform of the action.
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * setPlatform().
	 * 
	 * @param platform.
	 */
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	/**
	 * getAbx().
	 * 
	 * @return abx back end of the action.
	 */
	public Abx getAbx() {
		return abx;
	}

	/**
	 * setAbx().
	 * 
	 * @param abx.
	 */
	public void setAbx(Abx abx) {
		this.abx = abx;
	}

	/**
	 * getFiles().
	 * 
	 * @return files of the action.
	 */
	public List<String> getFiles() {
		return files;
	}

	/**
	 * setFiles().
	 * 
	 * @param files.
	 */
	public void setFiles(List<String> files) {
		this.files = files;
	}

	/**
	 * getBundle().
	 * 
	 * @return bundle data of the action (stores the dependency file content).
	 */
	public byte[] getBundle() {
		return this.bundle;
	}

	/**
	 * setBundle().
	 * 
	 * @param bundle byte array of the bundle file contents.
	 */
	public void setBundle(byte[] bundle) {
		this.bundle = bundle;
	}

	/**
	 * getBundleAsBase64().
	 * 
	 * @return the base 64 encoded content of the bundle data of the action.
	 */
	public String getBundleAsBase64() throws IOException {
		return Base64.getEncoder().encodeToString(this.bundle);
	}

	/**
	 * getAdditionalProperties().
	 * 
	 * @return additionalProperties of the action.
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * setAdditionalProperty().
	 * 
	 * @param nm.
	 * @param value.
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String nm, Object value) {
		this.additionalProperties.put(nm, value);
	}

	public static class DevDependencies {
		@JsonProperty("run-script-os")
		private String runScriptOs;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<>();

		/**
		 * getRunScriptOs().
		 * 
		 * @return runScriptOs of the action dependency.
		 */
		@JsonProperty("run-script-os")
		public String getRunScriptOs() {
			return runScriptOs;
		}

		/**
		 * setRunScriptOs().
		 * 
		 * @param runScriptOs.
		 */
		@JsonProperty("run-script-os")
		public void setRunScriptOs(String runScriptOs) {
			this.runScriptOs = runScriptOs;
		}

		/**
		 * getAdditionalProperties().
		 * 
		 * @return additionalProperties of the action dependency.
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperty().
		 * 
		 * @param nm.
		 * @param value.
		 */
		@JsonAnySetter
		public void setAdditionalProperty(String nm, Object value) {
			this.additionalProperties.put(nm, value);
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

		/**
		 * getBuild().
		 * 
		 * @return build of the action script.
		 */
		@JsonProperty("build")
		public String getBuild() {
			return build;
		}

		/**
		 * setBuild().
		 * 
		 * @param build.
		 */
		@JsonProperty("build")
		public void setBuild(String build) {
			this.build = build;
		}

		/**
		 * getClean().
		 * 
		 * @return clean of the action script.
		 */
		@JsonProperty("clean")
		public String getClean() {
			return clean;
		}

		/**
		 * setClean().
		 * 
		 * @param clean.
		 */
		@JsonProperty("clean")
		public void setClean(String clean) {
			this.clean = clean;
		}

		/**
		 * getCleanWin32().
		 * 
		 * @return cleanWin32 of the action script.
		 */
		@JsonProperty("clean:win32")
		public String getCleanWin32() {
			return cleanWin32;
		}

		/**
		 * setCleanWin32().
		 * 
		 * @param cleanWin32.
		 */
		@JsonProperty("clean:win32")
		public void setCleanWin32(String cleanWin32) {
			this.cleanWin32 = cleanWin32;
		}

		/**
		 * getCleanDefault().
		 * 
		 * @return cleanDefault of the action script.
		 */
		@JsonProperty("clean:default")
		public String getCleanDefault() {
			return cleanDefault;
		}

		/**
		 * cleanDefault().
		 * 
		 * @param cleanDefault.
		 */
		@JsonProperty("clean:default")
		public void setCleanDefault(String cleanDefault) {
			this.cleanDefault = cleanDefault;
		}

		/**
		 * getTest().
		 * 
		 * @return test of the action script.
		 */
		@JsonProperty("test")
		public String getTest() {
			return test;
		}

		/**
		 * setTest().
		 * 
		 * @param test.
		 */
		@JsonProperty("test")
		public void setTest(String test) {
			this.test = test;
		}

		/**
		 * getAdditionalProperties().
		 * 
		 * @return additionalProperties of the action script.
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperty().
		 * 
		 * @param nm.
		 * @param value.
		 */
		@JsonAnySetter
		public void setAdditionalProperty(String nm, Object value) {
			this.additionalProperties.put(nm, value);
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

		/**
		 * getVmwarePscoePolyglotpkg.
		 * 
		 * @return vmwarePscoePolyglotpkg of the dependency.
		 */
		@JsonProperty("@vmware-pscoe/polyglotpkg")
		public String getVmwarePscoePolyglotpkg() {
			return vmwarePscoePolyglotpkg;
		}

		/**
		 * setVmwarePscoePolyglotpkg().
		 * 
		 * @param vmwarePscoePolyglotpkg.
		 */
		@JsonProperty("@vmware-pscoe/polyglotpkg")
		public void setVmwarePscoePolyglotpkg(String vmwarePscoePolyglotpkg) {
			this.vmwarePscoePolyglotpkg = vmwarePscoePolyglotpkg;
		}

		/**
		 * getVmwarePscoeVropkg().
		 * 
		 * @return vmwarePscoeVropkg of the dependency.
		 */
		@JsonProperty("@vmware-pscoe/vropkg")
		public String getVmwarePscoeVropkg() {
			return vmwarePscoeVropkg;
		}

		/**
		 * setVmwarePscoeVropkg().
		 * 
		 * @param vmwarePscoeVropkg.
		 */
		@JsonProperty("@vmware-pscoe/vropkg")
		public void setVmwarePscoeVropkg(String vmwarePscoeVropkg) {
			this.vmwarePscoeVropkg = vmwarePscoeVropkg;
		}

		/**
		 * getAdditionalProperties().
		 * 
		 * @return additionalProperties of the dependency.
		 */
		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		/**
		 * setAdditionalProperty().
		 * 
		 * @param nm.
		 * @param value.
		 */
		@JsonAnySetter
		public void setAdditionalProperty(String nm, Object value) {
			this.additionalProperties.put(nm, value);
		}
	}
}
