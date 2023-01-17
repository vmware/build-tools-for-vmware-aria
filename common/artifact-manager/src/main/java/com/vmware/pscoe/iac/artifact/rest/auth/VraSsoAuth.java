package com.vmware.pscoe.iac.artifact.rest.auth;

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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro.AuthProvider;
import com.vmware.pscoe.iac.artifact.rest.model.sso.SsoEndpointDto;

public class VraSsoAuth {
    private static final String CAFE_CLI_OWNER = "CAFE_CLI_OWNER";
    private static final String AUTHORIZATION_SERVICE_URL_VRA_CLOUD = "/csp/gateway/am/api/auth/api-tokens/authorize";
    private static final String AUTHORIZATION_SERVICE_URL_VRA_8 = "/csp/gateway/am/api/login";
    private static final String AUTHORIZATION_SERVICE_URL_VRA_7 = "/SAAS/t/%s/auth/oauthtoken";
    private static final String COMPONENT_REGISTRY_URL_VRA_7 = "/component-registry/endpoints/types/sso";
    private static final String VERSION_URL= "vco/api/about";
    private static final String DEFAULT_HTTP_SCHEME = "https";
    private static final String TOKEN_NAME = "cspAuthToken";
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";
    private static final String VRA_8_VERSION_PREFIX = "8.";
    private static final String HTTP_ENTITY_TYPE = "httpEntity";
    private static final String TOKEN_URL_TYPE = "tokenUri";

    private ConfigurationVro vroConfig;
    private RestTemplate restTemplate;
    private String serverVersion;

    public VraSsoAuth(ConfigurationVro config, RestTemplate restTemplate) {
        this.vroConfig = config;
        this.restTemplate = restTemplate;
        this.serverVersion = this.getVersion();
    }

    public SsoToken acquireToken() {
        return acquireToken(null);
    }

    public SsoToken acquireToken(String clientId) {
        final  Map<String, Object> tokenUriAndHttpEntity;
		final boolean isTokenAuth = this.serverVersion.endsWith("-saas-enabled") || !StringUtils.isEmpty(this.vroConfig.getRefreshToken());

        if (isTokenAuth) {
			tokenUriAndHttpEntity = getTokenUriAndHttpEntityVraCloud();
		} else if (this.serverVersion.startsWith(VRA_8_VERSION_PREFIX)) {
            tokenUriAndHttpEntity = getTokenUriAndHttpEntityVra8();
        } else {
            tokenUriAndHttpEntity = getTokenUriAndHttpEntityVra7(clientId);
        }

        final URI tokenUri = (URI) tokenUriAndHttpEntity.get(TOKEN_URL_TYPE);
        final HttpEntity<?> httpEntity = (HttpEntity<?>) tokenUriAndHttpEntity.get(HTTP_ENTITY_TYPE);
        try {
            final ResponseEntity<String> response = this.restTemplate.exchange(tokenUri, HttpMethod.POST, httpEntity, String.class);
            final DocumentContext responseBody = JsonPath.parse(response.getBody());
            final String tokenValue = isTokenAuth
				? responseBody.read("$.access_token")
				: responseBody.read("$." + TOKEN_NAME);

            return new SsoToken(tokenValue, AuthProvider.VRA);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to acquire token for VRO SSO authentication: %s", e.getMessage()));
        }
    }

    public Map<String, Object> getTokenUriAndHttpEntityVra7(String clientId){
        if (StringUtils.isEmpty(clientId)) {
            clientId = retrieveClientId();
        }

        final URI tokenUri = UriComponentsBuilder.newInstance()
            .scheme(DEFAULT_HTTP_SCHEME)
            .host(this.vroConfig.getAuthHost())
            .port(this.vroConfig.getAuthPort())
            .path(String.format(AUTHORIZATION_SERVICE_URL_VRA_7, this.vroConfig.getTenant()))
            .queryParam("grant_type", "password")
            .build().toUri();

        // Prepare request payload data
        final MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
        payload.add("username", this.vroConfig.getUsername());
        payload.add("password", this.vroConfig.getPassword());
        payload.add("domain", this.vroConfig.getDomain());
        payload.add("client_id", clientId);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(payload, headers);

        final Map<String, Object> tokenUriAndHttpEntity = new HashMap<>();
        tokenUriAndHttpEntity.put(TOKEN_URL_TYPE, tokenUri);
        tokenUriAndHttpEntity.put(HTTP_ENTITY_TYPE, entity);

        return tokenUriAndHttpEntity;
    }

