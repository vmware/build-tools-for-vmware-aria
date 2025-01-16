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
package com.vmware.pscoe.iac.artifact.aria.operations.rest;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.aria.operations.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.rest.RestClientRequestInterceptor;

public class RestClientVropsAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVrops> {
	private static final String VROPS_AUTH_TOKEN_URI = "/suite-api/api/auth/token/acquire";
	private static final String AUTH_HEADER_NAME = "Authorization";
	private static final String VROPS_AUTH_HEADER_KEY = "vRealizeOpsToken";

	private final Logger logger = LoggerFactory.getLogger(RestClientVropsAuthNInterceptor.class);

	private String vropsAuthToken;
	private LocalDateTime vropsTokenExpirationTime;

	public RestClientVropsAuthNInterceptor(ConfigurationVrops configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			if (!hasValidTokens() && !isAccessTokenRequests(request)) {
				acquireToken(request);
			}
			if (this.vropsAuthToken != null) {
				request.getHeaders().remove(AUTH_HEADER_NAME);
				request.getHeaders().add(AUTH_HEADER_NAME, VROPS_AUTH_HEADER_KEY + " " + this.vropsAuthToken);
			}

			return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void acquireToken(HttpRequest request) throws JsonProcessingException {
		logger.info("Acquiring VROPS auth token.");
		final URI vropsTokenUri = UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme())
				.host(getConfiguration().getHost())
				.port(getConfiguration().getPort()).path(VROPS_AUTH_TOKEN_URI).build().toUri();
		logger.info("VROPS Auth Token URL: {}", vropsTokenUri);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		final Map<String, String> payload = new HashMap<>();
		payload.put("username", getConfiguration().getVropsRestUser());
		payload.put("password", getConfiguration().getVropsRestPassword());
		payload.put("authSource", getConfiguration().getVropsAuthSource());

		final HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(payload), headers);
		ResponseEntity<String> response = getRestTemplate().exchange(vropsTokenUri, HttpMethod.POST, entity,
				String.class);
		final DocumentContext responseBody = JsonPath.parse(response.getBody());

		final long ttl = responseBody.read("$.validity");
		this.vropsTokenExpirationTime = LocalDateTime.now(ZoneOffset.UTC).plus(ttl,
				ChronoField.MINUTE_OF_HOUR.getBaseUnit());
		this.vropsAuthToken = responseBody.read("$.token");
	}

	private boolean isAccessTokenRequests(HttpRequest request) {
		return request.getURI().getPath().contains(VROPS_AUTH_TOKEN_URI);
	}

	private boolean hasValidTokens() {
		return vropsAuthToken != null && LocalDateTime.now(ZoneOffset.UTC).isBefore(vropsTokenExpirationTime);
	}
}
