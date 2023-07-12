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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageContent;
import com.vmware.pscoe.iac.artifact.rest.helpers.JsonHelper;
import com.vmware.pscoe.iac.artifact.rest.model.VraPackageDTO;

import org.apache.commons.io.IOUtils;
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
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class RestClientVraPrimitive extends RestClient {

	/**
	 * Initialize logger.
	 * 
	 * param logger
	 */
	private final Logger logger = LoggerFactory.getLogger(RestClientVraPrimitive.class);

	/**
	 * param SERVICE_CONTENT.
	 */
	private final String SERVICE_CONTENT = "/content-management-service/api/contents";
	/**
	 * param SERVICE_PACKAGE.
	 */
	private final String SERVICE_PACKAGE = "/content-management-service/api/packages";
	/**
	 * param SERVICE_CONTENT.
	 */
	private final String BLUEPRINT_PACKAGE = "/composition-service/api/blueprints";
	/**
	 * param SERVICE_CONTENT.
	 */
	private final String SERVICE_PROPERETY_DEFINITION = "/properties-service/api/propertydefinitions";
	/**
	 * param SERVICE_PROPERTY_GROUP.
	 */
    private final String SERVICE_PROPERTY_GROUP = "/properties-service/api/propertygroups";
	/**
	 * param SERVICE_XAAS_OPERATION.
	 */
    private final String SERVICE_XAAS_OPERATION = "/advanced-designer-service/api/resourceOperations";
	/**
	 * param SERVICE_XAAS_BLUEPRINT.
	 */
    private final String SERVICE_XAAS_BLUEPRINT = "/advanced-designer-service/api/tenants/%s/blueprints";
	/**
	 * param SERVICE_XAAS_TYPE.
	 */
    private final String SERVICE_XAAS_TYPE = "/advanced-designer-service/api/tenants/%s/types";
	/**
	 * param SERVICE_CONTENT.
	 */
	private final String SERVICE_SOFTWARE = "/software-service/api/softwarecomponenttypes";
	/**
	 * param SERVICE_WORKFLOW_SUBSCRIPTION.
	 */
	private final String SERVICE_WORKFLOW_SUBSCRIPTION = "/advanced-designer-service/api/tenants/%s/event-broker/subscriptions";
	/**
	 * param CATALOG_ITEM.
	 */
	private final String CATALOG_ITEM = "/catalog-service/api/catalogItems";
	/**
	 * param CATALOG_SERVICE.
	 */
	private final String CATALOG_SERVICE = "/catalog-service/api/services";
	/**
	 * param CATALOG_ICON.
	 */
	private final String CATALOG_ICON = "/catalog-service/api/icons";

	/**
	 * param configuration.
	 */
	private ConfigurationVra configuration;

	/**
	 * param restTemplate.
	 */
	private RestTemplate restTemplate;

	protected RestClientVraPrimitive(final ConfigurationVra configuration, final RestTemplate restTemplate) {
		this.configuration = configuration;
		this.restTemplate = restTemplate;
	}
    
	/** 
	 * @return Configuration
	 */
	@Override
    protected Configuration getConfiguration() {
        return this.configuration;
    }
    
    
	/** 
	 * @return String
	 */
	@Override
    public String getVersion() {
        URI url = getURI(getURIBuilder().setPath("identity/api/about"));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        return JsonPath.parse(response.getBody()).read("$.productVersion");
    }

	
	/** 
	 * @param url
	 * @param filter
	 * @return List<Map<String, String>>
	 */
	private List<Map<String, String>> getContentTypePrimitive(final String url, final StringJoiner filter) {

		URIBuilder uriBuilder = getURIBuilder().setPath(url).setParameter("page", "1").setParameter("limit", "100");
		if (filter.length() != 0) {
			uriBuilder.setParameter("$filter", filter.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity,
				String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, String>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result;
	}

	
	/** 
	 * @param urlString
	 * @param jsonBody
	 * @return ResponseEntity<String>
	 * @throws URISyntaxException
	 */
	private ResponseEntity<String> postJsonPrimitive(final String urlString, final String jsonBody) throws URISyntaxException {
		URI url = getURIBuilder().setPath(urlString).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);
		return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	}

	
	/** 
	 * @param urlString
	 * @param jsonBody
	 * @return ResponseEntity<String>
	 * @throws URISyntaxException
	 */
	private ResponseEntity<String> putJsonPrimitive(final String urlString, final String jsonBody) throws URISyntaxException {
		URI url = getURIBuilder().setPath(urlString).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);
		return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
	}

	
	/** 
	 * @param contentTypeId content type id
	 * @param name name
	 * @return content type primitive collection
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected List<Map<String, String>> getContentPrimitive(final String contentTypeId, final String name)
			throws URISyntaxException {
		StringJoiner filter = new StringJoiner(" and ");

		if (contentTypeId != null) {
			filter.add("contentTypeId eq '" + contentTypeId + "'");
		}
		if (name != null) {
			filter.add("name eq '" + name + "'");
		}

		return getContentTypePrimitive(SERVICE_CONTENT, filter);
	}

	
	/** 
	 * @return content type primitive collection
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected List<Map<String, String>> getPackagesPrimitive() throws URISyntaxException {
		return getContentTypePrimitive(SERVICE_PACKAGE, new StringJoiner(""));
	}

	
	/** 
	 * @param pkgId package id
	 * @return content type primitive collection
	 */
	protected List<Map<String, String>> getPackageContentsPrimitive(final String pkgId) {
		String url = SERVICE_PACKAGE + "/" + pkgId + "/contents";
		return getContentTypePrimitive(url, new StringJoiner(""));
	}

	
	/** 
	 * @return content collection
	 */
	protected List<Map<String, Object>> getWorkflowSubscriptionsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()))
			.setParameter("page", "1")
			.setParameter("limit", "100");
		URI url = getURI(uriBuilder);		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result;
	}

	
	/** 
	 * @param subscriptionName subscription name
	 * @return subscriptions
	 */
	protected Map<String, Object> getWorkflowSubscriptionByNamePrimitive(final String subscriptionName) {
		URIBuilder uriBuilder = getURIBuilder()
		.setPath(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()))
			.setParameter("$filter", "name eq '" + subscriptionName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> subscriptions = context.read("$..content.*");
		
		if (subscriptions.size() > 0) {
			return subscriptions.get(0);
		}
		
		logger.debug(String.format("Workflow Subscription with name '%s' not found.", subscriptionName));
		return null;
	}

	
	/** 
	 * @param subscriptionName subscription name
	 * @param jsonBody json body - subscription context
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void importSubscriptionPrimitive(final String subscriptionName, String jsonBody) throws URISyntaxException {
		logger.info(String.format("Updating Workflow Subscription with name '%s'.", subscriptionName));
		// If subscription with such name already exists use it's ID to update it. Otherwise just remove the ID to create a new subscription
		Map<String, Object> subscription = getWorkflowSubscriptionByNamePrimitive(subscriptionName);

		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> subscriptionJson = context.read("$");

		// Update the subscription tenant ID with the target tenant ID
		subscriptionJson.put("tenantId", configuration.getTenant());

		if (subscription != null) {
			logger.debug(String.format("Workflow Subscription with name '%s' already exist. Updating existing one.", subscriptionName));
			subscriptionJson.put("id", (String) subscription.get("id"));
		} else {
			logger.debug(String.format("Workflow Subscription with name '%s' not found. Creating a new one.", subscriptionName));
		}

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		jsonBody = gson.toJson(subscriptionJson);
		postJsonPrimitive(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()), jsonBody);
	}

	
	/** 
	 * @param bpId blueprint id
	 * @return String
	 */
	protected String getBlueprintCustomFormPrimitive(final String bpId) {
		URI url = getURI(getURIBuilder().setPath(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			// CustomForms API returns the form as Json primitive string having value the stringified json form
			return new Gson().fromJson(response.getBody(), JsonPrimitive.class).getAsString();
		} catch (RuntimeException e) {
			if (e.getMessage().contains("404")) {
			    logger.info("Custom form for Blueprint '{}' not found.", bpId);
				return null;
			} else {
				throw new RuntimeException(e);
			}
		}

	}

	
	/** 
	 * @param bpId blueprint id
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void activateBlueprintCustomFormPrimitive(final String bpId) throws URISyntaxException {
		URIBuilder uriBuilder = getURIBuilder().setPath(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform/enable");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, String.class);
	}

	
	/** 
	 * @param bpId blueprint id
	 * @param jsonBody post body payload
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void setBlueprintCustomFormPrimitive(final String bpId, final String jsonBody) throws URISyntaxException {
		postJsonPrimitive(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform", jsonBody);
	}

	
	/** 
	 * @param catalogItemName catalog item name
	 * @return CatalogItem By Name Primitive
	 */
	protected Map<String, Object> getCatalogItemByNamePrimitive(final String catalogItemName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(CATALOG_ITEM)
			.setParameter("$filter", "name eq '" + catalogItemName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> catalogItems = context.read("$..content.*");
		
		if (catalogItems.size() > 0) {
			return catalogItems.get(0);
		}
		
		logger.debug(String.format("Catalog Item with name '%s' not found.", catalogItemName));
		return null;
	}

	
	/** 
	 * @param catalogItem catalog item by id
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void setCatalogItemPrimitive(final Map<String, Object> catalogItem) throws URISyntaxException {
		String catalogItemJson = JsonHelper.toJson(catalogItem);
		String catalogItemId = (String) catalogItem.get("id");
		putJsonPrimitive(CATALOG_ITEM + "/" + catalogItemId, catalogItemJson);
	}

	/**
	 * get Catalog Service By Name Primitive.
	 * @param serviceName service name
	 * @return services
	 */
	public Map<String, Object> getCatalogServiceByNamePrimitive(final String serviceName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(CATALOG_SERVICE)
			.setParameter("$filter", "name eq '" + serviceName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> services = context.read("$..content.*");
		
		if (services.size() > 0) {
			return services.get(0);
		}
		
		logger.debug(String.format("Catalog service with name '%s' not found.", serviceName));
		return null;
	}

	
	/** 
	 * @param iconId icon id
	 * @return Icon Primitive by id
	 */
	protected Map<String, Object> getIconPrimitive(final String iconId) {
		URIBuilder uriBuilder = getURIBuilder().setPath(CATALOG_ICON + "/" + iconId);
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		Map<String, Object> icon = context.read("$");
		
		if (icon == null) {
			logger.debug(String.format("Icon with ID '%s' not found.", iconId));
			return null;
		}

		return icon;
	}

	
	/** 
	 * @param icon icon
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void setIconPrimitive(final Map<String, Object> icon) throws URISyntaxException {
		String iconJson = JsonHelper.toJson(icon);
		postJsonPrimitive(CATALOG_ICON, iconJson);
	}

	
	/** 
	 * @return Global Property Definitions colelction by tenant id
	 */
	protected List<Map<String, Object>> getGlobalPropertyDefinitionsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERETY_DEFINITION).setParameter("page", "1").setParameter("limit", "100");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result.stream().filter(pg -> pg.get("tenantId") == null).collect(Collectors.toList());
	}

	
	/** 
	 * @param propertyDefinitionName property definition name
	 * @return Global Property Definition By Name
	 */
	protected Map<String, Object> getGlobalPropertyDefinitionByNamePrimitive(final String propertyDefinitionName) {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERETY_DEFINITION + "/" + propertyDefinitionName);
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw e;
			}
		}

		if (response != null) {
			DocumentContext context = JsonPath.parse(response.getBody());
			Map<String, Object> propertyDefinition = context.read("$");
			if (propertyDefinition.get("tenantId") == null) {
				return propertyDefinition;
			}	
		}

		logger.debug(String.format("Global Property Definition with name '%s' not found.", propertyDefinitionName));
		return null;
	}

	
	/** 
	 * @param propertyDefinitionName property definitio name
	 * @param jsonBody json body payload
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void importGlobalPropertyDefinitionPrimitive(final String propertyDefinitionName, String jsonBody) throws URISyntaxException {
		logger.debug(String.format("Updating Global Property Definition with name '%s'.", propertyDefinitionName));

		Map<String, Object> propertyDefinition = getGlobalPropertyDefinitionByNamePrimitive(propertyDefinitionName);
		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> propertyDefinitionJson = context.read("$");
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		jsonBody = gson.toJson(propertyDefinitionJson);

		if (propertyDefinition != null) {
			logger.debug(String.format("Global Property Definition with name '%s' already exist. Updating existing one.", propertyDefinitionName));
			putJsonPrimitive(SERVICE_PROPERETY_DEFINITION + "/" + propertyDefinitionName, jsonBody);
		} else {
			logger.debug(String.format("Global Property Definition with name '%s' not found. Creating a new one.", propertyDefinitionName));
			postJsonPrimitive(SERVICE_PROPERETY_DEFINITION, jsonBody);
		}
	}

	
	/** 
	 * @return Global Property Groups by tenant id
	 */
	protected List<Map<String, Object>> getGlobalPropertyGroupsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERTY_GROUP).setParameter("page", "1").setParameter("limit", "100");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result.stream().filter(pg -> pg.get("tenantId") == null).collect(Collectors.toList());
	}

	
	/** 
	 * @param propertyGroupName property group name
	 * @return Global Property Group By Name
	 */
	protected Map<String, Object> getGlobalPropertyGroupByNamePrimitive(final String propertyGroupName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(SERVICE_PROPERTY_GROUP)
			.setParameter("$filter", "label eq '" + propertyGroupName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> propertyGroups = context.read("$..content.*");
		
		if (propertyGroups.size() > 0) {
			return propertyGroups.get(0);
		}
		
		logger.debug(String.format("Global Property Group with name '%s' not found.", propertyGroupName));
		return null;
	}

	
	/** 
	 * @param propertyGroupName property group name
	 * @param jsonBody json body payload
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void importGlobalPropertyGroupPrimitive(final String propertyGroupName, String jsonBody) throws URISyntaxException {
		logger.debug(String.format("Updating Global Property Group with name '%s'.", propertyGroupName));

		Map<String, Object> propertyGroup = getGlobalPropertyGroupByNamePrimitive(propertyGroupName);
		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> propertyGroupJson = context.read("$");
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		
		if (propertyGroup != null) {
			logger.debug(String.format("Global Property Group with name '%s' already exist. Updating existing one.", propertyGroupName));
			String propertyGroupId = (String) propertyGroup.get("id");
			propertyGroupJson.put("id", propertyGroupId);
			jsonBody = gson.toJson(propertyGroupJson);
			putJsonPrimitive(SERVICE_PROPERTY_GROUP + "/" + propertyGroupId, jsonBody);			
		} else {
			logger.debug(String.format("Global Property Group with name '%s' not found. Creating a new one.", propertyGroupName));
			jsonBody = gson.toJson(propertyGroupJson);
			postJsonPrimitive(SERVICE_PROPERTY_GROUP, jsonBody);
		}
	}

	
	/** 
	 * @param vraPackage vra package
	 * @param contentIds content ids list
	 * @return String
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected String createPackagePrimitive(final Package vraPackage, final List<String> contentIds) throws URISyntaxException {
		String requestJson = new Gson().toJson(new VraPackageDTO(vraPackage.getFQName(), contentIds));
		ResponseEntity<String> response;
		try {
			response = postJsonPrimitive(SERVICE_PACKAGE, requestJson);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("400")) {
				throw new RuntimeException("Probably package with the same name already exist.", e);
			} else {
				throw new RuntimeException(e);
			}

		}
		List<String> locationHeader = response.getHeaders().get("Location");

		String locationUrl = locationHeader.get(0);
		String packageId = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
		logger.debug("Create Package[" + vraPackage.getFQName() + "] ID[" + packageId + "]");
		return packageId;
	}

	
	/** 
	 * @param vraPackage vra package
	 */
	protected void deletePackagePrimitive(final Package vraPackage) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_PACKAGE + "/" + vraPackage.getId()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
	}

	
	/** 
	 * @param vraPackage vra package
	 * @param validate is validate package
	 * @throws NumberFormatException throws number format exception incase value is not number
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected void exportPackagePrimitive(final Package vraPackage, final boolean validate)
			throws NumberFormatException, URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_PACKAGE + "/" + vraPackage.getId() + (validate ? "/validate" : ""))
				.build();

		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(final ClientHttpRequest request) throws IOException {
				request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
			}
		};

		ResponseExtractor<Void> responseExtractor = new ResponseExtractor<Void>() {
			public Void extractData(final ClientHttpResponse response) throws IOException {
				if (validate) {
					String json = IOUtils.toString(response.getBody(),
							response.getHeaders().getContentType().getCharset().name());
					logger.info(JsonHelper.getPrettyJson(json));

				} else {
					Files.copy(response.getBody(), Paths.get(vraPackage.getFilesystemPath()),
							StandardCopyOption.REPLACE_EXISTING);
					logger.debug(String.format("EXPORT  | %s to file: %s", vraPackage, vraPackage.getFilesystemPath()));
				}
				return null;
			}
		};

		try {
			restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("400")) {
				throw new RuntimeException("Probably vRA package has no contents.", e);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	
	/** 
	 * @param vraPackage vra package
	 * @param dryrun dry run code or not
	 * @return VraPackageContent
	 * @throws URISyntaxException throws URI syntax exception incase of invalid URI
	 */
	protected VraPackageContent importPackagePrimitive(final Package vraPackage, final boolean dryrun) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_PACKAGE + (dryrun ? "/validate" : ""))
				// Ex: /validate?resolution mode=SKIP,OVERWRITE
				.setParameter("resolution mode", configuration.getPackageImportOverwriteMode()).build();

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("name", new File(vraPackage.getFilesystemPath()).getName());
		map.add("Content-Type", "application/octet-stream");
		map.add("file", new FileSystemResource(vraPackage.getFilesystemPath()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				map, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		String operationStatus = JsonPath.parse(response.getBody()).read("$.operationStatus");
		if (operationStatus.equalsIgnoreCase("FAILED")) {
			logger.error(JsonHelper.getPrettyJson(response.getBody()));
			throw new RuntimeException("Import of vRA Package[" + vraPackage.getFilesystemPath() + "] has Failed.");
		} else {
			logger.debug(JsonHelper.getPrettyJson(response.getBody()));
			logger.debug(String.format("IMPORT  | %s %s", vraPackage, (dryrun ? "DRYRUN " : "")));
		}
		
		return this.getPackageContentPrimitive(response.getBody());
	}
    
    
	/** 
	 * @param content vra package content
	 * @param dryrun dry run or not
	 */
	protected void deleteContentPrimitive(final Content<VraPackageContent.ContentType> content, final boolean dryrun) {
        String deletePath = null;
        
        switch (content.getType()) {
            case XAAS_BLUEPRINT: deletePath = String.format(SERVICE_XAAS_BLUEPRINT, configuration.getTenant()); break;
            case XAAS_RESOURCE_TYPE: deletePath = String.format(SERVICE_XAAS_TYPE, configuration.getTenant()); break;
            case XAAS_RESOURCE_MAPPING: deletePath = String.format(SERVICE_XAAS_TYPE, configuration.getTenant()); break;
            case XAAS_RESOURCE_ACTION: deletePath = SERVICE_XAAS_OPERATION; break;
            case COMPOSITE_BLUEPRINT: deletePath = BLUEPRINT_PACKAGE; break;
            case SOFTWARE_COMPONENT: deletePath = SERVICE_SOFTWARE; break;
            case PROPERTY_GROUP: deletePath = SERVICE_PROPERTY_GROUP; break;
			case PROPERTY_DICTIONARY: deletePath = SERVICE_PROPERETY_DEFINITION; break;
			case WORKFLOW_SUBSCRIPTION: deletePath = String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()); break;
			case GLOBAL_PROPERTY_DEFINITION: deletePath = SERVICE_PROPERETY_DEFINITION; break;
			case GLOBAL_PROPERTY_GROUP: deletePath = SERVICE_PROPERTY_GROUP; break;
			default: break;
        }
        
        URI url = getURI(getURIBuilder().setPath(String.format("%s/%s", deletePath, content.getId())));
        
        if (!dryrun) {
            restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
        }
    }
    
    
	/** 
	 * @param pkg vra package
	 * @return VraPackageContent
	 */
	protected VraPackageContent getPackageContentPrimitive(final Package pkg) {
        URI url = getURI(getURIBuilder().setPath(String.format("%s/%s/contents", SERVICE_PACKAGE, pkg.getId())));

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        
        JsonElement root = JsonParser.parseString(response.getBody());
        
        List<Content<VraPackageContent.ContentType>> content = new ArrayList<>();

        if (root.isJsonObject()) {
            root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
                JsonObject ob = o.getAsJsonObject();
                
                content.add(new Content<>(
                    VraPackageContent.ContentType.getInstance(ob.get("contentTypeId").getAsString()),
                    ob.get("contentId").getAsString(),
                    ob.get("name").getAsString()));
                
            });

        }
        return new VraPackageContent(content);
    }
    
    
	/** 
	 * @param packageImportedResponse package import response
	 * @return VraPackageContent
	 */
	private VraPackageContent getPackageContentPrimitive(final String packageImportedResponse) {
        JsonElement root = JsonParser.parseString(packageImportedResponse);
        
        List<Content<VraPackageContent.ContentType>> content = new ArrayList<>();

        if (root.isJsonObject()) {
            root.getAsJsonObject().getAsJsonArray("operationResults").forEach(o -> {
                JsonObject ob = o.getAsJsonObject();
                
                content.add(new Content<>(
                    VraPackageContent.ContentType.getInstance(ob.get("contentTypeId").getAsString()),
                    ob.get("contentId").getAsString(),
                    ob.get("contentName").getAsString()));
                
            });

        }
        return new VraPackageContent(content);
    }

}
