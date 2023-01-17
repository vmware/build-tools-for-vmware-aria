package com.vmware.pscoe.maven.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class XmlBasedProjectPackageMojo extends AbstractPackageMojo {

	@Override
	protected void executeVroPkg(File packageFile) throws MojoExecutionException, MojoFailureException {
		this.runVroPkg("tree", project.getBasedir().toPath().toString(), "flat", packageFile.getParent());
	}

}
