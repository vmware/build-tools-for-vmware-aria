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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPolicy;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

public class VraNgContentSharingPolicyStoreTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected VraNgContentSharingPolicyStore store;
	protected RestClientVraNg restClient;
	protected Package pkg;
	protected ConfigurationVraNg config;
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;
	protected String DIR_CONTENT_SHARING_POLICIES = "policies";
	protected String CONTENT_SHARING_POLICY = "content-sharing";

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

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

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
				.get(tempFolder.getRoot().getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();

		//VERIFY
		verify(restClient, never()).getContentSharingPolicyIds();
		verify(restClient, never()).getContentSharingPolicy(anyString());

		assertEquals(null, contentSharingPolicyFolder.listFiles());
	}

	@Test
	void testExportContentWithAllContentSharingPolicies() {

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("cs",
				"com.vmware.policy.catalog.entitlement", "HARD", "TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicy2 = new VraNgContentSharingPolicy("testing",
				"com.vmware.policy.catalog.entitlement", "HARD","TEST", new VraNgDefinition());

		List<String> Ids = Arrays.asList(
				"679daee9-d63d-4ce2-9ee1-d4336861fe87", "94824034-ef7b-4728-a6c2-fb440aff590c");

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(new VraNgPolicy(null));
		when(restClient.getContentSharingPolicyIds()).thenReturn(Ids);
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);
		when(restClient.getContentSharingPolicy("94824034-ef7b-4728-a6c2-fb440aff590c")).thenReturn(csPolicy2);

		// TEST
		store.exportContent();

		// VERIFY
		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();
		assertEquals(2, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testExportContentWithSpecificContentSharingPolicies() {

		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("cs",
				"com.vmware.policy.catalog.entitlement", "HARD", "TEST", new VraNgDefinition());

		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"));

		// // GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);
		when(restClient.getContentSharingPolicyIds()).thenReturn(Arrays.asList(""));
		when(restClient.getContentSharingPolicy("")).thenReturn(csPolicy);

		// TEST
		store.exportContent();

		File contentSharingPolicyFolder = Paths
				.get(tempFolder.getRoot().getPath(), DIR_CONTENT_SHARING_POLICIES, CONTENT_SHARING_POLICY).toFile();

		// VERIFY
		assertEquals(1, contentSharingPolicyFolder.listFiles().length);
	}

	@Test
	void testImportContentWithUpdateLogic() {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("cs"));
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("cs",
				"com.vmware.policy.catalog.entitlement", "HARD", "TEST", new VraNgDefinition());

		List<String> Ids = Arrays.asList(
					"679daee9-d63d-4ce2-9ee1-d4336861fe87");
		
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths.get(fsMocks.getTempFolderProjectPath().getPath(), CONTENT_SHARING_POLICY).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[]{"cs.json"});
		
		when(restClient.getContentSharingPolicyIds()).thenReturn(Ids);
		when(restClient.getContentSharingPolicy("679daee9-d63d-4ce2-9ee1-d4336861fe87")).thenReturn(csPolicy);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).updateContentSharingPolicy(any());
	}

	@Test
	void testImportContentWithCreateLogic() {
		VraNgPolicy vraNgPolicy = new VraNgPolicy(Arrays.asList("test"));
		VraNgContentSharingPolicy csPolicy = new VraNgContentSharingPolicy("test",
				"com.vmware.policy.catalog.entitlement", "HARD","TEST", new VraNgDefinition());

		VraNgContentSharingPolicy csPolicyFromServer = new VraNgContentSharingPolicy("cs",
				"com.vmware.policy.catalog.entitlement", "HARD", "TEST", new VraNgDefinition());

		List<String> Ids = Arrays.asList("679daee9-d63d-4ce2-9ee1-d4336861fe87");
		
		// GIVEN
		when(vraNgPackageDescriptor.getPolicy()).thenReturn(vraNgPolicy);

		fsMocks.contentSharingFsMocks().addContentSharingPolicy(csPolicy);

		File contentSharingPolicyFolder = Paths.get(fsMocks.getTempFolderProjectPath().getPath(), CONTENT_SHARING_POLICY).toFile();

		AssertionsHelper.assertFolderContainsFiles(contentSharingPolicyFolder, new String[]{"test.json"});
		
		when(restClient.getContentSharingPolicyIds()).thenReturn(Ids);
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
		verify(restClient, never()).getContentSharingPolicyIds();
		verify(restClient, never()).getContentSharingPolicy(anyString());
		verify(restClient, never()).createContentSharingPolicy(any());
		verify(restClient, never()).updateContentSharingPolicy(any());
	}
}
