package com.vmware.pscoe.iac.artifact.rest.client.vrli;

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

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.rest.RestClientRequestInterceptor;
import org.apache.commons.lang3.StringUtils;
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
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;

public class RestClientVrliAuthInterceptor extends RestClientRequestInterceptor<ConfigurationVrli> {

    private static final String VRLI_AUTH_TOKEN_URI = "/api/v1/sessions";
    private static final String VROPS_AUTH_TOKEN_URI = "/suite-api/api/auth/token/acquire";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String VRLI_AUTH_HEADER_KEY = "Bearer";
    private static final String VROPS_AUTH_HEADER_KEY = "vRealizeOpsToken";

	private final Logger logger = LoggerFactory.getLogger(RestClientVrliAuthInterceptor.class);

    private String vrliAuthToken;
    private String vropsAuthToken;
    private LocalDateTime vrliTokenExpirationTime;
    private LocalDateTime vropsTokenExpirationTime;

	public RestClientVrliAuthInterceptor(ConfigurationVrli configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
            if (!hasValidTokens() && !isAccessTokenRequests(request)) {
                acquireTokens(request);
			}
            request.getHeaders().add(AUTH_HEADER_NAME, VRLI_AUTH_HEADER_KEY + " " + this.vrliAuthToken);
            if (this.vropsAuthToken != null && !request.getURI().getHost().contains(getConfiguration().getHost())) {
                request.getHeaders().remove(AUTH_HEADER_NAME);
                request.getHeaders().add(AUTH_HEADER_NAME, VROPS_AUTH_HEADER_KEY + " " + this.vropsAuthToken);
            }

            return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    private void acquireTokens(HttpRequest request) throws JsonProcessingException {
        logger.info("Acquiring VRLI token.");
        acquireVrliToken(request);
        if (!StringUtils.isEmpty(getConfiguration().getIntegrationVropsAuthHost())) {
            logger.info("Acquiring VROPS token.");
            acquireVropsToken(request);
        }
    }

    private void acquireVrliToken(HttpRequest request) throws JsonProcessingException {
        final URI vrliTokenUri = UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme()).host(getConfiguration().getHost())
                .port(getConfiguration().getPort()).path(VRLI_AUTH_TOKEN_URI).build().toUri();
        logger.info("VRLI Auth Token URL: {}", vrliTokenUri);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        final Map<String, String> payload = new HashMap<>();
        payload.put("username", getConfiguration().getUsername());
        payload.put("password", getConfiguration().getPassword());
        payload.put("provider", getConfiguration().getProvider());

        final HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(payload), headers);
        ResponseEntity<String> response = getRestTemplate().exchange(vrliTokenUri, HttpMethod.POST, entity, String.class);
        final DocumentContext responseBody = JsonPath.parse(response.getBody());

        final int ttl = responseBody.read("$.ttl");
        this.vrliTokenExpirationTime = LocalDateTime.now(ZoneOffset.UTC).plus(ttl, ChronoField.MINUTE_OF_HOUR.getBaseUnit());
        this.vrliAuthToken = responseBody.read("$.sessionId");
    }

    private void acquireVropsToken(HttpRequest request) throws JsonProcessingException {
        final URI vropsTokenUri = UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme()).host(getConfiguration().getIntegrationVropsAuthHost())
                .port(getConfiguration().getIntegrationVropsAuthPort()).path(VROPS_AUTH_TOKEN_URI).build().toUri();
        logger.info("VROPS Integration Auth Token URL: {}", vropsTokenUri);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        final Map<String, String> payload = new HashMap<>();
        payload.put("username", getConfiguration().getIntegrationVropsAuthUser());
        payload.put("password", getConfiguration().getIntegrationVropsAuthPassword());
        payload.put("authSource", getConfiguration().getIntegrationVropsAuthSource());

        final HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(payload), headers);
        ResponseEntity<String> response = getRestTemplate().exchange(vropsTokenUri, HttpMethod.POST, entity, String.class);
        final DocumentContext responseBody = JsonPath.parse(response.getBody());

        final long ttl = responseBody.read("$.validity");
        this.vropsTokenExpirationTime = LocalDateTime.now(ZoneOffset.UTC).plus(ttl, ChronoField.MINUTE_OF_HOUR.getBaseUnit());
        this.vropsAuthToken = responseBody.read("$.token");
    }

    private boolean isAccessTokenRequests(HttpRequest request) {
        if (!StringUtils.isEmpty(getConfiguration().getIntegrationVropsAuthHost())) {
            return request.getURI().getPath().contains(VRLI_AUTH_TOKEN_URI) || request.getURI().getPath().contains(VROPS_AUTH_TOKEN_URI);
        }

        return request.getURI().getPath().contains(VRLI_AUTH_TOKEN_URI);
    }

    private boolean hasValidTokens() {
        if (!StringUtils.isEmpty(getConfiguration().getIntegrationVropsAuthHost())) {
            return vrliAuthToken != null && LocalDateTime.now(ZoneOffset.UTC).isBefore(vrliTokenExpirationTime) && vropsAuthToken != null
                    && LocalDateTime.now(ZoneOffset.UTC).isBefore(vropsTokenExpirationTime);
        }

        return vrliAuthToken != null && LocalDateTime.now(ZoneOffset.UTC).isBefore(vrliTokenExpirationTime);
	}

}
