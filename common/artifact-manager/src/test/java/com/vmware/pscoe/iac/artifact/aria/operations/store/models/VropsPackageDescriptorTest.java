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
package com.vmware.pscoe.iac.artifact.aria.operations.store.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VropsPackageDescriptorTest {
	@TempDir
	File tempDir;

	@Test
	void getInstanceDistinguishesListsWithoutValueAndMissingOnes() throws IOException {
		// GIVEN
		File contentYaml = new File(tempDir, "content.yaml");
		Files.write(contentYaml.toPath(), ("---\nview:\n  - View One\ndashboard:\nreport: []\nsuper-metric:\n"
				+ "  - \"SM*\"\ndefault-policy: Default Policy\n").getBytes(StandardCharsets.UTF_8));

		// WHEN
		VropsPackageDescriptor descriptor = VropsPackageDescriptor.getInstance(contentYaml);

		// THEN
		assertEquals(Arrays.asList("View One"), descriptor.getView());
		// no value: everything is processed
		assertNull(descriptor.getDashboard());
		// empty list: nothing is processed
		assertEquals(Collections.emptyList(), descriptor.getReport());
		assertEquals(Arrays.asList("SM*"), descriptor.getSuperMetric());
		// missing: nothing is processed
		assertEquals(Collections.emptyList(), descriptor.getAlertDefinition());
		assertEquals(Collections.emptyList(), descriptor.getSymptomDefinition());
		assertEquals(Collections.emptyList(), descriptor.getRecommendation());
		assertEquals(Collections.emptyList(), descriptor.getMetricConfig());
		assertEquals(Collections.emptyList(), descriptor.getCustomGroup());
		assertEquals(Collections.emptyList(), descriptor.getPolicy());
		assertEquals("Default Policy", descriptor.getDefaultPolicy());
	}
}
