package com.vmware.pscoe.iac.artifact.store.vrang;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
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
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class VraNgPolicyStoreTest {

	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	/**
	 * store.
	 */
	private VraNgPolicyStore store;
	/**
	 * restClient.
	 */
	private RestClientVraNg restClient;
	/**
	 * pkg.
	 */
	private Package pkg;
	/**
	 * config.
	 */
	private ConfigurationVraNg config;
	/**
	 * vraNgPackageDescriptor.
	 */
	private VraNgPackageDescriptor vraNgPackageDescriptor;
	/**
	 * fsMocks.
	 */
	private FsMocks fsMocks;
	/**
	 * dirContentSharingPolicies.
	 */
	private String dirPolicies = "policies";
	/**
	 * contentSharingPolicy.
	 */
	private String contentSharingPolicy = "content-sharing";
	private String resourceQuotaPolicy = "resource-quota";

	/**
	 * Init function called before each test.
	 */
	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new VraNgPolicyStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

		VraNgOrganization org = new VraNgOrganization();
		org.setId("org1");
		org.setName("org1");

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		when(config.getOrgId()).thenReturn("org1");
		when(config.getOrgName()).thenReturn("org1");
		when(restClient.getOrganizationById("org1")).thenReturn(org);
		when(restClient.getOrganizationByName("org1")).thenReturn(org);
		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	/**
	 * Function called after each.
	 */
	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println("==========================================================");
		System.out.println("END");
		System.out.println("==========================================================");
	}


	@Test
	void testExportContentWithNoContentSharingPolicies() {
	System.out.println(this.getClass() + "testExportContentWithNoContentSharingPolicies");
		//GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, contentSharingPolicy).toFile();

		//VERIFY
		verify(restClient, never()).getContentSharingPolicies();
		verify(restClient, never()).getContentSharingPolicy(anyString());

		assertEquals(null, policyFolder.listFiles());
	}
	@Test
	void testExportContentWithNoResourceQuotaPolicies() {
		System.out.println(this.getClass() + "testExportContentWithNoResourceQuotaPolicies");
		//GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();

		//VERIFY
		verify(restClient, never()).getResourceQuotaPolicies();
		verify(restClient, never()).getResourceQuotaPolicy(anyString());

		assertEquals(null, policyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllContentSharingPolicies() {
	System.out.println(this.getClass() + "testExportContentWithAllContentSharingPolicies");
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
			"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicy2 = new VraNgContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c",
			"testing",
			"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		List<VraNgContentSharingPolicy> policies = Arrays.asList(csPolicy, csPolicy2);
		// // GIVEN
		when(store.vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null));
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null));
		when(store.restClient.getContentSharingPolicies()).thenReturn(policies);
		when(restClient.getContentSharingPolicies()).thenReturn(policies);
		when(store.restClient.getResourceQuotaPolicies()).thenReturn(new ArrayList<>());
		when(store.restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);
		when(store.restClient.getContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c")).thenReturn(csPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File contentSharingPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, contentSharingPolicy).toFile();
		//TODO::FIX ME!!!
		assertTrue(contentSharingPolicyFolder.getPath().endsWith("policies/content-sharing"));
		assertNotNull(contentSharingPolicyFolder.listFiles());
		assertEquals(2, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificContentSharingPolicies() {
		System.out.println(this.getClass() + "testExportContentWithSpecificContentSharingPolicies");
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs",
			"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"), null);
		// // GIVEN ?????
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(store.vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getContentSharingPolicy("1")).thenReturn(csPolicy);

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, contentSharingPolicy).toFile();

		// VERIFY
		assertEquals(1, contentSharingPolicyFolder.listFiles().length);
	}


	@Test
	void testExportContentWithAllResourceQuotaPolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllResourceQuotaPolicies");

		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282", "RQ01",
			"com.vmware.policy.resource.quota", "project1", "org1", "HARD", "TEST", new VraNgResourceQuotaDefinition());

		VraNgResourceQuotaPolicy rqPolicy2 = new VraNgResourceQuotaPolicy("2cf93725-38e9-4cb9-888a-a40994754c31",
			"RQ02",
			"com.vmware.policy.resource.quota", "project1", "org1", "HARD", "TEST", new VraNgResourceQuotaDefinition());

		List<VraNgResourceQuotaPolicy> policies = Arrays.asList(rqPolicy, rqPolicy2);
		List<String> policyNames = new ArrayList<>();
		//policyNames.add("resource-quota:");
		policyNames.add("RQ01");
		policyNames.add("RQ02");

		// // GIVEN
		when(store.vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null));
		when(restClient.getResourceQuotaPolicies()).thenReturn(policies);
		when(restClient.getResourceQuotaPolicies()).thenReturn(new ArrayList<>());
		when(restClient.getResourceQuotaPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(rqPolicy);
		when(restClient.getResourceQuotaPolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(rqPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File resourceQuotaPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();
		//TODO::FIX ME!!!
		assertNotNull(resourceQuotaPolicyFolder.listFiles());
		assertEquals(2, resourceQuotaPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificResourceQuotaPolicies() {
	System.out.println("this.getClass() + testExportContentWithSpecificResourceQuotaPolicies");
		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy("1", "cs",
			"com.vmware.policy.resource.quota", "project1", "org1", "HARD", "TEST", new VraNgResourceQuotaDefinition());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, Arrays.asList("rq"));
		// // GIVEN
		when(store.vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getResourceQuotaPolicies()).thenReturn(Arrays.asList(rqPolicy));
		when(restClient.getResourceQuotaPolicy("1")).thenReturn(rqPolicy);

		// TEST
		store.exportContent();

		File resourceQuotaPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();

		// VERIFY
		assertEquals(1, resourceQuotaPolicyFolder.listFiles().length);
	}
}
