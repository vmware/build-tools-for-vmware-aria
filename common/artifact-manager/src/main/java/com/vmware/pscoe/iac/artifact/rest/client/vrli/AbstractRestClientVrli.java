package com.vmware.pscoe.iac.artifact.rest.client.vrli;

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

import com.google.gson.*;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.rest.RestClient;
import com.vmware.pscoe.iac.artifact.rest.RestClientVrops;
import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Properties;

public abstract class AbstractRestClientVrli extends RestClient {
	protected String apiPrefix;
	protected RestTemplate restTemplate;
	protected ConfigurationVrli configuration;
	protected String vrliVersion;
	protected RestClientVrops vropsRestClient;
	protected Logger logger;
	protected static final String CONTENT_PACKS_API = "/content/contentpack";
	protected static final String CONTENT_PACKS_LIST_API = "/content/contentpack/list";
	protected static final String ALERTS_API = "/alerts";
	private static final String OVERWRITE_MODE = "OVERWRITE";

	protected AbstractRestClientVrli (String apiPrefix, ConfigurationVrli configuration, RestTemplate restTemplate) {
		this.apiPrefix = apiPrefix;
		this.restTemplate = restTemplate;
		this.configuration = configuration;
		this.vropsRestClient = getVropsRestClient();
	}

	@Override
	public String getVersion() {
		if (this.vrliVersion != null && !this.vrliVersion.isEmpty()) {
			return this.vrliVersion;
		}

		URI url = getURI(getURIBuilder().setPath(this.apiPrefix + "/version"));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
		this.vrliVersion = JsonPath.parse(response.getBody()).read("$.version");

		return this.vrliVersion;
	}

	@Override
	protected Configuration getConfiguration() {
		return this.configuration;
	}

	public String getContentPack(String contentPackNamespace) {
		String uriPattern = this.apiPrefix + CONTENT_PACKS_API + "/%s";
		String contentPackUriString = String.format(uriPattern, contentPackNamespace);
		URI url = getURI(getURIBuilder().setPath(contentPackUriString));
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
		} catch (HttpClientErrorException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new RuntimeException(String.format("Content pack '%s' does not exist: %s", contentPackNamespace, e.getMessage()));
			}
			throw new RuntimeException(String.format("Error fetching content pack data for content pack '%s': %s", contentPackNamespace, e.getMessage()));
		}

		// reformat the output JSON to be readable
		JsonObject jsonObject;
		try {
			jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			throw new RuntimeException(String.format("Error verifying data of content pack '%s': %s", contentPackNamespace, e.getMessage()));
		} catch (JsonIOException e) {
			throw new RuntimeException(String.format("Error reading and verifying data of content pack '%s': %s", contentPackNamespace, e.getMessage()));
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().serializeNulls().create();

		return gson.toJson(jsonObject);
	}

	public void importContentPack(String contentPackName, String contentPackJson) {
		Boolean overwrite = configuration.getPackageImportOverwriteMode().equals(Boolean.TRUE.toString())
				|| configuration.getPackageImportOverwriteMode().contains(OVERWRITE_MODE);
		URI url = getURI(getURIBuilder().setPath(this.apiPrefix + CONTENT_PACKS_API).addParameter("overwrite", overwrite.toString()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<>(contentPackJson, headers);
		try {
			restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		} catch (HttpClientErrorException e) {
			if (HttpStatus.CONFLICT.equals(e.getStatusCode())) {
				logger.warn("The content pack '{}' already exists on the target system, please uninstall it before importing again.", contentPackName);
				return;
			}
			if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
				throw new RuntimeException(String.format("Data validation error of the content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
			}
			throw new RuntimeException(String.format("Error importing content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
		} catch (RestClientException e) {
			throw new RuntimeException(String.format("REST client error during import of content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
		}
	}

	protected RestClientVrops getVropsRestClient() {
		if (this.vropsRestClient != null) {
			return this.vropsRestClient;
		}

		ConfigurationVrops vropsConfiguration;
		Properties vropsConfigProperties = new Properties();
		vropsConfigProperties.put(Configuration.HOST, configuration.getIntegrationVropsAuthHost());
		vropsConfigProperties.put(Configuration.PORT, configuration.getIntegrationVropsAuthPort());
		vropsConfigProperties.put(Configuration.USERNAME, configuration.getIntegrationVropsAuthUser());
		vropsConfigProperties.put(Configuration.PASSWORD, configuration.getIntegrationVropsAuthPassword());
		vropsConfigProperties.put(ConfigurationVrli.INTEGRATION_VROPS_AUTH_SOURCE, configuration.getIntegrationVropsAuthSource());
		vropsConfigProperties.put(ConfigurationVrops.VROPS_DASHBOARD_USER, "admin");
		vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_USER, configuration.getIntegrationVropsAuthUser());
		vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_PASSWORD, configuration.getIntegrationVropsAuthPassword());
		vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_AUTH_SOURCE, configuration.getIntegrationVropsAuthSource());
		vropsConfigProperties.put(ConfigurationVrops.SSH_PORT, "22");

		try {
			vropsConfiguration = ConfigurationVrops.fromProperties(vropsConfigProperties);
		} catch (ConfigurationException e) {
			throw new RuntimeException(
				String.format("Unable to update vCOPs integration for alert, vROPs integration configuration failed with message: %s", e.getMessage()));
		}

		return new RestClientVrops(vropsConfiguration, restTemplate);
	}
}
