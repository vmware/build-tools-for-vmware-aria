/**
 * Package.
 */
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.model.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlement;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlementDto;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlementType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItemType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgImageMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganizations;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegion;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSecret;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgStorageProfile;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSubscription;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgWorkflowContentSource;
import com.vmware.pscoe.iac.artifact.utils.VraNgOrganizationUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;

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
	 * SERVICE_SUBSCRIPTION.
	 */
	private static final String SERVICE_SUBSCRIPTION = "/event-broker/api/subscriptions";
	/**
	 * SERVICE_FLAVORS.
	 */
	private static final String SERVICE_FLAVORS = "/iaas/api/flavors";
	/**
	 * SERVICE_FLAVOR_PROFILE.
	 */
	private static final String SERVICE_FLAVOR_PROFILE = "/iaas/api/flavor-profiles";
	/**
	 * SERVICE_IMAGES.
	 */
	private static final String SERVICE_IMAGES = "/iaas/api/images";
	/**
	 * SERVICE_IMAGE_PROFILE.
	 */
	private static final String SERVICE_IMAGE_PROFILE = "/iaas/api/image-profiles";
	/**
	 * SERVICE_STORAGE_PROFILE.
	 */
	private static final String SERVICE_STORAGE_PROFILE = "/iaas/api/storage-profiles";
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
	 * CONTENT_SHARING_POLICY_TYPE.
	 */
	private static final String CONTENT_SHARING_POLICY_TYPE = "com.vmware.policy.catalog.entitlement";
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
	 * productVersion.
	 */
	private static final int PAGE_SIZE = 500;

	/**
	 * isVraAbove812.
	 */
	private boolean isVraAbove812;

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
		this.isVraAbove812 = this.isVraAbove(new Version("8.12.0.21583018"));
	}

	/**
	 * Retreive Configuration.
	 * 
	 * @return Configuration
	 */
	@Override
	protected Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Retreive Version.
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
	 * Retreive Product Version.
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
	 * Retreive Project ID.
	 * 
	 * @return Project ID
	 */
	public String getProjectId() {
		if (StringUtils.isNotEmpty(projectId)) {
			return this.projectId;
		}

		if (StringUtils.isNotEmpty(configuration.getProjectId())) {
			LOGGER.debug("Using project id defined in configuration: {}", configuration.getProjectId());
			this.projectId = this.getProjectIdPrimitive(configuration.getProjectId());
			if (this.projectId == null) {
				throw new RuntimeException(String.format("Project id '%s' could not be found on target system",
						configuration.getProjectId()));
			}

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
	 * Downloads the given icon.
	 * The byte array returned by the response must be consumed and saved on the fs
	 *
	 * @param iconId iconId
	 *
	 * @return entities
	 */
	protected ResponseEntity<byte[]> downloadIconPrimitive(final String iconId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_ICON_DOWNLOAD + "/" + iconId));

		return restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), byte[].class);
	}

	/**
	 * Uploads a File.
	 * Service Broker has a limit of 100KB that is NOT enforced here.
	 *
	 * @param iconFile iconFile
	 *
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
	 * used for
	 * patching limits, it could be extended in the future
	 *
	 * @param catalogItem catalogItem
	 * @param iconId      iconId
	 *
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
	 * Retreive list of projects.
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
	 * Retreive list of projects.
	 * 
	 * @return list of projects
	 */
	protected List<VraNgProject> getProjectsPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CLOUD_PROJECT));

		Gson gson = new Gson();
		List<VraNgProject> projects = this.getTotalElements(SERVICE_CLOUD_PROJECT, new HashMap<>())
				.stream()
				.map(jsonOb -> gson.fromJson(jsonOb, VraNgProject.class))
				.collect(Collectors.toList());

		return projects;
	}

	/**
	 * Retreive Id of the project.
	 * 
	 * @param project Project Object
	 * @return Id of the project
	 */
	protected String getProjectIdPrimitive(final String project) {
		List<VraNgProject> projects = getProjectsPrimitive(project);

		return projects.stream().findFirst().isPresent() ? projects.stream().findFirst().get().getId() : null;
	}

	/**
	 * Retreive name of the project.
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
	 *
	 * @param blueprintId blueprintId
	 * @return String
	 */
	public String getBlueprintVersionsContent(final String blueprintId) {
		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
				.addParameter("$top", "1000"));

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
	 */
	public void releaseBlueprintVersionPrimitive(final String blueprintId, final String version)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS));

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("version", version);
		map.put("release", true);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
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
	 * Update Blueprint with Blueprint Object.
	 *
	 * @param blueprint Blueprint Object
	 * @return Blueprint ID.
	 */
	public String updateBlueprintPrimitive(final VraNgBlueprint blueprint)
			throws URISyntaxException {

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
		URI url = getURI(
				getURIBuilder()
						.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
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

		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_BLUEPRINT + "/" + blueprintId + SERVICE_BLUEPRINT_VERSIONS)
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
		Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
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
		return this.getPagedContent(SERVICE_CONTENT_SOURCE, params)
				.stream()
				.filter(jsonOb -> VraNgContentSourceType.BLUEPRINT.toString()
						.equals(jsonOb.get("typeId").getAsString()))
				.map(jsonOb -> gson.fromJson(jsonOb, VraNgContentSource.class))
				.filter(contentSource -> contentSource.getProjectId().equals(projectIdentifier))
				.findFirst()
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

		return this.getPagedContent(SERVICE_CONTENT_SOURCE, params)
				.stream()
				.map(jsonOb -> {
					VraNgContentSourceType type = VraNgContentSourceType.fromString(jsonOb.get(
							"typeId").getAsString());
					return gson.fromJson(jsonOb, type.getTypeClass());
				})
				.collect(Collectors.toList());
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

		return this.getPagedContent(SERVICE_CATALOG_ADMIN_ITEMS, params)
				.stream()
				.map(jsonOb -> gson.fromJson(jsonOb.toString(), VraNgCatalogItem.class))
				.collect(Collectors.toList());
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
	 * @param sourceId   Srouce ID
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
	 * @param subscriptionJson subscription json
	 */
	protected void importSubscriptionPrimitive(final String subscriptionName, final String subscriptionJson)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_SUBSCRIPTION).build();

		this.postJsonPrimitive(url, HttpMethod.POST, subscriptionJson);
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
				subscriptions.put(id.getAsString(),
						new VraNgSubscription(id.getAsString(), name.getAsString(), json));
			}
		});

		return subscriptions;
	}

	/**
	 * Retrieve All Cloud Accounts.
	 * 
	 * @return List of Cloud accounts.
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
			String orgId = ob.get("orgId").getAsString();
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

		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_CATALOG_ADMIN_ITEMS)
				.addParameter("search", blueprintName.trim())
				.addParameter("page", "0"));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());
		if (!root.isJsonObject()) {
			return null;
		}

		Integer totalPages = root.getAsJsonObject().get("totalPages").getAsInt();
		for (int page = 0; page < totalPages; page++) {
			JsonArray content = root.getAsJsonObject().getAsJsonArray("content");
			if (content == null) {
				continue;
			}

			for (int i = 0; i < content.size(); i++) {
				JsonObject ob = content.get(i).getAsJsonObject();
				if (ob == null) {
					continue;
				}

				String name = ob.get("name").getAsString().trim();
				if (name.equals(blueprintName.trim())) {
					String id = ob.get("id").getAsString();
					String sourceId = ob.get("sourceId").getAsString();
					String sourceName = ob.get("sourceName").getAsString();
					VraNgCatalogItemType type = new Gson().fromJson(ob.get("type").getAsJsonObject(),
							VraNgCatalogItemType.class);

					return new VraNgCatalogItem(id, sourceId, name, sourceName, type);
				}
			}

			url = getURI(getURIBuilder().setPath(SERVICE_CATALOG_ADMIN_ITEMS).addParameter("search", blueprintName)
					.addParameter("page", String.valueOf(page)));
			response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
			root = JsonParser.parseString(response.getBody());
		}

		return null;
	}

	/**
	 * Common retriever for paged content. It performs GET requests against
	 * the given path until the final page has been reached.
	 *
	 * @param path      URL path
	 * @param paramsMap any number of query paramters
	 * @return combined results
	 */
	private List<JsonObject> getPagedContent(final String path, final Map<String, String> paramsMap) {
		URIBuilder uriBuilder = getURIBuilder()
				.setPath(String.format(path))
				.setParameter("page", "0");

		// add arbitrary parameters
		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			uriBuilder.setParameter(entry.getKey(), entry.getValue());
		}

		ResponseEntity<String> response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET,
				getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		List<JsonObject> allResults = new ArrayList<>();

		Integer totalPages = root.getAsJsonObject().get("totalPages").getAsInt();

		for (int page = 0; page < totalPages; page++) {
			JsonArray content = root.getAsJsonObject().get("content").getAsJsonArray();

			for (int i = 0; i < content.size(); i++) {
				allResults.add(content.get(i).getAsJsonObject());
			}

			uriBuilder.setParameter("page", String.valueOf(page + 1));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, getDefaultHttpEntity(),
					String.class);

			root = JsonParser.parseString(response.getBody());
		}

		return allResults;
	}

	/**
	 * Retriever for paged content based on totalElements and numberOfElements.
	 *
	 * @param path      URL path
	 * @param paramsMap any number of query paramters
	 * @return combined results
	 */
	private List<JsonObject> getTotalElements(final String path, final Map<String, String> paramsMap) {

		URIBuilder uriBuilder = getURIBuilder()
				.setPath(String.format(path))
				.setParameter("$top", String.valueOf(PAGE_SIZE))
				.setParameter("$skip", String.valueOf(0));

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
					getDefaultHttpEntity(),
					String.class);

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
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
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

		Map<String, List<VraNgCatalogEntitlementDto>> allEntitlements = allProjects
				.stream()
				.map(project -> this.getCatalogEntitlementsPerProject(project.getId()))
				.flatMap(Arrays::stream)
				.collect(Collectors.groupingBy(el -> el.getDefinition().get("name"), Collectors.toList()));
		return allEntitlements
				.values()
				.stream()
				.map(entitlementsGroup -> {
					VraNgCatalogEntitlementDto modelEntitlement = entitlementsGroup.get(0);
					List<String> projectIds = entitlementsGroup
							.stream()
							.map(ent -> ent.getProjectId())
							.collect(Collectors.toList());

					VraNgCatalogEntitlement entitlement = new VraNgCatalogEntitlement(
							modelEntitlement.getId(),
							null,
							modelEntitlement.getDefinition().get("name"),
							projectIds,
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
			throw new RuntimeException(String.format(
					"Error ocurred during creating of catalog entitlement. HTTP Status code %s : ( %s )", response
							.getStatusCodeValue(),
					response.getBody()));
		}
	}

	/**
	 * getCatalogItemVersionsPrimitive.
	 *
	 * @param sourceId
	 * @return catalogItemVersions JsonArray
	 */
	protected JsonArray getCatalogItemVersionsPrimitive(final String sourceId) {
		String path = SERVICE_CATALOG_ADMIN_ITEMS + "/" + sourceId + "/versions";
		URI url = getURI(getURIBuilder()
				.setPath(path));
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
					String.class);
			JsonElement root = JsonParser.parseString(response.getBody());
			if (root.isJsonObject()) {
				return root.getAsJsonObject().getAsJsonArray("content");
			}
		} catch (RestClientException e) {
			LOGGER.info("No Versions found for source id '{}'", sourceId);
		}
		return null;
	}

	/**
	 * fetchRequestFormPrimitive.
	 *
	 * @param sourceType
	 * @param sourceId
	 * @param formId
	 * @return customForm VraNgCustomForm
	 */
	protected VraNgCustomForm fetchRequestFormPrimitive(final String sourceType, final String sourceId,
			final String formId) {
		final String formType = "requestForm";
		URI url = getURI(getURIBuilder()
				.setPath(FETCH_REQUEST_FORM)
				.setParameter("formType", formType)
				.setParameter("sourceId", sourceId)
				.setParameter("sourceType", sourceType)
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
	 * @param sourceId   Srouce ID
	 * @return VraNg Custom Form.
	 */
	protected VraNgCustomForm getCustomFormByTypeAndSourcePrimitive(final String sourceType, final String sourceId) {
		final String formType = "requestForm";
		URI url = getURI(getURIBuilder()
				.setPath(SERVICE_CUSTOM_FORM_BY_SOURCE_AND_TYPE)
				.setParameter("formType", formType)
				.setParameter("sourceId", sourceId)
				.setParameter("sourceType", sourceType));

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
				tags.add(tagObj.get("key").getAsString() + ":" + tagObj.get("value").getAsString());
			});
		}

		return tags;
	}

	// =================================================
	// FLAVOR PROFILES OPERATIONS
	// =================================================

	/**
	 * Retrieve a flavor profile by id.
	 *
	 * @param id profile id
	 * @return REST response payload
	 * @throws URISyntaxException exception
	 */
	protected ResponseEntity<String> getFlavorProfileById(final String id) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_FLAVOR_PROFILE + "/" + id)
				.setParameter("apiVersion", this.getVersion()).build();

		return restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Retrieve all flavor mappings grouped by region and group them by region id.
	 *
	 * @return map with key=region, value=list of VraNgFlavorMapping.
	 * @see VraNgFlavorMapping
	 */
	protected Map<String, List<VraNgFlavorMapping>> getAllFlavorMappingsByRegionPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_FLAVORS));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		Map<String, List<VraNgFlavorMapping>> flavorMappingsByRegion = new HashMap<>();

		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
				JsonObject ob = o.getAsJsonObject();
				LOGGER.debug("Obtaining data from getAllFlavorMappingsByRegionPrimitive");
				if (!this.jsonObjectValid(ob)) {
					String regionId = this.getLinkRegionId(ob);
					if (!flavorMappingsByRegion.containsKey(regionId)) {
						flavorMappingsByRegion.put(regionId, new ArrayList<>());
					}

					JsonObject mappingsJson = ob.get("mapping").getAsJsonObject();
					List<VraNgFlavorMapping> mappings = this.getFlavorMappings(mappingsJson);
					flavorMappingsByRegion.get(regionId).addAll(mappings);
				} else {
					LOGGER.warn("Obtaining data from getAllFlavorMappingsByRegionPrimitive returns empty objects.");
				}
			});
		}

		return flavorMappingsByRegion;
	}

	/**
	 * Retrieve all flavor profile IDs grouped by region.
	 *
	 * @return map with key=region, value=list of flavor profile IDs.
	 */
	protected Map<String, List<String>> getAllFlavorProfilesByRegionPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_FLAVOR_PROFILE));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		Map<String, List<String>> flavorProfileIdsByRegion = new HashMap<>();

		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
				JsonObject ob = o.getAsJsonObject();
				LOGGER.debug("Obtaining data from getAllFlavorProfilesByRegionPrimitive");
				if (!this.jsonObjectValid(ob)) {
					String regionId = this.getLinkRegionId(ob);
					String flavorProfileId = ob.get("id").getAsString();

					if (!flavorProfileIdsByRegion.containsKey(regionId)) {
						flavorProfileIdsByRegion.put(regionId, new ArrayList<>());
					}

					flavorProfileIdsByRegion.get(regionId).add(flavorProfileId);
				} else {
					LOGGER.warn("Obtaining data from getAllFlavorProfilesByRegionPrimitive returns empty objects");
				}
			});
		}

		return flavorProfileIdsByRegion;
	}

	/**
	 * Extract flavor mapping definition out of REST response body.
	 *
	 * @param mappingsJson flavor mapping structure
	 * @return Flavor mappings
	 */
	private List<VraNgFlavorMapping> getFlavorMappings(final JsonObject mappingsJson) {
		List<VraNgFlavorMapping> mappings = new ArrayList<>();
		// will return members of your object
		Set<Map.Entry<String, JsonElement>> entries = mappingsJson.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String name = entry.getKey();
			String json = entry.getValue().getAsJsonObject().toString();
			mappings.add(new VraNgFlavorMapping(name, json));
		}

		return mappings;
	}

	/**
	 * Create a new flavor profile.
	 *
	 * @param regionId          region id
	 * @param flavorProfileName profile name
	 * @param flavorMappings    list of flavor mappings
	 * @throws URISyntaxException exception
	 */
	protected void createFlavorPrimitive(final String regionId, final String flavorProfileName,
			final List<VraNgFlavorMapping> flavorMappings) throws URISyntaxException {

		URI url = getURIBuilder().setPath(SERVICE_FLAVOR_PROFILE).setParameter("apiVersion", this.getVersion()).build();

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> mappings = new HashMap<>();
		flavorMappings.forEach(fm -> {
			mappings.put(fm.getName(), JsonParser.parseString(fm.getJson()));
		});

		map.put("name", flavorProfileName);
		map.put("regionId", regionId);
		map.put("flavorMapping", mappings);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
	}

	/**
	 * Update existing flavor profile with flavor mappings.
	 *
	 * @param flavorProfileId profile id
	 * @param flavorMappings  list of flavor mappings
	 * @throws URISyntaxException  exception
	 * @throws UnexpectedException exception
	 */
	protected void updateFlavorPrimitive(final String flavorProfileId, final List<VraNgFlavorMapping> flavorMappings)
			throws URISyntaxException, UnexpectedException {

		URI url = getURIBuilder().setPath(SERVICE_FLAVOR_PROFILE + "/" + flavorProfileId)
				.setParameter("apiVersion", this.getVersion()).build();
		List<VraNgFlavorMapping> flavorMappingsToImport = this.getFlavorMappingsToImport(flavorProfileId,
				flavorMappings);

		if (flavorMappingsToImport.size() == 0) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> flavorMapping = new HashMap<>();
		map.put("flavorMapping", new HashMap<>());
		flavorMappingsToImport.forEach(fm -> {
			flavorMapping.put(fm.getName(), JsonParser.parseString(fm.getJson()));
		});

		map.put("flavorMapping", flavorMapping);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.PATCH, jsonBody);
	}

	/**
	 * Flavor mappings reconciliation.
	 * Added some extra checks if the getFlavorProfileById returns unexpected
	 * response due to NullPointerException (IAC-458)
	 *
	 * @param flavorProfileId profile id
	 * @param flavorMappings  list of flavor mappings
	 * @return list of flavor mappings
	 * @throws JsonSyntaxException exception
	 * @throws URISyntaxException  exception
	 * @throws UnexpectedException exception
	 */
	private List<VraNgFlavorMapping> getFlavorMappingsToImport(final String flavorProfileId,
			final List<VraNgFlavorMapping> flavorMappings) throws JsonSyntaxException,
			URISyntaxException, UnexpectedException {
		ResponseEntity<String> flavorProfileById = this.getFlavorProfileById(flavorProfileId);

		if (flavorProfileById == null || !flavorProfileById.hasBody()) {
			throw new UnexpectedException("Invalid flavor profile response.");
		}

		JsonElement flavorProfileRoot = JsonParser.parseString(flavorProfileById.getBody());
		JsonObject flavorProfileObject = flavorProfileRoot.getAsJsonObject();

		List<VraNgFlavorMapping> flavorMappingsOnServer = (
		// If the profile has no flavor mappings yet, it would not have that property at
		// all, so we're
		// checking if it exists, and if not - defining the variable with a default
		// value
		flavorProfileObject.has("flavorMappings")
				&& flavorProfileObject.get("flavorMappings").getAsJsonObject().has("mapping"))
						? (this.getFlavorMappings(
								flavorProfileObject.get("flavorMappings").getAsJsonObject().get("mapping")
										.getAsJsonObject()))
						: new ArrayList<>();

		List<VraNgFlavorMapping> flavorMappingsToImport = new ArrayList<>(flavorMappings);
		flavorMappingsOnServer.forEach(fm -> {
			if (!flavorMappingsToImport.contains(fm)) {
				flavorMappingsToImport.add(fm);
			}
		});

		return flavorMappingsToImport;
	}

	// =================================================
	// IMAGE PROFILES OPERATIONS
	// =================================================

	/**
	 * Retrieve an image profile by id.
	 *
	 * @param id profile id
	 * @return REST response payload
	 * @throws URISyntaxException exception
	 */
	protected ResponseEntity<String> getImageProfileById(final String id) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_IMAGE_PROFILE + "/" + id)
				.setParameter("apiVersion", this.getVersion()).build();

		return restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
	}

	/**
	 * Retrieve all image mappings for a given region and group them by region id.
	 *
	 * @return map with key=region, value=list of VraNgImageMapping.
	 * @see VraNgImageMapping
	 */
	protected Map<String, List<VraNgImageMapping>> getAllImageMappingsByRegionPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_IMAGES));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		Map<String, List<VraNgImageMapping>> imageMappingsByRegion = new HashMap<>();

		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
				JsonObject ob = o.getAsJsonObject();
				LOGGER.debug("Obtaining data from getAllImageMappingsByRegionPrimitive");
				if (!this.jsonObjectValid(ob)) {
					String regionId = this.getLinkRegionId(ob);
					if (!imageMappingsByRegion.containsKey(regionId)) {
						imageMappingsByRegion.put(regionId, new ArrayList<>());
					}

					JsonObject mappingsJson = ob.get("mapping").getAsJsonObject();
					List<VraNgImageMapping> mappings = this.getImageMappings(mappingsJson);
					imageMappingsByRegion.get(regionId).addAll(mappings);
				} else {
					LOGGER.warn("Skipped empty data from getAllImageMappingsByRegion. Some items are empty");
				}
			});
		}

		return imageMappingsByRegion;
	}

	/**
	 * Checks JSON Object Validity.
	 * 
	 * @param ob JSON Object
	 * @return true if valid else false.
	 */
	protected boolean jsonObjectValid(final JsonObject ob) {
		boolean jsonObjectValid = ob.has("mapping")
				&& ob.has("_links")
				&& ob.get("mapping").getAsJsonObject().keySet().isEmpty()
				&& ob.get("_links").getAsJsonObject().keySet().isEmpty();

		LOGGER.debug(String.format("JSON object is valid: %s", jsonObjectValid));
		return jsonObjectValid;
	}

	/**
	 * Retrieve all image profile IDs grouped by region.
	 *
	 * @return map with key=region, value=list of image profile IDs.
	 */
	protected Map<String, List<String>> getAllImageProfilesByRegionPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_IMAGE_PROFILE));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		Map<String, List<String>> imageProfileIdsByRegion = new HashMap<>();

		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
				JsonObject ob = o.getAsJsonObject();

				LOGGER.debug("Obtaining data from getAllImageProfilesByRegionPrimitive");
				if (!this.jsonObjectValid(ob)) {
					String regionId = this.getLinkRegionId(ob);
					String imageProfileId = ob.get("id").getAsString();

					if (!imageProfileIdsByRegion.containsKey(regionId)) {
						imageProfileIdsByRegion.put(regionId, new ArrayList<>());
					}

					imageProfileIdsByRegion.get(regionId).add(imageProfileId);
				} else {
					LOGGER.warn("Obtaining data from getAllImageProfilesByRegionPrimitive returns empty objects");
				}
			});
		}

		return imageProfileIdsByRegion;
	}

	/**
	 * Extract image mapping definition out of REST response body.
	 *
	 * @param mappingsJson image mapping structure
	 * @return List of VraNG Image Mapping
	 */
	private List<VraNgImageMapping> getImageMappings(final JsonObject mappingsJson) {
		List<VraNgImageMapping> mappings = new ArrayList<>();
		// will return members of your object
		Set<Map.Entry<String, JsonElement>> entries = mappingsJson.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries) {
			String name = entry.getKey();
			String json = entry.getValue().getAsJsonObject().toString();
			mappings.add(new VraNgImageMapping(name, json));
		}

		return mappings;
	}

	/**
	 * Create a new image profile.
	 *
	 * @param regionId         region id
	 * @param imageProfileName profile name
	 * @param imageMappings    list of image mappings
	 * @throws URISyntaxException exception
	 */
	protected void createImageProfilePrimitive(final String regionId, final String imageProfileName,
			final List<VraNgImageMapping> imageMappings) throws URISyntaxException {

		URI url = getURIBuilder().setPath(SERVICE_IMAGE_PROFILE).setParameter("apiVersion", this.getVersion()).build();

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> mappings = new HashMap<>();
		imageMappings.forEach(im -> {
			mappings.put(im.getName(), JsonParser.parseString(im.getJson()));
		});

		map.put("name", imageProfileName);
		map.put("regionId", regionId);
		map.put("imageMapping", mappings);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
	}

	/**
	 * Update existing image profile with image mappings.
	 *
	 * @param imageProfileId profile id
	 * @param imageMappings  list of image mappings
	 * @throws URISyntaxException  exception
	 * @throws UnexpectedException exception
	 */
	protected void updateImageProfilePrimitive(final String imageProfileId, final List<VraNgImageMapping> imageMappings)
			throws URISyntaxException, UnexpectedException {

		URI url = getURIBuilder().setPath(SERVICE_IMAGE_PROFILE + "/" + imageProfileId)
				.setParameter("apiVersion", this.getVersion()).build();
		List<VraNgImageMapping> imageMappingsToImport = this.getImageMappingsToImport(imageProfileId, imageMappings);

		if (imageMappingsToImport.size() == 0) {
			return;
		}

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> imageMapping = new HashMap<>();
		map.put("imageMapping", new HashMap<>());
		imageMappingsToImport.forEach(im -> {
			imageMapping.put(im.getName(), JsonParser.parseString(im.getJson()));
		});

		map.put("imageMapping", imageMapping);

		String jsonBody = this.getJsonString(map);
		this.postJsonPrimitive(url, HttpMethod.PATCH, jsonBody);
	}

	/**
	 * Image mappings reconcilliation.
	 * If the response is empty or contains no mappings, an empty List is returned.
	 *
	 * @param imageProfileId profile id
	 * @param imageMappings  list of image mappings
	 * @return list of image mappings
	 * @throws JsonSyntaxException exception
	 * @throws URISyntaxException  exception
	 * @throws UnexpectedException exception
	 */
	private List<VraNgImageMapping> getImageMappingsToImport(final String imageProfileId,
			final List<VraNgImageMapping> imageMappings)
			throws JsonSyntaxException, URISyntaxException, UnexpectedException {
		ResponseEntity<String> response = this.getImageProfileById(imageProfileId);

		if (response == null || !response.hasBody()) {
			throw new UnexpectedException("Invalid image profile response.");
		}

		JsonElement imageProfileRoot = JsonParser.parseString(response.getBody());
		JsonObject imageProfileObject = imageProfileRoot.getAsJsonObject();

		List<VraNgImageMapping> imageMappingsOnServer = (
		// If the profile has no image mappings yet, it would not have that property at
		// all, so we're
		// checking if it exists, and if not - defining the variable with a default
		// value
		imageProfileObject.has("imageMappings")
				&& imageProfileObject.get("imageMappings").getAsJsonObject().has("mapping"))
						? (this.getImageMappings(
								imageProfileObject.get("imageMappings").getAsJsonObject().get("mapping")
										.getAsJsonObject()))
						: new ArrayList<>();

		List<VraNgImageMapping> imageMappingsToImport = new ArrayList<>(imageMappings);
		imageMappingsOnServer.forEach(im -> {
			if (!imageMappingsToImport.contains(im)) {
				imageMappingsToImport.add(im);
			}
		});

		return imageMappingsToImport;
	}

	// =================================================
	// STORAGE PROFILES OPERATIONS
	// =================================================

	/**
	 * Retrieve all storage profiles for a given region and group them by region id.
	 *
	 * @return map with key=region, value=list of VraNgStorageProfile.
	 * @see VraNgStorageProfile
	 */
	protected Map<String, List<VraNgStorageProfile>> getAllStorageProfilesByRegionPrimitive() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_STORAGE_PROFILE));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());

		Map<String, List<VraNgStorageProfile>> storageProfilesByRegion = new HashMap<>();

		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
				JsonObject ob = o.getAsJsonObject();
				LOGGER.debug("Obtaining data from getAllStorageProfilesByRegionPrimitive");
				if (!this.jsonObjectValid(ob)) {
					String regionId = this.getLinkRegionId(ob);
					if (!storageProfilesByRegion.containsKey(regionId)) {
						storageProfilesByRegion.put(regionId, new ArrayList<>());
					}

					if (ob.has("name")) {
						String name = ob.get("name").getAsString();
						String json = ob.toString();
						storageProfilesByRegion.get(regionId).add(new VraNgStorageProfile(name, json));
					} else {
						LOGGER.warn("Storage Profile has been skipped because don't contains a name definition");
						if (ob.has("id")) {
							LOGGER.warn(String.format("Id of the storage profile: %s", ob.get("id").getAsString()));
						} else {
							LOGGER.warn("Storage Profile don't have a valid id. Unable to track this object.");
						}
					}
				} else {
					LOGGER.info("Obtaining data from getAllStorageProfilesByRegionPrimitive returns empty objects");
				}
			});
		}

		return storageProfilesByRegion;
	}

	/**
	 * Update existing storage profile.
	 *
	 * @param profileId profile id
	 * @param profile   storage profile
	 * @throws URISyntaxException exception
	 */
	protected void updateStorageProfilePrimitive(final String profileId, final VraNgStorageProfile profile)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_STORAGE_PROFILE + "/" + profileId));
		ResponseEntity<String> response = this.putJsonPrimitive(url, profile.getJson());
	}

	/**
	 * Create a new storage profile returning its id.
	 *
	 * @param profile storage profile
	 * @throws URISyntaxException exception exception
	 * @return profile
	 *
	 */
	protected String createStorageProfilePrimitive(final VraNgStorageProfile profile) throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_STORAGE_PROFILE));
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, profile.getJson());
		JsonElement root = JsonParser.parseString(response.getBody());
		if (root.isJsonObject()) {
			return root.getAsJsonObject().get("id").getAsString();
		}
		return null;
	}

	/**
	 * Get specific storage profile.
	 *
	 * @param targetPool target pool of storage profiles
	 * @param profileId  profile id
	 * @return storage profile
	 */
	protected VraNgStorageProfile getSpecificProfilePrimitive(final String targetPool, final String profileId) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_IAAS_BASE + "/" + targetPool + "/" + profileId));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		JsonElement root = JsonParser.parseString(response.getBody());
		JsonObject ob = root.getAsJsonObject();

		String name = ob.get("name").getAsString();
		String json = ob.toString();
		return new VraNgStorageProfile(name, json);
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
	 * Returns a list of all property groups
	 * This will keep on making requests until all the groups are retrieved.
	 * If the nameFilter is null, all will be accepted, otherwise only property
	 * group names that contain the filter will be accepted.
	 *
	 * Note: hasMore is retrieved from the "last" property. JsonElement supports a
	 * getAsBoolean function however
	 * it does not cast the strings "true" or "false" to boolean.
	 *
	 * Note: When doing propertyGroupObject.get( "name" ).toString() the name is
	 * returned with surrounded '"',
	 * so it is trimmed
	 *
	 * @return list of property groups
	 * @param nameFilter filter
	 */
	protected List<VraNgPropertyGroup> getAllPropertyGroupsPrimitive(final String nameFilter) {
		boolean hasMore = true;
		int elementsToSkip = 0;
		List<VraNgPropertyGroup> propertyGroups = new ArrayList<>();

		while (hasMore) {
			URI url = getURI(
					getURIBuilder()
							.setPath(SERVICE_GET_PROPERTY_GROUPS)
							.addParameter("$skip", String.valueOf(elementsToSkip)));
			ResponseEntity<String> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					getDefaultHttpEntity(),
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
					propertyGroups.add(
							new VraNgPropertyGroup(
									propertyGroupName,
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
	 * Update specific storage profile.
	 *
	 * @param patchTarget patch target
	 * @param profileId   profile id
	 * @param profile     storage profile
	 * @throws URISyntaxException exception
	 */
	protected void updateSpecificProfilePrimitive(final String patchTarget, final String profileId,
			final VraNgStorageProfile profile)
			throws URISyntaxException {
		URI url = getURI(getURIBuilder().setPath(SERVICE_IAAS_BASE + "/" + patchTarget + "/" + profileId));
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.PATCH, profile.getJson());
	}

	/**
	 * Retrieve fabric entity name. This method calls a requested URL
	 * and returns the name property of the response.
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
	 * Retreive Fabric Entity ID.
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
	 * Retreive Organization By Name.
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
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
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
	 * Retreieve Organization By ID.
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
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
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
	 * Retreive Vra Workflow Integrations.
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
	 * Retreive Vra Workflow Integrations.
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
	 * Retreive all Custom Resource.
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
	 * remove them due to a vRA8 limitations.
	 * When creating a CR with additionalActions, we create the CR first then we
	 * apply the same json but with additionalActions
	 * added.
	 *
	 * @param customResourceJson String containing the raw json content of the
	 *                           custom resource
	 *
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
				throw new RuntimeException(
						String.format("Unable to import additionalActions for %s",
								originalCr.get("displayName").getAsString()));
			}
		}
	}

	/**
	 * Delete Custom Resource.
	 * 
	 * @param customResourceId Resource Action JSON
	 */
	protected void deleteCustomResourcePrimitive(final String customResourceId) throws URISyntaxException {
		String deleteURL = String.format(SERVICE_CUSTOM_RESOURCES + "/%s", customResourceId);
		URI url = getURIBuilder().setPath(deleteURL).build();
		restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
	}

	// =================================================
	// RESOURCE ACTIONS
	// =================================================

	/**
	 * Retreive all Resource Actions.
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
	 */
	protected void deleteResourceActionPrimitive(final String resourceActionId) {
		String deleteURL = String.format(SERVICE_RESOURCE_ACTIONS + "/%s", resourceActionId);
		URI url = getURI(getURIBuilder().setPath(deleteURL));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
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
				action.id = ob.get("id").getAsString();
				action.name = ob.get("name").getAsString();
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
	 */
	public String createAbxActionPrimitive(final AbxAction action) throws URISyntaxException, IOException {
		URI url = getURIBuilder().setPath(SERVICE_ABX_ACTIONS).build();

		Map<String, Object> map = createAbxActionMap(action);

		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.POST, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxAction.class).id;
	}

	/**
	 * Update Abx Action.
	 *
	 * @param id     ID
	 * @param action Abx Action
	 * @return Abx Action ID
	 */
	public String updateAbxActionPrimitive(final String id, final AbxAction action)
			throws URISyntaxException, IOException {
		URI url = getURIBuilder().setPath(SERVICE_ABX_ACTIONS + "/" + id).build();

		Map<String, Object> map = createAbxActionMap(action);

		String jsonBody = this.getJsonString(map);
		ResponseEntity<String> response = this.postJsonPrimitive(url, HttpMethod.PUT, jsonBody);
		return new Gson().fromJson(response.getBody(), AbxAction.class).id;
	}

	/**
	 * Retrieve Abx Last updated Version.
	 *
	 * @param actionId actionId
	 * @return Abx Action Version
	 */
	public AbxActionVersion getAbxLastUpdatedVersionPrimitive(final String actionId) {
		URI url = getURI(
				getURIBuilder()
						.setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/versions")
						.addParameter("projectId", getProjectId())
						.addParameter("orderBy", "createdMillis DESC"));

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
		URI url = getURI(
				getURIBuilder()
						.setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/versions")
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
		URI url = getURI(
				getURIBuilder()
						.setPath(SERVICE_ABX_ACTIONS + "/" + actionId + "/release")
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
	 * Extract region id from a link fragment part of REST response body.
	 *
	 * @param ob fragment
	 * @return region id
	 */
	private String getLinkRegionId(final JsonObject ob) {

		LOGGER.debug("Extracting data: {}", ob);

		String regionHref = ob.get("_links").getAsJsonObject()
				.get("region").getAsJsonObject()
				.get("href").getAsString();

		return regionHref.substring(regionHref.lastIndexOf('/') + 1);
	}

	/**
	 * Extract cloud account id from a link fragment part of REST response body.
	 *
	 * @param ob fragment
	 * @return cloud account id
	 */
	private String getLinkCloudAccountId(final JsonObject ob) {
		String regionHref = ob.get("_links").getAsJsonObject()
				.get("cloud-account").getAsJsonObject()
				.get("href").getAsString();

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
	 */
	protected Map<String, Object> createAbxActionMap(final AbxAction action) throws IOException {
		Map<String, Object> map = new LinkedHashMap<>();

		String[] providers = {"aws", "azure", "on-prem"};

		map.put("actionType", "SCRIPT");
		map.put("name", action.getName());
		map.put("description", action.description);
		map.put("projectId", getProjectId());
		map.put("runtime", action.platform.runtime);
		map.put("entrypoint", action.platform.entrypoint);
		map.put("inputs", action.abx.inputs);
		map.put("compressedContent", action.getBundleAsB64());
		map.put("shared", action.abx.shared);

		if (action.platform.timeoutSec != null && action.platform.timeoutSec > 0) {
			map.put("timeoutSeconds", action.platform.timeoutSec);
		}

		if (action.platform.memoryLimitMb != null && action.platform.memoryLimitMb > 0) {
			map.put("memoryInMB", action.platform.memoryLimitMb);
		}

		if (action.platform.provider != null && Arrays.stream(providers).anyMatch(action.platform.provider::equals)) {
			map.put("provider", action.platform.provider);
		} else if (action.platform.provider != null) {
			throw new RuntimeException(
					"Faas provider name is not correct. Possible values are: " + String.join(",", providers));
		}

		return map;
	}

	private String getJsonString(final Map<String, Object> entity) {
		Gson gson = new GsonBuilder().setLenient().serializeNulls().create();

		return gson.toJson(entity);
	}

	private ResponseEntity<String> postJsonPrimitive(final URI url, final HttpMethod method, final String jsonBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

		return restTemplate.exchange(url, method, entity, String.class);
	}

	private ResponseEntity<String> putJsonPrimitive(final URI url, final String jsonBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

		return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
	}

	private List<VraNgContentSourceBase> getContentSources() {
		URI url = getURI(getURIBuilder().setPath(SERVICE_CONTENT_SOURCE));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);

		List<VraNgContentSourceBase> retVal = new ArrayList<>();
		JsonElement root = JsonParser.parseString(response.getBody());
		if (root.isJsonObject()) {
			root.getAsJsonObject().getAsJsonArray("content").forEach(item -> {
				JsonObject contentSource = item.getAsJsonObject();
				if (contentSource != null) {
					String id = contentSource.get("id").getAsString();
					if (!StringUtils.isEmpty(id)) {
						VraNgContentSourceType type = VraNgContentSourceType
								.fromString(contentSource.get("typeId").getAsString());
						retVal.add(new Gson().fromJson(contentSource, type.getTypeClass()));
					}
				}
			});
		}

		return retVal;
	}

	/**
	 * Retrieve Content Source with name.
	 * 
	 * @param contentSourceName Content Source name.
	 * @return Retreived VraNg Content Source
	 */
	public VraNgContentSourceBase getContentSourceByName(final String contentSourceName) {
		List<VraNgContentSourceBase> contentSources = this.getContentSources();

		return contentSources.stream()
				.filter(contentSource -> contentSource.getName().equalsIgnoreCase(contentSourceName))
				.findFirst().orElse(null);
	}

	/**
	 * Delete Content Source.
	 * 
	 * @param contentSourceId Content Source ID to delete.
	 */
	public void deleteContentSource(final String contentSourceId) {
		if (StringUtils.isEmpty(contentSourceId)) {
			return;
		}
		String deleteURL = String.format(SERVICE_CONTENT_SOURCE + "/%s", contentSourceId);
		URI url = getURI(getURIBuilder().setPath(deleteURL));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
	}

	/**
	 * Checks VRA Version.
	 * 
	 * @return true if version above 81 else false.
	 */
	public boolean isVraAbove81() {
		return productVersion.getMajorVersion() != null && productVersion.getMajorVersion() >= VRA_VERSION_MAJOR
				&& this.productVersion
						.getMinorVersion() != null
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
	 * @param target
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

	/**
	 * Retrieve all content sharing policy Ids.
	 *
	 * @return list of sharing policy Ids that are available.
	 * 
	 */
	protected List<String> getAllContentSharingPolicyIdsPrimitive() {
		List<String> policyIds = new ArrayList<>();
		List<JsonObject> results = this.getPagedContent(SERVICE_POLICIES, new HashMap<>());
		LOGGER.debug("Policy Ids found on server: {}", results.size());
		results.forEach(o -> {
			JsonObject ob = o.getAsJsonObject();
			String typeId = ob.get("typeId").getAsString();
			if (typeId.equals(CONTENT_SHARING_POLICY_TYPE)) {
				String policyId = ob.get("id").getAsString();
				policyIds.add(policyId);
			}
		});
		return policyIds;
	}

	/**
	 * Retrieve content sharing policy Id based on name.
	 *
	 * @param name name of the policy
	 * @return content sharing policy Id.
	 * 
	 */
	protected String getContentSharingPolicyIdByName(final String name) {
		String policyId = "";
		List<JsonObject> results = this.getPagedContent(SERVICE_POLICIES, new HashMap<>());
		LOGGER.debug("Policies found on server: {}", results.size());
		for (JsonObject o : results) {
			JsonObject ob = o.getAsJsonObject();
			String typeId = ob.get("typeId").getAsString();
			String policyName = ob.get("name").getAsString();
			if (typeId.equals(CONTENT_SHARING_POLICY_TYPE) && policyName.equals(name)) {
				policyId = ob.get("id").getAsString();
				return policyId;
			}
		}
		return policyId;
	}

	/**
	 * Retrieve content sharing policy based on Id.
	 * 
	 * @param policyId
	 * @return Created VraNg Content Sharing Policy
	 */
	protected VraNgContentSharingPolicy getContentSharingPolicyPrimitive(final String policyId) {
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy();
		URI url = getURI(getURIBuilder().setPath(SERVICE_POLICIES + "/" + policyId));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(),
				String.class);
		JsonElement root = JsonParser.parseString(response.getBody());
		if (!root.isJsonObject()) {
			return null;
		}
		JsonObject result = root.getAsJsonObject();
		String name = result.get("name").getAsString();
		String description = result.has("description") ? result.get("description").getAsString() : "";
		String typeId = result.get("typeId").getAsString();
		String enforcementType = result.get("enforcementType").getAsString();
		VraNgDefinition definition = new Gson().fromJson(result.get("definition").getAsJsonObject(),
				VraNgDefinition.class);
		definition.entitledUsers.forEach(user -> user.items.forEach(item -> {
			VraNgContentSourceBase contentSource = this.getContentSourcePrimitive(item.id);
			item.name = (contentSource != null) ? contentSource.getName() : "";
		}));
		csPolicy.setDefinition(definition);
		csPolicy.setName(name);
		csPolicy.setEnforcementType(enforcementType);
		csPolicy.setDescription(description);
		csPolicy.setTypeId(typeId);
		return csPolicy;
	}

	/**
	 * Creates Content Sharing Policy.
	 * 
	 * @param csPolicy policy data to create
	 */
	public void createContentSharingPolicyPrimitive(final VraNgContentSharingPolicy csPolicy)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_POLICIES).build();
		String jsonBody = new Gson().toJson(csPolicy);
		JsonObject jsonObject = new Gson().fromJson(jsonBody, JsonObject.class);
		String organizationId = VraNgOrganizationUtil.getOrganization(this, configuration).getId();
		jsonObject.addProperty("orgId", organizationId);
		jsonObject.addProperty("projectId", getProjectId());
		handleItemsProperty(jsonObject);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonObject.toString());
	}

	/**
	 * Update Content Sharing Policy.
	 * 
	 * @param csPolicy policy data to update
	 */
	public void updateContentSharingPolicyPrimitive(final VraNgContentSharingPolicy csPolicy)
			throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_POLICIES).build();
		String jsonBody = new Gson().toJson(csPolicy);
		JsonObject jsonObject = new Gson().fromJson(jsonBody, JsonObject.class);
		String organizationId = VraNgOrganizationUtil.getOrganization(this, configuration).getId();
		jsonObject.addProperty("id", getContentSharingPolicyIdByName(csPolicy.getName()));
		jsonObject.addProperty("orgId", organizationId);
		jsonObject.addProperty("projectId", getProjectId());
		handleItemsProperty(jsonObject);
		this.postJsonPrimitive(url, HttpMethod.POST, jsonObject.toString());
	}

	/**
	 * handleItemsProperty.
	 * 
	 * @param csPolicyJsonObject
	 */
	public void handleItemsProperty(final JsonObject csPolicyJsonObject) {
		JsonObject definition = csPolicyJsonObject.getAsJsonObject("definition");
		JsonArray euArr = definition.getAsJsonArray("entitledUsers");
		for (JsonElement eu : euArr) {
			JsonObject entitledUserObj = eu.getAsJsonObject();
			JsonArray itemsArr = entitledUserObj.getAsJsonArray("items");
			for (JsonElement item : itemsArr) {
				JsonObject itemObj = item.getAsJsonObject();
				String contentSourceName = itemObj.get("name").getAsString();
				List<VraNgContentSourceBase> contentSources = this.getContentSources();
				VraNgContentSourceBase contentSource = contentSources.stream()
						.filter(cs -> cs.getName().equals(contentSourceName))
						.findFirst()
						.orElse(null);
				if (contentSource == null) {
					throw new RuntimeException(
							String.format("Content Source with name  '%s' could not be found on target system",
									contentSourceName));
				}
				itemObj.addProperty("id", contentSource.getId());
				itemObj.remove("name");
			}
		}
		definition.add("entitledUsers", euArr);
		csPolicyJsonObject.add("definition", definition);
	}
}
