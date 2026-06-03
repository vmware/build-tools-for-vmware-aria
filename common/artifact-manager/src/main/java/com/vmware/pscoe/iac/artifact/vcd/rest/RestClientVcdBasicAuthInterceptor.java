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
package com.vmware.pscoe.iac.artifact.vcd.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.common.rest.RestClientRequestInterceptor;
import com.vmware.pscoe.iac.artifact.vcd.configuration.ConfigurationVcd;

public class RestClientVcdBasicAuthInterceptor extends RestClientRequestInterceptor<ConfigurationVcd> {

	/**
	 * logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(RestClientVcdBasicAuthInterceptor.class);

	/**
	 * vcloudToken.
	 */
	private String vcloudToken;

	/**
	 * bearerToken.
	 */
	private String bearerToken;

	/**
	 * contentType.
	 */
	private MediaType contentType;

	/**
	 * defines whether to use URL_PROVIDER_SESSION or URL_SESSION during
	 * authentication.
	 */
	private Boolean useProviderAuth;

	/**
	 * provider session api path.
	 */
	private static final String URL_PROVIDER_SESSION = "/cloudapi/1.0.0/sessions/provider";

	/**
	 * session api path for non-provider / specific organization authentication.
	 */
	private static final String URL_SESSION = "/cloudapi/1.0.0/sessions";

	/**
	 * api path for acquiring provider session for specific organization,
	 */
	private static final String URL_PROVIDER_SESSION_FOR_TENANT = "/oidc/oauth2/token";

	/**
	 * organizations api path.
	 */
	private static final String URL_ORGANIZATIONS = "/cloudapi/1.0.0/orgs";

	/**
	 * relying parties api path. This is used to extract the
	 * "automation-relying-party" Client ID which in combination with the
	 * organization ID is used to acquire Organization specific token for a provider
	 * admin user.
	 */
	private static final String URL_RELYING_PARTIES = "cloudapi/1.0.0/openIdProvider/relyingParties";

	/**
	 * vcloud header key.
	 */
	private static final String HEADER_VCLOUD_TOKEN = "x-vcloud-authorization";

	/**
	 * bearer token header key.
	 */
	private static final String HEADER_BEARER_TOKEN = "x-vmware-vcloud-access-token";

	/**
	 * tenant/organization header key.
	 */
	private static final String HEADER_TENANT = "x-vmware-vcloud-tenant-context";

	/**
	 * authorization header key.
	 */
	private static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * JSON response values key name.
	 */
	private static final String JSON_RESPONSE_VALUES_KEY = "values";

	/**
	 * JSON response clientName key name.
	 */
	private static final String JSON_RESPONSE_CLIENT_NAME_KEY = "clientName";

	/**
	 * JSON response access_token key name.
	 */
	private static final String JSON_RESPONSE_ACCESS_TOKEN_KEY = "access_token";

	/**
	 * JSON response name key name.
	 */
	private static final String JSON_RESPONSE_NAME_KEY = "name";

	/**
	 * JSON response access_token key name.
	 */
	private static final String JSON_RESPONSE_ID_KEY = "id";

	/**
	 * JSON payload/response clientId key name.
	 */
	private static final String JSON_REQUEST_CLIENT_ID_KEY = "clientId";

	/**
	 * JSON payload grant_type key name.
	 */
	private static final String JSON_PAYLOAD_GRANT_TYPE_KEY = "grant_type";

	/**
	 * JSON payload grant_type value for aquiring provider session for specific
	 * organization.
	 */
	private static final String JSON_PAYLOAD_GRANT_TYPE_VALUE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

	/**
	 * JSON payload scope key name.
	 */
	private static final String JSON_PAYLOAD_SCOPE_KEY = "scope";

	/**
	 * JSON payload scope value for aquiring provider session for specific
	 * organization.
	 */
	private static final String JSON_PAYLOAD_SCOPE_VALUE = "openid profile email phone groups vcd_idp";

	/**
	 * JSON payload assertion key name.
	 */
	private static final String JSON_PAYLOAD_ASSERTION_KEY = "assertion";

