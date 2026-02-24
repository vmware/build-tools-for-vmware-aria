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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.store;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.PackageMocked;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.common.strategy.Strategy;
import com.vmware.pscoe.iac.artifact.common.strategy.StrategySkipOldVersions;

public class VroPackageStoreTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private VroPackageStore vroPackageStore;
	private RestClientVro restClientVro;

	@BeforeEach
	void init() {
		restClientVro = Mockito.mock(RestClientVro.class);

		ArrayList<Strategy> strategies = new ArrayList<>();
		strategies.add(new StrategySkipOldVersions());

		vroPackageStore = new VroPackageStore(restClientVro, strategies);
	}

	@Test
	void testImportAllPackagesShouldBeSucessfull() throws IOException {
		tempFolder.create();
		File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123",
				"DashboardName", "AlertDefinitions");
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		List<Package> packages = new ArrayList<>();
		packages.add(vropsPkg);

		Mockito
				.when(restClientVro.importPackage(vropsPkg, false, true))
				.thenReturn(vropsPkg);

		vroPackageStore.importAllPackages(packages, false, true);
	}

	@Test
	void testImportPackageShouldBeSucessfull() throws IOException {
		tempFolder.create();
		File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123",
				"DashboardName", "AlertDefinitions");
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		Mockito
				.when(restClientVro.importPackage(vropsPkg, false, true))
				.thenReturn(vropsPkg);

		vroPackageStore.importPackage(vropsPkg, false, true);
	}
}
