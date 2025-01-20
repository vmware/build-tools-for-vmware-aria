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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Properties;

import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.helpers.vro.RestClientVroTestDouble;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestClientVroTest {
	protected RestTemplate restTemplate;
	protected ConfigurationVro config;
	protected RestClientVroTestDouble restClient;

	@BeforeEach
	void init() {
		restTemplate = Mockito.mock(RestTemplate.class);
		config = Mockito.mock(ConfigurationVro.class);
		restClient = new RestClientVroTestDouble(config, restTemplate);
		when(config.getHost()).thenReturn("vra-l-01a.corp.local");
		when(config.getPort()).thenReturn(443);
		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	@AfterEach
	void tearDown() {
		System.out.println("==========================================================");
		System.out.println("END");
		System.out.println("==========================================================");
	}

	@Test
	void testGetInputParametersTypes() {

		// GIVEN
		String responseBody = String.join("\n", "{", "    \"display-name\": \"Install\",", "    \"input\": {",
				"        \"param\": [", "            { \"name\": \"jsonString\", \"type\": \"string\" },",
				"            { \"name\": \"tags\", \"type\": \"Array/string\" },",
				"            { \"name\": \"blacklist\", \"type\": \"Array/string\" }", "        ]", "    }", "}");

		// WHEN
		when(
				restTemplate.exchange(
						any(URI.class),
						any(HttpMethod.class),
						any(HttpEntity.class),
						any(Class.class)))
				.thenReturn(new ResponseEntity<String>(responseBody, HttpStatus.OK));

		Properties parametersTypes = restClient.getInputParametersTypes("1490692845582937823496790834565483423");

		// THEN
		assertEquals(parametersTypes.get("jsonString").toString(), "string");
		assertEquals(parametersTypes.get("tags").toString(), "Array/string");
		assertEquals(parametersTypes.get("blacklist").toString(), "Array/string");
	}

	@Test
	void testBuildParametersJsonForNumberType() {

		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		params.put("foo", 9);
		params.put("bar", 11);

		inputParametersTypes.put("foo", "number");
		inputParametersTypes.put("bar", "number");

		String expectedResult = String.join("",
				"{",
				"\"parameters\":[{",
				"\"name\":\"bar\",",
				"\"scope\":\"local\",",
				"\"type\":\"number\",",
				"\"value\":{",
				"\"number\":{",
				"\"value\":11",
				"}",
				"}",
				"},{",
				"\"name\":\"foo\",",
				"\"scope\":\"local\",",
				"\"type\":\"number\",",
				"\"value\":{",
				"\"number\":{",
				"\"value\":9",
				"}",
				"}",
				"}]",
				"}");

		// WHEN
		String actualResult = restClient.buildParametersJson(params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testBuildParametersJsonForBooleanType() {

		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		params.put("foo", false);
		params.put("bar", true);

		inputParametersTypes.put("foo", "boolean");
		inputParametersTypes.put("bar", "boolean");

		String expectedResult = String.join("",
				"{",
				"\"parameters\":[{",
				"\"name\":\"bar\",",
				"\"scope\":\"local\",",
				"\"type\":\"boolean\",",
				"\"value\":{",
				"\"boolean\":{",
				"\"value\":true",
				"}",
				"}",
				"},{",
				"\"name\":\"foo\",",
				"\"scope\":\"local\",",
				"\"type\":\"boolean\",",
				"\"value\":{",
				"\"boolean\":{",
				"\"value\":false",
				"}",
				"}",
				"}]",
				"}");

		// WHEN
		String actualResult = restClient.buildParametersJson(params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testBuildParametersJsonForStringType() {

		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		params.put("foo", "abc");
		params.put("bar", "def");

		inputParametersTypes.put("foo", "string");
		inputParametersTypes.put("bar", "string");

		String expectedResult = String.join("",
				"{",
				"\"parameters\":[{",
				"\"name\":\"bar\",",
				"\"scope\":\"local\",",
				"\"type\":\"string\",",
				"\"value\":{",
				"\"string\":{",
				"\"value\":\"def\"",
				"}",
				"}",
				"},{",
				"\"name\":\"foo\",",
				"\"scope\":\"local\",",
				"\"type\":\"string\",",
				"\"value\":{",
				"\"string\":{",
				"\"value\":\"abc\"",
				"}",
				"}",
				"}]",
				"}");

		// WHEN
		String actualResult = restClient.buildParametersJson(params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testBuildParametersJsonForNonExistentType() {

		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		params.put("foo", "abc");
		params.put("bar", "def");

		inputParametersTypes.put("foo", "nonExistentType");
		inputParametersTypes.put("bar", "nonExistentType");

		// all non-existent types should be handled as String
		String expectedResult = String.join("",
				"{",
				"\"parameters\":[{",
				"\"name\":\"bar\",",
				"\"scope\":\"local\",",
				"\"type\":\"string\",",
				"\"value\":{",
				"\"string\":{",
				"\"value\":\"def\"",
				"}",
				"}",
				"},{",
				"\"name\":\"foo\",",
				"\"scope\":\"local\",",
				"\"type\":\"string\",",
				"\"value\":{",
				"\"string\":{",
				"\"value\":\"abc\"",
				"}",
				"}",
				"}]",
				"}");

		// WHEN
		String actualResult = restClient.buildParametersJson(params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testBuildParametersJsonForStringArrayType() {

		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		params.put("foo", "[\"git\", \"avi\"]");

		inputParametersTypes.put("foo", "Array/string");

		String expectedResult = String.join("",
				"{",
				"\"parameters\":[{",
				"\"name\":\"foo\",",
				"\"scope\":\"local\",",
				"\"type\":\"Array/string\",",
				"\"value\":{",
				"\"array\":{",
				"\"elements\":[{",
				"\"string\":{",
				"\"value\":\"git\"",
				"}",
				"},{",
				"\"string\":{",
				"\"value\":\"avi\"",
				"}",
				"}]",
				"}",
				"}",
				"}]",
				"}");

		// WHEN
		String actualResult = restClient.buildParametersJson(params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testStartWorkflow() {
		// GIVEN
		Properties params = new Properties();
		Properties inputParametersTypes = new Properties();

		String workflowId = "1490692845582937823496790834565483423";
		String expectedResult = "9e63824b-0612-41dd-9e69-8f7bb8c7846c";
		String responseBody = String.join("\n", "{", "    \"id\": \"" + expectedResult + "\",",
				"    \"state\": \"running\",", "    \"start-date\": \"2022-05-27T09:05:27.587+00:00\",",
				"    \"started-by\": \"configurationadmin\",",
				"    \"running-instance-id\": \"vra-l-01a.corp.local-vco-app-77844f5fb9-csdlw\",",
				"    \"name\": \"Install\",", "    \"current-item-for-display\": \"__item-undefined__\"", "}");

		// WHEN
		when(
				restTemplate.exchange(
						any(RequestEntity.class),
						any(Class.class)))
				.thenReturn(new ResponseEntity<String>(responseBody, HttpStatus.OK));

		String actualResult = restClient.startWorkflow(workflowId, params, inputParametersTypes);

		// THEN
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void testStartWorkflowWhenKeyTypeIsNullThrows() {
		Properties params = new Properties();
		params.put("foo", "abc");
		Properties inputParametersTypes = new Properties();

		String workflowId = "1490692845582937823496790834565483423";

		assertThrows(RuntimeException.class, () -> restClient.startWorkflow(workflowId, params, inputParametersTypes));
	}
}