	/**
	 * automation relying party name.
	 */
	private static final String AUTOMATION_RELYING_PARTY = "automation-relying-party";

	/**
	 * provider domain name.
	 */
	private static final String PROVIDER_DOMAIN = "System";

	public RestClientVcdBasicAuthInterceptor(ConfigurationVcd configuration, RestTemplate restTemplate,
			String apiVersion) {
		this(configuration, restTemplate, apiVersion, true);
	}

	public RestClientVcdBasicAuthInterceptor(ConfigurationVcd configuration, RestTemplate restTemplate,
			String apiVersion, Boolean useProviderAuth) {
		super(configuration, restTemplate);
		// Preserving API version check for backwards compatibility
		if (apiVersion.indexOf(".") == apiVersion.lastIndexOf(".") // This check eliminates the versions post VCFA 9.0.1. 40.0 changed to 9.0.0
				&& Double.parseDouble(apiVersion) >= Double.parseDouble(RestClientVcd.API_VERSION_38)
				&& Double.parseDouble(apiVersion) < Double.parseDouble(RestClientVcd.API_VERSION_40)) {
			logger.info("Unsupported version: {}", apiVersion);
			logger.warn("Detected VCD API version equal or greater than " + RestClientVcd.API_VERSION_38
					+ " and lower than " + RestClientVcd.API_VERSION_40 + ". Switching to using API version "
					+ RestClientVcd.API_VERSION_37);
			apiVersion = RestClientVcd.API_VERSION_37;
		}

		this.contentType = VcdApiHelper.buildMediaType(MediaType.APPLICATION_JSON_VALUE, apiVersion);
		this.useProviderAuth = useProviderAuth;
	}

