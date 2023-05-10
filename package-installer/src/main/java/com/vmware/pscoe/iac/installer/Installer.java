package com.vmware.pscoe.iac.installer;

/*
 * #%L
 * installer
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.VroWorkflowExecutor;
import com.vmware.pscoe.iac.artifact.VroWorkflowExecutor.WorkflowExecutionException;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationAbx;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationSsh;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVroNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowExecution;
import com.vmware.pscoe.iac.artifact.rest.RestClientFactory;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;

/**
 * Created by tsimchev on 2/22/18.
 */

enum Option {

	/**
	 *
	 * timeouts.
	 */
    CONNECTION_TIMEOUT(
        "http_connection_timeout",
        Configuration.CONNECTION_TIMEOUT),

	/**
	 * Socket timeout.
	 */
    SOCKET_TIMEOUT(
        "http_socket_timeout",
        Configuration.SOCKET_TIMEOUT),

    /**
	 * Configurations.
	 *
     * VRA server.
     */
    VRA_SERVER(
            "vra_server",
            Configuration.HOST),
	/**
	 * VRA port.
	 */
    VRA_PORT(
            "vra_port",
            Configuration.PORT),
	/**
	 * VRA tenant.
	 */
    VRA_TENANT(
            "vra_tenant",
            ConfigurationVra.TENANT),
    /**
     * VRA username.
     */
    VRA_USERNAME(
            "vra_username",
            Configuration.USERNAME),
	/**
	 * VRA password.
	 */
    VRA_PASSWORD(
            "vra_password",
            Configuration.PASSWORD),
	/**
	 * VRA import old versions.
	 */
    VRA_IMPORT_OLD_VERSIONS(
            "vra_import_old_versions",
            ConfigurationVra.IMPORT_OLD_VERSIONS),
	/**
	 * Skip VRA import old versions.
	 */
    SKIP_VRA_IMPORT_OLD_VERSIONS(
            "skip_vra_import_old_versions",
            StringUtils.EMPTY),
	/**
	 *  VRA import overwrite mode.
	 */
    VRA_IMPORT_OVERWRITE_MODE(
            "vra_import_overwrite_mode",
            ConfigurationVra.PACKAGE_IMPORT_OVERWRITE_MODE),
	/**
	 * VRANG host.
	 */
    VRANG_SERVER(
            "vrang_host",
            Configuration.HOST),
	/**
	 * VRANG CSP host.
	 */
    VRANG_CSP_SERVER(
            "vrang_csp_host",
            ConfigurationVraNg.CSP_HOST),
	/**
	 * VRANG proxy.
	 */
    VRANG_PROXY(
            "vrang_proxy",
            ConfigurationVraNg.PROXY),
	/**
	 * VRANG proxy required.
	 */
	VRANG_PROXY_REQUIRED(
			"vrang_proxy_required",
			ConfigurationVraNg.PROXY_REQUIRED),
	/**
	 * VRANG port.
	 */
    VRANG_PORT(
            "vrang_port",
            Configuration.PORT),
	/**
	 * VRANG project Id.
	 */
    VRANG_PROJECT_ID(
            "vrang_project.id",
            ConfigurationVraNg.PROJECT_ID),
	/**
	 * VRANG data collection delay in seconds.
	 */
	VRANG_DATA_COLLECTION_DELAY_SECONDS(
		"vrang_data.collection.delay.seconds",
		ConfigurationVraNg.DATA_COLLECTION_DELAY_SECONDS),
	/**
	 * VRANG org Id.
	 */
    VRANG_ORGANIZATION_ID(
        "vrang_org_id",
        ConfigurationVraNg.ORGANIZATION_ID),
	/**
	 * VRANG org name.
	 */
    VRANG_ORGANIZATION_NAME(
            "vrang_org_name",
            ConfigurationVraNg.ORGANIZATION_NAME),
	/**
	 * VRANG project name.
	 */
    VRANG_PROJECT_NAME(
            "vrang_project_name",
            ConfigurationVraNg.PROJECT_NAME),
	/**
	 * VRANG auth with refresh token.
	 */
    VRANG_AUTH_WITH_REFRESH_TOKEN(
            "vrang_auth_with_refresh_token",
            "auth_with_" + ConfigurationVraNg.REFRESH_TOKEN),
	/**
	 * VRANG refresh token.
	 */
    VRANG_REFRESH_TOKEN(
        "vrang_refresh_token",
        ConfigurationVraNg.REFRESH_TOKEN),
	/**
	 * VRANG username.
	 */
    VRANG_USERNAME(
            "vrang_username",
            Configuration.USERNAME),
	/**
	 * VRANG password.
	 */
    VRANG_PASSWORD(
            "vrang_password",
            Configuration.PASSWORD),
	/**
	 * VRANG import overwrite mode.
	 */
    VRANG_IMPORT_OVERWRITE_MODE(
            "vrang_import_overwrite_mode",
            ConfigurationVraNg.PACKAGE_IMPORT_OVERWRITE_MODE),
	/**
	 * VRANG import timeout.
	 */
    VRANG_IMPORT_TIMEOUT(
            "vrang_import_timeout",
            ConfigurationVraNg.IMPORT_TIMEOUT),
	/**
	 * VRANG VRO integration name.
	 */
    VRANG_VRO_INTEGRATION_NAME(
            "vrang_vro_integration_name",
            ConfigurationVraNg.VRO_INTEGRATION),
	/**
	 * VRANG cloud proxy name.
	 */
    VRANG_CLOUD_PROXY_NAME(
            "varng_cloud_proxy_name",
            ConfigurationVraNg.CLOUD_PROXY_NAME),
	/**
	 * VRLI server.
	 */
    VRLI_SERVER(
            "vrli_server",
            Configuration.HOST),
	/**
	 * VRLI port.
	 */
    VRLI_PORT(
            "vrli_port",
            Configuration.PORT),
	/**
	 * VRLI provider.
	 */
    VRLI_PROVIDER(
            "vrli_provider",
            ConfigurationVrli.PROVIDER),
	/**
	 * VRLI username.
	 */
    VRLI_USERNAME(
            "vrli_username",
            Configuration.USERNAME),
	/**
	 * VRLI password.
	 */
    VRLI_PASSWORD(
            "vrli_password",
            Configuration.PASSWORD),
	/**
	 * VRLI package import overwrite mode.
	 */
    VRLI_PACKAGE_IMPORT_OVERWRITE_MODE(
            "vrli_package_import_overwrite_mode",
            ConfigurationVrli.PACKAGE_IMPORT_OVERWRITE_MODE),
	/**
	 * VRLI VROPS server.
	 */
    VRLI_VROPS_INTEGRATION_HOST(
            "vrli_vrops_server",
            ConfigurationVrli.INTEGRATION_VROPS_HOST),
	/**
	 * VRLI VROPS server port.
	 */
    VRLI_VROPS_INTEGRATION_PORT(
            "vrli_vrops_server_port",
            ConfigurationVrli.INTEGRATION_VROPS_PORT),
	/**
	 * VRLI VROPS server user.
	 */
    VRLI_VROPS_INTEGRATION_USER(
            "vrli_vrops_server_user",
            ConfigurationVrli.INTEGRATION_VROPS_USER),
	/**
	 * VRLI VROPS server password.
	 */
    VRLI_VROPS_INTEGRATION_PASSWORD(
            "vrli_vrops_server_password",
            ConfigurationVrli.INTEGRATION_VROPS_PASS),
	/**
	 * VRLI VROPS server auth source.
	 */
    VRLI_VROPS_INTEGRATION_AUTH_SOURCE(
            "vrli_vrops_server_auth_source",
            ConfigurationVrli.INTEGRATION_VROPS_AUTH_SOURCE),
	/**
	 * VCD server.
	 */
    VCD_SERVER(
            "vcd_server",
            Configuration.HOST),
	/**
	 * VCD port.
	 */
    VCD_PORT(
            "vcd_port",
            Configuration.PORT),
	/**
	 * VCD username.
	 */
    VCD_USERNAME(
            "vcd_username",
            Configuration.USERNAME),
	/**
	 * VCD password.
	 */
    VCD_PASSWORD(
            "vcd_password",
            Configuration.PASSWORD),
	/**
	 * VCD import old versions.
	 */
    VCD_IMPORT_OLD_VERSIONS(
            "vcd_import_old_versions",
            ConfigurationVcd.IMPORT_OLD_VERSIONS),
	/**
	 * skip VCD import old versions.
	 */
    SKIP_VCD_IMPORT_OLD_VERSIONS(
            "skip_vcd_import_old_versions",
            StringUtils.EMPTY),
	/**
	 * VCD import overwrite mode.
	 */
    VCD_IMPORT_OVERWRITE_MODE(
            "vcd_import_overwrite_mode",
            ConfigurationVcd.PACKAGE_IMPORT_OVERWRITE_MODE),

