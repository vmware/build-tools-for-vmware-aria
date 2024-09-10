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

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugins.annotations.*;

@Mojo(name = "run-vro-tests", defaultPhase = LifecyclePhase.TEST)
public class JsBasedActionsTestMojo extends AbstractVroTestMojo {
	private static final String SRC_JS_PATH = Paths.get("src", "main", "resources").toString();
	private static final String SRC_TEST_PATH = Paths.get("src", "test", "resources").toString();

	protected Boolean hasTests() {
		String projectRoot = project.getBasedir().toPath().toString();
		return super.hasTests() && new File(Paths.get(projectRoot, SRC_TEST_PATH).toString()).exists();
	}

	protected void addTestbedPaths(List<String> cmd, Configuration config) {
		String projectRoot = project.getBasedir().toPath().toString();
		cmd.add("--actions");
		cmd.add(Paths.get(projectRoot, SRC_JS_PATH).toString());
		cmd.add("--tests");
		cmd.add(Paths.get(projectRoot, SRC_TEST_PATH).toString());
	}
}
