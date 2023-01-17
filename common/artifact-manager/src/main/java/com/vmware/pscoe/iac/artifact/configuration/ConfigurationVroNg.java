package com.vmware.pscoe.iac.artifact.configuration;

import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.springframework.util.StringUtils;

import java.util.Properties;

public final class ConfigurationVroNg extends ConfigurationVraNg implements ConfigurationNg {
    private ConfigurationVroNg(Properties props) {
        super(props);
    }

    public static ConfigurationVroNg fromProperties(Properties props) throws ConfigurationException {
        ConfigurationVroNg config = new ConfigurationVroNg(props);
        config.validate(true);
        return config;
    }

    // Important - when modify properties refer to comments in @Configuration
    public static final String ORGANIZATION_ID = "org.id";
    public static final String REFRESH_TOKEN = "refresh.token";
    /**
     * vRA Package Import content conflict resolution mode
     */

    public String getOrgId() {
        return this.properties.getProperty(ORGANIZATION_ID);
    }

    public String getRefreshToken() {
        return this.properties.getProperty(REFRESH_TOKEN);
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

        if (StringUtils.isEmpty(getRefreshToken()) && StringUtils.isEmpty(super.getUsername())){
            message.append("Refresh token or Username ");
        }

        if (message.length() != 0) {
            throw new ConfigurationException("Configuration validation failed: Empty " + message);
        }
    }

    @Override
    public boolean isPackageImportConfigurationAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES));
    }

    @Override
    public String getPackageTagsImportMode() {
        return this.properties.getProperty(PACKAGE_IMPORT_TAGS_IMPORT_MODE, DEFAULT_TAG_IMPORT_MODE);
    }

    @Override
    public boolean isPackageImportConfigSecureStringAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_IMPORT_CONFIGURATION_SECURE_ATTRIBUTE_VALUES));
    }

    @Override
    public boolean isPackageExportVersionHistory() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_VERSION_HISTORY));
    }

    @Override
    public boolean isPackageExportConfigurationAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_CONFIGURATION_ATTRIBUTE_VALUES));
    }

    @Override
    public boolean isPackageExportConfigSecureStringAttributeValues() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES));
    }

    @Override
    public boolean isPackageExportGlobalTags() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_GLOBAL_TAGS));
    }

    @Override
    public boolean isPackgeExportAsZip() {
        return Boolean.parseBoolean(this.properties.getProperty(PACKAGE_EXPORT_AS_ZIP));
    }
}