	/**
	 * VRO server.
	 */
    VRO_SERVER(
            "vro_server",
            Configuration.HOST),
	/**
	 * VRO port.
	 */
    VRO_PORT(
            "vro_port",
            Configuration.PORT),
	/**
	 * VRO tenant.
	 */
    VRO_TENANT(
            "vro_tenant",
            ConfigurationVro.TENANT),
	/**
	 * VRO username.
	 */
    VRO_USERNAME(
            "vro_username",
            Configuration.USERNAME),
	/**
	 * VRO password.
	 */
    VRO_PASSWORD(
            "vro_password",
            Configuration.PASSWORD),
	/**
	 * VRO refresh token.
	 */
	VRO_REFRESH_TOKEN(
		"vro_refresh_token",
			ConfigurationVro.REFRESH_TOKEN),
	/**
	 * VRO auth.
	 */
    VRO_AUTH(
            "vro_auth",
            ConfigurationVro.AUTH),
	/**
	 * VRO auth host.
	 */
    VRO_AUTHHOST(
            "vro_authHost",
            ConfigurationVro.AUTH_HOST),
	/**
	 * VRO auth port.
	 */
    VRO_AUTHPORT(
            "vro_authPort",
            ConfigurationVro.AUTH_PORT),
	/**
	 * VRO proxy.
	 */
    VRO_PROXY(
            "vro_proxy",
            ConfigurationVro.PROXY),
	/**
	 * VRO embedded.
	 */
    VRO_EMBEDDED(
            "vro_embedded",
            ConfigurationVroNg.EMBEDDED),

	/**
	 * VROPS ssh port.
	 */
    VROPS_SSH_PORT(
            "vrops_sshPort",
            ConfigurationVrops.SSH_PORT),
	/**
	 * VROPS ssh username.
	 */
    VROPS_SSH_USER(
            "vrops_sshUsername",
            Configuration.USERNAME),
	/**
	 * VROPS ssh password.
	 */
    VROPS_SSH_PASSWORD(
            "vrops_sshPassword",
            Configuration.PASSWORD),
	/**
	 * VROPS dashboard user.
	 */
    VROPS_DASHBOARD_USER(
            "vrops_dashboardUser",
            ConfigurationVrops.VROPS_DASHBOARD_USER),
	/**
	 * VROPS http host.
	 */
    VROPS_HTTP_HOST(
            "vrops_httpHost",
            Configuration.HOST),
	/**
	 * VROPS http port.
	 */
    VROPS_HTTP_PORT(
            "vrops_httpPort",
            Configuration.PORT),
	/**
	 * VROPS REST user.
	 */
    VROPS_REST_USER(
            "vrops_restUser",
            ConfigurationVrops.VROPS_REST_USER),
	/**
	 * VROPS REST password.
	 */
    VROPS_REST_PASSWORD(
            "vrops_restPassword",
            ConfigurationVrops.VROPS_REST_PASSWORD),
	/**
	 * VROPS REST auth source.
	 */
    VROPS_REST_AUTH_SOURCE(
            "vrops_restAuthSource",
            ConfigurationVrops.VROPS_REST_AUTH_SOURCE),
	/**
	 * VROPS REST auth provider.
	 */
    VROPS_REST_AUTH_PROVIDER(
            "vrops_restAuthProvider",
            ConfigurationVrops.VROPS_REST_AUTH_PROVIDER),

	/**
	 * Ignore ssl certificate verification.
	 */
    IGNORE_SSL_CERT_CHECK(
            "ignore_ssl_certificate_verification",
            RestClientFactory.IGNORE_SSL_CERTIFICATE_VERIFICATION),
	/**
	 * Ignore ssl host verification.
	 */
    IGNORE_SSL_HOST_CHECK(
            "ignore_ssl_host_verification",
            RestClientFactory.IGNORE_SSL_HOSTNAME_VERIFICATION),

	/**
	 * ssh server.
	 */
    SSH_SERVER(
            "ssh_server",
            Configuration.HOST),
	/**
	 * ssh port.
	 */
    SSH_PORT(
            "ssh_port",
            Configuration.PORT),
	/**
	 * ssh username.
	 */
    SSH_USERNAME(
            "ssh_username",
            Configuration.USERNAME),
	/**
	 * ssh password.
	 */
    SSH_PASSWORD(
            "ssh_password",
            Configuration.PASSWORD),
	/**
	 * ssh directory.
	 */
    SSH_DIRECTORY(
            "ssh_directory",
            ConfigurationSsh.SSH_DIRECTORY),

	/**
	 * Operations.
	 *
	 * VRA import packages.
	 */
    VRA_IMPORT(
            "vra_import_packages",
            StringUtils.EMPTY),
	/**
	 * VRANG import packages.
	 */
    VRANG_IMPORT(
            "vra_ng_import_packages",
            StringUtils.EMPTY),
	/**
	 * VRO import packages.
	 */
    VRO_IMPORT(
            "vro_import_packages",
			StringUtils.EMPTY),
	/**
	 * VRO enable backup.
	 */
	VRO_ENABLE_BACKUP(
		"vro_enable_backup",
		StringUtils.EMPTY),

