/*
 * #%L
 * vrops-package-maven-plugin
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

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.aria.operations.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "pull")
public class PullMojo extends AbstractIacMojo {
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	/**
	 * Whether to include the out-of-the-box content in the content export, e.g.
	 * to pull built-in views or dashboards. Only the user created content is
	 * exported by default.
	 */
	@Parameter(required = false, property = "includeOutOfTheBoxContent", defaultValue = "false")
	private boolean includeOutOfTheBoxContent;

	@Override
	protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
		props.setProperty(Configuration.IMPORT_OLD_VERSIONS, "true");
		props.setProperty(ConfigurationVrops.VROPS_INCLUDE_OUT_OF_THE_BOX_CONTENT,
				Boolean.toString(includeOutOfTheBoxContent));
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("vROps Pull Plugin: Executing in Project Base: \"" + project.getBasedir() + "\"");
		super.execute();

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		Package pkg = PackageFactory.getInstance(PackageType.VROPS, pkgInfoProvider.getSourceDirectory());
		try {
			PackageStore<?> store = PackageStoreFactory.getInstance(getConfigurationForVrops());
			store.exportPackage(pkg, new File(project.getBasedir(), "content.yaml"), dryrun);
		} catch (ConfigurationException e) {
			getLog().error(e);
			throw new MojoExecutionException(e, "Error pulling vROps package", "Error pulling vROps package");
		}
	}
}
