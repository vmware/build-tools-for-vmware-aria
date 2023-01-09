package com.vmware.pscoe.iac.artifact.configuration;

import java.util.Properties;

import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVrli extends Configuration {
    // Important - when modify properties refer to comments in @Configuration
	public static final String PROVIDER = "provider";
    public static final String INTEGRATION_VROPS_HOST = "vropsHost";
    public static final String INTEGRATION_VROPS_PORT = "vropsPort";
    public static final String INTEGRATION_VROPS_USER = "vropsUser";
    public static final String INTEGRATION_VROPS_PASS = "vropsPassword";
    public static final String INTEGRATION_VROPS_AUTH_SOURCE = "vropsAuthSource";

    /**
     * VRLI Package Import content conflict resolution mode
     */
	public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";

    protected ConfigurationVrli(Properties props) {
        super(PackageType.VRLI, props);
    }

    public String getPackageImportOverwriteMode() {
        return this.properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
    }
    
    public String getProvider() {
        return this.properties.getProperty(PROVIDER);
    }

    public String getIntegrationVropsAuthSource() {
        return this.properties.getProperty(INTEGRATION_VROPS_AUTH_SOURCE);
    }

    public String getIntegrationVropsAuthHost() {
        return this.properties.getProperty(INTEGRATION_VROPS_HOST);
    }

    public String getIntegrationVropsAuthPort() {
        return this.properties.getProperty(INTEGRATION_VROPS_PORT);
    }

    public String getIntegrationVropsAuthUser() {
        return this.properties.getProperty(INTEGRATION_VROPS_USER);
    }

    public String getIntegrationVropsAuthPassword() {
        return this.properties.getProperty(INTEGRATION_VROPS_PASS);
    }

    @Override
	public void validate(boolean domainOptional) throws ConfigurationException {
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

    public static ConfigurationVrli fromProperties(Properties props) throws ConfigurationException {
    	ConfigurationVrli config = new ConfigurationVrli(props);

    	config.validate(false);

    	return config;
    }
}
