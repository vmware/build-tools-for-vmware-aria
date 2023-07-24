package com.vmware.pscoe.iac.artifact.utils;

/*-
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ResourceActionMockBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class VraNgProjectUtilTest {

	/**
	 * RestClientVraNg.
	 */
	private RestClientVraNg restClient;

	/**
	 * Init before each test.
	 */
	@BeforeEach
	void init() {
		restClient = Mockito.mock(RestClientVraNg.class);

		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	@Test
	void testShouldChangeProjectIdOnImportResourceActionThatAlreadyHasOne() throws IOException {
		//GIVEN
		final String configurationProjectId = "configuration-file-project-id";
		final String resourceActionProjectId = "resource-action-file-project-id";

		when(restClient.getProjectId()).thenReturn(configurationProjectId);

		ResourceActionMockBuilder resourceActionMockBuilder = new ResourceActionMockBuilder();
		resourceActionMockBuilder.setName("testResourceAction");
		resourceActionMockBuilder.setPropertyInRawData("projectId", resourceActionProjectId);
		VraNgResourceAction resourceAction = resourceActionMockBuilder.build();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		final JsonObject resourceActionJsonElement = gson.fromJson(resourceAction.getJson(), JsonObject.class);

		//TEST
		VraNgProjectUtil.changeProjectIdBetweenOrganizations(restClient, resourceActionJsonElement, "projectId");

		//VERIFY
		assertEquals(configurationProjectId, resourceActionJsonElement.get("projectId").getAsString());
	}

	@Test
	void testShouldNotChangeProjectIdOnImportResourceActionThatHasEmptyProjectId() throws IOException {
		//GIVEN
		final String configurationProjectId = "configuration-file-project-id";
		final String resourceActionProjectId = "";

		when(restClient.getProjectId()).thenReturn(configurationProjectId);

		ResourceActionMockBuilder resourceActionMockBuilder = new ResourceActionMockBuilder();
		resourceActionMockBuilder.setName("testResourceAction");
		resourceActionMockBuilder.setPropertyInRawData("projectId", resourceActionProjectId);
		VraNgResourceAction resourceAction = resourceActionMockBuilder.build();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		final JsonObject resourceActionJsonElement = gson.fromJson(resourceAction.getJson(), JsonObject.class);

		//TEST
		VraNgProjectUtil.changeProjectIdBetweenOrganizations(restClient, resourceActionJsonElement, "projectId");

		//VERIFY
		assertEquals(null, resourceActionJsonElement.get("projectId"));
	}

	@Test
	void testShouldNotChangeProjectIdOnImportResourceActionThatHasNullProjectId() throws IOException {
		//GIVEN
		final String configurationProjectId = "configuration-file-project-id";

		when(restClient.getProjectId()).thenReturn(configurationProjectId);

		ResourceActionMockBuilder resourceActionMockBuilder = new ResourceActionMockBuilder();
		resourceActionMockBuilder.setName("testResourceAction");
		VraNgResourceAction resourceAction = resourceActionMockBuilder.build();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		final JsonObject resourceActionJsonElement = gson.fromJson(resourceAction.getJson(), JsonObject.class);
		resourceActionJsonElement.remove("projectId");

		//TEST
		VraNgProjectUtil.changeProjectIdBetweenOrganizations(restClient, resourceActionJsonElement, "projectId");

		//VERIFY
		assertEquals(null, resourceActionJsonElement.get("projectId"));
	}
}
