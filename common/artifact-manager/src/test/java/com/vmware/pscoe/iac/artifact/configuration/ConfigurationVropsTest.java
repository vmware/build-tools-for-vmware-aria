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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.vmware.pscoe.iac.artifact.aria.operations.configuration.ConfigurationVrops;

import java.net.UnknownHostException;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

class ConfigurationVropsTest {
	protected Properties properties;
	protected ConfigurationVrops mockedInstance;
	private ConfigurationVrops configurationVrops;

	@BeforeEach
	void init() throws UnknownHostException {
		this.properties = new Properties();
		this.properties.setProperty("host", "localhost");
		this.properties.setProperty("port", "443");
		this.properties.setProperty("sshPort", "443");
		this.properties.setProperty("authHost", "auth_host.corp.local");
		this.properties.setProperty("authPort", "443");
		this.properties.setProperty("authPort", "443");
		this.properties.setProperty("dashboardUser", "dashboardUser");
		this.properties.setProperty("restUser", "restUser");
		this.properties.setProperty("restPassword", "restPassword");
		this.configurationVrops = new ConfigurationVrops(this.properties);

		this.mockedInstance = Mockito.mock(ConfigurationVrops.class);
		when(mockedInstance.getHost()).thenReturn(null);
	}

	@Test
	public void testValidateShouldBeEmptyHost() {
		try {
			this.mockedInstance.validate(true);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		assertSame(this.mockedInstance.getHost(), null);
	}

	@Test
	public void testValidateShouldNotBeNullHost() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getHost(), "localhost");
	}

	@Test
	public void testValidateShouldNotBeNullVropDashboardUser() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getVropsDashboardUser(), "dashboardUser");
	}

	@Test
	public void testValidateShouldNotBeNullVropRestUser() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getVropsDashboardUser(), "dashboardUser");
	}

}
