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

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationVcd extends Configuration {
		// Important - when modify properties refer to comments in @Configuration
	    /**
	     * vCD Package Import content conflict resolution mode
	     */
		public static final String PACKAGE_IMPORT_OVERWRITE_MODE = "packageImportOverwriteMode";

	    private ConfigurationVcd(Properties props) {
	        super(PackageType.VCDNG, props);
	    }

	    public String getPackageImportOverwriteMode() {
	        return this.properties.getProperty(PACKAGE_IMPORT_OVERWRITE_MODE, "SKIP,OVERWRITE");
	    }

	    public static ConfigurationVcd fromProperties(Properties props) throws ConfigurationException {
	    	ConfigurationVcd config = new ConfigurationVcd(props);
	    	config.validate(false);
	    	return config;
	    }
}
