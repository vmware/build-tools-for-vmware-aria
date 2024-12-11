/*
 * #%L
 * vrealize-package-maven-plugin
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

import java.util.Arrays;

import com.vmware.pscoe.iac.artifact.AbxReleaseManager;
import com.vmware.pscoe.iac.artifact.VraNgReleaseManager;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.aria.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.rest.RestClientFactory;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;


@Mojo(name = "release", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class ReleaseMojo extends AbstractIacMojo {
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	@Parameter(required = false, property = "vrang.contentType", defaultValue = "all")
	private String contentType;

	@Parameter(required = false, property = "vrang.contentNames")
	private String[] contentNames;

	@Parameter(required = true, property = "vrang.version")
	private String version;

	@Parameter(required = false, property = "vrang.releaseIfNotUpdated", defaultValue = "false")
	private boolean releaseIfNotUpdated;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		final String artifactType = project.getArtifact().getType();
		final PackageType packageType = PackageType.fromExtension(artifactType);

		try {

			if (PackageType.VRANG == packageType) {

				RestClientVraNg restClient = RestClientFactory.getClientVraNg((ConfigurationVraNg) getConfigurationForType(packageType).get());
				VraNgReleaseManager releaseManager = new VraNgReleaseManager(restClient);
				releaseManager.releaseContent(this.contentType, Arrays.asList(this.contentNames), this.version, this.releaseIfNotUpdated);

			} else if (PackageType.ABX == packageType) {

				RestClientVraNg restClient = RestClientFactory.getClientVraNg((ConfigurationVraNg) getConfigurationForType(packageType).get());
				AbxReleaseManager releaseManager = new AbxReleaseManager(restClient);
				releaseManager.releaseContent(this.version, project.getBasedir());

			} else {
				getLog().warn(String.format("Skipping release because of unsupported artifact type '%s'", artifactType));
			}

		} catch (ConfigurationException e) {
			getLog().error(e);
			throw new MojoExecutionException(e, "Error processing configuration", "Error processing configuration");
		}
	}

}
