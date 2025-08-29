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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "push", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class PushMojo extends AbstractIacMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = false, property = "dryrun", defaultValue = "false")
	private boolean dryrun;

	@Parameter(required = true, property = "includeDependencies", defaultValue = "true")
	private boolean includeDependencies;

	@Parameter(required = false, property = "files", defaultValue = "")
	private List<String> filesChanged;

	private static Package packageFromArtifact(Artifact artifact) {
		return PackageFactory.getInstance(PackageType.fromType(artifact.getType()), artifact.getFile(),
				new MavenArtifactPackageInfoProvider(artifact).getPackageName());
	}

	private void importArtifacts(Collection<Artifact> allArtifacts) throws MojoExecutionException {
		Map<String, List<Artifact>> artifactsByType = allArtifacts.stream()
				.collect(Collectors.groupingBy(Artifact::getType));

		for (Map.Entry<String, List<Artifact>> type : artifactsByType.entrySet()) {
			PackageType pkgType = PackageType.fromType(type.getKey());
			if (pkgType == null) {
				continue;
			}
			try {
				List<Package> packages = artifactsByType.get(type.getKey()).stream().map(PushMojo::packageFromArtifact)
						.collect(Collectors.toList());
				PackageStore<?> store = getConfigurationForType(PackageType.fromType(type.getKey()))
						.flatMap(configuration -> Optional.of(PackageStoreFactory.getInstance(configuration)))
						.orElseThrow(() -> new ConfigurationException(
								"Unable to find PackageStore based on configuration. Make sure there is configuration for type: "
										+ pkgType.name()));

				boolean mergePackages = filesChanged.size() != 0; // it means that only a few files was selected to
																	// create the package
				this.getLog().info("Merge Package vrealize PushMojo: " + mergePackages);
				store.importAllPackages(packages, dryrun, mergePackages);
			} catch (ConfigurationException e) {
				getLog().error(e);
				String message = String.format("Error processing configuration : %s", e.getMessage());
				throw new MojoExecutionException(e, message, message);
			}
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		this.printFilesSelected();
		final String artifactType = project.getArtifact().getType();
		final PackageType packageType = PackageType.fromType(artifactType);
		if (packageType == null) {
			getLog().warn(String.format("Skipping push because of unsupported artifact type '%s'", artifactType));
			return;
		}
		if (project.getArtifact().getFile() == null) {
			throw new MojoExecutionException(
					"You need to have the package goal as well when pushing vRealize projects.");
		}

		LinkedList<Artifact> artifacts = new LinkedList<>();
		if (includeDependencies) {
			for (Object artifact : project.getArtifacts()) {
				artifacts.addLast((Artifact) artifact);
			}
		}
		artifacts.addLast(project.getArtifact());
		try {
			importArtifacts(artifacts);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException("Failed to import artifacts", e);
		}
	}

	private void printFilesSelected() {
		Log log = getLog();
		String totalFiles = this.filesChanged.size() == 0 ? "ALL" : this.filesChanged.size() + "";
		log.info(String.format("Files changed on Git Version. Total files to be pushed: %s", totalFiles));
		this.filesChanged.forEach(fileSelected -> log.info(String.format("File to push: %s", fileSelected)));
	}
}
