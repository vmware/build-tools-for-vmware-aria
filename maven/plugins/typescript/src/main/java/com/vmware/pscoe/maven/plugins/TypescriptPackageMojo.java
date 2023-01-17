package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * o11n-typescript-package-maven-plugin
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