	/**
	 * VCD import packages.
	 */
    VCD_IMPORT(
            "vcd_import_packages",
            StringUtils.EMPTY),
	/**
	 * VROPS import packages.
	 */
    VROPS_IMPORT(
            "vrops_import_packages",
            StringUtils.EMPTY),
	/**
	 * VRLI import packages.
	 */
    VRLI_IMPORT(
            "vrli_import_packages",
            StringUtils.EMPTY),
	/**
	 * ssh import packages.
	 */
    SSH_IMPORT(
        "ssh_import_packages",
        StringUtils.EMPTY),
	/**
	 * VRO import old versions.
	 */
    VRO_IMPORT_OLD_VERSIONS(
            "vro_import_old_versions",
            ConfigurationVro.IMPORT_OLD_VERSIONS),
	/**
	 * Skip VRO import old versions.
	 */
    SKIP_VRO_IMPORT_OLD_VERSIONS(
            "skip_vro_import_old_versions",
            StringUtils.EMPTY),
	/**
	 * VRO import configuration attribute values.
	 */
    VRO_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES(
            "vro_import_configuration_attribute_values",
            ConfigurationVro.PACKAGE_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES),
	/**
	 * VRO import configuration secure attribute values.
	 */
    VRO_IMPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES(
            "vro_import_configuration_secure_attribute_values",
            ConfigurationVro.PACKAGE_IMPORT_CONFIGURATION_SECURE_ATTRIBUTE_VALUES),
	/**
	 * VRO run workflow.
	 */
    VRO_RUN_WORKFLOW(
            "vro_run_workflow",
            StringUtils.EMPTY),
	/**
	 * VRO run workflow ID.
	 */
    VRO_RUN_WORKFLOW_ID(
            "vro_run_workflow_id",
            StringUtils.EMPTY),
	/**
	 * VRO run workflow input file path.
	 */
    VRO_RUN_WORKFLOW_INPUT_FILE_PATH(
            "vro_run_workflow_input_file_path",
            StringUtils.EMPTY),
	/**
	 * VRO run workflow output file path.
	 */
    VRO_RUN_WORKFLOW_OUTPUT_FILE(
            "vro_run_workflow_output_file_path",
            StringUtils.EMPTY),
	/**
	 * VRO run workflow error file path.
	 */
    VRO_RUN_WORKFLOW_ERR_FILE(
            "vro_run_workflow_err_file_path",
            StringUtils.EMPTY),
	/**
	 * VRO run workflow timeout.
	 */
    VRO_RUN_WORKFLOW_TIMEOUT(
            "vro_run_workflow_timeout",
            StringUtils.EMPTY),
	/**
	 * VRA delete old versions.
	 */
    VRA_DELETE_OLD_VERSIONS(
            "vra_delete_old_versions",
            StringUtils.EMPTY),
	/**
	 * VRA delete last version.
	 */
    VRA_DELETE_LAST_VERSION(
            "vra_delete_last_version",
            StringUtils.EMPTY),
	/**
	 * VRA delete include dependencies.
	 */
    VRA_DELETE_INCLUDE_DEPENDENCIES(
            "vra_delete_include_dependencies",
            StringUtils.EMPTY),
	/**
	 * VRO delete old versions.
	 */
    VRO_DELETE_OLD_VERSIONS(
            "vro_delete_old_versions",
            StringUtils.EMPTY),
	/**
	 * VRO delete last version.
	 */
    VRO_DELETE_LAST_VERSION(
            "vro_delete_last_version",
            StringUtils.EMPTY),
	/**
	 * VCD delete old versions.
	 */
    VCD_DELETE_OLD_VERSIONS(
            "vcd_delete_old_versions",
            StringUtils.EMPTY),
	/**
	 * CS import packages.
	 */
    CS_IMPORT(
            "cs_import_packages",
            StringUtils.EMPTY),
	/**
	 * ABX import packages.
	 */
    ABX_IMPORT(
            "abx_import_packages",
            StringUtils.EMPTY);


	/**
	 * Option name.
	 */
    private final String name;
	/**
	 * Option mapping.
	 */
    private final String mapping;

    Option(final String optionName, final String optionMapping) {
        this.name = optionName;
        this.mapping = optionMapping;
    }

    public String getName() {
        return name;
    }

    public String getMapping() {
        return mapping;
    }

	/**
	 * Returns a string representation of name.
	 */
    @Override
    public String toString() {
        return name;
    }
}

class Input extends Properties {
	/**
	 * Password prefix.
	 */
    static final String PASS_PREFIX = "{PASS}";
	/**
	 * Text IO factory.
	 */
    private final TextIO textIO = TextIoFactory.getTextIO();

    public synchronized void put(final Option o, final Object v) {
        super.put(o.getName(), String.valueOf(v));
    }

    public String get(final Option o) {
        return super.getProperty(o.getName());
    }

    public TextIO getText() {
        return textIO;
    }

    public boolean allTrue(final Option... os) {
        boolean isActive = true;
        for (Option o : os) {
            String value = super.getProperty(o.getName());
            isActive = isActive && !StringUtils.isEmpty(value) && !value.equals("false");
        }
        return isActive;
    }

    public boolean anyTrue(final Option... os) {
        for (final Option o : os) {
            String value = super.getProperty(o.getName());
            if (!StringUtils.isEmpty(value) && !value.equals("false")) {
                return true;
            }
        }
        return false;
    }

    public void putAll(final Properties properties) {
        super.putAll(properties);
    }

    public void load(final String location) {
        textIO.getTextTerminal().println("Loading environment profile from " + location);
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(location));
            Enumeration<?> keys = props.propertyNames();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                String value = props.getProperty("" + key);
                try {
                    if (key.toString().endsWith("password") && value.startsWith(Input.PASS_PREFIX)) {
                        String decoded = value.substring(Input.PASS_PREFIX.length());
                        decoded = new String(Base64.getDecoder().decode(decoded.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                        value = decoded;
                    }
                } catch (IllegalArgumentException invalidBase64) {
                    // Ignore;
                }
                super.setProperty("" + key, value);
            }
        } catch (IOException e) {
            textIO.getTextTerminal()
                    .println("Cannot load environment details from " + location + ". Reason " + e.getMessage());
        }
    }

    public void store(final String location) {
        textIO.getTextTerminal().println("Saving environment profile to " + location);
        Properties props = new Properties();
        Enumeration<?> keys = super.propertyNames();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String value = super.getProperty("" + key);
            if (key.toString().endsWith("password")) {
                value = Input.PASS_PREFIX + new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            }
            props.setProperty("" + key, value);
        }
        try {
            props.store(new FileOutputStream(location), null);
        } catch (IOException e) {
            textIO.getTextTerminal()
                    .println("Cannot store environment details from " + location + ". Reason " + e.getMessage());
        }
    }

    public Properties getMappings(final String prefix) {
        return getMappings(new String[] {prefix});
    }

    public Properties getMappings(final String[] prefixes) {
        Properties mappings = new Properties();
        for (int i = 0; i < prefixes.length; i++) {
            String prefix = prefixes[i];
            for (Option option : Option.values()) {
                boolean doMap = super.get(option.getName()) != null;
                doMap = doMap && (prefix != null && option.getName().startsWith(prefix) || option.getName().startsWith("ignore_"));
                doMap = doMap && option.getMapping() != null && !option.getMapping().trim().equals(StringUtils.EMPTY);
                if (doMap) {
                    mappings.put(option.getMapping(), super.get(option.getName()));
                }
            }
        }

        return mappings;
    }

}

