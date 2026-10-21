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
package com.vmware.pscoe.iac.artifact.common.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.vcd.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.VcfAutoConfiguration;

/**
 * Unit tests for RestClientFactory.
 */
class RestClientFactoryTest {

	private static final String ORG_NAME = "vmw-emea-mgmt";

	private static Properties providerAdminProperties() {
		Properties props = new Properties();
		props.setProperty(Configuration.HOST, "vcfa.corp.internal");
		props.setProperty(Configuration.PORT, "443");
		props.setProperty(Configuration.USERNAME, "admin@System");
		props.setProperty(Configuration.PASSWORD, "secret");
		props.setProperty(VcfAutoConfiguration.PROJECT_NAME, "dev");
		props.setProperty(VcfAutoConfiguration.ORGANIZATION_NAME, ORG_NAME);
		return props;
	}

	@Test
	void testCreateConfigurationVcd_VcfAutoPropagatesOrgName() {
		VcfAutoConfiguration config = VcfAutoConfiguration.fromProperties(providerAdminProperties());

		ConfigurationVcd vcdConfig = RestClientFactory.createConfigurationVcd(config);

		assertEquals(ORG_NAME, vcdConfig.getOrgName());
		assertEquals("System", vcdConfig.getDomain());
	}

	@Test
	void testCreateConfigurationVcd_VraNgPropagatesOrgName() {
		ConfigurationVraNg config = ConfigurationVraNg.fromProperties(providerAdminProperties());

		ConfigurationVcd vcdConfig = RestClientFactory.createConfigurationVcd(config);

		assertEquals(ORG_NAME, vcdConfig.getOrgName());
	}
}