    public Map<String, Object> getTokenUriAndHttpEntityVra8(){
        final URI tokenUri = UriComponentsBuilder.newInstance()
            .scheme(DEFAULT_HTTP_SCHEME)
            .host(this.vroConfig.getAuthHost())
            .port(this.vroConfig.getAuthPort())
            .path(AUTHORIZATION_SERVICE_URL_VRA_8)
            .queryParam("tenant", this.vroConfig.getTenant())
            .queryParam(TOKEN_NAME, "").build().toUri();

        // Prepare request payload data
        final Map<String, String> payload = new HashMap<>();
        payload.put("username", this.vroConfig.getUsername());
        payload.put("password", this.vroConfig.getPassword());
        payload.put("domain", this.vroConfig.getDomain());

        Gson gsonObject = new Gson();
        String stringPayload = gsonObject.toJson(payload);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> entity = new HttpEntity<>(stringPayload, headers);

        final Map<String, Object> tokenUriAndHttpEntity = new HashMap<>();
        tokenUriAndHttpEntity.put(TOKEN_URL_TYPE, tokenUri);
        tokenUriAndHttpEntity.put(HTTP_ENTITY_TYPE, entity);

        return tokenUriAndHttpEntity;
    }

	public Map<String, Object> getTokenUriAndHttpEntityVraCloud(){
		final URI tokenUri = UriComponentsBuilder.newInstance()
			.scheme(DEFAULT_HTTP_SCHEME)
			.host(this.vroConfig.getAuthHost())
			.port(this.vroConfig.getAuthPort())
			.path(AUTHORIZATION_SERVICE_URL_VRA_CLOUD).build().toUri();

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("api_token",this.vroConfig.getRefreshToken());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		final Map<String, Object> tokenUriAndHttpEntity = new HashMap<>();
		tokenUriAndHttpEntity.put(TOKEN_URL_TYPE, tokenUri);
		tokenUriAndHttpEntity.put(HTTP_ENTITY_TYPE, entity);

		return tokenUriAndHttpEntity;
	}

    public String retrieveClientId() {
        final URI tokenUri = UriComponentsBuilder.newInstance()
                .scheme(DEFAULT_HTTP_SCHEME)
                .host(this.vroConfig.getAuthHost())
                .port(this.vroConfig.getAuthPort())
                .path(COMPONENT_REGISTRY_URL_VRA_7).build().toUri();

        final SsoEndpointDto ssoEndpoint = this.restTemplate.getForObject(tokenUri, SsoEndpointDto.class);

        return ssoEndpoint.getEndPointAttributes().stream().filter(attr -> CAFE_CLI_OWNER.equals(attr.getKey())).findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find the SSO Client ID")).getValue();
    }

    private String getVersion() {
        // vRA Cloud doesn't have vRO services and it's auth host (console.cloud.vmware.com)
        // is different than the Extensibility Proxy's vRO address (ext-proxy01.corp.local),
        // hence the /vco/api/about is not available on the 'authHost' address, but on the 'host' address
        final URI versionUri = UriComponentsBuilder.newInstance()
                .scheme(DEFAULT_HTTP_SCHEME)
                .host(this.vroConfig.getHost())
                .port(this.vroConfig.getPort())
                .path(VERSION_URL).build().toUri();

        ResponseEntity<String> response = restTemplate.exchange(versionUri, HttpMethod.GET, getDefaultHttpEntity(), String.class);

		Configuration conf = Configuration.defaultConfiguration();
		conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
		DocumentContext jsonDoc = JsonPath.using(conf).parse(response.getBody());
		JSONArray saasCheck = jsonDoc.read("$.features[?(@.name == 'saas-enabled')].name");

		String version = jsonDoc.read("$.version");
		if (version != null && !saasCheck.isEmpty()) {
			version += "-saas-enabled";
		}

        return version;
    }

    private HttpEntity<String> getDefaultHttpEntity() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(headers);
    }

    public static class SsoToken {
        private String value;
        private String tokenType;
        private AuthProvider type;

        public SsoToken(String token, AuthProvider type) {
            this.value = token;
            this.type = type;
            this.tokenType = DEFAULT_TOKEN_TYPE;
        }

        public String getValue() {
            return value;
        }

        public boolean isExpired() {
            return Boolean.TRUE;
        }

        public AuthProvider getType() {
            return type;
        }

        public String getTokenType() {
            return tokenType;
        }
    }
}
