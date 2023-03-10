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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganization;
import com.google.gson.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.util.*;


import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CustomResourceMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VraNgCustomResourceStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected VraNgCustomResourceStore store;
	protected RestClientVraNg restClient;
	protected Package pkg;
	protected ConfigurationVraNg config;
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;
	protected VraNgOrganization org;

	protected static String PROJECT_ID	= "projectId";
	protected static String ORG_ID		= "orgId";
	protected static String ORG_NAME	= "testOrg";

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks 				= new FsMocks(tempFolder.getRoot());
		store 					= new VraNgCustomResourceStore();
		restClient 				= Mockito.mock(RestClientVraNg.class);
		pkg 					= PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config 					= Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor 	= Mockito.mock(VraNgPackageDescriptor.class);
		org					 	= Mockito.mock(VraNgOrganization.class);

		when( config.getOrgId() ).thenReturn( ORG_ID );
		when( restClient.getOrganizationById(any()) ).thenReturn( org );
		when( org.getId() ).thenReturn( ORG_ID );
		when( org.getName() ).thenReturn( ORG_NAME );
		when( org.getRefLink() ).thenReturn( "reflink" );

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
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
	void testImportCustomResourcesWhenIdNotExistsInVroAndExistsInJson() throws IOException{
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("WRONG").build();
		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);

		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder = new CustomResourceMockBuilder();
		String comparator = mockBuilder.withoutIdInRawData().setOrgId( ORG_ID ).removeFormDefinitionIds().build().getJson().toString();

		when( restClient.getProjectId() ).thenReturn(PROJECT_ID);
		when( restClient.getVraWorkflowIntegration( any()) ).thenReturn( vro );
		when( restClient.getAllCustomResources() ).thenReturn( new HashMap<String, VraNgCustomResource>() );
		doNothing().when( restClient ).importCustomResource( anyString(), anyString() );
		doNothing().when( restClient ).deleteCustomResource( anyString(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		ArgumentCaptor<String> cr = ArgumentCaptor.forClass( String.class );

		// VERIFY
		verify( restClient, times( 3 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getVraWorkflowIntegration( any() );
		verify( restClient, times( 1 ) ).getAllCustomResources();
		verify( restClient, times( 1 ) ).importCustomResource( any(), cr.capture() );
		verify( restClient, never() ).deleteCustomResource( any(), anyString() );

		assertEquals( comparator, cr.getValue() );
	}

	@Test
	void testImportCustomResourcesWhenIdNotExistsInVroAndNotExistsInJson() throws IOException {
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should not contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.withoutIdInRawData().setOrgId("WRONG").build();

		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);

		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder	= new CustomResourceMockBuilder();
		String comparator = mockBuilder.withoutIdInRawData().setOrgId( ORG_ID ).removeFormDefinitionIds().build().getJson().toString();

		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn( vro );
		when( restClient.getAllCustomResources() ).thenReturn( new HashMap<String, VraNgCustomResource>() );
		doNothing().when( restClient ).importCustomResource( anyString(), anyString() );
		doNothing().when( restClient ).deleteCustomResource( anyString(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		ArgumentCaptor<String> cr = ArgumentCaptor.forClass( String.class );

		// VERIFY
		verify( restClient, times( 3 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getVraWorkflowIntegration( any() );
		verify( restClient, times( 1 ) ).getAllCustomResources();
		verify( restClient, times( 1 ) ).importCustomResource( any(), cr.capture() );
		verify( restClient, never() ).deleteCustomResource( any(), anyString() );

		assertEquals( comparator, cr.getValue() );
	}

	@ParameterizedTest
	@ValueSource(strings = {
							"AmpersandCharacter&",
							"DollarCharacter$",
							"_UnderscoreInfront",
							"UnderscoreBehind_",
							".DotInfront",
							"DotBehind.",
							" SpaceInfront",
							"SpaceBehind ",
							"Space InMiddle"
	})
	void testImportCustomResourcesWhenAdditionalActionNamesDoNotPassValidation(String wrongAdditionalActionName)  throws IOException{
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		JsonArray additionalActions = JsonParser.parseString(mockBuilder.build().getJson()).getAsJsonObject().get("additionalActions").getAsJsonArray();
		additionalActions.get(0).getAsJsonObject().addProperty("name", wrongAdditionalActionName);
		VraNgCustomResource mockResource = mockBuilder.setAdditionalActions(additionalActions).setOrgId("WRONG").build();

		// Create mock return value of getAllCustomResources().
		// These represent the retrieved Custom Resources from the environment
		// and are expected to contain property 'id'.
		JsonElement mockResourceElement = JsonParser.parseString(mockResource.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String id = object.get( "id" ).toString();

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );

		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);

		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( config.getOrgId() ).thenReturn( ORG_ID );
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn( vro );
		when( restClient.getAllCustomResources() ).thenReturn( map );
		doNothing().when( restClient ).importCustomResource( any(), anyString() );
		doNothing().when( restClient ).deleteCustomResource( any(), anyString() );

		// START TEST
		assertThrows( RuntimeException.class, () -> {
			store.importContent(tempFolder.getRoot());
		});
	}

	@Test
	void testImportCustomResourcesWhenIdExistsInVroAndInJson()  throws IOException{
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("WRONG").build();

		// Create mock return value of getAllCustomResources(). 
		// These represent the retrieved Custom Resources from the environment 
		// and are expected to contain property 'id'.
		JsonElement mockResourceElement = JsonParser.parseString(mockResource.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String id = object.get( "id" ).toString();

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );
		
		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);
		
		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder = new CustomResourceMockBuilder();
		String comparator = mockBuilder.withoutIdInRawData().setOrgId( ORG_ID ).removeFormDefinitionIds().build().getJson();

		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn( vro );
		when( restClient.getAllCustomResources() ).thenReturn( map );
		doNothing().when( restClient ).importCustomResource( any(), anyString() );
		doNothing().when( restClient ).deleteCustomResource( any(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		ArgumentCaptor<String> cr = ArgumentCaptor.forClass( String.class );

		// VERIFY
		verify( restClient, times( 3 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getVraWorkflowIntegration( any() );
		verify( restClient, times( 1 ) ).getAllCustomResources();
		verify( restClient, times( 1 ) ).importCustomResource( any(), cr.capture() );
		verify( restClient, times( 1 ) ).deleteCustomResource( any(), anyString() );

		assertEquals( comparator, cr.getValue() );
	}

	@Test
	void testImportCustomResourcesWhenIdExistsInVroAndHasDeployments()  throws IOException{
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("WRONG").build();

		// Create mock return value of getAllCustomResources(). 
		// These represent the retrieved Custom Resources from the environment 
		// and are expected to contain property 'id'.
		JsonElement mockResourceElement = JsonParser.parseString(mockResource.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String id = object.get( "id" ).toString();

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );
		
		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);
		
		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder = new CustomResourceMockBuilder();
		String comparator = mockBuilder.withoutIdInRawData().setOrgId( ORG_ID ).removeFormDefinitionIds().build().getJson().toString();

		when( restClient.getProjectId() ).thenReturn( PROJECT_ID );
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn( vro );
		when( restClient.getAllCustomResources() ).thenReturn( map );
		doNothing().when( restClient ).importCustomResource( any(), anyString() );
		doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Resource type cannot be deleted as there are active resources attached to it"))
			.when( restClient ).deleteCustomResource( any(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify( restClient, times( 3 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getVraWorkflowIntegration( any() );
		verify( restClient, times( 1 ) ).getAllCustomResources();
		verify( restClient, times(1) ).importCustomResource( any(), anyString() );
		verify( restClient, times( 1 ) ).deleteCustomResource( any(), anyString() );
	}

	@Test
	void testImportCustomResourcesWhenIdExistsInVroAndNotInJson() throws IOException {
		// GIVEN
		List<String> list = new ArrayList<String>();
		list.add("Avi Load Balancer L3DSR");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock Custom Resource - should not contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("WRONG").withoutIdInRawData().build();


		// Create mock return value of getAllCustomResources(). 
		// These represent the retrieved Custom Resources from the environment 
		// and are expected to contain property 'id'.
		mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResourceWithId = mockBuilder.setOrgId("WRONG").build();
		JsonElement mockResourceElement = JsonParser.parseString(mockResourceWithId.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String id = object.get( "id" ).toString();

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );

		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);
		
		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder = new CustomResourceMockBuilder();
		String comparator = mockBuilder.withoutIdInRawData().setOrgId( ORG_ID ).removeFormDefinitionIds().build().getJson().toString();

		when( restClient.getProjectId() ).thenReturn(PROJECT_ID);
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn(vro);
		when( restClient.getAllCustomResources() ).thenReturn(map);
		doNothing().when( restClient ).importCustomResource( any(), anyString());
		doNothing().when( restClient ).deleteCustomResource( any(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		ArgumentCaptor<String> cr = ArgumentCaptor.forClass( String.class );

		// VERIFY
		verify( restClient, times( 3 ) ).getProjectId();
		verify( restClient, times( 1 ) ).getVraWorkflowIntegration( any() );
		verify( restClient, times( 1 ) ).getAllCustomResources();
		verify( restClient, times( 1 ) ).importCustomResource( any(), cr.capture() );
		verify( restClient, times( 1 ) ).deleteCustomResource( any(), anyString() );

		assertEquals( comparator, cr.getValue() );
	}

	@Test
	void testImportCustomResourcesWhenNotInConfiguration () throws IOException {
		// GIVEN
		// Create mock Custom Resource - should not contain property 'id'
		CustomResourceMockBuilder mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("WRONG").withoutIdInRawData().build();

		List<String> list = new ArrayList<String>();
		list.add("name");
		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);

		// Create mock return value of getAllCustomResources(). 
		// These represent the retrieved Custom Resources from the environment 
		// and are expected to contain property 'id'.
		mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResourceWithId = mockBuilder.setOrgId("WRONG").build();
		JsonElement mockResourceElement = JsonParser.parseString(mockResourceWithId.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String id = object.get( "id" ).toString();

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );

		// Create mock vRO Integration
		VraNgIntegration vro = new VraNgIntegration();
		vro.setEndpointConfigurationLink("dummy");
		vro.setName("dummy_name");

		fsMocks.customResourceFsMocks().addCustomResource(mockResource);
		
		// 'comparator' will be used for comparison between expected value 
		// and passed value in importCustomResource()
		mockBuilder = new CustomResourceMockBuilder();
		String comparator = mockBuilder.setOrgId( ORG_ID ).removeFormDefinitionIds().withoutIdInRawData().build().getJson().toString();

		when( restClient.getProjectId() ).thenReturn(PROJECT_ID);
		when( restClient.getVraWorkflowIntegration(any()) ).thenReturn(vro);
		when( restClient.getAllCustomResources() ).thenReturn(map);
		doNothing().when( restClient ).importCustomResource( any(), anyString());
		doNothing().when( restClient ).deleteCustomResource( any(), anyString() );

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify( restClient, never() ).importCustomResource( any(), anyString() );
	}

	@Test
	void testExportCustomResourcesRemovesIdOnSave() throws IOException {
		// GIVEN
		// Create mock Custom Resource 
		CustomResourceMockBuilder	mockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockResource = mockBuilder.setOrgId("orgId").build();
		JsonElement mockResourceElement = JsonParser.parseString(mockResource.getJson());
		JsonObject object = mockResourceElement.getAsJsonObject();
		String name = mockResource.getName();
		String id = object.get( "id" ).toString();
		String fileName = name.concat(".json");

		mockBuilder = new CustomResourceMockBuilder();
		String expectedFileContent = mockBuilder.setOrgId("orgId").withoutIdInRawData().build().getJson();

		List<String> list = new ArrayList<String>();
		list.add( name );

		HashMap<String, VraNgCustomResource> map = new HashMap<String, VraNgCustomResource>();
		map.put( id, mockResource );

		when(vraNgPackageDescriptor.getCustomResource()).thenReturn(list);
		when(restClient.getAllCustomResources()).thenReturn(map);

		// START TEST
		store.exportContent();

		// VERIFY
		verify( vraNgPackageDescriptor, times( 1 ) ).getCustomResource();
		verify( restClient, times( 1 ) ).getAllCustomResources();
		assertEquals( 1, Objects.requireNonNull(fsMocks.getTempFolderProjectPath().listFiles()).length );
		assertEquals( expectedFileContent, getFileContent( fsMocks.getTempFolderProjectPath(), fileName ));
		
		String[] expectedFiles	= { fileName };
		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedFiles );
	
	}

	
	/**
	 * Return content of a specific item in the given folder.
	 * Throws if missing
	 *
	 * @param	folder -> the folder to search in
	 * @param	itemName - The item name to search for
	 *
	 * @return	File content as String
	 */
	private String getFileContent( File folder, String itemName ) {
		File file = fsMocks.findItemByNameInFolder(folder, itemName);
		try{			
			String content = FileUtils.readFileToString( file, "UTF-8" );
			if(content != null){
				Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
				JsonObject customResourceJsonElement = gson.fromJson(content, JsonObject.class);
				return customResourceJsonElement.toString();
			}
		}catch (IOException e) {
			throw new RuntimeException("Error reading from file: " + file.getPath(), e);
		}
		return "";
	}

	@Test
	void testExportContentWithNoCustomResources() {
		//GIVEN 
		when( vraNgPackageDescriptor.getCustomResource()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify( restClient, never() ).getAllCustomResources();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}
	

	@Test
	void testExportContentWithAllCustomResources() throws IOException {
		//GIVEN 
		Map<String, VraNgCustomResource> customResources = new HashMap<>();
		CustomResourceMockBuilder	mockBuilder = new CustomResourceMockBuilder();

		customResources.put( "Avi Load Balancer L3DSR", mockBuilder.setDisplayNameInRawData("Avi Load Balancer L3DSR").build());

		when( vraNgPackageDescriptor.getCustomResource()).thenReturn(null);
		when( restClient.getAllCustomResources()).thenReturn( customResources );

		//TEST
		store.exportContent();

		String[] expectedCustomResource	= { "Avi Load Balancer L3DSR.json" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedCustomResource );
	}


	@Test
	void testExportContentWithSpecificCustomResources() throws IOException{
		//GIVEN 
		Map<String, VraNgCustomResource> customResources = new HashMap<>();
		CustomResourceMockBuilder mockBuider = new CustomResourceMockBuilder();

		customResources.put( "Avi Load Balancer L3DSR", mockBuider.setDisplayNameInRawData("Avi Load Balancer L3DSR").build());
		customResources.put( "Ngnix Load Balancer", mockBuider.setDisplayNameInRawData("Ngnix Load Balancer L3DSR").build());

		List<String> customResourceNames = new ArrayList<>();
		customResourceNames.add("Avi Load Balancer L3DSR");

		when( vraNgPackageDescriptor.getCustomResource()).thenReturn(customResourceNames);
		when( restClient.getAllCustomResources()).thenReturn( customResources );

		//TEST
		store.exportContent();

		String[] expectedCustomResource	= { "Avi Load Balancer L3DSR.json" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedCustomResource );
	}

	@Test
	void testExportContentForNonExistingCustomResource() throws IOException{
		//GIVEN 
		Map<String, VraNgCustomResource> customResources = new HashMap<>();
		CustomResourceMockBuilder mockBuider = new CustomResourceMockBuilder();

		customResources.put( "Avi Load Balancer L3DSR", mockBuider.setDisplayNameInRawData("Avi Load Balancer L3DSR").build());
		customResources.put( "Ngnix Load Balancer", mockBuider.setDisplayNameInRawData("Ngnix Load Balancer L3DSR").build());

		List<String> customResourceNames = new ArrayList<>();
		customResourceNames.add("Not There");

		when( vraNgPackageDescriptor.getCustomResource()).thenReturn(customResourceNames);
		when( restClient.getAllCustomResources()).thenReturn( customResources );

		//TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());
	}

}
