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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

public class RestClientVraNgAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVraNg> {
    private static final String SERVICE_REFRESH_TOKEN = "/csp/gateway/am/api/auth/api-tokens/authorize";
    private static final String SERVICE_CREDENTIALS = "/csp/gateway/am/api/login";
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";

    private final Logger logger = LoggerFactory.getLogger(RestClientVraNgAuthNInterceptor.class);

    private String token;
    private String tokenType;
    private LocalDateTime tokenExpirationDate;

    protected RestClientVraNgAuthNInterceptor(ConfigurationVraNg configuration, RestTemplate restTemplate) {
        super(configuration, restTemplate);
        this.tokenType = DEFAULT_TOKEN_TYPE;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            if (!isRequestInBlackList(request) && !hasValidToken()) {
                logger.info("Request URL: {}", request.getURI());
                acquireToken(request);
            }
            if (this.token != null) {
               request.getHeaders().add("Authorization", this.tokenType + " " + this.token);
            }
 
            return execution.execute(request, body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void acquireToken(HttpRequest request) throws JsonProcessingException {
        String refreshToken;
        if (getConfiguration().getRefreshToken() != null && !getConfiguration().getRefreshToken().isEmpty()) {
            logger.info("Acquiring with refresh token");
            refreshToken = getConfiguration().getRefreshToken();
        } else {
            logger.info("Acquiring refresh token with credentials");
            ResponseEntity<String> requestWithCredentialsResponse = this.requestWithCredentials(request);
            DocumentContext requestWithCredentialsResponseBody = JsonPath.parse(requestWithCredentialsResponse.getBody());
            refreshToken = requestWithCredentialsResponseBody.read("$.refresh_token");
        }

        ResponseEntity<String> response = this.requestWithRefreshToken(request, refreshToken);
        DocumentContext responseBody = JsonPath.parse(response.getBody());
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        final int expiresIn = responseBody.jsonString().contains("expires_in") ? responseBody.read("$.expires_in") : 0;
        this.tokenExpirationDate = now.plus(expiresIn, ChronoField.SECOND_OF_MINUTE.getBaseUnit());
        this.token = responseBody.jsonString().contains("access_token") ? responseBody.read("$.access_token") : null;
        String tokenTypeFromResponse = responseBody.jsonString().contains("token_type") ? responseBody.read("$.token_type") : null;
        this.setTokenType(tokenTypeFromResponse);
    }

    private ResponseEntity<String> requestWithRefreshToken(HttpRequest request, String refreshToken) {
        final URI tokenUri = UriComponentsBuilder.newInstance()
            .scheme(request.getURI().getScheme())
            .host(this.getConfiguration().getAuthHost())
			.port(this.getConfiguration().getPort())
            .path(SERVICE_REFRESH_TOKEN).build().toUri();

        logger.info("Token URI: {}", tokenUri);
        // Prepare Headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare Request
        final MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
        payload.add("refresh_token", refreshToken);

        final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(payload, headers);

        return getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity, String.class);
    }

    private ResponseEntity<String> requestWithCredentials(HttpRequest request) throws JsonProcessingException {
        final URI tokenUri = UriComponentsBuilder.newInstance()
            .scheme(request.getURI().getScheme())
            .host(this.getConfiguration().getAuthHost())
			.port(this.getConfiguration().getPort())
            .path(SERVICE_CREDENTIALS)
            .queryParam("access_token")
            .build().toUri();

        logger.info("Auth URL: {}", tokenUri);

        // Prepare Headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // Prepare Request
        final Map<String, String> payload = new HashMap<>();

        // try to set domain
        String domain = getConfiguration().getDomain();
        if (domain != null) {
            logger.debug("Detected domain '{}'", domain);
            payload.put("domain", domain);
        } else {
            logger.debug("No domain detected");
        }
        payload.put("username", getConfiguration().getUsername());
        payload.put("password", getConfiguration().getPassword());
        final String requestJson = new ObjectMapper().writeValueAsString(payload);
        final HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        return getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity, String.class);
    }

    private boolean isRequestInBlackList(HttpRequest request) {
        return request.getURI().getPath().contains(SERVICE_REFRESH_TOKEN) || request.getURI().getPath().contains(SERVICE_CREDENTIALS);
    }

    private boolean hasValidToken() {
        return token != null && LocalDateTime.now(ZoneOffset.UTC).isBefore(tokenExpirationDate);
    }

    private boolean isValidTokenType(String tokenType) {
        return tokenType != null && tokenType.length() > 0;
    }

    private void setTokenType(String newTokenType) {
        if (this.isValidTokenType(newTokenType)) {
            // Capitalized for case sensitive Auth schemes
            this.tokenType = newTokenType.substring(0,1).toUpperCase() + newTokenType.substring(1).toLowerCase();
        } else if (this.tokenType == null) {
            this.tokenType = DEFAULT_TOKEN_TYPE;
        }
    }

}
