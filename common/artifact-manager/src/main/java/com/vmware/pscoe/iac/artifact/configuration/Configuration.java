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

import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public abstract class Configuration {

    private final PackageType type;

    // Important - Maven base projects (maven/base-package/**/pom.xml)
    // configurations must be compatible with @Configuration and all its subclasses
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CONNECTION_TIMEOUT = "vrealize.connection.timeout";
    public static final String SOCKET_TIMEOUT = "vrealize.socket.timeout";
    public static final Integer DEFAULT_CONNECTION_TIMEOUT = 360;
    public static final Integer DEFAULT_SOCKET_TIMEOUT = 360;

    /**
     * Strategy configuration property. If set to true will perform force import
     * regardless of the vRO server content
     */
    public static final String IMPORT_OLD_VERSIONS = "importOldVersions";

    protected Properties properties;

	protected final Logger logger = LoggerFactory.getLogger(Configuration.class);;

    protected Configuration(PackageType type, Properties props) {
        this.type = type;
        this.properties = props;
    }

    public PackageType getPackageType() {
        return type;
    }

    public String getHost() {
        return this.properties.getProperty(HOST);
    }

    public int getPort() {
        try {
            return Integer.parseInt(this.properties.getProperty(PORT));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Port is not a number");
        }
    }

	public String getUsername() {
		String username = this.properties.getProperty(USERNAME);
		return StringUtils.isEmpty(username) ? username
			: (username.indexOf("@") > 0 ?  username.substring(0, username.lastIndexOf("@")) : username);
	}

	public String getDomain() {
		String username = this.properties.getProperty(USERNAME);
		return StringUtils.isEmpty(username) ? username
			: (username.indexOf("@") > 0 ? username.substring(username.lastIndexOf("@") + 1) : null);
	}

    public String getPassword() {
        return this.properties.getProperty(PASSWORD);
    }

    public boolean isImportOldVersions() {
        return Boolean.parseBoolean(this.properties.getProperty(IMPORT_OLD_VERSIONS));
    }

	public void validate(boolean domainOptional) throws ConfigurationException{
		validate(domainOptional, false);
	}

    public void validate(boolean domainOptional, boolean useRefreshTokenForAuthentication) throws ConfigurationException {
        StringBuilder message = new StringBuilder();
        if (StringUtils.isEmpty(getHost())) {
            message.append("Hostname ");
        }
        if (StringUtils.isEmpty(getPort())) {
            message.append("Port ");
        }
		if (StringUtils.isEmpty(getDomain()) && !domainOptional) {
			message.append("Domain (in username) ");
		}
		if(!useRefreshTokenForAuthentication) {

			logger.info("Refresh token not detected. Checking username and password on configuration");
			if (StringUtils.isEmpty(getUsername())) {
				message.append("Username ");
			}
			if (StringUtils.isEmpty(getPassword())) {
				message.append("Password ");
			}

		}
        if (message.length() != 0) {
            throw new ConfigurationException("Configuration validation failed: Empty " + message);
        }

        try {
            new URIBuilder().setScheme("https").setHost(getHost()).setPort(getPort()).build();
        } catch (URISyntaxException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

}
