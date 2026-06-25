/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.Test;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;

/**
 * Unit tests for ConfigurationVcfAuto.
 */
class ConfigurationVcfAutoTest {

    @Test
    void testFromProperties_ValidConfiguration() throws ConfigurationException {
        Properties props = createValidProperties();
        ConfigurationVcfAuto config = ConfigurationVcfAuto.fromProperties(props);

        assertNotNull(config);
        assertEquals("vcfa.example.com", config.getHost());
        assertEquals(443, config.getPort());
        assertEquals("refresh-123", config.getRefreshToken());
    }

    @Test
    void testGetAuthHost_FallsBackToHost() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.CSP_HOST);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals("vcfa.example.com", config.getAuthHost());
    }

    @Test
    void testGetAuthHost_UsesCspHost() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.CSP_HOST, "csp.example.com");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals("csp.example.com", config.getAuthHost());
    }

    @Test
    void testGetters_WithExplicitValues() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.DATA_COLLECTION_DELAY_SECONDS, "120");
        props.setProperty(ConfigurationVcfAuto.VRO_INTEGRATION, "embedded-VRO");
        props.setProperty(ConfigurationVcfAuto.CLOUD_PROXY_NAME, "proxy-1");
        props.setProperty(ConfigurationVcfAuto.PACKAGE_IMPORT_OVERWRITE_MODE, "OVERWRITE");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals("120", config.getDataCollectionDelaySeconds());
        assertEquals("embedded-VRO", config.getVroIntegration());
        assertEquals("proxy-1", config.getCloudProxyName());
        assertEquals("OVERWRITE", config.getPackageImportOverwriteMode());
        assertEquals("projectA", config.getProjectName());
        assertEquals("orgA", config.getOrgName());
        assertEquals("refresh-123", config.getRefreshToken());
    }

    @Test
    void testGetImportTimeout_Default() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.IMPORT_TIMEOUT);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals(ConfigurationVcfAuto.DEFAULT_IMPORT_TIMEOUT, config.getImportTimeout());
    }

    @Test
    void testGetImportTimeout_CustomValue() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.IMPORT_TIMEOUT, "9000");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals(Integer.valueOf(9000), config.getImportTimeout());
    }

    @Test
    void testGetImportTimeout_InvalidValueFallsBackToDefault() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.IMPORT_TIMEOUT, "not-a-number");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertEquals(ConfigurationVcfAuto.DEFAULT_IMPORT_TIMEOUT, config.getImportTimeout());
    }

    @Test
    void testGetProxy_NullWhenEmpty() {
        Properties props = createValidProperties();
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertNull(config.getProxy());
    }

    @Test
    void testGetProxy_ValidHost() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.PROXY, "http://proxy.example.com:8080");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        HttpHost proxy = config.getProxy();
        assertNotNull(proxy);
        assertEquals("proxy.example.com", proxy.getHostName());
        assertEquals(8080, proxy.getPort());
    }

    @Test
    void testGetProxy_InvalidThrowsConfigurationException() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.PROXY, "not a valid proxy");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertThrows(ConfigurationException.class, config::getProxy);
    }

    @Test
    void testGetUnreleaseBlueprintVersions_DefaultTrue() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.UNRELEASE_BLUEPRINT_VERSIONS);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertTrue(config.getUnreleaseBlueprintVersions());
    }

    @Test
    void testGetUnreleaseBlueprintVersions_False() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.UNRELEASE_BLUEPRINT_VERSIONS, "false");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertFalse(config.getUnreleaseBlueprintVersions());
    }

    @Test
    void testGetDeleteContent_DefaultFalse() {
        Properties props = createValidProperties();
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertFalse(config.getDeleteContent());
    }

    @Test
    void testGetDeleteContent_True() {
        Properties props = createValidProperties();
        props.setProperty(ConfigurationVcfAuto.DELETE_CONTENT, "true");
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertTrue(config.getDeleteContent());
    }

    @Test
    void testValidate_MissingHostThrows() {
        Properties props = createValidProperties();
        props.remove(Configuration.HOST);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        ConfigurationException ex = assertThrows(ConfigurationException.class, () -> config.validate(false));
        assertTrue(ex.getMessage().contains("Hostname"));
    }

    @Test
    void testValidate_MissingProjectNameThrows() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.PROJECT_NAME);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        ConfigurationException ex = assertThrows(ConfigurationException.class, () -> config.validate(false));
        assertTrue(ex.getMessage().contains("Project name"));
    }

    @Test
    void testValidate_MissingOrgNameThrows() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.ORGANIZATION_NAME);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        ConfigurationException ex = assertThrows(ConfigurationException.class, () -> config.validate(false));
        assertTrue(ex.getMessage().contains("Organization Name"));
    }

    @Test
    void testValidate_MissingAuthThrows() {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.REFRESH_TOKEN);
        props.remove(Configuration.USERNAME);
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        ConfigurationException ex = assertThrows(ConfigurationException.class, () -> config.validate(false));
        assertTrue(ex.getMessage().contains("Refresh token or Username"));
    }

    @Test
    void testFromProperties_UsesUsernamePassword() throws ConfigurationException {
        Properties props = createValidProperties();
        props.remove(ConfigurationVcfAuto.REFRESH_TOKEN);
        props.setProperty(Configuration.USERNAME, "admin@tenant");
        props.setProperty(Configuration.PASSWORD, "secret");

        ConfigurationVcfAuto config = ConfigurationVcfAuto.fromProperties(props);
        assertEquals("admin", config.getUsername());
        assertEquals("tenant", config.getDomain());
    }

    @Test
    void testDeprecationWarnings_DoesNotThrow() {
        Properties props = createValidProperties();
        ConfigurationVcfAuto config = new ConfigurationVcfAuto(props);

        assertDoesNotThrow(config::deprecationWarnings);
    }

    private Properties createValidProperties() {
        Properties props = new Properties();
        props.setProperty(Configuration.HOST, "vcfa.example.com");
        props.setProperty(Configuration.PORT, "443");
        props.setProperty(ConfigurationVcfAuto.ORGANIZATION_NAME, "orgA");
        props.setProperty(ConfigurationVcfAuto.PROJECT_NAME, "projectA");
        props.setProperty(ConfigurationVcfAuto.REFRESH_TOKEN, "refresh-123");
        return props;
    }
}
