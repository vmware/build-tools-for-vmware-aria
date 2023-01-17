package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * ssh-maven-plugin
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
import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.*;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.PackageManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

@Mojo(name = "pull")
public class SshPullMojo extends AbstractIacMojo {
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
		File pkgFile = new File(Files.createTempDir(), pkgInfoProvider.getPackageName() + "." + PackageType.BASIC.getPackageExtention()); 
		com.vmware.pscoe.iac.artifact.model.Package pkg = PackageFactory.getInstance(PackageType.BASIC, pkgFile);
		try {
			PackageStore store = PackageStoreFactory.getInstance(getConfigurationForSsh());
			store.exportPackage(pkg, new File(project.getBasedir(), "content.yaml"), dryrun);
            PackageManager.copyContents(Paths.get(pkg.getFilesystemPath(), "content").toFile(), 
            		new File(project.getBasedir(), "src"));
		} catch (ConfigurationException | IOException e) {
			getLog().error(e);
			throw new MojoExecutionException(e, "Error pulling SSH package", "Error pulling SSH package");
		}
	}
}
