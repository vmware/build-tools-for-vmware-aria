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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgApprovalPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgOrganization;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class VraNgApprovalPolicyStoreTest {
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
	 * Logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgApprovalPolicyStoreTest.class);

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
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		// TEST
		store.exportContent();

		File approvalPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		// VERIFY
		verify(restClient, never()).getApprovalPolicies();
		verify(restClient, never()).getApprovalPolicy(anyString());

		assertEquals(null, approvalPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllApprovalPolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllApprovalPolicies");
		VraNgApprovalPolicy policy1 = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());

		VraNgApprovalPolicy policy2 = new VraNgApprovalPolicy(
				"2cf93725-38e9-4cb9-888a-a40994754c31",
				"AP02",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
				"HARD",
				"TEST",
				new JsonObject(),
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
		System.out.println(this.getClass() + ".testExportContentWithSpecificApprovalPolicies");

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);
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
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), approvalPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "AP01.json" });

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
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgApprovalPolicy policyFromServer = new VraNgApprovalPolicy(
				"2cf93725-38e9-4cb9-888a-a40994754c31",
				"AP02",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());

		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policy);

		File policyFolder = Paths
				.get(fsMocks.getTempFolderProjectPath().getPath(), approvalPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(policyFolder, new String[] { "AP01.json" });

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

	@Test
	void testExportContentWithSpecificApprovalPoliciesAndDuplicateFiles() {
		System.out.println(this.getClass() + ".testExportContentWithSpecificApprovalPoliciesAndDuplicateFiles");
		VraNgApprovalPolicy policyInFile = new VraNgApprovalPolicy(
				"d160119e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"SOFT",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getApprovalPolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("AP01_1");
		fsMocks.getApprovalPolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("AP01_2");
		fsMocks.getApprovalPolicyFsMocks().addPolicy(policyInFile);
		policyInFile.setName("AP01_3");
		fsMocks.getApprovalPolicyFsMocks().addPolicy(policyInFile);

		// TEST
		store.exportContent();

		// VERIFY
		assertEquals(5, Objects.requireNonNull(policyFolder.listFiles()).length);
		AssertionsHelper.assertFolderContainsFiles(policyFolder,
				new String[] { "AP01.json", "AP01_1.json", "AP01_2.json", "AP01_3.json", "AP01_4.json" });
	}

	@Test
	void testExportContentWithPolicyAlreadyInFile() {
		System.out.println(this.getClass() + ".testExportContentWithPolicyAlreadyInFile");

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getApprovalPolicies()).thenReturn(Collections.singletonList(policy));
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);

		File policyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		fsMocks.getApprovalPolicyFsMocks().addPolicy(policy);
		// TEST
		store.exportContent();

		// VERIFY
		// export should overwrite policy, not create a new file.
		assertEquals(1, Objects.requireNonNull(policyFolder.listFiles()).length);
	}

	@Test
	void testExportContentWithSpecificApprovalPoliciesAndDuplicateNames() {
		System.out.println(this.getClass() + ".testExportContentWithSpecificApprovalPoliciesAndDuplicateNames");
		VraNgApprovalPolicy policyInFile = new VraNgApprovalPolicy(
				"d160119e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"SOFT",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());

		VraNgApprovalPolicy policy = new VraNgApprovalPolicy(
				"df60ff9e-4027-48d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgApprovalPolicy policy1 = new VraNgApprovalPolicy(
				"df60ff9e-4027-11d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST1",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgApprovalPolicy policy2 = new VraNgApprovalPolicy(
				"df60ff9e-4027-12d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST2",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgApprovalPolicy policy3 = new VraNgApprovalPolicy(
				"df60ff9e-4027-13d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST3",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());
		VraNgApprovalPolicy policy4 = new VraNgApprovalPolicy(
				"df60ff9e-4027-14d1-a2b5-5229b3cee282",
				"AP01",
				"com.vmware.policy.approval",
				"b899c648-bf84-4d35-a61c-db212ecb4c1e",
				"VIDM-L-01A",
				"HARD",
				"TEST4",
				new JsonObject(),
				new JsonObject(),
				new JsonObject());

		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, null, null, null, Collections.singletonList("AP01"), null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getApprovalPolicies()).thenReturn(Arrays.asList(policy, policy1, policy2, policy3, policy4));
		when(restClient.getApprovalPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(policy);
		when(restClient.getApprovalPolicy("df60ff9e-4027-11d1-a2b5-5229b3cee282")).thenReturn(policy1);
		when(restClient.getApprovalPolicy("df60ff9e-4027-12d1-a2b5-5229b3cee282")).thenReturn(policy2);
		when(restClient.getApprovalPolicy("df60ff9e-4027-13d1-a2b5-5229b3cee282")).thenReturn(policy3);
		when(restClient.getApprovalPolicy("df60ff9e-4027-14d1-a2b5-5229b3cee282")).thenReturn(policy4);

		File policyFolder = Paths
				.get(tempFolder.getRoot().getPath(), dirPolicies, approvalPolicy).toFile();

		// TEST
		store.exportContent();

		// VERIFY
		assertEquals(5, Objects.requireNonNull(policyFolder.listFiles()).length);
		AssertionsHelper.assertFolderContainsFiles(policyFolder,
				new String[] { "AP01.json", "AP01_1.json", "AP01_2.json", "AP01_3.json", "AP01_4.json" });
	}

}
