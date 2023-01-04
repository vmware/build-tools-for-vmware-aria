package com.vmware.pscoe.iac.artifact.cli;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;

public class CliManagerFactory {

    public static CliManagerVrops getVropsCliManager(ConfigurationVrops config) {
        return new CliManagerVrops(config);
    }
}
