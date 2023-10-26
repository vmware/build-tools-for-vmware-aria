package com.vmware.pscoe.iac.artifact.store.vrang;

import com.google.gson.JsonArray;

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
import com.vmware.pscoe.iac.artifact.helpers.GeneralMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CatalogItemMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.ContentSourceBaseMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CustomFormMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
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

/**
 * NOTE: This does not test duplicate names from one content source, since the Store is not responsible for that kind of logic.
 */
public class VraNgCatalogItemStoreTest {
	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	/**
	 * store.
	 */
	protected VraNgCatalogItemStore store;
	/**
	 * restClient.
	 */
	protected RestClientVraNg restClient;
	/**
	 * pkg.
	 */
	protected Package pkg;
	/**
	 * config.
	 */
	protected ConfigurationVraNg config;
	/**
	 * vraNgPackageDescriptor.
	 */
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	/**
	 * fsMocks.
	 */
	protected FsMocks fsMocks;
	/**
	 * Example project id.
	 */
	protected static String PROJECT_ID = "projectId";
	/**
	 * Mocked source id.
	 */
	protected static String CONTENT_SOURCE_ID = "mockedSourceId";
	/**
	 * Mocked catalog item id.
	 */
	protected static String CATALOG_ITEM_ID	= "mockedItemId";

