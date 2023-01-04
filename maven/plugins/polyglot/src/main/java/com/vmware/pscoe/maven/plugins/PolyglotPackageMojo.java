package com.vmware.pscoe.maven.plugins;

import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Paths;


@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PolyglotPackageMojo extends AbstractVroPkgMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("basedir " + project.getBasedir());

        String projectRoot = project.getBasedir().toPath().toString();

        String target = Paths.get(projectRoot,"target").toString();
        String distDir = Paths.get(projectRoot,"dist", "vro").toString();
        runVroPkg("tree", distDir, "flat", target);

		MavenProjectPackageInfoProvider pkgProvider = new MavenProjectPackageInfoProvider(project);
		String targetFilename = pkgProvider.getPackageName() + "." + PackageType.VRO.getPackageExtention();
		File targetFile = new File(target, targetFilename);
		getLog().info("Setting artifact to " + targetFile.getAbsolutePath());
        project.getArtifact().setFile(targetFile); //IAC-611 artifacts should be updated within plugin implementation
    }
}
