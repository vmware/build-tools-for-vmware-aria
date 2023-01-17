package com.vmware.pscoe.iac.artifact.configuration;

public  interface ConfigurationNg {
    // Important - when modify properties refer to comments in @Configuration
    public static final String EMBEDDED = "embedded";
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


    public boolean isPackageImportConfigurationAttributeValues();

    public String getPackageTagsImportMode();

    public boolean isPackageImportConfigSecureStringAttributeValues();

    public boolean isPackageExportVersionHistory();

    public boolean isPackageExportConfigurationAttributeValues();

    public boolean isPackageExportConfigSecureStringAttributeValues();

    public boolean isPackageExportGlobalTags();

    public boolean isPackgeExportAsZip();
}
