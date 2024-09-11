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
package com.vmware.pscoe.maven.plugins;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.StringJoiner;

public final class TypescriptHelper {
	public static String getActionsNamespaceForProject(MavenProject project) {
		StringJoiner actionNamespacePathJoiner = new StringJoiner(File.separator);
		Arrays.stream(project.getGroupId().split("\\.")).forEach(actionNamespacePathJoiner::add);
		Arrays.stream(project.getArtifactId().split("\\.")).forEach(actionNamespacePathJoiner::add);
		return actionNamespacePathJoiner.toString();
	}
}
