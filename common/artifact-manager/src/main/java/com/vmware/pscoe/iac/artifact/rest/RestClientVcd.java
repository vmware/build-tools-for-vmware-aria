package com.vmware.pscoe.iac.artifact.rest;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vcd.VcdNgPackageManifest;
import com.vmware.pscoe.iac.artifact.rest.helpers.VcdApiHelper;
import com.vmware.pscoe.iac.artifact.rest.model.VcdPluginMetadataDTO;
import com.vmware.pscoe.iac.artifact.rest.model.VcdPluginResourceDTO;

import net.minidev.json.JSONArray;

public class RestClientVcd extends RestClient {

	/**
	 * packageType.
	 */
	private static final PackageType PACKAGE_TYPE = PackageType.VCDNG;

	/**
	 * logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(RestClientVcd.class);

	/**
	 * configuration.
	 */
	private ConfigurationVcd configuration;

	/**
	 * restTemplate.
	 */
	private RestTemplate restTemplate;

	/**
	 * apiVersion.
	 */
	private String apiVersion;

	/**
	 * versions api path.
	 */
	private static final String URL_VERSIONS = "api/versions";

	/**
	 * extensions api path.
	 */
	private static final String URL_UI_EXTENSION_BASE = "cloudapi/extensions/ui";

	/**
	 * extension base.
	 */
	private static final String URL_UI_EXTENSION_BY_ID = URL_UI_EXTENSION_BASE + "/%s";

	/**
	 * plugin base.
	 */
	private static final String URL_UI_PLUGIN_BY_ID = URL_UI_EXTENSION_BASE + "/%s/plugin";

	/**
	 * publish all path.
	 */
	private static final String URL_UI_PLUGIN_PUBLISH_ALL = URL_UI_EXTENSION_BASE + "/%s/tenants/publishAll";

	/**
	 * api version that is supported.
	 */
	private static final String API_VERSION_37 = "37.0";

	/**
	 * api version that is not yet supported.
	 */
	private static final String API_VERSION_38 = "38.0";

	protected RestClientVcd(ConfigurationVcd configuration, RestTemplate restTemplate) {
		this.configuration = configuration;
		this.restTemplate = restTemplate;
	}

	/**
	 * getRestTemplate.
	 * 
	 * @return RestTemplate
	 */
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * getConfiguration.
	 * 
	 * @return Configuration
	 */
	@Override
	protected Configuration getConfiguration() {
		return this.configuration;
	}

	private HttpEntity<String> getVcdHttpEntity() {
		HttpHeaders headers = getCommonVcdHeaders();
		return new HttpEntity<String>(headers);
	}

	private HttpHeaders getCommonVcdHeaders() {
		HttpHeaders headers = new HttpHeaders();
		MediaType contentType = VcdApiHelper.buildMediaType("application/json", apiVersion);

		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(contentType);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return headers;
	}

	/**
	 * getVersion.
	 */
	@Override
	public String getVersion() {
		if (this.apiVersion == null) {
			URI url = getURI(getURIBuilder().setPath(URL_VERSIONS));

			HttpHeaders headers = new HttpHeaders();
			MediaType contentType = VcdApiHelper.buildMediaType("application/*+json", null);

			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(contentType);
			headers.setAccept(acceptableMediaTypes);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<String>(headers), String.class);
			JSONArray versionArray = JsonPath.parse(response.getBody()).read("$.versionInfo[*].version");
			this.apiVersion = versionArray.get(versionArray.size() - 1).toString();
			if (Double.parseDouble(this.apiVersion) >= Double.parseDouble(API_VERSION_38)) {
				logger.warn("Detected vCD API version equal or greater than " + API_VERSION_38 + ". Switching to using API version " + API_VERSION_37);
				this.apiVersion = API_VERSION_37;
			}

			logger.debug("API version is: " + this.apiVersion);
		}

