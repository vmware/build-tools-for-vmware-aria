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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class RestClientVraCafeAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVra> {

    private final Logger logger = LoggerFactory.getLogger(RestClientVraCafeAuthNInterceptor.class);

    private String token;
    private LocalDateTime tokenExpirationDate;

    protected RestClientVraCafeAuthNInterceptor(ConfigurationVra configuration, RestTemplate restTemplate) {
        super(configuration, restTemplate);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            if (!request.getURI().getPath().contains("/identity/api/tokens") &&
                (token == null || tokenExpirationDate.isBefore(LocalDateTime.now()))) {
                logger.info("vRA authentication token has expired. Acquiring a new one.");
                acquireToken(request);
            }

            request.getHeaders().add("Authorization", "Bearer " + this.token);
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void acquireToken(HttpRequest request) throws JsonProcessingException {
        final URI tokenUri = UriComponentsBuilder.newInstance()
            .scheme(request.getURI().getScheme())
            .host(request.getURI().getHost())
            .port(request.getURI().getPort())
            .path("/identity/api/tokens").build().toUri();

        // Prepare Headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // Prepare Request
        final Map<String, String> payload = new HashMap<>();
        payload.put("username", getConfiguration().getUsername());
        payload.put("domain", getConfiguration().getDomain());
        payload.put("password", getConfiguration().getPassword());
        payload.put("tenant", getConfiguration().getTenant());

        final String requestJson = new ObjectMapper().writeValueAsString(payload);
        final HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        final ResponseEntity<String> response =
            getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity, String.class);
        final DocumentContext responseBody = JsonPath.parse(response.getBody());
        final Instant expires = Instant.parse(responseBody.read("$.expires"));

        this.tokenExpirationDate = LocalDateTime.ofInstant(expires, ZoneOffset.UTC);
        this.token = responseBody.read("$.id");
    }

}
