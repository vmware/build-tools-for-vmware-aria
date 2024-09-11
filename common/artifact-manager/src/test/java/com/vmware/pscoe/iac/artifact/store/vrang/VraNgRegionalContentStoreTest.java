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
import com.vmware.pscoe.iac.artifact.helpers.stubs.FlavorMappingMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ImageMappingMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.RegionMappingMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.StorageProfileMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgImageMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgRegionMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgStorageProfile;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VraNgRegionalContentStoreTest {
	@Rule
	public TemporaryFolder tempFolder	= new TemporaryFolder();

	protected VraNgRegionalContentStore store;
	protected RestClientVraNg restClient;
	protected Package pkg;
	protected ConfigurationVraNg config;
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		}
		catch ( IOException e ) {
			throw new RuntimeException( "Could not create a temp folder" );
		}

		fsMocks					= new FsMocks( tempFolder.getRoot() );
		store					= new VraNgRegionalContentStore();
		restClient				= Mockito.mock( RestClientVraNg.class );
		pkg						= PackageFactory.getInstance( PackageType.VRANG, tempFolder.getRoot() );
		config					= Mockito.mock( ConfigurationVraNg.class );
		vraNgPackageDescriptor	= Mockito.mock( VraNgPackageDescriptor.class );

		store.init( restClient, pkg, config, vraNgPackageDescriptor );
		System.out.println( "==========================================================" );
		System.out.println( "START" );
		System.out.println( "==========================================================" );
	}

	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println( "==========================================================" );
		System.out.println( "END" );
		System.out.println( "==========================================================" );
	}

	@Test
	void testExportContentWithNoRegionMapping() {
		//GIVEN
		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(null);

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, never()).getCloudAccounts();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithRegionMappingButWithNoExportTag() {
		//GIVEN
		RegionMappingMockBuilder builder = new RegionMappingMockBuilder();
		VraNgRegionMapping mockedRegionalMapping = builder.setExportTag(null).build();
		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(mockedRegionalMapping);

		CloudAccountMockBuilder cloudAccountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> mockedCloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> tags = new ArrayList<String>();
		tags.add("env:dev");
		mockedCloudAccounts.add(cloudAccountBuilder.setTags(tags).build());

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, times(1)).getCloudAccounts();

		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}
	
	@Test
	void testExportContentWithRegionMappingButWithEmptyExportTag() {
		//GIVEN
		RegionMappingMockBuilder regionsBuilder = new RegionMappingMockBuilder();
		VraNgRegionMapping mockedRegionalMapping = Mockito.spy(regionsBuilder.setExportTag("").build());
		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(mockedRegionalMapping);

		CloudAccountMockBuilder cloudAccountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> mockedCloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> tags = new ArrayList<String>();
		tags.add("env:dev");
		mockedCloudAccounts.add(cloudAccountBuilder.setTags(tags).build());

		when(restClient.getCloudAccounts()).thenReturn(mockedCloudAccounts);

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, times(1)).getCloudAccounts();
		verify(mockedRegionalMapping, times(1)).getCloudAccountTags();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithRegionMappingButWithExportTagButNoCloudAccountsMapped() {
		//GIVEN
		RegionMappingMockBuilder builder = new RegionMappingMockBuilder();
		VraNgRegionMapping mockedRegionalMapping = Mockito.spy(builder.setExportTag("env:dev").build());

		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(mockedRegionalMapping);

		CloudAccountMockBuilder cloudAccountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> mockedCloudAccounts = new ArrayList<VraNgCloudAccount>();
		mockedCloudAccounts.add(cloudAccountBuilder.build());

		when(restClient.getCloudAccounts()).thenReturn(mockedCloudAccounts);

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, times(1)).getCloudAccounts();
		verify(mockedRegionalMapping, times(1)).getCloudAccountTags();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}



	@Test
	void testExportContentWithRegionMappingWithExportTagAndWithCloudAccountsMapped() throws IOException{
		//GIVEN

		//RegionMappping
		RegionMappingMockBuilder builder = new RegionMappingMockBuilder();
		VraNgRegionMapping mockedRegionalMapping = Mockito.spy(builder.setExportTag("env:dev").build());

		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(mockedRegionalMapping);

		//CloudAccounts
		CloudAccountMockBuilder cloudAccountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> mockedCloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> tags = new ArrayList<String>();
		tags.add("env:dev");
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		mockedCloudAccounts.add(cloudAccountBuilder.setName("mockedAccountName").setTags(tags).setRegionIds(regionIds).build());

		when(restClient.getCloudAccounts()).thenReturn(mockedCloudAccounts);

		//FlavorMapping
		FlavorMappingMockBuilder flavorBuilder = new FlavorMappingMockBuilder();
		flavorBuilder.setName("smallMock");

		Map<String, List<VraNgFlavorMapping>> mockedFlavorsByRegion = new HashMap<String, List<VraNgFlavorMapping>>();
		List<VraNgFlavorMapping> flavorMappings = new ArrayList<VraNgFlavorMapping>();
		flavorMappings.add(flavorBuilder.build());
		mockedFlavorsByRegion.put("mockedRegionId1", flavorMappings);

		List<String> flavorMappingNames = new ArrayList<>();
		flavorMappingNames.add("smallMock");

		when(restClient.getAllFlavorMappingsByRegion()).thenReturn(mockedFlavorsByRegion);
		when(vraNgPackageDescriptor.getFlavorMapping()).thenReturn(flavorMappingNames);


		//ImageMapping
		ImageMappingMockBuilder imageBuilder = new ImageMappingMockBuilder();
		imageBuilder.setName("Ubuntu");

		Map<String, List<VraNgImageMapping>> mockedImagesByRegion = new HashMap<String, List<VraNgImageMapping>>();
		List<VraNgImageMapping> imageMappings = new ArrayList<VraNgImageMapping>();
		imageMappings.add(imageBuilder.build());
		mockedImagesByRegion.put("mockedRegionId1", imageMappings);

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(null);
		when(restClient.getAllImageMappingsByRegion()).thenReturn(mockedImagesByRegion);

		//StorageProfile
		StorageProfileMockBuilder storageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile storageProfile = storageBuilder.setName("smallStorageMock").build();

		Map<String, List<VraNgStorageProfile>> mockStorageProfilesByRegionId = new HashMap<String, List<VraNgStorageProfile>>();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(storageProfile);
		mockStorageProfilesByRegionId.put("mockedRegionId1", storageProfiles);


		when( vraNgPackageDescriptor.getStorageProfile()).thenReturn(null);
		when(restClient.getAllStorageProfilesByRegion()).thenReturn(mockStorageProfilesByRegionId);
		when(restClient.getSpecificStorageProfile( anyString(), anyString())).thenReturn(storageProfile);
		when(restClient.getFabricEntityName( anyString() )).thenReturn("mockedFabricEntity");


		//TEST
		store.exportContent();
		

		//VERIFY
		verify(restClient, times(1)).getCloudAccounts();
		verify(restClient, times(1)).getAllFlavorMappingsByRegion();
		verify(mockedRegionalMapping, times(1)).getCloudAccountTags();
		assertEquals(mockedRegionalMapping.getCloudAccountTags().getExportTag(), "env:dev");
		
		File regionsFolder = new File (tempFolder.getRoot().getPath() + "/regions");

		// VERIFY Flavor
		String[] expectedFlavorMappingFolder	= { "mockedAccountName~mockedRegionId1"  };
		String[] expectedFlavorMappingFiles	    = { "smallMock.json" };
		verify(restClient, times( 1 ) ).getAllFlavorMappingsByRegion();
		// generated folder regions/{cloudaccountname~regionId}/flavor-mapping/{flavormapping name}.json

		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedFlavorMappingFolder  );

		File flavorMappingFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/flavor-mappings" );
		AssertionsHelper.assertFolderContainsFiles( flavorMappingFolder, expectedFlavorMappingFiles );

		//VERIFY Image
		String[] expectedImageMappingFolder		= { "mockedAccountName~mockedRegionId1"  };
		String[] expectedImageMappingFiles	    = { "Ubuntu.json" };

		verify(restClient, times( 1 ) ).getAllImageMappingsByRegion();
		// generated folder regions/{cloudaccountname~regionId}/image-mapping/{imagemapping name}.json
		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedImageMappingFolder  );

		File imageMappingFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/image-mappings" );
		AssertionsHelper.assertFolderContainsFiles( imageMappingFolder, expectedImageMappingFiles );

		// VERIFY Storage
		String[] expectedStorageProfilesFolder	= { "mockedAccountName~mockedRegionId1"  };
		String[] expectedStorageProfilesFiles	    = { "smallStorageMock.json" };
		String[] expectedProfileFiles = { "src-region-profile.json", "storage-profiles", "image-mappings", "flavor-mappings" };

		verify(restClient, times( 1 ) ).getAllStorageProfilesByRegion();
		// generated folder regions/{cloudaccountname~regionId}/storage-profiles/{storage-profiles name}.json
		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedStorageProfilesFolder  );

		File storageProfilesFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/storage-profiles" );
		AssertionsHelper.assertFolderContainsFiles( storageProfilesFolder, expectedStorageProfilesFiles );

		// regions/mockedAccountName~mockedRegionId1/src-region-profile.json 
		File profileFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1" );
		AssertionsHelper.assertFolderContainsFiles( profileFolder, expectedProfileFiles );
	}

	@Test
	void testExportContentWithRegionMappingWithExportTagAndWithCloudAccountsMappedButFilterOnEmptyItems() throws IOException{
		//GIVEN

		//RegionMappping
		RegionMappingMockBuilder builder = new RegionMappingMockBuilder();
		VraNgRegionMapping mockedRegionalMapping = Mockito.spy(builder.setExportTag("env:dev").build());

		when(vraNgPackageDescriptor.getRegionMapping()).thenReturn(mockedRegionalMapping);

		//CloudAccounts
		CloudAccountMockBuilder cloudAccountBuilder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> mockedCloudAccounts = new ArrayList<VraNgCloudAccount>();
		List<String> tags = new ArrayList<String>();
		tags.add("env:dev");
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		mockedCloudAccounts.add(cloudAccountBuilder.setName("mockedAccountName").setTags(tags).setRegionIds(regionIds).build());

		when(restClient.getCloudAccounts()).thenReturn(mockedCloudAccounts);

		//FlavorMapping
		FlavorMappingMockBuilder flavorBuilder = new FlavorMappingMockBuilder();
		flavorBuilder.setName("smallMock");

		Map<String, List<VraNgFlavorMapping>> mockedFlavorsByRegion = new HashMap<String, List<VraNgFlavorMapping>>();
		List<VraNgFlavorMapping> flavorMappings = new ArrayList<VraNgFlavorMapping>();
		flavorMappings.add(flavorBuilder.build());
		mockedFlavorsByRegion.put("mockedRegionId1", flavorMappings);


		when(restClient.getAllFlavorMappingsByRegion()).thenReturn(mockedFlavorsByRegion);
		when(vraNgPackageDescriptor.getFlavorMapping()).thenReturn(new ArrayList<>());


		//ImageMapping
		ImageMappingMockBuilder imageBuilder = new ImageMappingMockBuilder();
		imageBuilder.setName("Ubuntu");

		Map<String, List<VraNgImageMapping>> mockedImagesByRegion = new HashMap<String, List<VraNgImageMapping>>();
		List<VraNgImageMapping> imageMappings = new ArrayList<VraNgImageMapping>();
		imageMappings.add(imageBuilder.build());
		mockedImagesByRegion.put("mockedRegionId1", imageMappings);

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(new ArrayList<>());
		when(restClient.getAllImageMappingsByRegion()).thenReturn(mockedImagesByRegion);

		//StorageProfile
		StorageProfileMockBuilder storageBuilder = new StorageProfileMockBuilder();
		VraNgStorageProfile storageProfile = storageBuilder.setName("smallStorageMock").build();

		Map<String, List<VraNgStorageProfile>> mockStorageProfilesByRegionId = new HashMap<String, List<VraNgStorageProfile>>();
		List<VraNgStorageProfile> storageProfiles = new ArrayList<VraNgStorageProfile>();
		storageProfiles.add(storageProfile);
		mockStorageProfilesByRegionId.put("mockedRegionId1", storageProfiles);


		when( vraNgPackageDescriptor.getStorageProfile()).thenReturn(new ArrayList<>());
		when(restClient.getAllStorageProfilesByRegion()).thenReturn(mockStorageProfilesByRegionId);
		when(restClient.getSpecificStorageProfile( anyString(), anyString())).thenReturn(storageProfile);
		when(restClient.getFabricEntityName( anyString() )).thenReturn("mockedFabricEntity");


		//TEST
		store.exportContent();
		

		//VERIFY
		verify(restClient, times(1)).getCloudAccounts();
		verify(restClient, never()).getAllFlavorMappingsByRegion();
		verify(mockedRegionalMapping, times(1)).getCloudAccountTags();
		assertEquals(mockedRegionalMapping.getCloudAccountTags().getExportTag(), "env:dev");
		
		verify(restClient, never() ).getAllFlavorMappingsByRegion();
		verify(restClient, never() ).getAllImageMappingsByRegion();
		verify(restClient, never() ).getAllStorageProfilesByRegion();

		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

}