public final class Installer {
	/**
	 * SSH port.
	 */
	private static final int SSH_PORT = 22;
	/**
	 * HTTPS port.
	 */
	private static final int HTTPS_PORT = 443;
	/**
	 * VRLI port.
	 */
	private static final int VRLI_PORT = 9543;
	/**
	 * VRO run workflow timeout.
	 */
	private static final int VRO_RUN_WORKFLOW_TIMEOUT = 300;
	/**
	 * VRANG data collection delay in seconds.
	 */
	private static final int VRANG_DATA_COLLECTION_DELAY_SECONDS = 600;
	/**
	 * Exit success code.
	 */
	private static final int EXIT_SUCCESS_CODE = 0;
	/**
	 * Exit workflow exec failed code.
	 */
    private static final int EXIT_WF_EXEC_FAILED_CODE = -1;

	private Installer() {
	}

    private enum ConfigurationPrefix {
		/**
		 * VRA.
		 */
        VRA("vra_"),
		/**
		 * VRANG.
		 */
        VRANG("vrang_"),
		/**
		 * VRO.
		 */
        VRO("vro_"),
		/**
		 * VROPS.
		 */
        VROPS("vrops_"),
		/**
		 * VCD.
		 */
        VCD("vcd_"),
		/**
		 * VRLI.
		 */
    	VRLI("vrli_"),
		/**
		 * SSH.
		 */
        SSH("ssh_");

		/**
		 * value.
		 */
        private String value;

        ConfigurationPrefix(final String prefix) {
            value = prefix;
        }

