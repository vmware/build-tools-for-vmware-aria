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
