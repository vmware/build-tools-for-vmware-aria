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

    /**
     * Execute the request and return response body while validating expected codes.
     * If no expected codes are provided, 200 is expected.
     */
    private String executeRequestWithExpected(HttpUriRequestBase request, int... expected) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int code = response.getCode();
            String responseBody = "";
            if (response.getEntity() != null) {
                responseBody = getResponseBody(response.getEntity());
            }

            boolean ok;
            if (expected == null || expected.length == 0) {
                ok = (code == 200);
            } else {
                ok = false;
                for (int c : expected) {
                    if (c == code) {
                        ok = true;
                        break;
                    }
                }
            }

            if (!ok) {
                throw new IOException("Request failed: " + code + " " + responseBody);
            }

            return responseBody;
        }
    }

    private List<Map<String,Object>> getList(String relativePath) throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + relativePath);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String responseBody = executeRequestWithExpected(request, 200);
        Map<String,Object> result = objectMapper.readValue(responseBody, Map.class);
        return (List<Map<String,Object>>) result.get("content");
    }

    private Map<String,Object> postMap(String relativePath, Map<String,Object> payload, int... expected) throws IOException {
        HttpPost request = new HttpPost(configuration.getBaseUrl() + relativePath);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(payload);
        request.setEntity(new StringEntity(requestBody));
        String responseBody = executeRequestWithExpected(request, expected.length == 0 ? new int[]{201} : expected);
        if (responseBody == null || responseBody.isEmpty()) return null;
        return objectMapper.readValue(responseBody, Map.class);
    }

    private Map<String,Object> putMap(String relativePath, Map<String,Object> payload, int... expected) throws IOException {
        HttpPut request = new HttpPut(configuration.getBaseUrl() + relativePath);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(payload);
        request.setEntity(new StringEntity(requestBody));
        String responseBody = executeRequestWithExpected(request, expected.length == 0 ? new int[]{200} : expected);
        if (responseBody == null || responseBody.isEmpty()) return null;
        return objectMapper.readValue(responseBody, Map.class);
    }

    private void deletePath(String relativePath, int... expected) throws IOException {
        HttpDelete request = new HttpDelete(configuration.getBaseUrl() + relativePath);
        setAuthHeader(request);
        executeRequestWithExpected(request, expected.length == 0 ? new int[]{204} : expected);
    }

    public List<Map<String, Object>> getBlueprints() throws IOException {
        return getList("/blueprint/api/blueprints");
    }

    public Map<String, Object> getBlueprintById(String id) throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/blueprint/api/blueprints/" + id);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String responseBody = executeRequestWithExpected(request, 200);
        return objectMapper.readValue(responseBody, Map.class);
    }

    public Map<String, Object> createBlueprint(Map<String, Object> blueprint) throws IOException {
        return postMap("/blueprint/api/blueprints", blueprint, 201);
    }

    public Map<String, Object> updateBlueprint(String id, Map<String, Object> blueprint) throws IOException {
        return putMap("/blueprint/api/blueprints/" + id, blueprint, 200);
    }

    public Map<String, Object> createBlueprint(com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint blueprint) throws IOException {
        return createBlueprint(objectMapper.convertValue(blueprint, Map.class));
    }

    public Map<String, Object> updateBlueprint(String id, com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint blueprint) throws IOException {
        return updateBlueprint(id, objectMapper.convertValue(blueprint, Map.class));
    }

    public void deleteBlueprint(String id) throws IOException {
        deletePath("/blueprint/api/blueprints/" + id, 204);
    }

    public List<Map<String, Object>> getCatalogItems() throws IOException {
        return getList("/catalog/api/items");
    }

    public List<Map<String, Object>> getContentSources() throws IOException {
        return getList("/content/api/sources");
    }

    public List<Map<String, Object>> getCustomResources() throws IOException {
        return getList("/resource/api/types");
    }

    public List<Map<String, Object>> getResourceActions() throws IOException {
        return getList("/catalog/api/resource-actions");
    }

    public List<Map<String, Object>> getSubscriptions() throws IOException {
        return getList("/provisioning/uerp/provisioning/mgmt/event-broker/subscriptions");
    }

    public List<Map<String, Object>> getPolicies() throws IOException {
        return getList("/policy/api/policies");
    }

    public List<Map<String, Object>> getPropertyGroups() throws IOException {
        return getList("/blueprint/api/property-groups");
    }

    public List<Map<String, Object>> getCatalogEntitlements() throws IOException {
        return getList("/catalog/api/entitlements");
    }

    public List<Map<String, Object>> getScenarios() throws IOException {
        return getList("/catalog/api/notification/scenarios");
    }

    public Map<String, Object> createContentSource(Map<String, Object> payload) throws IOException {
        return postMap("/content/api/sources", payload, 201, 200);
    }

    public Map<String, Object> createContentSource(com.vmware.pscoe.iac.artifact.vcf.automation.models.ContentSource payload) throws IOException {
        return createContentSource(objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updateContentSource(String id, com.vmware.pscoe.iac.artifact.vcf.automation.models.ContentSource payload) throws IOException {
        return updateContentSource(id, objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updateContentSource(String id, Map<String, Object> payload) throws IOException {
        return putMap("/content/api/sources/" + id, payload, 200);
    }

    public void deleteContentSource(String id) throws IOException {
        deletePath("/content/api/sources/" + id, 204, 200);
    }

    public Map<String, Object> createPropertyGroup(Map<String, Object> payload) throws IOException {
        return postMap("/properties/api/property-groups", payload, 201, 200);
    }

    public Map<String, Object> createPropertyGroup(com.vmware.pscoe.iac.artifact.vcf.automation.models.PropertyGroup payload) throws IOException {
        return createPropertyGroup(objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updatePropertyGroup(String id, com.vmware.pscoe.iac.artifact.vcf.automation.models.PropertyGroup payload) throws IOException {
        return updatePropertyGroup(id, objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updatePropertyGroup(String id, Map<String, Object> payload) throws IOException {
        return putMap("/properties/api/property-groups/" + id, payload, 200);
    }

    public void deletePropertyGroup(String id) throws IOException {
        deletePath("/properties/api/property-groups/" + id, 204, 200);
    }

    public Map<String, Object> createPolicy(Map<String, Object> payload) throws IOException {
        return postMap("/policy/api/policies", payload, 200,201,202);
    }

    public Map<String, Object> createPolicy(com.vmware.pscoe.iac.artifact.vcf.automation.models.Policy payload) throws IOException {
        return createPolicy(objectMapper.convertValue(payload, Map.class));
    }

    public void deletePolicy(String id) throws IOException {
        deletePath("/policy/api/policies/" + id, 204,200);
    }

    public Map<String, Object> createResourceAction(Map<String, Object> payload) throws IOException {
        return postMap("/form-service/api/custom/resource-actions", payload, 200,201);
    }

    public Map<String, Object> createResourceAction(com.vmware.pscoe.iac.artifact.vcf.automation.models.ResourceAction payload) throws IOException {
        return createResourceAction(objectMapper.convertValue(payload, Map.class));
    }

    public void deleteResourceAction(String id) throws IOException {
        deletePath("/form-service/api/custom/resource-actions/" + id, 200,204);
    }

    public Map<String, Object> createCustomResourceType(Map<String, Object> payload) throws IOException {
        return postMap("/form-service/api/custom/resource-types", payload, 200,201);
    }

    public Map<String, Object> createCustomResourceType(com.vmware.pscoe.iac.artifact.vcf.automation.models.CustomResourceType payload) throws IOException {
        return createCustomResourceType(objectMapper.convertValue(payload, Map.class));
    }

    public void deleteCustomResourceType(String id) throws IOException {
        deletePath("/form-service/api/custom/resource-types/" + id, 200,204);
    }

    public Map<String, Object> createCatalogItem(Map<String, Object> payload) throws IOException {
        return postMap("/catalog/api/items", payload, 201,200);
    }

    public Map<String, Object> createCatalogItem(com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogItem payload) throws IOException {
        return createCatalogItem(objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updateCatalogItem(String id, com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogItem payload) throws IOException {
        return updateCatalogItem(id, objectMapper.convertValue(payload, Map.class));
    }

    public Map<String, Object> updateCatalogItem(String id, Map<String, Object> payload) throws IOException {
        return putMap("/catalog/api/items/" + id, payload, 200);
    }

    public void deleteCatalogItem(String id) throws IOException {
        deletePath("/catalog/api/items/" + id, 204,200);
    }

    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