        public String getValue() {
            return value;
        }
    }

/**
 *
 * @param args
 * @throws ConfigurationException
 */
	public static void main(final String[] args) throws ConfigurationException {
        Input input = new Input();
        if (args.length > 0) {
            input.load(args[0]);
        } else {
            loadInputInteractiveMode(input);
        }

        // set common properties (i.e. ssl certificate check, timeouts, etc)
        setCommonProperties(input);

        if (input.allTrue(Option.VRO_IMPORT, Option.VRO_EMBEDDED)) {
            String[] arr = { ConfigurationPrefix.VRO.getValue(), ConfigurationPrefix.VRANG.getValue() };
            PackageStoreFactory.getInstance(ConfigurationVroNg.fromProperties(input.getMappings(arr)))
                    .importAllPackages(getFilesystemPackages(PackageType.VRO), false, input.allTrue(Option.VRO_ENABLE_BACKUP));
        } else if (input.allTrue(Option.VRO_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVro.fromProperties(input.getMappings(ConfigurationPrefix.VRO.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VRO), false, input.allTrue(Option.VRO_ENABLE_BACKUP));
		}

		boolean vroEnableBackup = false; //the backup will be possible only for vRO packages

        if (input.allTrue(Option.VRA_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVra.fromProperties(input.getMappings(ConfigurationPrefix.VRA.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VRA), false, vroEnableBackup);
        }

        if (input.allTrue(Option.CS_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationCs.fromProperties(input.getMappings(ConfigurationPrefix.VRANG.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.CS), false, vroEnableBackup);
        }

        if (input.allTrue(Option.ABX_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationAbx.fromProperties(input.getMappings(ConfigurationPrefix.VRANG.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.ABX), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VRANG_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVraNg.fromProperties(input.getMappings(ConfigurationPrefix.VRANG.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VRANG), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VCD_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVcd.fromProperties(input.getMappings(ConfigurationPrefix.VCD.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VCDNG), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VRO_DELETE_LAST_VERSION)) {
            PackageStore<?> packageStore = null;
            if (input.allTrue(Option.VRO_EMBEDDED)) {
                String[] prefixes = { ConfigurationPrefix.VRO.getValue(), ConfigurationPrefix.VRANG.getValue() };
                packageStore = PackageStoreFactory.getInstance(ConfigurationVro.fromProperties(input.getMappings(prefixes)));
            } else {
                packageStore = PackageStoreFactory.getInstance(ConfigurationVro.fromProperties(input.getMappings(ConfigurationPrefix.VRO.getValue())));
            }
            packageStore.deleteAllPackages(getFilesystemPackages(PackageType.VRO), true, false, false);
        }

        if (input.allTrue(Option.VRO_DELETE_OLD_VERSIONS)) {
            PackageStore<?> packageStore = null;
            if (input.allTrue(Option.VRO_EMBEDDED)) {
                String[] prefixes = { ConfigurationPrefix.VRO.getValue(), ConfigurationPrefix.VRANG.getValue() };
                packageStore = PackageStoreFactory.getInstance(ConfigurationVro.fromProperties(input.getMappings(prefixes)));
            } else {
                packageStore = PackageStoreFactory.getInstance(ConfigurationVro.fromProperties(input.getMappings(ConfigurationPrefix.VRO.getValue())));
            }
            packageStore.deleteAllPackages(getFilesystemPackages(PackageType.VRO), false, true, false);
        }

        if (input.allTrue(Option.VRA_DELETE_LAST_VERSION)) {
            String[] prefixes = { ConfigurationPrefix.VRA.getValue(), ConfigurationPrefix.VRANG.getValue() };
            PackageStoreFactory.getInstance(ConfigurationVra.fromProperties(input.getMappings(prefixes)))
                    .deleteAllPackages(getFilesystemPackages(PackageType.VRO), true, false, false);
        }

        if (input.allTrue(Option.VRA_DELETE_OLD_VERSIONS)) {
            String[] prefixes = { ConfigurationPrefix.VRA.getValue(), ConfigurationPrefix.VRANG.getValue() };
            PackageStoreFactory.getInstance(ConfigurationVra.fromProperties(input.getMappings(prefixes)))
                    .deleteAllPackages(getFilesystemPackages(PackageType.VRO), false, true, false);
        }

        if (input.anyTrue(Option.VRA_DELETE_LAST_VERSION, Option.VRA_DELETE_INCLUDE_DEPENDENCIES)) {
            String[] prefixes = { ConfigurationPrefix.VRA.getValue(), ConfigurationPrefix.VRANG.getValue() };
            PackageStoreFactory.getInstance(ConfigurationVra.fromProperties(input.getMappings(prefixes)))
                    .deleteAllPackages(getFilesystemPackages(PackageType.VRO), true, false, false);
        }

        if (input.anyTrue(Option.VRA_DELETE_OLD_VERSIONS, Option.VRA_DELETE_INCLUDE_DEPENDENCIES)) {
            String[] prefixes = { ConfigurationPrefix.VRA.getValue(), ConfigurationPrefix.VRANG.getValue() };
            PackageStoreFactory.getInstance(ConfigurationVra.fromProperties(input.getMappings(prefixes)))
                    .deleteAllPackages(getFilesystemPackages(PackageType.VRO), false, true, false);
        }

        if (input.allTrue(Option.VROPS_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVrops.fromProperties(input.getMappings(ConfigurationPrefix.VROPS.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VROPS), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VRLI_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationVrli.fromProperties(input.getMappings(ConfigurationPrefix.VRLI.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.VRLI), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VCD_DELETE_OLD_VERSIONS)) {
            // TODO - add clean up support for vCD
            input.getText().getTextTerminal().println("vCloud Director clean up is not supported yet.");
        }

        if (input.allTrue(Option.SSH_IMPORT)) {
            PackageStoreFactory.getInstance(ConfigurationSsh.fromProperties(input.getMappings(ConfigurationPrefix.SSH.getValue())))
                    .importAllPackages(getFilesystemPackages(PackageType.BASIC), false, vroEnableBackup);
        }

        if (input.allTrue(Option.VRO_RUN_WORKFLOW)) {
            try {
                runWorkflow(input);
            } catch (RuntimeException e) {
               input.getText().getTextTerminal().println("Error executing workflow: " + e.getMessage());
               System.exit(EXIT_WF_EXEC_FAILED_CODE);
            }
        }
        System.exit(EXIT_SUCCESS_CODE);
    }

    private static void userInput(final Input input, final Option param, final String prompt, final boolean defaultValue) {
        userInput(input, param, prompt, Boolean.valueOf(defaultValue), Boolean.class, false);
    }

    private static void userInput(final Input input, final Option param, final String prompt) {
        userInput(input, param, prompt, (String) null, String.class, false);
    }

    private static void userInput(final Input input, final Option param, final String prompt, final String defaultValue) {
        userInput(input, param, prompt, defaultValue, String.class, false);
    }

    private static void userInput(final Input input, final Option param, final String prompt, final int defaultValue) {
        userInput(input, param, prompt, Integer.valueOf(defaultValue), Integer.class, false);
    }

    private static void passInput(final Input input, final Option param, final String prompt) {
        userInput(input, param, prompt, null, String.class, true);
    }

    private static void userInput(final Input input, final Option param, final String prompt, final Object defaultValue, final Class<?> type, final boolean password) {
        String value = input.get(param);
        if (value != null) {
            return;
        }
        if (type == Boolean.class) {
            input.put(param, input.getText().newBooleanInputReader().withDefaultValue(((Boolean) defaultValue).booleanValue()).read(prompt));
            return;
        }
        if (type == String.class) {
            if (password) {
                input.put(param, input.getText().newStringInputReader().withInputMasking(true).read(prompt));
                return;
            }
            if (defaultValue != null) {
                input.put(param, input.getText().newStringInputReader().withDefaultValue((String) defaultValue).read(prompt));
                return;
            }
            input.put(param, input.getText().newStringInputReader().read(prompt));
            return;
        }

        if (type == Integer.class) {
            input.put(param, input.getText().newIntInputReader().withDefaultValue(((Integer) defaultValue).intValue()).read(prompt));
            return;
        }
        throw new IllegalStateException("Error while searching for user input to read the value of param \"" + param + "\", (Prompt: " + prompt
                    + "), expecting an user entry of type \"" + type.getName() + "\". Unsupported entry type. Supported entry types are Boolean and String and Integer.");
    }

    private static void loadInputInteractiveMode(final Input input) {
        String ignoreCertCheck = System.getProperty(Option.IGNORE_SSL_CERT_CHECK.getMapping());
        String ignoreHostCheck = System.getProperty(Option.IGNORE_SSL_HOST_CHECK.getMapping());

        if (ignoreCertCheck == null) {
            userInput(input, Option.IGNORE_SSL_CERT_CHECK, "Ignore SSL Certificate Verification?", false);
        } else {
            input.put(Option.IGNORE_SSL_CERT_CHECK, Boolean.valueOf(ignoreCertCheck));
        }
        if (ignoreHostCheck == null) {
            userInput(input, Option.IGNORE_SSL_HOST_CHECK, "Ignore SSL Hostname Verification?", false);
        } else {
            input.put(Option.IGNORE_SSL_HOST_CHECK, Boolean.valueOf(ignoreHostCheck));
        }

        // common properties (i.e. timeouts)
        readCommonProperties(input);

        //  +------------------------------
        //  |  vRealize Automation
        //  +------------------------------
        boolean hasVraPackages = !getFilesystemPackages(PackageType.VRA).isEmpty();
        if (hasVraPackages) {
            userInput(input, Option.VRA_IMPORT, "Import vRA packages?", true);
            if (input.anyTrue(Option.VRA_IMPORT)) {
                readVraProperties(input);
                readVraImportProperties(input);
                userInput(input, Option.VRA_DELETE_OLD_VERSIONS, "Clean up old vRA package versions?", true);
                userInput(input, Option.VRA_DELETE_LAST_VERSION, "Clean up last vRA package version?", true);
				userInput(input, Option.VRA_DELETE_INCLUDE_DEPENDENCIES, "Clean up vRA dependent packages as well?", true);
            }
        }
        //  +-------------------------------------
        //  |  vRealize Code Stream Automation (New Generation)
        //  +-------------------------------------
        boolean hasCsPackages = !getFilesystemPackages(PackageType.CS).isEmpty();
        if (hasCsPackages) {
            userInput(input, Option.CS_IMPORT, "Import Code Stream packages?", true);
        }

        //  +-------------------------------------
        //  |  vRealize Automation (New Generation)
        //  +-------------------------------------
        boolean hasVraNgPackages = !getFilesystemPackages(PackageType.VRANG).isEmpty();
        if (hasVraNgPackages) {
            userInput(input, Option.VRANG_IMPORT, "Import vRA8 packages?", true);
          
        }

        if (input.anyTrue(Option.VRANG_IMPORT, Option.CS_IMPORT)) {
            readVrangProperties(input);
        }
        if (input.anyTrue(Option.VRANG_IMPORT)) {
            readVrangImportProperties(input);
        }
        if (input.anyTrue(Option.CS_IMPORT)) {
            readCsImportProperties(input);
        }

        //  +-------------------------------------
        //  |  vRealize Orchestrator
        //  +-------------------------------------
		if (!getFilesystemPackages(PackageType.VRO).isEmpty()) {
            userInput(input, Option.VRO_IMPORT, "Import vRO packages?", true);
            if (input.anyTrue(Option.VRO_IMPORT)) {
				userInput(input, Option.VRO_ENABLE_BACKUP, "Backup existing vRO packages?", true);

                userInput(input, Option.VRO_EMBEDDED, "Is vRO Embedded (in vRA)?", hasVraNgPackages);
                if (input.anyTrue(Option.VRO_EMBEDDED)) {
                    readVroEmbeddedInVrangProperties(input, false);
                } else {
                    readVroProperties(input, hasVraNgPackages);
                }
                readVroImportProperties(input);
            } else {
                userInput(input, Option.VRO_DELETE_LAST_VERSION, "Clean up last vRO package version?", true);
            }
            userInput(input, Option.VRO_DELETE_OLD_VERSIONS, "Clean up old vRO package versions?", true);
        }
        userInput(input, Option.VRO_RUN_WORKFLOW, "Run vRO workflow?", true);
        if (input.allTrue(Option.VRO_RUN_WORKFLOW)) {
            if (input.anyTrue(Option.VRO_EMBEDDED)) {
                readVroEmbeddedInVrangProperties(input, false);
            } else {
                readVroProperties(input, hasVraNgPackages);
            }
            readVroWorkflowProperties(input);
        }
        //  +-------------------------------------
        //  |  vCloud Director (New Generation)
        //  +-------------------------------------
        if (!getFilesystemPackages(PackageType.VCDNG).isEmpty()) {
            userInput(input, Option.VCD_IMPORT, "Import vCD packages?", true);
        }
        if (input.anyTrue(Option.VCD_IMPORT)) {
            readVcdImportProperties(input);
        }
        //  +-------------------------------------
        //  |  vROps
        //  +-------------------------------------
        if (!getFilesystemPackages(PackageType.VROPS).isEmpty()) {
            userInput(input, Option.VROPS_IMPORT, "Run vROps import?", true);
        }
        if (input.anyTrue(Option.VROPS_IMPORT)) {
            readVropsImportProperties(input);
        }
        //  +-------------------------------------
        //  |  vRLI
        //  +-------------------------------------
        if (!getFilesystemPackages(PackageType.VRLI).isEmpty()) {
            userInput(input, Option.VRLI_IMPORT, "Run vRLI import?", true);
        }
        if (input.anyTrue(Option.VRLI_IMPORT)) {
            readVrliImportProperties(input);
        }
        //  +-------------------------------------
        //  |  vRLI
        //  +-------------------------------------
        if (input.anyTrue(Option.VCD_IMPORT, Option.VCD_DELETE_OLD_VERSIONS)) {
            readVcdProperties(input);
        }

        //  +-------------------------------------
        //  |  SSH
        //  +-------------------------------------
        if (!getFilesystemPackages(PackageType.BASIC).isEmpty()) {
            userInput(input, Option.SSH_IMPORT, "Run SSH import?", true);
        }
        if (input.anyTrue(Option.SSH_IMPORT)) {
            readSshImportProperties(input);
        }

        //  +-------------------------------------
        //  |  Store Properties for reusage
        //  +-------------------------------------
        boolean isStoring = true;
        String location = "environment.properties";
        File propsLocation = new File(location);
        propsLocation = new File(propsLocation.getAbsolutePath());
        isStoring = input.getText().newBooleanInputReader().withDefaultValue(true).read("Store Configuration?");
        if (isStoring) {
            if (propsLocation.exists() || propsLocation.isDirectory() || propsLocation.getParentFile() == null || !propsLocation.getParentFile().canWrite()) {
                input.getText().getTextTerminal().println("Cannot store in \"" + propsLocation.getAbsolutePath() + "\". Please select alternative location.");
                location = input.getText().newStringInputReader().withDefaultValue(location).read("Location");
            }
            input.store(location);
        }
        input.getText().getTextTerminal().println("");
        input.getText().getTextTerminal().println("");
    }

    private static void readCommonProperties(final Input input) {
        input.getText().getTextTerminal().println("HTTP Common Properties:");
        userInput(input, Option.CONNECTION_TIMEOUT, "  HTTP connection timeout", Configuration.DEFAULT_CONNECTION_TIMEOUT.toString());
        Validate.timeout(input.get(Option.CONNECTION_TIMEOUT), input.getText());
        userInput(input, Option.SOCKET_TIMEOUT, "  HTTP socket timeout", Configuration.DEFAULT_SOCKET_TIMEOUT.toString());
        Validate.timeout(input.get(Option.SOCKET_TIMEOUT), input.getText());
    }

    private static void setCommonProperties(final Input input) {
        System.setProperty(Option.IGNORE_SSL_CERT_CHECK.getMapping(), input.get(Option.IGNORE_SSL_CERT_CHECK));
        System.setProperty(Option.IGNORE_SSL_HOST_CHECK.getMapping(), input.get(Option.IGNORE_SSL_HOST_CHECK));
        if (input.get(Option.CONNECTION_TIMEOUT) != null) {
            System.setProperty(Option.CONNECTION_TIMEOUT.getMapping(), input.get(Option.CONNECTION_TIMEOUT));
        } else {
            System.setProperty(Option.CONNECTION_TIMEOUT.getMapping(), Configuration.DEFAULT_CONNECTION_TIMEOUT.toString());
        }
        if (input.get(Option.SOCKET_TIMEOUT) != null) {
            System.setProperty(Option.SOCKET_TIMEOUT.getMapping(), input.get(Option.SOCKET_TIMEOUT));
        } else {
            System.setProperty(Option.SOCKET_TIMEOUT.getMapping(), Configuration.DEFAULT_SOCKET_TIMEOUT.toString());
        }
    }

    private static void readVroProperties(final Input input, final boolean hasVraNgPackages) {
        input.getText().getTextTerminal().println("vRealize Orchestrator Configuration:");
        userInput(input, Option.VRO_SERVER, "  vRO FQDN", input.get(Option.VRA_SERVER));
        Validate.host(input.get(Option.VRO_SERVER), input.getText());
        userInput(input, Option.VRO_PORT, "  vRO Port", HTTPS_PORT);
        Validate.port(Integer.valueOf(input.get(Option.VRO_PORT)), input.getText());
        Validate.hostAndPort(input.get(Option.VRO_SERVER), Integer.valueOf(input.get(Option.VRO_PORT)), input.getText());
        if (!input.getText().newBooleanInputReader().withDefaultValue(true).read("  Is single tenant environment (Y/N)?")) {
            userInput(input, Option.VRO_TENANT, "  vRO Tenant", "vsphere.local");
        }
        userInput(input, Option.VRO_USERNAME, "  vRO Username[@Domain]", "administrator@vsphere.local");
        passInput(input, Option.VRO_PASSWORD, "  vRO Password");
        if (hasVraNgPackages) {
            input.put(Option.VRO_AUTH, "basic");
        }
        userInput(input, Option.VRO_AUTH, "  vRO Authorization Type (basic|vra)", "basic");
        if (input.get(Option.VRO_AUTH).equalsIgnoreCase("vra")) {
            userInput(input, Option.VRO_AUTHHOST, "    Authorization Host FQDN", input.get(Option.VRO_SERVER));
            userInput(input, Option.VRO_AUTHPORT, "    Authorization Port", input.get(Option.VRO_PORT));
        } else {
            Validate.vroauth(input.get(Option.VRO_SERVER), Integer.valueOf(input.get(Option.VRO_PORT)), input.get(Option.VRO_USERNAME), input.get(Option.VRO_PASSWORD),
                    input.getText());
        }
    }

    private static void readVroWorkflowProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Orchestrator Run Workflow:");
        userInput(input, Option.VRO_RUN_WORKFLOW_ID, "  Workflow ID:", (String) null);
        userInput(input, Option.VRO_RUN_WORKFLOW_INPUT_FILE_PATH, "  Workflow Input File Path");
        userInput(input, Option.VRO_RUN_WORKFLOW_OUTPUT_FILE, "  Workflow Output File Path");
        userInput(input, Option.VRO_RUN_WORKFLOW_ERR_FILE, "  Workflow Error File Path", Option.VRO_RUN_WORKFLOW_OUTPUT_FILE + ".err");
        userInput(input, Option.VRO_RUN_WORKFLOW_TIMEOUT, "  Workflow Execution Timeout", VRO_RUN_WORKFLOW_TIMEOUT);
    }

    private static void readVraProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Automation Configuration:");
        userInput(input, Option.VRA_SERVER, "  vRA FQDN:");
        Validate.host(input.get(Option.VRA_SERVER), input.getText());
        userInput(input, Option.VRA_PORT, "  vRA Port", HTTPS_PORT);
        Validate.port(Integer.valueOf(input.get(Option.VRA_PORT)), input.getText());
        Validate.hostAndPort(input.get(Option.VRA_SERVER), Integer.valueOf(input.get(Option.VRA_PORT)), input.getText());
        userInput(input, Option.VRA_TENANT, "  vRA Tenant", "vsphere.local");
        userInput(input, Option.VRA_USERNAME, "  vRA Username@Domain", "configurationadmin@vsphere.local");
        passInput(input, Option.VRA_PASSWORD, "  vRA Password");
    }
    private static void readVroEmbeddedInVrangProperties(final Input input, final boolean needCspHost) {
        input.getText().getTextTerminal().println("vRealize Automation NG Configuration:");
        userInput(input, Option.VRANG_SERVER, "  vRA FQDN:");
        Validate.host(input.get(Option.VRANG_SERVER), input.getText());

        userInput(input, Option.VRANG_PORT, "  vRA Port", HTTPS_PORT);
        Validate.port(Integer.valueOf(input.get(Option.VRANG_PORT)), input.getText());
        Validate.hostAndPort(input.get(Option.VRANG_SERVER), Integer.valueOf(input.get(Option.VRANG_PORT)), input.getText());

        if (needCspHost) {
            userInput(input, Option.VRANG_CSP_SERVER, "  Authentication CSP FQDN:", input.get(Option.VRANG_SERVER));
        }
        userInput(input, Option.VRANG_AUTH_WITH_REFRESH_TOKEN, "  Authenticate with refresh token?:", false);
        if (input.allTrue(Option.VRANG_AUTH_WITH_REFRESH_TOKEN)) {
            userInput(input, Option.VRANG_REFRESH_TOKEN, "    vRA Refresh Token");
            Validate.token(input.get(Option.VRANG_SERVER), Integer.valueOf(input.get(Option.VRANG_PORT)), input.get(Option.VRANG_REFRESH_TOKEN), input.getText());
        } else {
            userInput(input, Option.VRANG_USERNAME, "    vRA Username");
            passInput(input, Option.VRANG_PASSWORD, "    vRA Password");
            Validate.vrang(needCspHost ? input.get(Option.VRANG_CSP_SERVER) : input.get(Option.VRANG_SERVER), Integer.valueOf(input.get(Option.VRANG_PORT)),
                    input.get(Option.VRANG_USERNAME), input.get(Option.VRANG_PASSWORD), input.getText());
        }
    }
    private static void readVrangProperties(final Input input) {
        readVroEmbeddedInVrangProperties(input, true);
        userInput(input, Option.VRANG_PROJECT_NAME, "  Project name");
        Validate.ProjectAndOrg validated = Validate.project(input, input.get(Option.VRANG_PROJECT_NAME), input.getText());
        userInput(input, Option.VRANG_PROJECT_ID, "  Project ID (Optional if you supplied project name)", validated.projectId);
        userInput(input, Option.VRANG_ORGANIZATION_NAME, "  Organization Name (Optional if you supplied organization ID)", validated.org);
        userInput(input, Option.VRANG_ORGANIZATION_ID, "  Organization ID (Optional if you supplied organization Name)", validated.orgId);
		userInput(input, Option.VRANG_PROXY_REQUIRED, "  Use proxy server for vRA? (Optional)", false);
		if (input.allTrue(Option.VRANG_PROXY_REQUIRED)) {
			userInput(input, Option.VRANG_PROXY, "    VRA proxy server");
		}
    }

    private static void readVcdProperties(final Input input) {
        input.getText().getTextTerminal().println("vCloud Director Configuration:");
        userInput(input, Option.VCD_SERVER, "  vCD FQDN:");
        userInput(input, Option.VCD_PORT, "  vCD Port", HTTPS_PORT);
        userInput(input, Option.VCD_USERNAME, "  vCD Username@Org (Provider)", "administrator@system");
        passInput(input, Option.VCD_PASSWORD, "  vCD Password (Provider)");
    }

    private static void readVroImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Orchestrator Import Configuration:");
        userInput(input, Option.SKIP_VRO_IMPORT_OLD_VERSIONS, "  Skip import of Packages where Filesystem.version < Server.version?", true);
        input.put(Option.VRO_IMPORT_OLD_VERSIONS, input.get(Option.SKIP_VRO_IMPORT_OLD_VERSIONS).equals(Boolean.FALSE));
        userInput(input, Option.VRO_IMPORT_CONFIGURATION_ATTRIBUTE_VALUES, "  Import Configuration Elements Values?", false);
        userInput(input, Option.VRO_IMPORT_CONFIG_SECURE_STRING_ATTRIBUTE_VALUES, "  Import Configuration Elements Secure String Values?", false);
    }

    private static void readVraImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Automation Import Configuration:");
        userInput(input, Option.SKIP_VRA_IMPORT_OLD_VERSIONS, "  Skip Old Package Versions?", true);
        input.put(Option.VRA_IMPORT_OLD_VERSIONS, input.get(Option.SKIP_VRA_IMPORT_OLD_VERSIONS).equals(Boolean.FALSE));
        userInput(input, Option.VRA_IMPORT_OVERWRITE_MODE, "  Import Mode", "SKIP,OVERWRITE");
    }

    private static void readVrangImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Automation 8 Import Configuration:");
        userInput(input, Option.VRANG_IMPORT_OVERWRITE_MODE, "  vRA8 Import Mode", "SKIP,OVERWRITE");
        userInput(input, Option.VRANG_VRO_INTEGRATION_NAME, "  vRA8 integration name", "embedded-VRO");
        userInput(input, Option.VRANG_IMPORT_TIMEOUT, "  vRA8 Import timeout", ConfigurationVraNg.DEFAULT_IMPORT_TIMEOUT);
        userInput(input, Option.VRANG_DATA_COLLECTION_DELAY_SECONDS, "  vRA's vRO Data Collection delay in seconds.", VRANG_DATA_COLLECTION_DELAY_SECONDS);
    }

    private static void readCsImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Code Stream Import Configuration:");
        userInput(input, Option.VRANG_CLOUD_PROXY_NAME, "  Cloud proxy to be used by Code Stream endpoints");
    }

    private static void readVcdImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vCloud Director Import Configuration:");
        userInput(input, Option.SKIP_VCD_IMPORT_OLD_VERSIONS, "  vCD Skip Old Package Versions?", true);
        input.put(Option.VCD_IMPORT_OLD_VERSIONS, input.get(Option.SKIP_VCD_IMPORT_OLD_VERSIONS).equals(Boolean.FALSE));
        userInput(input, Option.VCD_IMPORT_OVERWRITE_MODE, "  vCD Import Mode", "SKIP,OVERWRITE");
    }

    private static void readVropsImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Operations REST Endpoint Configuration:");
        userInput(input, Option.VROPS_HTTP_HOST, "  vROps REST FQDN");
        userInput(input, Option.VROPS_HTTP_PORT, "  vROps REST Port", HTTPS_PORT);
        userInput(input, Option.VROPS_REST_USER, "  vROps REST Username");
        passInput(input, Option.VROPS_REST_PASSWORD, "  vROps REST Password");
        userInput(input, Option.VROPS_REST_AUTH_SOURCE, "  vROps REST Auth Source", "local");
        userInput(input, Option.VROPS_REST_AUTH_PROVIDER, "  vROps REST Auth Provider", "BASIC");

        input.getText().getTextTerminal().println("vRealize Operations SSH Endpoint Configuration:");
        userInput(input, Option.VROPS_SSH_PORT, "  vROps SSH Port", SSH_PORT);
        userInput(input, Option.VROPS_SSH_USER, "  vROps SSH Username");
        passInput(input, Option.VROPS_SSH_PASSWORD, "  vROps SSH Password");
        userInput(input, Option.VROPS_DASHBOARD_USER, "  Dashboard username");
    }

