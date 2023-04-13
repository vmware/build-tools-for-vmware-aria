package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ResourceActionMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgResourceAction;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyString;
import static org.mockito.ArgumentMatchers.any;

public class VraNgResourceActionStoreTest {

	/**
	 * tempFolder.
	 */
	@Rule
	public TemporaryFolder 				tempFolder		= new TemporaryFolder();

	/**
	 * VraNgResourceActionStore.
	 */
	private VraNgResourceActionStore 	store;
	/**
	 * RestClientVraNg.
	 */
	private RestClientVraNg 			restClient;
	/**
	 * Package.
	 */
	private Package 					pkg;
	/**
	 * ConfigurationVraNg.
	 */
	private ConfigurationVraNg 		config;
	/**
	 * vraNgPackageDescriptor.
	 */
	private VraNgPackageDescriptor 	vraNgPackageDescriptor;
	/**
	 * FsMocks.
	 */
	private FsMocks 					fsMocks;

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

		fsMocks					= new FsMocks(tempFolder.getRoot());
		store					= new VraNgResourceActionStore();
		restClient				= Mockito.mock(RestClientVraNg.class);
		pkg						= PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config					= Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor	= Mockito.mock(VraNgPackageDescriptor.class);

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
		//GIVEN 
		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, never()).getAllResourceActions();

		assertEquals(0, tempFolder.getRoot().listFiles().length);
	}

	@Test
	void testExportContentWithAllResourceActions() throws IOException {
		//GIVEN 
		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder mockBuilder = new ResourceActionMockBuilder();
		
		resourceActions.put("mockedResourceActionId", mockBuilder.setId("mockedResourceActionId").setName("mockedResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(null);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		//TEST
		store.exportContent();

		String[] expectedResourceActions	= {"mockedResourceAction.json"};

		// VERIFY
		verify(restClient, times(1)).getAllResourceActions();
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedResourceActions);
	}


	@Test
	void testExportContentWithSpecificResourceActions() throws IOException {
		//GIVEN 
		List<String> exportedResourceActions = new ArrayList<String>();
		exportedResourceActions.add("Cloud.vSphere.Machine__AzureResourceAction");

		Map<String, VraNgResourceAction> resourceActions = new HashMap<>();
		ResourceActionMockBuilder azureMockBuilder = new ResourceActionMockBuilder();
		ResourceActionMockBuilder vsphereMockBuilder = new ResourceActionMockBuilder();

		resourceActions.put("AzureResourceActionId", azureMockBuilder.setId("AzureResourceActionId").setName("AzureResourceAction").build());
		resourceActions.put("vsphereMdResourceActionId", vsphereMockBuilder.setId("vsphereMResourceActionId").setName("vsphereMResourceAction").build());

		when(vraNgPackageDescriptor.getResourceAction()).thenReturn(exportedResourceActions);
		when(restClient.getAllResourceActions()).thenReturn(resourceActions);

		//TEST
		store.exportContent();

		String[] expectedResourceActions	= {"Cloud.vSphere.Machine__AzureResourceAction.json"};

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
}
