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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.rest.helpers.VcdApiHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
	 * session api path.
	 */
	private static final String URL_SESSION = "/api/sessions";
	
	/**
	 * version api path.
	 */
	private static final String URL_VERSION = "/api/versions";

	/**
	 * vcloud header key.
	 */
	private static final String HEADER_VCLOUD_TOKEN = "x-vcloud-authorization";

	/**
	 * bearer token header key.
	 */
	private static final String HEADER_BEARER_TOKEN = "x-vmware-vcloud-access-token";

	/**
	 * authorization header key.
	 */
	private static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * api version that is supported.
	 */
	private static final String API_VERSION_37 = "37.0";

	/**
	 * api version that is not yet supported.
	 */
	private static final String API_VERSION_38 = "38.0";

	protected RestClientVcdBasicAuthInterceptor(ConfigurationVcd configuration, RestTemplate restTemplate,
			String apiVersion) {
		super(configuration, restTemplate);
		if (Double.parseDouble(apiVersion) >= Double.parseDouble(API_VERSION_38)) {
			logger.warn("Detected vCD API version equal or greater than " + API_VERSION_38 + ". Switching to using API version " + API_VERSION_37);
			apiVersion = API_VERSION_37;
		}

		this.contentType = VcdApiHelper.buildMediaType("application/*+json", apiVersion);
	}

	/**
	 * Intercepts all requests done to the vCD API.
	 * 
	 * @param request Http request
	 * @param body body
	 * @param execution request execution
	 * @return ClientHttpResponse
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			if (!request.getURI().getPath().contains(URL_SESSION) && !request.getURI().getPath().contains(URL_VERSION)) {
				if (this.vcloudToken == null) {
					logger.info("Aquiring vCD auth token...");
					acquireToken(request);
					logger.info("vCD auth token aquired");
				}
				
				request.getHeaders().add(HEADER_VCLOUD_TOKEN, this.vcloudToken);
				request.getHeaders().add(HEADER_AUTHORIZATION, VcdApiHelper.buildBearerToken(this.bearerToken));
			}
			
			return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void acquireToken(HttpRequest request) throws JsonProcessingException {
		final URI tokenUri = UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme())
				.host(request.getURI().getHost()).port(request.getURI().getPort()).path(URL_SESSION).build().toUri();

		// Prepare Headers
		final HttpHeaders headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(contentType);

		headers.setAccept(acceptableMediaTypes);
		headers.add(HEADER_AUTHORIZATION, VcdApiHelper.buildBasicAuth(this.getConfiguration().getUsername(),
				this.getConfiguration().getDomain(), this.getConfiguration().getPassword()));
		final HttpEntity<String> entity = new HttpEntity<>(headers);
		
		logger.warn("token uri: " + tokenUri);
		logger.warn("Auth: " + VcdApiHelper.buildBasicAuth(this.getConfiguration().getUsername(),
				this.getConfiguration().getDomain(), this.getConfiguration().getPassword()));

		final ResponseEntity<String> response = getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity,
				String.class);
		this.vcloudToken = response.getHeaders().get(HEADER_VCLOUD_TOKEN).get(0);
		this.bearerToken = response.getHeaders().get(HEADER_BEARER_TOKEN).get(0);
	}

}
