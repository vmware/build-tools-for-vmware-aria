package com.vmware.pscoe.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class TypescriptPackageMojo extends AbstractPackageMojo {
	@Override
	protected void executeVroPkg(File packageFile) throws MojoExecutionException, MojoFailureException {
		try {
			// Get all needed paths
			String projectRoot = project.getBasedir().toPath().toString();
			Path jsRootPath = Paths.get(projectRoot, TypescriptConstants.OUT_JS_ROOT_PATH);
			Path xmlRootPath = Paths.get(projectRoot, TypescriptConstants.OUT_XML_ROOT_PATH);
			// Create XML folder in case there are no XML vRO objects
			if (!Files.exists(xmlRootPath)) {
				Files.createDirectories(xmlRootPath);
			}
			// vRO Actions exist
			if (Files.exists(jsRootPath)) {
				// JS actions -> XML structure
				this.runVroPkg("js", jsRootPath.toString(), "tree", xmlRootPath.toString());
			}
			// XML and/or Actions -> vRO package
			this.runVroPkg("tree", xmlRootPath.toString(), "flat", packageFile.getParent());
		} catch (IOException e) {
			throw new MojoExecutionException("IO error: ", e);
		}
	}
}
