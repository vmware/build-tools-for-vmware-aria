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

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.PropertyGroupMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPropertyGroup;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VraNgPropertyGroupStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected VraNgPropertyGroupStore store;
	protected RestClientVraNg restClient;
	protected Package pkg;
	protected ConfigurationVraNg config;
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new VraNgPropertyGroupStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

		VraNgOrganization org = new VraNgOrganization();
		org.setId("b2c558c8-f20c-4da6-9bc3-d7561f64df16");
		org.setName("VIDM-L-01A");

		when(config.getOrgName()).thenReturn("VIDM-L-01A");
		when(restClient.getOrganizationById("b2c558c8-f20c-4da6-9bc3-d7561f64df16")).thenReturn(org);
		when(restClient.getOrganizationByName("VIDM-L-01A")).thenReturn(org);

		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println("==========================================================");
		System.out.println("END");
		System.out.println("==========================================================");
	}

	@Test
	void testExportContentWithAllPropertyGroups() throws IOException {
		// GIVEN
		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory", "compute")));

		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(null);
		// TEST
		store.exportContent();

		String[] expectedPropertyGroups = { "memory.json", "compute.json" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedPropertyGroups);
	}

	@Test
	void testExportContentWithAllPropertyGroupsButFilteringIsNone() throws IOException {
		// GIVEN
		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory")));
		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(new ArrayList<>());

		// TEST
		store.exportContent();

		String[] expectedPropertyGroups = {};

		assertEquals(0, tempFolder.getRoot().listFiles().length);
	}

	@Test
	void testExportContentWithSpecificPropertyGroups() throws IOException {
		// GIVEN
		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory", "compute")));

		List<String> propertyGroupNames = new ArrayList<>();
		propertyGroupNames.add("memory");
		List<VraNgPropertyGroup> propertyGroups = new ArrayList<>();

		PropertyGroupMockBuilder memoryBuilder = new PropertyGroupMockBuilder();
		propertyGroups.add(memoryBuilder.setName("memory").build());

		PropertyGroupMockBuilder computeBuilder = new PropertyGroupMockBuilder();
		propertyGroups.add(computeBuilder.setName("compute").build());

		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(propertyGroupNames);

		// TEST
		store.exportContent();

		String[] expectedPropertyGroups = { "memory.json" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedPropertyGroups);
	}

	@Test
	void testImportContentWithAllPropertyGroups() throws IOException {
		// GIVEN
		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(null);

		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory", "compute")));

		PropertyGroupMockBuilder memoryBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(memoryBuilder.setName("memory").build());

		PropertyGroupMockBuilder computeBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(computeBuilder.setName("compute").build());

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).getPropertyGroups();
		verify(restClient, times(2)).updatePropertyGroup(any());
	}

	@Test
	void testImportContentWithExistingPropertyGroup() throws IOException {
		// GIVEN
		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(null);

		List<VraNgPropertyGroup> pgs = this
				.initStoreWithPropertyGroups(new ArrayList<String>(Collections.singletonList("memory")));

		PropertyGroupMockBuilder memoryBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(
				memoryBuilder.setPropertyInRawData("description", "SANITY_CHECK").setName("memory")
						.setId("testImportContentWithExistingPropertyGroup").build());

		ArgumentCaptor<VraNgPropertyGroup> pgArgumentCaptor = ArgumentCaptor.forClass(VraNgPropertyGroup.class);

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).getPropertyGroups();
		verify(restClient, times(1)).updatePropertyGroup(pgArgumentCaptor.capture());
		verify(restClient, never()).createPropertyGroup(any());

		System.out.println(pgArgumentCaptor.getValue().getRawData());
		assertEquals("mockedId", pgArgumentCaptor.getValue().getId());
		assertTrue(pgArgumentCaptor.getValue().getRawData().contains("SANITY_CHECK"));
	}

	@Test
	void testImportContentWithNonExistingPropertyGroups() throws IOException {
		// GIVEN
		List<String> names = new ArrayList<>();
		names.add("nonexisting");
		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(names);

		this.initStoreWithPropertyGroups(new ArrayList<String>());

		PropertyGroupMockBuilder computeBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(computeBuilder.setName("nonexisting").build());

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).getPropertyGroups();
		verify(restClient, times(1)).createPropertyGroup(any());
	}

	@Test
	void testImportContentWithConfig() throws IOException {
		// GIVEN
		List<String> names = new ArrayList<>();
		names.add("memory");
		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(names);

		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory", "compute")));

		PropertyGroupMockBuilder memoryBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(memoryBuilder.setName("memory").build());

		PropertyGroupMockBuilder computeBuilder = new PropertyGroupMockBuilder();
		fsMocks.propertyGroupFsMocks().addPropertyGroup(computeBuilder.setName("compute").build());

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).getPropertyGroups();
		verify(restClient, times(1)).updatePropertyGroup(any());
	}

	@Test
	void testExportContentWithNotExistingPropertyGroups() throws IOException {
		// GIVEN
		this.initStoreWithPropertyGroups(new ArrayList<String>(Arrays.asList("memory", "compute")));

		List<String> propertyGroupNames = new ArrayList<>();
		propertyGroupNames.add("out ouf memory");

		when(vraNgPackageDescriptor.getPropertyGroup()).thenReturn(propertyGroupNames);

		// TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());
	}

	private List<VraNgPropertyGroup> initStoreWithPropertyGroups(ArrayList<String> propertyGroupNames)
			throws IOException {
		List<VraNgPropertyGroup> propertyGroups = new ArrayList<>();
		for (String pgName : propertyGroupNames) {
			PropertyGroupMockBuilder computeBuilder = new PropertyGroupMockBuilder();
			propertyGroups.add(computeBuilder.setName(pgName).build());
		}

		when(restClient.getPropertyGroups()).thenReturn(propertyGroups);

		store.init(restClient, pkg, config, vraNgPackageDescriptor);

		return propertyGroups;
	}
}
