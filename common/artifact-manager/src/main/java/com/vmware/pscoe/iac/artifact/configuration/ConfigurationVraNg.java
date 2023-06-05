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

import org.apache.http.HttpHost;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVraNg extends Configuration {

    // Important - when modify properties refer to comments in @Configuration
    public static final String CSP_HOST = "csp.host";
    public static final String PROJECT_ID = "project.id";
    public static final String DATA_COLLECTION_DELAY_SECONDS = "data.collection.delay.seconds";
    public static final String PROJECT_NAME = "project.name";
    public static final String ORGANIZATION_ID = "org.id";
    public static final String ORGANIZATION_NAME = "org.name";
    public static final String REFRESH_TOKEN = "refresh.token";
    public static final String IMPORT_TIMEOUT = "import.timeout"; // in miliseconds
    public static final String VRO_INTEGRATION = "vro.integration";
    public static final String PROXY = "proxy";
    public static final String PROXY_REQUIRED = "proxy.required";
    public static final String CLOUD_PROXY_NAME = "cloud.proxy.name";
    public static final String UNRELEASE_BLUEPRINT_VERSIONS = "bp.unrelease.versions";

    public static final Integer DEFAULT_IMPORT_TIMEOUT = 6000; // in miliseconds

    /**
     * vRA Package Import content conflict resolution mode
     */
    public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";
	protected Logger logger;

    protected ConfigurationVraNg(Properties props) {
        super(PackageType.VRANG, props);
		this.logger = LoggerFactory.getLogger(this.getClass());
    }

    protected ConfigurationVraNg(PackageType pkgType, Properties props) {
        super(pkgType, props);
    }

    public String getPackageImportOverwriteMode() {
        return this.properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
    }

    public String getAuthHost() {
        if (this.properties.getProperty(CSP_HOST) == null || this.properties.getProperty(CSP_HOST).isEmpty()) {
            return this.properties.getProperty(HOST);
        } else {
            return this.properties.getProperty(CSP_HOST);
        }
    }

    public String getProjectId() {
        return this.properties.getProperty(PROJECT_ID);
    }

    public String getDataCollectionDelaySeconds() {
        return this.properties.getProperty(DATA_COLLECTION_DELAY_SECONDS);
    }

    public String getProjectName() {
        return this.properties.getProperty(PROJECT_NAME);
    }

    public String getOrgId() {
        return this.properties.getProperty(ORGANIZATION_ID);
    }

    public String getOrgName() {
        return this.properties.getProperty(ORGANIZATION_NAME);
    }

    public String getVroIntegration() {
        return this.properties.getProperty(VRO_INTEGRATION);
    }

    public String getRefreshToken() {
        return this.properties.getProperty(REFRESH_TOKEN);
    }

    public String getCloudProxyName() {
        return this.properties.getProperty(CLOUD_PROXY_NAME);
    }

    public Integer getImportTimeout() {
        if (StringUtils.isEmpty(this.properties.getProperty(IMPORT_TIMEOUT))) {
            return DEFAULT_IMPORT_TIMEOUT;
        }
        try {
            return Integer.valueOf(this.properties.getProperty(IMPORT_TIMEOUT));
        } catch (NumberFormatException e) {
            return DEFAULT_IMPORT_TIMEOUT;
        }
    }

    public HttpHost getProxy() {
        String proxy = this.properties.getProperty(PROXY);
        if (StringUtils.isEmpty(proxy)) {
            return null;
        }

        return HttpHost.create(proxy);
    }

    public boolean getUnreleaseBlueprintVersions() {
        return Boolean.parseBoolean(this.properties.getProperty(UNRELEASE_BLUEPRINT_VERSIONS, "true"));
    }

    @Override
    public void validate(boolean domainOptional) throws ConfigurationException {
        StringBuilder message = new StringBuilder();

        if (StringUtils.isEmpty(getHost())) {
            message.append("Hostname ");
        }

        if (StringUtils.isEmpty(getAuthHost())) {
            message.append("Authentication hostname ");
        }

        if (StringUtils.isEmpty(getPort())) {
            message.append("Port ");
        }

        if (StringUtils.isEmpty(getProjectId()) && StringUtils.isEmpty(getProjectName())) {
            message.append("Project name/Project id ");
        }

        if (StringUtils.isEmpty(getOrgId()) && StringUtils.isEmpty(getOrgName())) {
            message.append("Organization id and Organization Name ");
        }

        if (StringUtils.isEmpty(getRefreshToken()) && StringUtils.isEmpty(super.getUsername())) {
            message.append("Refresh token or Username ");
        }

        if (message.length() != 0) {
            throw new ConfigurationException("Configuration validation failed: Empty " + message);
        }
    }

	public void deprecationWarnings() {
		String[] deprecatedFlags = new String[]{
			"bp.ignore.versions",
			"details.json"
		};

		for (String flag: deprecatedFlags) {
			this.logger.warn("%s has been deprecated, it is ignored. Consult the releases %s for more information", flag, "https://github.com/vmware/build-tools-for-vmware-aria/releases");
		}
	}

    public static ConfigurationVraNg fromProperties(Properties props) throws ConfigurationException {
        ConfigurationVraNg config = new ConfigurationVraNg(props);

        config.validate(false);

        return config;
    }
}
