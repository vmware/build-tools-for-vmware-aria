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

import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class VraNgLeasePolicyStoreTest  {
	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	/**
	 * store.
	 */
	private VraNgLeasePolicyStore store;
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
	 * dirPolicies.
	 */
	private final String dirPolicies = "policies";
	/**
	 * Lease Policy.
	 */
	private final String leasePolicy = "lease";

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
		store = new VraNgLeasePolicyStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

		VraNgOrganization org = new VraNgOrganization();
		org.setId("b2c558c8-f20c-4da6-9bc3-d7561f64df16");
		org.setName("VIDM-L-01A");

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		when(config.getOrgId()).thenReturn("b2c558c8-f20c-4da6-9bc3-d7561f64df16");
		when(config.getOrgName()).thenReturn("VIDM-L-01A");
		when(restClient.getOrganizationById("b2c558c8-f20c-4da6-9bc3-d7561f64df16")).thenReturn(org);
		when(restClient.getOrganizationByName("VIDM-L-01A")).thenReturn(org);
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
	void testExportContentWithNoLeasePolicies() {
		System.out.println(this.getClass() + "testExportContentWithNoLeasePolicies");
		//GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File leasePolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, leasePolicy).toFile();

		//VERIFY
		verify(restClient, never()).getLeasePolicies();
		verify(restClient, never()).getLeasePolicy(anyString());

		assertEquals(null, leasePolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllLeasePolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllLeasePolicies");
		VraNgLeasePolicy policy1 = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());

		VraNgLeasePolicy policy2 = new VraNgLeasePolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"LP02",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());

		List<VraNgLeasePolicy> policies = Arrays.asList(policy1, policy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null, null, null, null, null));
		when(restClient.getLeasePolicies()).thenReturn(policies);
		when(restClient.getLeasePolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy1);
		when(restClient.getLeasePolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(policy2);

		// TEST
		store.exportContent();

		// VERIFY
		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, leasePolicy).toFile();
		assertEquals(2, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testExportContentWithSpecificLeasePolicies() {
		System.out.println(this.getClass() + "testExportContentWithSpecificLeasePolicies");

		VraNgLeasePolicy policy = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, Collections.singletonList("LP01"), null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getLeasePolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getLeasePolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		// TEST
		store.exportContent();

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, leasePolicy).toFile();

		// VERIFY
		assertEquals(1, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		System.out.println("testImportContentWithUpdateLogic");
		VraNgLeasePolicy policy = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",

			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, Collections.singletonList("LP01"), null, null);
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getLeasePolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), leasePolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "LP01.json" });

		when(restClient.getLeasePolicies()).thenReturn(Arrays.asList(policy));
		when(restClient.getLeasePolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createLeasePolicy(any());
	}


	@Test
	void testImportContentWithCreateLogic() {
		System.out.println("testImportContentWithCreateLogic");
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, Collections.singletonList("LP01"), null, null);

		VraNgLeasePolicy policy = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());
		VraNgLeasePolicy policyFromServer = new VraNgLeasePolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"LP02",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getLeasePolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), leasePolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "LP01.json" });

		when(restClient.getLeasePolicies()).thenReturn(Collections.emptyList());
		when(restClient.getLeasePolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(policyFromServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createLeasePolicy(any());
	}

	@Test
	void testImportContentWithNoFile() {
		System.out.println(this.getClass() + "testImportContentWithNoFile");

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(vraNgPackageDescriptor, never()).getPolicy();
		verify(restClient, never()).getLeasePolicies();
		verify(restClient, never()).getLeasePolicy(anyString());
		verify(restClient, never()).createLeasePolicy(any());
	}
	@Test
	void testExportContentWithPolicyAlreadyInFile() {
		System.out.println(this.getClass() + ".testExportContentWithPolicyAlreadyInFile");

		VraNgLeasePolicy policy = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null , Collections.singletonList("LP01"), null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getLeasePolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getLeasePolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, leasePolicy).toFile();


		fsMocks.getLeasePolicyFsMocks().addPolicy(policy);
		// TEST
		store.exportContent();

		// VERIFY
		//export should overwrite policy, not create a new file.
		assertEquals(1, Objects.requireNonNull(policyFolder.listFiles()).length);
	}
	@Test
	void testExportContentWithSpecificPoliciesAndDuplicateFiles() {
		System.out.println(this.getClass() + ".testExportContentWithSpecificPoliciesAndDuplicateFiles");
		VraNgLeasePolicy policyInFile = new VraNgLeasePolicy(
			"d160119e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"SOFT",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());

		VraNgLeasePolicy policy = new VraNgLeasePolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"LP01",
			"com.vmware.policy.deployment.lease",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, Collections.singletonList("LP01"), null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getLeasePolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getLeasePolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, leasePolicy).toFile();


		fsMocks.getLeasePolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("LP01_1");
		fsMocks.getLeasePolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("LP01_2");
		fsMocks.getLeasePolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("LP01_3");
		fsMocks.getLeasePolicyFsMocks().addPolicy(policyInFile);

		// TEST
		store.exportContent();

		// VERIFY
		assertEquals(5, Objects.requireNonNull(policyFolder.listFiles()).length);
		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "LP01.json", "LP01_1.json", "LP01_2.json", "LP01_3.json", "LP01_4.json" });
	}

}
