/*
 * #%L
 * o11n-actions-package-maven-plugin
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
