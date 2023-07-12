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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
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
	private VraNgContentSharingPolicyStore store;
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
		store = new VraNgContentSharingPolicyStore();
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
		verify(restClient, never()).getContentSharingPolicies();
		verify(restClient, never()).getContentSharingPolicy(anyString());

		assertEquals(null, contentSharingPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllContentSharingPolicies() {

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicy2 = new VraNgContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c",
				"testing",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		List<VraNgContentSharingPolicy> policies = Arrays.asList(csPolicy, csPolicy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null));
		when(restClient.getContentSharingPolicies()).thenReturn(policies);
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);
		when(restClient.getContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c")).thenReturn(csPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();
		assertEquals(2, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificContentSharingPolicies() {

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"));
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getContentSharingPolicy("1")).thenReturn(csPolicy);

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		// VERIFY
		assertEquals(1, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"));
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "cs.json" });

		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createContentSharingPolicy(any());
	}

	@Test
	void testImportContentWithCreateLogic() {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("test"));
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "test",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicyFromServer = new VraNgContentSharingPolicy(
				"679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "project1", "org1", "HARD", "TEST", new VraNgDefinition());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "test.json" });

		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList());
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicyFromServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createContentSharingPolicy(any());
	}

	@Test
	void testImportContentWithNoFile() {
		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(vraNgPackageDescriptor, never()).getPolicy();
		verify(restClient, never()).getContentSharingPolicies();
		verify(restClient, never()).getContentSharingPolicy(anyString());
		verify(restClient, never()).createContentSharingPolicy(any());
	}

	@Test
	void testImportContentForDifferentDestinationProject() {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("test"));
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "test",
				"com.vmware.policy.catalog.entitlement", "asd1", "org2", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicyFromServer2 = new VraNgContentSharingPolicy(
				"679daee9-d63d-4ce2-9ee1-d4336861fe86", "test",
				"com.vmware.policy.catalog.entitlement", "project2", "org1", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy toBeCreated = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86",
				"test",
				"com.vmware.policy.catalog.entitlement", "project2", "org1", "HARD", "TEST", new VraNgDefinition());
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "test.json" });

		when(restClient.getProjectId()).thenReturn("project2");
		when(restClient.getContentSharingPolicyIdByName("test")).thenReturn("679daee9-d63d-4ce2-9ee1-d4336861fe86");
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicyFromServer2));
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86"))
				.thenReturn(csPolicyFromServer2);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient).createContentSharingPolicy(argThat(x -> {
			return x.getId().equals(toBeCreated.getId()) 
				&& x.getOrgId().equals(toBeCreated.getOrgId())
				&& x.getProjectId().equals(toBeCreated.getProjectId());
		}));
	}
}
