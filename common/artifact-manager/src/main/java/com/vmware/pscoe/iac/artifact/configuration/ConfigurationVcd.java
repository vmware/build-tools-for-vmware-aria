package com.vmware.pscoe.iac.artifact.configuration;

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
