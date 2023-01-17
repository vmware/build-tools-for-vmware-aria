package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * abx-package-maven-plugin
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

import com.google.common.io.Files;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Mojo(name = "pull")
public class PullMojo extends AbstractIacMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	@Override
	protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
		props.setProperty(Configuration.IMPORT_OLD_VERSIONS, "true");
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		File pkgFile = new File(Files.createTempDir(), pkgInfoProvider.getPackageName() + "." + PackageType.ABX.getPackageExtention());
		com.vmware.pscoe.iac.artifact.model.Package pkg = PackageFactory.getInstance(PackageType.ABX, pkgFile);

		try {
			PackageStore<?> store = PackageStoreFactory.getInstance(getConfigurationForAbx());
			store.exportPackage(pkg, project.getBasedir(), dryrun);
			PackageManager.copyContents(new File(pkg.getFilesystemPath()), new File(pkgInfoProvider.getSourceDirectory().getAbsolutePath()));
		} catch (ConfigurationException | IOException e) {
			getLog().error(e);
			String message = String.format("Error pulling vRA ng package : %s", e.getMessage());
			throw new MojoExecutionException(e, message, message);
		}

	}
}
