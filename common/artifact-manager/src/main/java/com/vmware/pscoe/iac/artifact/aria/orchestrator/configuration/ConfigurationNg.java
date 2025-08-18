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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration;

public interface ConfigurationNg {
	// Important - when modify properties refer to comments in @Configuration
	String EMBEDDED = "embedded";
	String DEFAULT_TAG_IMPORT_MODE = "ImportAndOverwriteExistingValue";

	/**
	 * These all are vRO API Package import options
	 */
	String PACKAGE_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES = "packageImportConfigurationAttributeValues";
	String PACKAGE_IMPORT_CONFIGURATION_SECURE_ATTRIBUTE_VALUES = "packageImportConfigSecureStringAttributeValues";
	String PACKAGE_IMPORT_TAGS_IMPORT_MODE = "packageImportTagsImportMode";

	/**
	 * These all are vRO API Package export options
	 */
	String PACKAGE_EXPORT_VERSION_HISTORY = "packageExportVersionHistory";
	String PACKAGE_EXPORT_CONFIGURATION_ATTRIBUTE_VALUES = "packageExportConfigurationAttributeValues";
	String PACKAGE_EXPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES = "packageExportConfigSecureStringAttributeValues";
	String PACKAGE_EXPORT_GLOBAL_TAGS = "packageExportGlobalTags";

	String PACKAGE_EXPORT_AS_ZIP = "packgeExportAsZip";

	boolean isPackageImportConfigurationAttributeValues();

	String getPackageTagsImportMode();

	boolean isPackageImportConfigSecureStringAttributeValues();

	boolean isPackageExportVersionHistory();

	boolean isPackageExportConfigurationAttributeValues();

	boolean isPackageExportConfigSecureStringAttributeValues();

	boolean isPackageExportGlobalTags();

	boolean isPackgeExportAsZip();
}
