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
package com.vmware.pscoe.iac.artifact.aria.codestream.store;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.PackageMocked;
import com.vmware.pscoe.iac.artifact.aria.codestream.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.aria.codestream.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

public class CsPackageStoreTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private RestClientCs restClient;
	private ConfigurationCs config;
	private CsPackageStore store;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder", e);
		}
		restClient = Mockito.mock(RestClientCs.class);
		config = Mockito.mock(ConfigurationCs.class);
		store = new CsPackageStore(restClient, config);
	}

	@AfterEach
	void tearDown() {
		tempFolder.delete();
	}

	// -------------------------------------------------------------------------
	// Content-validation tests (validateContentMatchesDescriptor)
	// -------------------------------------------------------------------------

	/**
	 * Wraps a zip in a Package and calls importAllPackages on the store.
	 */
	private List<Package> doImport(File packageZip) {
		Package pkg = PackageFactory.getInstance(PackageType.CS, packageZip);
		List<Package> pkgs = new ArrayList<>();
		pkgs.add(pkg);
		return store.importAllPackages(pkgs, false, false);
	}

	@Test
	void importPackageValidationPassesWhenContentYamlAndPackageAreEmpty() throws IOException {
		// GIVEN: content.yaml with empty pipeline section; package has no content files.
		// Both descriptor and disk are empty for every content type → validation passes.
		String yaml = "---\npipeline:\n";
		Map<String, String> entries = new LinkedHashMap<>();
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		// WHEN / THEN: no exception expected
		List<Package> result = doImport(zip);
		assertNotNull(result);
	}

	@Test
	void importPackageValidationFailsWhenExactPipelineEntryInContentYamlMissingFromPackage() throws IOException {
		// GIVEN: content.yaml lists a pipeline; package has no pipeline files at all
		String yaml = "---\npipeline:\n  - MissingPipeline\n";
		Map<String, String> entries = new LinkedHashMap<>();
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		// WHEN / THEN: RuntimeException must mention the missing pipeline
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(zip));
		assertTrue(ex.getMessage().contains("MissingPipeline"),
				"Message should name the missing item; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("missing in package"),
				"Message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationFailsWhenPackageContainsPipelineNotListedInContentYaml() throws IOException {
		// GIVEN: content.yaml has empty pipeline list; package has an extra pipeline file
		String yaml = "---\npipeline:\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("pipelines/ExtraPipeline.yaml", "---\nname: ExtraPipeline\n");
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		// WHEN / THEN: RuntimeException must mention the unlisted file
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(zip));
		assertTrue(ex.getMessage().contains("ExtraPipeline"),
				"Message should name the unlisted item; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("not listed in content.yaml"),
				"Message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationFailsWhenEndpointListedInContentYamlIsMissingFromPackage() throws IOException {
		// GIVEN: content.yaml lists an endpoint; package has no endpoint files
		String yaml = "---\nendpoint:\n  - MissingEndpoint\n";
		Map<String, String> entries = new LinkedHashMap<>();
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		// WHEN / THEN: RuntimeException must mention the missing endpoint
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(zip));
		assertTrue(ex.getMessage().contains("MissingEndpoint"),
				"Message should name the missing item; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("missing in package"),
				"Message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationIsSkippedWhenNoContentYamlIsPresent() throws IOException {
		// GIVEN: package has NO content.yaml and NO content files — validation must be skipped.
		// All CS type stores find empty directories and return without REST calls.
		Map<String, String> entries = new LinkedHashMap<>();
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), null, entries);

		// WHEN / THEN: no exception expected — validation is opt-in
		List<Package> result = doImport(zip);
		assertNotNull(result);
	}

}