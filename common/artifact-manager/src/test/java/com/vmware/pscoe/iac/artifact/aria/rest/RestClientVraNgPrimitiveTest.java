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
package com.vmware.pscoe.iac.artifact.aria.automation.rest;

import static com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil.getOrganization;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.stubs.AbxActionMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ContentSharingPolicyMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CustomResourceMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.PropertyGroupMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.vrang.RestClientVraNgPrimitiveTestDouble;
import com.vmware.pscoe.iac.artifact.helpers.vrang.RestClientVraNgPrimitiveTestResponseProvider;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgProject;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.aria.automation.utils.VraNgOrganizationUtil;

@SuppressWarnings("unchecked")
class RestClientVraNgPrimitiveTest {
	/**
	 * HTTPS_PORT.
	 */
	private static final int HTTPS_PORT = 443;

	/**
	 * HTTPS_PORT.
	 */
	private static final int HTTP_SUCCESS_CODE = 200;

	/**
	 * HTTPS_PORT.
	 */
	private static final int HTTP_BAD_REQUEST_CODE = 400;

	/**
	 * restTemplate.
	 */
	private RestTemplate restTemplate;

	/**
	 * config.
	 */
	private ConfigurationVraNg config;

	/**
	 * restClient.
	 */
	private RestClientVraNgPrimitiveTestDouble restClient;

	/**
	 * vraNgOrganizationUtil.
	 */
	private MockedStatic<VraNgOrganizationUtil> vraNgOrganizationUtil;

	/**
	 * vraNgOrganization.
	 */
	private VraNgOrganization vraNgOrganization;

	@BeforeEach
	void init() {
		restTemplate = Mockito.mock(RestTemplate.class);
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgOrganizationUtil = Mockito.mockStatic(VraNgOrganizationUtil.class);
		restClient = new RestClientVraNgPrimitiveTestDouble(config, restTemplate);
		vraNgOrganization = Mockito.mock(VraNgOrganization.class);
		when(config.getHost()).thenReturn("vra-l-01a.corp.local");
		when(config.getPort()).thenReturn(HTTPS_PORT);
		vraNgOrganizationUtil.when(() -> getOrganization(restClient, config)).thenReturn(vraNgOrganization);
		when(vraNgOrganization.getId()).thenReturn("mockOrg");
	}

	@AfterEach
	void close() {
		vraNgOrganizationUtil.close();
	}

