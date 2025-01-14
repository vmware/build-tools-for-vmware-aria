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

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfigurationAbx extends ConfigurationVraNg {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationAbx.class);

	protected ConfigurationAbx(Properties props) {
		super(PackageType.ABX, props);
	}

	public static ConfigurationAbx fromProperties(Properties props) throws ConfigurationException {
		ConfigurationAbx config = new ConfigurationAbx(props);
		config.validate(false);
		return config;
	}
}
