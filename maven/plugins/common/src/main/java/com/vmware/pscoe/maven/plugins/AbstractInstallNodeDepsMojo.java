/*
 * #%L
 * common
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Executes the external project's dependency list.
 */
public abstract class AbstractInstallNodeDepsMojo extends AbstractIacMojo {
	/**
	 * The external project that is built with Build Tools for VMware Aria.
	 */
	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	/**
	 * Boolean indicating whether the Node dependencies must be installed.
	 */
	@Parameter(property = "skipInstallNodeDeps", defaultValue = "false")
	private boolean skipInstallNodeDeps;

	/**
	 * Constant indicating the maximum number of commands for dependencies
	 * installation within one file.
	 */
	private static final int MAX_NUMBER_OF_CMD_DEPS = 7000;

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		boolean allTgzLibsResolved = true;
		File nodeModules = new File(project.getBasedir(), "node_modules");
		if (skipInstallNodeDeps) {
			if (nodeModules.exists()) {
				getLog().info("Skipping the Dependency installation");
				return;
			} else {
				getLog().info("Ignoring the flag skipInstallNodeDeps,"
						+ "as node dependencies doesn't exist and are required for the successful build...");
			}
		}

		if (!nodeModules.exists()) {
			getLog().debug("node_modules doesn't exists. Creating it...");
			nodeModules.mkdirs();
		}

		// TODO: Think of a way to put the logic for unit test framework in one place
		// instead of two separate components.
		// Checking if there's specific unit test framework specified. If there is - it
		// will be added to the dependencies list.
		// If not - the default Jasmine type definitions will be kept from the original
		// dependencies list.

		Properties projectProperties = project.getProperties();
		String unitTestFrameworkPackage = projectProperties.getProperty("test.framework.package");
		String unitTestFrameworkVersion = projectProperties.getProperty("test.framework.version");
		String unitTestFrameworkTypes = projectProperties.getProperty("test.framework.types.version");
		boolean useDefaultUnitTestFramework = unitTestFrameworkPackage == null
				|| unitTestFrameworkPackage.equals(new String("jasmine")) && unitTestFrameworkVersion == null;

		getLog().debug("unitTestFrameworkPackage => " + unitTestFrameworkPackage);
		getLog().debug("unitTestFrameworkVersion => " + unitTestFrameworkVersion);
		getLog().debug("unitTestFrameworkTypes => " + unitTestFrameworkTypes);

		List<String> deps = new LinkedList<>();

		for (Object o : project.getArtifacts()) {
			Artifact a = (Artifact) o;
			if ("tgz".equals(a.getType())) {
				if (!useDefaultUnitTestFramework
						&& a.getArtifactId().equals(new String("jasmine"))) {
					continue;
				}
				deps.add(a.getFile().getAbsolutePath());
				allTgzLibsResolved = allTgzLibsResolved && a.isResolved();
			}
		}

		if (!allTgzLibsResolved) {
			getLog().debug("Not All .tgz plugins resolved. Executing 'mvn dependency:go-offline' first");
			List<String> goOfflineCmds = new LinkedList<>();

			goOfflineCmds.add("mvn");
			goOfflineCmds.add("dependency:go-offline");
			executeProcess(goOfflineCmds, "Going Offline");
		}

		getLog().debug("Dependencies length:  " + deps.stream().mapToInt(String::length).sum());

		List<List<String>> dependencies = new LinkedList<List<String>>();
		int size = 0;
		int pointer = 0;
		dependencies.add(pointer, new LinkedList<String>());

		for (String dep : deps) {
			size += dep.length();
			if (size > AbstractInstallNodeDepsMojo.MAX_NUMBER_OF_CMD_DEPS) {
				size = dep.length();
				pointer++;
			}

			if (dependencies.size() - 1 < pointer) {
				dependencies.add(pointer, new LinkedList<String>());
			}

			dependencies.get(pointer).add(dep);
		}

		String npmExec = SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";

		// Add a dependency for the unit test framework type
		// definitions according to the project configuration.
		if (!useDefaultUnitTestFramework) {
			if (unitTestFrameworkVersion == null) {
				getLog().info(
						"No specific versions of the unit testing framework received, will be using the"
								+ " latest available for both the framework dependency and its type definitions.");
				unitTestFrameworkTypes = "latest";
				unitTestFrameworkVersion = "latest";
			} else if (unitTestFrameworkTypes == null) {
				if (unitTestFrameworkVersion.equals(new String("latest"))) {
					unitTestFrameworkTypes = "latest";
				} else {
					int dotPosition = unitTestFrameworkVersion.lastIndexOf('.');
					getLog().warn("dotPosition: " + dotPosition);
					unitTestFrameworkTypes = unitTestFrameworkVersion.substring(0, dotPosition) + ".X";
				}
				getLog().warn(
						"Received specific version for the unit testing framework, but not for its type definitions, "
								+ "attempting to use [" + unitTestFrameworkTypes
								+ "], generated from the framework version. "
								+ "Please, be aware the type definitions are handled by the DefinitelyTyped project "
								+ "(https://definitelytyped.org/). In case there are any issues with installing the generated types "
								+ "version, please try specifying which version should be applied.");
			} else {
				getLog().warn(
						"Received specific versions for unit test framework and framework type definition,"
								+ " will attempt to install those. Please, be aware these need to be a proper match"
								+ " and are handled by the DefinitelyTyped project (https://definitelytyped.org/).");
			}

			unitTestFrameworkPackage = unitTestFrameworkPackage != null ? unitTestFrameworkPackage : "jasmine";
			String unitTestFrameworkTypeDefsPackage = "@types/" + unitTestFrameworkPackage + "@"
					+ unitTestFrameworkTypes;

			getLog().info(
					"Adding dependency to be installed for the unit tests framework "
							+ "type definitions: " + unitTestFrameworkTypeDefsPackage);

			List<String> framework = new LinkedList<>();
			framework.add(unitTestFrameworkTypeDefsPackage);
			dependencies.add(framework);
		}

		// Idea: Should we separate dependencies as dev deps and regular ones?

		for (List<String> dependency : dependencies) {
			getLog().debug("Dependency size: " + dependency.size());
			dependency.add(0, npmExec);
			dependency.add(1, "install");
			if (!getLog().isDebugEnabled()) {
				dependency.add(2, "--silent");
			}

			String depString = String.join(",", dependency);
			getLog().debug("Dependency as string: " + depString);

			executeProcess(dependency, "Dependency Installation");
		}
	}

	/**
	 * This method is used to execute the dependencies.
	 * 
	 * @param command  this will have list of dependencies
	 * @param nameText this is the name for Process Executor
	 * @exception MojoExecutionException for exception during process execution
	 * @exception MojoFailureException   for exception during process failure
	 */
	protected void executeProcess(final List<String> command, final String nameText)
			throws MojoExecutionException, MojoFailureException {
		if (!getLog().isDebugEnabled()) {
			command.add("--silent");
		}

		new ProcessExecutor()
				.name(nameText)
				.directory(project.getBasedir())
				.throwOnError(true)
				.command(command)
				.execute(getLog());
	}

}
