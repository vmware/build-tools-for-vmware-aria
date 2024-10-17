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

import org.apache.maven.project.MavenProject;

public class MavenProjectPackageInfoProvider implements PackageInfoProvider {
	private final String suffix;
	private final MavenProject project;

	public MavenProjectPackageInfoProvider(MavenProject project) {
		this(project, null);
	}

	public MavenProjectPackageInfoProvider(MavenProject project, String suffix) {
		this.project = project;
		this.suffix = suffix == null ? "" : suffix;
	}

	@Override
	public String getPackageName() {
		return project.getGroupId() + "." + project.getName() + "-" + project.getVersion() + suffix;

	}

	@Override
	public String getVersion() {
		return project.getVersion();
	}

	@Override
	public String getDescription() {
		return this.project.getDescription();
	}

	@Override
	public void setDescription(String description) {
		this.project.setDescription(description);
	}

}
