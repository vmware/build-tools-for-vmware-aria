/*
 * #%L
 * vcd-ng-package-maven-plugin
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.vmware.pscoe.iac.artifact.model.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class VcdNgPackageMojo extends AbstractVroMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		
		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.VCDNG.getPackageExtention());
		getLog().info("Target vcd-ng package file " + pkgFile.getAbsolutePath());
		
		Path zipBundle = Paths.get(directory.getAbsolutePath(), "bundle", "plugin.zip");

		String npmExec = SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
		
		ArrayList<String> nodeBuildArgs = new ArrayList<>();
		nodeBuildArgs.add(npmExec);
		nodeBuildArgs.add("run");
		nodeBuildArgs.add("build");
		if (!getLog().isDebugEnabled()) {
			nodeBuildArgs.add("--silent");
		}

		new ProcessExecutor()
			.name("Packaging project")
			.directory(project.getBasedir())
			.command(nodeBuildArgs)
			.execute(getLog());

		if(!java.nio.file.Files.exists(zipBundle)) {
			throw new RuntimeException(String.format(
				"Unable to find packaged files %s. Please check npm output with -X option.", 
				zipBundle.toString()));
		}
		
		try {
			Files.move(zipBundle, pkgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not package project.", e);
		} 
	}

}
