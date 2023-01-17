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

import com.google.common.io.Files;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractPackageMojo extends AbstractVroPkgMojo {

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	protected File directory;

	@Parameter(property = "packageSuffix", defaultValue = "")
	protected String packageSuffix;

	protected MavenProjectPackageInfoProvider getPackageInfoProvider() {
		return new MavenProjectPackageInfoProvider(project, packageSuffix);
	}

	abstract void executeVroPkg(File packageFile) throws MojoExecutionException, MojoFailureException;


	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Project basedir: " + project.getBasedir());

		PackageInfoProvider packageInfoProvider = this.getPackageInfoProvider();
		getLog().info("Package name '" + packageInfoProvider.getPackageName());

		String packageName = packageInfoProvider
				.getPackageName();

		String targetFilename = packageName + "." + PackageType.VRO.getPackageExtention();
		Path tempFolder = Paths.get(directory.getAbsolutePath(), "vropkg");
		File packageFile = new File(tempFolder.toFile(), targetFilename);
		try {
			Files.createParentDirs(packageFile);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not create target directory.", e);
		}

		getLog().info("Building vRO package '" + packageFile.getName() + "' to: " + directory.toString());
		this.executeVroPkg(packageFile);
		File targetFile = new File(directory, targetFilename);
		try {
			Files.move(packageFile, targetFile);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Fail to move artefact from: " + packageFile.toString() + " to: " + targetFile.toString(), e);
		}
		project.getArtifact().setFile(targetFile);
	}
}
