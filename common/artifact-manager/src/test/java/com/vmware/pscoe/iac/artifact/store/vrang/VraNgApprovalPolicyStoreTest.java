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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPolicy;
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

public class VraNgApprovalPolicyStoreTest  {
	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	/**
	 * store.
	 */
	private VraNgApprovalPolicyStore store;
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
	 * approval Policy.
	 */
	private final String approvalPolicy = "approval";

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
		store = new VraNgApprovalPolicyStore();
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
	void testExportContentWithNoApprovalPolicies() {
		System.out.println(this.getClass() + "testExportContentWithNoApprovalPolicies");
		//GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File approvalPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		//VERIFY
		verify(restClient, never()).getApprovalPolicies();
		verify(restClient, never()).getApprovalPolicy(anyString());

		assertEquals(null, approvalPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllApprovalPolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllApprovalPolicies");
		VraNgApprovalPolicy policy1 = new VraNgApprovalPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"D2A01",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

		VraNgApprovalPolicy policy2 = new VraNgApprovalPolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"D2A02",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

		List<VraNgApprovalPolicy> policies = Arrays.asList(policy1, policy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null, null, null, null, null));
		when(restClient.getApprovalPolicies()).thenReturn(policies);
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy1);
		when(restClient.getApprovalPolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(policy2);

		// TEST
		store.exportContent();

		// VERIFY
		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();
		assertEquals(2, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testExportContentWithSpecificApprovalPolicies() {
		System.out.println(this.getClass() + "testExportContentWithSpecificApprovalPolicies");

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"D2A01",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, Collections.singletonList("D2A01"), null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getApprovalPolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		// TEST
		store.exportContent();

		File policyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		// VERIFY
		assertEquals(1, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		System.out.println("testImportContentWithUpdateLogic");
		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"D2A01",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, Collections.singletonList("D2A01"), null, null, null);
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), approvalPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "D2A01.json" });

		when(restClient.getApprovalPolicies()).thenReturn(Arrays.asList(policy));
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createApprovalPolicy(any());
	}


	@Test
	void testImportContentWithCreateLogic() {
		System.out.println("testImportContentWithCreateLogic");
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, Arrays.asList("D2A01"), null, null, null);

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"D2A01",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgApprovalPolicy policyFromServer = new VraNgApprovalPolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"D2A02",
			"com.vmware.policy.deployment.action",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), approvalPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "D2A01.json" });

		when(restClient.getApprovalPolicies()).thenReturn(Collections.emptyList());
		when(restClient.getApprovalPolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(policyFromServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createApprovalPolicy(any());
	}

	@Test
	void testImportContentWithNoFile() {
		System.out.println(this.getClass() + "testImportContentWithNoFile");

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(vraNgPackageDescriptor, never()).getPolicy();
		verify(restClient, never()).getApprovalPolicies();
		verify(restClient, never()).getApprovalPolicy(anyString());
		verify(restClient, never()).createApprovalPolicy(any());
	}

}