	/**
	 * Init method called before each test.
	 */
	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		}
		catch ( IOException e ) {
			throw new RuntimeException( "Could not create a temp folder" );
		}

		fsMocks = new FsMocks( tempFolder.getRoot() );
		store = new VraNgCatalogItemStore();
		restClient = Mockito.mock( RestClientVraNg.class );
		pkg 	= PackageFactory.getInstance( PackageType.VRANG, tempFolder.getRoot() );
		config = Mockito.mock( ConfigurationVraNg.class );
		vraNgPackageDescriptor	= Mockito.mock( VraNgPackageDescriptor.class );

		store.init( restClient, pkg, config, vraNgPackageDescriptor );
		System.out.println( "==========================================================" );
		System.out.println( "START" );
		System.out.println( "==========================================================" );
	}

	/**
	 * Cleanup method called after each test.
	 */
	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println( "==========================================================" );
		System.out.println( "END" );
		System.out.println( "==========================================================" );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenNoItems() {
		// GIVEN
		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( new ArrayList<>() );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, never() ).getContentSource( anyString() );
		verify( restClient, never() ).getCustomFormByTypeAndSource( anyString(), anyString() );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromOneSource() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder 		= new ContentSourceBaseMockBuilder();
		String contentSourceType 			= VraNgContentSourceType.VRO_WORKFLOW.toString();

		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );

		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );
		assertEquals( 2, fsMocks.getTempFolderProjectPath().listFiles().length );

		String[] expectedFiles	= { "contentSourceNameOne__catalogItemOne.json", "contentSourceNameOne__catalogItemTwo.json" };

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFiles );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentButFilteringIsEmpty() {
		// GIVEN
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( new ArrayList<>() );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, never() ).getCatalogItemsForProject( anyString() );
		verify( restClient, never() ).getProjectId();
		verify( restClient, never() ).getContentSource( anyString() );
		verify( restClient, never() ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithSomeCatalogItemsWhenItemsArePresentFromOneSource() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder 		= new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<String> mockCatalogItemNames			= new ArrayList<>();
		mockCatalogItemNames.add( "contentSourceNameOne__catalogItemOne" );

		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( mockCatalogItemNames );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getContentSource( anyString() );
		verify( restClient, times( 1 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );
		assertEquals( fsMocks.getTempFolderProjectPath().listFiles().length, 1 );

		String[] expectedFiles	= { "contentSourceNameOne__catalogItemOne.json" };

		assertArrayEquals( fsMocks.getTempFolderProjectPath().list(), expectedFiles );
	}

	@Test
	void testExportContentWithSomeCatalogItemsWhenItemNameHasIncorrectFormat() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();

		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add( "wrongFormat" );

		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( mockCatalogItemNames );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );


		// START TEST
		assertThrows( RuntimeException.class, () -> { store.exportContent(); } );

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, never() ).getContentSource( anyString() );
		verify( restClient, never() ).getCustomFormByTypeAndSource( anyString(), anyString() );
	}

	@Test
	void testExportContentWithSomeCatalogItemsWhenContentSourceDoesNotExist() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add( "contentSourceNameOne__catalogItemOne" );

		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( mockCatalogItemNames );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( null );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );


		// START TEST
		assertThrows( RuntimeException.class, () -> { store.exportContent(); } );

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getContentSource( anyString() );
		verify( restClient, never() ).getCustomFormByTypeAndSource( anyString(), anyString() );
	}
	@Test
	void testExportContentWithNoNExistingCatalogItems() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add( "contentSourceNameOne__nonExisting" );

		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder builderOne 			= new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		CatalogItemMockBuilder builderTwo			= new CatalogItemMockBuilder( "catalogItemTwo", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = builderOne.build();
		VraNgCatalogItem catalogItemTwo = builderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( mockCatalogItemNames );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( null );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		assertThrows( IllegalStateException.class, () -> { store.exportContent(); } );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromOneSourceWithForm() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();

		String formIdOne 		= "formIdCatalogItemOne";
		String formIdTwo 		= "formIdCatalogItemTwo";
		CustomFormMockBuilder builderOne			= new CustomFormMockBuilder("nameOne");
		CustomFormMockBuilder builderTwo			= new CustomFormMockBuilder("nameTwo");
		VraNgCustomForm mockedFormOne = builderOne.setId(formIdOne).build();
		VraNgCustomForm mockedFormTwo = builderTwo.setId(formIdTwo).build();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();

		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.setCustomFormId( formIdOne ).build();

		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemTwo", "contentSourceNameOne");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.setCustomFormId( formIdTwo ).build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when(
			restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID )
		).thenReturn( mockedFormOne, mockedFormTwo );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );

		String[] expectedFilesAndFolders	= {
			"contentSourceNameOne__catalogItemOne.json",
			"forms",
			"contentSourceNameOne__catalogItemTwo.json"
		};

		String[] expectedFormNames			= {
			"contentSourceNameOne__catalogItemOne.json",
			"contentSourceNameOne__catalogItemOne__FormData.json",
			"contentSourceNameOne__catalogItemTwo.json",
			"contentSourceNameOne__catalogItemTwo__FormData.json"
		};

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFilesAndFolders );

		for ( File file: fsMocks.getTempFolderProjectPath().listFiles() ) {
			if ( file.getName().equalsIgnoreCase( "forms" ) ){
 AssertionsHelper.assertFolderContainsFiles( file, expectedFormNames );
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromOneSourceWithFormAndIcon() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();

		String iconIdOne 		= "iconIdCatalogItemOne";
		String formIdOne 		= "formIdCatalogItemOne";
		String formIdTwo 		= "formIdCatalogItemTwo";
		String iconIdTwo 		= "iconIdCatalogItemTwo";
		Map<String, String> headers = new HashMap<>();
		headers.put( "Content-Type", "image/png" );

		ResponseEntity<byte[]> iconOneResponse		= GeneralMocks.mockResponseEntity(
			"iconBodyOne".getBytes(),
			200,
			headers
		);
		ResponseEntity<byte[]> iconTwoResponse		= GeneralMocks.mockResponseEntity(
			"iconBodyTwo".getBytes(),
			200,
			headers
		);

		CustomFormMockBuilder builderOne			= new CustomFormMockBuilder("nameOne");
		CustomFormMockBuilder builderTwo			= new CustomFormMockBuilder("nameTwo");
		VraNgCustomForm mockedFormOne = builderOne.setId(formIdOne).build();
		VraNgCustomForm mockedFormTwo = builderTwo.setId(formIdTwo).build();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		
		CatalogItemMockBuilder catalogBuilderOne    = new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.setIconId(iconIdOne).setIconExt("png").setCustomFormId(formIdOne).build();
		
		CatalogItemMockBuilder catalogBuilderTwo	= new CatalogItemMockBuilder("catalogItemTwo", "contentSourceNameOne");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.setIconId(iconIdTwo).setIconExt("png").setCustomFormId(formIdTwo).build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( restClient.downloadIcon( anyString() ) ).thenReturn( iconOneResponse, iconTwoResponse );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when(
			restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID )
		).thenReturn( mockedFormOne, mockedFormTwo );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );

		String[] expectedFilesAndFolders	= {
			"contentSourceNameOne__catalogItemOne.json",
			"forms",
			"icons",
			"contentSourceNameOne__catalogItemTwo.json"
		};

		String[] expectedFormNames			= {
			"contentSourceNameOne__catalogItemOne.json",
	        "contentSourceNameOne__catalogItemOne__FormData.json",
			"contentSourceNameOne__catalogItemTwo.json",
	        "contentSourceNameOne__catalogItemTwo__FormData.json",
		};

		String[] expectedIconNames			= {
			"contentSourceNameOne__catalogItemOne.png",
			"contentSourceNameOne__catalogItemTwo.png"
		};

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFilesAndFolders );

		AssertionsHelper.assertFolderContainsFiles(
			fsMocks.findItemByNameInFolder( fsMocks.getTempFolderProjectPath(), "forms" ),
			expectedFormNames
		);

		AssertionsHelper.assertFolderContainsFiles(
			fsMocks.findItemByNameInFolder( fsMocks.getTempFolderProjectPath(), "icons" ),
			expectedIconNames
		);
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromOneSourceButIconDoesNotExist() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();

		String iconIdOne 		= "iconIdCatalogItemOne";
		String formIdOne 		= "formIdCatalogItemOne";
		String formIdTwo 		= "formIdCatalogItemTwo";
		String iconIdTwo 		= "iconIdCatalogItemTwo";

		ResponseEntity<byte[]> iconOneResponse		= GeneralMocks.mockResponseEntity(
			"iconBodyOne".getBytes(),
			500
		);
		CustomFormMockBuilder builderOne			= new CustomFormMockBuilder("nameOne");
		CustomFormMockBuilder builderTwo			= new CustomFormMockBuilder("nameTwo");
		VraNgCustomForm mockedFormOne = builderOne.setId(formIdOne).build();
		VraNgCustomForm mockedFormTwo = builderTwo.setId(formIdTwo).build();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();

		CatalogItemMockBuilder catalogBuilderOne 	= new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.setIconId(iconIdOne).setIconExt("png").setCustomFormId(formIdOne).build();
		
		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemTwo", "contentSourceNameOne");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.setIconId(iconIdTwo).setIconExt("png").setCustomFormId(formIdTwo).build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( restClient.downloadIcon( anyString() ) ).thenReturn( iconOneResponse );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when(
			restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID )
		).thenReturn( mockedFormOne, mockedFormTwo );

		// START TEST
		assertThrows( RuntimeException.class, () -> store.exportContent() );

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getContentSource( anyString() );
		verify( restClient, times( 1 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromOneSourceWithFormAndSameIcon() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();

		String iconId 			= "iconId";
		String formIdOne 		= "formIdCatalogItemOne";
		String formIdTwo 		= "formIdCatalogItemTwo";
		Map<String, String> headers = new HashMap<>();
		headers.put( "Content-Type", "image/png" );

		ResponseEntity<byte[]> iconOneResponse		= GeneralMocks.mockResponseEntity(
			"iconBody".getBytes(),
			200,
			headers
		);
		CustomFormMockBuilder builderOne			= new CustomFormMockBuilder("nameOne");
		CustomFormMockBuilder builderTwo			= new CustomFormMockBuilder("nameTwo");
		VraNgCustomForm mockedFormOne = builderOne.setId(formIdOne).build();
		VraNgCustomForm mockedFormTwo = builderTwo.setId(formIdTwo).build();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder catalogBuilderOne 	= new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.setIconId(iconId).setIconExt("png").setCustomFormId(formIdOne).build();
		
		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemTwo", "contentSourceNameOne");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.setIconId(iconId).setIconExt("png").setCustomFormId(formIdTwo).build();


		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( restClient.downloadIcon( anyString() ) ).thenReturn( iconOneResponse );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when(
			restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID )
		).thenReturn( mockedFormOne, mockedFormTwo );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );

		String[] expectedFilesAndFolders	= {
			"contentSourceNameOne__catalogItemOne.json",
			"forms",
			"icons",
			"contentSourceNameOne__catalogItemTwo.json"
		};

		String[] expectedFormNames			= {
			"contentSourceNameOne__catalogItemOne.json",
            "contentSourceNameOne__catalogItemOne__FormData.json",
			"contentSourceNameOne__catalogItemTwo.json",
            "contentSourceNameOne__catalogItemTwo__FormData.json"
		};

		String[] expectedIconNames			= {
			"contentSourceNameOne__catalogItemOne.png",
			"contentSourceNameOne__catalogItemTwo.png",
		};

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFilesAndFolders );

		AssertionsHelper.assertFolderContainsFiles(
			fsMocks.findItemByNameInFolder( fsMocks.getTempFolderProjectPath(), "forms" ),
			expectedFormNames
		);

		AssertionsHelper.assertFolderContainsFiles(
			fsMocks.findItemByNameInFolder( fsMocks.getTempFolderProjectPath(), "icons" ),
			expectedIconNames
		);
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromTwoSources() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();

		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.build();

		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemTwo", "contentSourceNameTwo");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );
		assertEquals( 2, fsMocks.getTempFolderProjectPath().listFiles().length );

		String[] expectedFiles	= { "contentSourceNameOne__catalogItemOne.json", "contentSourceNameTwo__catalogItemTwo.json" };

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFiles );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromTwoSourcesWithSameName() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();
		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();

		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.build();

		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameTwo");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );
		assertEquals( 2, fsMocks.getTempFolderProjectPath().listFiles().length );

		String[] expectedFiles	= { "contentSourceNameOne__catalogItemOne.json", "contentSourceNameTwo__catalogItemOne.json" };

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFiles );
	}

	@Test
	void testExportContentWithAllCatalogItemsWhenItemsArePresentFromSameSourceWithSameNames() {
		// GIVEN
		ContentSourceBaseMockBuilder contentSourceBuilder = new ContentSourceBaseMockBuilder();

		String contentSourceType = VraNgContentSourceType.VRO_WORKFLOW.toString();
		VraNgContentSourceBase contentSource = spy(contentSourceBuilder.setTypeId(contentSourceType).build());
		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItemOne = catalogBuilderOne.build();

		CatalogItemMockBuilder catalogBuilderTwo = new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItem catalogItemTwo = catalogBuilderTwo.build();

		mockedCatalogItems.add( catalogItemOne );
		mockedCatalogItems.add( catalogItemTwo );

		when( vraNgPackageDescriptor.getCatalogItem() ).thenReturn( null );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getContentSource( CONTENT_SOURCE_ID ) ).thenReturn( contentSource );
		when( contentSource.getType() ).thenReturn( VraNgContentSourceType.VRO_WORKFLOW );
		when( restClient.getCustomFormByTypeAndSource( contentSourceType, CATALOG_ITEM_ID ) ).thenReturn( null );

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCatalogItem();
		verify( restClient, times( 1 ) ).getCatalogItemsForProject( anyString() );
		verify( restClient, times( 1 ) ).getProjectId();
		verify( restClient, times( 2 ) ).getContentSource( anyString() );
		verify( restClient, times( 2 ) ).getCustomFormByTypeAndSource( anyString(), anyString() );
		assertEquals( 1, tempFolder.getRoot().listFiles().length );
		assertEquals( fsMocks.getTempFolderProjectPath().listFiles().length, 1 );

		// Gets overwritten by the last one
		String[] expectedFiles	= { "contentSourceNameOne__catalogItemOne.json" };

		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFiles );
	}

	@Test
	void testImportContentWithNoIconsOrForms() {
		// GIVEN
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItem = catalogBuilderOne.build();
		fsMocks.catalogItemFsMocks().addCatalogItem( catalogItem );

		List<VraNgCatalogItem>  mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add( catalogItem );

		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );

		// START TEST
		store.importContent( tempFolder.getRoot() );

		// VERIFY
		verify( restClient, never() ).importCustomForm( any(), anyString() );
		verify( restClient, never() ).uploadIcon( any() );
	}
	

	@Test
	void testImportContentWithAnIconAndNoForms() {
		// GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSourceNameOne__catalogItemOne");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		String iconId = "iconId";
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItem = catalogBuilderOne.setIconId(iconId).setIconExt("png").build();

		Map<String, String> headers = new HashMap<>();
		headers.put( "Location", "/some/path/iconId" );
		ResponseEntity<String> response	= GeneralMocks.mockResponseEntity( "", 201, headers );

		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add( catalogItem );

		fsMocks.catalogItemFsMocks().addCatalogItem( catalogItem );
		fsMocks.catalogItemFsMocks().addCatalogItemIcon( catalogItem );
		when( restClient.getCatalogItemByBlueprintName( anyString() ) ).thenReturn( catalogItem );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.uploadIcon( any() ) ).thenReturn( response );

		// START TEST
		store.importContent( tempFolder.getRoot() );

		// VERIFY
		verify( restClient, never() ).importCustomForm( any(), anyString() );
		verify( restClient, times( 1 ) ).uploadIcon( any() );
	}

	@Test
	void testImportContentWithAnIconAndForms() {
		// GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSourceNameOne__catalogItemOne");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		String iconId = "iconId";
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItem = catalogBuilderOne.setIconId(iconId).setIconExt("png").setCustomFormId("formId").build();

		Map<String, String> headers = new HashMap<>();
		headers.put( "Location", "/some/path/iconId" );
		ResponseEntity<String> response	= GeneralMocks.mockResponseEntity( "", 201, headers );

		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add( catalogItem );

		fsMocks.catalogItemFsMocks().addCatalogItem( catalogItem );
		fsMocks.catalogItemFsMocks().addCatalogItemIcon( catalogItem );
		fsMocks.catalogItemFsMocks().addCatalogItemForm( catalogItem );
		when( restClient.getCatalogItemByBlueprintName( anyString() ) ).thenReturn( catalogItem );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.uploadIcon( any() ) ).thenReturn( response );
		doNothing().when( restClient ).importCustomForm( any(), anyString() );

		// START TEST
		store.importContent( tempFolder.getRoot() );

		// VERIFY
		verify( restClient, times( 1 ) ).importCustomForm( any(), anyString() );
		verify( restClient, times( 1 ) ).uploadIcon( any() );
	}

	@Test
	void testImportContentWithAnIconAndFormsWithDotInName() {
		// GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSource. NameOne__catalogItem One.x");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		String iconId = "iconId";
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItem One.x", "contentSource. NameOne" );
		VraNgCatalogItem catalogItem = catalogBuilderOne.setIconId(iconId).setIconExt("png").setCustomFormId("formId").build();

		Map<String, String> headers = new HashMap<>();
		headers.put( "Location", "/some/path/iconId" );
		ResponseEntity<String> response	= GeneralMocks.mockResponseEntity( "", 201, headers );

		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add( catalogItem );

		fsMocks.catalogItemFsMocks().addCatalogItem( catalogItem );
		fsMocks.catalogItemFsMocks().addCatalogItemIcon( catalogItem );
		fsMocks.catalogItemFsMocks().addCatalogItemForm( catalogItem );
		when( restClient.getCatalogItemByBlueprintName( anyString() ) ).thenReturn( catalogItem );
		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
		when( restClient.uploadIcon( any() ) ).thenReturn( response );
		doNothing().when( restClient ).importCustomForm( any(), anyString() );

		// START TEST
		store.importContent( tempFolder.getRoot() );

		// VERIFY
		verify( restClient, times( 1 ) ).importCustomForm( any(), anyString() );
		verify( restClient, times( 1 ) ).uploadIcon( any() );
	}

    @Test
    void testImportContentWithoutAnIconAndWithForms() {
        // GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSourceNameOne__catalogItemOne");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder( "catalogItemOne", "contentSourceNameOne" );
		VraNgCatalogItem catalogItem = catalogBuilderOne.setCustomFormId("formId").build();

        List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
        mockedCatalogItems.add( catalogItem );

        fsMocks.catalogItemFsMocks().addCatalogItem( catalogItem );
        fsMocks.catalogItemFsMocks().addCatalogItemForm( catalogItem );
        when( restClient.getCatalogItemByBlueprintName( anyString() ) ).thenReturn( catalogItem );
        when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
        when( restClient.getCatalogItemsForProject( PROJECT_ID ) ).thenReturn( mockedCatalogItems );
        doNothing().when( restClient ).importCustomForm( any(), anyString() );

        // START TEST
        store.importContent( tempFolder.getRoot() );

        // VERIFY
        verify( restClient, times( 1 ) ).importCustomForm( any(), anyString() );
    }

	@Test
	void testImportContentWithAnIconAndFormsWithVraAbove812() {
		// GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSourceNameOne__catalogItemOne");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		String iconId = "iconId";
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItemType bpCatalogItem = new VraNgCatalogItemType(VraNgContentSourceType.BLUEPRINT, null, null);
		VraNgCatalogItem catalogItem = catalogBuilderOne.setIconId(iconId).setIconExt("png").setCustomFormId("formId").setType(bpCatalogItem).build();

		Map<String, String> headers = new HashMap<>();
		headers.put("Location", "/some/path/iconId");
		ResponseEntity<String> response = GeneralMocks.mockResponseEntity("", 201, headers);

		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add(catalogItem);

		fsMocks.catalogItemFsMocks().addCatalogItem(catalogItem);
		fsMocks.catalogItemFsMocks().addCatalogItemIcon(catalogItem);
		fsMocks.catalogItemFsMocks().addCatalogItemForm(catalogItem);
		when(restClient.getCatalogItemByBlueprintName(anyString())).thenReturn(catalogItem);
		when(restClient.getProjectId()).thenReturn(PROJECT_ID);
		when(restClient.getCatalogItemsForProject(PROJECT_ID)).thenReturn(mockedCatalogItems);
		when(restClient.uploadIcon(any())).thenReturn(response);
		when(restClient.getIsVraAbove812()).thenReturn(true);
		when(restClient.getCatalogItemVersions(catalogItem.getId())).thenReturn(new JsonArray());

		doNothing().when(restClient).importCustomForm(any(), anyString());
		VraNgCatalogItemStore vra812Store = (VraNgCatalogItemStore812) new VraNgTypeStoreFactory(restClient, pkg, config, vraNgPackageDescriptor)
				.getStoreForType(VraNgPackageContent.ContentType.CATALOG_ITEM);

		// START TEST
		vra812Store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).importCustomForm(any(), anyString());
		verify(restClient, times(1)).uploadIcon(any());
	}

	@Test
	void testImportContentWithAnIconAndFormsWithVraBelow812() {
		// GIVEN
		List<String> mockCatalogItemNames = new ArrayList<>();
		mockCatalogItemNames.add("contentSourceNameOne__catalogItemOne");
		when(vraNgPackageDescriptor.getCatalogItem()).thenReturn(mockCatalogItemNames);

		String iconId = "iconId";
		CatalogItemMockBuilder catalogBuilderOne = new CatalogItemMockBuilder("catalogItemOne", "contentSourceNameOne");
		VraNgCatalogItem catalogItem = catalogBuilderOne.setIconId(iconId).setIconExt("png").setCustomFormId("formId").build();

		Map<String, String> headers = new HashMap<>();
		headers.put("Location", "/some/path/iconId");
		ResponseEntity<String> response = GeneralMocks.mockResponseEntity("", 201, headers);

		List<VraNgCatalogItem> mockedCatalogItems = new ArrayList<>();
		mockedCatalogItems.add(catalogItem);

		fsMocks.catalogItemFsMocks().addCatalogItem(catalogItem);
		fsMocks.catalogItemFsMocks().addCatalogItemIcon(catalogItem);
		fsMocks.catalogItemFsMocks().addCatalogItemForm(catalogItem);
		when(restClient.getCatalogItemByBlueprintName(anyString())).thenReturn(catalogItem);
		when(restClient.getProjectId()).thenReturn(PROJECT_ID);
		when(restClient.getCatalogItemsForProject(PROJECT_ID)).thenReturn(mockedCatalogItems);
		when(restClient.uploadIcon(any())).thenReturn(response);
		when(restClient.getIsVraAbove812()).thenReturn(false);

		doNothing().when(restClient).importCustomForm(any(), anyString());

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).importCustomForm(any(), anyString());
		verify(restClient, times(1)).uploadIcon(any());
	}
}
