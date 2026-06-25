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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.common.rest.RestClient;
import com.vmware.pscoe.iac.artifact.common.store.Version;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogEntitlement;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPolicy;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaProject;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaScenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;

public class RestClientVcfAutoPrimitive extends RestClient {

	private static final String SERVICE_VERSION = "/vco/api/about";

	protected final ConfigurationVcfAuto configuration;
	protected static final Logger LOGGER = LoggerFactory.getLogger(RestClientVcfAutoPrimitive.class);
	protected final ObjectMapper objectMapper;
	protected final RestTemplate restTemplate;
	protected String apiVersion;
	protected Version productVersion;
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

	// --- REFACTORED TO ACCEPT RELATIVE PATH STRING ---
	protected <T> T get(String relativePath, Class<T> cls) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(relativePath));
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		String responseBody = response.getBody();
		return objectMapper.readValue(responseBody, cls);
	}

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

	protected String getOrgIdFromProjectPrimitive(final String project) throws IOException {
		List<VcfaProject> projects = getProjectsPrimitive(project);
		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getOrgId() : null;
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

	// =========================================================================
	// VRO WORKFLOW CATALOG PRIMITIVES
	// =========================================================================

	/**
	 * Retrieves all catalog items, filters them by the specified type
	 * identifier, and logs their request template schemas for testing.
	 * Maps to: GET /catalog/api/items
	 */
	protected List<JsonObject> getCatalogItemsByTypePrimitive(String typeId) throws IOException {
		List<Map<String, Object>> results = this.getPagedContent("/catalog/api/items", new java.util.HashMap<>());
		com.google.gson.Gson gson = new com.google.gson.Gson();

		// 1. Process and filter the items first
		List<JsonObject> filteredItems = results.stream()
				.filter(itemMap -> {
					if (itemMap.containsKey("type") && itemMap.get("type") instanceof Map) {
						Map<?, ?> typeBlock = (Map<?, ?>) itemMap.get("type");
						return typeId.equals(typeBlock.get("id"));
					}
					return false;
				})
				.map(itemMap -> gson.toJsonTree(itemMap).getAsJsonObject())
				.collect(Collectors.toList());

		List<JsonObject> formattedItems = new java.util.ArrayList<>();

		// 2. Fetch schema payloads and structure clean minimal payloads
		LOGGER.info("Executing clean payload formatting for {} catalog items...", filteredItems.size());
		for (JsonObject item : filteredItems) {
			if (item.has("id") && !item.get("id").isJsonNull()) {
				String catalogItemId = item.get("id").getAsString();
				String itemName = item.has("name") ? item.get("name").getAsString() : "Unknown";
				String requestPath = "/catalog/api/items/" + catalogItemId;

				try {
					LOGGER.info("Fetching schema for payload minimization on item '{}' (ID: {})", itemName,
							catalogItemId);

					// Invoking the base GET primitive
					Object requestTemplateObj = this.get(requestPath, Object.class);

					if (requestTemplateObj != null) {
						JsonObject requestTemplate = gson.toJsonTree(requestTemplateObj).getAsJsonObject();

						// Extract target fields from the raw schema response
						String externalId = requestTemplate.has("externalId")
								&& !requestTemplate.get("externalId").isJsonNull()
										? requestTemplate.get("externalId").getAsString()
										: "";

						// Assemble the strictly requested minimal JSON footprint
						JsonObject cleanPayload = new JsonObject();
						cleanPayload.addProperty("typeId", typeId);

						// Structure the internal nested spec block
						JsonObject specBlock = new JsonObject();
						specBlock.addProperty("workflowId", externalId);
						cleanPayload.add("spec", specBlock);

						// Attach the rest of your target string values
						cleanPayload.addProperty("name", itemName);

						// Attach the rest of your target string values
						cleanPayload.addProperty("id", catalogItemId);

						String description = item.has("description") && !item.get("description").isJsonNull()
								? item.get("description").getAsString()
								: "";
						cleanPayload.addProperty("description", description);

						String projectId = item.has("sourceProjectId") && !item.get("sourceProjectId").isJsonNull()
								? item.get("sourceProjectId").getAsString()
								: "";
						cleanPayload.addProperty("projectId", projectId);

						boolean isGlobal = item.has("global") && !item.get("global").isJsonNull()
								&& item.get("global").getAsBoolean();
						cleanPayload.addProperty("global", isGlobal);

						formattedItems.add(cleanPayload);
						LOGGER.info("[SUCCESS] Generated clean footprint for item '{}'", itemName);
					} else {
						LOGGER.warn("[EMPTY] Server returned empty response for item: {}. Item omitted.",
								catalogItemId);
					}
				} catch (Exception e) {
					LOGGER.error("[FAILURE] Could not retrieve request template layout for item '{}': {}",
							itemName, e.getMessage());
				}
			}
		}

		// 3. Return the transformed elements completely stripped of metadata
		return formattedItems;
	}

	/**
	 * Publishes a completely new vRO workflow catalog item mapping payload
	 * reference.
	 * Maps to: POST /catalog/api/items:publish
	 */
	protected JsonObject publishCatalogItemPrimitive(JsonObject payload) throws IOException {
		// Safe cross-library transformation: Convert Gson JsonObject to Jackson Map for
		// engine processing
		Map<String, Object> bodyPayload = objectMapper.readValue(payload.toString(), Map.class);

		Map<String, Object> result = postMap("/catalog/api/items:publish", bodyPayload, 200, 201);
		if (result == null) {
			return null;
		}

		return new com.google.gson.Gson().toJsonTree(result).getAsJsonObject();
	}

	/**
	 * Re-publishes/mutates an existing deployed catalog configuration mapping
	 * reference.
	 * Maps to: POST /catalog/api/items/{id}:republish
	 */
	protected JsonObject republishCatalogItemPrimitive(String id, JsonObject payload) throws IOException {
		Map<String, Object> bodyPayload = objectMapper.readValue(payload.toString(), Map.class);

		Map<String, Object> result = postMap("/catalog/api/items/" + id + ":republish", bodyPayload, 200, 201);
		if (result == null) {
			return null;
		}

		return new com.google.gson.Gson().toJsonTree(result).getAsJsonObject();
	}

	/**
	 * Removes/unpublishes a configuration visibility index reference tracking
	 * target.
	 * Maps to: POST /catalog/api/items/{id}:unpublish
	 */
	protected void unpublishCatalogItemPrimitive(String id) throws IOException {
		// Follows the same standard VMware action-colon pattern as :publish and
		// :republish
		postMap("/catalog/api/items/" + id + ":unpublish", null, 200, 204);
	}

	// =========================================================================
	// BLUEPRINT PRIMITIVES
	// =========================================================================
	protected List<VcfaBlueprint> getBlueprintsPrimitive() throws IOException {
		List<Map<String, Object>> results = this.getPagedContent("/blueprint/api/blueprints", new HashMap<>());
		return results.stream()
				.map(m -> objectMapper.convertValue(m, VcfaBlueprint.class))
				.collect(Collectors.toList());
	}

	protected VcfaBlueprint getBlueprintByIdPrimitive(String id) throws IOException {
		return get("/blueprint/api/blueprints/" + id, VcfaBlueprint.class);
	}

	protected VcfaBlueprint createBlueprintPrimitive(Map<String, Object> blueprint) throws IOException {
		Map<String, Object> result = postMap("/blueprint/api/blueprints", blueprint, 201);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaBlueprint.class);
	}

	protected Map<String, Object> versionBlueprintPrimitive(String blueprintId, Map<String, Object> incomingPayload)
			throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}

		// Create a fresh payload map for the versioning endpoint
		Map<String, Object> versioningPayload = new java.util.HashMap<>();

		// Merge blueprint metadata (name, description, content, projectId) into the
		// versioning payload
		if (incomingPayload != null) {
			versioningPayload.putAll(incomingPayload);
		}

		// Inject required lifecycle flags for immediate catalog propagation
		versioningPayload.put("changeLog", "Imported via IaC Automation CI/CD");
		versioningPayload.put("description", "Automated release version");
		versioningPayload.put("release", true);
		versioningPayload.put("sourceControlPush", false);

		// Generate current epoch time as a string version identifier (e.g.,
		// "1781086031000")
		String epochVersion = String.valueOf(System.currentTimeMillis());
		versioningPayload.put("version", epochVersion);

		LOGGER.info("Creating and releasing blueprint version '{}' for blueprint ID: {}", epochVersion, blueprintId);

		return postMap("/blueprint/api/blueprints/" + blueprintId + "/versions", versioningPayload, 200, 201);
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

	protected String getBlueprintVersionsPrimitive(String blueprintId) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(
				getURIBuilder().setPath(String.format("/blueprint/api/blueprints/%s/versions", blueprintId)));
		org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
				uri,
				org.springframework.http.HttpMethod.GET,
				getDefaultHttpEntity(),
				String.class);
		return response.getBody();
	}

	protected void unreleaseBlueprintVersionPrimitive(String blueprintId, String versionId) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(
				String.format("/blueprint/api/blueprints/%s/versions/%s/actions/unrelease", blueprintId, versionId)));
		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("{}",
				getDefaultHttpEntity().getHeaders());
		restTemplate.exchange(uri, org.springframework.http.HttpMethod.POST, entity, String.class);
	}

	/**
	 * Primitive method to register and apply a pre-configured custom request form
	 * layout template straight out of workspace artifact files, packing them into
	 * the required stringified envelope structure.
	 */
	protected void createBlueprintFormPrimitive(String blueprintId, Map<String, Object> formPayload, String yamlContent,
			String blueprintName, String blueprintDescription, Boolean requestScopeOrg, String styles)
			throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}

		// --- Step 1: POST
		// /blueprint/api/blueprints/{blueprintId}/form?apiVersion=2020-08-25 ---
		String creationResponseRaw = "";
		if (formPayload.containsKey("name")
				&& (formPayload.get("name") == null || formPayload.get("name").toString().isEmpty())) {
			formPayload.put("name", blueprintName);
		}

		String serializedPayloadString = "";
		try {
			serializedPayloadString = this.objectMapper.writeValueAsString(formPayload);
		} catch (Exception se) {
			throw new IOException("Failed to stringify inner form layout content block: " + se.getMessage(), se);
		}

		// Build the precise root-level JSON envelope expected by the Aria API endpoint
		Map<String, Object> apiEnvelopePayload = new java.util.HashMap<>();
		apiEnvelopePayload.put("name", blueprintName);
		apiEnvelopePayload.put("form", serializedPayloadString);

		if (styles != null) {
			apiEnvelopePayload.put("styles", styles);
		}

		apiEnvelopePayload.put("status", "ON");
		apiEnvelopePayload.put("type", "requestForm");
		apiEnvelopePayload.put("sourceId", blueprintId);
		apiEnvelopePayload.put("sourceType", "com.vmw.blueprint");

		String fullySerializedEnvelope = "";
		try {
			fullySerializedEnvelope = this.objectMapper.writeValueAsString(apiEnvelopePayload);
		} catch (Exception se) {
			LOGGER.error("Full envelope serialization failed!", se);
		}

		try {
			java.net.URI step1Uri = getURI(getURIBuilder()
					.setPath("/blueprint/api/blueprints/" + blueprintId + "/form")
					.setParameter("apiVersion", "2020-08-25"));

			org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			org.springframework.http.HttpEntity<String> formEntity = new org.springframework.http.HttpEntity<>(
					fullySerializedEnvelope, headers);

			creationResponseRaw = this.restTemplate.postForObject(step1Uri, formEntity,
					String.class);
			LOGGER.info("Step 1: Custom Request Form envelope successfully pushed!");
		} catch (Exception e) {
			throw new IOException("Failed to apply custom form layout template configuration: " + e.getMessage(), e);
		}

		// Parse response to handle subsequent lifecycle rules if required by framework
		com.google.gson.JsonObject creationResponseObj = new com.google.gson.Gson()
				.fromJson(creationResponseRaw, com.google.gson.JsonObject.class);
		String formId = creationResponseObj.get("id").getAsString();

		// --- Step 2: Extract and validate layout using TEXT_PLAIN ---
		String step2TextPayload = "";
		if (creationResponseObj.has("form")) {
			com.google.gson.JsonElement formElement = creationResponseObj.get("form");
			// If the server gave it back to us as a primitive string, extract it directly
			// to prevent double escaping
			if (formElement.isJsonPrimitive()) {
				step2TextPayload = formElement.getAsString();
			} else {
				step2TextPayload = new com.google.gson.Gson().toJson(formElement);
			}
		} else {
			// Fallback to our clean local serialized payload string if the server response
			// block differs
			step2TextPayload = serializedPayloadString;
		}

		try {
			String generateFormJsonSchemaPath = this.getProductVersion().getString().startsWith("9.0")
					? "/form-service/api/forms/designer/generate-form-json-schema"
					: "/blueprint/api/blueprints/form/generate-form-json-schema";
			java.net.URI step2Uri = getURI(getURIBuilder().setPath(generateFormJsonSchemaPath));

			org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
			org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(
					step2TextPayload, headers);

			this.restTemplate.postForObject(step2Uri, entity, String.class);
			LOGGER.info(
					"Step 2: Successfully executed schema pre-generation rules using server-generated form layout.");
		} catch (org.springframework.web.client.HttpClientErrorException.BadRequest bre) {
			throw new IOException("Failed schema generation validation pass: " +
					bre.getResponseBodyAsString(), bre);
		}

		// --- Step 3: Bind structural configuration values back via PUT ---
		Map<String, Object> finalizePayload = new java.util.HashMap<>();
		finalizePayload.put("name", blueprintName);
		finalizePayload.put("description", blueprintDescription != null ? blueprintDescription : "");
		finalizePayload.put("valid", true);
		finalizePayload.put("projectId", this.getProjectId());
		finalizePayload.put("requestScopeOrg", requestScopeOrg != null ? requestScopeOrg : true);
		finalizePayload.put("formId", formId);
		finalizePayload.put("content", yamlContent);

		// --- FIXED: Inject styles here so the draft update preserves them alongside
		// the form assignment ---
		if (styles != null && !styles.isEmpty()) {
			finalizePayload.put("styles", styles);
		} else {
			finalizePayload.put("styles", "");
		}

		try {
			java.net.URI step3Uri = getURI(getURIBuilder().setPath("/blueprint/api/blueprints/" + blueprintId));

			org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			org.springframework.http.HttpEntity<Map<String, Object>> putEntity = new org.springframework.http.HttpEntity<>(
					finalizePayload, headers);

			this.restTemplate.exchange(step3Uri, org.springframework.http.HttpMethod.PUT,
					putEntity, String.class);
			LOGGER.info("Step 3: Successfully executed request form push.");
		} catch (Exception e) {
			throw new IOException(
					"Failed to bind form metadata configurations to target blueprint entity: " +
							e.getMessage(),
					e);
		}
	}

	// =========================================================================
	// CATALOG ITEM PRIMITIVES
	// =========================================================================
	public List<VcfaCatalogItem> getCatalogItemsPrimitive() throws IOException {
		List<VcfaCatalogItem> summaryList = getList("/catalog/api/items", VcfaCatalogItem.class);
		if (summaryList == null || summaryList.isEmpty()) {
			return java.util.Collections.emptyList();
		}

		List<VcfaCatalogItem> fullyHydratedList = new java.util.ArrayList<>();
		for (VcfaCatalogItem summary : summaryList) {
			if (summary.getId() == null) {
				fullyHydratedList.add(summary);
				continue;
			}
			try {
				String rawJson = get("/catalog/api/items/" + summary.getId(), String.class);
				VcfaCatalogItem richItem = objectMapper.readValue(rawJson, VcfaCatalogItem.class);

				Map<String, Object> rawMap = objectMapper.readValue(rawJson, Map.class);
				if (rawMap != null) {
					if (rawMap.containsKey("sourceId")) {
						richItem.setSourceId(String.valueOf(rawMap.get("sourceId")));
					} else if (rawMap.containsKey("blueprintId")) {
						richItem.setSourceId(String.valueOf(rawMap.get("blueprintId")));
					} else if (rawMap.containsKey("type") && rawMap.get("type") instanceof Map) {
						Map<?, ?> typeMap = (Map<?, ?>) rawMap.get("type");
						if (typeMap.containsKey("sourceId")) {
							richItem.setSourceId(String.valueOf(typeMap.get("sourceId")));
						}
					}
				}
				fullyHydratedList.add(richItem != null ? richItem : summary);
			} catch (Exception e) {
				fullyHydratedList.add(summary);
			}
		}
		return fullyHydratedList;
	}

	protected VcfaCatalogItem createCatalogItemPrimitive(VcfaCatalogItem item) throws IOException {
		Map<String, Object> payload = objectMapper.convertValue(item, Map.class);
		Map<String, Object> result = postMap("/catalog/api/items:publish", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaCatalogItem.class);
	}

	protected void deleteCatalogItemPrimitive(String id) throws IOException {
		deletePath("/catalog/api/items/" + id, 204, 200);
	}

	// =========================================================================
	// CUSTOM FORM LOOKUP & RESOLUTION PRIMITIVES
	// =========================================================================
	public VcfaCatalogItemForm fetchFormMetaBySourceAndType(String sourceType, String sourceId) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}

		URIBuilder uriBuilder = getURIBuilder()
				.setPath("/form-service/api/forms/fetchBySourceAndType")
				.setParameter("sourceType", sourceType)
				.setParameter("sourceId", sourceId)
				.setParameter("formType", "requestForm");

		java.net.URI uri = getURI(uriBuilder);

		try {
			LOGGER.info("Querying custom form availability for source ID: {} ({})", sourceId, sourceType);
			org.springframework.http.ResponseEntity<VcfaCatalogItemForm> response = restTemplate.exchange(
					uri,
					org.springframework.http.HttpMethod.GET,
					getDefaultHttpEntity(),
					VcfaCatalogItemForm.class);
			return response.getBody();
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			if (e.getStatusCode() == org.springframework.http.HttpStatus.NOT_FOUND) {
				LOGGER.info(
						"No customized request form configuration active on the server for source type '{}' and ID '{}'. Skipping form export gracefully.",
						sourceType, sourceId);
				return null;
			}
			LOGGER.error("Genuinely failed checking form availability for source '{}': status {}", sourceId,
					e.getStatusCode());
			throw e;
		}
	}

	protected VcfaCatalogItemForm getCatalogItemFormPrimitive(String sourceType, String sourceId) throws IOException {
		VcfaCatalogItemForm meta = fetchFormMetaBySourceAndType(sourceType, sourceId);
		if (meta == null || meta.getId() == null) {
			return null;
		}
		return get("/form-service/api/forms/" + meta.getId(), VcfaCatalogItemForm.class);
	}

	/**
	 * Cleaned catalog item configuration path - completely removes the
	 * cross-contamination blueprint generation schema references.
	 */
	protected void updateCatalogItemFormPrimitive(String sourceType, String catalogItemId, VcfaCatalogItemForm form)
			throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}

		String resolvedSourceId = (form.getSourceId() != null) ? form.getSourceId() : catalogItemId;

		LOGGER.info("Checking server-side form footprint using resolved ID: {} ({})", resolvedSourceId, sourceType);
		VcfaCatalogItemForm existingFormMeta = fetchFormMetaBySourceAndType(sourceType, resolvedSourceId);

		Map<String, Object> payload = objectMapper.convertValue(form, Map.class);

		if (payload.get("form") != null) {
			Object formProperty = payload.get("form");
			if (!(formProperty instanceof String)) {
				payload.put("form", objectMapper.writeValueAsString(formProperty));
			}
		}

		// ROUTING MATRIX
		if (existingFormMeta != null && existingFormMeta.getId() != null) {
			LOGGER.info("Form footprint discovered (Form ID: {}). Skipping update configuration action for testing.",
					existingFormMeta.getId());
		} else {
			LOGGER.info("No active form footprint discovered on server. Executing fresh creation via POST...");
			postMap("/form-service/api/forms", payload, 200, 201);
		}
	}

	// =========================================================================
	// CONTENT SOURCE PRIMITIVES
	// =========================================================================
	protected List<VcfaContentSource> getContentSourcesPrimitive() throws IOException {
		return getList("/content/api/items", VcfaContentSource.class);
	}

	protected VcfaContentSource createContentSourcePrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/content/api/items", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaContentSource.class);
	}

	protected VcfaContentSource updateContentSourcePrimitive(String id, Map<String, Object> payload)
			throws IOException {
		Map<String, Object> result = putMap("/content/api/items/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaContentSource.class);
	}

	protected void deleteContentSourcePrimitive(String id) throws IOException {
		deletePath("/content/api/items/" + id, 204, 200);
	}

	// =========================================================================
	// SUBSCRIPTIONS PRIMITIVES
	// =========================================================================
	protected List<VcfaSubscription> getSubscriptionsPrimitive() throws IOException {
		return getList("/event-broker/api/subscriptions", VcfaSubscription.class);
	}

	protected VcfaSubscription createSubscriptionPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/event-broker/api/subscriptions", payload, 200, 201);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaSubscription.class);
	}

	protected VcfaSubscription updateSubscriptionPrimitive(String id, Map<String, Object> payload) throws IOException {
		Map<String, Object> result = putMap("/event-broker/api/subscriptions/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaSubscription.class);
	}

	protected void deleteSubscriptionPrimitive(String id) throws IOException {
		deletePath("/event-broker/api/subscriptions/" + id, 200, 204);
	}

	/**
	 * Resolves an ABX script action GUID identifier by querying using a name
	 * filter.
	 */
	protected String getAbxActionIdByNamePrimitive(String name) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath("/abx/api/resources/actions").addParameter("name", name));
		org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
				uri, org.springframework.http.HttpMethod.GET, getDefaultHttpEntity(), Map.class);

		if (response.getBody() != null && response.getBody().get("content") instanceof List) {
			List<?> content = (List<?>) response.getBody().get("content");
			if (!content.isEmpty() && content.get(0) instanceof Map) {
				return String.valueOf(((Map<?, ?>) content.get(0)).get("id"));
			}
		}
		return null;
	}

	/**
	 * Translates a technical ABX action GUID back into its human-readable
	 * definition name.
	 */
	protected String getAbxActionNameByIdPrimitive(String id) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		java.net.URI uri = getURI(getURIBuilder().setPath(String.format("/abx/api/resources/actions/%s", id)));
		try {
			org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
					uri, org.springframework.http.HttpMethod.GET, getDefaultHttpEntity(), Map.class);
			if (response.getBody() != null && response.getBody().containsKey("name")) {
				return String.valueOf(response.getBody().get("name"));
			}
		} catch (Exception e) {
			LOGGER.warn("Unable to resolve ABX action name for ID '{}': {}", id, e.getMessage());
		}
		return id; // Fallback to raw ID if resource cannot be resolved on target instance
	}

	// =========================================================================
	// PROPERTY GROUP PRIMITIVES
	// =========================================================================
	protected List<VcfaPropertyGroup> getPropertyGroupsPrimitive() throws IOException {
		return getList("/properties/api/property-groups", VcfaPropertyGroup.class);
	}

	protected VcfaPropertyGroup createPropertyGroupPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/properties/api/property-groups", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaPropertyGroup.class);
	}

	protected VcfaPropertyGroup updatePropertyGroupPrimitive(String id, Map<String, Object> payload)
			throws IOException {
		Map<String, Object> result = putMap("/properties/api/property-groups/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaPropertyGroup.class);
	}

	protected void deletePropertyGroupPrimitive(String id) throws IOException {
		deletePath("/properties/api/property-groups/" + id, 204, 200);
	}

	// =========================================================================
	// ENTITLEMENTS PRIMITIVES
	// =========================================================================
	protected List<CatalogEntitlement> getCatalogEntitlementsPrimitive() throws IOException {
		return getList("/catalog/api/entitlements", CatalogEntitlement.class);
	}

	// =========================================================================
	// SCENARIOS PRIMITIVES
	// =========================================================================
	protected List<VcfaScenario> getScenariosListPrimitive() throws IOException {
		return getList("/notification/api/scenario-configs", VcfaScenario.class);
	}

	public List<VcfaScenario> getScenarios() throws IOException {
		List<VcfaScenario> result = new ArrayList<>();
		List<VcfaScenario> items = getScenariosListPrimitive();
		items.forEach(s -> {
			try {
				result.add(getScenarioExpandedPrimitive(s.getId()));
			} catch (Exception e) {
				throw new RuntimeException(
						String.format("Could not get scenario expanded object!"),
						e);
			}
		});
		return result;
	}

	protected VcfaScenario getScenarioExpandedPrimitive(String id) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}

		// Step 1: Attempt standard expanded lookup
		java.net.URI uri = getURI(getURIBuilder().setPath("/notification/api/scenario-configs/" + id)
				.setParameter("expandBody", "true"));
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		VcfaScenario scenario = objectMapper.readValue(response.getBody(), VcfaScenario.class);

		// Step 2: Fallback check — if the scenario body is missing/empty, pull the
		// default out-of-the-box layout
		if (scenario != null && (scenario.getBody() == null || scenario.getBody().trim().isEmpty())) {
			LOGGER.info(
					"Scenario body for ID '{}' is empty. Forcing retrieval using default configuration layout parameters...",
					id);

			java.net.URI defaultUri = getURI(getURIBuilder().setPath("/notification/api/scenario-configs/" + id)
					.setParameter("expandBody", "true")
					.setParameter("defaultConfig", "true"));

			ResponseEntity<String> defaultResponse = restTemplate.exchange(defaultUri, HttpMethod.GET,
					getDefaultHttpEntity(),
					String.class);
			scenario = objectMapper.readValue(defaultResponse.getBody(), VcfaScenario.class);
		}

		return scenario;
	}

	protected VcfaScenario createScenarioPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/notification/api/scenario-configs", payload, 201, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaScenario.class);
	}

	protected VcfaScenario updateScenarioPrimitive(String id, Map<String, Object> payload) throws IOException {
		Map<String, Object> result = putMap("/notification/api/scenario-configs/" + id, payload, 200);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaScenario.class);
	}

	protected void deleteScenarioPrimitive(String id) throws IOException {
		deletePath("/notification/api/scenario-configs/" + id, 204, 200);
	}

	// =========================================================================
	// POLICIES PRIMITIVES
	// =========================================================================
	public List<VcfaPolicy> getPoliciesPrimitive() throws IOException {
		List<VcfaPolicy> summaryList = getList("/policy/api/policies", VcfaPolicy.class);
		if (summaryList == null || summaryList.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		List<VcfaPolicy> fullyHydratedList = new java.util.ArrayList<>();
		for (VcfaPolicy summary : summaryList) {
			if (summary.getId() == null) {
				fullyHydratedList.add(summary);
				continue;
			}
			try {
				VcfaPolicy richPolicy = get(String.format("/policy/api/policies/%s", summary.getId()),
						VcfaPolicy.class);
				if (richPolicy != null) {
					fullyHydratedList.add(richPolicy);
				} else {
					fullyHydratedList.add(summary);
				}
			} catch (Exception e) {
				fullyHydratedList.add(summary);
			}
		}
		return fullyHydratedList;
	}

	protected VcfaPolicy createOrUpdatePolicyPrimitive(Map<String, Object> payload) throws IOException {
		Map<String, Object> result = postMap("/policy/api/policies", payload, 200, 201, 202);
		if (result == null)
			return null;
		return objectMapper.convertValue(result, VcfaPolicy.class);
	}

	protected void deletePolicyPrimitive(String id) throws IOException {
		deletePath("/policy/api/policies/" + id, 204, 200);
	}

	// =========================================================================
	// RESOURCE ACTIONS PRIMITIVES
	// =========================================================================
	protected List<VcfaResourceAction> getResourceActionsPrimitive() throws IOException {
		return getList("/form-service/api/custom/resource-actions", VcfaResourceAction.class);
	}

	/**
	 * Executes an HTTP POST to create a resource action and yields the raw body
	 * string response.
	 */
	protected String createResourceActionPrimitiveString(Map<String, Object> payload) throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		sanitizeEndpointLinks(payload);
		java.net.URI uri = getURI(getURIBuilder().setPath("/form-service/api/custom/resource-actions"));
		org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(
				payload, getDefaultHttpEntity().getHeaders());
		org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
				uri,
				org.springframework.http.HttpMethod.POST,
				entity,
				String.class);
		return response.getBody();
	}

	protected void deleteResourceActionPrimitive(String id) throws IOException {
		deletePath("/form-service/api/custom/resource-actions/" + id, 200, 204);
	}

	// =========================================================================
	// CUSTOM RESOURCE PRIMITIVES
	// =========================================================================
	protected List<VcfaCustomResourceType> getCustomResourcesPrimitive() throws IOException {
		return getList("/form-service/api/custom/resource-types", VcfaCustomResourceType.class);
	}

	public Map<String, Object> createCustomResourceTypePrimitive(Map<String, Object> payloadMap) throws IOException {
		return this.postMap("/form-service/api/custom/resource-types", payloadMap);
	}

	// =========================================================================
	// DEEP PAYLOAD SANITIZATION UTILITIES
	// =========================================================================

	/**
	 * Deeply traverses maps and lists to intercept and fix missing or corrupted
	 * "endpointLink" values before they are sent to the target server.
	 */
	private void sanitizeEndpointLinks(Object node) {
		if (node instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) node;

			// 1. Check for specific nested target objects that require an endpointLink
			if (map.containsKey("create") && map.get("create") instanceof Map) {
				ensureEndpointLink((Map<String, Object>) map.get("create"));
			}
			if (map.containsKey("delete") && map.get("delete") instanceof Map) {
				ensureEndpointLink((Map<String, Object>) map.get("delete"));
			}
			if (map.containsKey("runnableItem") && map.get("runnableItem") instanceof Map) {
				ensureEndpointLink((Map<String, Object>) map.get("runnableItem"));
			}

			// 2. Global Catch-All: Fix any "endpointLink" that was explicitly passed as
			// "null" string
			if (map.containsKey("endpointLink")) {
				Object link = map.get("endpointLink");
				if (link == null || StringUtils.isBlank(link.toString())
						|| "null".equalsIgnoreCase(link.toString().trim())) {
					map.put("endpointLink", "/iaas/api/integrations/embedded-vro-placeholder");
				}
			}

			// 3. Recurse down the tree
			for (Object value : map.values()) {
				sanitizeEndpointLinks(value);
			}
		} else if (node instanceof List) {
			for (Object item : (List<?>) node) {
				sanitizeEndpointLinks(item);
			}
		}
	}

	private void ensureEndpointLink(Map<String, Object> targetNode) {
		Object link = targetNode.get("endpointLink");
		if (link == null || StringUtils.isBlank(link.toString()) || "null".equalsIgnoreCase(link.toString().trim())) {
			targetNode.put("endpointLink", "/iaas/api/integrations/embedded-vro-placeholder");
		}
	}

	protected void deleteCustomResourceTypePrimitive(String id) throws IOException {
		deletePath("/form-service/api/custom/resource-types/" + id, 200, 204);
	}

	protected String getOrganizationIdPrimitive() throws IOException {
		return getOrganizationIdPrimitive(configuration.getOrgName());
	}

	protected String getOrganizationIdPrimitive(String orgName) throws IOException {
		if (StringUtils.isEmpty(orgName)) {
			return null;
		}
		List<Map<String, Object>> results = this.getPagedContent("/identity-service/api/orgs", new HashMap<>());
		return results.stream()
				.filter(m -> orgName.equalsIgnoreCase(String.valueOf(m.get("name"))))
				.map(m -> String.valueOf(m.get("id")))
				.findFirst()
				.orElse(null);
	}

	protected String getVroTargetIntegrationEndpointLinkPrimitive() throws IOException {
		if (restTemplate == null) {
			throw new IOException("RestTemplate not configured for RestClientVcfAuto");
		}
		String vroIntegrationName = configuration.getVroIntegration();
		List<Map<String, Object>> results = this.getPagedContent("/integration/api/system/integrations",
				new HashMap<>());
		for (Map<String, Object> integration : results) {
			if (vroIntegrationName.equalsIgnoreCase(String.valueOf(integration.get("name")))) {
				Object links = integration.get("_links");
				if (links instanceof Map) {
					Map<?, ?> linksMap = (Map<?, ?>) links;
					for (Object keyObj : linksMap.keySet()) {
						Object val = linksMap.get(keyObj);
						if (val instanceof Map) {
							Map<?, ?> link = (Map<?, ?>) val;
							if ("href".equals(keyObj) && link.containsKey("href")) {
								return String.valueOf(link.get("href"));
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected String getProjectNameByIdPrimitive(String id) throws IOException {
		List<VcfaProject> projects = getProjectsPrimitive();
		return projects.stream()
				.filter(p -> id.equalsIgnoreCase(p.getId()))
				.map(VcfaProject::getName)
				.findFirst()
				.orElse(null);
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

	/**
	 * Retrieve Product Version.
	 *
	 * @return Version
	 */
	public Version getProductVersion() {
		if (this.productVersion != null) {
			return this.productVersion;
		}
		URI url = getURI(getURIBuilder().setPath(SERVICE_VERSION));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		this.productVersion = new Version(JsonPath.parse(response.getBody()).read("$.version"));

		return this.productVersion;
	}
}
