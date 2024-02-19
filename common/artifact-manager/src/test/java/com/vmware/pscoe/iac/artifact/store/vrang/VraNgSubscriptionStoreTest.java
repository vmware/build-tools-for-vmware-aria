package com.vmware.pscoe.iac.artifact.store.vrang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

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
import com.vmware.pscoe.iac.artifact.helpers.stubs.SubscriptionMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class VraNgSubscriptionStoreTest {
	@Rule
	public TemporaryFolder 				tempFolder		= new TemporaryFolder();

	protected VraNgSubscriptionStore	store;
	protected RestClientVraNg 			restClient;
	protected Package 					pkg;
	protected ConfigurationVraNg 		config;
	protected VraNgPackageDescriptor 	vraNgPackageDescriptor;
	protected FsMocks 					fsMocks;
	protected Gson						gson;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		}
		catch ( IOException e ) {
			throw new RuntimeException( "Could not create a temp folder" );
		}

		fsMocks					= new FsMocks( tempFolder.getRoot() );
		store					= new VraNgSubscriptionStore();
		restClient				= Mockito.mock( RestClientVraNg.class );
		pkg						= PackageFactory.getInstance( PackageType.VRANG, tempFolder.getRoot() );
		config					= Mockito.mock( ConfigurationVraNg.class );
		vraNgPackageDescriptor	= Mockito.mock( VraNgPackageDescriptor.class );
		gson 					= new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

		//mock all rest calls in init
		VraNgOrganization mockedOrg = new VraNgOrganization();
		mockedOrg.setId("mockOrg");
		when(config.getOrgId()).thenReturn("mockOrg");
		when(restClient.getProjectId()).thenReturn("mockedProjectId");
		when(restClient.getProjects()).thenReturn(new ArrayList<VraNgProject>());
		when(restClient.getOrganizationById( anyString() )).thenReturn(mockedOrg);
		when(restClient.getOrganizationByName( anyString() )).thenReturn(mockedOrg);

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
	void testExportContentWithNoSubscriptions() {
		//GIVEN 
		when( vraNgPackageDescriptor.getSubscription()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify( restClient, never() ).getAllSubscriptions();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithAllSubscriptions() throws IOException{
		//GIVEN 
		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		SubscriptionMockBuilder mockBuilder = new SubscriptionMockBuilder();
		
		subscriptions.put( "mockId", mockBuilder.setId("mockId").setName("NamingVM").build() );

		when( vraNgPackageDescriptor.getSubscription()).thenReturn(null);
		when( restClient.getAllSubscriptions()).thenReturn( subscriptions );

		//TEST
		store.exportContent();

		String[] expectedSubscriptionFiles	= { "NamingVM.json" };

		// VERIFY
		File pathToSubscription = new File(tempFolder.getRoot().getPath() + "/subscriptions");
		AssertionsHelper.assertFolderContainsFiles( pathToSubscription, expectedSubscriptionFiles );
	}


	@Test
	void testExportContentWithSpecificSubscriptions() throws IOException{
		//GIVEN 
		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("NamingVM");

		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		SubscriptionMockBuilder nameVmMockBuilder = new SubscriptionMockBuilder();
		SubscriptionMockBuilder tagsVmMockBuilder = new SubscriptionMockBuilder();
		
		subscriptions.put( "nameVmId", nameVmMockBuilder.setId("nameVmId").setName("NamingVM").build() );
		subscriptions.put( "tagVmId", tagsVmMockBuilder.setId("tagVmId").setName("TaggingVM").build() );

		when( vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when( restClient.getAllSubscriptions()).thenReturn( subscriptions );

		//TEST
		store.exportContent();

		String[] expectedSubscriptionFiles	= { "NamingVM.json" };

		// VERIFY
		File pathToSubscription = new File( tempFolder.getRoot().getPath() + "/subscriptions" );
		AssertionsHelper.assertFolderContainsFiles( pathToSubscription, expectedSubscriptionFiles );
	}

	@Test
	void testExportAbxSubscriptions() throws IOException{
		//GIVEN 		
		List<AbxAction> abxActions = new ArrayList<>();	
		AbxAction abxAction = new AbxAction();
		abxAction.id = "329e94b1";
		abxAction.name = "createDns";
		abxActions.add(abxAction);
		
		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("addDnsRecord");
		
		SubscriptionMockBuilder subscriptionMockBuilder = new SubscriptionMockBuilder();		
		subscriptionMockBuilder.setPropertyInRawData("runnableId", "329e94b1");		
		subscriptionMockBuilder.setPropertyInRawData("runnableType", "extensibility.abx");
		subscriptionMockBuilder.setName("addDnsRecord");		
		subscriptionMockBuilder.setId("addDnsRecordId");

		VraNgSubscription subscriptionMock = subscriptionMockBuilder.build();
		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		subscriptions.put("addDnsRecord", subscriptionMock);

		when( vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when( restClient.getAllSubscriptions()).thenReturn( subscriptions );
		when(restClient.getAllAbxActions()).thenReturn(abxActions);

		//TEST
		store.exportContent();

		// VERIFY
		File subscription = new File( tempFolder.getRoot().getPath() + "/subscriptions/addDnsRecord.json" );
		JsonReader reader = new JsonReader(new FileReader(subscription.getPath()));
		JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();		
		assertEquals("createDns", jsonObj.get("runnableName").getAsString());		
	}

	@Test
	void testExportContentWithNonExistingAbxAction() throws IOException{
		//GIVEN 		
		List<AbxAction> abxActions = new ArrayList<>();	
		AbxAction abxAction = new AbxAction();
		abxAction.id = "329e94b1";
		abxAction.name = "createRecord";
		abxActions.add(abxAction);
		
		List<String> subscriptionNames = new ArrayList<>();	
		subscriptionNames.add("addDnsRecord");
		
		SubscriptionMockBuilder subscriptionMockBuilder = new SubscriptionMockBuilder();		
		subscriptionMockBuilder.setPropertyInRawData("runnableId", "ebab1ca8");		
		subscriptionMockBuilder.setPropertyInRawData("runnableType", "extensibility.abx");
		subscriptionMockBuilder.setName("addDnsRecord");		
		subscriptionMockBuilder.setId("addDnsRecordId");

		VraNgSubscription subscriptionMock = subscriptionMockBuilder.build();
		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		subscriptions.put("addDnsRecord", subscriptionMock);

		when( vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when( restClient.getAllSubscriptions()).thenReturn( subscriptions );
		when(restClient.getAllAbxActions()).thenReturn(abxActions);

		// VERIFY
		assertThrows(RuntimeException.class, () -> store.exportContent());
	}

	@Test
	void testImportAbxSubscriptionsWithNonExistingAbxActionName() throws IOException{
		//GIVEN
		List<AbxAction> abxActions = new ArrayList<>();	
		AbxAction abxAction = new AbxAction();
		abxAction.id = "329e94b1";	
		abxAction.name = "createDns";
		abxActions.add(abxAction);

		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("addDnsRecord");

		SubscriptionMockBuilder subscriptionMockBuilder = new SubscriptionMockBuilder();		
		subscriptionMockBuilder.setPropertyInRawData("runnableName", "NonExistingName");		
		subscriptionMockBuilder.setPropertyInRawData("runnableType", "extensibility.abx");
		subscriptionMockBuilder.setName("addDnsRecord");		
		subscriptionMockBuilder.setId("addDnsRecordId");

		VraNgSubscription subscriptionMock = subscriptionMockBuilder.build();
		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		subscriptions.put("addDnsRecord", subscriptionMock);
		fsMocks.subscriptionFsMocks().addSubscription(subscriptionMock);
		
		when(restClient.getAllSubscriptions()).thenReturn(subscriptions);
		when(vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when(restClient.getAllAbxActions()).thenReturn(abxActions);

		//VERIFY		
		assertThrows(RuntimeException.class, () -> store.importContent(tempFolder.getRoot()));
	}

	@Test
	void testImportAbxSubscriptions() throws IOException{
		//GIVEN
		List<AbxAction> abxActions = new ArrayList<>();	
		AbxAction abxAction = new AbxAction();
		abxAction.id = "329e94b1";	
		abxAction.name = "createDns";
		abxActions.add(abxAction);

		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("addDnsRecord");

		SubscriptionMockBuilder subscriptionMockBuilder = new SubscriptionMockBuilder();		
		subscriptionMockBuilder.setPropertyInRawData("runnableName", "createDns");		
		subscriptionMockBuilder.setPropertyInRawData("runnableType", "extensibility.abx");
		subscriptionMockBuilder.setName("addDnsRecord");		
		subscriptionMockBuilder.setId("addDnsRecordId");

		VraNgSubscription subscriptionMock = subscriptionMockBuilder.build();
		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		subscriptions.put("addDnsRecord", subscriptionMock);
		fsMocks.subscriptionFsMocks().addSubscription(subscriptionMock);
		
		ArgumentCaptor<String> subscriptionName = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> subscriptionContent = ArgumentCaptor.forClass(String.class);
		when(restClient.getAllSubscriptions()).thenReturn(subscriptions);
		when(vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when(restClient.getAllAbxActions()).thenReturn(abxActions);

		//TEST
		store.importContent(tempFolder.getRoot());

		//VERIFY		
		verify(restClient).importSubscription(subscriptionName.capture(), subscriptionContent.capture());
		JsonObject subscription = gson.fromJson(subscriptionContent.getValue(), JsonObject.class);
		assertEquals(abxAction.id, subscription.get("runnableId").getAsString());
		assertNull(subscription.get("runnableName"));
	}


	@Test
	void testImportContentExcludingFromConfiguration() throws IOException {
		//GIVEN
		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("NamingVM1");
		SubscriptionMockBuilder builder1 = new SubscriptionMockBuilder();
		VraNgSubscription sub1 = builder1.setId("nameVmId").setName("NamingVM").build();

		fsMocks.subscriptionFsMocks().addSubscription(sub1);
		Map<String, VraNgSubscription> allSubs = new HashMap<String, VraNgSubscription>();
		allSubs.put("nameVmId", sub1);
		when( restClient.getAllSubscriptions() ).thenReturn( allSubs );
		when( vraNgPackageDescriptor.getSubscription() ).thenReturn(subscriptionNames);

		//TEST
		store.importContent(tempFolder.getRoot());

		//VERIFY
		verify( restClient, never() ).importSubscription(any(), any());
	}

	@Test
	void testExportContentWithNonExistingSubscription() throws IOException {
		// GIVEN
		List<String> subscriptionNames = new ArrayList<>();
		subscriptionNames.add("nope");

		Map<String, VraNgSubscription> subscriptions = new HashMap<>();
		SubscriptionMockBuilder nameVmMockBuilder = new SubscriptionMockBuilder();
		SubscriptionMockBuilder tagsVmMockBuilder = new SubscriptionMockBuilder();

		subscriptions.put("nameVmId", nameVmMockBuilder.setId("nameVmId").setName("NamingVM").build());
		subscriptions.put("tagVmId", tagsVmMockBuilder.setId("tagVmId").setName("TaggingVM").build());

		when(vraNgPackageDescriptor.getSubscription()).thenReturn(subscriptionNames);
		when(restClient.getAllSubscriptions()).thenReturn(subscriptions);

		// TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());
	}
}
