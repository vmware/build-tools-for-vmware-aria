package com.vmware.pscoe.maven.plugins;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import java.io.File;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class JsBasedActionsPackageMojo extends AbstractPackageMojo {

	@Override
	protected void executeVroPkg(File packageFile) throws MojoExecutionException, MojoFailureException {
		this.runVroPkg("js", project.getBasedir().toPath().toString(), "flat", packageFile.getParent());
	}
}
