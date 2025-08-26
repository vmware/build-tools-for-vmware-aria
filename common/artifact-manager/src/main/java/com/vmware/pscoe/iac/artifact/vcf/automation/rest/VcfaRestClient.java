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
package com.vmware.pscoe.iac.artifact.vcf.automation.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.VcfaConfiguration;

public class VcfaRestClient {
    
    private final VcfaConfiguration configuration;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;

    public VcfaRestClient(VcfaConfiguration configuration) {
        this.configuration = configuration;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClientBuilder.create().build();
    }

    public void authenticate() throws IOException {
        if (configuration.getApiToken() != null) {
            this.authToken = configuration.getApiToken();
        } else {
            authenticateWithUsernamePassword();
        }
    }

    private void authenticateWithUsernamePassword() throws IOException {
        String loginType = configuration.getLoginType();
        String authString;
        String loginUrl;

        if ("provider".equals(loginType)) {
            // Provider login: USER@System:PASS
            authString = configuration.getUsername() + "@System:" + configuration.getPassword();
            loginUrl = configuration.getBaseUrl() + "/cloudapi/1.0.0/sessions/provider";
        } else {
            // Tenant login: USER@TENANT_NAME:PASS  
            String tenantName = configuration.getTenantName();
            if (tenantName == null || tenantName.isEmpty()) {
                throw new IOException("Tenant name is required for tenant login");
            }
            authString = configuration.getUsername() + "@" + tenantName + ":" + configuration.getPassword();
            loginUrl = configuration.getBaseUrl() + "/cloudapi/1.0.0/sessions";
        }

        String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        
        HttpPost request = new HttpPost(loginUrl);
        request.setHeader("Authorization", authHeader);
        request.setHeader("Accept", "application/*;version=" + configuration.getApiVersion());
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() == 200) {
                // Extract the x-vmware-vcloud-access-token from response headers
                org.apache.hc.core5.http.Header tokenHeader = response.getFirstHeader("x-vmware-vcloud-access-token");
                if (tokenHeader != null) {
                    this.authToken = tokenHeader.getValue().trim();
                } else {
                    throw new IOException("Authentication failed: x-vmware-vcloud-access-token header not found in response");
                }
            } else {
                String responseBody = getResponseBody(response.getEntity());
                throw new IOException("Authentication failed: " + response.getCode() + " - " + responseBody);
            }
        }
    }

    private void setAuthHeader(HttpUriRequestBase request) {
        if (authToken != null) {
            request.setHeader("x-vmware-vcloud-access-token", authToken);
            request.setHeader("Accept", "application/*;version=" + configuration.getApiVersion());
        }
    }

    private String getResponseBody(HttpEntity entity) throws IOException {
        try {
            return EntityUtils.toString(entity);
        } catch (ParseException e) {
            throw new IOException("Failed to parse response body", e);
        }
    }

    public List<Map<String, Object>> getBlueprints() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/blueprint/api/blueprints");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get blueprints: " + response.getCode());
            }
        }
    }

    public Map<String, Object> getBlueprintById(String id) throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/blueprint/api/blueprints/" + id);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                return objectMapper.readValue(responseBody, Map.class);
            } else {
                throw new IOException("Failed to get blueprint: " + response.getCode());
            }
        }
    }

    public Map<String, Object> createBlueprint(Map<String, Object> blueprint) throws IOException {
        HttpPost request = new HttpPost(configuration.getBaseUrl() + "/blueprint/api/blueprints");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        String requestBody = objectMapper.writeValueAsString(blueprint);
        request.setEntity(new StringEntity(requestBody));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 201) {
                return objectMapper.readValue(responseBody, Map.class);
            } else {
                throw new IOException("Failed to create blueprint: " + response.getCode());
            }
        }
    }

    public Map<String, Object> updateBlueprint(String id, Map<String, Object> blueprint) throws IOException {
        HttpPut request = new HttpPut(configuration.getBaseUrl() + "/blueprint/api/blueprints/" + id);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        String requestBody = objectMapper.writeValueAsString(blueprint);
        request.setEntity(new StringEntity(requestBody));
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                return objectMapper.readValue(responseBody, Map.class);
            } else {
                throw new IOException("Failed to update blueprint: " + response.getCode());
            }
        }
    }

    public void deleteBlueprint(String id) throws IOException {
        HttpDelete request = new HttpDelete(configuration.getBaseUrl() + "/blueprint/api/blueprints/" + id);
        setAuthHeader(request);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() != 204) {
                throw new IOException("Failed to delete blueprint: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getCatalogItems() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/catalog/api/items");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get catalog items: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getContentSources() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/content/api/sources");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get content sources: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getCustomResources() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/resource/api/types");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get custom resources: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getResourceActions() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/catalog/api/resource-actions");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get resource actions: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getSubscriptions() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/provisioning/uerp/provisioning/mgmt/event-broker/subscriptions");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get subscriptions: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getPolicies() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/policy/api/policies");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get policies: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getPropertyGroups() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/blueprint/api/property-groups");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get property groups: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getCatalogEntitlements() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/catalog/api/entitlements");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get catalog entitlements: " + response.getCode());
            }
        }
    }

    public List<Map<String, Object>> getScenarios() throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/catalog/api/notification/scenarios");
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = getResponseBody(response.getEntity());
            
            if (response.getCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                return (List<Map<String, Object>>) result.get("content");
            } else {
                throw new IOException("Failed to get scenarios: " + response.getCode());
            }
        }
    }

    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
