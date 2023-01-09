package com.vmware.pscoe.maven.plugins;

import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File directory;

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Override
    public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		
		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory, pkgInfoProvider.getPackageName() + "." + PackageType.VRANG.getPackageExtention());
		getLog().info("Target vRA NG package file " + pkgFile.getAbsolutePath());
		
        Package pkg = PackageFactory.getInstance(PackageType.VRANG, pkgFile);
		try {

			getLog().info("Packaging vRA NG bundle from: " + pkgInfoProvider.getSourceDirectory().getAbsolutePath());
			PackageManager mgr = new PackageManager(pkg);
			mgr.pack(pkgInfoProvider.getSourceDirectory());

			File contentFile = new File(project.getBasedir().getPath() + File.separator + "content.yaml");
			if (contentFile.exists()) {
				getLog().debug("Adding to package: "+ contentFile.getName());
				mgr.addTextFileToExistingZip(contentFile, Paths.get("."));
			}

	        project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
            String message = String.format("Error creating vRA NG bundle: %s", e.getMessage());
            throw new MojoExecutionException(e, message, message);
		}
	}

}
