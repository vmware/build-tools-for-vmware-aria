package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * abx-package-maven-plugin
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

import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Mojo(name = "pull")
public class PullMojo extends AbstractIacMojo {
	/**
	 * The project that is built with the tools.
	 */
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	/**
	 * Dry run or not. Default value is false.
	 */
	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	/**
	 * Set the importOldVersions property.
	 *
	 * @param type  The package type.
	 * @param props The properties.
	 */
	@Override
	protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
		props.setProperty(Configuration.IMPORT_OLD_VERSIONS, "true");
	}

	/**
	 * Implement the pull operation for abx.
	 *
	 * @throws MojoExecutionException If the maven plugin execution fails.
	 * @throws MojoFailureException   If the Mojo (maven plugin) fails.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("abx-pull");
		} catch (IOException e) {
			throw new MojoExecutionException("Could not create a temp directory");
		}

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		File pkgFile = tempDir.resolve(pkgInfoProvider.getPackageName() + "." + PackageType.ABX.getPackageExtention()).toFile();
		Package pkg = PackageFactory.getInstance(PackageType.ABX, pkgFile);

		try {
			PackageStore<?> store = PackageStoreFactory.getInstance(getConfigurationForAbx());
			store.exportPackage(pkg, project.getBasedir(), dryrun);
			PackageManager.copyContents(new File(pkg.getFilesystemPath()), new File(pkgInfoProvider.getSourceDirectory().getAbsolutePath()));
		} catch (ConfigurationException | IOException e) {
			getLog().error(e);
			String message = String.format("Error pulling vRA ng package : %s", e.getMessage());
			throw new MojoExecutionException(e, message, message);
		}

	}
}
