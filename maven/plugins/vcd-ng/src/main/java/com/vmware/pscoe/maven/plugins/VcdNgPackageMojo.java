package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.model.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class VcdNgPackageMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File directory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

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
