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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class VraNgResourceQuotaPolicyStoreTest {

	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	/**
	 * store.
	 */
	private VraNgResourceQuotaPolicyStore store;
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
	private String dirPolicies = "policies";
	/**
	 * resourceQuotaPolicy.
	 */
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
		store = new VraNgResourceQuotaPolicyStore();
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
	void testExportContentWithNoResourceQuotaPolicies() {
		System.out.println(this.getClass() + "testExportContentWithNoResourceQuotaPolicies");
		//GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy());

		//TEST
		store.exportContent();

		File resourceQuotaPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();

		//VERIFY
		verify(restClient, never()).getResourceQuotaPolicies();
		verify(restClient, never()).getResourceQuotaPolicy(anyString());

		assertEquals(null, resourceQuotaPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllResourceQuotaPolicies() {
		System.out.println(this.getClass() + "testExportContentWithAllResourceQuotaPolicies");
		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"RQ01",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

		VraNgResourceQuotaPolicy rqPolicy2 = new VraNgResourceQuotaPolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"RQ02",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

		List<VraNgResourceQuotaPolicy> policies = Arrays.asList(rqPolicy, rqPolicy2);

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null, null, null, null, null, null));
		when(restClient.getResourceQuotaPolicies()).thenReturn(policies);
		when(restClient.getResourceQuotaPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(rqPolicy);
		when(restClient.getResourceQuotaPolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(rqPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File resourceQuotaPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();
		assertEquals(2, Objects.requireNonNull(resourceQuotaPolicyFolder.listFiles()).length);
	}

	@Test
	void testExportContentWithSpecificResourceQuotaPolicies() {
		System.out.println(this.getClass() + "testExportContentWithSpecificResourceQuotaPolicies");

		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"RQ01",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, Collections.singletonList("RQ01"), null, null, null, null);
		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getResourceQuotaPolicies()).thenReturn(Collections.singletonList(rqPolicy));
		when(restClient.getResourceQuotaPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(rqPolicy);

		// TEST
		store.exportContent();

		File resourceQuotaPolicyFolder = Paths
			.get(tempFolder.getRoot().getPath(), dirPolicies, resourceQuotaPolicy).toFile();

		// VERIFY
		assertEquals(1, Objects.requireNonNull(resourceQuotaPolicyFolder.listFiles()).length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		System.out.println("testImportContentWithUpdateLogic");
		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"RQ01",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"VIDM-L-01A",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, Collections.singletonList("RQ01"), null, null, null, null);
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.resourceQuotaPolicyFsMocks().addResourceQuotaPolicy(rqPolicy);

		File resourceQuotaPolicyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), resourceQuotaPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(resourceQuotaPolicyFolder, new String[] { "RQ01.json" });

		when(restClient.getResourceQuotaPolicies()).thenReturn(Arrays.asList(rqPolicy));
		when(restClient.getResourceQuotaPolicy("df60ff9e-4027-48d1-a2b5-5229b3cee282")).thenReturn(rqPolicy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createResourceQuotaPolicy(any());
	}


@Test
	void testImportContentWithCreateLogic() {
		System.out.println("testImportContentWithCreateLogic");
		VraNgPolicy vraNgPolicy = new VraNgPolicy(null, Arrays.asList("RQ01"), null, null, null, null);

		VraNgResourceQuotaPolicy rqPolicy = new VraNgResourceQuotaPolicy(
			"df60ff9e-4027-48d1-a2b5-5229b3cee282",
			"RQ01",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());
		VraNgResourceQuotaPolicy rqPolicyFromServer = new VraNgResourceQuotaPolicy(
			"2cf93725-38e9-4cb9-888a-a40994754c31",
			"RQ02",
			"com.vmware.policy.resource.quota",
			"b899c648-bf84-4d35-a61c-db212ecb4c1e",
			"b2c558c8-f20c-4da6-9bc3-d7561f64df16",
			"HARD",
			"TEST",
			new JsonObject(),
			new JsonObject());

	// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.resourceQuotaPolicyFsMocks().addResourceQuotaPolicy(rqPolicy);

		File resourceQuotaPolicyFolder = Paths
			.get(fsMocks.getTempFolderProjectPath().getPath(), resourceQuotaPolicy).toFile();

		AssertionsHelper.assertFolderContainsFiles(resourceQuotaPolicyFolder, new String[] { "RQ01.json" });

		when(restClient.getResourceQuotaPolicies()).thenReturn(Collections.emptyList());
		when(restClient.getResourceQuotaPolicy("2cf93725-38e9-4cb9-888a-a40994754c31")).thenReturn(rqPolicyFromServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createResourceQuotaPolicy(any());
	}

@Test
void testImportContentWithNoFile() {
	System.out.println(this.getClass() + "testImportContentWithNoFile");

	// START TEST
	store.importContent(tempFolder.getRoot());

	// VERIFY
	verify(vraNgPackageDescriptor, never()).getPolicy();
	verify(restClient, never()).getResourceQuotaPolicies();
	verify(restClient, never()).getResourceQuotaPolicy(anyString());
	verify(restClient, never()).createResourceQuotaPolicy(any());
}

}
