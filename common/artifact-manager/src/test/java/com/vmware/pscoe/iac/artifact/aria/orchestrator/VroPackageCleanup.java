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
package com.vmware.pscoe.iac.artifact.aria.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

class VroPackageCleanup {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetOrderedPackages() throws IOException {
		List<String> pkgsNames = new ArrayList<>();

		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-1.3.0");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-1.5.2-SNAPSHOT");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.3.0-SNAPSHOT");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.3.0");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.5.0");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.5.1-SNAPSHOT");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.5.1");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.5.2-SNAPSHOT");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.6.0");
		pkgsNames.add("com.vmware.pscoe.library.vra.dispatcher-2.10.0");

		List<Package> pkgs = pkgsNames.stream()
				.map(c -> PackageFactory.getInstance(PackageType.VRO, new File(c + ".zip")))
				.collect(Collectors.toList());

		// Exception from the rule. Unit test must be repeatable
		Collections.shuffle(pkgs);
		Collections.sort(pkgs);

		for (int i = 0; i < pkgs.size(); i++) {
			assertEquals(pkgs.get(i).getFQName(), pkgsNames.get(i));
		}
	}

}
