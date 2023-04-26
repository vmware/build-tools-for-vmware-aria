package com.vmware.pscoe.iac.artifact.configuration;

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

import com.vmware.pscoe.iac.artifact.configuration.fixtures.ConfigurationTestDouble;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class ConfigurationTest {

	protected Properties properties;

	@ParameterizedTest
	@MethodSource("usernameProvider")
	public void testGetUsername(String username, String expectedUsername, String expectedDomain ) {
		Properties properties = new Properties();
		properties.setProperty("username", username);
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(expectedUsername, configuration.getUsername());
	}
	@ParameterizedTest
	@MethodSource("usernameProvider")
	public void testGetDomain(String username, String expectedUsername, String expectedDomain ) {
		Properties properties = new Properties();
		properties.setProperty("username", username);
		ConfigurationTestDouble configuration = new ConfigurationTestDouble(properties);

		assertEquals(expectedDomain, configuration.getDomain());
	}

	private static Stream<Arguments> usernameProvider() {
		return Stream.of(
			arguments("configurationadmin", "configurationadmin", null),
			arguments("configurationadmin@corp.local", "configurationadmin", "corp.local"),
			arguments("configurationadmin@System Domain", "configurationadmin", "System Domain"),
			arguments("configurationadmin@@System Domain", "configurationadmin@", "System Domain"),
			arguments("configurationadmin@test@System Domain", "configurationadmin@test", "System Domain"),
			arguments("configurationadmin@test@test2@System Domain", "configurationadmin@test@test2", "System Domain")
		);
	}
}
