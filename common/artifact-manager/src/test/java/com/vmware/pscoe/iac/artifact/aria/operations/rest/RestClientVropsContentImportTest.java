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
package com.vmware.pscoe.iac.artifact.aria.operations.rest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.aria.operations.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.aria.operations.models.AuthUserDTO;

class RestClientVropsContentImportTest {
	private static final String PREVIOUS_IMPORT = "{\"state\":\"FINISHED\",\"startTime\":1,\"operationSummaries\":[]}";
	private static final String RUNNING_IMPORT = "{\"state\":\"RUNNING\",\"startTime\":2,\"operationSummaries\":[]}";

	@TempDir
	File tempDir;

	private RestTemplate restTemplate;
	private RestClientVrops restClient;
	private File bundle;

	@BeforeEach
	void init() throws IOException {
		restTemplate = Mockito.mock(RestTemplate.class);
		restClient = new RestClientVrops(new ConfigurationVrops(getProperties(false)), restTemplate);
		restClient.setContentOperationPollIntervalMs(1);
		bundle = new File(tempDir, "bundle.zip");
		Files.write(bundle.toPath(), new byte[] { 1 });
	}

	@Test
	void importContentWaitsForTheNewImportToFinish() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, PREVIOUS_IMPORT, RUNNING_IMPORT, finishedImport(1, 0, 0));

		// WHEN
		restClient.importContent(bundle, "dashboard 'test'");

		// THEN
		ArgumentCaptor<URI> uri = ArgumentCaptor.forClass(URI.class);
		Mockito.verify(restTemplate).postForEntity(uri.capture(), any(HttpEntity.class), eq(String.class));
		assertEquals("/suite-api/api/content/operations/import", uri.getValue().getPath());
		assertEquals("force=true", uri.getValue().getQuery());
		Mockito.verify(restTemplate, Mockito.times(4)).exchange(any(URI.class), eq(HttpMethod.GET),
				any(HttpEntity.class), eq(String.class));
	}

	@Test
	void importContentWhenNoPreviousImport() {
		// GIVEN
		Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
				.thenReturn(ResponseEntity.ok(finishedImport(1, 0, 0)));

		// WHEN / THEN
		assertDoesNotThrow(() -> restClient.importContent(bundle, "dashboard 'test'"));
	}

	@Test
	void importContentFailsWhenContentFailed() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, finishedImport(0, 0, 1));

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> restClient.importContent(bundle, "dashboard 'test'"));

		// THEN
		assertTrue(e.getMessage().contains("dashboard 'test'"), e.getMessage());
		assertTrue(e.getMessage().contains("DASHBOARDS: 0 imported, 1 failed, 0 skipped out of 1"), e.getMessage());
	}

	@Test
	void importContentFailsWhenContentSkipped() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, finishedImport(0, 1, 0));

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> restClient.importContent(bundle, "dashboard 'test'"));

		// THEN
		assertTrue(e.getMessage().contains("DASHBOARDS: 0 imported, 0 failed, 1 skipped out of 1"), e.getMessage());
	}

	@Test
	void importContentFailsWhenContentNotImported() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, finishedImport(0, 0, 0));

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> restClient.importContent(bundle, "dashboard 'test'"));

		// THEN
		assertTrue(e.getMessage().contains("DASHBOARDS: 0 imported, 0 failed, 0 skipped out of 1"), e.getMessage());
	}

	@Test
	void importContentFailsWhenImportFailed() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, "{\"state\":\"FAILED\",\"startTime\":2,\"errorCode\":\"INVALID_FILE_FORMAT\","
				+ "\"errorMessages\":[\"Failed to import content: invalid format\"],\"operationSummaries\":[]}");

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> restClient.importContent(bundle, "dashboard 'test'"));

		// THEN
		assertTrue(e.getMessage().contains("Failed to import content: invalid format"), e.getMessage());
	}

	@Test
	void getDashboardImportUsersReturnsConfiguredUser() {
		assertEquals(Collections.singletonList("dashboardUser"), restClient.getDashboardImportUsers());
	}

	@Test
	void getDashboardImportUsersReturnsAllUsersWhenImportingForAllUsers() {
		// GIVEN
		RestClientVrops client = Mockito.spy(new RestClientVrops(new ConfigurationVrops(getProperties(true)),
				restTemplate));
		AuthUserDTO admin = new AuthUserDTO();
		admin.setUsername("admin");
		AuthUserDTO user = new AuthUserDTO();
		user.setUsername("user1");
		Mockito.doReturn(Arrays.asList(admin, user)).when(client).findAllAuthUsers();

		// WHEN
		List<String> users = client.getDashboardImportUsers();

		// THEN
		assertEquals(Arrays.asList("admin", "user1"), users);
	}

	@Test
	void exportContentDownloadsTheBundle() throws IOException {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, RUNNING_IMPORT, "{\"state\":\"FINISHED\",\"startTime\":2}");
		Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(byte[].class)))
				.thenReturn(ResponseEntity.ok(new byte[] { 1, 2 }));
		File target = new File(tempDir, "export.zip");

		// WHEN
		restClient.exportContent(Arrays.asList("DASHBOARDS", "VIEW_DEFINITIONS"), target);

		// THEN
		ArgumentCaptor<URI> uri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpEntity> request = ArgumentCaptor.forClass(HttpEntity.class);
		Mockito.verify(restTemplate).postForEntity(uri.capture(), request.capture(), eq(String.class));
		assertEquals("/suite-api/api/content/operations/export", uri.getValue().getPath());
		String body = (String) request.getValue().getBody();
		assertTrue(body.contains("\"scope\":\"ALL\""), body);
		assertTrue(body.contains("\"contentTypes\":[\"DASHBOARDS\",\"VIEW_DEFINITIONS\"]"), body);
		assertArrayEquals(new byte[] { 1, 2 }, Files.readAllBytes(target.toPath()));
	}

	@Test
	void exportContentFailsWhenExportFailed() {
		// GIVEN
		mockImportStatuses(PREVIOUS_IMPORT, "{\"state\":\"FAILED\",\"startTime\":2,\"errorMessages\":[\"boom\"]}");

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class,
				() -> restClient.exportContent(Arrays.asList("DASHBOARDS"), new File(tempDir, "export.zip")));

		// THEN
		assertTrue(e.getMessage().contains("boom"), e.getMessage());
	}

	@Test
	void setPolicyPrioritiesSkipsInactivePolicies() throws Exception {
		// GIVEN
		RestClientVrops client = mockPolicies("[]");

		// WHEN
		client.setPolicyPriorities(Arrays.asList("Policy 1"));

		// THEN
		Mockito.verify(restTemplate, Mockito.never()).exchange(any(URI.class), eq(HttpMethod.PUT),
				any(HttpEntity.class), eq(String.class));
	}

	@Test
	void setPolicyPrioritiesOrdersAllActivePolicies() throws Exception {
		// GIVEN
		RestClientVrops client = mockPolicies("[{\"id\":\"p1\",\"name\":\"Policy 1\",\"defaultPolicy\":false,\"priority\":2},"
				+ "{\"id\":\"p2\",\"name\":\"Policy 2\",\"defaultPolicy\":false,\"priority\":1}]");

		// WHEN
		client.setPolicyPriorities(Arrays.asList("Policy 1"));

		// THEN
		ArgumentCaptor<HttpEntity> request = ArgumentCaptor.forClass(HttpEntity.class);
		Mockito.verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.PUT), request.capture(),
				eq(String.class));
		assertEquals("{\"policyIds\":[\"p1\",\"p2\"]}", request.getValue().getBody());
	}

	/**
	 * Mocks the policies, all of them and the active ones.
	 */
	private RestClientVrops mockPolicies(final String activePolicies) {
		RestClientVrops client = Mockito.spy(restClient);
		Mockito.doReturn(true).when(client).isVersionAbove812();
		Mockito.doReturn(true).when(client).isVersionAbove817();
		String defaultPolicy = "{\"id\":\"d\",\"name\":\"Default Policy\",\"defaultPolicy\":true}";
		String allPolicies = "{\"policySummaries\":[" + defaultPolicy + ","
				+ "{\"id\":\"p1\",\"name\":\"Policy 1\",\"defaultPolicy\":false},"
				+ "{\"id\":\"p2\",\"name\":\"Policy 2\",\"defaultPolicy\":false}]}";
		String actives = "{\"policySummaries\":[" + defaultPolicy
				+ ("[]".equals(activePolicies) ? "" : "," + activePolicies.substring(1, activePolicies.length() - 1))
				+ "]}";
		Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenAnswer(invocation -> {
					URI uri = invocation.getArgument(0);
					boolean activesOnly = uri.getQuery() != null && uri.getQuery().contains("activesOnly=true");
					return ResponseEntity.ok(activesOnly ? actives : allPolicies);
				});
		return client;
	}

	private void mockImportStatuses(final String first, final String... next) {
		ResponseEntity<String>[] responses = Arrays.stream(next).map(ResponseEntity::ok)
				.toArray(ResponseEntity[]::new);
		Mockito.when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.ok(first), responses);
	}

	private static String finishedImport(final int imported, final int skipped, final int failed) {
		return String.format("{\"state\":\"FINISHED\",\"startTime\":2,\"errorMessages\":[],\"operationSummaries\":[{"
				+ "\"contentType\":\"DASHBOARDS\",\"state\":\"FINISHED\",\"imported\":%d,\"skipped\":%d,\"failed\":%d,"
				+ "\"total\":1,\"errorMessages\":[],\"infoMessages\":[]}]}", imported, skipped, failed);
	}

	private static Properties getProperties(final boolean importDashboardsForAllUsers) {
		Properties properties = new Properties();
		properties.setProperty("host", "localhost");
		properties.setProperty("port", "443");
		properties.setProperty("dashboardUser", "dashboardUser");
		properties.setProperty("importDashboardsForAllUsers", Boolean.toString(importDashboardsForAllUsers));
		return properties;
	}
}
