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
import com.vmware.pscoe.iac.artifact.helpers.stubs.CloudAccountMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ImageMappingMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.rest.RestClient;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VraNgImageMappingStoreTest {
	@Rule
	public TemporaryFolder 				tempFolder		= new TemporaryFolder();

	protected VraNgImageMappingStore	store;
	protected RestClientVraNg 			restClient;
	protected Package 					pkg;
	protected ConfigurationVraNg 		config;
	protected VraNgPackageDescriptor 	vraNgPackageDescriptor;
	protected FsMocks 					fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		}
		catch ( IOException e ) {
			throw new RuntimeException( "Could not create a temp folder" );
		}

		fsMocks					= new FsMocks( tempFolder.getRoot() );
		store					= new VraNgImageMappingStore();
		restClient				= Mockito.mock( RestClientVraNg.class );
		pkg						= PackageFactory.getInstance( PackageType.VRANG, tempFolder.getRoot() );
		vraNgPackageDescriptor	= Mockito.mock( VraNgPackageDescriptor.class );

		store.init( restClient, pkg, vraNgPackageDescriptor );
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
	void testExportContentWithNoImageMapping(){
		//GIVEN
		CloudAccountMockBuilder builder = new CloudAccountMockBuilder();
		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		cloudAccounts.add(builder.build());

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent(cloudAccounts);

		//VERIFY
		verify(restClient, never()).getAllImageMappingsByRegion();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithAllImageMapping() throws IOException{
		//GIVEN
		CloudAccountMockBuilder accountBuilder = new CloudAccountMockBuilder();
		
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		VraNgCloudAccount cloudAccount = accountBuilder.setName("mockedAccountName").setRegionIds(regionIds).build();

		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		cloudAccounts.add(cloudAccount);

		ImageMappingMockBuilder mappingBuilder = new ImageMappingMockBuilder();
		mappingBuilder.setName("Ubuntu");

		Map<String, List<VraNgImageMapping>> mockedImagesByRegion = new HashMap<String, List<VraNgImageMapping>>();
		List<VraNgImageMapping> imageMappings = new ArrayList<VraNgImageMapping>();
		imageMappings.add(mappingBuilder.build());
		mockedImagesByRegion.put("mockedRegionId1", imageMappings);

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(null);
		when(restClient.getAllImageMappingsByRegion()).thenReturn(mockedImagesByRegion);

		//TEST
		store.exportContent(cloudAccounts);

		String[] expectedImageMappingFolder	= { "mockedAccountName~mockedRegionId1"  };
		String[] expectedImageMappingFiles	    = { "Ubuntu.json" };

		// VERIFY
		verify(restClient, times( 1 ) ).getAllImageMappingsByRegion();
		// generated folder regions/{cloudaccountname~regionId}/image-mapping/{imagemapping name}.json
		File regionsFolder = new File (tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedImageMappingFolder  );

		File imageMappingFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/image-mappings" );
		AssertionsHelper.assertFolderContainsFiles( imageMappingFolder, expectedImageMappingFiles );
	}

	@Test
	void testExportContentWithSpecificFlavorMapping() throws IOException{
		//GIVEN
		CloudAccountMockBuilder accountBuilder = new CloudAccountMockBuilder();
		
		List<String> regionIds = new ArrayList<String>();
		regionIds.add("mockedRegionId1");
		VraNgCloudAccount cloudAccount = accountBuilder.setName("mockedAccountName").setRegionIds(regionIds).build();

		List<VraNgCloudAccount> cloudAccounts = new ArrayList<VraNgCloudAccount>();
		cloudAccounts.add(cloudAccount);

		ImageMappingMockBuilder ubuntuMappingBuilder = new ImageMappingMockBuilder();
		ubuntuMappingBuilder.setName("Ubuntu");

		ImageMappingMockBuilder citrixMappingBuilder = new ImageMappingMockBuilder();
		citrixMappingBuilder.setName("Citrix");

		Map<String, List<VraNgImageMapping>> mockedImagesByRegion = new HashMap<String, List<VraNgImageMapping>>();
		List<VraNgImageMapping> imageMappings = new ArrayList<VraNgImageMapping>();
		imageMappings.add(ubuntuMappingBuilder.build());
		imageMappings.add(citrixMappingBuilder.build());

		mockedImagesByRegion.put("mockedRegionId1", imageMappings);

		List<String> images = new ArrayList<String>();
		images.add("Ubuntu");

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(images);
		when(restClient.getAllImageMappingsByRegion()).thenReturn(mockedImagesByRegion);

		//TEST
		store.exportContent(cloudAccounts);

		String[] expectedImageMappingFolder		= { "mockedAccountName~mockedRegionId1"  };
		String[] expectedImageMappingFiles	    = { "Ubuntu.json" };

		// VERIFY
		verify(restClient, times( 1 ) ).getAllImageMappingsByRegion();
		// generated folder regions/{cloudaccountname~regionId}/image-mapping/{imagemapping name}.json
		File regionsFolder = new File (tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedImageMappingFolder  );

		File imageMappingFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/image-mappings" );
		AssertionsHelper.assertFolderContainsFiles( imageMappingFolder, expectedImageMappingFiles );
	}

	@Test
	void testExportContentWithSpecificFlavorMappingFromSpecificCloudAccount() throws IOException{
		//GIVEN
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

		ImageMappingMockBuilder ubuntuMappingBuilder = new ImageMappingMockBuilder();
		ubuntuMappingBuilder.setName("Ubuntu");

		ImageMappingMockBuilder citrixMappingBuilder = new ImageMappingMockBuilder();
		citrixMappingBuilder.setName("Citrix");


		Map<String, List<VraNgImageMapping>> mockedImagesByRegion = new HashMap<String, List<VraNgImageMapping>>();
		List<VraNgImageMapping> imageMappings = new ArrayList<VraNgImageMapping>();
		imageMappings.add(ubuntuMappingBuilder.build());
		imageMappings.add(citrixMappingBuilder.build());

		mockedImagesByRegion.put("mockedRegionId1", imageMappings);

		List<String> images = new ArrayList<String>();
		images.add("Ubuntu");

		when( vraNgPackageDescriptor.getImageMapping()).thenReturn(images);
		when(restClient.getAllImageMappingsByRegion()).thenReturn(mockedImagesByRegion);

		//TEST
		store.exportContent(cloudAccounts);

		String[] expectedImageMappingFolder	    = { "mockedAccountName~mockedRegionId1"  };
		String[] expectedImageMappingFiles	    = { "Ubuntu.json" };

		// VERIFY
		verify(restClient, times( 1 ) ).getAllImageMappingsByRegion();
		// generated folder regions/{cloudaccountname~regionId}/image-mapping/{imagemapping name}.json
		File regionsFolder = new File (tempFolder.getRoot().getPath() + "/regions");
		AssertionsHelper.assertFolderContainsFiles( regionsFolder, expectedImageMappingFolder  );

		File imageMappingFolder = new File(regionsFolder.getPath() + "/mockedAccountName~mockedRegionId1/image-mappings" );
		AssertionsHelper.assertFolderContainsFiles( imageMappingFolder, expectedImageMappingFiles );
	}
}
