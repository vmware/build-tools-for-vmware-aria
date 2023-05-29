package com.vmware.pscoe.maven.plugins;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Executes the external project's dependency list.
 */
public abstract class AbstractInstallNodeDepsMojo extends AbstractIacMojo {
	/**
	 * The external project that is built with VMware Aria Build Tools.
	 */
	@Parameter(defaultValue = "${project}")
    private MavenProject project;

	/**
	 * Boolean indicating whether the Node dependencies must be installed.
	 */
	@Parameter(property = "skipInstallNodeDeps", defaultValue = "false")
	private boolean skipInstallNodeDeps;

	/**
	 * Constant indicating the maximum number of commands for dependencies installation within one file.
	 */
	private static final int MAX_NUMBER_OF_CMD_DEPS = 7000;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        boolean allTgzLibsResolved = true;
		int commandLength = 0;
        File nodeModules = new File(project.getBasedir(), "node_modules");
		if (skipInstallNodeDeps) {
			if (nodeModules.exists()) {
				getLog().info("Skipping the Dependency installation");
				return;
			} else {
				getLog().info("Ignoring the flag skipInstallNodeDeps," 
				+
				 "as node dependencies doesn't exist and are required for the successful build...");
			}
		}

        if (!nodeModules.exists()) {
            getLog().debug("node_modules doesn't exists. Creating it...");
            nodeModules.mkdirs();
        }
		
        List<String> deps = new LinkedList<>();
		List<String> depsCmdMax7000 = new LinkedList<>();

        String npmExec = SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
        deps.add(npmExec);
        deps.add("install");
        for (Object o : project.getArtifacts()) {
            Artifact a = (Artifact) o;
            if ("tgz".equals(a.getType())) {
                deps.add(a.getFile().getAbsolutePath());
                allTgzLibsResolved = allTgzLibsResolved && a.isResolved();
            }
        }

        if (!getLog().isDebugEnabled()) {
            deps.add("--silent");
        }

        if (!allTgzLibsResolved) {
            getLog().debug("Not All .tgz plugins resolved. Executing 'mvn dependency:go-offline' first");
            new ProcessExecutor()
				.name("Going Offline")
				.directory(project.getBasedir())
				.throwOnError(true)
				.command("mvn dependency:go-offline")
				.execute(getLog());
        }

        getLog().debug("Dependencies length:  " + deps.stream().mapToInt(String::length).sum());

		for (String path : deps) {
			commandLength = commandLength + path.length();
			if (commandLength <= this.MAX_NUMBER_OF_CMD_DEPS) {
				depsCmdMax7000.add(path);
			} else {
				executeProcess(depsCmdMax7000, "Dependency installation - Command Length is greater than 7000");
				depsCmdMax7000 = new LinkedList<>();
				depsCmdMax7000.add(npmExec);
				depsCmdMax7000.add("install");
				depsCmdMax7000.add(path);
				commandLength = path.length();					
			}
		}
		if (commandLength > 0) {
			executeProcess(depsCmdMax7000, "Dependency installation - Last Batch");
		}
    }

	/**
	 * This method is used to execute the dependencies.
	 * @param command this will have list of dependencies
	 * @param nameText this is the name for Process Executor
	 * @exception MojoExecutionException for exception during process execution 
	 * @exception MojoFailureException for exception during process failure
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
