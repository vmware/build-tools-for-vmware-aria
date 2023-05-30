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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


class ConfigurationVroTest {

	protected Properties properties;
	protected ConfigurationVro configurationVro;
	protected final String VRO_TEST_REFRESH_TOKEN = "7Zrj54LHsnerIjMgBGj4tCIvdtsWpOxI";
	protected final String AUTH_METHOD = "vra";

	@BeforeEach
	void init() {

		this.properties = new Properties();
		this.properties.setProperty("host", "host.corp.local");
		this.properties.setProperty("port", "443");
		this.properties.setProperty("authHost", "auth_host.corp.local");
		this.properties.setProperty("authPort", "443");
	}

	@AfterEach
	void cleanConfiguration() {
		this.properties.remove("refresh.token");
		this.properties.remove("username");
		this.properties.remove("password");
		this.properties.remove(AUTH_METHOD);
	}

	@Test
	public void testValidateShouldNotBeNullMainProperties() throws Exception {
		this.properties.setProperty("username", "test");
		this.properties.setProperty("password", "test");
		this.configurationVro = new ConfigurationVro(this.properties);

		this.configurationVro.validate(true);
		assertSame(this.configurationVro.getHost(), "host.corp.local");
		assertEquals(this.configurationVro.getPort(), 443);
		assertSame(this.configurationVro.getAuthHost(), "auth_host.corp.local");
		assertEquals(this.configurationVro.getAuthPort(), 443);
		assertSame(this.configurationVro.getUsername(), "test");
		assertSame(this.configurationVro.getPassword(), "test");
	}

	@Test
	public void testValidateShouldNotCheckUsernameAndPassword() throws Exception {
		this.properties.setProperty("refresh.token", VRO_TEST_REFRESH_TOKEN);
		this.configurationVro = new ConfigurationVro(this.properties);

		this.configurationVro.validate(true);
	}

	@Test
	public void testValidateShouldNotBeNullRefreshTokenAndAuthMethod() throws Exception {
		this.properties.setProperty("refresh.token", VRO_TEST_REFRESH_TOKEN);
		this.properties.setProperty("auth", AUTH_METHOD);

		this.configurationVro = new ConfigurationVro(this.properties);
		this.configurationVro.validate(true);
		assertSame(this.configurationVro.getRefreshToken(), VRO_TEST_REFRESH_TOKEN);
		assertSame(this.configurationVro.getAuth(), ConfigurationVro.AuthProvider.VRA);
	}

	@Test
	public void testValidateShouldCheckUsernameAndPassword() throws Exception {
		this.properties.setProperty("username", "test");
		this.properties.setProperty("password", "test");

		this.configurationVro = new ConfigurationVro(this.properties);
		this.configurationVro.validate(true);
		assertSame(this.configurationVro.getUsername(), "test");
		assertSame(this.configurationVro.getPassword(), "test");
	}

}
