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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import static org.mockito.Mockito.doNothing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgIntegration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ResourceActionMockBuilder;

public class VraNgResourceActionStoreTest {

	/**
	 * tempFolder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	/**
	 * VraNgResourceActionStore.
	 */
	private VraNgResourceActionStore store;
	/**
	 * RestClientVraNg.
	 */
	private RestClientVraNg restClient;
	/**
	 * Package.
	 */
	private Package pkg;
	/**
	 * ConfigurationVraNg.
	 */
	private ConfigurationVraNg config;
	/**
	 * vraNgPackageDescriptor.
	 */
	private VraNgPackageDescriptor vraNgPackageDescriptor;
	/**
	 * FsMocks.
	 */
	private FsMocks fsMocks;

	/**
	 * init.
	 */
	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new VraNgResourceActionStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	/**
	 * teardown.
	 */
	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println("==========================================================");
		System.out.println("END");
		System.out.println("==========================================================");
	}

	@Test
	void testExportContentWithNoResourceActions() {
		// GIVEN
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(new ArrayList<String>());

		// TEST
		store.exportContent();

		// VERIFY
		verify(restClient, never()).getAllResourceActions();

		assertEquals(0, tempFolder.getRoot().listFiles().length);
	}

	@Test
	void testExportContentWithAllResourceActions() throws IOException {
		// GIVEN
		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder mockBuilder = new ResourceActionMockBuilder();

		resourceActions.put("mockedResourceActionId",
				mockBuilder.setId("mockedResourceActionId").setName("mockedResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(null);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		// TEST
		store.exportContent();

		String[] expectedResourceActions = { "mockedResourceAction.json" };

		// VERIFY
		verify(restClient, times(1)).getAllResourceActions();
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedResourceActions);
	}

	@Test
	void testExportContentWithSpecificResourceActions() throws IOException {
		// GIVEN
		List<String> exportedResourceActions = new ArrayList<String>();
		exportedResourceActions.add("Cloud.vSphere.Machine__AzureResourceAction");

		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder azureMockBuilder = new ResourceActionMockBuilder();
		ResourceActionMockBuilder vsphereMockBuilder = new ResourceActionMockBuilder();

		resourceActions.put("AzureResourceActionId",
				azureMockBuilder.setId("AzureResourceActionId").setName("AzureResourceAction").build());
		resourceActions.put("vsphereMdResourceActionId",
				vsphereMockBuilder.setId("vsphereMResourceActionId").setName("vsphereMResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(exportedResourceActions);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		// TEST
		store.exportContent();

		String[] expectedResourceActions = { "Cloud.vSphere.Machine__AzureResourceAction.json" };

		// VERIFY
		verify(restClient, times(1)).getAllResourceActions();
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedResourceActions);
	}

	@Test
	void testImportContentWithConfig() throws IOException {
		// GIVEN
		List<String> names = new ArrayList<>();
		// names.add("memory");
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(names);
		when(restClient.importResourceAction(anyString(), anyString())).thenReturn("test");

		ResourceActionMockBuilder memoryBuilder = new ResourceActionMockBuilder();
		fsMocks.resourceActionFsMocks().addResourceAction(memoryBuilder.setName("memory").build());

		ResourceActionMockBuilder computeBuilder = new ResourceActionMockBuilder();
		fsMocks.resourceActionFsMocks().addResourceAction(computeBuilder.setName("compute").build());

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(0)).importResourceAction(any(), any());
	}

	@Test
	void testExportContentWithNonExistingResourceActions() throws IOException {
		// GIVEN
		List<String> exportedResourceActions = new ArrayList<String>();
		exportedResourceActions.add("Cloud.vSphere.Machine__nothing");

		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder azureMockBuilder = new ResourceActionMockBuilder();
		ResourceActionMockBuilder vsphereMockBuilder = new ResourceActionMockBuilder();

		resourceActions.put("AzureResourceActionId",
				azureMockBuilder.setId("AzureResourceActionId").setName("AzureResourceAction").build());
		resourceActions.put("vsphereMdResourceActionId",
				vsphereMockBuilder.setId("vsphereMResourceActionId").setName("vsphereMResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(exportedResourceActions);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		// TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());

		// VERIFY
		verify(restClient, times(1)).getAllResourceActions();
	}

	@Test
	void testExportContentWithIncorrectName() throws IOException {
		// GIVEN
		List<String> exportedResourceActions = new ArrayList<String>();
		exportedResourceActions.add("nothing");

		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder azureMockBuilder = new ResourceActionMockBuilder();
		ResourceActionMockBuilder vsphereMockBuilder = new ResourceActionMockBuilder();

		resourceActions.put("AzureResourceActionId",
				azureMockBuilder.setId("AzureResourceActionId").setName("AzureResourceAction").build());
		resourceActions.put("vsphereMdResourceActionId",
				vsphereMockBuilder.setId("vsphereMResourceActionId").setName("vsphereMResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(exportedResourceActions);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		// TEST
		assertThrows(RuntimeException.class, () -> store.exportContent());

		// VERIFY
		verify(restClient, times(1)).getAllResourceActions();
	}

	// ─────────────────────────────────────────────────────────────────────────────
	// Helper: minimal resource-action JSON that satisfies the import pipeline.
	// Includes the fields consumed by sanitizeResourceActionJsonElement,
	// populateVroEndpoint, VraNgProjectUtil.changeProjectIdBetweenOrganizations,
	// and deleteResourceAction.
	// ─────────────────────────────────────────────────────────────────────────────
	// ─────────────────────────────────────────────────────────────────────────────
	// Helpers & constants for custom-form import tests
	// ─────────────────────────────────────────────────────────────────────────────
	private static final String RA_ID   = "ra-id-001";
	private static final String RA_NAME = "memory";
	private static final String FORM_JSON = "{\"layout\":{},\"schema\":{},\"options\":{}}";

	/**
	 * Raw vRA resource-action JSON that passes through every step of
	 * importResourceAction (sanitize → populateVroEndpoint → projectId → delete →
	 * serialize → import x2).
	 */
	private static final String RA_INPUT_JSON = "{"
			+ "\"id\":\"" + RA_ID + "\","
			+ "\"name\":\"" + RA_NAME + "\","
			+ "\"resourceType\":\"Cloud.vSphere.Machine\","
			+ "\"runnableItem\":{\"endpointLink\":\"/old/endpoint\"},"
			+ "\"formDefinition\":{\"id\":\"old-form-id\",\"form\":\"\",\"status\":\"ON\"},"
			+ "\"projectId\":\"project-001\","
			+ "\"orgId\":\"org-001\""
			+ "}";

	/** JSON the REST API returns after the first importResourceAction call. */
	private static final String RA_RESULT_JSON = "{"
			+ "\"id\":\"" + RA_ID + "\","
			+ "\"name\":\"" + RA_NAME + "\","
			+ "\"formDefinition\":{\"id\":\"new-form-id\"}"
			+ "}";

	/**
	 * Writes RA_INPUT_JSON directly to resource-actions/memory.json so that
	 * importResourceAction receives the correct raw vRA format (NOT the
	 * VraNgResourceAction wrapper that addResourceAction() would produce).
	 */
	private void createResourceActionFile() throws IOException {
		File raDir = Paths.get(tempFolder.getRoot().getPath(), "resource-actions").toFile();
		raDir.mkdirs();
		Files.write(Paths.get(raDir.getPath(), RA_NAME + ".json"), RA_INPUT_JSON.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Writes the resource action file and a companion __FormData.json.
	 *
	 * @param formContent JSON to write into the companion form file
	 */
	private void createResourceActionAndFormFiles(String formContent) throws IOException {
		createResourceActionFile();

		Path customFormsDir = Paths.get(tempFolder.getRoot().getPath(), "catalog-items", "custom-forms");
		Files.createDirectories(customFormsDir);
		Files.write(customFormsDir.resolve(RA_NAME + "__FormData.json"), formContent.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Mocks all REST calls that importResourceAction makes before it reaches
	 * processResourceActionCustomForm.
	 */
	private void setupImportMocks() {
		VraNgIntegration integration = Mockito.mock(VraNgIntegration.class);
		when(integration.getName()).thenReturn("mock-vro");
		when(integration.getEndpointConfigurationLink()).thenReturn("/resources/endpoints/mock");
		when(config.getVroIntegration()).thenReturn("mock-vro");
		when(restClient.getVraWorkflowIntegration("mock-vro")).thenReturn(integration);

		when(restClient.getProjectId()).thenReturn("project-001");

		doNothing().when(restClient).deleteResourceAction(anyString(), anyString());

		when(restClient.importResourceAction(anyString(), anyString())).thenReturn(RA_RESULT_JSON);
	}

	@Test
	void testImportResourceActionAppliesNewCustomForm() throws IOException {
		// GIVEN: one resource action is configured for import
		List<String> names = new ArrayList<>();
		names.add(RA_NAME);
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(names);

		createResourceActionAndFormFiles(FORM_JSON);
		setupImportMocks();

		// Server has no existing form → treat as new
		when(restClient.getCustomFormByTypeAndSource("resource.action", RA_ID))
				.thenThrow(new RuntimeException("form not found"));

		// WHEN
		store.importContent(tempFolder.getRoot());

		// THEN: importCustomForm must be called once with the resource action id
		verify(restClient, times(1)).importCustomForm(any(VraNgCustomForm.class), eq(RA_ID));
	}

	@Test
	void testImportResourceActionSkipsCustomFormWhenUnchanged() throws IOException {
		// GIVEN
		List<String> names = new ArrayList<>();
		names.add(RA_NAME);
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(names);

		createResourceActionAndFormFiles(FORM_JSON);
		setupImportMocks();

		// Server already has the identical form → no update needed
		VraNgCustomForm serverForm = new VraNgCustomForm(
				"existing-form-id", RA_NAME, FORM_JSON, null,
				RA_ID, "resource.action", "requestForm", "ON", "JSON");
		when(restClient.getCustomFormByTypeAndSource("resource.action", RA_ID)).thenReturn(serverForm);

		// WHEN
		store.importContent(tempFolder.getRoot());

		// THEN: importCustomForm must NOT be called
		verify(restClient, never()).importCustomForm(any(), anyString());
	}

	@Test
	void testImportResourceActionNoCustomFormFileSkipsImportCustomForm() throws IOException {
		// GIVEN: resource action present, but NO companion form file
		List<String> names = new ArrayList<>();
		names.add(RA_NAME);
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(names);

		createResourceActionFile();   // no form file written
		setupImportMocks();

		when(restClient.getCustomFormByTypeAndSource("resource.action", RA_ID)).thenReturn(null);

		// WHEN
		store.importContent(tempFolder.getRoot());

		// THEN: importCustomForm must never be called
		verify(restClient, never()).importCustomForm(any(), anyString());
	}

}
