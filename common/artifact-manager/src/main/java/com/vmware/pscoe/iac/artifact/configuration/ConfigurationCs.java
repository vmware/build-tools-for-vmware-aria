package com.vmware.pscoe.iac.artifact.configuration;

import java.util.Properties;

import com.vmware.pscoe.iac.artifact.model.PackageType;

public class ConfigurationCs extends ConfigurationVraNg {

    private ConfigurationCs(Properties props) {
        super(PackageType.CS, props);
    }

    public static ConfigurationCs fromProperties(Properties props) throws ConfigurationException {
        ConfigurationCs config = new ConfigurationCs(props);
        config.validate(false);
        return config;
    }
}
