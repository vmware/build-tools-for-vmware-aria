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
package com.vmware.pscoe.iac.artifact.vcf.automation.configuration;

import java.util.Properties;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

public class VcfaConfiguration extends Configuration {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROJECT_ID = "projectId";
    public static final String API_TOKEN = "apiToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String SOCKET_TIMEOUT = "socketTimeout";
    public static final String IGNORE_SSL_CERTIFICATE = "ignoreSslCertificate";
    public static final String IGNORE_SSL_HOSTNAME = "ignoreSslHostname";
    public static final String TENANT_NAME = "tenantName";
    public static final String API_VERSION = "apiVersion";
    public static final String LOGIN_TYPE = "loginType";

    public VcfaConfiguration(Properties props) {
        super(PackageType.VCFA, props);
    }

    public String getHost() {
        return this.properties.getProperty(HOST);
    }

    public void setHost(String host) {
        this.properties.setProperty(HOST, host);
    }

    public int getPort() {
        return Integer.parseInt(this.properties.getProperty(PORT, "443"));
    }

    public void setPort(int port) {
        this.properties.setProperty(PORT, String.valueOf(port));
    }

    public String getUsername() {
        return this.properties.getProperty(USERNAME);
    }

    public void setUsername(String username) {
        this.properties.setProperty(USERNAME, username);
    }

    public String getPassword() {
        return this.properties.getProperty(PASSWORD);
    }

    public void setPassword(String password) {
        this.properties.setProperty(PASSWORD, password);
    }

    public String getProjectId() {
        return this.properties.getProperty(PROJECT_ID);
    }

    public void setProjectId(String projectId) {
        this.properties.setProperty(PROJECT_ID, projectId);
    }

    public String getApiToken() {
        return this.properties.getProperty(API_TOKEN);
    }

    public void setApiToken(String apiToken) {
        this.properties.setProperty(API_TOKEN, apiToken);
    }

    public String getRefreshToken() {
        return this.properties.getProperty(REFRESH_TOKEN);
    }

    public void setRefreshToken(String refreshToken) {
        this.properties.setProperty(REFRESH_TOKEN, refreshToken);
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(this.properties.getProperty(CONNECTION_TIMEOUT, "30000"));
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.properties.setProperty(CONNECTION_TIMEOUT, String.valueOf(connectionTimeout));
    }

    public int getSocketTimeout() {
        return Integer.parseInt(this.properties.getProperty(SOCKET_TIMEOUT, "30000"));
    }

    public void setSocketTimeout(int socketTimeout) {
        this.properties.setProperty(SOCKET_TIMEOUT, String.valueOf(socketTimeout));
    }

    public boolean isIgnoreSslCertificate() {
        return Boolean.parseBoolean(this.properties.getProperty(IGNORE_SSL_CERTIFICATE, "false"));
    }

    public void setIgnoreSslCertificate(boolean ignoreSslCertificate) {
        this.properties.setProperty(IGNORE_SSL_CERTIFICATE, String.valueOf(ignoreSslCertificate));
    }

    public boolean isIgnoreSslHostname() {
        return Boolean.parseBoolean(this.properties.getProperty(IGNORE_SSL_HOSTNAME, "false"));
    }

    public void setIgnoreSslHostname(boolean ignoreSslHostname) {
        this.properties.setProperty(IGNORE_SSL_HOSTNAME, String.valueOf(ignoreSslHostname));
    }

    public String getTenantName() {
        return this.properties.getProperty(TENANT_NAME);
    }

    public void setTenantName(String tenantName) {
        this.properties.setProperty(TENANT_NAME, tenantName);
    }

    public String getApiVersion() {
        return this.properties.getProperty(API_VERSION, "40.0");
    }

    public void setApiVersion(String apiVersion) {
        this.properties.setProperty(API_VERSION, apiVersion);
    }

    public String getLoginType() {
        return this.properties.getProperty(LOGIN_TYPE, "tenant");
    }

    public void setLoginType(String loginType) {
        this.properties.setProperty(LOGIN_TYPE, loginType);
    }

    public String getBaseUrl() {
        return String.format("https://%s:%d", getHost(), getPort());
    }

    @Override
    public PackageType getPackageType() {
        return PackageType.VCFA;
    }

    public static VcfaConfiguration fromProperties(Properties props) {
        if (props == null || props.isEmpty()) {
            return null;
        }
        return new VcfaConfiguration(props);
    }
}
