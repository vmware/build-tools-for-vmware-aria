package com.vmware.pscoe.iac.artifact.store.vrang;

/*-
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies.VraNgPolicyBase;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.ariaPolicies.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgDefinition;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPolicy;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

public class VraNgContentSharingPolicyStoreTest {

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
	private String dirContentSharingPolicies = "policies";
	/**
	 * contentSharingPolicy.
	 */
	private String contentSharingPolicy = "content-sharing";

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
		//GIVEN 
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		//VERIFY
		verify(restClient, never()).getVraPolicies();
		verify(restClient, never()).getPolicy(anyString());

		assertEquals(null, contentSharingPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllContentSharingPolicies() {
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicy2 = new VraNgContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c",
				"testing",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		List<VraNgPolicyBase> policies = Arrays.asList(csPolicy, csPolicy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null, null, null, null, null));
		when(restClient.getVraPolicies()).thenReturn(policies);
		when(restClient.getPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);
		when(restClient.getPolicy("94824034-ef7b-4728-a6c2-fb440aff590c")).thenReturn(csPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();
		assertEquals(2, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificContentSharingPolicies() {
		List<String> csPoliciesList = Arrays.asList("cs");
		List<String> resourcePoliciesList = Arrays.asList("resource_test");
		List<String> leasePoliciesList = Arrays.asList("lease_test");
		List<String> day2PoliciesList = Arrays.asList("lease_test");
		List<String> approvalPoliciesList = Arrays.asList("lease_test");
		List<String> deploymentLimitPoliciesList = Arrays.asList("lease_test");

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(csPoliciesList, resourcePoliciesList, leasePoliciesList, day2PoliciesList, approvalPoliciesList, deploymentLimitPoliciesList);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getVraPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getPolicy("1")).thenReturn(csPolicy);

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		// VERIFY
		assertEquals(1, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		List<String> csPoliciesList = Arrays.asList("cs");
		List<String> resourcePoliciesList = Arrays.asList("resource_test");
		List<String> leasePoliciesList = Arrays.asList("lease_test");
		List<String> day2PoliciesList = Arrays.asList("lease_test");
		List<String> approvalPoliciesList = Arrays.asList("lease_test");
		List<String> deploymentLimitPoliciesList = Arrays.asList("lease_test");

		VraNgPolicy vraNgPolicy = new VraNgPolicy(csPoliciesList, resourcePoliciesList, leasePoliciesList, day2PoliciesList, approvalPoliciesList, deploymentLimitPoliciesList);
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "cs.json" });

		when(restClient.getVraPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createVraNgPolicy(any());
	}

	@Test
	void testImportContentWithCreateLogic() {
		List<String> csPoliciesList = Arrays.asList("cs_test");
		List<String> resourcePoliciesList = Arrays.asList("resource_test");
		List<String> leasePoliciesList = Arrays.asList("lease_test");
		List<String> day2PoliciesList = Arrays.asList("lease_test");
		List<String> approvalPoliciesList = Arrays.asList("lease_test");
		List<String> deploymentLimitPoliciesList = Arrays.asList("lease_test");

		VraNgPolicy vraNgPolicy = new VraNgPolicy(csPoliciesList, resourcePoliciesList, leasePoliciesList, day2PoliciesList, approvalPoliciesList, deploymentLimitPoliciesList);
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs_test",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicyFromServer = new VraNgContentSharingPolicy(
				"679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "cs_test.json" });

		when(restClient.getVraPolicies()).thenReturn(Arrays.asList());
		when(restClient.getPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicyFromServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createVraNgPolicy(any());
	}

	@Test
	void testImportContentWithNoFile() {
		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(vraNgPackageDescriptor, never()).getPolicy();
		verify(restClient, never()).getVraPolicies();
		verify(restClient, never()).getPolicy(anyString());
		verify(restClient, never()).createVraNgPolicy(any());
	}

	@Test
	void testImportContentForDifferentDestinationProject() {
		List<String> csPoliciesList = Arrays.asList("cs_test");
		List<String> resourcePoliciesList = Arrays.asList("resource_test");
		List<String> leasePoliciesList = Arrays.asList("lease_test");
		List<String> day2PoliciesList = Arrays.asList("lease_test");
		List<String> approvalPoliciesList = Arrays.asList("lease_test");
		List<String> deploymentLimitPoliciesList = Arrays.asList("lease_test");

		VraNgPolicy vraNgPolicy = new VraNgPolicy(csPoliciesList, resourcePoliciesList, leasePoliciesList, day2PoliciesList, approvalPoliciesList, deploymentLimitPoliciesList);
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs_test",
				"com.vmware.policy.catalog.entitlement", "asd1", "org2", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicyFromServer2 = new VraNgContentSharingPolicy(
				"679daee9-d63d-4ce2-9ee1-d4336861fe86", "cs_test",
				"com.vmware.policy.catalog.entitlement", "project2", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy toBeCreated = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86",
				"cs_test",
				"com.vmware.policy.catalog.entitlement", "project2", "org1", "HARD", "TEST", new VraNgDefinition());
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "cs_test.json" });

		when(restClient.getProjectId()).thenReturn("project2");
		when(restClient.getPolicyIdByName("cs_test")).thenReturn("679daee9-d63d-4ce2-9ee1-d4336861fe86");
		when(restClient.getVraPolicies()).thenReturn(Arrays.asList(csPolicyFromServer2));
		when(restClient.getPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86"))
				.thenReturn(csPolicyFromServer2);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient).createVraNgPolicy(argThat(x -> {
			return x.getId().equals(toBeCreated.getId()) 
				&& x.getOrgId().equals(toBeCreated.getOrgId())
				&& x.getProjectId().equals(toBeCreated.getProjectId());
		}));
	}
}
