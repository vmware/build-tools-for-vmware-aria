/*
 * #%L
 * vcfa-all-apps-package-maven-plugin
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
package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "clean")
public class CleanMojo extends AbstractIacMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	@Override
	protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
		// Ensuring safety flags are properly populated if needed during destination
		// deletion streams
		props.setProperty(Configuration.IMPORT_OLD_VERSIONS, "true");
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("vcfa-all-app-clean");
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Could not create a temp directory for clean operational execution context");
		}

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		File pkgFile = tempDir
				.resolve(pkgInfoProvider.getPackageName() + "." + PackageType.VCFA_ALL_APPS.getPackageExtension())
				.toFile();
		Package pkg = PackageFactory.getInstance(PackageType.VCFA_ALL_APPS, pkgFile);

		try {
			getLog().info("Initializing execution wipe routine targeting the remote system environment...");

			// 1. Fetch the configuration and rest client for the VCFA engine
			com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto config = getConfigurationForVcfAuto();
			com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto restClient = com.vmware.pscoe.iac.artifact.common.rest.RestClientFactory
					.getClientVcfAuto(config);

			// 2. Load the project's content.yaml file directly
			java.io.File contentYamlFile = new java.io.File(project.getBasedir(), "content.yaml");
			if (!contentYamlFile.exists()) {
				throw new MojoExecutionException(
						"Cannot execute clean command: Missing required 'content.yaml' manifest descriptor.");
			}

			com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor pkgDescriptor = com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor
					.getInstance(contentYamlFile);

			if (dryrun) {
				getLog().info(
						"Dry run mode active - scanning deletion order manifests without executing remote calls.");
			}

			// 3. Initialize the Type Store Factory using the verified configuration context
			com.vmware.pscoe.iac.artifact.vcf.automation.store.VcfaTypeStoreFactory storeFactory = com.vmware.pscoe.iac.artifact.vcf.automation.store.VcfaTypeStoreFactory
					.withConfig(restClient, pkg, config, pkgDescriptor);

			// 4. Iterate through the platform's native deletion order layout sequentially
			for (com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType type : com.vmware.pscoe.iac.artifact.vcf.automation.store.VcfaTypeStoreFactory
					.getDeleteOrder()) {

				getLog().info("Currently evaluating deletion routines for component category: " + type.getTypeValue());

				if (!dryrun) {
					// Fetch the public store module for the target type and call its public cleanup
					// interface
					storeFactory.getStoreForType(type).deleteContent();
				}
			}

			getLog().info("Successfully cleaned tracked infrastructure references from target endpoint environment.");
		} catch (ConfigurationException e) {
			getLog().error(e);
			String message = String.format(
					"Error executing clean wipe action on VCFA All App destination environment: %s", e.getMessage());
			throw new MojoExecutionException(e, message, message);
		}
	}
}