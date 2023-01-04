package com.vmware.pscoe.iac.artifact.configuration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public final class ConfigurationVra extends ConfigurationWithRefreshToken {

    // Important - when modify properties refer to comments in @Configuration
	public static final String TENANT = "tenant";

	public static final String REFRESH_TOKEN = "refresh.token";

	private final Logger logger = LoggerFactory.getLogger(Configuration.class);;

    /**
     * vRA Package Import content conflict resolution mode
     */
	public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";

    private ConfigurationVra(Properties props) {
        super(PackageType.VRA, props);
    }

    public String getPackageImportOverwriteMode() {
        return this.properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
    }

    public String getTenant() {
        return this.properties.getProperty(TENANT);
    }

	@Override
	public String getRefreshToken() {
		return this.properties.getProperty(REFRESH_TOKEN);
	}

    @Override
    public String getUsername() {
        String username = this.properties.getProperty(USERNAME);
        return username;
    }

    @Override
	public void validate(boolean domainOptional) throws ConfigurationException {
		logger.info("Checking if exists refresh token");
		boolean useRefreshTokenForAuth = !StringUtils.isEmpty(this.getRefreshToken());

		if(useRefreshTokenForAuth)
			logger.info("Refresh token in config for vra will be used");
		else
			logger.info("Refresh token not detected using predefined auth method");

		super.validate(domainOptional, useRefreshTokenForAuth);

		StringBuilder message = new StringBuilder();

		if (StringUtils.isEmpty(getTenant())) {
			message.append("Tenant ");
		}

		if (message.length() != 0) {
			throw new ConfigurationException("Configuration validation failed: Empty " + message);
		}
	}

    public static ConfigurationVra fromProperties(Properties props) throws ConfigurationException {
    	ConfigurationVra config = new ConfigurationVra(props);
    	config.validate(false);
    	return config;
    }
}
