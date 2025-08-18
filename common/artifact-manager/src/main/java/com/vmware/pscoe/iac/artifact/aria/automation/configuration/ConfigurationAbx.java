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
package com.vmware.pscoe.iac.artifact.aria.automation.configuration;

import java.util.Properties;

import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.PackageType;

/**
 * Holds ABx configuration
 */
public class ConfigurationAbx extends ConfigurationVraNg {
	protected ConfigurationAbx(Properties props) {
		super(PackageType.ABX, props);
	}

	/**
	 * Creates a new ConfigurationAbx directly from a Properties object, which is
	 * usually extracted from maven directly
	 *
	 * @param props - The properties to use
	 *
	 * @throws ConfigurationException in case of Validation Failures
	 */
	public static ConfigurationAbx fromProperties(Properties props) throws ConfigurationException {
		ConfigurationAbx config = new ConfigurationAbx(props);
		config.validate(false);
		return config;
	}
}
