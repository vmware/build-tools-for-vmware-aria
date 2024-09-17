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
import java.util.logging.Logger;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVrops extends ConfigurationWithRefreshToken {
	// Important - when modify properties refer to comments in @Configuration
	public static final String VROPS_DASHBOARD_USER = "dashboardUser";
	public static final String VROPS_REST_USER = "restUser";
	public static final String VROPS_REST_PASSWORD = "restPassword";
	public static final String VROPS_REST_AUTH_SOURCE = "restAuthSource";
	public static final String VROPS_REST_AUTH_PROVIDER = "restAuthProvider";
	public static final String SSH_PORT = "sshPort";
	private static final String DEFAULT_AUTH_SOURCE = "local";
	private static final AuthProvider DEFAULT_AUTH_PROVIDER = AuthProvider.AUTH_N;

	/**
	 * vROps Package Import content conflict resolution mode
	 */
	public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";

	protected ConfigurationVrops(Properties props) {
		super(PackageType.VROPS, props);
	}

	public String getPackageImportOverwriteMode() {
		return properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
	}

	public int getHttpPort() {
		try {
			return Integer.parseInt(properties.getProperty(PORT));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Vrops property \"" + PORT + "\" with value \"" + properties.getProperty(PORT) + "\" is not a port number: "
					+ e.getLocalizedMessage() + ". Please specify the port number which the vROps web UI listens on as vrops property \"" + PORT
					+ "\" in your settings.xml.");
		}
	}

	public int getSshPort() {
		try {
			return Integer.parseInt(properties.getProperty(SSH_PORT));
		} catch (NumberFormatException e) {
			throw new RuntimeException("Vrops property \"" + SSH_PORT + "\" with value \"" + properties.getProperty(SSH_PORT) + "\" is not a port number: "
					+ e.getLocalizedMessage() + ". Please specify the port number which the vROps appliance SSH Service listens on as vrops property \"" + SSH_PORT
					+ "\" in your settings.xml.");
		}
	}

	@Override
	public String getRefreshToken() {
		String token = this.properties.getProperty(REFRESH_TOKEN);
		return StringUtils.isEmpty(token) ? null : token;
	}

	public String getVropsDashboardUser() {
		return properties.getProperty(VROPS_DASHBOARD_USER);
	}

	public String getVropsRestUser() {
		return properties.getProperty(VROPS_REST_USER);
	}

	public String getVropsRestPassword() {
		return properties.getProperty(VROPS_REST_PASSWORD);
	}

	public String getVropsAuthSource() {
		if (StringUtils.isEmpty(properties.getProperty(VROPS_REST_AUTH_SOURCE))) {
			return DEFAULT_AUTH_SOURCE;
		}

		return properties.getProperty(VROPS_REST_AUTH_SOURCE);
	}

	public AuthProvider getAuthProvider() {
		final String authProvider = properties.getProperty(VROPS_REST_AUTH_PROVIDER);
		return StringUtils.isEmpty(authProvider) ? DEFAULT_AUTH_PROVIDER : AuthProvider.valueOf(authProvider.toUpperCase());
	}

	@Override
	public void validate(boolean domainOptional) throws ConfigurationException {
		if (StringUtils.isEmpty(getHost())) {
			throw new ConfigurationException("Configuration validation failed. Empty vROps host. Please make sure you have defined the vrops property '"
					+ HOST + "'. You may define that in maven 'settings.xml'.");
		}

		if (StringUtils.isEmpty(getVropsDashboardUser())) {
			throw new ConfigurationException("Configuration validation failed. Empty vROps Dashboard user. "
					+ "Please make sure you have defined the vrops property '" + VROPS_DASHBOARD_USER + "'. You may define that in maven 'settings.xml'.");
		}
		if (StringUtils.isEmpty(getVropsRestUser())) {
			throw new ConfigurationException("Configuration validation failed. Empty vROps REST  user. "
					+ "Please make sure you have defined the vrops property '" + VROPS_REST_USER + "'. You may define that in maven 'settings.xml'.");
		}

		if (StringUtils.isEmpty(getDomain()) && !domainOptional) {
			throw new ConfigurationException("Domain (in vrops username) is empty and domain optional flag is 'false'. Username format should be <userowname>@<domain>.");
		}

		try {
			new URIBuilder().setScheme("https").setHost(getHost()).setPort(getHttpPort()).build();
		} catch (URISyntaxException e) {
			throw new ConfigurationException(e.getMessage(), e);
		}

		String host = getHost();
		try {
			InetAddress.getByName(host);
		} catch (UnknownHostException uhe) {
			throw new ConfigurationException(
					String.format("Configuration validation failed. The vrops %s value %s is not valid host / IP address of the server. %s", HOST, host, uhe.getMessage()));
		}
		try {
			if (!checkPort(getSshPort())) {
				throw new ConfigurationException(
						String.format("The vrops %s value %s is not valid since it should be between %d and %d", SSH_PORT,
								getSshPort(), 0, 0x0FFFF));
			}
			if (!checkPort(getHttpPort())) {
				throw new ConfigurationException(
						String.format("The vrops %s value %s is not valid since it should be between %d and %d", PORT,
								getHttpPort(), 0, 0x0FFFF));
			}
		} catch (RuntimeException e) {
			throw new ConfigurationException(String.format("Vrops configuration validation failed. Invalid port number: %s", e.getMessage()));
		}

		String restPass = getVropsRestPassword();
		if (restPass == null || restPass.trim().length() == 0) {
			throw new ConfigurationException("Configuration validation failed. Empty vROps REST User Password. Please make sure you have defined the vrops property \""
					+ VROPS_REST_PASSWORD + "\". You may define that in maven 'settings.xml'.");
		}
	}

	public static ConfigurationVrops fromProperties(Properties props) throws ConfigurationException {
		ConfigurationVrops config = new ConfigurationVrops(props);

		config.validate(true);

		return config;
	}

	private boolean checkPort(int port) {
		return port > 0 && port < 0x0FFFF;
	}

	public enum AuthProvider {
		BASIC, AUTH_N
	}
}
