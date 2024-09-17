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

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public final class ConfigurationSsh extends Configuration {
	// Important - when modify properties refer to comments in @Configuration
	public static final String SSH_DIRECTORY = "directory";

	/**
	 * SSH Package Import content conflict resolution mode
	 */
	public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";
	public static final String MSSING_PROPERTY_EXCEPTION = "Configuration validation failed. Empty SSH property. Please make sure you have defined the SSH property '%s'. You may define that in maven 'settings.xml'.";

	private ConfigurationSsh(Properties props) {
		super(PackageType.BASIC, props);
	}

	public String getPackageImportOverwriteMode() {
		return properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
	}

	public String getSshDirectory() {
		return properties.getProperty(SSH_DIRECTORY);
	}

	@Override
	public void validate(boolean domainOptional) throws ConfigurationException {
		if (StringUtils.isEmpty(getHost())) {
			throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, HOST));
		}

		if (StringUtils.isEmpty(getPort())) {
			throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, PORT));
		}

		if (StringUtils.isEmpty(getSshDirectory())) {
			throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, SSH_DIRECTORY));
		}

		if (StringUtils.isEmpty(getUsername())) {
			throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, USERNAME));
		}

		String sshPassword = getPassword();
		if (sshPassword == null || sshPassword.trim().length() == 0) {
			throw new ConfigurationException(String.format(MSSING_PROPERTY_EXCEPTION, PASSWORD));
		}

		try {
			new URIBuilder().setScheme("https").setHost(getHost()).setPort(getPort()).build();
		} catch (URISyntaxException e) {
			throw new ConfigurationException(e.getMessage(), e);
		}

		String host = getHost();
		try {
			InetAddress.getByName(host);
		} catch (UnknownHostException uhe) {
			throw new ConfigurationException(
					String.format(
							"Configuration validation failed. The vrops %s value %s is not valid host / IP address of the server. %s",
							HOST, host, uhe.getMessage()));
		}
	}

	public static ConfigurationSsh fromProperties(Properties props) throws ConfigurationException {
		ConfigurationSsh config = new ConfigurationSsh(props);

		config.validate(true);

		return config;
	}
}
