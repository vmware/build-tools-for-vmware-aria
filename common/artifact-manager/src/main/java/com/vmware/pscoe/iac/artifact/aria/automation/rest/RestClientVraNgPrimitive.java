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
/**
 * Package.
 */
package com.vmware.pscoe.iac.artifact.aria.automation.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.rest.RestClient;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPolicyDTO;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogEntitlement;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogEntitlementDto;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogEntitlementType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItemType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDay2ActionsPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgDeploymentLimitPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgLeasePolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgOrganizations;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgProject;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgRegion;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceQuotaPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgScenario;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgSecret;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgSubscription;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgWorkflowContentSource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.aria.automation.models.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;

public class RestClientVraNgPrimitive extends RestClient {
	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientVraNgPrimitive.class);
	/**
	 * SERVICE_IAAS_BASE.
	 */
	private static final String SERVICE_IAAS_BASE = "/iaas/api";
	/**
	 * API_VERSION.
	 */
	private static final String API_VERSION = "/iaas/api/about";
	/**
	 * SERVICE_VERSION.
	 */
	private static final String SERVICE_VERSION = "/vco/api/about";
	/**
	 * SERVICE_BLUEPRINT.
	 */
	private static final String SERVICE_BLUEPRINT = "/blueprint/api/blueprints";
	/**
	 * SERVICE_BLUEPRINT_VERSIONS.
	 */
	private static final String SERVICE_BLUEPRINT_VERSIONS = "/versions";
	/**
	 * SERVICE_BLUEPRINT_UNRELEASE_VERSIONS_ACTION.
	 */
	private static final String SERVICE_BLUEPRINT_UNRELEASE_VERSIONS_ACTION = "/actions/unrelease";
	/**
	 * SERVICE_SUBSCRIPTION.
	 */
	private static final String SERVICE_SUBSCRIPTION = "/event-broker/api/subscriptions";
	/**
	 * SERVICE_SCENARIO.
	 */
	private static final String SERVICE_SCENARIO = "/notification/api/scenario-configs";
	/**
	 * SERVICE_CLOUD_ACCOUNT.
	 */
	private static final String SERVICE_CLOUD_ACCOUNT = "/iaas/api/cloud-accounts";
	/**
	 * SERVICE_CLOUD_PROJECT.
	 */
	private static final String SERVICE_CLOUD_PROJECT = "/iaas/api/projects";
	/**
	 * SERVICE_REGION.
	 */
	private static final String SERVICE_REGION = "/iaas/api/regions";
	/**
	 * SERVICE_CATALOG_ADMIN_ITEMS.
	 */
	private static final String SERVICE_CATALOG_ADMIN_ITEMS = "/catalog/api/admin/items";
	/**
	 * SERVICE_CATALOG_ENTITLEMENTS.
	 */
	private static final String SERVICE_CATALOG_ENTITLEMENTS = "/catalog/api/admin/entitlements";
	/**
	 * SERVICE_CONTENT_SOURCE.
	 */
	private static final String SERVICE_CONTENT_SOURCE = "/catalog/api/admin/sources";
	/**
	 * SERVICE_CUSTOM_FORM.
	 */
	private static final String SERVICE_CUSTOM_FORM = "/form-service/api/forms";
	/**
	 * SERVICE_CUSTOM_FORM_BY_SOURCE_AND_TYPE.
	 */
	private static final String SERVICE_CUSTOM_FORM_BY_SOURCE_AND_TYPE = "/form-service/api/forms/fetchBySourceAndType";
	/**
	 * FETCH_REQUEST_FORM.
	 */
	private static final String FETCH_REQUEST_FORM = "/form-service/api/forms/designer/request";
	/**
	 * SERVICE_VRA_INTEGRATIONS.
	 */
	private static final String SERVICE_VRA_INTEGRATIONS = "provisioning/uerp/provisioning/mgmt/endpoints";
	/**
	 * SERVICE_VRA_ORGANIZATIONS.
	 */
	private static final String SERVICE_VRA_ORGANIZATIONS = "/csp/gateway/am/api/loggedin/user/orgs";
	/**
	 * SERVICE_VRA_ORGANIZATION.
	 */
	private static final String SERVICE_VRA_ORGANIZATION = "/csp/gateway/am/api/orgs/";
	/**
	 * SERVICE_CUSTOM_RESOURCES.
	 */
	private static final String SERVICE_CUSTOM_RESOURCES = "/form-service/api/custom/resource-types";
	/**
	 * SERVICE_RESOURCE_ACTIONS.
	 */
	private static final String SERVICE_RESOURCE_ACTIONS = "/form-service/api/custom/resource-actions";
	/**
	 * SERVICE_ABX_ACTIONS.
	 */
	private static final String SERVICE_ABX_ACTIONS = "/abx/api/resources/actions";
	/**
	 * SERVICE_ABX_CONSTANT.
	 */
	private static final String SERVICE_ABX_CONSTANT = "/abx/api/resources/action-secrets";
	/**
	 * SERVICE_ICON_DOWNLOAD.
	 */
	private static final String SERVICE_ICON_DOWNLOAD = "/icon/api/icons";
	/**
	 * SERVICE_ICON_UPLOAD.
	 */
	private static final String SERVICE_ICON_UPLOAD = "/icon/api/icons";
	/**
	 * SERVICE_CATALOG_ITEM_ICON_UPDATE.
	 */
	private static final String SERVICE_CATALOG_ITEM_ICON_UPDATE = "/catalog/api/admin/items";
	/**
	 * SERVICE_GET_PROPERTY_GROUPS.
	 */
	private static final String SERVICE_GET_PROPERTY_GROUPS = "/properties/api/property-groups";
	/**
	 * SERVICE_POST_PROPERTY_GROUP.
	 */
	private static final String SERVICE_POST_PROPERTY_GROUP = "/properties/api/property-groups";
	/**
	 * SERVICE_PUT_PROPERTY_GROUP.
	 */
	private static final String SERVICE_PUT_PROPERTY_GROUP = "/properties/api/property-groups";
	/**
	 * SERVICE_SECRET.
	 */
	private static final String SERVICE_SECRET = "/platform/api/secrets";
	/**
	 * SERVICE_POLICIES.
	 */
	private static final String SERVICE_POLICIES = "/policy/api/policies";
	/**
	 * VRA_VERSION_MAJOR.
	 */
	private static final int VRA_VERSION_MAJOR = 8;
	/**
	 * VRA_VERSION_MINOR.
	 */
	private static final int VRA_VERSION_MINOR = 1;
	/**
	 * VRA_CLOUD_HOSTS.
	 */
	private static final List<String> VRA_CLOUD_HOSTS = Arrays.asList("console.cloud.vmware.com",
			"api.mgmt.cloud.vmware.com");
	/**
	 * VRA_CLOUD_VERSION.
	 */
	private static final String VRA_CLOUD_VERSION = "cloud";
	/**
	 * CUSTOM_FORM_DEFAULT_FORMAT.
	 */
	private static final String CUSTOM_FORM_DEFAULT_FORMAT = "JSON";
	/**
	 * MAX_BLUEPRINTS_RETRIEVAL_COUNT.
	 */
	private static final String MAX_BLUEPRINTS_RETRIEVAL_COUNT = "1000";
	/**
	 * configuration.
	 */
	private final ConfigurationVraNg configuration;
	/**
	 * restTemplate.
	 */
	private final RestTemplate restTemplate;
	/**
	 * apiVersion.
	 */
	private String apiVersion;
	/**
	 * projectId.
	 */
	private String projectId;
	/**
	 * mapper.
	 */
	private final ObjectMapper mapper = new ObjectMapper();
	/**
	 * productVersion.
	 */
	private Version productVersion;
	/**
	 * default page size.
	 */
	private static final int PAGE_SIZE = 500;
	/**
	 * vRA 8.12 version.
	 */
	private static final String VRA_8_12 = "8.12.0.21583018";
	/**
	 * vRA 8.10 version. vRealize Automation 8.10.2.27406 (20867529).
	 */
	private static final String VRA_8_10 = "8.10.2.27406";
	/**
	 * Not found error returned by vRA.
	 */
	private static final String NOT_FOUND_ERROR = "404 Not Found";
	/**
	 * isVraAbove812.
	 */
	private boolean isVraAbove812;
	/**
	 * isVraAbove811.
	 */
	private boolean isVraAbove810;

	/**
	 * RestClientVraNgPrimitive.
	 *
	 * @param config   configuration
	 * @param restTemp restTemplate
	 */
	protected RestClientVraNgPrimitive(final ConfigurationVraNg config, final RestTemplate restTemp) {
		this.configuration = config;
		this.restTemplate = restTemp;
		this.productVersion = this.getProductVersion();
		this.isVraAbove812 = this.isVraAbove(new Version(VRA_8_12));
		this.isVraAbove810 = this.isVraAbove(new Version(VRA_8_10));
	}

	/**
	 * Retrieve Configuration.
	 *
	 * @return Configuration
	 */
	@Override
	protected Configuration getConfiguration() {
		return this.configuration;
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

		URI url = getURI(getURIBuilder().setPath(API_VERSION));
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
		if (this.isVraCloud(url)) {
			// vRA Cloud doesn't have vRO services, hence the /vco/api/about is not
			// available
			this.productVersion = new Version(VRA_CLOUD_VERSION);
		} else {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			this.productVersion = new Version(JsonPath.parse(response.getBody()).read("$.version"));
		}

		return this.productVersion;
	}

	/**
	 * Getter for isVraAbove812.
	 *
	 * @return boolean
	 */
	public boolean getIsVraAbove812() {
		return this.isVraAbove812;
	}

	/**
	 * Retrieve Project ID.
	 *
	 * @return Project ID
	 */
	public String getProjectId() {
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

	/**
	 * Downloads the given icon. The byte array returned by the response must be
	 * consumed and saved on the fs
	 *
	 * @param iconId iconId
	 * @return entities
	 */
	protected ResponseEntity<byte[]> downloadIconPrimitive(final String iconId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ICON_DOWNLOAD + "/" + iconId));

		return restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), byte[].class);
	}

	/**
	 * Uploads a File. Service Broker has a limit of 100KB that is NOT enforced
	 * here.
	 *
	 * @param iconFile iconFile
	 * @return list of responses
	 */
	protected ResponseEntity<String> uploadIconPrimitive(final File iconFile) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ICON_UPLOAD));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(iconFile));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		return restTemplate.postForEntity(url, requestEntity, String.class);
	}

	/**
	 * Patches just the icon of a catalog item. This request can theoretically be
	 * used for patching limits, it could be extended in the future
	 *
	 * @param catalogItem catalogItem
	 * @param iconId      iconId
	 * @return list of response entities
	 */
	protected ResponseEntity<String> patchCatalogItemIconPrimitive(final VraNgCatalogItem catalogItem,
			final String iconId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CATALOG_ITEM_ICON_UPDATE + "/" + catalogItem.getId()));
		Map<String, Object> map = new HashMap<>();
		map.put("iconId", iconId);

		String jsonBody = this.getJsonString(map);
		return this.postJsonPrimitive(url, HttpMethod.PATCH, jsonBody);
	}

	/**
	 * Retrieve list of projects.
	 *
	 * @param project Project
	 * @return list of projects
	 */
	protected List<VraNgProject> getProjectsPrimitive(final String project) {
		List<VraNgProject> allProjects = this.getProjectsPrimitive();
		if (allProjects == null || allProjects.isEmpty()) {
			return new ArrayList<>();
		}

		return allProjects.stream().filter(projectObject -> {
			return projectObject.getId().equalsIgnoreCase(project) || projectObject.getName().equalsIgnoreCase(project);
		}).collect(Collectors.toList());
	}

	/**
	 * Retrieve list of projects.
	 *
	 * @return list of projects
	 */
	protected List<VraNgProject> getProjectsPrimitive() {
		Gson gson = new Gson();
		List<VraNgProject> projects = this.getTotalElements(SERVICE_CLOUD_PROJECT, new HashMap<>()).stream()
				.map(jsonOb -> gson.fromJson(jsonOb, VraNgProject.class)).collect(Collectors.toList());

		return projects;
	}

	/**
	 * Retrieve Id of the project.
	 *
	 * @param project Project Object
	 * @return Id of the project
	 */
	protected String getProjectIdPrimitive(final String project) {
		List<VraNgProject> projects = getProjectsPrimitive(project);

		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getId() : null;
	}

	/**
	 * Retrieve name of the project.
	 *
	 * @param project Project Object
	 * @return Name of the project
	 */
	protected String getProjectNamePrimitive(final String project) {
		List<VraNgProject> projects = getProjectsPrimitive(project);

		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getName() : null;
	}

	/**
	 * Returns all Blueprints.
	 *
	 * @return List of VraNg Blueprint Objects
	 */
	public List<VraNgBlueprint> getAllBlueprintsPrimitive() {
		List<VraNgBlueprint> blueprints = new ArrayList<>();
		List<JsonObject> results = this.getPagedContent(SERVICE_BLUEPRINT, new HashMap<>());

		LOGGER.debug("Blueprints found on server: {}", results.size());
		results.forEach(o -> {
			JsonObject ob = o.getAsJsonObject();
			String prjId = ob.get("projectId").getAsString();
			if (getProjectId().equals(prjId)) {
				blueprints.add(this.getBlueprintPrimitive(ob.get("id").getAsString()));
			}
		});

		LOGGER.debug("Blueprints in target project: {}", blueprints.size());

		return blueprints;
	}

	/**
	 * Returns the blueprint.
	 *
	 * @param id blueprintId
	 * @return VraNgBlueprint
	 */
	public VraNgBlueprint getBlueprintPrimitive(final String id) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + id));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		return new Gson().fromJson(response.getBody(), VraNgBlueprint.class);
	}

	/**
	 * Returns Blueprint Version Content.
	 *
	 * @param blueprintId blueprintId
	 * @param version     version
	 * @return String
	 */
	public String getBlueprintVersionContentPrimitive(final String blueprintId, final String version) {
		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS + "/" + version)
				.addParameter("orderBy", "updatedAt DESC"));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());

		String content = "";
		if (root.isJsonObject()) {
			JsonObject jsonObject = root.getAsJsonObject();
			if (isJsonElementPresent(jsonObject.get("content"))) {
				content = jsonObject.get("content").getAsString();
			}
		}

		return content;
	}

	/**
	 * Returns the raw string content of a blueprint version details API call.
	 * <p>
	 * !!!This will fail if there are more than 1000 blueprints.!!!
	 *
	 * @param blueprintId blueprintId
	 * @return String
	 */
	public String getBlueprintVersionsContent(final String blueprintId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
				.addParameter("$top", MAX_BLUEPRINTS_RETRIEVAL_COUNT).addParameter("orderBy", "createdAt DESC"));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());

		String content = "";
		if (root.isJsonObject()) {
			JsonObject jsonObject = root.getAsJsonObject();
			if (isJsonElementPresent(jsonObject.get("content"))) {
				content = jsonObject.get("content").toString();
			}
		}

		return content;
	}

	/**
	 * Checks if blueprint version present.
	 *
	 * @param blueprintId Blueprint ID
	 * @param version     Blueprint Version
	 * @return true if blueprint version present
	 */
	public Boolean isBlueprintVersionPresentPrimitive(final String blueprintId, final String version) {
		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS + "/" + version));

		try {
			restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Release Blueprint Version.
	 *
	 * @param blueprintId Blueprint ID
	 * @param version     Blueprint Version
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	public void releaseBlueprintVersionPrimitive(final String blueprintId, final String version)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS));

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("version", version);
		map.put("release", true);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
	}

	/**
	 * Unrelease Blueprint Version.
	 *
	 * @param blueprintId Blueprint ID
	 * @param versionId   Blueprint versionId
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	public void unreleaseBlueprintVersionPrimitive(final String blueprintId, final String versionId)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS
				+ "/" + versionId + SERVICE_BLUEPRINT_UNRELEASE_VERSIONS_ACTION));

		try {
			this.postJsonPrimitive(url, HttpMethod.POST, "");
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(
					String.format("Error ocurred while unreleasing version %s for blueprint %s. Message: %s",
							versionId, blueprintId, e.getMessage()));
		}
	}

	/**
	 * Consuming the vRA REST API endpoint to create a blueprint version with the
	 * provided details.
	 *
	 * @param blueprintId    blueprintId
	 * @param versionDetails versionDetails
	 * @throws URISyntaxException exception
	 */
	public void createBlueprintVersionPrimitive(final String blueprintId, final Map<String, Object> versionDetails)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS));
		this.postJsonPrimitive(url, HttpMethod.POST, this.getJsonString(versionDetails));
	}

	/**
	 * Create Blueprint from Blueprint object.
	 *
	 * @param blueprint Blueprint Object to create
	 * @return VraNgCustom Form Object.
	 * @throws URISyntaxException exception
	 */
	public String createBlueprintPrimitive(final VraNgBlueprint blueprint) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_BLUEPRINT).build();
		Map<String, Object> map = this.createBlueprintMap(blueprint);
		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
		return new Gson().fromJson(response.getBody(), VraNgBlueprint.class).getId();
	}

	/**
	 * Delete Blueprint by id.
	 *
	 * @param bpId Blueprint ID
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 * @return the response
	 */
	public ResponseEntity<String> deleteBlueprintPrimitive(final String bpId) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + bpId).build();

		return restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Update Blueprint with Blueprint Object.
	 *
	 * @param blueprint Blueprint Object
	 * @return Blueprint ID.
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	public String updateBlueprintPrimitive(final VraNgBlueprint blueprint) throws URISyntaxException {

		if (blueprint.getId().isEmpty()) {
			throw new RuntimeException("Blueprint id is missing.");
		}

		URI url = getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprint.getId()).build();
		Map<String, Object> map = this.createBlueprintMap(blueprint);
		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.putJsonPrimitive(url, jsonBody);
		return new Gson().fromJson(response.getBody(), VraNgBlueprint.class).getId();
	}

	/**
	 * Retrieve Blueprint Last updated Version.
	 *
	 * @param blueprintId Blueprint ID
	 * @return Blueprint Version.
	 */
	public String getBlueprintLastUpdatedVersionPrimitive(final String blueprintId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
				.addParameter("orderBy", "updatedAt DESC"));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		String lastVersion = null;
		if (root.isJsonObject()) {
			JsonArray responseContent = root.getAsJsonObject().getAsJsonArray("content");
			if (responseContent.size() > 0) {
				lastVersion = responseContent.get(0).getAsJsonObject().get("version").getAsString();
			}
		}

		return lastVersion;
	}

	/**
	 * Checks Blueprint Release status.
	 *
	 * @param blueprintId Blue print ID
	 * @return True if released else false.
	 */
	public boolean isBlueprintReleasedPrimitive(final String blueprintId) {

		final String statusReleased = "RELEASED";

		URI url = getURI(getURIBuilder().setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
				.addParameter("status", statusReleased));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		if (root.isJsonObject()) {
			JsonArray responseContent = root.getAsJsonObject().getAsJsonArray("content");
			return responseContent.size() > 0;
		}

		return false;
	}

	/**
	 * Create or Update Content Source.
	 *
	 * @param contentSource Content Source
	 * @return Content Source ID.
	 */
	public String createOrUpdateContentSourcePrimitive(final VraNgContentSourceBase contentSource) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CONTENT_SOURCE));
		Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).serializeNulls().create();
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, gson.toJson(contentSource));
		return new Gson().fromJson(response.getBody(), contentSource.getType().getTypeClass()).getId();
	}

	/**
	 * Retrieve Vra Workflow Custom Form.
	 *
	 * @param formName form name
	 * @return VraNgCustom Form Object.
	 */
	protected VraNgCustomForm getVraWorkflowCustomFormPrimitive(final String formName) {
		VraNgCatalogItem catalogItem = getCatalogItemByBlueprintNamePrimitive(formName);
		if (catalogItem == null || StringUtils.isEmpty(catalogItem.getId())) {
			return null;
		}
		String customFormType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		String customFormSourceId = catalogItem.getId();

		return this.getCustomFormByTypeAndSourcePrimitive(customFormType, customFormSourceId);
	}

	/**
	 * Retrieve the Cloud Assembly Blueprint content source available for the
	 * configured project.
	 *
	 * @return VraNgContentSource objects.
	 * @see VraNgContentSource
	 */
	protected VraNgContentSource getBlueprintContentSourceForProjectPrimitive() {
		Gson gson = new Gson();

		Map<String, String> params = new HashMap<>();
		String projectIdentifier = getProjectId();
		params.put("projectId", projectIdentifier);
		return this.getPagedContent(SERVICE_CONTENT_SOURCE, params).stream()
				.filter(jsonOb -> VraNgContentSourceType.BLUEPRINT.toString()
						.equals(jsonOb.get("typeId").getAsString()))
				.map(jsonOb -> gson.fromJson(jsonOb, VraNgContentSource.class))
				.filter(contentSource -> contentSource.getProjectId().equals(projectIdentifier)).findFirst()
				.orElse(null);
	}

	/**
	 * Retrieve all content sources available for the configured project.
	 *
	 * @param project project
	 * @return list of VraNgContentSource objects.
	 * @see VraNgContentSource
	 */
	protected List<VraNgContentSourceBase> getContentSourcesForProjectPrimitive(final String project) {
		Gson gson = new Gson();

		Map<String, String> params = new HashMap<>();
		String csProjectId = StringUtils.isEmpty(project) ? getProjectId() : project;
		params.put("projectId", csProjectId);

		return this.getPagedContent(SERVICE_CONTENT_SOURCE, params).stream().map(jsonOb -> {
			VraNgContentSourceType type = VraNgContentSourceType.fromString(jsonOb.get("typeId").getAsString());
			return gson.fromJson(jsonOb, type.getTypeClass());
		}).collect(Collectors.toList());
	}

	/**
	 * Retrieve Content Sources for the Projects.
	 *
	 * @param projects List of projects
	 * @return list of VraNg Content Source objects.
	 */
	protected Map<String, List<VraNgContentSourceBase>> getContentSourcesForProjectsPrimitive(
			final List<String> projects) {
		Map<String, List<VraNgContentSourceBase>> retVal = new HashMap<>();
		for (String project : projects) {
			List<VraNgContentSourceBase> contentSources = this.getContentSourcesForProjectPrimitive(project);
			if (contentSources != null && !contentSources.isEmpty()) {
				retVal.put(project, contentSources);
			}
		}

		return retVal;
	}

	/**
	 * Retrieve all catalog items available for the configured project.
	 *
	 * @param project project
	 * @return list of VraNgCatalogItem objects.
	 * @see VraNgCatalogItem
	 */
	protected List<VraNgCatalogItem> getCatalogItemsForProjectPrimitive(final String project) {
		Gson gson = new Gson();

		Map<String, String> params = new HashMap<>();
		String ciProjectId = StringUtils.isEmpty(project) ? getProjectId() : project;
		params.put("projectId", ciProjectId);

		return this.getPagedContent(SERVICE_CATALOG_ADMIN_ITEMS, params).stream().filter(jsonOb -> jsonOb != null)
				.map(jsonOb -> gson.fromJson(jsonOb.toString(), VraNgCatalogItem.class)).collect(Collectors.toList());
	}

	/**
	 * Deletes a catalog item by id.
	 *
	 * @param catalogItemId catalogItemId
	 * @return the response
	 */
	protected ResponseEntity<String> deleteCatalogItemPrimitive(final String catalogItemId) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_CATALOG_ADMIN_ITEMS + "/" + catalogItemId).build();
		return restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Retrieve Catalog items For Projects.
	 *
	 * @param projects List of Projects
	 * @return VraNg Content Source.
	 */
	protected Map<String, List<VraNgCatalogItem>> getCatalogItemsForProjectsPrimitive(final List<String> projects) {
		Map<String, List<VraNgCatalogItem>> retVal = new HashMap<>();
		for (String project : projects) {
			List<VraNgCatalogItem> catalogItems = this.getCatalogItemsForProjectPrimitive(project);
			if (catalogItems != null && !catalogItems.isEmpty()) {
				retVal.put(project, catalogItems);
			}
		}

		return retVal;
	}

	/**
	 * Import Custom Form.
	 *
	 * @param customForm VraNg Custom Form
	 * @param sourceId   Source ID
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	public void importCustomFormPrimitive(final VraNgCustomForm customForm, final String sourceId)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CUSTOM_FORM));

		String customFormFormat = CUSTOM_FORM_DEFAULT_FORMAT; // Some vro versions don't specify the format. Assuming
		// JSON format as default
		if (customForm.getFormFormat() != null && !customForm.getFormFormat().equals("")) {
			customFormFormat = customForm.getFormFormat();
		}

		Map<String, Object> map = new HashMap<>();
		map.put("name", customForm.getName());
		map.put("form", customForm.getForm());
		map.put("sourceType", customForm.getSourceType());
		map.put("sourceId", sourceId);
		map.put("type", customForm.getType());
		map.put("status", customForm.getStatus());
		map.put("formFormat", customFormFormat);
		map.put("styles", customForm.getStyles());

		String jsonBody = this.getJsonString(map);

		this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
	}

	/**
	 * Import Subscriptions.
	 *
	 * @param subscriptionName subscription name
	 * @param subscriptionJson subscription JSON
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected void importSubscriptionPrimitive(final String subscriptionName, final String subscriptionJson)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SUBSCRIPTION).build();

		this.postJsonPrimitive(url, HttpMethod.POST, subscriptionJson);
	}

	/**
	 * Deletes a sub.
	 *
	 * @param subscriptionId subscriptionId
	 * @return the response
	 */
	protected ResponseEntity<String> deleteSubscriptionPrimitive(final String subscriptionId)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SUBSCRIPTION + "/" + subscriptionId).build();
		return restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Retrieve All Subscriptions.
	 *
	 * @param filter filter
	 * @return VraNg Subscriptions.
	 */
	public Map<String, VraNgSubscription> getAllSubscriptionsPrimitive(final String filter) {
		LOGGER.debug("Getting all subscriptions with filter: {}", filter);

		Map<String, String> params = new HashMap<>();
		params.put("$filter", filter);
		List<JsonObject> allResults = this.getPagedContent(SERVICE_SUBSCRIPTION, params);

		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		String projectIdentifier = getProjectId();

		allResults.forEach(ob -> {
			Set<String> projectIds = new HashSet<>();
			JsonElement constraints = ob.get("constraints");
			if (constraints != null) {
				JsonElement pIds = constraints.getAsJsonObject().get("projectId");
				// Check if object and contents of object are not null
				if (pIds != null && !pIds.isJsonNull()) {
					pIds.getAsJsonArray().forEach(pId -> projectIds.add(pId.getAsString()));
				}
			}

			JsonElement id = ob.get("id");
			JsonElement name = ob.get("name");

			if (projectIds.isEmpty() || (projectIds.contains(projectIdentifier) && id != null && name != null)) {
				String json = ob.toString();
				subscriptions.put(id.getAsString(), new VraNgSubscription(id.getAsString(), name.getAsString(), json));
			}
		});

		return subscriptions;
	}

	/**
	 * Import Scenario.
	 *
	 * @param scenarioJson scenario JSON
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected void importScenarioPrimitive(final String scenarioJson)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SCENARIO).setParameter("expandBody", "true").build();

		this.postJsonPrimitive(url, HttpMethod.POST, scenarioJson);
	}

	/**
	 * Delete a scenario customization.
	 *
	 * @param objId objId
	 * @return the response
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected ResponseEntity<String> deleteScenarioPrimitive(final String objId)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SCENARIO + "/" + objId).build();
		return restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}
	/**
	 * Retrieve a scenario customization.
	 *
	 * @param objId objId
	 * @return the response
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected VraNgScenario getScenarioPrimitive(final String objId)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SCENARIO + "/" + objId).setParameter("expandBody", "true").build();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), 
			String.class);
		JsonElement root = JsonParser.parseString(response.getBody());
		if (!root.isJsonObject()) {
			return null;
		}
		JsonObject ob = root.getAsJsonObject();
		Boolean enabled = ob.get("enabled").getAsBoolean();
		String scenarioCategory = ob.get("scenarioCategory").getAsString();
		String scenarioName = ob.get("scenarioName").getAsString();
		String scenarioId = ob.get("scenarioId").getAsString();
		String subject = ob.has("subject") ? ob.get("subject").getAsString() : null;
		String body = ob.has("body") ? ob.get("body").getAsString() : null;

		if (subject == null || body == null) {
			URI url2 = getURIBuilder().setPath(SERVICE_SCENARIO + "/" + objId).setParameter("expandBody", "true").setParameter("defaultConfig", "true").build();
			ResponseEntity<String> response2 = restTemplate.exchange(url2, HttpMethod.GET, getDefaultHttpEntity(), 
				String.class);
			JsonElement root2 = JsonParser.parseString(response2.getBody());
			if (!root2.isJsonObject()) {
				return null;
			}
			JsonObject ob2 = root2.getAsJsonObject();
			if (subject == null) {
				subject = ob2.get("subject").getAsString();
			}
			if (body == null) {
				body = ob2.get("body").getAsString();
			}
		}
		return new VraNgScenario(enabled, scenarioCategory, scenarioName, scenarioId, subject, body);
	}

	/**
	 * Retrieve Scenario by name.
	 *
	 * @param name Scenario name
	 * @return VraNg Scenario.
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected VraNgScenario getScenarioByNamePrimitive(final String name)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SCENARIO).build();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), 
			String.class);
		
		JsonElement root = JsonParser.parseString(response.getBody());

		if (!root.isJsonObject()) {
			return null;
		}
		JsonArray content = root.getAsJsonObject().getAsJsonArray("content");

		for (JsonElement o: content) {
			JsonObject ob = o.getAsJsonObject();
			String scenarioName = ob.get("scenarioName").getAsString();
			if (scenarioName.equals(name)) {
				String scenarioId = ob.get("scenarioId").getAsString();
				try {
					VraNgScenario scenario = getScenarioPrimitive(scenarioId);
					if (scenario != null) {
						return scenario;
					}	
				} catch (Exception e) { 
					throw new RuntimeException(
						String.format("Error ocurred during during reading of scenario. Message: %s", e.getMessage()));
				}
			}
		}
		return null;
	}

	/**
	 * Retrieve All Scenarios.
	 *
	 * @return VraNg Scenarios.
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected List<VraNgScenario> getAllScenariosPrimitive()
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SCENARIO).build();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), 
			String.class);
		
		JsonElement root = JsonParser.parseString(response.getBody());

		List<VraNgScenario> scenarios = new ArrayList<>();

		if (!root.isJsonObject()) {
			return scenarios;
		}
		JsonArray content = root.getAsJsonObject().getAsJsonArray("content");

		content.forEach(o -> {
			JsonObject ob = o.getAsJsonObject();
			String scenarioId = ob.get("scenarioId").getAsString();
			try {
				VraNgScenario scenario = getScenarioPrimitive(scenarioId);
				if (scenario != null) {
					scenarios.add(scenario);
				}
			} catch (Exception e) { 
				throw new RuntimeException(
					String.format("Error ocurred during reading of scenario. Message: %s", e.getMessage()));
			}
		});
		return scenarios;
	}

	/**
	 * Retrieve All Cloud Accounts.
	 *
	 * @return List of Cloud accounts.
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected List<VraNgCloudAccount> getAllCloudAccounts() throws URISyntaxException {
		List<VraNgCloudAccount> retVal = new ArrayList<>();

		URI url = getURIBuilder().setPath(SERVICE_CLOUD_ACCOUNT).setParameter("apiVersion", this.getVersion()).build();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());

		if (!root.isJsonObject()) {
			return retVal;
		}
		String organizationId = VraNgOrganizationUtil.getOrganization(this, configuration).getId();
		root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
			JsonObject ob = o.getAsJsonObject();

			JsonElement id = ob.get("id");
			JsonElement name = ob.get("name");
			JsonElement orgId = ob.get("orgId");
			JsonElement type = ob.get("cloudAccountType");
			JsonElement tagsElement = ob.get("tags");
			JsonElement linksElement = ob.get("_links");

			if (orgId.getAsString().equals(organizationId)) {
				List<String> tags = this.getTags(tagsElement);
				List<String> regionIds = this.getRegions(linksElement);
				retVal.add(new VraNgCloudAccount(id.getAsString(), name.getAsString(), type.getAsString(), regionIds,
						tags));
			}
		});

		return retVal;
	}

	/**
	 * Retrieve a cloud account by its id.
	 *
	 * @param id cloud account id
	 * @return VraNgRegion
	 */
	protected VraNgCloudAccount getCloudAccountPrimitive(final String id) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CLOUD_ACCOUNT + "/" + id));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());

		if (root.isJsonObject()) {
			JsonObject ob = root.getAsJsonObject();

			String cloudAccountId = ob.get("id").getAsString();
			String name = ob.get("name").getAsString();
			String type = ob.get("cloudAccountType").getAsString();
			JsonElement tagsElement = ob.get("tags");
			JsonElement linkElement = ob.get("_links");

			List<String> tags = this.getTags(tagsElement);
			List<String> regionIds = this.getRegions(linkElement);

			return new VraNgCloudAccount(cloudAccountId, name, type, regionIds, tags);
		}
		return null;
	}

	/**
	 * Retrieve a region by its id.
	 *
	 * @param id region id
	 * @return VraNgRegion
	 */
	protected VraNgRegion getRegionPrimitive(final String id) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_REGION + "/" + id));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());

		if (root.isJsonObject()) {
			JsonObject ob = root.getAsJsonObject();
			String cloudAccountId = getLinkCloudAccountId(ob);
			String regionid = ob.get("id").getAsString();
			return new VraNgRegion(regionid, cloudAccountId);
		}
		return null;
	}

	/**
	 * Retrieve Secret by name (name is unique for secrets).
	 *
	 * @param name of the secret
	 * @return VraNgSecret item
	 */
	protected VraNgSecret getSecretPrimitive(final String name) {
		String queryString = String.format("$filter=name eq '%s' and projectId eq '%s'", name, getProjectId());
		URI url = getURI(getURIBuilder().setPath(SERVICE_SECRET).setCustomQuery(queryString));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());
		JsonObject ob = root.getAsJsonObject();

		if (ob.get("numberOfElements").getAsInt() > 0) {
			JsonArray entities = ob.get("content").getAsJsonArray();

			if (entities.size() > 0) {
				return new Gson().fromJson(entities.get(0).getAsJsonObject(), VraNgSecret.class);
			}
		}

		return null;
	}

	/**
	 * Retrieve Catalog Item By Blueprint Name.
	 *
	 * @param blueprintName Blueprint name
	 * @return VraNg Content Source.
	 */
	protected VraNgCatalogItem getCatalogItemByBlueprintNamePrimitive(final String blueprintName) {
		Map<String, String> params = new HashMap<>();
		params.put("search", blueprintName.trim());

		List<JsonObject> results = this.getPagedContent(SERVICE_CATALOG_ADMIN_ITEMS, params);
		// filter the results matching the blueprintName
		JsonObject result = results.stream().filter(item -> {
			JsonObject ob = item.getAsJsonObject();
			if (ob == null) {
				return Boolean.FALSE;
			}
			String name = ob.get("name").getAsString().trim();
			return name.equalsIgnoreCase(blueprintName.trim());
		}).findFirst().orElse(null);

		// return null if the blueprint is not found
		if (result == null) {
			return null;
		}

		// construct the retrieved blueprint object
		String id = result.get("id").getAsString();
		String sourceId = result.get("sourceId").getAsString();
		String sourceName = result.get("sourceName").getAsString();
		JsonObject typeObj = result.get("type").getAsJsonObject();
		// additional sanity check
		if (typeObj == null) {
			throw new RuntimeException(
					String.format("Unable to extract catalog item type for blueprint '%s'", blueprintName));
		}
		VraNgCatalogItemType type = new Gson().fromJson(typeObj, VraNgCatalogItemType.class);

		return new VraNgCatalogItem(id, sourceId, result.get("name").getAsString().trim(), sourceName, type);
	}

	/**
	 * Common retriever for paged content. It performs GET requests against the
	 * given path until the final page has been reached.
	 *
	 * @param path      URL path
	 * @param paramsMap any number of query parameters
	 * @return combined results
	 */
	private List<JsonObject> getPagedContent(final String path, final Map<String, String> paramsMap) {
		URIBuilder uriBuilder = getURIBuilder().setPath(String.format(path)).setParameter("page", "0")
				.setParameter("size", String.valueOf(PAGE_SIZE));

		// add arbitrary parameters
		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}
		URI uri = getURI(uriBuilder);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		if (response == null) {
			return Collections.emptyList();
		}
		JsonElement root = JsonParser.parseString(response.getBody());
		if (root == null) {
			return Collections.emptyList();
		}
		List<JsonObject> allResults = new ArrayList<>();
		Integer totalPages = root.getAsJsonObject().get("totalPages").getAsInt();
		for (int page = 0; page < totalPages; page++) {
			JsonArray content = root.getAsJsonObject().get("content").getAsJsonArray();
			for (int i = 0; i < content.size(); i++) {
				allResults.add(content.get(i).getAsJsonObject());
			}
			// no further REST call is needed if all results are on one page
			if (totalPages == 1) {
				return allResults;
			}
			uriBuilder.setParameter("page", String.valueOf(page + 1));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, getDefaultHttpEntity(), String.class);
			root = JsonParser.parseString(response.getBody());
		}

		return allResults;
	}

	/**
	 * Retriever for paged content based on totalElements and numberOfElements.
	 *
	 * @param path      URL path
	 * @param paramsMap any number of query parameters
	 * @return combined results
	 */
	private List<JsonObject> getTotalElements(final String path, final Map<String, String> paramsMap) {

		URIBuilder uriBuilder = getURIBuilder().setPath(String.format(path))
				.setParameter("$top", String.valueOf(PAGE_SIZE)).setParameter("$skip", String.valueOf(0));

		// add arbitrary parameters
		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}

		List<JsonObject> allResults = new ArrayList<>();
		int page = 0;
		Integer totalElements = 0;
		Integer numberOfElements = 0;
		do {
			uriBuilder.setParameter("$skip", String.valueOf(PAGE_SIZE * (page)));
			ResponseEntity<String> response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET,
					getDefaultHttpEntity(), String.class);

			JsonElement root = JsonParser.parseString(response.getBody());

			totalElements = root.getAsJsonObject().get("totalElements").getAsInt();
			numberOfElements = root.getAsJsonObject().get("numberOfElements").getAsInt();
			LOGGER.debug(String.format("Page %d number of elements: %d", page, numberOfElements));
			JsonArray content = root.getAsJsonObject().get("content").getAsJsonArray();

			for (int i = 0; i < content.size(); i++) {
				allResults.add(content.get(i).getAsJsonObject());
			}

			page += 1;
		} while ((page * PAGE_SIZE) < totalElements);

		LOGGER.debug(String.format("Total pages: %d, Total elements: %d", page, totalElements));
		return allResults;
	}

	/**
	 * Retrieve Vra Workflow Content Source.
	 *
	 * @param id ID
	 * @return VraNg Workflow Content Source.
	 */
	protected VraNgWorkflowContentSource getVraWorkflowContentSourcePrimitive(final String id) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CONTENT_SOURCE + "/" + id));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		if (StringUtils.isEmpty(response.getBody())) {
			return null;
		}
		VraNgWorkflowContentSource retVal = null;
		try {
			retVal = mapper.readValue(response.getBody(), VraNgWorkflowContentSource.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return retVal;
	}

	/**
	 * Retrieve Content Source.
	 *
	 * @param id ID
	 * @return VraNg Content Source.
	 */
	protected VraNgContentSourceBase getContentSourcePrimitive(final String id) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CONTENT_SOURCE + "/" + id));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());
		if (!root.isJsonObject()) {
			return null;
		}
		JsonObject ob = root.getAsJsonObject();
		VraNgContentSourceType type = VraNgContentSourceType.fromString(ob.get("typeId").getAsString());
		VraNgContentSourceBase result = new Gson().fromJson(root, type.getTypeClass());
		return result;
	}

	/**
	 * Retrieve all catalog entitlements for the configured project.
	 *
	 * @param project Project
	 * @return list of VraNgCatalogEntitlement objects.
	 * @see VraNgCatalogEntitlement
	 */
	private VraNgCatalogEntitlementDto[] getCatalogEntitlementsPerProject(final String project) {
		LOGGER.debug("Fetching catalog entitlement for project '{}'", project);

		URI url = getURI(getURIBuilder().setPath(SERVICE_CATALOG_ENTITLEMENTS).addParameter("projectId", project));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().serializeNulls().create();
		VraNgCatalogEntitlementDto[] entitlements = gson.fromJson(response.getBody(),
				VraNgCatalogEntitlementDto[].class);
		return entitlements;
	}

	/**
	 * Retrieve all catalog entitlements by name for all of the configured projects.
	 *
	 * @return list of VraNgCatalogEntitlement objects that are shared for all of
	 *         the configured projects.
	 * @see VraNgCatalogEntitlement
	 */
	protected List<VraNgCatalogEntitlement> getAllCatalogEntitlementsPrimitive() {
		LOGGER.debug("Fetching all available catalog entitlements");
		List<VraNgProject> allProjects = this.getProjectsPrimitive();
		if (allProjects == null || allProjects.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, List<VraNgCatalogEntitlementDto>> allEntitlements = allProjects.stream()
				.map(project -> this.getCatalogEntitlementsPerProject(project.getId())).flatMap(Arrays::stream)
				.collect(Collectors.groupingBy(el -> el.getDefinition().get("name"), Collectors.toList()));
		return allEntitlements.values().stream().map(entitlementsGroup -> {
			VraNgCatalogEntitlementDto modelEntitlement = entitlementsGroup.get(0);
			List<String> projectIds = entitlementsGroup.stream().map(ent -> ent.getProjectId())
					.collect(Collectors.toList());

			VraNgCatalogEntitlement entitlement = new VraNgCatalogEntitlement(modelEntitlement.getId(), null,
					modelEntitlement.getDefinition().get("name"), projectIds,
					VraNgCatalogEntitlementType.fromString(modelEntitlement.getDefinition().get("type")),
					VraNgContentSourceType.fromString(modelEntitlement.getDefinition().get("sourceType")));
			String iconId = modelEntitlement.getDefinition().get("iconId");
			if (iconId != null) {
				entitlement.setIconId(iconId);
			}
			return entitlement;
		}).collect(Collectors.toList());

	}

	/**
	 * Deletes a catalog entitlement.
	 *
	 * @param entitlementId entitlementId
	 * @return the response
	 */
	protected ResponseEntity<String> deleteCatalogEntitlementPrimitive(final String entitlementId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CATALOG_ENTITLEMENTS + "/" + entitlementId));
		return restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Create new entitlement.
	 *
	 * @param entitlement - the entitlement definition to be created.
	 * @param project     - project id of where to share the entitlement definition.
	 * @throws URISyntaxException exception, RuntimeException
	 */
	protected void createCatalogEntitlementPrimitive(final VraNgCatalogEntitlement entitlement, final String project)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_CATALOG_ENTITLEMENTS).build();

		// prepare payload
		Map<String, Object> definition = new LinkedHashMap<>();
		definition.put("id", entitlement.getSourceId());
		definition.put("name", entitlement.getName());
		definition.put("type", entitlement.getType());
		definition.put("sourceType", entitlement.getSourceType().toString());
		definition.put("iconId", entitlement.getIconId() == null ? null : entitlement.getIconId());

		Map<String, Object> payload = new LinkedHashMap<>();
		if (!StringUtils.isEmpty(entitlement.getId())) {
			payload.put("id", entitlement.getId());
		}
		payload.put("projectId", project);
		payload.put("definition", definition);

		// prepare request
		String jsonBody = this.getJsonString(payload);
		ResponseEntity<String> response;
		try {
			response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(
					String.format("Error ocurred during creating of catalog entitlement. Message: %s", e.getMessage()));
		}

		if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
			throw new RuntimeException(
					String.format("Error ocurred during creating of catalog entitlement. HTTP Status code %s : ( %s )",
							response.getStatusCode(), response.getBody()));
		}
	}

	/**
	 * getCatalogItemVersionsPrimitive.
	 *
	 * @param catalogItemId catalog item id.
	 * @return catalogItemVersions JsonArray
	 */
	protected JsonArray getCatalogItemVersionsPrimitive(final String catalogItemId) {
		String path = SERVICE_CATALOG_ADMIN_ITEMS + "/" + catalogItemId + "/versions";
		URI url = getURI(getURIBuilder().setPath(path));
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			JsonElement root = JsonParser.parseString(response.getBody());
			if (root.isJsonObject()) {
				return root.getAsJsonObject().getAsJsonArray("content");
			}
		} catch (RestClientException e) {
			LOGGER.info("No versions found for catalog item id '{}'", catalogItemId);
		}

		return null;
	}

	/**
	 * fetchRequestFormPrimitive.
	 *
	 * @param sourceType source type
	 * @param sourceId   source id
	 * @param formId     form id
	 * @return customForm VraNgCustomForm
	 */
	protected VraNgCustomForm fetchRequestFormPrimitive(final String sourceType, final String sourceId,
			final String formId) {
		final String formType = "requestForm";
		URI url = getURI(getURIBuilder().setPath(FETCH_REQUEST_FORM).setParameter("formType", formType)
				.setParameter("sourceId", sourceId).setParameter("sourceType", sourceType)
				.setParameter("formId", formId));
		Map<String, Object> map = new HashMap<>();
		map.put("type", "object");
		try {
			ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, this.getJsonString(map));
			JsonElement root = JsonParser.parseString(response.getBody());
			if (root.isJsonObject()) {
				return new Gson().fromJson(root.getAsJsonObject(), VraNgCustomForm.class);
			}
		} catch (RestClientException e) {
			LOGGER.info("No custom form found for source id '{}' and source type '{}'", sourceId, sourceType, e);
		}

		return null;
	}

	/**
	 * Get Custom Form By Type And Source.
	 *
	 * @param sourceType Source Type
	 * @param sourceId   Source ID
	 * @return VraNg Custom Form.
	 */
	protected VraNgCustomForm getCustomFormByTypeAndSourcePrimitive(final String sourceType, final String sourceId) {
		final String formType = "requestForm";
		URI url = getURI(
				getURIBuilder().setPath(SERVICE_CUSTOM_FORM_BY_SOURCE_AND_TYPE).setParameter("formType", formType)
						.setParameter("sourceId", sourceId).setParameter("sourceType", sourceType));

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			JsonElement root = JsonParser.parseString(response.getBody());
			if (root.isJsonObject()) {
				return new Gson().fromJson(root.getAsJsonObject(), VraNgCustomForm.class);
			}
		} catch (RestClientException e) {
			LOGGER.info("No custom form found for source id '{}' and source type '{}'", sourceId, sourceType);
		}

		return null;
	}

	/**
	 * Parse links element for regions and take their ids.
	 *
	 * @param linksElement link element
	 * @return list of region ids
	 */
	private List<String> getRegions(final JsonElement linksElement) {
		List<String> regionIds = new ArrayList<>();

		if (isJsonElementPresent(linksElement)) {
			JsonElement regions = linksElement.getAsJsonObject().get("regions");
			if (isJsonElementPresent(regions)) {
				JsonElement hrefs = regions.getAsJsonObject().get("hrefs");
				if (isJsonElementPresent(hrefs)) {
					hrefs.getAsJsonArray().forEach(h -> {
						String href = h.getAsString();
						String regionId = href.substring(href.lastIndexOf('/') + 1);
						regionIds.add(regionId);
					});
				}
			}
		}

		return regionIds;
	}

	private List<String> getTags(final JsonElement tagsElement) {
		List<String> tags = new ArrayList<>();

		if (isJsonElementPresent(tagsElement)) {
			tagsElement.getAsJsonArray().forEach(tag -> {
				JsonObject tagObj = tag.getAsJsonObject();
				String key = tagObj.get("key").getAsString();
				String value = tagObj.get("value").getAsString();
				String result = key;
				if (StringUtils.isNotEmpty(value)) {
					result = result + ":" + value;
				}

				tags.add(result);
			});
		}

		return tags;
	}

	/**
	 * Alias to getAllPropertyGroupsPrimitive( String nameFilter ) without any
	 * filter specified.
	 *
	 * @return list of property groups
	 */
	protected List<VraNgPropertyGroup> getAllPropertyGroupsPrimitive() {
		return this.getAllPropertyGroupsPrimitive(null);
	}

	/**
	 * Deletes a PG.
	 *
	 * @param pgId - the PG ID
	 * @throws URISyntaxException in case of erros while forming the URI
	 * @return the response
	 */
	protected ResponseEntity<String> deletePropertyGroupPrimitive(String pgId) throws URISyntaxException {
		String deleteURL = String.format(SERVICE_GET_PROPERTY_GROUPS + "/%s", pgId);
		URI url = getURIBuilder().setPath(deleteURL).build();
		return restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
	}

	/**
	 * Returns a list of all property groups This will keep on making requests until
	 * all the groups are retrieved. If the nameFilter is null, all will be
	 * accepted, otherwise only property group names that contain the filter will be
	 * accepted.
	 * <p>
	 * Note: hasMore is retrieved from the "last" property. JsonElement supports a
	 * getAsBoolean function however it does not cast the strings "true" or "false"
	 * to boolean.
	 * <p>
	 * Note: When doing propertyGroupObject.get( "name" ).toString() the name is
	 * returned with surrounded '"', so it is trimmed
	 *
	 * @param nameFilter filter
	 * @return list of property groups
	 */
	protected List<VraNgPropertyGroup> getAllPropertyGroupsPrimitive(final String nameFilter) {
		boolean hasMore = true;
		int elementsToSkip = 0;
		List<VraNgPropertyGroup> propertyGroups = new ArrayList<>();

		while (hasMore) {
			URI url = getURI(getURIBuilder().setPath(SERVICE_GET_PROPERTY_GROUPS).addParameter("$skip",
					String.valueOf(elementsToSkip)));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			JsonElement jsonElement = JsonParser.parseString(response.getBody());
			hasMore = jsonElement.getAsJsonObject().get("last").toString().equals("false");
			JsonArray content = jsonElement.getAsJsonObject().get("content").getAsJsonArray();
			elementsToSkip += content.size();

			for (int i = 0; i < content.size(); i++) {
				JsonObject propertyGroupObject = content.get(i).getAsJsonObject();
				String propertyGroupName = StringUtils.strip(propertyGroupObject.get("name").toString(), "\"");

				// Accept all if null or filter if given
				if (nameFilter == null || propertyGroupName.contains(nameFilter)) {
					propertyGroups.add(new VraNgPropertyGroup(propertyGroupName,
							StringUtils.strip(propertyGroupObject.get("id").toString(), "\""),
							propertyGroupObject.toString()));
				}
			}
		}

		return propertyGroups;
	}

	/**
	 * Performs a POST request to create the given property group.
	 *
	 * @param propertyGroup - Property group to be posted
	 */
	public void createPropertyGroupPrimitive(final VraNgPropertyGroup propertyGroup) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_POST_PROPERTY_GROUP));
		this.postJsonPrimitive(url, HttpMethod.POST, propertyGroup.getRawData());
	}

	/**
	 * Performs a PUT request to update the given property group.
	 *
	 * @param propertyGroup - Property group to update
	 */
	public void updatePropertyGroupPrimitive(final VraNgPropertyGroup propertyGroup) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_PUT_PROPERTY_GROUP + "/" + propertyGroup.getId()));
		this.putJsonPrimitive(url, propertyGroup.getRawData());
	}

	/**
	 * Retrieve fabric entity name. This method calls a requested URL and returns
	 * the name property of the response.
	 *
	 * @param fabricUrl url
	 * @return fabric entity name
	 */
	protected String getFabricEntityNamePrimitive(final String fabricUrl) {
		URI url = getURI(getURIBuilder().setPath(fabricUrl));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		if (root.isJsonObject()) {
			return root.getAsJsonObject().get("name").getAsString();
		}
		return null;
	}

	/**
	 * Retrieve Fabric Entity ID.
	 *
	 * @param fabricType fabric type
	 * @param fabricName fabric name
	 * @return Fabric Entity ID
	 */
	protected String getFabricEntityIdPrimitive(final String fabricType, final String fabricName) {
		String queryString = String.format("$filter=name eq '%s'", fabricName);
		URI url = getURI(getURIBuilder().setPath(SERVICE_IAAS_BASE + "/" + fabricType).setCustomQuery(queryString));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());
		JsonObject ob = root.getAsJsonObject();

		// look for a fabric entity with name equals to the requested name
		if (ob.get("numberOfElements").getAsInt() > 0) {
			JsonArray entities = ob.get("content").getAsJsonArray();
			Iterator<JsonElement> it = entities.iterator();
			while (it.hasNext()) {
				JsonObject entity = it.next().getAsJsonObject();
				String name = entity.get("name").getAsString();
				if (name.equals(fabricName)) {
					return entity.get("id").getAsString();
				}
			}
		}

		return null;
	}

	/**
	 * Retrieve Organization By Name.
	 *
	 * @param organizationName name
	 * @return VraNgOrganization
	 */
	public VraNgOrganization getOrganizationByName(final String organizationName) {
		if (StringUtils.isEmpty(organizationName)) {
			return null;
		}

		URIBuilder uriBuilder = getURIBuilder().setHost(configuration.getAuthHost()).setPath(SERVICE_VRA_ORGANIZATIONS)
				.setParameter("expand", "1");

		URI url;
		Optional<VraNgOrganization> result = null;
		try {
			url = uriBuilder.build();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().serializeNulls()
					.create();
			VraNgOrganizations organizations = gson.fromJson(response.getBody(), VraNgOrganizations.class);
			result = organizations.getItems().stream().filter(vraNgOrganization -> {
				return vraNgOrganization.getName().equalsIgnoreCase(organizationName);
			}).findFirst();
		} catch (URISyntaxException e) {
			throw new RuntimeException(String.format("Unable to build REST URI to fetch organization name %s : %s",
					organizationName, e.getMessage()));
		} catch (Exception error) {
			throw new RuntimeException(
					"Organization not found by the provided name. Error message: " + error.getMessage(), error);
		}
		return result.isPresent() ? result.get() : null;
	}

	/**
	 * Retrieve Organization By ID.
	 *
	 * @param organizationId organizationId
	 * @return VraNg Organization
	 */
	public VraNgOrganization getOrganizationById(final String organizationId) {
		if (StringUtils.isEmpty(organizationId)) {
			return null;
		}
		VraNgOrganization org = null;
		try {
			URIBuilder uriBuilder = getURIBuilder().setHost(configuration.getAuthHost())
					.setPath(SERVICE_VRA_ORGANIZATION + organizationId);
			URI url;
			try {
				url = uriBuilder.build();
			} catch (URISyntaxException e) {
				throw new RuntimeException(
						String.format("Unable to build REST URI to fetch organization with ID %s : %s", organizationId,
								e.getMessage()));
			}
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().serializeNulls()
					.create();
			org = gson.fromJson(response.getBody(), VraNgOrganization.class);

			LOGGER.debug("Found organization: {}", gson.toJson(org));
		} catch (Exception error) {
			throw new Error("Organization not found by the provided ID. Error message: " + error.getMessage(), error);
		}
		return org;
	}
	// =================================================
	// WORKFLOW OPERATIONS
	// =================================================

	/**
	 * Retrieve Vra Workflow Integrations.
	 *
	 * @param name name
	 * @return Resource Action
	 */
	protected VraNgIntegration getVraWorkflowIntegrationPrimitive(final String name) {
		// for vra 8.0 query string should be: name eq name
		// for vra 8.1 query string should be: endpointType eq name
		// for compatibility with both 8.0 and 8.1 the query string should combine both
		String queryString = String.format("expand=true&$filter=name eq '%s' or endpointType eq '%s'", name, name);
		URI url = getURI(getURIBuilder().setPath(SERVICE_VRA_INTEGRATIONS).setCustomQuery(queryString));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());
		VraNgIntegration retVal = new VraNgIntegration();
		if (!root.isJsonObject()) {
			return retVal;
		}

		JsonObject jsonObject = root.getAsJsonObject();
		if (!isJsonElementPresent(jsonObject)) {
			return retVal;
		}

		List<String> documentLinks = new ArrayList<>();
		JsonArray documentLinksObject = jsonObject.getAsJsonArray("documentLinks");
		documentLinksObject.forEach(link -> documentLinks.add(link.getAsString()));
		for (String documentLink : documentLinks) {
			JsonElement element = jsonObject.getAsJsonObject("documents").get(documentLink);
			if (!isJsonElementPresent(element)) {
				continue;
			}
			retVal.setEndpointConfigurationLink(documentLink);
			retVal.setName(name);
			JsonElement endpointElement = element.getAsJsonObject().get("endpointProperties");
			if (isJsonElementPresent(endpointElement) && endpointElement != null) {
				String endpointUri = endpointElement.getAsJsonObject().get("hostName").getAsString();
				retVal.setEndpointUri(endpointUri);
			}
		}

		return retVal;
	}

	/**
	 * Retrieve Vra Workflow Integrations.
	 *
	 * @return VraNg Integration
	 */
	protected List<VraNgIntegration> getVraWorkflowIntegrationsPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_VRA_INTEGRATIONS).setCustomQuery("expand=true"));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		List<VraNgIntegration> retVal = new ArrayList<>();
		JsonElement root = JsonParser.parseString(response.getBody());
		if (!root.isJsonObject()) {
			return retVal;
		}

		JsonObject jsonObject = root.getAsJsonObject();
		if (!isJsonElementPresent(jsonObject)) {
			return retVal;
		}

		List<String> documentLinks = new ArrayList<>();
		JsonArray documentLinksObject = jsonObject.getAsJsonArray("documentLinks");
		documentLinksObject.forEach(link -> documentLinks.add(link.getAsString()));
		for (String documentLink : documentLinks) {
			JsonElement document = jsonObject.getAsJsonObject("documents").get(documentLink);
			if (!isJsonElementPresent(document)) {
				continue;
			}
			if (document.getAsJsonObject() == null) {
				continue;
			}
			String name = document.getAsJsonObject().get("name").getAsString();
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			VraNgIntegration integrationItem = new VraNgIntegration();
			integrationItem.setEndpointConfigurationLink(documentLink);
			integrationItem.setName(name);
			JsonElement endpointProperties = document.getAsJsonObject().get("endpointProperties");
			if (isJsonElementPresent(endpointProperties) && endpointProperties.getAsJsonObject() != null) {
				String endpointUri;
				if (isJsonElementPresent(endpointProperties.getAsJsonObject().get("apiEndpoint"))) {
					endpointUri = endpointProperties.getAsJsonObject().get("apiEndpoint").getAsString();
				} else if (isJsonElementPresent(endpointProperties.getAsJsonObject().get("hostName"))) {
					endpointUri = endpointProperties.getAsJsonObject().get("hostName").getAsString();
				} else {
					endpointUri = "";
				}
				integrationItem.setEndpointUri(endpointUri);
			}

			retVal.add(integrationItem);
		}

		return retVal;
	}

	// =================================================
	// CUSTOM RESOURCES
	// =================================================

	/**
	 * Retrieve all Custom Resource.
	 *
	 * @return Resource Actions
	 */
	protected Map<String, VraNgCustomResource> getAllCustomResourcesPrimitive() {
		Map<String, VraNgCustomResource> customResources = new HashMap<>();
		List<JsonObject> results = this.getPagedContent(SERVICE_CUSTOM_RESOURCES, new HashMap<>());

		for (JsonObject o : results) {
			JsonObject ob = o.getAsJsonObject();
			JsonElement prjId = ob.get("projectId");
			if (isJsonElementPresent(prjId) && !getProjectId().equals(prjId.getAsString())) {
				continue;
			}

			JsonElement id = ob.get("id");
			JsonElement name = ob.get("displayName");
			String json = ob.toString();
			customResources.put(id.getAsString(), new VraNgCustomResource(id.getAsString(), name.getAsString(), json));
		}

		return customResources;
	}

	/**
	 * When importing a custom resource with additionalActions, we first need to
	 * remove them due to a vRA8 limitations. When creating a CR with
	 * additionalActions, we create the CR first then we apply the same JSON but
	 * with additionalActions added.
	 *
	 * @param customResourceJson String containing the raw JSON content of the
	 *                           custom resource
	 * @throws URISyntaxException exception in case of incorrect URI
	 */
	protected void importCustomResourcePrimitive(final String customResourceJson) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_CUSTOM_RESOURCES).build();

		// Strip additionalActions from definition
		JsonObject originalCr = JsonParser.parseString(customResourceJson).getAsJsonObject();
		JsonArray additionalActions = originalCr.getAsJsonArray("additionalActions");
		originalCr.remove("additionalActions");
		originalCr.add("additionalActions", new JsonArray());

		ResponseEntity<String> resp = this.postJsonPrimitive(url, HttpMethod.POST, originalCr.toString());

		if (resp.getStatusCode().is2xxSuccessful() && additionalActions != null && additionalActions.size() > 0) {
			// Add additionalActions to definition and upload again
			JsonObject returnedCr = JsonParser.parseString(resp.getBody()).getAsJsonObject();
			returnedCr.remove("additionalActions");
			returnedCr.add("additionalActions", additionalActions);

			ResponseEntity<String> resp2 = this.postJsonPrimitive(url, HttpMethod.POST, returnedCr.toString());
			if (!resp2.getStatusCode().is2xxSuccessful()) {
				throw new RuntimeException(String.format("Unable to import additionalActions for %s",
						originalCr.get("displayName").getAsString()));
			}
		}
	}

	/**
	 * Delete Custom Resource.
	 *
	 * @param customResourceId Resource Action JSON
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 * @return the response
	 */
	protected ResponseEntity<String> deleteCustomResourcePrimitive(final String customResourceId)
			throws URISyntaxException {
		String deleteURL = String.format(SERVICE_CUSTOM_RESOURCES + "/%s", customResourceId);
		URI url = getURIBuilder().setPath(deleteURL).build();
		return restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
	}

	// =================================================
	// RESOURCE ACTIONS
	// =================================================

	/**
	 * Retrieve all Resource Actions.
	 *
	 * @return Resource Actions
	 */
	protected Map<String, VraNgResourceAction> getAllResourceActionsPrimitive() {
		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		List<JsonObject> results = this.getPagedContent(SERVICE_RESOURCE_ACTIONS, new HashMap<>());

		for (JsonObject o : results) {
			JsonObject ob = o.getAsJsonObject();
			JsonElement prjId = ob.get("projectId");
			if (isJsonElementPresent(prjId) && !getProjectId().equals(prjId.getAsString())) {
				continue;
			}

			JsonElement id = ob.get("id");
			JsonElement name = ob.get("name");
			JsonElement resourceType = ob.get("resourceType");
			String json = ob.toString();
			resourceActions.put(id.getAsString(),
					new VraNgResourceAction(id.getAsString(), name.getAsString(), json, resourceType.getAsString()));
		}

		return resourceActions;
	}

	/**
	 * Import Resource Action.
	 *
	 * @param resourceActionJson Resource Action JSON
	 * @return Resource Action
	 * @throws URISyntaxException throws URI syntax exception in case of invalid URI
	 */
	protected String importResourceActionPrimitive(final String resourceActionJson) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_RESOURCE_ACTIONS).build();
		ResponseEntity<String> result = this.postJsonPrimitive(url, HttpMethod.POST, resourceActionJson);

		if (result.getStatusCode().is2xxSuccessful()) {
			return result.getBody();
		}
		return null;
	}

	/**
	 * Delete Resource Action.
	 *
	 * @param resourceActionId Resource Action ID to delete
	 * @return the response
	 */
	protected ResponseEntity<String> deleteResourceActionPrimitive(final String resourceActionId) {
		String deleteURL = String.format(SERVICE_RESOURCE_ACTIONS + "/%s", resourceActionId);
		URI url = getURI(getURIBuilder().setPath(deleteURL));

		return restTemplate.exchange(url, HttpMethod.DELETE, RestClient.getDefaultHttpEntity(), String.class);
	}

	// =================================================
	// ABX ACTIONS
	// =================================================

	/**
	 * Retrieve all ABX Actions.
	 *
	 * @return List of Abx Actions
	 */
	public List<AbxAction> getAllAbxActionsPrimitive() {
		List<AbxAction> actions = new ArrayList<>();
		List<JsonObject> results = this.getPagedContent(SERVICE_ABX_ACTIONS, new HashMap<>());

		LOGGER.debug("ABX Actions found on server: {}", results.size());
		results.forEach(o -> {
			JsonObject ob = o.getAsJsonObject();
			String prjId = ob.get("projectId").getAsString();
			if (getProjectId().equals(prjId)) {
				AbxAction action = new AbxAction();
				action.setId(ob.get("id").getAsString());
				action.setName(ob.get("name").getAsString());
				actions.add(action);
			}
		});
		LOGGER.debug("Actions in target project: {}", actions.size());

		return actions;
	}

	/**
	 * Retrieve ABX Constant by name (name is unique for the constants).
	 *
	 * @param name of the constant
	 * @return AbxConstant item
	 */
	protected AbxConstant getAbxConstantPrimitive(final String name) {
		String queryString = String.format("$filter=name eq '%s'", name);
		URI url = getURI(getURIBuilder().setPath(SERVICE_ABX_CONSTANT).setCustomQuery(queryString));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());
		JsonObject ob = root.getAsJsonObject();

		if (ob.get("numberOfElements").getAsInt() > 0) {
			JsonArray entities = ob.get("content").getAsJsonArray();

			if (entities.size() > 0) {
				return new Gson().fromJson(entities.get(0).getAsJsonObject(), AbxConstant.class);
			}
		}

		return null;
	}

	/**
	 * Create Abx Action.
	 *
	 * @param action Abx Action
	 * @return Abx Action ID
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 * @throws IOException        throws IO exception incase of invalid json
	 *                            response
	 */
	public String createAbxActionPrimitive(final AbxAction action) throws URISyntaxException, IOException {
		URI url = getURIBuilder().setPath(SERVICE_ABX_ACTIONS).build();

		Map<String, Object> map = createAbxActionMap(action);

		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxAction.class).getId();
	}

	/**
	 * Update Abx Action.
	 *
	 * @param id     ID
	 * @param action Abx Action
	 * @return Abx Action ID
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 * @throws IOException        throws IO exception incase of invalid json
	 *                            response
	 */
	public String updateAbxActionPrimitive(final String id, final AbxAction action)
			throws URISyntaxException, IOException {
		URI url = getURIBuilder().setPath(SERVICE_ABX_ACTIONS + "/" + id).build();

		Map<String, Object> map = createAbxActionMap(action);

		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.PUT, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxAction.class).getId();
	}

	/**
	 * Retrieve Abx Last updated Version.
	 *
	 * @param actionId actionId
	 * @return Abx Action Version
	 */
	public AbxActionVersion getAbxLastUpdatedVersionPrimitive(final String actionId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/versions")
				.addParameter("projectId", getProjectId()).addParameter("orderBy", "createdMillis DESC"));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		AbxActionVersion lastVersion = null;
		if (root.isJsonObject()) {
			JsonArray responseContent = root.getAsJsonObject().getAsJsonArray("content");
			if (responseContent.size() > 0) {
				lastVersion = new Gson().fromJson(responseContent.get(0).getAsJsonObject(), AbxActionVersion.class);
			}
		}

		return lastVersion;
	}

	/**
	 * Create Abx Version.
	 *
	 * @param actionId action ID
	 * @param version  version
	 * @return Abx Action Version
	 */
	public AbxActionVersion createAbxVersionPrimitive(final String actionId, final String version) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/versions")
				.addParameter("projectId", getProjectId()));

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", version);
		String jsonBody = this.getJsonString(map);

		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxActionVersion.class);
	}

	/**
	 * Release Abx Version.
	 *
	 * @param actionId  action ID
	 * @param versionId version ID
	 * @return Abx Action Version
	 */
	public AbxActionVersion releaseAbxVersionPrimitive(final String actionId, final String versionId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/release")
				.addParameter("projectId", getProjectId()));

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("version", versionId);
		String jsonBody = this.getJsonString(map);

		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.PUT, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxActionVersion.class);
	}

	// =================================================
	// UTILITY METHODS
	// =================================================

	private boolean isJsonElementPresent(final JsonElement jsonElement) {
		return jsonElement != null && !jsonElement.isJsonNull();
	}

	/**
	 * Extract cloud account id from a link fragment part of REST response body.
	 *
	 * @param ob fragment
	 * @return cloud account id
	 */
	private String getLinkCloudAccountId(final JsonObject ob) {
		String regionHref = ob.get("_links").getAsJsonObject().get("cloud-account").getAsJsonObject().get("href")
				.getAsString();

		return regionHref.substring(regionHref.lastIndexOf('/') + 1);
	}

	private Map<String, Object> createBlueprintMap(final VraNgBlueprint blueprint) {
		Map<String, Object> map = new LinkedHashMap<>();

		map.put("name", blueprint.getName());
		map.put("content", blueprint.getContent());
		map.put("description", blueprint.getDescription());
		map.put("requestScopeOrg", blueprint.getRequestScopeOrg());
		map.put("projectId", getProjectId());

		return map;
	}

	/**
	 * Creates ABX Action.
	 *
	 * @param action Abx Action.
	 * @return Object
	 * @throws IOException throws IO exception in case FaaS provider name is not
	 *                     correct
	 */
	protected Map<String, Object> createAbxActionMap(final AbxAction action) throws IOException {
		Map<String, Object> map = new LinkedHashMap<>();

		String[] providers = { "aws", "azure", "on-prem" };

		map.put("actionType", "SCRIPT");
		map.put("name", action.getName());
		map.put("description", action.getDescription());
		map.put("projectId", getProjectId());
		map.put("runtime", action.getPlatform().getRuntime());
		map.put("entrypoint", action.getPlatform().getEntrypoint());
		map.put("inputs", action.getAbx().getInputs());
		map.put("compressedContent", action.getBundleAsBase64());
		map.put("shared", action.getAbx().getShared());
		map.put("runtimeVersion", action.getPlatform().getRuntimeVersion());

		if (action.getPlatform().getTimeoutSec() != null && action.getPlatform().getTimeoutSec() > 0) {
			map.put("timeoutSeconds", action.getPlatform().getTimeoutSec());
		}

		if (action.getPlatform().getMemoryLimitMb() != null && action.getPlatform().getMemoryLimitMb() > 0) {
			map.put("memoryInMB", action.getPlatform().getMemoryLimitMb());
		}

		if (action.getPlatform().getProvider() != null) {
			if (Arrays.stream(providers).anyMatch(action.getPlatform().getProvider()::equals)) {
				map.put("provider", action.getPlatform().getProvider());
			} else {
				throw new RuntimeException(
						"Faas provider name is not correct. Possible values are: " + String.join(",", providers));
			}
		}

		return map;
	}

	private String getJsonString(final Map<String, Object> entity) {
		Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).serializeNulls().create();

		return gson.toJson(entity);
	}

	private ResponseEntity<String> postJsonPrimitive(final URI url, final HttpMethod method, final String jsonBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
		LOGGER.debug("Executing method {} on URI {} with entity {} ", method, url, entity);
		return restTemplate.exchange(url, method, entity, String.class);
	}

	private ResponseEntity<String> putJsonPrimitive(final URI url, final String jsonBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

		return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
	}

	private List<VraNgContentSourceBase> getContentSources() {
		Map<String, String> params = new HashMap<>();
		params.put("projectId", this.getProjectId());

		return this.getPagedContent(SERVICE_CONTENT_SOURCE, params).stream().map(contentSource -> {
			VraNgContentSourceType type = VraNgContentSourceType.fromString(contentSource.get("typeId").getAsString());
			return new Gson().fromJson(contentSource, type.getTypeClass());
		}).collect(Collectors.toList());
	}

	/**
	 * Retrieve Content Source with name.
	 *
	 * @param contentSourceName Content Source name.
	 * @return Retrieved VraNg Content Source
	 */
	public VraNgContentSourceBase getContentSourceByName(final String contentSourceName) {
		List<VraNgContentSourceBase> contentSources = this.getContentSources();

		return contentSources.stream()
				.filter(contentSource -> contentSource.getName().equalsIgnoreCase(contentSourceName)).findFirst()
				.orElse(null);
	}

	/**
	 * Delete Content Source.
	 *
	 * @param contentSourceId Content Source ID to delete.
	 * @return the response
	 */
	public ResponseEntity<String> deleteContentSource(final String contentSourceId) {
		if (StringUtils.isEmpty(contentSourceId)) {
			return null;
		}

		String deleteURL = String.format(SERVICE_CONTENT_SOURCE + "/%s", contentSourceId);
		URI url = getURI(getURIBuilder().setPath(deleteURL));

		return restTemplate.exchange(url, HttpMethod.DELETE, RestClient.getDefaultHttpEntity(), String.class);
	}

	/**
	 * Checks VRA Version.
	 *
	 * @return true if version above 81 else false.
	 */
	public boolean isVraAbove81() {
		return productVersion.getMajorVersion() != null && productVersion.getMajorVersion() >= VRA_VERSION_MAJOR
				&& this.productVersion.getMinorVersion() != null
				&& this.productVersion.getMinorVersion() >= VRA_VERSION_MINOR;
	}

	/**
	 * Checks Is VRA Cloud.
	 *
	 * @param url
	 * @return true if VRA Cloud else false.
	 */
	private boolean isVraCloud(final URI url) {
		return VRA_CLOUD_HOSTS.stream().filter(host -> url.getHost().contains(host)).count() > 0;
	}

	/**
	 * isVraAbove.
	 *
	 * @param target target version
	 * @return isIt boolean
	 */
	public boolean isVraAbove(final Version target) {
		LOGGER.debug("Product version: {}", productVersion.getString());
		LOGGER.debug("Targeted version: {}", target.getString());
		int isGreater = productVersion.compareTo(target);
		boolean is = isGreater >= 0;
		LOGGER.debug("Is greater: {}", is);
		return is;
	}

	// =================================================
	// Content Sharing Policy
	// =================================================

	/**
	 * Retrieve all content sharing policy Ids.
	 *
	 * @return list of sharing policy Ids that are available.
	 */
	protected List<VraNgContentSharingPolicy> getAllContentSharingPoliciesPrimitive() {
		Map<String, String> params = new HashMap<>();
		params.put("expandDefinition", "true");
		params.put("computeStats", "true");
		// Add additional filter to reduce the data received from server for newer vRA
		// versions (8.16 and above)
		if (isVraAbove810) {
			params.put("typeId", VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE.id);
		}
		List<VraNgContentSharingPolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
				.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgContentSharingPolicy.class))
				.filter(policy -> VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE.isTypeOf(policy))
				.collect(Collectors.toList());
		LOGGER.debug("Policy Ids found on server - {}, for projectId: {}", results.size(), this.getProjectId());

		return results;
	}

	/**
	 * Retrieve content sharing policy Id based on name.
	 *
	 * @param name name of the policy
	 * @return content sharing policy Id.
	 */
	public String getContentSharingPolicyIdByName(final String name) {
		Map<String, String> params = new HashMap<>();
		params.put("expandDefinition", "true");
		params.put("computeStats", "true");

		VraNgContentSharingPolicy policy = this.getPagedContent(SERVICE_POLICIES, params).stream()
				.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgContentSharingPolicy.class))
				.filter(p -> VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE.isTypeOf(p))
				.filter(p -> p.getName().equals(name) && p.getProjectId().equals(this.getProjectId())).findFirst()
				.orElse(null);
		if (policy == null) {
			throw new Error("Cannot find Content Sharing Policy by name" + name);
		} else {
			return policy.getId();
		}
	}

	/**
	 * Retrieve content sharing policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Created VraNg Content Sharing Policy
	 */
	protected VraNgContentSharingPolicy getContentSharingPolicyPrimitive(final String policyId) {
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy();
		URI url = getURI(getURIBuilder().setPath(String.format(SERVICE_POLICIES + "/%s", policyId)));
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			JsonElement root = JsonParser.parseString(response.getBody());
			if (!root.isJsonObject()) {
				return null;
			}
			try {
				csPolicy = new Gson().fromJson(response.getBody(), VraNgContentSharingPolicy.class);
			} catch (JsonIOException | JsonSyntaxException e) {
				throw new RuntimeException(String.format("Error parsing response for policy: %s", policyId), e);
			} catch (Exception e) {
				throw new RuntimeException(String.format("Error processing response for policy: %s", policyId), e);
			}
		} catch (Exception e) {
			if (e.getMessage().contains(NOT_FOUND_ERROR)) {
				return null;
			} else {
				throw e;
			}
		}

		return csPolicy;
	}

	// =================================================
	// Resource Quota Policy
	// =================================================
	/**
	 * Retrieve all resource quota policy Ids.
	 *
	 * @return list of resource quota policy Ids that are available.
	 */
	protected List<VraNgResourceQuotaPolicy> getAllResourceQuotaPoliciesPrimitive() {
		if (this.isVraAbove810) {
			Map<String, String> params = new HashMap<>();
			params.put("expandDefinition", "true");
			params.put("computeStats", "true");
			params.put("typeId", VraNgPolicyTypes.RESOURCE_QUOTA_POLICY_TYPE.id);

			List<VraNgResourceQuotaPolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
					.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgResourceQuotaPolicy.class))
					.filter(policy -> VraNgPolicyTypes.RESOURCE_QUOTA_POLICY_TYPE.isTypeOf(policy))
					.collect(Collectors.toList());

			LOGGER.debug("Policy Ids found on server - {}, for projectId: {}", results.size(), this.getProjectId());
			return results;

		} else {
			throw (new UnsupportedOperationException(
					"Policy import/export supported in VRA Versions  8.10.x or newer."));
		}
	}

	/**
	 * Retrieve resource quota policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Created VraNg Resource Quota Policy
	 */
	protected VraNgResourceQuotaPolicy getResourceQuotaPolicyPrimitive(final String policyId) {

		if (this.isVraAbove810) {
			URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			return new Gson().fromJson(response.getBody(), VraNgResourceQuotaPolicy.class);
		} else {
			throw (new UnsupportedOperationException(
					"Policy import/export supported in VRA Versions  8.10.x or newer."));
		}
	}
	// =================================================
	// Day 2 Actions Policy
	// =================================================

	/**
	 * Retrieve all Day 2 Actions policy Ids.
	 *
	 * @return list of Day 2 Actions policy Ids that are available.
	 */
	protected List<VraNgDay2ActionsPolicy> getAllDay2ActionsPoliciesPrimitive() {
		if (this.isVraAbove810) {
			Map<String, String> params = new HashMap<>();
			params.put("expandDefinition", "true");
			params.put("computeStats", "true");
			// filtering by typeId works on 8.16 but not on earlier versions.
			params.put("typeId", VraNgPolicyTypes.DAY2_ACTION_POLICY_TYPE.id);

			List<VraNgDay2ActionsPolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
					.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgDay2ActionsPolicy.class))
					.filter(policy -> VraNgPolicyTypes.DAY2_ACTION_POLICY_TYPE.isTypeOf(policy))
					.collect(Collectors.toList());

			LOGGER.debug("Policy Ids found on server - {}, for projectId: {}", results.size(), this.getProjectId());
			return results;
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	/**
	 * Retrieve Day 2 Actions Policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Created olicy
	 */
	protected VraNgDay2ActionsPolicy getDay2ActionsPolicyPrimitive(final String policyId) {
		if (this.isVraAbove810) {
			URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			return new Gson().fromJson(response.getBody(), VraNgDay2ActionsPolicy.class);
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	// =================================================
	// Lease Policy
	// =================================================

	/**
	 * Retrieve all lease policy Ids.
	 *
	 * @return list of sharing policy Ids that are available.
	 */
	protected List<VraNgLeasePolicy> getAllLeasePoliciesPrimitive() {
		if (this.isVraAbove810) {

			Map<String, String> params = new HashMap<>();
			params.put("expandDefinition", "true");
			params.put("computeStats", "true");
			params.put("typeId", VraNgPolicyTypes.LEASE_POLICY_TYPE.id);

			List<VraNgLeasePolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
					.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgLeasePolicy.class))
					.filter(policy -> VraNgPolicyTypes.LEASE_POLICY_TYPE.isTypeOf(policy))
					.collect(Collectors.toList());

			LOGGER.debug("Lease Policies found on server - {}, for projectId: {}", results.size(), this.getProjectId());
			return results;
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	/**
	 * Retrieve lease policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Created VraNg lease Policy
	 */
	protected VraNgLeasePolicy getLeasePolicyPrimitive(final String policyId) {
		if (this.isVraAbove810) {

			URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			return new Gson().fromJson(response.getBody(), VraNgLeasePolicy.class);
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	// =================================================
	// Deployment Limit Policy
	// =================================================

	/**
	 * Retrieve Deployment Limit Policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Deployment Limit Policy
	 */
	protected VraNgDeploymentLimitPolicy getDeploymentLimitPolicyPrimitive(final String policyId) {
		if (this.isVraAbove810) {

			URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			return new Gson().fromJson(response.getBody(), VraNgDeploymentLimitPolicy.class);
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	/**
	 * Retrieve all Deployment Limit policy.
	 *
	 * @return list of Deployment Limit policies that are available.
	 */
	protected List<VraNgDeploymentLimitPolicy> getAllDeploymentLimitPoliciesPrimitive() {
		if (this.isVraAbove810) {

			Map<String, String> params = new HashMap<>();
			params.put("expandDefinition", "true");
			params.put("computeStats", "true");
			// filtering by typeId works on 8.16 but not on earlier versions.
			params.put("typeId", VraNgPolicyTypes.DEPLOYMENT_LIMIT_POLICY_TYPE.id);

			List<VraNgDeploymentLimitPolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
					.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgDeploymentLimitPolicy.class))
					.filter(policy -> VraNgPolicyTypes.DEPLOYMENT_LIMIT_POLICY_TYPE.isTypeOf(policy))
					.collect(Collectors.toList());

			LOGGER.debug("Policy Ids found on server - {}, for projectId: {}", results.size(), this.getProjectId());
			return results;
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	// =================================================
	// Approval Policy
	// =================================================

	/**
	 * Retrieve Approval Policy based on Id.
	 *
	 * @param policyId policy id
	 * @return Approval Policy
	 */
	protected VraNgApprovalPolicy getApprovalPolicyPrimitive(final String policyId) {
		if (isVraAbove810) {
			URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			return new Gson().fromJson(response.getBody(), VraNgApprovalPolicy.class);
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
	}

	/**
	 * Retrieve all Approval policies.
	 *
	 * @return list of Approval policies that are available.
	 */
	protected List<VraNgApprovalPolicy> getAllApprovalPoliciesPrimitive() {
		if (isVraAbove810) {
			Map<String, String> params = new HashMap<>();
			params.put("expandDefinition", "true");
			params.put("computeStats", "true");
			// filtering by typeId works on 8.16 but not on earlier versions.
			// filter here to reduce traffic for newer vRA versions.
			params.put("typeId", VraNgPolicyTypes.APPROVAL_POLICY_TYPE.id);

			// filter here for older vRA versions.
			List<VraNgApprovalPolicy> results = this.getPagedContent(SERVICE_POLICIES, params).stream()
					.map(jsonOb -> new Gson().fromJson(jsonOb.toString(), VraNgApprovalPolicy.class))
					.filter(policy -> VraNgPolicyTypes.APPROVAL_POLICY_TYPE.isTypeOf(policy))
					.collect(Collectors.toList());

			LOGGER.debug("Policy Ids found on server - {}, for projectId: {}", results.size(), this.getProjectId());
			return results;
		} else {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions 8.10.x or newer.");
		}
	}

	/**
	 * Delete a policy by id.
	 *
	 * @param policyId policy id
	 * @return the response
	 */
	protected ResponseEntity<String> deletePolicyPrimitive(String policyId) {
		if (!isVraAbove810) {
			throw new UnsupportedOperationException("Policy deletion supported inVRA Versions 8.10.x or newer.");
		}
		String deleteURL = String.format(SERVICE_POLICIES + "/%s", policyId);
		URI url = getURI(getURIBuilder().setPath(deleteURL));
		LOGGER.debug("Executing method DELETE on URI {} with entity {} ", url);
		return restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
	}

	/**
	 * Create (when ID is null) or update (when ID si not null) policy.
	 * 
	 * @param policy     - the policy to create/update
	 * @param <T>        - the policy type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends VraNgPolicyDTO> void createOrUpdatePolicy(T policy) {
		if (!isVraAbove810) {
			throw new UnsupportedOperationException("Policy import/export supported inVRA Versions  8.10.x or newer.");
		}
		VraNgPolicyTypes policyType = VraNgPolicyTypes.forPolicyClass(policy.getClass());
		String createOrUpdate = policy.getId() == null ? "create" : "update";
		String failureCause = null;
		try {
			URI url = getURIBuilder().setPath(SERVICE_POLICIES).build();
			String jsonBody = new Gson().toJson(policy);
			ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
			String newId = !response.getStatusCode().is2xxSuccessful() ? null
					: new Gson().fromJson(response.getBody(), (Class<VraNgPolicyDTO>) policyType.vraNgPolicyClass).getId();
			if (newId == null) {
				failureCause = String.format("Status: %s; Body: %s", response.getStatusCode(), response.getBody());
			} else if (policy.getId() != null && !policy.getId().equals(newId)) {
				failureCause = String.format("Updated ID '%s' does not match original ID '%s'!", newId, policy.getId());
			} else {
				policy.setId(newId);
				return;
			}
		} catch (HttpClientErrorException e)  {
			String msg = e.getStatusText() != null ? e.getStatusText() : e.getMessage();
        	Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(msg);
			failureCause = matcher.find() ? String.format("{%s}", matcher.group(1)) : msg;
		} catch (Exception e) {
			failureCause = e.getMessage();
		}

		throw new RuntimeException(String.format("Could not %s %s Policy '%s' (ID=%s): %s",
				createOrUpdate,
				policyType.description,
				policy.getName(),
				policy.getId(),
				failureCause
				));
	}
}
