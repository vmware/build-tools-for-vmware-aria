package com.vmware.pscoe.iac.artifact.configuration;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.springframework.util.StringUtils;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVro extends ConfigurationWithRefreshToken implements ConfigurationNg {

    // Important - when modify properties refer to comments in @Configuration
    public static final String TENANT = "tenant";
    public static final String EMBEDDED = "embedded";
    public static final String AUTH = "auth";
    public static final String AUTH_HOST = "authHost";
    public static final String AUTH_PORT = "authPort";
    public static final String PROXY = "proxy";
    public static final String DEFAULT_TAG_IMPORT_MODE = "ImportAndOverwriteExistingValue";

    /**
     * These all are vRO API Package import options
     */
    public static final String PACKAGE_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES = "packageImportConfigurationAttributeValues";
    public static final String PACKAGE_IMPORT_CONFIGURATION_SECURE_ATTRIBUTE_VALUES = "packageImportConfigSecureStringAttributeValues";
    public static final String PACKAGE_IMPORT_TAGS_IMPORT_MODE = "packageImportTagsImportMode";

    /**
     * These all are vRO API Package export options
     */
    public static final String PACKAGE_EXPORT_VERSION_HISTORY = "packageExportVersionHistory";
    public static final String PACKAGE_EXPORT_CONFIGURATION_ATTRIBUTE_VALUES = "packageExportConfigurationAttributeValues";
    public static final String PACKAGE_EXPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES = "packageExportConfigSecureStringAttributeValues";
    public static final String PACKAGE_EXPORT_GLOBAL_TAGS= "packageExportGlobalTags";

    public static final String PACKAGE_EXPORT_AS_ZIP = "packgeExportAsZip";

	private final Logger logger = Logger.getAnonymousLogger();

    protected ConfigurationVro(Properties props) {
        super(PackageType.VRO, props);
    }

    protected ConfigurationVro(PackageType type, Properties props) {
        super(type, props);
    }

    public boolean isPackageImportConfigurationAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES));
    }

    public String getPackageTagsImportMode() {
        return this.properties.getProperty(PACKAGE_IMPORT_TAGS_IMPORT_MODE, DEFAULT_TAG_IMPORT_MODE);
    }

    public boolean isPackageImportConfigSecureStringAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_IMPORT_CONFIGURATION_SECURE_ATTRIBUTE_VALUES));
    }

    public boolean isPackageExportVersionHistory() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_VERSION_HISTORY));
    }

    public boolean isPackageExportConfigurationAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_CONFIGURATION_ATTRIBUTE_VALUES));
    }

    public boolean isPackageExportConfigSecureStringAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES));
    }

    public boolean isPackageExportGlobalTags() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_GLOBAL_TAGS));
    }

    public boolean isPackgeExportAsZip() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_AS_ZIP));
    }

    public String getTenant() {
        return this.properties.getProperty(TENANT);
    }

    @Override
    public String getUsername() {
        String username = this.properties.getProperty(USERNAME);
        return this.getAuth() == AuthProvider.VRA ? username.replaceAll("@.*", "") :  username;
    }

    public AuthProvider getAuth() {
        final String authValue = this.properties.getProperty(AUTH);
        return StringUtils.isEmpty(authValue) ? AuthProvider.BASIC : AuthProvider.valueOf(authValue.toUpperCase());
    }

    public boolean isEmbeddedVro8() {
        final String embeddedValue = this.properties.getProperty(EMBEDDED);
        try{
            return StringUtils.isEmpty(embeddedValue) ? false : Boolean.parseBoolean(embeddedValue);
        }catch(Exception e){
            throw new RuntimeException("Embedded Value is not a boolean.");
        }
    }

    public String getAuthHost() {
        final String authHost = this.properties.getProperty(AUTH_HOST);
        return StringUtils.isEmpty(authHost) ? this.getHost() : authHost;
    }

    public int getAuthPort() {
    	final String authPort = this.properties.getProperty(AUTH_PORT);
    	try{
            return StringUtils.isEmpty(authPort) ? this.getPort() : Integer.parseInt(authPort);
        }catch(NumberFormatException e){
        	throw new RuntimeException("Port is not a number");
        }
    }

    public HttpHost getProxy() {
        String proxy = this.properties.getProperty(PROXY);
        if (StringUtils.isEmpty(proxy)) {
            return null;
        }

        return HttpHost.create(proxy);
    }

	@Override
	public String getRefreshToken() {
		String token = this.properties.getProperty(REFRESH_TOKEN);
		return StringUtils.isEmpty(token) ? null : token;
	}

	@Override
	public void validate(boolean domainOptional) throws ConfigurationException{
		logger.info("Checking if exists refresh token");
		boolean useRefreshTokenForAuth = !StringUtils.isEmpty(this.getRefreshToken());
		if(useRefreshTokenForAuth)
			logger.info(String.format("Refresh token in config for vro is: %s", this.getRefreshToken()));
		else
			logger.info("Refresh token not detected using BASIC Authentication");
		super.validate(domainOptional, useRefreshTokenForAuth);
	}

	public static ConfigurationVro fromProperties(Properties props) throws ConfigurationException {
        ConfigurationVro config = new ConfigurationVro(props);
 
        boolean hasVroTenant = !StringUtils.isEmpty(config.getTenant());
        if (hasVroTenant && config.getAuth() != AuthProvider.VRA) {
            throw new ConfigurationException("vRO configuration validation error! Multi-tenancy requires 'vra' authentication!");
        }

    	config.validate(!hasVroTenant);
    	return config;
    }

    public enum AuthProvider {
        BASIC, VRA, VC
    }

}
