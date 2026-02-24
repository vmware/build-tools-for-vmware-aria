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
package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractVroMojo {

	/**
	 * The function is extended with support for multiple ABX bundles in a single
	 * project The ABX package consists of package.json and bundle.zip files They
	 * are taken from project_root/package.json and project_root/dist/bundle.zip The
	 * format does not support multiple actions
	 *
	 * This function is enhanced to support alternative folder structure:
	 * project_root/ dist/ action_name/ package.json dist/ bundle.zip The program
	 * searches for sub-directories of project_root/dist that contain package.json
	 * and dist/ If found, every such directory is treated as separate artifact
	 * source. If none found, project_root is used as artifact source.
	 *
	 * maven does not support multiple artifacts. To work around this, all artifacts
	 * created except the last one are added to the project as dependencies.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws MojoExecutionException {
		String packageName = project.getName();
		List<File> dirList = new ArrayList<>();
		List<String> packageNameList = new ArrayList<>();
		List<Artifact> depArtifacts = new ArrayList<>();
		File distDir = new File(project.getBasedir(), "dist");

		// Prepare list of artifact root folders
		getBundlesList(distDir, packageName, dirList, packageNameList);

		int index = 0;
		for (File dir : dirList) {
			if (index > 0) {
				Artifact depArtifact = ArtifactUtils.copyArtifact(project.getArtifact());
				depArtifact.setScope("system");
				depArtifacts.add(depArtifact);
			}
			String newPackageName = packageNameList.get(index);
			project.getArtifact().setArtifactId(newPackageName);
			project.setName(newPackageName);
			getLog().info("ABX action name '" + newPackageName + "'");

			MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
			File pkgFile = new File(directory,
					pkgInfoProvider.getPackageName() + "." + PackageType.ABX.getPackageExtention());
			Package pkg = PackageFactory.getInstance(PackageType.ABX, pkgFile);
			try {
				this.preparePackageFile(pkg, dir, new File(pkgFile.getAbsolutePath()));
				getLog().info("Target ABX package file '" + project.getArtifact().getFile().getAbsolutePath() + "'");
			} catch (MojoExecutionException e) {
				throw e;
			}

			index++;
		}

		// Add dependency artifacts to the project
		for (Artifact artifact : depArtifacts) {
			project.getDependencyArtifacts().add(artifact);
		}
	}

	/**
	 * Method checks for sub-directories of a given directory that contain a file
	 * package.json and a sub-directory dist If no results are found, the list is
	 * populated with input data
	 */
	private void getBundlesList(File dir, String packageName, List<File> dirList, List<String> packageNameList) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				File packageJson = new File(file, "package.json");
				if (packageJson.exists() && !packageJson.isDirectory()) {
					// multi-artifact ABX project
					File distSubfolder = new File(file, "dist");
					if (distSubfolder.exists() && distSubfolder.isDirectory()) {
						dirList.add(file);
						packageNameList.add(packageName + "_" + file.getName());
					}
				}
			}
		}
		if (dirList.isEmpty()) {
			dirList.add(dir);
			packageNameList.add(packageName);
		}
	}

	private void preparePackageFile(Package pkg, File dir, File targetPackageFile) throws MojoExecutionException {
		try {
			PackageManager mgr = new PackageManager(pkg);
			// add everything from the dist directory
			File distFile = new File(dir, "dist");
			if (distFile.exists()) {
				// multi-artifact ABX project
				mgr.pack(distFile);
			} else {
				// single artifact ABX project
				mgr.pack(new File(dir, "."));
			}
			// add package.json
			File packageJsonFile = new File(dir, "package.json");
			mgr.addTextFileToExistingZip(packageJsonFile, Paths.get("."));
			project.getArtifact().setFile(targetPackageFile);
		} catch (IOException e) {
			String message = String.format("Error creating ABX bundle: %s", e.getMessage());
			throw new MojoExecutionException(e, message, message);
		}
	}
}