		return this.apiVersion;
	}

	/**
	 * getAllUiExtensions.
	 * 
	 * @return list of packages
	 */
	public List<Package> getAllUiExtensions() {
		URI url = getURI(getURIBuilder().setPath(URL_UI_EXTENSION_BASE));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getVcdHttpEntity(), String.class);
		List<String> ids = JsonPath.parse(response.getBody()).read("$[*].id", List.class);
		List<Package> extensions = new ArrayList<Package>();

		for (String id : ids) {
			extensions.add(getUiExtension(id));
		}

		return extensions;
	}
	
	/**
	 * 
	 * @param id extension id
	 * @return Package
	 * 
	 * getUiExtension.
	 */
	public Package getUiExtension(String id) {
		logger.debug("Getting UI extension for ID [" + id + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_EXTENSION_BY_ID, id)));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getVcdHttpEntity(), String.class);
		String pluginId = JsonPath.parse(response.getBody()).read("$.id");
		String pluginName = JsonPath.parse(response.getBody()).read("$.pluginName");
		String pluginVersion = JsonPath.parse(response.getBody()).read("$.version");

		logger.debug("UI extension for ID [" + id + "] retrieved.");
		return PackageFactory.getInstance(PACKAGE_TYPE, pluginId,
				new File(pluginName + "-" + pluginVersion + "." + PACKAGE_TYPE));
	}

	/**
	 * 
	 * @param localPkg local package
	 * @return Package
	 * 
	 * getUiExtension.
	 */
	public Package getUiExtension(Package localPkg) {
		List<Package> extensions = getAllUiExtensions();
		for (Package remotePkg : extensions) {
			if (localPkg.getName().equals(remotePkg.getName())) {
				return remotePkg;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param pkg package
	 * @return Package
	 * 
	 * addUiExtension.
	 */
	public Package addUiExtension(Package pkg) {
		logger.debug("Adding UI extension for [" + pkg + "]...");
		VcdNgPackageManifest manifest = VcdNgPackageManifest.getInstance(pkg);
		String requestBody = new Gson().toJson(new VcdPluginMetadataDTO(manifest));

		URI url = getURI(getURIBuilder().setPath(URL_UI_EXTENSION_BASE));
		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		String pluginId = JsonPath.parse(response.getBody()).read("$.id");
		logger.debug("UI extension for [" + pkg + "] added.");
		return getUiExtension(pluginId);
	}

	/**
	 * 
	 * @param remotePkg remote package
	 * 
	 * removeUiExtension.
	 */
	public void removeUiExtension(Package remotePkg) {
		logger.debug("Removing UI extension for [" + remotePkg + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_EXTENSION_BY_ID, remotePkg.getId())));
		restTemplate.exchange(url, HttpMethod.DELETE, this.getVcdHttpEntity(), String.class);
		logger.debug("UI extension for [" + remotePkg + "] removed.");
	}

	/**
	 * 
	 * @param localPkg local package
	 * @param remotePkg remote package
	 * 
	 * uploadUiPlugin.
	 */
	public void uploadUiPlugin(Package localPkg, Package remotePkg) {
		logger.debug("Uploading plugin resource for [" + remotePkg + "].");
		File pkgFile = new File(localPkg.getFilesystemPath());
		String requestBody = new Gson().toJson(new VcdPluginResourceDTO(pkgFile.getName(), pkgFile.length()));

		// Get upload link
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_BY_ID, remotePkg.getId())));
		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String linkHeader = response.getHeaders().get("link").get(0);
		String uploadLink = linkHeader.substring(1, linkHeader.indexOf(">"));

		// Upload package
		url = getURI(getURIBuilder().setPath(uploadLink.substring(uploadLink.indexOf("transfer"))));
		headers = getCommonVcdHeaders();
		headers.setContentType(VcdApiHelper.buildMediaType("application/zip", null));

		byte[] pkgContent;
		try {
			pkgContent = IOUtils.toByteArray(new FileInputStream(pkgFile));
		} catch (IOException e) {
			throw new RuntimeException("Unable to find plugin file " + localPkg.getFilesystemPath(), e);
		}
		HttpEntity<byte[]> pluginEntity = new HttpEntity<>(pkgContent, headers);

		restTemplate.exchange(url, HttpMethod.PUT, pluginEntity, String.class);
		logger.debug("Plugin resource for [" + remotePkg + "] uploaded.");
	}

	/**
	 * 
	 * @param remotePkg remote package
	 * 
	 * deleteUiPlugin.
	 */
	public void deleteUiPlugin(Package remotePkg) {
		logger.debug("Deleting plugin resource for [" + remotePkg + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_BY_ID, remotePkg.getId())));
		restTemplate.exchange(url, HttpMethod.DELETE, this.getVcdHttpEntity(), String.class);
		logger.debug("Plugin resource for [" + remotePkg + "] deleted.");
	}
	
	/**
	 * 
	 * @param pkg package
	 * @return Package
	 * 
	 * addOrReplaceUiPlugin.
	 */
	public Package addOrReplaceUiPlugin(Package pkg) {
		Package remotePkg = this.getUiExtension(pkg);
		if (remotePkg != null) {
			this.deleteUiPlugin(remotePkg);
			this.removeUiExtension(remotePkg);
		}

		remotePkg = this.addUiExtension(pkg);
		this.uploadUiPlugin(pkg, remotePkg);
		this.publishUiPlugin(remotePkg);
		
		return remotePkg;
	}
	
	/**
	 * 
	 * @param remotePkg remote package
	 * 
	 * publishUiPlugin.
	 */
	public void publishUiPlugin(Package remotePkg) {
		logger.debug("Publishing UI extension [" + remotePkg + "] to all tenants...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_PUBLISH_ALL, remotePkg.getId())));

		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		logger.debug("UI extension [" + remotePkg + "] published to all tenants.");
	}
}
