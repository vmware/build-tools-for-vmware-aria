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
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.common.rest.RestClient;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogEntitlement;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.ContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.PropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.ResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Scenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Subscription;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Policy;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

public class RestClientVcfAuto extends RestClient {
    
    private final ConfigurationVcfAuto configuration;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private String authToken;

    public RestClientVcfAuto(ConfigurationVcfAuto configuration) {
        this(configuration, null);
    }

    public RestClientVcfAuto(ConfigurationVcfAuto configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClientBuilder.create().build();
        this.restTemplate = restTemplate;
    }

    @Override
    protected com.vmware.pscoe.iac.artifact.common.configuration.Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public String getVersion() {
        // Vcfa exposes API version via configuration; return that as client version
        return this.configuration.getApiVersion();
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

    private <T> List<T> getList(String relativePath, Class<T> cls) throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + relativePath);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String responseBody = executeRequestWithExpected(request, 200);
        Map<String,Object> result = objectMapper.readValue(responseBody, Map.class);
        Object content = result.get("content");
        if (content == null) return java.util.Collections.emptyList();
        return objectMapper.convertValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, cls));
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

    public List<VcfaBlueprint> getBlueprints() throws IOException {
        return getList("/blueprint/api/blueprints", VcfaBlueprint.class);
    }

    public VcfaBlueprint getBlueprintById(String id) throws IOException {
        HttpGet request = new HttpGet(configuration.getBaseUrl() + "/blueprint/api/blueprints/" + id);
        setAuthHeader(request);
        request.setHeader("Content-Type", "application/json");
        String responseBody = executeRequestWithExpected(request, 200);
        return objectMapper.readValue(responseBody, VcfaBlueprint.class);
    }

    public VcfaBlueprint createBlueprint(Map<String, Object> blueprint) throws IOException {
        Map<String,Object> result = postMap("/blueprint/api/blueprints", blueprint, 201);
        if (result == null) return null;
        return objectMapper.convertValue(result, VcfaBlueprint.class);
    }

    public VcfaBlueprint updateBlueprint(String id, Map<String, Object> blueprint) throws IOException {
        Map<String,Object> result = putMap("/blueprint/api/blueprints/" + id, blueprint, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, VcfaBlueprint.class);
    }

    public VcfaBlueprint createBlueprint(VcfaBlueprint blueprint) throws IOException {
        return createBlueprint(objectMapper.convertValue(blueprint, Map.class));
    }

    public VcfaBlueprint updateBlueprint(String id, VcfaBlueprint blueprint) throws IOException {
        return updateBlueprint(id, objectMapper.convertValue(blueprint, Map.class));
    }

    public void deleteBlueprint(String id) throws IOException {
        deletePath("/blueprint/api/blueprints/" + id, 204);
    }

    public List<CatalogItem> getCatalogItems() throws IOException {
        return getList("/catalog/api/items", CatalogItem.class);
    }

    public List<ContentSource> getContentSources() throws IOException {
        return getList("/content/api/sources", ContentSource.class);
    }

    public List<CustomResourceType> getCustomResources() throws IOException {
        return getList("/resource/api/types", CustomResourceType.class);
    }

    public List<ResourceAction> getResourceActions() throws IOException {
        return getList("/catalog/api/resource-actions", ResourceAction.class);
    }

    public List<Subscription> getSubscriptions() throws IOException {
        return getList("/provisioning/uerp/provisioning/mgmt/event-broker/subscriptions", Subscription.class);
    }

    public List<Policy> getPolicies() throws IOException {
        return getList("/policy/api/policies", Policy.class);
    }

    public List<PropertyGroup> getPropertyGroups() throws IOException {
        return getList("/blueprint/api/property-groups", PropertyGroup.class);
    }

    public List<CatalogEntitlement> getCatalogEntitlements() throws IOException {
        return getList("/catalog/api/entitlements", CatalogEntitlement.class);
    }

    public List<Scenario> getScenarios() throws IOException {
        return getList("/catalog/api/notification/scenarios", Scenario.class);
    }

    public ContentSource createContentSource(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/content/api/sources", payload, 201, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, ContentSource.class);
    }

    public ContentSource createContentSource(ContentSource payload) throws IOException {
        return createContentSource(objectMapper.convertValue(payload, Map.class));
    }

    public ContentSource updateContentSource(String id, ContentSource payload) throws IOException {
        return updateContentSource(id, objectMapper.convertValue(payload, Map.class));
    }

    public ContentSource updateContentSource(String id, Map<String, Object> payload) throws IOException {
        Map<String,Object> result = putMap("/content/api/sources/" + id, payload, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, ContentSource.class);
    }

    public void deleteContentSource(String id) throws IOException {
        deletePath("/content/api/sources/" + id, 204, 200);
    }

    public PropertyGroup createPropertyGroup(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/properties/api/property-groups", payload, 201, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, PropertyGroup.class);
    }

    public PropertyGroup createPropertyGroup(PropertyGroup payload) throws IOException {
        return createPropertyGroup(objectMapper.convertValue(payload, Map.class));
    }

    public PropertyGroup updatePropertyGroup(String id, PropertyGroup payload) throws IOException {
        return updatePropertyGroup(id, objectMapper.convertValue(payload, Map.class));
    }

    public PropertyGroup updatePropertyGroup(String id, Map<String, Object> payload) throws IOException {
        Map<String,Object> result = putMap("/properties/api/property-groups/" + id, payload, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, PropertyGroup.class);
    }

    public void deletePropertyGroup(String id) throws IOException {
        deletePath("/properties/api/property-groups/" + id, 204, 200);
    }

    public Policy createPolicy(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/policy/api/policies", payload, 200,201,202);
        if (result == null) return null;
        return objectMapper.convertValue(result, Policy.class);
    }

    public Policy createPolicy(Policy payload) throws IOException {
        return createPolicy(objectMapper.convertValue(payload, Map.class));
    }

    public void deletePolicy(String id) throws IOException {
        deletePath("/policy/api/policies/" + id, 204,200);
    }

    public ResourceAction createResourceAction(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/form-service/api/custom/resource-actions", payload, 200,201);
        if (result == null) return null;
        return objectMapper.convertValue(result, ResourceAction.class);
    }

    public ResourceAction createResourceAction(ResourceAction payload) throws IOException {
        return createResourceAction(objectMapper.convertValue(payload, Map.class));
    }

    public void deleteResourceAction(String id) throws IOException {
        deletePath("/form-service/api/custom/resource-actions/" + id, 200,204);
    }

    public CustomResourceType createCustomResourceType(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/form-service/api/custom/resource-types", payload, 200,201);
        if (result == null) return null;
        return objectMapper.convertValue(result, CustomResourceType.class);
    }

    public CustomResourceType createCustomResourceType(CustomResourceType payload) throws IOException {
        return createCustomResourceType(objectMapper.convertValue(payload, Map.class));
    }

    public void deleteCustomResourceType(String id) throws IOException {
        deletePath("/form-service/api/custom/resource-types/" + id, 200,204);
    }

    public CatalogItem createCatalogItem(Map<String, Object> payload) throws IOException {
        Map<String,Object> result = postMap("/catalog/api/items", payload, 201,200);
        if (result == null) return null;
        return objectMapper.convertValue(result, CatalogItem.class);
    }

    public CatalogItem createCatalogItem(CatalogItem payload) throws IOException {
        return createCatalogItem(objectMapper.convertValue(payload, Map.class));
    }

    public CatalogItem updateCatalogItem(String id, CatalogItem payload) throws IOException {
        return updateCatalogItem(id, objectMapper.convertValue(payload, Map.class));
    }

    public CatalogItem updateCatalogItem(String id, Map<String, Object> payload) throws IOException {
        Map<String,Object> result = putMap("/catalog/api/items/" + id, payload, 200);
        if (result == null) return null;
        return objectMapper.convertValue(result, CatalogItem.class);
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
