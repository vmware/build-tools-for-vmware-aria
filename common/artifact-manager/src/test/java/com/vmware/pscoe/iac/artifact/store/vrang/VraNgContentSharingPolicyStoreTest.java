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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.vmware.pscoe.iac.artifact.model.vrang.*;
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
	 * organization1.
	 */
	private VraNgOrganization organization = new VraNgOrganization();
	/**
	 * scope1.
	 */
	private String scope1;
	/**
	 * scope2.
	 */
	private String scope2;
	/**
	 * projectId.
	 */
	private static Integer projectId = 1;
	/**
	 * dirContentSharingPolicies.
	 */
	private static final String dirContentSharingPolicies = "policies";
	/**
	 * contentSharingPolicy.
	 */
	private static final String contentSharingPolicy = "content-sharing";
	/**
	 * organizationId.
	 */
	private static final String organizationId = "1";

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

		int nextProject = projectId;
		this.organization.setId(organizationId);
		this.organization.setName(String.format("org%s", organizationId));
		this.scope1 = String.format("projectId%s", projectId);
		this.scope2 = String.format("projectId%s", nextProject++);

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		when(config.getOrgId()).thenReturn(organization.getId());
		when(config.getOrgName()).thenReturn(organization.getName());
		when(restClient.getOrganizationById(this.organization.getId())).thenReturn(this.organization);
		when(restClient.getOrganizationByName(this.organization.getName())).thenReturn(this.organization);
		when(restClient.getProjectIdByName(Mockito.anyString())).thenReturn(projectId.toString());
		when(restClient.getProjectId()).thenReturn(projectId.toString());

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
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		// VERIFY
		verify(restClient, never()).getContentSharingPolicies();
		verify(restClient, never()).getContentSharingPolicy(anyString());

		assertEquals(null, contentSharingPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllContentSharingPolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllContentSharingPolicies");

		VraNgContentSharingPolicy csPolicy1 = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST", new VraNgDefinition(), this.scope1, this.organization.getName());
		VraNgContentSharingPolicy csPolicy2 = new VraNgContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c", "testing",
				"com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST", new VraNgDefinition(), this.scope1, this.organization.getName());

		List<VraNgContentSharingPolicy> policies = Arrays.asList(csPolicy1, csPolicy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null, null, null, null, null));
		when(restClient.getContentSharingPolicies()).thenReturn(policies);
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy1);
		when(restClient.getContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c")).thenReturn(csPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File contentSharingPolicyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();
		assertEquals(2, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificContentSharingPolicies() {
		System.out.println(this.getClass() + "testExportContentWithSpecificContentSharingPolicies");

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "cs", "com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST",
				new VraNgDefinition(), this.scope1, this.organization.getName());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"), null, null, null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getContentSharingPolicy("1")).thenReturn(csPolicy);

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		// VERIFY
		assertEquals(1, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testImportContentUpdate() {
		System.out.println("testImportContentUpdate");
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"), null, null, null, null, null);
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST", new VraNgDefinition(), this.scope1, this.organization.getName());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);
		File contentSharingPolicyFolder = Paths.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();
		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "cs.json" });

		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicy));
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createContentSharingPolicy(any());
	}

	@Test
	void testImportContentCreate() {
		System.out.println("testImportContentCreate");
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("test"), null, null, null, null, null);
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("1", "test", "com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST",
				new VraNgDefinition(), this.scope1, this.organization.getName());

		VraNgContentSharingPolicy csPolicyFromServer = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87", "cs",
				"com.vmware.policy.catalog.entitlement", "1", "1", "HARD", "TEST", new VraNgDefinition(), this.scope1, this.organization.getName());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();
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
		System.out.println("testImportContentWithNoFile");

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
		System.out.println("testImportContentForDifferentDestinationProject");
		VraNgContentSharingPolicy toBeCreated = this.prepareTestObjects(this.scope1, this.organization.getName());

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient).createContentSharingPolicy(argThat(item -> {
			return item.getId().equals(toBeCreated.getId()) && item.getOrgId().equals(toBeCreated.getOrgId())
					&& item.getProjectId().equals(toBeCreated.getProjectId());
		}));
	}

	@Test
	void testImportContentForEmptyScope() {
		System.out.println("testImportContentForEmptyScope");
		VraNgContentSharingPolicy toBeCreated = this.prepareTestObjects(null, this.organization.getName());

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient).createContentSharingPolicy(argThat(item -> {
			return item.getId().equals(toBeCreated.getId()) && item.getOrgId().equals(toBeCreated.getOrgId())
					&& item.getProjectId().equals(toBeCreated.getProjectId());
		}));
	}

	@Test
	void testImportContentForDifferentScope() {
		System.out.println("testImportContentForDifferentScope");
		VraNgContentSharingPolicy toBeCreated = this.prepareTestObjects(this.scope2, this.organization.getName());

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient).createContentSharingPolicy(argThat(item -> {
			return item.getId().equals(toBeCreated.getId()) && item.getOrgId().equals(toBeCreated.getOrgId())
					&& item.getProjectId().equals(toBeCreated.getProjectId());
		}));
	}

	@Test
	void testExportContentWithPolicyAlreadyInFile() {
		System.out.println(this.getClass() + ".testExportContentWithPolicyAlreadyInFile");

		VraNgContentSharingPolicy policy = new VraNgContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Collections.singletonList("CS01"), null, null, null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(policy);
		// TEST
		store.exportContent();

		// VERIFY
		// export should overwrite policy, not create a new file.
		assertEquals(1, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testExportContentWithSpecificPoliciesAndDuplicateFiles() {
		System.out.println(this.getClass() + ".testExportContentWithSpecificPoliciesAndDuplicateFiles");
		VraNgContentSharingPolicy policyInFile = new VraNgContentSharingPolicy("d160119e-4027-48d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "SOFT", "TEST", new VraNgDefinition(),
				this.scope1, this.organization.getName());

		VraNgContentSharingPolicy policy = new VraNgContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Collections.singletonList("CS01"), null, null, null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(policyInFile);
		policyInFile.setName("CS01_1");
		fsMocks.contentSharingFsMocks().addContentSharingPolicy(policyInFile);
		policyInFile.setName("CS01_2");
		fsMocks.contentSharingFsMocks().addContentSharingPolicy(policyInFile);
		policyInFile.setName("CS01_3");
		fsMocks.contentSharingFsMocks().addContentSharingPolicy(policyInFile);

		// TEST
		store.exportContent();

		// VERIFY
		assertEquals(5, Objects.requireNonNull(policyFolder.listFiles()).length);
		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "CS01.json", "CS01_1.json", "CS01_2.json", "CS01_3.json", "CS01_4.json" });
	}

	@Test
	void testExportContentWithSpecificPoliciesAndDuplicateNames() {
		System.out.println(this.getClass() + ".testExportContentWithSpecificPoliciesAndDuplicateNames");

		VraNgContentSharingPolicy policy = new VraNgContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgContentSharingPolicy policy1 = new VraNgContentSharingPolicy("df60ff9e-4027-11d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST1", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgContentSharingPolicy policy2 = new VraNgContentSharingPolicy("df60ff9e-4027-12d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST2", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgContentSharingPolicy policy3 = new VraNgContentSharingPolicy("df60ff9e-4027-13d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST3", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgContentSharingPolicy policy4 = new VraNgContentSharingPolicy("df60ff9e-4027-14d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST4", new VraNgDefinition(),
				this.scope1, this.organization.getName());
		VraNgContentSharingPolicy policy5 = new VraNgContentSharingPolicy("df60ff9e-4027-15d1-a2b5-5229b3cee282", "CS01",
				"com.vmware.policy.catalog.entitlement", "b899c648-bf84-4d35-a61c-db212ecb4c1e", "VIDM-L-01A", "HARD", "TEST5", new VraNgDefinition(),
				this.scope1, this.organization.getName());

		VraNgPolicy vraNgPolicy = new VraNgPolicy(Collections.singletonList("CS01"), null, null, null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(policy, policy1, policy2, policy3, policy4, policy5));
		when(restClient.getContentSharingPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);
		when(restClient.getContentSharingPolicy("df60ff9e-4027-11d1-a2b5-5229b3cee282")).thenReturn(policy1);
		when(restClient.getContentSharingPolicy("df60ff9e-4027-12d1-a2b5-5229b3cee282")).thenReturn(policy2);
		when(restClient.getContentSharingPolicy("df60ff9e-4027-13d1-a2b5-5229b3cee282")).thenReturn(policy3);
		when(restClient.getContentSharingPolicy("df60ff9e-4027-14d1-a2b5-5229b3cee282")).thenReturn(policy4);
		when(restClient.getContentSharingPolicy("df60ff9e-4027-15d1-a2b5-5229b3cee282")).thenReturn(policy5);

		File policyFolder = Paths.get(tempFolder.getRoot().getPath(), dirContentSharingPolicies, contentSharingPolicy).toFile();

		// TEST
		store.exportContent();

		// VERIFY
		assertEquals(6, Objects.requireNonNull(policyFolder.listFiles()).length);
		AssertionsHelper.assertFolderContainsFiles(policyFolder,
				new String[] { "CS01.json", "CS01_1.json", "CS01_2.json", "CS01_3.json", "CS01_4.json", "CS01_5.json" });
	}

	private VraNgContentSharingPolicy prepareTestObjects(String scope, String organization) {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("test"), null, null, null, null, null);
		VraNgDefinition contentSourcesDefinition = new VraNgDefinition();
		VraNgEntitledUser entitledUser = new VraNgEntitledUser();
		VraNgItem vraNgItem = new VraNgItem();
		vraNgItem.setId("1");
		vraNgItem.setName("Content Source 1");
		vraNgItem.setType("CATALOG_SOURCE_IDENTIFIER");
		entitledUser.setItems(Arrays.asList(new VraNgItem[] { vraNgItem }));
		contentSourcesDefinition.setEntitledUsers(Arrays.asList(new VraNgEntitledUser[] { entitledUser }));

		VraNgDefinition contentItemsDefinition = new VraNgDefinition();
		entitledUser = new VraNgEntitledUser();
		vraNgItem = new VraNgItem();
		vraNgItem.setId("1");
		vraNgItem.setName("Catalog Item 1");
		vraNgItem.setType("CATALOG_ITEM_IDENTIFIER");
		entitledUser.setItems(Arrays.asList(new VraNgItem[] { vraNgItem }));

		VraNgContentSharingPolicy csPolicyFromFile = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86", "test",
				"com.vmware.policy.catalog.entitlement", "1", this.organization.getId(), "HARD", "TEST", contentSourcesDefinition, scope, organization);

		VraNgContentSharingPolicy csPolicyFromServer2 = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86", "test",
				"com.vmware.policy.catalog.entitlement", "1", this.organization.getId(), "HARD", "TEST", contentSourcesDefinition, scope, organization);

		VraNgContentSharingPolicy toBeCreated = new VraNgContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86", "test",
				"com.vmware.policy.catalog.entitlement", "1", this.organization.getId(), "HARD", "TEST", contentItemsDefinition, scope, organization);
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicyFromFile);
		File contentSharingPolicyFolder = Paths.get(fsMocks.getTempFolderProjectPath().getPath(), contentSharingPolicy).toFile();
		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[] { "test.json" });

		when(restClient.getContentSharingPolicyIdByName("test")).thenReturn("679daee9-d63d-4ce2-9ee1-d4336861fe86");
		when(restClient.getContentSharingPolicies()).thenReturn(Arrays.asList(csPolicyFromServer2));
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe86")).thenReturn(csPolicyFromServer2);
		when(restClient.getProjectIdByName(Mockito.anyString())).thenReturn("1");

		return toBeCreated;
	}
}
