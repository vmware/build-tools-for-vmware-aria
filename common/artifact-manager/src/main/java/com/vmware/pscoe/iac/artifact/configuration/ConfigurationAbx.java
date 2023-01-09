package com.vmware.pscoe.iac.artifact.configuration;

import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfigurationAbx extends ConfigurationVraNg {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationAbx.class);

    protected ConfigurationAbx(Properties props) {
        super(PackageType.ABX, props);
    }

    public static ConfigurationAbx fromProperties(Properties props) throws ConfigurationException {
    	ConfigurationAbx config = new ConfigurationAbx(props);
    	config.validate(false);
    	return config;
    }
}