	@Test
	void testGetProjectsPrimitiveEmptyFirstPage() {
		// GIVEN
		final int totalElements = 0;
		final int page = 0;
		final int pageSize = 500;

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetCatalogItemsByProjectEmptyFirstPage() {
		// GIVEN
		final int totalElements = 0;
		final int page = 0;
		final int pageSize = 500;
		final String projectId = "project 3";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		List<VraNgCatalogItem> catalogItems = restClient.testGetCatalogItemsForProjectPrimitive(projectId);

		// THEN
		assertEquals(totalElements, catalogItems.size());
	}

	@Test
	void testGetCatalogItemPerBlueprintNameSingleElementPage() {
		// GIVEN
		final int totalElements = 1;
		final int page = 0;
		final int pageSize = 1;
		final String catalogItemName = "catalog item 0";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		VraNgCatalogItem catalogItem = restClient.testGetCatalogItemByBlueprintNamePrimitive(catalogItemName);

		// THEN
		assertEquals(catalogItem.getName(), catalogItemName);
	}

	@Test
	void testGetProjectsPrimitiveNonEmptyFirstPage() {
		// GIVEN
		final int totalElements = 51;
		final int pageSize = 500;
		int page = 0;

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetCatalogItemsForProjectFirstNonEmptyFirstPage() {
		// GIVEN
		final int totalElements = 51;
		final int pageSize = 500;
		int page = 0;
		final String projectId = "project 3";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		List<VraNgCatalogItem> catalogItems = restClient.testGetCatalogItemsForProjectPrimitive(projectId);

		// THEN
		assertEquals(totalElements, catalogItems.size());
	}

	@Test
	void testGetCatalogItemPerBlueprintNameNonEmptyFirstPage() {
		// GIVEN
		final int totalElements = 51;
		final int page = 0;
		final int pageSize = 500;
		final String catalogItemName = "catalog item 0";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		VraNgCatalogItem catalogItem = restClient.testGetCatalogItemByBlueprintNamePrimitive(catalogItemName);

		// THEN
		assertEquals(catalogItem.getName(), catalogItemName);
	}

	@Test
	void testGetProjectsPrimitiveFirstPageEdgeCase() {
		// GIVEN
		final int totalElements = 500;
		final int pageSize = 500;
		int page = 0;

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testCatalogItemsPerProjectFirstPageEdgeCase() {
		// GIVEN
		final int totalElements = 100;
		final int pageSize = 1000;
		int page = 0;
		final String projectId = "project 3";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		List<VraNgCatalogItem> catalogItems = restClient.testGetCatalogItemsForProjectPrimitive(projectId);

		// THEN
		assertEquals(totalElements, catalogItems.size());
	}

	@Test
	void testCatalogItemsPerBlueprintNameFirstPageEdgeCase() {
		// GIVEN
		final int totalElements = 100;
		final int pageSize = 1000;
		int page = 0;
		final String catalogItemName = "catalog item 0";

		// WHEN
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedCatalogItemsResponse(totalElements,
						pageSize, page));
		VraNgCatalogItem catalogItem = restClient.testGetCatalogItemByBlueprintNamePrimitive(catalogItemName);

		// THEN
		assertEquals(catalogItem.getName(), catalogItemName);
	}

	@Test
	void testGetProjectsPrimitiveSecondPageWithSingleElement() throws URISyntaxException {
		// GIVEN
		final int totalElements = 501;
		final int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, firstPage));

		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveSecondPageWithMultipleElements() throws URISyntaxException {
		// GIVEN
		final int totalElements = 755;
		final int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, firstPage));

		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveSecondPageEdgeCase() throws URISyntaxException {
		// GIVEN
		final int totalElements = 1000;
		final int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, firstPage));

		when(restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d",
						pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
				.thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements,
						pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testImportCustomFormPrimitiveAlwaysCustomFormFormatNull() {
		VraNgCustomForm parameter = new VraNgCustomForm("id", "name", "form", "styles", "VRO", "VRO", "VRO", "OK",
				null);
		String sourceId = "sourceId";

		Assertions.assertDoesNotThrow(() -> {
			this.restClient.importCustomFormPrimitive(parameter, sourceId);
		});
	}

	@Test
	void testImportCustomFormPrimitiveAlwaysCustomFormFormatNotNull() {
		VraNgCustomForm parameter = new VraNgCustomForm("id", "name", "form", "styles", "VRO", "VRO", "VRO", "OK",
				"JSON");
		String sourceId = "sourceId";

		Assertions.assertDoesNotThrow(() -> {
			this.restClient.importCustomFormPrimitive(parameter, sourceId);
		});
	}

	@Test
	void testCreatePropertyGroupPrimitive() throws IOException {
		PropertyGroupMockBuilder propertyGroupBuilder = new PropertyGroupMockBuilder();
		VraNgPropertyGroup pg = propertyGroupBuilder.setName("memory").build();
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/properties/api/property-groups"));
		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity<String>> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		restClient.createPropertyGroupPrimitive(pg);

		verify(restTemplate, times(1)).exchange(argCaptorUri.capture(), argCaptorMethod.capture(),
				argCaptorEntity.capture(), any(Class.class));

		assertEquals(uri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());
		assertEquals(pg.getRawData(), argCaptorEntity.getValue().getBody());
	}

	@Test
	void testUpdatePropertyGroupPrimitive() throws IOException {
		PropertyGroupMockBuilder propertyGroupBuilder = new PropertyGroupMockBuilder();
		VraNgPropertyGroup pg = propertyGroupBuilder.setName("memory").build();
		URI uri = restClient
				.getURI(restClient.getURIBuilder().setPath("/properties/api/property-groups/" + pg.getId()));
		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity<String>> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		restClient.updatePropertyGroupPrimitive(pg);

		verify(restTemplate, times(1)).exchange(argCaptorUri.capture(), argCaptorMethod.capture(),
				argCaptorEntity.capture(), any(Class.class));

		assertEquals(uri, argCaptorUri.getValue());
		assertEquals(HttpMethod.PUT, argCaptorMethod.getValue());
		assertEquals(pg.getRawData(), argCaptorEntity.getValue().getBody());
	}

	@Test
	void testImportCustomResourcePrimitive() throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.build();

		URI expectedUri = restClient
				.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity<String>> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		when(response.getStatusCode()).thenReturn(HttpStatus.valueOf(HTTP_SUCCESS_CODE));
		when(response.getBody()).thenReturn(mockCustomResource.getJson());

		when(restTemplate.exchange(argCaptorUri.capture(), argCaptorMethod.capture(), argCaptorEntity.capture(),
				any(Class.class))).thenReturn(response);

		// WHEN
		restClient.importCustomResourcePrimitive(mockCustomResource.getJson());

		// THEN
		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions(new JsonArray())
				.build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(JsonParser.parseString(expectedMockCustomResource.getJson()).getAsJsonObject(),
				JsonParser.parseString(argCaptorEntity.getAllValues().get(0).getBody().toString()).getAsJsonObject());

		// Second time we expect the CR with the `additionalActions` back
		assertEquals(JsonParser.parseString(mockCustomResource.getJson()).getAsJsonObject(),
				JsonParser.parseString(argCaptorEntity.getAllValues().get(1).getBody().toString()).getAsJsonObject());
	}

	@Test
	void testImportCustomResourcePrimitiveWithNoAdditionalActions() throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.setAdditionalActions(new JsonArray())
				.build();

		URI expectedUri = restClient
				.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity<String>> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		when(response.getStatusCode()).thenReturn(HttpStatus.valueOf(HTTP_SUCCESS_CODE));
		when(response.getBody()).thenReturn(mockCustomResource.getJson());

		when(restTemplate.exchange(argCaptorUri.capture(), argCaptorMethod.capture(), argCaptorEntity.capture(),
				any(Class.class))).thenReturn(response);

		// WHEN
		restClient.importCustomResourcePrimitive(mockCustomResource.getJson());

		// THEN
		verify(restTemplate, times(1)).exchange(any(), any(), any(), any(Class.class));

		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions(new JsonArray())
				.build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(JsonParser.parseString(expectedMockCustomResource.getJson()).getAsJsonObject(),
				JsonParser.parseString(argCaptorEntity.getValue().getBody().toString()).getAsJsonObject());
	}

	@Test
	void testImportCustomResourcePrimitiveThrowsIfCannotImportCustomResourceActions()
			throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.build();

		URI expectedUri = restClient
				.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity<String>> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity<String> response = Mockito.mock(ResponseEntity.class);
		when(response.getStatusCode()).thenReturn(HttpStatus.valueOf(HTTP_SUCCESS_CODE))
				.thenReturn(HttpStatus.valueOf(HTTP_BAD_REQUEST_CODE));
		when(response.getBody()).thenReturn(mockCustomResource.getJson());

		when(restTemplate.exchange(argCaptorUri.capture(), argCaptorMethod.capture(), argCaptorEntity.capture(),
				any(Class.class))).thenReturn(response);

		// WHEN
		Exception exception = assertThrows(RuntimeException.class, () -> {
			restClient.importCustomResourcePrimitive(mockCustomResource.getJson());
		});

		// THEN
		assertTrue(exception.getMessage().contains("Unable to import additionalActions for Avi Load Balancer L3DSR"));

		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions(new JsonArray())
				.build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(JsonParser.parseString(expectedMockCustomResource.getJson()).getAsJsonObject(),
				JsonParser.parseString(argCaptorEntity.getAllValues().get(0).getBody().toString()).getAsJsonObject());

		// Second time we expect the CR with the `additionalActions` back
		assertEquals(JsonParser.parseString(mockCustomResource.getJson()).getAsJsonObject(),
				JsonParser.parseString(argCaptorEntity.getAllValues().get(1).getBody().toString()).getAsJsonObject());
	}

	// =================================================
	// ABX ACTIONS
	// =================================================

	@ParameterizedTest
	@MethodSource("abxActionArgs")
	void testCreateAbxActionMap(final Map<String, Object> expectedResult, final AbxAction abxAction)
			throws IOException {

		Map<String, Object> actualResult = restClient.createAbxActionMap(abxAction);
		assertEquals(expectedResult, actualResult);
	}

	private static Stream<Arguments> abxActionArgs() throws IOException {
		List<Arguments> arguments = new ArrayList<Arguments>();

		Map<String, Object> expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		AbxAction abxAction = AbxActionMockBuilder.buildAbxAction();

		// Providers, Timeout, MemoryLimit all are empty
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Memory Limit equal to zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.getPlatform().setMemoryLimitMb(0);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Memory Limit Greater Than zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.getPlatform().setMemoryLimitMb(AbxActionMockBuilder.MEMORY_LIMIT);
		expectedResult.put("memoryInMB", AbxActionMockBuilder.MEMORY_LIMIT);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Timeout equal to zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();

		abxAction.getPlatform().setTimeoutSec(0);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Timeout greater than zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();

		abxAction.getPlatform().setTimeoutSec(AbxActionMockBuilder.TIMEOUT);
		expectedResult.put("timeoutSeconds", AbxActionMockBuilder.TIMEOUT);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Faas Provider Name Is Correct
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();

		abxAction.getPlatform().setProvider(AbxActionMockBuilder.FAAS_PROVIDER);

		expectedResult.put("provider", AbxActionMockBuilder.FAAS_PROVIDER);
		arguments.add(Arguments.of(expectedResult, abxAction));

		return arguments.stream();
	}

	@Test
	void testCreateAbxActionMapThrownExceptionWhenFaasProviderNameIsNotCorrect() throws IOException {
		AbxAction abxAction = AbxActionMockBuilder.buildAbxAction();
		abxAction.getPlatform().setProvider("Non-existent provider");

		Exception exception = assertThrows(RuntimeException.class, () -> {
			restClient.createAbxActionMap(abxAction);
		});

		assertTrue(exception.getMessage().contains("Faas provider name is not correct"));
	}

	@Test
	void testGetContentSharingPolicyJSON() {
		URI uri = restClient.getURI(restClient.getURIBuilder()
				.setPath("/policy/api/policies" + "/" + "679daee9-d63d-4ce2-9ee1-d4336861fe87"));

		URI uri2 = restClient.getURI(restClient.getURIBuilder()
				.setPath("/catalog/api/admin/sources" + "/" + "d0624893-4932-46a7-8e25-fab1e4109c2e"));

		when(this.restTemplate.exchange(uri, HttpMethod.GET, RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class))
				.thenReturn(new ResponseEntity<>(
						"{\"id\":\"679daee9-d63d-4ce2-9ee1-d4336861fe87\",\"name\":\"CsPolicy\",\"description\":\"Testing\",\"typeId\":\"com.vmware.policy.catalog.entitlement\",\"enforcementType\":\"HARD\",\"orgId\":\"27aaf31d-d9af-4c48-9736-eb9c9faa4ae8\",\"projectId\":\"c3f029f9-a97c-4df6-bdc4-c0e4b91aa18e\",\"definition\":{\"entitledUsers\":[{\"items\":[{\"id\":\"d0624893-4932-46a7-8e25-fab1e4109c2e\",\"type\":\"CATALOG_SOURCE_IDENTIFIER\"}],\"userType\":\"USER\",\"principals\":[{\"type\":\"PROJECT\",\"referenceId\":\"\"}]}]},\"createdAt\":\"2023-02-14T13:58:53.964473Z\",\"createdBy\":\"configurationadmin\",\"lastUpdatedAt\":\"2023-02-23T11:34:17.898581Z\",\"lastUpdatedBy\":\"configurationadmin\"}",
						HttpStatus.OK));

		when(this.restTemplate.exchange(uri2, HttpMethod.GET, RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class))
				.thenReturn(new ResponseEntity<>(
						"{\"id\":\"d0624893-4932-46a7-8e25-fab1e4109c2e\",\"name\":\"Contentsource\",\"description\":\"Testing\",\"typeId\":\"com.vmw.vro.workflow\"}",
						HttpStatus.OK));

		// START TEST
		VraNgContentSharingPolicy csPolicy = restClient
				.getContentSharingPolicyPrimitive("679daee9-d63d-4ce2-9ee1-d4336861fe87");
		VraNgContentSharingPolicy expectedResult = ContentSharingPolicyMockBuilder.buildContentSharingPolicy();
		assertEquals(new Gson().toJson(expectedResult), new Gson().toJson(csPolicy));
		Assertions.assertDoesNotThrow(() -> {
			this.restClient.getContentSharingPolicyPrimitive("679daee9-d63d-4ce2-9ee1-d4336861fe87");
		});
	}

	@Test
	void testGetCloudAccountTags() throws URISyntaxException {
		// GIVEN
		URI uri = restClient
				.getURI(restClient.getURIBuilder().setPath("/iaas/api/cloud-accounts").setParameter("apiVersion", ""));

		// WHEN
		when(this.restTemplate.exchange(uri, HttpMethod.GET, RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class))
				.thenReturn(new ResponseEntity<>(
						"{\"content\":[{\"id\":\"\",\"name\":\"vc-l-01a\",\"cloudAccountType\":\"vsphere\",\"orgId\":\"mockOrg\",\"_links\":{},\"tags\":[{\"key\":\"env\",\"value\":\"dev\"},{\"key\":\"::::\",\"value\":\"\"},{\"key\":\"env_DEV::::\",\"value\":\"\"},{\"key\":\"env_DEV\",\"value\":\"\"}]}]}",
						HttpStatus.OK));

		List<VraNgCloudAccount> cloudAccounts = this.restClient.getAllCloudAccounts();
		String actual = cloudAccounts.stream().map(e -> e.getTags().toString()).reduce("", String::concat);
		String expected = "[env:dev, ::::, env_DEV::::, env_DEV]";

		// THEN
		assertEquals(expected, actual);
	}

}
