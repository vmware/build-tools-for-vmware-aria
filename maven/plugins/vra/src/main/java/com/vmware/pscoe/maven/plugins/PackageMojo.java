package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File directory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		
		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.VRA.getPackageExtention());
		getLog().info("Target vRA package file " + pkgFile.getAbsolutePath());
		
		com.vmware.pscoe.iac.artifact.model.Package pkg = PackageFactory.getInstance(PackageType.VRA,
				pkgFile);
		try {
			getLog().info("Packaging vRA bundle from: " + pkgInfoProvider.getSourceDirectory().getAbsolutePath());
			new PackageManager(pkg).pack(pkgInfoProvider.getSourceDirectory());
	        project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
			throw new MojoExecutionException(e, "Error creating vRA bundle", "Error creating vRA bundle");
		}
	}

}