    private static void readVrliImportProperties(final Input input) {
        input.getText().getTextTerminal().println("vRealize Log Insight import configuration:");
        userInput(input, Option.VRLI_SERVER, "  vRLI FQDN");
        userInput(input, Option.VRLI_PORT, "  vRLI Port", VRLI_PORT);
        userInput(input, Option.VRLI_USERNAME, "  vRLI Username");
        passInput(input, Option.VRLI_PASSWORD, "  vRLI Password");
        userInput(input, Option.VRLI_PROVIDER, "  vRLI Provider", "Local");
        userInput(input, Option.VRLI_PACKAGE_IMPORT_OVERWRITE_MODE, "  vRLI Package Overwrite Mode", "OVERWRITE");

        // vROps integration for vRLI settings
        userInput(input, Option.VRLI_VROPS_INTEGRATION_HOST, "  vROps integration FQDN");
        userInput(input, Option.VRLI_VROPS_INTEGRATION_PORT, "  vROps integration port", HTTPS_PORT);
        userInput(input, Option.VRLI_VROPS_INTEGRATION_USER, "  vROps integration username");
        passInput(input, Option.VRLI_VROPS_INTEGRATION_PASSWORD, "  vROps integration password");
        userInput(input, Option.VRLI_VROPS_INTEGRATION_AUTH_SOURCE, "  vROps integration auth source", "local");
    }

