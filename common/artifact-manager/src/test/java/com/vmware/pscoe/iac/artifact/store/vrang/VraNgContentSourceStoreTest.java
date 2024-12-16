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
package com.vmware.pscoe.iac.artifact.aria.store;

import com.vmware.pscoe.iac.artifact.aria.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ContentSourceBaseMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.aria.model.*;
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

public class  VraNgContentSourceStoreTest{

	@Rule
	public TemporaryFolder 				tempFolder		= new TemporaryFolder();
	protected static String PROJECT_ID			= "projectId";
	protected VraNgContentSourceStore 	store;
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
		store					= new VraNgContentSourceStore();
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
	void testExportContentWithNoContentSource() {
		//GIVEN
		ArrayList<String> data = new ArrayList<>();
		data.add("nothing");

		List<VraNgContentSourceBase>  mockedCatalogItems = new ArrayList<>();
		ContentSourceBaseMockBuilder builderOne = new ContentSourceBaseMockBuilder();
		VraNgContentSourceBase contentSourceOne = builderOne.setName("thing1").build();
		ContentSourceBaseMockBuilder builderTwo = new ContentSourceBaseMockBuilder();
		VraNgContentSourceBase contentSourceTwo = builderOne.setName("thing2").build();
		mockedCatalogItems.add( contentSourceOne );
		mockedCatalogItems.add( contentSourceTwo );

		when( vraNgPackageDescriptor.getContentSource()).thenReturn(data);
		when(restClient.getContentSourcesForProject(PROJECT_ID)).thenReturn(mockedCatalogItems);
		//TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());

		//VERIFY
		verify( restClient, never() ).getContentSourcesForProject(anyString());

		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentThatDoesNotExistOnTheServer() {
		//GIVEN 
		when( vraNgPackageDescriptor.getContentSource()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify( restClient, never() ).getContentSourcesForProject(anyString());

		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithNullValue() {
		
		when( vraNgPackageDescriptor.getContentSource()).thenReturn(null);
		//TEST
		assertDoesNotThrow(() -> store.exportContent());

	}

	//TODO not easy to implement the test because the VraNgContentSourceBase is too complext to be mocked. VraNgContentSourceBase is casted dynamically to other classes 
	// @Test
	// void testExportContentWithAllContentSources() {
	// 		//GIVEN
	// 	when( restClient.getProjectId()).thenReturn("mockedProjectId");
	// 	VraNgContentSourceStore 	store1 = new VraNgContentSourceStore();
	// 	store1.init( restClient, pkg, config, vraNgPackageDescriptor );

	// 	List<VraNgContentSourceBase> contentSources = new ArrayList<>();
	// 	ContentSourceBaseMockBuilder builder = new ContentSourceBaseMockBuilder();

	// 	contentSources.add( builder.setName("mockContentSource1").build());

	// 	when( vraNgPackageDescriptor.getContentSource()).thenReturn(null);
	// 	when( restClient.getContentSourcesForProject("mockedProjectId")).thenReturn( contentSources );

	// 	//TEST
	// 	store1.exportContent();

	// 	String[] expectedContentSources	= { "mockContentSource1.json" };

	// 	// VERIFY
	// 	verify(restClient, times(1)).getContentSourcesForProject("mockedProjectId");
	// 	AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedContentSources );
	// }

	// @Test
	// void testExportContentWithSpecificContentSources() {}

	
}
