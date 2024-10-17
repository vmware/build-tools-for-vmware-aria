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
package com.vmware.pscoe.iac.artifact.store.vrang;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CloudAccountMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.StorageProfileMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.rest.RestClient;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Blue;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VraNgStorageProfileStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected VraNgStorageProfileStore store;
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
		store = new VraNgStorageProfileStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

		store.init(restClient, pkg, vraNgPackageDescriptor);
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
	void testExportContentWithNoStorageProfiles() {
		// GIVEN
		CloudAccountMockBuilder builder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		cloudAccounts.add(builder.build());

		when(vraNgPackageDescriptor.getStorageProfile()).thenReturn(new ArrayList<String>());

		// TEST
		store.exportContent(cloudAccounts);

		// VERIFY
		verify(restClient, never()).getAllStorageProfilesByRegion();
		assertEquals(0, tempFolder.getRoot().listFiles().length);
	}

	@Test
	void testExportContentWithAllStorageProfiles() throws IOException {
		// GIVEN
		CloudAccountMockBuilder accountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		VraNgCloudAccount cloudAccount = accountBuilder.setId("mockedId").setName("mockedAccountName")
				.setTags(new ArrayList<String>()).setType("vsphere").setRegionIds(regionIds).build();

		cloudAccounts.add(cloudAccount);

		StorageProfileMockBuilder storageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile storageProfile = storageBuilder.setName("smallStorageMock").build();

		Map<String, List<VraNgStorageProfile>> mockStorageProfilesByRegionId = new HashMap<String, List<VraNgStorageProfile>>();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(storageProfile);
		mockStorageProfilesByRegionId.put("mockedRegionId1", storageProfiles);

		when(vraNgPackageDescriptor.getStorageProfile()).thenReturn(null);
		when(restClient.getAllStorageProfilesByRegion()).thenReturn(mockStorageProfilesByRegionId);
		when(restClient.getSpecificStorageProfile(anyString(), anyString())).thenReturn(storageProfile);
		when(restClient.getFabricEntityName(anyString())).thenReturn("mockedFabricEntity");

		// TEST
		store.exportContent(cloudAccounts);

		String[] expectedStorageProfilesFolder = { "mockedAccountName~mockedRegionId1" };
		String[] expectedStorageProfilesFiles = { "smallStorageMock.json" };
		String[] expectedProfileFiles = { "src-region-profile.json", "storage-profiles" };

		// VERIFY
		verify(restClient, times(1)).getAllStorageProfilesByRegion();
		// generated folder
		// regions/{cloudaccountname~regionId}/storage-profiles/{storage-profiles
		// name}.json
		File regionsFolder = new File(tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles(regionsFolder, expectedStorageProfilesFolder);

		File storageProfilesFolder = new File(
				regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/storage-profiles");
		AssertionsHelper.assertFolderContainsFiles(storageProfilesFolder, expectedStorageProfilesFiles);

		// regions/mockedAccountName~mockedRegionId1/src-region-profile.json
		File profileFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1");
		AssertionsHelper.assertFolderContainsFiles(profileFolder, expectedProfileFiles);

	}

	@Test
	void testExportContentWithSpecificStorageProfile() throws IOException {
		// GIVEN
		List<String> storageProfileNames = new ArrayList<>();
		storageProfileNames.add("smallStorageMock");

		CloudAccountMockBuilder accountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		VraNgCloudAccount cloudAccount = accountBuilder.setId("mockedId").setName("mockedAccountName")
				.setTags(new ArrayList<String>()).setType("vsphere").setRegionIds(regionIds).build();

		cloudAccounts.add(cloudAccount);

		StorageProfileMockBuilder smallStorageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile smallStorageProfile = smallStorageBuilder.setName("smallStorageMock")
				.setPropertyInRawData("id", "smallId").build();

		StorageProfileMockBuilder bigStorageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile bigStorageProfile = bigStorageBuilder.setName("bigStorageMock")
				.setPropertyInRawData("id", "bigId").build();

		Map<String, List<VraNgStorageProfile>> mockStorageProfilesByRegionId = new HashMap<String, List<VraNgStorageProfile>>();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(smallStorageProfile);
		storageProfiles.add(bigStorageProfile);
		mockStorageProfilesByRegionId.put("mockedRegionId1", storageProfiles);

		when(vraNgPackageDescriptor.getStorageProfile()).thenReturn(storageProfileNames);
		when(restClient.getAllStorageProfilesByRegion()).thenReturn(mockStorageProfilesByRegionId);
		when(restClient.getSpecificStorageProfile(anyString(), eq("smallId"))).thenReturn(smallStorageProfile);
		when(restClient.getSpecificStorageProfile(anyString(), eq("bigId"))).thenReturn(bigStorageProfile);
		when(restClient.getFabricEntityName(anyString())).thenReturn("mockedFabricEntity");

		// TEST
		store.exportContent(cloudAccounts);

		String[] expectedStorageProfilesFolder = { "mockedAccountName~mockedRegionId1" };
		String[] expectedStorageProfilesFiles = { "smallStorageMock.json" };
		String[] expectedProfileFiles = { "src-region-profile.json", "storage-profiles" };

		// VERIFY
		verify(restClient, times(1)).getAllStorageProfilesByRegion();
		// generated folder
		// regions/{cloudaccountname~regionId}/storage-profiles/{storage-profiles
		// name}.json
		File regionsFolder = new File(tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles(regionsFolder, expectedStorageProfilesFolder);

		File storageProfilesFolder = new File(
				regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/storage-profiles");
		AssertionsHelper.assertFolderContainsFiles(storageProfilesFolder, expectedStorageProfilesFiles);

		// regions/mockedAccountName~mockedRegionId1/src-region-profile.json
		File profileFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1");
		AssertionsHelper.assertFolderContainsFiles(profileFolder, expectedProfileFiles);
	}

	@Test
	void testExportContentWithSpecificFlavorMappingFromSpecificCloudAccount() throws IOException {
		// GIVEN
		List<String> storageProfileNames = new ArrayList<>();
		storageProfileNames.add("smallStorageMock");

		CloudAccountMockBuilder accountBuilder1 = new CloudAccountMockBuilder();
		List<String> regionIds1 = new ArrayList<String>();
		regionIds1.add("mockedRegionId1");
		VraNgCloudAccount cloudAccount1 = accountBuilder1.setName("mockedAccountName").setRegionIds(regionIds1).build();

		CloudAccountMockBuilder accountBuilder2 = new CloudAccountMockBuilder();
		List<String> regionIds2 = new ArrayList<String>();
		regionIds2.add("mockedRegionId2");
		VraNgCloudAccount cloudAccount2 = accountBuilder2.setName("mockedAccountName").setRegionIds(regionIds2).build();

		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		cloudAccounts.add(cloudAccount1);
		cloudAccounts.add(cloudAccount2);

		StorageProfileMockBuilder smallStorageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile smallStorageProfile = smallStorageBuilder.setName("smallStorageMock")
				.setPropertyInRawData("id", "smallId").build();

		StorageProfileMockBuilder bigStorageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile bigStorageProfile = bigStorageBuilder.setName("bigStorageMock")
				.setPropertyInRawData("id", "bigId").build();

		Map<String, List<VraNgStorageProfile>> mockStorageProfilesByRegionId = new HashMap<String, List<VraNgStorageProfile>>();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(smallStorageProfile);
		storageProfiles.add(bigStorageProfile);
		mockStorageProfilesByRegionId.put("mockedRegionId1", storageProfiles);

		when(vraNgPackageDescriptor.getStorageProfile()).thenReturn(storageProfileNames);
		when(restClient.getAllStorageProfilesByRegion()).thenReturn(mockStorageProfilesByRegionId);
		when(restClient.getSpecificStorageProfile(anyString(), eq("smallId"))).thenReturn(smallStorageProfile);
		when(restClient.getSpecificStorageProfile(anyString(), eq("bigId"))).thenReturn(bigStorageProfile);
		when(restClient.getFabricEntityName(anyString())).thenReturn("mockedFabricEntity");

		// TEST
		store.exportContent(cloudAccounts);

		String[] expectedStorageProfilesFolder = { "mockedAccountName~mockedRegionId1" };
		String[] expectedStorageProfilesFiles = { "smallStorageMock.json" };
		String[] expectedProfileFiles = { "src-region-profile.json", "storage-profiles" };

		// VERIFY
		verify(restClient, times(1)).getAllStorageProfilesByRegion();
		// generated folder
		// regions/{cloudaccountname~regionId}/storage-profiles/{storage-profiles
		// name}.json
		File regionsFolder = new File(tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles(regionsFolder, expectedStorageProfilesFolder);

		File storageProfilesFolder = new File(
				regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/storage-profiles");
		AssertionsHelper.assertFolderContainsFiles(storageProfilesFolder, expectedStorageProfilesFiles);

		// regions/mockedAccountName~mockedRegionId1/src-region-profile.json
		File profileFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1");
		AssertionsHelper.assertFolderContainsFiles(profileFolder, expectedProfileFiles);
	}

	@Test
	void testStorageProfileWithoutDiskType() throws IOException {
		// GIVEN
		List<String> storageProfileNames = new ArrayList<>();
		storageProfileNames.add("noFirstClassDisk");
		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		CloudAccountMockBuilder builder = new CloudAccountMockBuilder();
		VraNgCloudAccount cloudAccount = builder.build();
		cloudAccounts.add(cloudAccount);

		StorageProfileMockBuilder storageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile noFcdStorageProfile = storageBuilder.setName("noFirstClassDisk").setPropertyInRawData("diskType", null).build();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(noFcdStorageProfile);

		when(vraNgPackageDescriptor.getStorageProfile()).thenReturn(storageProfileNames);
		when(restClient.getSpecificStorageProfile(anyString(), eq("noFirstClassDisk"))).thenReturn(noFcdStorageProfile);

		// TEST
		store.exportContent(cloudAccounts);

		// VERIFY
		verify(restClient, times(1)).getAllStorageProfilesByRegion();
	}
}
