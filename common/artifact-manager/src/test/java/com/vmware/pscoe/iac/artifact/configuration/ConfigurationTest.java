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
package com.vmware.pscoe.iac.artifact.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.vmware.pscoe.iac.artifact.configuration.fixtures.ConfigurationTestDouble;

class ConfigurationTest {
	protected Properties properties;

	@ParameterizedTest
	@MethodSource("usernameProvider")
	public void testGetUsername(String username, String expectedUsername, String expectedDomain) {
		Properties properties = new Properties();
		properties.setProperty("username", username);
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(expectedUsername, configuration.getUsername());
	}

	@ParameterizedTest
	@MethodSource("usernameProvider")
	public void testGetDomain(String username, String expectedUsername, String expectedDomain) {
		Properties properties = new Properties();
		properties.setProperty("username", username);
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(expectedDomain, configuration.getDomain());
	}

	@ParameterizedTest
	@MethodSource("importStrategyProvider")
	public void testDefaultImportStrategy(Boolean expectedStrategy) {
		Properties properties = new Properties();
		properties.setProperty("forceImportLatestVersions", "");
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(expectedStrategy, configuration.isForceImportLatestVersions());
	}

	@ParameterizedTest
	@MethodSource("sshTimeoutProvider")
	public void testGetSshTimeout(String sshTimeout, int exptectedSshTimeout) {
		Properties properties = new Properties();
		properties.setProperty("sshTimeout", sshTimeout);
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(exptectedSshTimeout, configuration.getSshTimeout());
	}

	@Test
	public void testGetSshTimeoutShouldThrowIfNotNumber() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("sshTimeout", "NaN");
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		// WHEN
		Exception exception = assertThrows(RuntimeException.class, () -> {
			configuration.getSshTimeout();
		});

		// THEN
		assertTrue(exception.getMessage().contains("SSH timeout is not a number"));
	}

	private static Stream<Arguments> usernameProvider() {
		return Stream.of(arguments("configurationadmin", "configurationadmin", null),
				arguments("configurationadmin@corp.local", "configurationadmin", "corp.local"),
				arguments("configurationadmin@System Domain", "configurationadmin", "System Domain"),
				arguments("configurationadmin@@System Domain", "configurationadmin@", "System Domain"),
				arguments("configurationadmin@test@System Domain", "configurationadmin@test", "System Domain"),
				arguments("configurationadmin@test@test2@System Domain", "configurationadmin@test@test2",
						"System Domain"));
	}

	private static Stream<Arguments> importStrategyProvider() {
		return Stream.of(arguments(Boolean.FALSE));
	}

	private static Stream<Arguments> sshTimeoutProvider() {
		return Stream.of(arguments("20", 20),
				arguments("0", 300),
				arguments("", 300));
	}
}