	/**
	 * Intercepts all requests done to the VCD API.
	 * 
	 * @param request   Http request
	 * @param body      body
	 * @param execution request execution
	 * @return ClientHttpResponse
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			// Session URL needs to be adapted based on whether the user is Provider Admin
			// or part of specific organization
			String sessionUrl = this.useProviderAuth ? URL_PROVIDER_SESSION : URL_SESSION;

			if (!request.getURI().getPath().contains(sessionUrl)
					&& !request.getURI().getPath().contains(URL_PROVIDER_SESSION_FOR_TENANT)
					&& !request.getURI().getPath().contains(RestClientVcd.URL_VERSIONS)) {
				if (this.bearerToken == null) {
					acquireToken(request, sessionUrl);

					// In case of provider (System) domain additional calls are required that use
					// the bearer token acquired above in order to create a provider admin session
					// for a specific organization. An exception is embedded Orchestrator where
					// organization input is not required
					ConfigurationVcd configuration = this.getConfiguration();
					if (configuration.getDomain().equals(PROVIDER_DOMAIN) && configuration.getOrgName() != null) {
						acquireProviderToken(request);
					}
				}

				request.getHeaders().add(HEADER_VCLOUD_TOKEN, this.vcloudToken);
				request.getHeaders().add(HEADER_AUTHORIZATION, VcdApiHelper.buildBearerToken(this.bearerToken));
			}

			return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void acquireToken(HttpRequest request, String sessionUrl) throws JsonProcessingException {
		final URI tokenUri = generateUri(request, sessionUrl);
		logger.info("VCD Auth Token URL: {}", tokenUri);

		final HttpHeaders headers = generateDefaultHeaders();
		headers.add(HEADER_AUTHORIZATION, VcdApiHelper.buildBasicAuth(this.getConfiguration().getUsername(),
				this.getConfiguration().getDomain(), this.getConfiguration().getPassword()));

		final HttpEntity<String> entity = new HttpEntity<>(headers);
		final ResponseEntity<String> response = getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity,
				String.class);

		this.vcloudToken = response.getHeaders().get(HEADER_VCLOUD_TOKEN) == null ? null
				: response.getHeaders().get(HEADER_VCLOUD_TOKEN).get(0);
		this.bearerToken = response.getHeaders().get(HEADER_BEARER_TOKEN).get(0);
	}

	private void acquireProviderToken(HttpRequest request) {
		String oidcClientId = acquireOidcClientId(request);
		String organizationId = getOrganizationId(request);

		final URI providerTokenUri = generateUri(request, URL_PROVIDER_SESSION_FOR_TENANT);
		final HttpHeaders headers = generateDefaultHeaders();

		headers.add(HEADER_TENANT, organizationId);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();

		payload.add(JSON_PAYLOAD_GRANT_TYPE_KEY, JSON_PAYLOAD_GRANT_TYPE_VALUE);
		payload.add(JSON_PAYLOAD_SCOPE_KEY, JSON_PAYLOAD_SCOPE_VALUE);
		payload.add(JSON_PAYLOAD_ASSERTION_KEY, this.bearerToken);
		payload.add(JSON_REQUEST_CLIENT_ID_KEY, oidcClientId);

		final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(payload, headers);
		final ResponseEntity<String> response = getRestTemplate().exchange(providerTokenUri, HttpMethod.POST, entity,
				String.class);

		this.bearerToken = JsonParser.parseString(response.getBody()).getAsJsonObject()
				.get(JSON_RESPONSE_ACCESS_TOKEN_KEY)
				.getAsString();
	}

	/**
	 * Acquires the Automation Relying Party ID which can be used for generating
	 * provider admin user token for API operations on specific Organization
	 * 
	 * @param request
	 * @return
	 */
	private String acquireOidcClientId(HttpRequest request) {
		final URI relyingPartiesUri = generateUri(request, URL_RELYING_PARTIES);
		final HttpHeaders headers = generateDefaultHeaders();
		final HttpEntity<String> entity = new HttpEntity<>(headers);
		final ResponseEntity<String> response = getRestTemplate().exchange(relyingPartiesUri, HttpMethod.GET, entity,
				String.class);

		JsonElement values = JsonParser.parseString(response.getBody()).getAsJsonObject().get(JSON_RESPONSE_VALUES_KEY);
		JsonElement autoRelyingPartyElement = values.getAsJsonArray()
				.asList()
				.stream()
				.filter(e -> e.getAsJsonObject().get(JSON_RESPONSE_CLIENT_NAME_KEY).getAsString()
						.equals(AUTOMATION_RELYING_PARTY))
				.findFirst().orElseThrow();

		return autoRelyingPartyElement.getAsJsonObject().get(JSON_REQUEST_CLIENT_ID_KEY).getAsString();
	}

	private String getOrganizationId(HttpRequest request) {
		final URI organizationsUri = generateUri(request, URL_ORGANIZATIONS);
		final HttpHeaders headers = generateDefaultHeaders();
		final HttpEntity<String> entity = new HttpEntity<>(headers);
		final ResponseEntity<String> response = getRestTemplate().exchange(organizationsUri, HttpMethod.GET, entity,
				String.class);

		JsonElement values = JsonParser.parseString(response.getBody()).getAsJsonObject().get(JSON_RESPONSE_VALUES_KEY);
		String organizationName = this.getConfiguration().getOrgName();

		JsonElement organization = values.getAsJsonArray()
				.asList()
				.stream()
				.filter(e -> e.getAsJsonObject().get(JSON_RESPONSE_NAME_KEY).getAsString().equals(organizationName))
				.findFirst().orElseThrow();

		// The full organization ID contains prefix which needs to be removed, e.g.
		// "urn:vcloud:org:79af3eb2-11c6-416e-a700-9c79c0156f98"
		String fullOrganizationId = organization.getAsJsonObject().get(JSON_RESPONSE_ID_KEY).getAsString();
		return fullOrganizationId.substring(fullOrganizationId.lastIndexOf(":") + 1);
	}

	private URI generateUri(HttpRequest request, String url) {
		return UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme())
				.host(request.getURI().getHost())
				.port(request.getURI().getPort())
				.path(url)
				.build()
				.toUri();
	}

	private HttpHeaders generateDefaultHeaders() {
		final HttpHeaders headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(contentType);
		headers.setAccept(acceptableMediaTypes);
		return headers;
	}
}
