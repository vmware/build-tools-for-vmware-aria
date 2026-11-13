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
package com.vmware.pscoe.iac.artifact.aria.operations.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CliManagerVropsTest {
	private final CliManagerVrops cliManager = new CliManagerVrops(null);

	@Test
	void logOutputAndFailOnErrorsPassesWhenCommandsSucceed() {
		assertDoesNotThrow(() -> cliManager.logOutputAndFailOnErrors(
				Arrays.asList("Starting VMware Cloud Foundation Operations CLI", "Import completed")));
	}

	@Test
	void logOutputAndFailOnErrorsFailsWhenCommandFails() {
		String failure = "Script command: \" dashboard import all \"/tmp/Dashboard.zip\" --force\" failed with exit code: 3";

		RuntimeException e = assertThrows(RuntimeException.class, () -> cliManager.logOutputAndFailOnErrors(
				Arrays.asList("Error encountered: java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory", failure)));

		assertTrue(e.getMessage().contains(failure), e.getMessage());
	}
}
