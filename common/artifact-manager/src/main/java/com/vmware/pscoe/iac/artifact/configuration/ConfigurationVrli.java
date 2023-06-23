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

import java.util.Properties;

import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVrli extends Configuration {
	// Important - when modify properties refer to comments in @Configuration.
	/**
	 * provider configuration entry.
	 */
	public static final String PROVIDER = "provider";
	/**
	 * vropsHost configuration entry.
	 */
	public static final String INTEGRATION_VROPS_HOST = "vropsHost";
	/**
	 * vropsPort configuration entry.
	 */
	public static final String INTEGRATION_VROPS_PORT = "vropsPort";
	/**
	 * vropsUser configuration entry.
	 */
	public static final String INTEGRATION_VROPS_USER = "vropsUser";
	/**
	 * vropsPassword configuration entry.
	 */
	public static final String INTEGRATION_VROPS_PASS = "vropsPassword";
	/**
	 * vropsAuthSource configuration entry.
	 */
	public static final String INTEGRATION_VROPS_AUTH_SOURCE = "vropsAuthSource";
	/**
	 * useOldAlertsApi configuration entry.
	 */
	public static final String VRLI_USE_OLD_ALERTS_API = "useOldAlertsApi";

	/**
	 * VRLI Package Import content conflict resolution mode.
	 */
	public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";

	protected ConfigurationVrli(final Properties props) {
		super(PackageType.VRLI, props);
	}

	/**
	 * VRLI Package overwrite mode.
	 * 
	 * @return value of the packageImportOverwriteMode.
	 */
	public String getPackageImportOverwriteMode() {
		return this.properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
	}

	/**
	 * VRLI Package provider name.
	 * 
	 * @return value of the provider.
	 */
	public String getProvider() {
		return this.properties.getProperty(PROVIDER);
	}

	/**
	 * VRLI vROPs integration auth source (used for managing of vROPs enabled vRLI
	 * alerts).
	 * 
	 * @return value of the vropsAuthSource.
	 */
	public String getIntegrationVropsAuthSource() {
		return this.properties.getProperty(INTEGRATION_VROPS_AUTH_SOURCE);
	}

	/**
	 * VRLI vROPs integration auth host (used for managing of vROPs enabled vRLI
	 * alerts).
	 * 
	 * @return value of the vropsHost.
	 */
	public String getIntegrationVropsAuthHost() {
		return this.properties.getProperty(INTEGRATION_VROPS_HOST);
	}

	/**
	 * VRLI vROPs integration auth port (used for managing of vROPs enabled vRLI
	 * alerts).
	 * 
	 * @return value of the vropsPort.
	 */
	public String getIntegrationVropsAuthPort() {
		return this.properties.getProperty(INTEGRATION_VROPS_PORT);
	}

	/**
	 * VRLI vROPs integration auth user (used for managing of vROPs enabled vRLI
	 * alerts).
	 * 
	 * @return value of the vropsUser.
	 */
	public String getIntegrationVropsAuthUser() {
		return this.properties.getProperty(INTEGRATION_VROPS_USER);
	}

	/**
	 * VRLI vROPs integration auth password (used for managing of vROPs enabled vRLI
	 * alerts).
	 * 
	 * @return value of the vropsPassword.
	 */
	public String getIntegrationVropsAuthPassword() {
		return this.properties.getProperty(INTEGRATION_VROPS_PASS);
	}

	/**
	 * VRLI is old alerts API used (used to workaround bug in vRLI v2 API support of
	 * vRLI 8.10).
	 * 
	 * @return value of the useOldAlertsApi.
	 */
	public boolean isOldAlertsApiUsed() {
		return Boolean.parseBoolean(this.properties.getProperty(VRLI_USE_OLD_ALERTS_API));
	}

	/**
	 * Validate configuration settings.
	 * 
	 * @param domainOptional flag whether domain is optional parameter.
	 * @throws ConfigurationException if the configuration is wrong.
	 */
	@Override
	public void validate(final boolean domainOptional) throws ConfigurationException {
		StringBuilder message = new StringBuilder();

		if (StringUtils.isEmpty(getHost())) {
			message.append("Hostname ");
		}
		if (StringUtils.isEmpty(getPort())) {
			message.append("Port ");
		}
		if (StringUtils.isEmpty(getProvider())) {
			message.append("Provider ");
		}
		if (message.length() != 0) {
			throw new ConfigurationException("Configuration validation failed: Empty " + message);
		}
	}

	/**
	 * Read configuration data from properties.
	 * 
	 * @param props structure where to read file from.
	 * @return instance of the ConfigurationVrli object.
	 * @throws ConfigurationException if the configuration is wrong.
	 */
	public static ConfigurationVrli fromProperties(final Properties props) throws ConfigurationException {
		ConfigurationVrli config = new ConfigurationVrli(props);

		config.validate(false);

		return config;
	}
}
