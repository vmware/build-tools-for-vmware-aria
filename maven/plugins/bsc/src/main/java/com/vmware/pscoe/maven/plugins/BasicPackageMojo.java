package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * bsc-package-maven-plugin
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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.io.Files;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.basic.BasicPackageDescriptor;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class BasicPackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File directory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		
		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.BASIC.getPackageExtention());
		getLog().info("Target package file: " + pkgFile.getAbsolutePath());
		File srcDir = new File(project.getBasedir(), "src");
		File contentsDir = new File(directory, "contents");
		File tempPkgFile = new File(contentsDir, pkgInfoProvider.getPackageName() + "." + PackageType.BASIC.getPackageExtention());

		com.vmware.pscoe.iac.artifact.model.Package tmpPkg = PackageFactory.getInstance(PackageType.BASIC, tempPkgFile);

        BasicPackageDescriptor descriptor = BasicPackageDescriptor.getInstance(new File(project.getBasedir(), "content.yaml"));
        List<File> files = descriptor.getContent().stream().map(file -> (Paths.get(contentsDir.getAbsolutePath(), file).toFile())).collect(Collectors.toList());
        
		try {
			getLog().info("Packaging basic bundle from: " + srcDir.getAbsolutePath());
			PackageManager.copyContents(srcDir, contentsDir);
			new PackageManager(tmpPkg).addToExistingZip(files);
			Files.copy(tempPkgFile, pkgFile);
	        project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
			throw new MojoExecutionException(e, "Error creating basic bundle", "Error creating basic package");
		}
	}

}
