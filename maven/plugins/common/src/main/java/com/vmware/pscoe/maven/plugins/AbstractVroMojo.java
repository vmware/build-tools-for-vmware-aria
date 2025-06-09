/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2023 - 2025 VMware
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
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractVroMojo extends AbstractMojo {
	/**
	 * Project build directory.
	 */
	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	protected File directory;
	/**
	 * Project handle.
	 */
	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	/**
	 * name of the vRO ignore file. Default is ".vroignore"
	 */
	@Parameter(property = "vroIgnoreFile", defaultValue = ".vroignore")
	protected String vroIgnoreFile;

	/**
	 * Converts the path argument so that it is platform independent.
	 *
	 * @param first first path argument.
	 * @param more next path argument.
	 *
	 * @return path argument that is platform independent.
	 */
	protected String toPathArgument(String first, String... more) {
		String path = Paths.get(first, more)
			.normalize()
			.toString()
			.replaceAll("[\\\\/]+", "/")
			.replace("\"", "");
		return path.indexOf(" ") >= 0 ? "\"" + path + "\"" : path;
	}

	/**
	 * Adds the vroIgnoreFile command argument to the given list of commands
	 * @param vroCmd - list of commands
	 */
	protected void addVroIgnoreArgToCmd(List<String> vroCmd) {
		String projectRoot = project.getBasedir().toPath().toString();
		vroCmd.add("--vroIgnoreFile");
		vroCmd.add(toPathArgument(projectRoot, vroIgnoreFile));
	}
}