    private static void readSshImportProperties(final Input input) {
        input.getText().getTextTerminal().println("SSH import configuration:");
        userInput(input, Option.SSH_SERVER, "  SSH Server FQDN");
        userInput(input, Option.SSH_PORT, "  SSH Port", SSH_PORT);
        userInput(input, Option.SSH_USERNAME, "  SSH Username");
        passInput(input, Option.SSH_PASSWORD, "  SSH Password");
        userInput(input, Option.SSH_DIRECTORY, "  SSH Working Directory");
    }

    private static List<Package> getFilesystemPackages(final PackageType type) {
    	// We use app.repo instead of user.dir as result it returns the correct path to application directory
    	// even in the case when we run the installer outside the application directory
    	// https://www.mojohaus.org/appassembler/appassembler-maven-plugin/usage-script.html
    	File repoDir = new File(System.getProperty("app.repo"));
        File workingDir = repoDir.getParentFile();
        if (workingDir.getName().equals("bin")) {
            workingDir = workingDir.getParentFile();
        }

        File containerDir = new File(workingDir, type.getPackageContainer());
        if (!containerDir.exists()) {
            return new ArrayList<>();
        }
        if (!containerDir.isDirectory()) {
            throw new RuntimeException(String.format("Cannot find any packages at %s .", containerDir.getAbsolutePath()));
        }

        List<File> packages = new ArrayList<>();
        packages.addAll(FileUtils.listFiles(containerDir, new String[] { type.getPackageExtention() }, true));

        return packages.stream().map(file -> PackageFactory.getInstance(type, file)).collect(Collectors.toList());
    }

