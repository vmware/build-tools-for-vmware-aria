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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

// replaced low-level HTTP client usage with RestTemplate

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.common.rest.RestClient;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.*;

public class RestClientVcfAutoPrimitive extends RestClient {

	protected final ConfigurationVcfAuto configuration;
	protected static final Logger LOGGER = LoggerFactory.getLogger(RestClientVcfAutoPrimitive.class);
	protected final ObjectMapper objectMapper;
	protected final RestTemplate restTemplate;
	protected String apiVersion;
	private String projectId;

	public RestClientVcfAutoPrimitive(ConfigurationVcfAuto configuration) {
		this(configuration, null);
	}

	public RestClientVcfAutoPrimitive(ConfigurationVcfAuto configuration, RestTemplate restTemplate) {
		this.configuration = configuration;
		this.objectMapper = new ObjectMapper();
		this.restTemplate = restTemplate;
	}

	@Override
	protected com.vmware.pscoe.iac.artifact.common.configuration.Configuration getConfiguration() {
		return this.configuration;
	}

	protected <T> List<T> getList(String relativePath, Class<T> cls) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(relativePath));
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		String responseBody = response.getBody();
		Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
		Object content = result.get("content");
		if (content == null)
			return java.util.Collections.emptyList();
		return objectMapper.convertValue(content,
				TypeFactory.defaultInstance().constructCollectionType(List.class, cls));
	}

	/**
	 * Common retriever for paged content. It performs GET requests against the
	 * given path until the final page has been reached.
	 *
	 * @param path      URL path
	 * @param paramsMap any number of query parameters
	 * @return combined results as list of maps
	 */
	protected List<Map<String, Object>> getPagedContent(final String path, final Map<String, String> paramsMap)
			throws IOException {
		final int PAGE_SIZE = 500;

		URIBuilder uriBuilder = getURIBuilder().setPath(path).setParameter("page", "0").setParameter("size",
				String.valueOf(PAGE_SIZE));

		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}

		java.net.URI uri = getURI(uriBuilder);
		org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(uri,
				org.springframework.http.HttpMethod.GET, getDefaultHttpEntity(), String.class);
		if (response == null)
			return java.util.Collections.emptyList();

		Map<String, Object> root = objectMapper.readValue(response.getBody(), Map.class);
		if (root == null)
			return java.util.Collections.emptyList();

		List<Map<String, Object>> allResults = new ArrayList<>();
		Object tp = root.get("totalPages");
		int totalPages = tp instanceof Number ? ((Number) tp).intValue() : 1;

		for (int page = 0; page < totalPages; page++) {
			if (page > 0) {
				uriBuilder.setParameter("page", String.valueOf(page));
				response = restTemplate.exchange(getURI(uriBuilder), org.springframework.http.HttpMethod.GET,
						getDefaultHttpEntity(), String.class);
				root = objectMapper.readValue(response.getBody(), Map.class);
			}
			Object content = root.get("content");
			if (content instanceof List) {
				List<?> list = (List<?>) content;
				for (Object o : list) {
					if (o instanceof Map) {
						allResults.add((Map<String, Object>) o);
					}
				}
			}
			if (totalPages == 1)
				break;
		}

		return allResults;
	}

	/**
	 * Retriever for paged content based on totalElements and numberOfElements
	 * ($top/$skip style).
	 */
	protected List<Map<String, Object>> getTotalElements(final String path, final Map<String, String> paramsMap)
			throws IOException {
		final int PAGE_SIZE = 500;
		URIBuilder uriBuilder = getURIBuilder().setPath(path)
				.setParameter("$top", String.valueOf(PAGE_SIZE)).setParameter("$skip", String.valueOf(0));

		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}

		List<Map<String, Object>> allResults = new ArrayList<>();
		int page = 0;
		int totalElements = 0;
		int numberOfElements = 0;
		do {
			uriBuilder.setParameter("$skip", String.valueOf(PAGE_SIZE * (page)));
			ResponseEntity<String> response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET,
					getDefaultHttpEntity(), String.class);
			Map<String, Object> root = objectMapper.readValue(response.getBody(), Map.class);
			Object te = root.get("totalElements");
			Object ne = root.get("numberOfElements");
			totalElements = te instanceof Number ? ((Number) te).intValue() : 0;
			numberOfElements = ne instanceof Number ? ((Number) ne).intValue() : 0;

			Object content = root.get("content");
			if (content instanceof List) {
				for (Object o : (List<?>) content) {
					if (o instanceof Map)
						allResults.add((Map<String, Object>) o);
				}
			}

			page += 1;
		} while ((page * PAGE_SIZE) < totalElements);

		return allResults;
	}

	protected java.util.List<VcfaProject> getProjectsPrimitive() throws IOException {
		List<Map<String, Object>> results = this.getTotalElements("/project-service/api/projects", new HashMap<>());
		return results.stream().map(m -> objectMapper.convertValue(m, VcfaProject.class)).collect(Collectors.toList());
	}

	protected java.util.List<VcfaProject> getProjectsPrimitive(final String project) throws IOException {
		List<VcfaProject> allProjects = this.getProjectsPrimitive();
		if (allProjects == null || allProjects.isEmpty())
			return java.util.Collections.emptyList();
		return allProjects.stream()
				.filter(p -> project.equalsIgnoreCase(p.getId()) || project.equalsIgnoreCase(p.getName()))
				.collect(Collectors.toList());
	}

	protected String getProjectIdPrimitive(final String project) throws IOException {
		List<VcfaProject> projects = getProjectsPrimitive(project);
		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getId() : null;
	}

	protected String getProjectNamePrimitive(final String project) throws IOException {
		List<VcfaProject> projects = getProjectsPrimitive(project);
		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getName() : null;
	}

	public String getProjectId() throws IOException {
		if (StringUtils.isNotEmpty(projectId)) {
			return this.projectId;
		}

		String projectName = configuration.getProjectName();
		if (StringUtils.isEmpty(projectName)) {
			throw new RuntimeException(
					"Either project name or project id must be supplied to the vRA NG configuration.");
		}
		this.projectId = this.getProjectIdPrimitive(projectName);
		if (this.projectId == null) {
			throw new RuntimeException(String.format("Project id for project '%s' could not be found on target system",
					configuration.getProjectName()));
		}
		LOGGER.info("Using project name defined in the configuration '{}', project id: '{}'", projectName,
				this.projectId);

		return projectId;
	}

	protected Map<String, Object> postMap(String relativePath, Map<String, Object> payload, int... expected)
			throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(relativePath));
		String requestBody = objectMapper.writeValueAsString(payload);
		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody,
				getDefaultHttpEntity().getHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		String responseBody = response.getBody();
		if (responseBody == null || responseBody.isEmpty())
			return null;
		return objectMapper.readValue(responseBody, Map.class);
	}

	protected Map<String, Object> putMap(String relativePath, Map<String, Object> payload, int... expected)
			throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(relativePath));
		String requestBody = objectMapper.writeValueAsString(payload);
		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody,
				getDefaultHttpEntity().getHeaders());
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
		String responseBody = response.getBody();
		if (responseBody == null || responseBody.isEmpty())
			return null;
		return objectMapper.readValue(responseBody, Map.class);
	}

	protected void deletePath(String relativePath, int... expected) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(relativePath));
		restTemplate.exchange(uri, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}

	// ============== primitives for business methods ==============
	protected List<VcfaBlueprint> getBlueprintsPrimitive() throws IOException {
		List<Map<String, Object>> results = this.getPagedContent("/blueprint/api/blueprints", new HashMap<>());
		return results.stream()
				.map(m -> objectMapper.convertValue(m, VcfaBlueprint.class))
				.collect(Collectors.toList());
	}

	protected VcfaBlueprint getBlueprintByIdPrimitive(String id) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath("/blueprint/api/blueprints/" + id));
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		return objectMapper.readValue(response.getBody(), VcfaBlueprint.class);
	}

	protected VcfaBlueprint createBlueprintPrimitive(Map<String, Object> blueprint) throws IOException {
		Map<String, Object> result = postMap("/blueprint/api/blueprints", blueprint, 201);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaBlueprint.class);
	}

	protected VcfaBlueprint updateBlueprintPrimitive(String id, Map<String, Object> blueprint) throws IOException {
		Map<String, Object> result = putMap("/blueprint/api/blueprints/" + id, blueprint, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaBlueprint.class);
	}

	protected void deleteBlueprintPrimitive(String id) throws IOException {
		deletePath("/blueprint/api/blueprints/" + id, 204);
	}

	protected List<CatalogItem> getCatalogItemsPrimitive() throws IOException {
		return getList("/catalog/api/items", CatalogItem.class);
	}

	protected List<ContentSource> getContentSourcesPrimitive() throws IOException {
		return getList("/content/api/sources", ContentSource.class);
	}

	protected List<CustomResourceType> getCustomResourcesPrimitive() throws IOException {
		return getList("/resource/api/types", CustomResourceType.class);
	}

	protected List<ResourceAction> getResourceActionsPrimitive() throws IOException {
		return getList("/catalog/api/resource-actions", ResourceAction.class);
	}

	protected List<Subscription> getSubscriptionsPrimitive() throws IOException {
		return getList("/provisioning/uerp/provisioning/mgmt/event-broker/subscriptions", Subscription.class);
	}

	protected List<Policy> getPoliciesPrimitive() throws IOException {
		return getList("/policy/api/policies", Policy.class);
	}

	protected List<PropertyGroup> getPropertyGroupsPrimitive() throws IOException {
		return getList("/blueprint/api/property-groups", PropertyGroup.class);
	}

	protected List<CatalogEntitlement> getCatalogEntitlementsPrimitive() throws IOException {
		return getList("/catalog/api/entitlements", CatalogEntitlement.class);
	}

	protected List<Scenario> getScenariosPrimitive() throws IOException {
		return getList("/catalog/api/notification/scenarios", Scenario.class);
	}

	protected ContentSource createContentSourcePrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/content/api/sources", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, ContentSource.class);
	}

	protected ContentSource updateContentSourcePrimitive(String id, Map<String, Object> payload) throws IOException {
		Map<String, Object> result = putMap("/content/api/sources/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, ContentSource.class);
	}

	protected void deleteContentSourcePrimitive(String id) throws IOException {
		deletePath("/content/api/sources/" + id, 204, 200);
	}

	protected PropertyGroup createPropertyGroupPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/properties/api/property-groups", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, PropertyGroup.class);
	}

	protected PropertyGroup updatePropertyGroupPrimitive(String id, Map<String, Object> payload) throws IOException {
		Map<String, Object> result = putMap("/properties/api/property-groups/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, PropertyGroup.class);
	}

	protected void deletePropertyGroupPrimitive(String id) throws IOException {
		deletePath("/properties/api/property-groups/" + id, 204, 200);
	}

	protected Policy createPolicyPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/policy/api/policies", payload, 200, 201, 202);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, Policy.class);
	}

	protected void deletePolicyPrimitive(String id) throws IOException {
		deletePath("/policy/api/policies/" + id, 204, 200);
	}

	protected ResourceAction createResourceActionPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/form-service/api/custom/resource-actions", payload, 200, 201);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, ResourceAction.class);
	}

	protected void deleteResourceActionPrimitive(String id) throws IOException {
		deletePath("/form-service/api/custom/resource-actions/" + id, 200, 204);
	}

	protected CustomResourceType createCustomResourceTypePrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/form-service/api/custom/resource-types", payload, 200, 201);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, CustomResourceType.class);
	}

	protected void deleteCustomResourceTypePrimitive(String id) throws IOException {
		deletePath("/form-service/api/custom/resource-types/" + id, 200, 204);
	}

	protected CatalogItem createCatalogItemPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/catalog/api/items", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, CatalogItem.class);
	}

	protected CatalogItem updateCatalogItemPrimitive(String id, Map<String, Object> payload) throws IOException {
		Map<String, Object> result = putMap("/catalog/api/items/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, CatalogItem.class);
	}

	protected void deleteCatalogItemPrimitive(String id) throws IOException {
		deletePath("/catalog/api/items/" + id, 204, 200);
	}

	/**
	 * Retrieve Version.
	 *
	 * @return Version
	 */
	@Override
	public String getVersion() {
		if (this.apiVersion != null && !this.apiVersion.isEmpty()) {
			return this.apiVersion;
		}

		URI url = getURI(getURIBuilder().setPath("/iaas/api/about"));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		this.apiVersion = JsonPath.parse(response.getBody()).read("$.supportedApis[0].apiVersion");
		LOGGER.info("Detected API Version {}", this.apiVersion);

		return this.apiVersion;
	}
}
