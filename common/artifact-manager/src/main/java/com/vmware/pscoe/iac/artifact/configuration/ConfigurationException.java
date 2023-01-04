package com.vmware.pscoe.iac.artifact.configuration;

public class ConfigurationException extends Exception {

    public ConfigurationException(String message, Throwable parentException) {
        super(message, parentException);
    }

    public ConfigurationException(String message) {
        super(message);
    }

}
