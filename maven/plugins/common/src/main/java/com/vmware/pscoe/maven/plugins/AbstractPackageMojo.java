package com.vmware.pscoe.maven.plugins;

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
