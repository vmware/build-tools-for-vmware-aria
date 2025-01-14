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
import com.vmware.pscoe.iac.artifact.helpers.stubs.CatalogEntitlementMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.*;
import com.vmware.pscoe.iac.artifact.rest.RestClient;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VraNgEntitlementStoreTest {
	@Rule
	public TemporaryFolder 				tempFolder		= new TemporaryFolder();

	protected VraNgEntitlementStore		store;
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
		store					= new VraNgEntitlementStore();
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
	void testExportContentWithNoEntitlements() {
		//GIVEN
		when( vraNgPackageDescriptor.getCatalogEntitlement() ).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify( restClient, never() ).getAllCatalogEntitlements();
		assertEquals( 0, tempFolder.getRoot().listFiles().length );
	}

	@Test
	void testExportContentWithAllEntitlements() {
		//GIVEN
		when(vraNgPackageDescriptor.getCatalogEntitlement()).thenReturn(null);

		List<VraNgCatalogEntitlement> entitlements = new ArrayList<>();
		CatalogEntitlementMockBuilder builder = new CatalogEntitlementMockBuilder("entitlement1");
		entitlements.add(builder.build());
		when( restClient.getAllCatalogEntitlements() ).thenReturn( entitlements );

		//TEST
		store.exportContent();

		String[] expectedEntitlementsFile	= { "entitlement1.yaml" };

		//VERIFY
		verify( restClient, times(1) ).getAllCatalogEntitlements();
		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedEntitlementsFile );
	}

	@Test
	void testExportContentWithSpecificEntitlements() {
		//GIVEN
		List<VraNgCatalogEntitlement> entitlements = new ArrayList<>();
		CatalogEntitlementMockBuilder builder1 = new CatalogEntitlementMockBuilder("entitlement1");
		CatalogEntitlementMockBuilder builder2 = new CatalogEntitlementMockBuilder("entitlement2");

		entitlements.add(builder1.build());
		entitlements.add(builder2.build());
		when(restClient.getAllCatalogEntitlements()).thenReturn(entitlements);

		List<String> entitlementsNames = new ArrayList<>();
		entitlementsNames.add("entitlement1");
		when( vraNgPackageDescriptor.getCatalogEntitlement() ).thenReturn(entitlementsNames);

		//TEST
		store.exportContent();

		String[] expectedEntitlementsFile	= { "entitlement1.yaml" };

		//VERIFY
		verify( restClient, times(1) ).getAllCatalogEntitlements();
		AssertionsHelper.assertFolderContainsFiles( fsMocks.getTempFolderProjectPath(), expectedEntitlementsFile );
	}

	@Test
	void testImportContentExcludingFromConfiguration() {
		//GIVEN
		CatalogEntitlementMockBuilder builder1 = new CatalogEntitlementMockBuilder("entitlement1");
		CatalogEntitlementMockBuilder builder2 = new CatalogEntitlementMockBuilder("entitlement2");

		fsMocks.entitlementStore().addEntitlement(builder1.build());
		fsMocks.entitlementStore().addEntitlement(builder2.build());

		List<String> entitlementsNames = new ArrayList<>();
		entitlementsNames.add("entitlement");
		when( vraNgPackageDescriptor.getCatalogEntitlement() ).thenReturn(entitlementsNames);

		//TEST
		store.importContent(tempFolder.getRoot());

		//VERIFY
		verify( restClient, times(0) ).createCatalogEntitlement(any(), any());
	}

	@Test
	void testExportContentWithNoExistingEntitlement() {
		//GIVEN
		List<VraNgCatalogEntitlement> entitlements = new ArrayList<>();
		CatalogEntitlementMockBuilder builder1 = new CatalogEntitlementMockBuilder("entitlement1");
		CatalogEntitlementMockBuilder builder2 = new CatalogEntitlementMockBuilder("entitlement2");
		entitlements.add(builder1.build());
		entitlements.add(builder2.build());

		when(restClient.getAllCatalogEntitlements()).thenReturn(entitlements);

		List<String> entitlementsNames = new ArrayList<>();
		entitlementsNames.add("noting");
		when( vraNgPackageDescriptor.getCatalogEntitlement() ).thenReturn(entitlementsNames);

		//TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());

		//VERIFY
		verify( restClient, times(1)).getAllCatalogEntitlements();
	}
}