    private static void runWorkflow(final Input input) throws ConfigurationException {
        input.getText().getTextTerminal().println("Executing vRealize Orchestrator Workflow ...");

        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
        String wfID = input.get(Option.VRO_RUN_WORKFLOW_ID);
        String wfInputFilePath = input.get(Option.VRO_RUN_WORKFLOW_INPUT_FILE_PATH);
        String wfOutputFilePath = input.get(Option.VRO_RUN_WORKFLOW_OUTPUT_FILE);
        String wfErrFilePath = input.get(Option.VRO_RUN_WORKFLOW_ERR_FILE);
        wfErrFilePath = StringUtils.isEmpty(wfErrFilePath) && !StringUtils.isEmpty(wfOutputFilePath) ? wfOutputFilePath + ".err" : "workflow.err";        
        String wfTimeout = input.get(Option.VRO_RUN_WORKFLOW_TIMEOUT);

        Properties wfInput = new Properties();
        try {
            String wfInputJSON = new String(Files.readAllBytes(Paths.get(wfInputFilePath)));
            for (Map.Entry<String, JsonElement> entry : gson.fromJson(wfInputJSON, JsonObject.class).entrySet()) {
                wfInput.put(entry.getKey(), StringEscapeUtils.escapeJava(gson.toJson(entry.getValue())));
            }
            List<String> prefixes = new ArrayList<>();
            if (input.allTrue(Option.VRO_EMBEDDED)) {
                prefixes.add(ConfigurationPrefix.VRO.getValue());
                prefixes.add(ConfigurationPrefix.VRANG.getValue());
            } else {
                prefixes.add(ConfigurationPrefix.VRO.getValue());
            }
            RestClientVro client = RestClientFactory.getClientVro(ConfigurationVro.fromProperties(input.getMappings(prefixes.toArray(new String[0]))));
            WorkflowExecution workflowExecutionResult = new VroWorkflowExecutor(client).executeWorkflow(wfID, wfInput, Integer.parseInt(wfTimeout));

            JsonObject wfOutputJson = new JsonObject();
            for (String key : workflowExecutionResult.getOutput().stringPropertyNames()) {
                String value = StringEscapeUtils.unescapeJson(workflowExecutionResult.getOutput().getProperty(key));
                wfOutputJson.add(key, gson.fromJson(value, JsonObject.class));
            }
            // write the workflow output
            Files.write(Paths.get(wfOutputFilePath), gson.toJson(wfOutputJson).getBytes(), StandardOpenOption.CREATE);
            // write the workflow error in a workflow error file (if any)
            if (!StringUtils.isEmpty(workflowExecutionResult.getError())) {
                Files.write(Paths.get(wfErrFilePath), workflowExecutionResult.getError().getBytes(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to perform file operation: " + e.getClass().getName() + " : " + e.getLocalizedMessage(), e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Unable to parse input / output file data: " + e.getClass().getName() + " : " + e.getLocalizedMessage(), e);
        } catch (WorkflowExecutionException e) {
            try {
                Files.write(Paths.get(wfErrFilePath), e.getMessage().getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e1) {
                throw new RuntimeException("Unable to perform file operation: " + e.getClass().getName() + " : " + e.getLocalizedMessage(), e);
            }
            throw new RuntimeException("Workflow execution failed: " + e.getClass().getName() + " : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("General error during workflow execution: " + e.getClass().getName() + " : " + e.getLocalizedMessage(), e);
        }
    }
}
