package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * common
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

import com.vmware.pscoe.iac.artifact.configuration.*;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.rest.RestClientFactory;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractIacMojo extends AbstractVroPkgMojo {

    @Component
    private Settings settings;

    @Component
    private SettingsDecrypter decrypter;

    @Parameter(required = false, property = "vro", defaultValue = "${vro.*}")
    private Map<String, String> vro;

    @Parameter(required = false, property = "vra", defaultValue = "${vra.*}")
    private Map<String, String> vra;

    @Parameter(required = false, property = "vrang", defaultValue = "${vrang.*}")
    private Map<String, String> vrang;

    @Parameter(required = false, property = "vcd", defaultValue = "${vcd.*}")
    private Map<String, String> vcd;

    @Parameter(required = false, property = "vrops", defaultValue = "${vrops.*}")
    private Map<String, String> vrops;

    @Parameter(required = false, property = "vrli", defaultValue = "${vrli.*}")
    private Map<String, String> vrli;

    @Parameter(required = false, property = "ssh", defaultValue = "${ssh.*}")
    private Map<String, String> ssh;
    

    @Parameter(required = true, property = "ignoreSslCertificate", defaultValue = "false")
    private boolean ignoreSslCertificate;

    @Parameter(required = true, property = "ignoreSslHostname", defaultValue = "false")
    private boolean ignoreSslHostname;

    @Parameter(required = false, property = "connectionTimeout",  defaultValue = "${vrealize.connection.timeout}")
    private int connectionTimeout;

    @Parameter(required = false, property = "socketTimeout", defaultValue = "${vrealize.socket.timeout}")
    private int socketTimeout;

    protected void processSslSystemProperties() {
        System.setProperty(RestClientFactory.IGNORE_SSL_CERTIFICATE_VERIFICATION, Boolean.toString(ignoreSslCertificate));
        System.setProperty(RestClientFactory.IGNORE_SSL_HOSTNAME_VERIFICATION, Boolean.toString(ignoreSslHostname));
    }

    protected void processOtherSystemProperties() {
        System.setProperty(RestClientFactory.CONNECTION_TIMEOUT, String.valueOf(connectionTimeout));
        System.setProperty(RestClientFactory.SOCKET_TIMEOUT, String.valueOf(socketTimeout));
    }

    protected void overwriteFromCmdLine(Properties props, String prefix) {
        for (Object o : System.getProperties().keySet()) {
            String key = o.toString();
            if (key.startsWith(prefix)) {
                props.put(key.substring(prefix.length()), System.getProperty(key));
            }
        }
    }

    protected ConfigurationVro getConfigurationForVro() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VRO);
        if (configuration.isPresent()) {
            return (ConfigurationVro) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete vRO configuration.");
        }
    }

    protected ConfigurationVra getConfigurationForVra() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VRA);
        if (configuration.isPresent()) {
            return (ConfigurationVra) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete vRO configuration.");
        }
    }

    protected ConfigurationVraNg getConfigurationForVraNg() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VRANG);
        if (configuration.isPresent()) {
            return (ConfigurationVraNg) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete vRA ng configuration.");
        }
    }

    /**
     * Retrieve ABX configuration for ABX interaction. The configuration structure is
     * defined in the Artifact Manager project.
     * @return ABX configuration
     * @throws ConfigurationException
     */
    protected ConfigurationAbx getConfigurationForAbx() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.ABX);
        if (configuration.isPresent()) {
            return (ConfigurationAbx) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete ABX configuration.");
        }
    }

    protected ConfigurationVrli getConfigurationForVrli() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VRLI);
        if (configuration.isPresent()) {
            return (ConfigurationVrli) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete VRLI configuration.");
        }
    }

    protected ConfigurationVcd getConfigurationForVcd() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VCDNG);
        if (configuration.isPresent()) {
            return (ConfigurationVcd) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete vCD configuration.");
        }
    }

    protected ConfigurationVrops getConfigurationForVrops() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.VROPS);
        if (configuration.isPresent()) {
            return (ConfigurationVrops) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete vCD configuration.");
        }
    }

    protected ConfigurationSsh getConfigurationForSsh() throws ConfigurationException {
        Optional<Configuration> configuration = Optional
                .ofNullable(ConfigurationSsh.fromProperties(getConfigurationProperties(PackageType.BASIC, ssh, "ssh.")));
        if (configuration.isPresent()) {
            return (ConfigurationSsh) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete SSH configuration.");
        }
    }
    protected ConfigurationCs getConfigurationForCs() throws ConfigurationException {
        Optional<Configuration> configuration = getConfigurationForType(PackageType.CS);
        if (configuration.isPresent()) {
            return (ConfigurationCs) configuration.get();
        } else {
            throw new ConfigurationException("Invalid or incomplete CS configuration.");
        }
    }

    protected RestClientVro getVroRestClient() throws ConfigurationException {
        return RestClientFactory.getClientVro(getConfigurationForVro());
    }

    protected Optional<Configuration> getConfigurationForType(PackageType type) throws ConfigurationException {
        if (PackageType.VRA == type) {
            return Optional.ofNullable(ConfigurationVra.fromProperties(getConfigurationProperties(type, vra, "vra.")));
        } else if (PackageType.VRO == type) {
            return Optional.ofNullable(ConfigurationVro.fromProperties(getConfigurationProperties(type, vro, "vro.")));
        } else if (PackageType.VCDNG == type) {
            return Optional.ofNullable(ConfigurationVcd.fromProperties(getConfigurationProperties(type, vcd, "vcd.")));
        } else if (PackageType.VROPS == type) {
            return Optional
                    .ofNullable(ConfigurationVrops.fromProperties(getConfigurationProperties(type, vrops, "vrops.")));
        } else if (PackageType.VRANG == type) {
            return Optional
                    .ofNullable(ConfigurationVraNg.fromProperties(getConfigurationProperties(type, vrang, "vrang.")));
        } else if (PackageType.ABX == type) {
            // Intentionally parse vrang.* configuration properties for ABX as they are common with regular vRA configurations.
            return Optional
                    .ofNullable(ConfigurationAbx.fromProperties(getConfigurationProperties(type, vrang, "vrang.")));
        } else if (PackageType.VRLI == type) {
            return Optional
                    .ofNullable(ConfigurationVrli.fromProperties(getConfigurationProperties(type, vrli, "vrli.")));
        } else if (PackageType.CS == type) {
            return Optional.ofNullable(ConfigurationCs.fromProperties(getConfigurationProperties(type, vrang, "vrang.")));
        } else {
            return Optional.empty();
        }
    }

    private Properties getConfigurationProperties(PackageType type, Map<String, String> map, String prefix) {
        Properties props = new Properties();
        getLog().info("Reading config for type : " + type);
        map.forEach((key, value) -> {
            if (map.get(key) != null) {
                props.setProperty(key, value);
            }
        });
        overwriteFromCmdLine(props, prefix);
        overwriteServerCredentials(props);
        overwriteConfigurationPropertiesForType(type, props);

        return props;
    }

    protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
        props.setProperty(Configuration.CONNECTION_TIMEOUT, Configuration.DEFAULT_CONNECTION_TIMEOUT.toString());
        props.setProperty(Configuration.SOCKET_TIMEOUT, Configuration.DEFAULT_SOCKET_TIMEOUT.toString());
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.processSslSystemProperties();
        this.processOtherSystemProperties();
    }

    private void overwriteServerCredentials(Properties props) {
        Optional<String> serverId = Optional.ofNullable(props.getProperty("serverId"));
        String host = props.getProperty(Configuration.HOST);
        Optional.ofNullable(serverId.orElse(host)).flatMap(id -> getServer(id)).ifPresent(server -> {
            props.setProperty(Configuration.USERNAME, server.getUsername());
            props.setProperty(Configuration.PASSWORD, server.getPassword());
        });
    }

    private Optional<Server> getServer(String serverId) {
        return Optional.ofNullable(settings.getServer(serverId)).map(server -> {
            SettingsDecryptionRequest request = new DefaultSettingsDecryptionRequest(server);
            SettingsDecryptionResult result = decrypter.decrypt(request);
            return result.getServer();
        });
    }
}
