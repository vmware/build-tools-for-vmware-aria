/*
 * #%L
 * o11n-actions-package-maven-plugin
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
package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.o11n.project.CleanProjectTree;
import com.vmware.pscoe.o11n.project.JsBasedActionsProjectTree;
import com.vmware.pscoe.o11n.project.ProjectTree;

@Mojo(name = "pull")
public class JsBasedActionsPullMojo extends AbstractIacMojo {

	@Parameter(property = "packageName")
	private String packageName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		final Path tempDir;
		try {
			tempDir = Files.createTempDirectory("vro-js-pull");
		} catch (IOException e) {
			throw new MojoExecutionException("Could not create a temp directory");
		}
		final PackageInfoProvider packageInfoProvider = new MavenProjectPackageInfoProvider(project);
		final String pkgName = StringUtils.isBlank(packageName) ? packageInfoProvider.getPackageName() : packageName;
		final File packageFile = tempDir.resolve(pkgName + "." + PackageType.VRO.getPackageExtention()).toFile();
		final Package pkg = PackageFactory.getInstance(PackageType.VRO, packageFile);
		// Get vRO package via REST API
		try {
			final PackageStore<?> packageStore = PackageStoreFactory.getInstance(getConfigurationForVro());
			packageStore.exportPackage(pkg, false);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException("Could not process the configuration", e);
		}
		// Delete all local files
		final ProjectTree projectTree = new JsBasedActionsProjectTree(project.getBasedir().toPath(), true);
		try {
			projectTree.walk(new CleanProjectTree());
		} catch (Exception e) {
			throw new MojoExecutionException("Could not clean the project tree", e);
		}

		// Convert flat (.pakcage file) to JS tree structure
		String projectRoot = project.getBasedir().toPath().toString();
		this.runVroPkg("flat", packageFile.getAbsolutePath(), "js", projectRoot);
	}
}
